package de.rehagoal.rehagoalwebapp.model.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Stop stub
 */

public class Stop implements Serializable {
    @SerializedName("id")
    int id;

    /**
     * Constructs a new stop message request
     *
     * @param id contains the workflow id which should get stopped
     */
    public Stop(int id) {
        this.id = id;
    }

    /**
     * Receive the Id of this stop message
     *
     * @return contains the workflow id which should get stopped
     */
    public int getId() {
        return id;
    }
}
