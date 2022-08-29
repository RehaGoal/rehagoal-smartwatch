package de.rehagoal.rehagoalwebapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import de.rehagoal.rehagoalwebapp.RehaGoalApiChecker;
import de.rehagoal.rehagoalwebapp.model.API.Ping;
import de.rehagoal.rehagoalwebapp.model.API.Reply;
import de.rehagoal.rehagoalwebapp.model.API.Start;
import de.rehagoal.rehagoalwebapp.model.API.Stop;
import de.rehagoal.rehagoalwebapp.model.API.Workflow_List;
import de.rehagoal.rehagoalwebapp.model.ApiResponse;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_LIST;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_PING;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_REPLY;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_START;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_STOP;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_WEBAPP_LIST;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_WEBAPP_PING;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_WEBAPP_REPLY;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_WEBAPP_START;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_WEBAPP_STOP;
import static de.rehagoal.rehagoalwebapp.Constants.GOOGLE_API_CLIENT_ERROR_MSG;
import static de.rehagoal.rehagoalwebapp.Constants.GOOGLE_API_CLIENT_TIMEOUT_S;

/**
 * Handle send data to data layer from wear to device (phone)
 */
public class RehaGoalResponderService extends IntentService {
    private static final String TAG = RehaGoalResponderService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    /**
     * Default constructor needed by the system to invoke this Service by name
     */
    public RehaGoalResponderService() {
        super(TAG);
    }

    /**
     * Checks connectivity and connects to the wearable dataapi
     *
     * @return either TRUE when connection to wearable dataapi was successful, or FALSE if an
     * error or timeout occurred
     */
    private boolean isConnectedToApi() {
        mGoogleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect(
                GOOGLE_API_CLIENT_TIMEOUT_S, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess() || !mGoogleApiClient.isConnected()) {
            Log.e(TAG, String.format(GOOGLE_API_CLIENT_ERROR_MSG,
                    connectionResult.getErrorCode()));
            return false;
        }
        return true;
    }

    /**
     * This function creates an ApiResponse for the connected rehagoal-webapp. It checks the
     * connection and parses the intent data into a valid response message
     *
     * @param intent request by the application to send a message through the wearable dataapi
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && isConnectedToApi()) {
            String path = null;
            ApiResponse response = null;

            boolean sendDataAllowed = RehaGoalApiChecker.isApiVersionValid();

            switch (intent.getAction()) {
                case REHAGOAL_ACTION_START:
                    path = REHAGOAL_WEBAPP_START;
                    response = new ApiResponse((Start) intent.getSerializableExtra(REHAGOAL_ACTION_START));
                    break;
                case REHAGOAL_ACTION_STOP:
                    path = REHAGOAL_WEBAPP_STOP;
                    response = new ApiResponse((Stop) intent.getSerializableExtra(REHAGOAL_ACTION_STOP));
                    break;
                case REHAGOAL_ACTION_REPLY:
                    path = REHAGOAL_WEBAPP_REPLY;
                    response = new ApiResponse((Reply) intent.getSerializableExtra(REHAGOAL_ACTION_REPLY));
                    break;
                case REHAGOAL_ACTION_LIST:
                    path = REHAGOAL_WEBAPP_LIST;
                    response = new ApiResponse((Workflow_List) intent.getSerializableExtra(REHAGOAL_ACTION_LIST));
                    break;
                case REHAGOAL_ACTION_PING:
                    path = REHAGOAL_WEBAPP_PING;
                    response = new ApiResponse((Ping) intent.getSerializableExtra(REHAGOAL_ACTION_PING));
                    sendDataAllowed = true; //Force skip check for ping
                    break;
                default:
                    break;
            }

            if (path != null && sendDataAllowed) {
                Gson gson = new Gson();
                String data = gson.toJson(response);
                sendDataToApp(path, data);
            }
        }
    }

    /**
     * Helper method to send the response message through the dataapi. it creates a unique msgID
     * and puts the data into the provided path. It then waits until the transmission finishes
     *
     * @param path contains the path where data should be placed, like: 'wear://*\/path'
     * @param data contains the message for the rehagoal-webapp
     */
    private void sendDataToApp(final String path, final String data) {
        // create base data map object
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        DataMap response = putDataMapRequest.getDataMap();

        // add data using DataMap object model
        long id = new Random(System.currentTimeMillis()).nextLong();
        response.putLong("msgID", id);
        response.putString("data", data);

        // send data via Android Wear DataApi
        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest().setUrgent()).await();

    }
}
