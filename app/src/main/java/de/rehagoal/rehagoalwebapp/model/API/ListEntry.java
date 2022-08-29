package de.rehagoal.rehagoalwebapp.model.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Helper Class for List entries
 */

public class ListEntry implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    /**
     * Constructs an overview entry for the list adapter
     *
     * @param id   Index of the parsed rehagoal-webapp object
     * @param name String to show inside the UI
     */
    public ListEntry(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Index Value of the parsed rehagoal-webapp object
     *
     * @return Integer Value of the associated object
     */
    public int getId() {
        return id;
    }

    /**
     * Name of the parsed rehagoal-webapp object
     *
     * @return String to show inside the UI
     */
    public String getName() {
        return name;
    }

}
