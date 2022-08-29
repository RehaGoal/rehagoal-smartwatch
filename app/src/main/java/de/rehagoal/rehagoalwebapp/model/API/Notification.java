package de.rehagoal.rehagoalwebapp.model.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Notification stub
 */

public class Notification implements Serializable {
    @SerializedName("text")
    String text;
    @SerializedName("taskId")
    int taskId;
    @SerializedName("workflowId")
    int workflowId;

    /**
     * Constructs a new Notification with a single text string
     * @param text  message for the user (single string)
     * @param taskId    contains the current task id
     * @param workflowId    contains the current workflow id
     */
    public Notification(String text, int taskId, int workflowId) {
        this.text = text;
        this.taskId = taskId;
        this.workflowId = workflowId;
    }

    /**
     * Get the notification content
     * @return  message of the notification, type: string
     */
    public String getText() {
        return text;
    }

    /**
     * Get the taskId of this notification
     * @return  number of this task
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * Get the workflowID of this notification
     * @return  number of this workflow
     */
    public int getWorkflowId() {
        return workflowId;
    }
}
