package de.rehagoal.rehagoalwebapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableDialogHelper;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import de.rehagoal.rehagoalwebapp.R;

import de.rehagoal.rehagoalwebapp.model.API.ListEntry;
import de.rehagoal.rehagoalwebapp.model.API.Ping;
import de.rehagoal.rehagoalwebapp.model.API.Settings;
import de.rehagoal.rehagoalwebapp.model.API.Workflow_List;
import de.rehagoal.rehagoalwebapp.model.ApiResponse;
import de.rehagoal.rehagoalwebapp.model.Navigation;
import de.rehagoal.rehagoalwebapp.services.RehaGoalResponderService;
import de.rehagoal.rehagoalwebapp.services.TTSService;

import java.util.concurrent.TimeUnit;

import static de.rehagoal.rehagoalwebapp.Constants.DEFAULT_NAVIGATION_SECTION;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_LIST;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_PING;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_API_RETRY_TIMEOUT_SECONDS;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_API_VERSION;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_COMPANIONS_LIST;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_COMPANIONS_PING;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_COMPANIONS_SETTINGS;
import static de.rehagoal.rehagoalwebapp.Constants.GOOGLE_API_CLIENT_TIMEOUT_S;
import static de.rehagoal.rehagoalwebapp.Constants.TTS_ACTION_SPEAK;

/**
 * RehaGoal-Wearapp
 */

public class MainActivity extends WearableActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, DataApi.DataListener {

    //TODO: write JavaDoc for this activity

    private static final String TAG = MainActivity.class.getSimpleName();

    private RelativeLayout mLoadingBar;
    private GoogleApiClient mGoogleApiClient;
    private Gson mGson;
    private Handler mHandler;
    private AlertDialog mRetryDialog;
    private AlertDialog mOutdatedDialog;

    private ListEntry[] mWorkflowList;
    private boolean mLoadingBarVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate init");

        // recovering the instance state
        if (savedInstanceState != null) {
            mWorkflowList = (ListEntry[]) savedInstanceState.getSerializable(REHAGOAL_ACTION_LIST);
            mLoadingBarVisible = savedInstanceState.getBoolean("mLoadingBarVisible");
        } else {
            mWorkflowList = new ListEntry[]{};
            mLoadingBarVisible = true;
        }

        // build api connection
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        // create json parser
        mGson = new Gson();

        // create handler for loading timeout check
        mHandler = new Handler();
        scheduleRetryDialog();

        // create UI
        setActivityLayout();
        replaceFragmentContainerWith(newOverviewFragment());

        // start TTSService
        Intent ttsServiceIntent = new Intent(this, TTSService.class);
        startService(ttsServiceIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putSerializable(REHAGOAL_ACTION_LIST, mWorkflowList);
        bundle.putBoolean("mLoadingBarVisible", mLoadingBarVisible);
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }

