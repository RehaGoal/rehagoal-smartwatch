package de.rehagoal.rehagoalwebapp.model.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Reply stub
 */

public class Reply implements Serializable {
    @SerializedName("taskId")
    int id;
    @SerializedName("response")
    String response;

    /**
     * Constructs a new reply message for the rehagoal-webapp
     *
     * @param id       this contains the id of the current running task
     * @param response contains the action the user has taken
     */
    public Reply(int id, String response) {
        this.id = id;
        this.response = response;
    }
}
