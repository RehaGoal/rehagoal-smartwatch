package de.rehagoal.rehagoalwebapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static de.rehagoal.rehagoalwebapp.Constants.TASK_TYPE_PARALLEL;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_TYPE_TIMER;


/**
 * Task model
 */

//TODO: id type string/number ????

public class Task implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private int type;
    @SerializedName("text")
    private String text;
    @SerializedName("workflowId")
    private int workflowId;
    @SerializedName("timer")
    private int timer;
    @SerializedName("quantity")
    private int quantity;
    @SerializedName("subtasks")
    private Task[] subtasks;

    /**
     * Get the id of the currently running task
     *
     * @return id of type: int
     */
    public int getId() {
        return id;
    }

    /**
     * Get the type of the currently running task
     *
     * @return either @TASK_TYPE_SIMPLE, @TASK_TYPE_CONDITIONAL, @TASK_TYPE_TIMER, @TASK_TYPE_PARALLEL or TASK_TYPE_END
     */
    public int getType() {
        return type;
    }

    /**
     * Get the text of the currently running task
     *
     * @return description of type: string
     */
    public String getText() {
        return text;
    }

    /**
     * Get the Id of the currently running workflow
     *
     * @return workflow id of type: integer
     */
    public int getWorkflowId() {
        return workflowId;
    }

    /**
     * Get the time value of this workflow in seconds
     * This usually means a break for n seconds
     *
     * @return timer of type: integer
     */
    public int getTimer() {
        return timer;
    }

    /**
     * Get the time value of this workflow in milliseconds
     *
     * @return timer of type: long
     */
    public long getTimerInMillis() {
        if (isTimerTask()) {
            return timer * 1000;
        }
        return 0;
    }

    /**
     * If the currently running task is @TASK_TYPE_PARALLEL, it contains the number of subtasks necessary to finish this task in general
     *
     * @return quantity of needed subtasks to be finished, type: integer
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * If the currently running task is @TASK_TYPE_PARALLEL, it contains all TASK_TYPE_SIMPLE subtasks of this task
     *
     * @return subtasks of type: Task[]
     */
    public Task[] getSubtasks() {
        return subtasks;
    }

    /**
     * If the currently running task is @TASK_TYPE_PARALLEL, it contains the number of subtasks of this task
     *
     * @return size/length of all subtasks of type: int
     */
    public int getSubtasksSize() {
        return subtasks.length;
    }

    /**
     * Checks if this task is a timer task
     *
     * @return either TRUE, when this task is a TASK_TYPE_TIMER, or FALSE if not
     */
    public boolean isTimerTask() {
        return type == TASK_TYPE_TIMER;
    }

    /**
     * Checks if this task is a parallel task
     *
     * @return either TRUE, when this task is a TASK_TYPE_PARALLEL, or FALSE if not
     */
    public boolean isParallelTask() {
        return type == TASK_TYPE_PARALLEL;
    }
}


