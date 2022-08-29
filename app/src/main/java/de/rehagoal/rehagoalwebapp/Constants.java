package de.rehagoal.rehagoalwebapp;

import de.rehagoal.rehagoalwebapp.model.Navigation;

/**
 * REHAGOAL-Wearapp constants
 */
public class Constants {
    /**
     * Unique notification ID of the execution activity for workflow/task notifications
     */
    public static final int NOTIFICATION_ID_EXECUTION_ACTIVITY = 100;
    /**
     * Timeout value in seconds - defines how long the app should try to connect to the api
     */
    public static final int GOOGLE_API_CLIENT_TIMEOUT_S = 10;
    /**
     * Error message - this message gets shown if GOOGLE_API_CLIENT timed out or an error occurred
     */
    public static final String GOOGLE_API_CLIENT_ERROR_MSG = "Failed to connect to GoogleApiClient (error code = %d)";
    /**
     * Application actions used in @Intents, as well as path and listener calls
     * Used to identify: workflow_list messages
     */
    public static final String REHAGOAL_ACTION_LIST = "workflow_list";
    /**
     * Application actions used in @Intents, as well as path and listener calls
     * Used to identify: settings messages
     */
    public static final String REHAGOAL_ACTION_SETTINGS = "settings";
    /**
     * Application actions used in @Intents, as well as path and listener calls
     * Used to identify: task messages
     */
    public static final String REHAGOAL_ACTION_TASK = "task";
    /**
     * Application actions used in @Intents, as well as path and listener calls
     * Used to identify: reply messages
     */
    public static final String REHAGOAL_ACTION_REPLY = "reply";
    /**
     * Application actions used in @Intents, as well as path and listener calls
     * Used to identify: start messages
     */
    public static final String REHAGOAL_ACTION_START = "start";
    /**
     * Application actions used in @Intents, as well as path and listener calls
     * Used to identify: stop messages
     */
    public static final String REHAGOAL_ACTION_STOP = "stop";
    /**
     * Application actions used in @Intents, as well as path and listener calls
     * Used to identify: ping messages
     */
    public static final String REHAGOAL_ACTION_PING = "ping";
    /**
     * Application actions used in @Intents, as well as path and listener calls
     * Used to identify: notification messages
     */
    public static final String REHAGOAL_ACTION_NOTIFICATION = "notification";
    /**
     * Application actions used in @Intents calls
     * Used to identify: tts speak
     */
    public static final String TTS_ACTION_SPEAK = "tts_speak";
    /**
     * List type definition for workflows
     */
    public static final String LIST_TYPE_WORKFLOW = "workflow";
    /**
     * Simple tasks - defined as tasks with a single button
     */
    public static final int TASK_TYPE_SIMPLE = 1;
    /**
     * Conditional tasks - contain a choice with yes/no buttons
     */
    public static final int TASK_TYPE_CONDITIONAL = 2;
    /**
     * Timer tasks - contain a countdown timer and no visible buttons
     */
    public static final int TASK_TYPE_TIMER = 3;
    /**
     * Parallel tasks = defined as a task with multiple simple subtasks
     */
    public static final int TASK_TYPE_PARALLEL = 4;
    /**
     * End task - a current workflow has finished
     */
    public static final int TASK_TYPE_END = 5;
    /**
     * Repeat task - a current task has to be done multiple times
     */
    public static final int TASK_TYPE_REPEAT = 6;
    /**
     * Mark the current (sub-) Task as done
     */
    public static final String TASK_REPLY_OK = "ok";
    /**
     * Conditional task with option VALID
     */
    public static final String TASK_REPLY_YES = "yes";
    /**
     * Conditional task with option INVALID
     */
    public static final String TASK_REPLY_NO = "no";
    /**
     * Message for communicating with webapp to dismiss infoModal
     */
    public static final String NOTIFICATION_REPLY_OK = "info_ok";
    /**
     * Defines the version of the protocol used by the rehagoal-webapp and companions
     * Higher versions are backward compatible
     */
    public static final int REHAGOAL_API_VERSION = 1;
    /**
     * Seconds until the application shows a retry dialog if no workflow_list had been received
     */
    public static final int REHAGOAL_API_RETRY_TIMEOUT_SECONDS = 10;
    /**
     * Unique path prefix to communicate with the rehagoal-webapp
     */
    public static final String REHAGOAL_API_PREFIX_WEBAPP = "/rehagoal/webapp/";
    /**
     * Unique path prefix to communicate with (all) companions
     */
    public static final String REHAGOAL_API_PREFIX_COMPANIONS = "/rehagoal/companions/";
    /**
     * Path to send a data message to the webapp, containing: workflow_list
     */
    public static final String REHAGOAL_WEBAPP_LIST = REHAGOAL_API_PREFIX_WEBAPP + REHAGOAL_ACTION_LIST;
    /**
     * Path to send a data message to the webapp, containing: settings
     */
    public static final String REHAGOAL_WEBAPP_SETTINGS = REHAGOAL_API_PREFIX_WEBAPP + REHAGOAL_ACTION_SETTINGS;
    /**
     * Path to send a data message to the webapp, containing: task
     */
    public static final String REHAGOAL_WEBAPP_TASK = REHAGOAL_API_PREFIX_WEBAPP + REHAGOAL_ACTION_TASK;
    /**
     * Path to send a data message to the webapp, containing: reply
     */
    public static final String REHAGOAL_WEBAPP_REPLY = REHAGOAL_API_PREFIX_WEBAPP + REHAGOAL_ACTION_REPLY;
    /**
     * Path to send a data message to the webapp, containing: start
     */
    public static final String REHAGOAL_WEBAPP_START = REHAGOAL_API_PREFIX_WEBAPP + REHAGOAL_ACTION_START;
    /**
     * Path to send a data message to the webapp, containing: stop
     */
    public static final String REHAGOAL_WEBAPP_STOP = REHAGOAL_API_PREFIX_WEBAPP + REHAGOAL_ACTION_STOP;
    /**
     * Path to send a data message to the webapp, containing: ping
     */
    public static final String REHAGOAL_WEBAPP_PING = REHAGOAL_API_PREFIX_WEBAPP + REHAGOAL_ACTION_PING;
    /**
     * Path to send a data message to the webapp, containing: notification
     */
    public static final String REHAGOAL_WEBAPP_NOTIFICATION = REHAGOAL_API_PREFIX_WEBAPP + REHAGOAL_ACTION_NOTIFICATION;
    /**
     * Path to receive a data message for the companions, containing: workflow_list
     */
    public static final String REHAGOAL_COMPANIONS_LIST = REHAGOAL_API_PREFIX_COMPANIONS + REHAGOAL_ACTION_LIST;
    /**
     * Path to receive a data message for the companions, containing: settings
     */
    public static final String REHAGOAL_COMPANIONS_SETTINGS = REHAGOAL_API_PREFIX_COMPANIONS + REHAGOAL_ACTION_SETTINGS;
    /**
     * Path to receive a data message for the companions, containing: task
     */
    public static final String REHAGOAL_COMPANIONS_TASK = REHAGOAL_API_PREFIX_COMPANIONS + REHAGOAL_ACTION_TASK;
    /**
     * Path to receive a data message for the companions, containing: reply
     */
    public static final String REHAGOAL_COMPANIONS_REPLY = REHAGOAL_API_PREFIX_COMPANIONS + REHAGOAL_ACTION_REPLY;
    /**
     * Path to receive a data message for the companions, containing: start
     */
    public static final String REHAGOAL_COMPANIONS_START = REHAGOAL_API_PREFIX_COMPANIONS + REHAGOAL_ACTION_START;
    /**
     * Path to receive a data message for the companions, containing: stop
     */
    public static final String REHAGOAL_COMPANIONS_STOP = REHAGOAL_API_PREFIX_COMPANIONS + REHAGOAL_ACTION_STOP;
    /**
     * Path to receive a data message for the companions, containing: ping
     */
    public static final String REHAGOAL_COMPANIONS_PING = REHAGOAL_API_PREFIX_COMPANIONS + REHAGOAL_ACTION_PING;
    /**
     * Path to receive a data message for the companions, containing: notification
     */
    public static final String REHAGOAL_COMPANIONS_NOTIFICATION = REHAGOAL_API_PREFIX_COMPANIONS + REHAGOAL_ACTION_NOTIFICATION;
    /**
     * Default Navigation-NavigationSection when Wearapp starts
     * DEFAULT_SECTION - sets the default fragment to be loaded when the app starts
     */
    public static final Navigation DEFAULT_NAVIGATION_SECTION = Navigation.Workflows;
}
