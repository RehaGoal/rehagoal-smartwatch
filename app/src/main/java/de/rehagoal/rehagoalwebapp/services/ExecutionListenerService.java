package de.rehagoal.rehagoalwebapp.services;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

import de.rehagoal.rehagoalwebapp.ExecutionActivity;
import de.rehagoal.rehagoalwebapp.RehaGoalApiChecker;
import de.rehagoal.rehagoalwebapp.R;
import de.rehagoal.rehagoalwebapp.model.ApiResponse;

import static de.rehagoal.rehagoalwebapp.Constants.*;

/**
 * This service listens for changes in specific paths defined in android manifest
 */
public class ExecutionListenerService extends WearableListenerService {
    private static final String TAG = ExecutionListenerService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private Gson mGson;

    /**
     * Create service and connect to wearable dataapi
     * Instantiates a new Gson parser
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        mGson = new Gson();
    }

    /**
     * Removes connection to wearable dataapi and closes this service
     */
    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    /**
     * Listens for changes in the wearable dataapi
     * It checks for an TYPE_CHANGED event in /task, /stop, and /notification and notifies the executionActivity with the received data
     * If the path is not one of the mentioned it returns with no activity call
     *
     * @param dataEventBuffer buffered data from the wearable network. it contains dataMaps, which contain
     *                        URI, TYPE and DATA
     */
    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        if (!RehaGoalApiChecker.isApiVersionValid()) {
            return;
        }

        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataMap data = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                String path = event.getDataItem().getUri().getPath();

                ApiResponse response = mGson.fromJson(data.getString("data"), ApiResponse.class);
                if (response == null) {
                    Log.e(TAG, getString(R.string.rehagoal_api_event_invalidResponse));
                    break;
                }

                Intent taskExecutionIntent = new Intent(this, ExecutionActivity.class);
                taskExecutionIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                switch (path) {
                    case REHAGOAL_COMPANIONS_TASK:
                        taskExecutionIntent.setAction(REHAGOAL_ACTION_TASK);
                        taskExecutionIntent.putExtra(REHAGOAL_ACTION_TASK, response.getTask());
                        break;
                    case REHAGOAL_COMPANIONS_STOP:
                        taskExecutionIntent.setAction(REHAGOAL_ACTION_STOP);
                        taskExecutionIntent.putExtra(REHAGOAL_ACTION_STOP, response.getStop());
                        break;
                    case REHAGOAL_COMPANIONS_NOTIFICATION:
                        taskExecutionIntent.setAction(REHAGOAL_ACTION_NOTIFICATION);
                        taskExecutionIntent.putExtra(REHAGOAL_ACTION_NOTIFICATION, response.getNotification());
                        break;
                    default:
                        break;
                }

                if (taskExecutionIntent.getAction() != null) {
                    startActivity(taskExecutionIntent);
                }

                //TODO: remove debug log
                // Log.d(TAG, getString(R.string.rehagoal_api_event_uri, event.getDataItem().getUri()));
                // Log.d(TAG, getString(R.string.rehagoal_api_event_type, event.getType()));
                // Log.d(TAG, getString(R.string.rehagoal_api_event_data, data.toString()));
            }
        }
    }
}
