package de.rehagoal.rehagoalwebapp.model.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Ping stub
 */

public class Ping implements Serializable {
    @SerializedName("rehagoal_api_version")
    int version;

    /**
     * Constructs a new Ping Message
     * @param version   contains the used REHAGOAL_API_VERSION
     */
    public Ping(int version) {
        this.version = version;
    }

    /**
     * Get the used REHAGOAL_API_VERSION of this Ping message
     * @return  REHAGOAL_API_VERSION of this message
     */
    public int getVersion() {
        return version;
    }

}