        if (mWorkflowList.length == 0) {
            requestWorkflowListFromApp();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onDestroy() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
                Log.d(TAG, "disconnecting GoogleApiClient");
            }
        }
        if (mOutdatedDialog != null) {
            mOutdatedDialog.cancel();
        }
        if (mRetryDialog != null) {
            mRetryDialog.cancel();
        }
        mHandler = null;


        Log.d(TAG, "destroying activity");
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        if (!RehaGoalApiChecker.isApiVersionValid()) {
            requestPingFromApp();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended with cause: " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed with reason: " + connectionResult);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();

                String path = item.getUri().getPath();

                if (RehaGoalApiChecker.isApiVersionValid() || path.equals(REHAGOAL_COMPANIONS_PING)) {
                    switch (path) {
                        case REHAGOAL_COMPANIONS_LIST:
                        case REHAGOAL_COMPANIONS_SETTINGS:
                        case REHAGOAL_COMPANIONS_PING:
                            GetDataTask asyncTask = new GetDataTask();
                            asyncTask.execute(item.getUri());
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void scheduleRetryDialog() {
        final long timeout = TimeUnit.SECONDS.toMillis(REHAGOAL_API_RETRY_TIMEOUT_SECONDS);
        Runnable scheduleRetryDialog = new Runnable() {
            @Override
            public void run() {
                if (mOutdatedDialog == null && mWorkflowList.length == 0) {
                    showRetryDialog();
                    mHandler.postDelayed(this, timeout);
                }
            }
        };
        mHandler.postDelayed(scheduleRetryDialog, timeout);
    }

    private void showRetryDialog() {
        if (mRetryDialog != null && mRetryDialog.isShowing()) {
            return;
        }

        final View alertDialogView = getLayoutInflater().inflate(R.layout.alert_dialog_retry, null);
        alertDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPingFromApp();
                mRetryDialog.dismiss();
            }
        });
        mRetryDialog = new WearableDialogHelper.DialogBuilder(this)
                .setTitle(R.string.main_retry_request_title)
                .setIcon(R.mipmap.ic_launcher)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPingFromApp();
                        dialog.dismiss();
                    }
                })
                .setView(alertDialogView)
                .create();
        mRetryDialog.show();
    }

    private void showOutdatedVersionDialog() {
        if (mOutdatedDialog == null) {
            mOutdatedDialog = new WearableDialogHelper.DialogBuilder(this)
                    .setTitle(R.string.main_outdated_dialog_title)
                    .setMessage(R.string.main_outdated_dialog_text)
                    .setIcon(R.drawable.ic_full_sad)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
        }
        mOutdatedDialog.show();
    }

    private void handlePing(Ping ping) {
        RehaGoalApiChecker.setApiVersionValid(REHAGOAL_API_VERSION >= ping.getVersion());
        if (RehaGoalApiChecker.isApiVersionValid()) {
            Log.d(TAG, "Local api version is equal/newer than webapp");
        } else {
            Log.d(TAG, "Local api version is outdated");
            showOutdatedVersionDialog();
        }
    }

    private void handleSettings(Settings settings) {
        //TODO: Handle settings from the phone here
    }

    private void handleWorkflowList(Workflow_List newList) {
        mWorkflowList = newList.getWorkflow_list();
        replaceFragmentContainerWith(newOverviewFragment());
        setLoadingBarVisibility(false);
    }

    private void requestWorkflowListFromApp() {
        Intent responseServiceIntent = new Intent(this, RehaGoalResponderService.class);
        responseServiceIntent.setAction(REHAGOAL_ACTION_LIST);
        responseServiceIntent.putExtra(REHAGOAL_ACTION_LIST, new Workflow_List(mWorkflowList));
        startService(responseServiceIntent);
    }

    private void requestPingFromApp() {
        Intent responseServiceIntent = new Intent(this, RehaGoalResponderService.class);
        responseServiceIntent.setAction(REHAGOAL_ACTION_PING);
        responseServiceIntent.putExtra(REHAGOAL_ACTION_PING, new Ping(REHAGOAL_API_VERSION));
        startService(responseServiceIntent);
    }

    private void setLoadingBarVisibility(boolean isVisible) {
        if (isVisible) {

            mLoadingBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingBar.setVisibility(View.GONE);
        }
        mLoadingBarVisible = isVisible;
    }

    private void setActivityLayout() {
        // load xml res
        setContentView(R.layout.activity_main);

        // Main Wearable Drawer Layout that wraps all content
        WearableDrawerLayout mWearableDrawerLayout = (WearableDrawerLayout) findViewById(R.id.drawer_layout);
        mWearableDrawerLayout.peekDrawer(Gravity.TOP);

        // Top Navigation Drawer
        WearableNavigationDrawer mWearableNavigationDrawer = (WearableNavigationDrawer) findViewById(R.id.top_navigation_drawer);
        mWearableNavigationDrawer.setAdapter(new NavigationDrawerAdapter(this));

        // Progress Bar
        mLoadingBar = (RelativeLayout) findViewById(R.id.loadingPanel);
        setLoadingBarVisibility(true);
    }

    private void replaceFragmentContainerWith(Fragment fragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private Fragment newOverviewFragment() {
        OverviewFragment fragment = new OverviewFragment();
        Bundle extras = new Bundle();
        extras.putSerializable("list", mWorkflowList);
        fragment.setArguments(extras);
        return fragment;
    }

    private class NavigationDrawerAdapter extends WearableNavigationDrawer.WearableNavigationDrawerAdapter {
        private final Context mContext;
        private Navigation mCurrentNavigationSection = DEFAULT_NAVIGATION_SECTION;

        NavigationDrawerAdapter(final Context context) {
            mContext = context;
        }

        @Override
        public String getItemText(int index) {
            return mContext.getString(Navigation.values()[index].getTitle());
        }

        @Override
        public Drawable getItemDrawable(int index) {
            return mContext.getDrawable(Navigation.values()[index].getDrawable());
        }

        @Override
        public void onItemSelected(int index) {
            Navigation selectedNavigationSection = Navigation.values()[index];

            // call tts for fragment
            Intent speakTextIntent = new Intent(getApplicationContext(), TTSService.class);
            speakTextIntent.putExtra(TTS_ACTION_SPEAK, getString(selectedNavigationSection.getTitle()));
            startService(speakTextIntent);

            // Only replace the fragment if the section is changing.
            if (selectedNavigationSection == mCurrentNavigationSection) {
                return;
            }

            mCurrentNavigationSection = selectedNavigationSection;

            Fragment sectionFragment = null;

            switch (selectedNavigationSection) {
                case Workflows:
                    sectionFragment = newOverviewFragment();
                    setLoadingBarVisibility(mLoadingBarVisible);
                    requestWorkflowListFromApp();
                    break;
                case Settings:
                    sectionFragment = new SettingsFragment();
                    setLoadingBarVisibility(false);
                    break;
                default:
                    // should never happen
                    break;
            }

            // replace view
            replaceFragmentContainerWith(sectionFragment);
        }

        @Override
        public int getCount() {
            return Navigation.values().length;
        }
    }

    private class GetDataTask extends AsyncTask<Uri, Void, Object> {
        @Override
        protected Object doInBackground(Uri... uri) {
            if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.blockingConnect(GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);
            }

            DataItemBuffer results = Wearable.DataApi.getDataItems(mGoogleApiClient, uri[0]).await();
            if (results.getCount() != 0) {
                DataItem item = results.get(0);

                DataMap data = DataMapItem.fromDataItem(item).getDataMap();
                ApiResponse response = mGson.fromJson(data.getString("data"), ApiResponse.class);

                results.release();

                switch (uri[0].getPath()) {
                    case REHAGOAL_COMPANIONS_LIST:
                        return response.getWorkflow_list();
                    case REHAGOAL_COMPANIONS_SETTINGS:
                        return response.getSettings();
                    case REHAGOAL_COMPANIONS_PING:
                        return response.getPing();
                    default:
                        break;
                }
            }
            results.release();
            return null;
        }

        @Override
        protected void onPostExecute(Object response) {
            if (response == null) {
                Log.d(TAG, "onPostExecute with NO response");
            } else {
                if (response instanceof Workflow_List) {
                    handleWorkflowList((Workflow_List) response);
                } else if (response instanceof Ping) {
                    handlePing((Ping) response);
                } else if (response instanceof Settings) {
                    handleSettings((Settings) response);
                }

                Log.d(TAG, "onPostExecute with GetDataTask of type: " + response.getClass().getSimpleName());
            }
        }
    }
}
