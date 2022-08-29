package de.rehagoal.rehagoalwebapp.model.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Start stub
 */

public class Start implements Serializable {
    @SerializedName("id")
    int id;
    @SerializedName("type")
    String type;

    /**
     * Construct a new start message request for the rehagoal-webapp
     *
     * @param id   contains the id of the type the user wants to start
     * @param type can contain different things, but until further development, only @LIST_TYPE_WORKFLOW is handled by rehagoal-webapp
     */
    public Start(int id, String type) {
        this.id = id;
        this.type = type;
    }
}
