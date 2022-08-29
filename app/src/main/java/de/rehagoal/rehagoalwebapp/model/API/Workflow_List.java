package de.rehagoal.rehagoalwebapp.model.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Workflow list stub
 */
public class Workflow_List implements Serializable {
    @SerializedName("workflow_list")
    ListEntry[] workflow_list;

    /**
     * Constructs a new workflow_list message request
     *
     * @param entries String array of multiple ListEntry values
     */
    public Workflow_List(ListEntry[] entries) {
        workflow_list = entries;
    }

    /**
     * Receive the containing list
     *
     * @return all ListEntry from workflow_list are returned
     */
    public ListEntry[] getWorkflow_list() {
        return workflow_list;
    }
}
