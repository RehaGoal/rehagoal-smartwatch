package de.rehagoal.rehagoalwebapp.model;

import de.rehagoal.rehagoalwebapp.model.API.*;

import de.rehagoal.rehagoalwebapp.model.API.ListEntry;
import de.rehagoal.rehagoalwebapp.model.API.Notification;
import de.rehagoal.rehagoalwebapp.model.API.Ping;
import de.rehagoal.rehagoalwebapp.model.API.Reply;
import de.rehagoal.rehagoalwebapp.model.API.Settings;
import de.rehagoal.rehagoalwebapp.model.API.Start;
import de.rehagoal.rehagoalwebapp.model.API.Stop;
import de.rehagoal.rehagoalwebapp.model.API.Workflow_List;


/**
 * Api Response Class for JSON Wrapper
 */

public class ApiResponse {
    private ListEntry[] workflow_list;
    private Settings settings;
    private Task task;
    private Reply reply;
    private Start start;
    private Stop stop;
    private Ping ping;
    private Notification notification;

    /**
     * Creates a new workflow_list request
     *
     * @param workflow_list needs a Workflow_list object
     */
    public ApiResponse(Workflow_List workflow_list) {
        this.workflow_list = workflow_list.getWorkflow_list();
    }

    /**
     * Creates a new settings request
     *
     * @param settings needs a settings object
     */
    public ApiResponse(Settings settings) {
        this.settings = settings;
    }

    /**
     * Creates a new task request
     *
     * @param task needs a task object
     */
    public ApiResponse(Task task) {
        this.task = task;
    }

    /**
     * Creates a new reply request
     *
     * @param reply needs a reply object
     */
    public ApiResponse(Reply reply) {
        this.reply = reply;
    }

    /**
     * Creates a new start request
     *
     * @param start needs a start object
     */
    public ApiResponse(Start start) {
        this.start = start;
    }

    /**
     * Creates a new stop request
     *
     * @param stop needs a stop object
     */
    public ApiResponse(Stop stop) {
        this.stop = stop;
    }

    /**
     * Creates a new ping message
     *
     * @param ping needs a ping object
     */
    public ApiResponse(Ping ping) {
        this.ping = ping;
    }

    /**
     * Creates a new notification request
     *
     * @param notification needs a notification object
     */
    public ApiResponse(Notification notification) {
        this.notification = notification;
    }

    // getter

    /**
     * Get the requested workflow_list data
     *
     * @return data as type: ListEntry[]
     */
    public Workflow_List getWorkflow_list() {
        return new Workflow_List(workflow_list);
    }

    /**
     * Get the requested settings data
     *
     * @return data as type: settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Get the requested task data
     *
     * @return data as type: task
     */
    public Task getTask() {
        return task;
    }

    /**
     * Get the requested reply data
     *
     * @return data as type: reply
     */
    public Reply getReply() {
        return reply;
    }

    /**
     * Get the requested start data
     *
     * @return data as type: start
     */
    public Start getStart() {
        return start;
    }

    /**
     * Get the requested stop data
     *
     * @return data as type: stop
     */
    public Stop getStop() {
        return stop;
    }

    /**
     * Get the requested ping data
     *
     * @return data as type: ping
     */
    public Ping getPing() {
        return ping;
    }

    /**
     * Get the requested notification data
     *
     * @return data as type: notification
     */
    public Notification getNotification() {
        return notification;
    }

}
