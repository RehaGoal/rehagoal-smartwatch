package de.rehagoal.rehagoalwebapp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableDialogHelper;
import android.support.wearable.view.drawer.WearableActionDrawer;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.rehagoal.rehagoalwebapp.R;

import de.rehagoal.rehagoalwebapp.model.API.Notification;
import de.rehagoal.rehagoalwebapp.model.API.Reply;
import de.rehagoal.rehagoalwebapp.model.API.Stop;
import de.rehagoal.rehagoalwebapp.model.Task;
import de.rehagoal.rehagoalwebapp.services.RehaGoalResponderService;
import de.rehagoal.rehagoalwebapp.services.TTSService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_NOTIFICATION;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_REPLY;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_STOP;
import static de.rehagoal.rehagoalwebapp.Constants.REHAGOAL_ACTION_TASK;
import static de.rehagoal.rehagoalwebapp.Constants.NOTIFICATION_ID_EXECUTION_ACTIVITY;
import static de.rehagoal.rehagoalwebapp.Constants.NOTIFICATION_REPLY_OK;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_REPLY_NO;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_REPLY_OK;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_REPLY_YES;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_TYPE_CONDITIONAL;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_TYPE_END;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_TYPE_PARALLEL;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_TYPE_SIMPLE;
import static de.rehagoal.rehagoalwebapp.Constants.TASK_TYPE_TIMER;
import static de.rehagoal.rehagoalwebapp.Constants.TTS_ACTION_SPEAK;

public class ExecutionActivity extends WearableActivity implements
        WearableActionDrawer.OnMenuItemClickListener,
        Chronometer.OnChronometerTickListener {

    //TODO: write JavaDoc for this activity

    private static final String TAG = ExecutionActivity.class.getSimpleName();
    private static final int NOTIFICATION_ID = NOTIFICATION_ID_EXECUTION_ACTIVITY;
    private WearableDrawerLayout mWearableDrawerLayout;
    private TextView mTaskTextView;
    private Chronometer mTaskTimer;
    private ProgressBar mProgressBar;
    private AlertDialog mReminderAlert;
    private TextView mProgressBarText;
    private WearableActionDrawer mWearableActionDrawer;
    private NotificationManager mNotificationManager;

    private Task mCurrentTask;
    private long timerFinishTime;
    private boolean isExecutionFinished;
    private boolean isLoading;
    private List<String> mTextList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UI
        setActivityLayout();

        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(REHAGOAL_ACTION_TASK)) {
            handleTask((Task) intent.getSerializableExtra(action));
        } else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String action = intent.getAction();
        Serializable intentData = intent.getSerializableExtra(action);

        switch (action) {
            case REHAGOAL_ACTION_REPLY:
                handleReply();
            case REHAGOAL_ACTION_TASK:
                handleTask((Task) intentData);
                break;
            case REHAGOAL_ACTION_STOP:
                handleStop((Stop) intentData);
                break;
            case REHAGOAL_ACTION_NOTIFICATION:
                handleNotification((Notification) intentData);
                break;
            default:
                finish();
        }
    }

    @Override
    protected void onDestroy() {
        //TODO: add alertDialog to confirm dismissal!
        finishExecution();
        super.onDestroy();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (mWearableActionDrawer.isClosed()) {
            mWearableActionDrawer.peekDrawer();
        }

        String title = menuItem.getTitle().toString();
        if (title.equals(getString(R.string.exec_action_drawer_ok))) {
            onSelectOK(mCurrentTask.getId());
        } else if (title.equals(getString(R.string.exec_action_drawer_yes))) {
            onSelectYes();
        } else if (title.equals(getString(R.string.exec_action_drawer_no))) {
            onSelectNo();
        } else {
            onSelectOK(menuItem.getItemId());
        }
        return false;
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        if (SystemClock.elapsedRealtime() >= timerFinishTime) {
            chronometer.stop();
            onSelectOK(mCurrentTask.getId());
        }
    }

    private void updateDisplay() {
        updateProgressBar();

        if (isAmbient()) {
            mWearableDrawerLayout.setBackgroundColor(Color.BLACK);
            mTaskTextView.setTextColor(Color.LTGRAY);
            mTaskTextView.getPaint().setAntiAlias(false);
            mWearableActionDrawer.lockDrawerClosed();
        } else {
            mWearableDrawerLayout.setBackground(null);
            mTaskTextView.setTextColor(Color.WHITE);
            mTaskTextView.getPaint().setAntiAlias(true);
            if (mCurrentTask.isTimerTask()) {
                mWearableActionDrawer.lockDrawerClosed();
            } else {
                mWearableActionDrawer.unlockDrawer();
                mWearableActionDrawer.peekDrawer();
            }
        }
    }

    private void setActivityLayout() {
        setContentView(R.layout.activity_execution);
        setAmbientEnabled();

        mNotificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        mWearableDrawerLayout = (WearableDrawerLayout) findViewById(R.id.drawer_layout);

        mTaskTextView = (TextView) findViewById(R.id.exec_task_text);
        mTaskTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                speakText();
                return true;
            }
        });
        mProgressBar = (ProgressBar) findViewById(R.id.taskLoadingBar);
        mProgressBarText = (TextView) findViewById(R.id.taskLoadingText);

        mTaskTimer = (Chronometer) findViewById(R.id.exec_task_timer);
        mWearableActionDrawer = (WearableActionDrawer) findViewById(R.id.exec_action_drawer);
        mWearableActionDrawer.setOnMenuItemClickListener(this);
    }

    private void setCurrentTask(final Task task) {
        if (task != mCurrentTask) {
            mCurrentTask = task;
            mTextList = new ArrayList<>();

            mTaskTextView.setText(mCurrentTask.getText());

            // reset Chronometer
            mTaskTimer.setVisibility(View.GONE);
            mTaskTimer.stop();

            // clear action drawer
            mWearableActionDrawer.getMenu().clear();
            mWearableActionDrawer.setTitle(getString(R.string.exec_action_drawer_title));

            updateDisplay();

            switch (task.getType()) {
                case TASK_TYPE_CONDITIONAL:
                    setTaskLayoutConditional();
                    break;
                case TASK_TYPE_TIMER:
                    setTaskLayoutTimer();
                    break;
                case TASK_TYPE_PARALLEL:
                    setTaskLayoutParallel();
                    break;
                case TASK_TYPE_END:
                    isExecutionFinished = true;
                    setTaskLayoutSimple();
                    break;
                case TASK_TYPE_SIMPLE:
                default:
                    // simple task / end / other
                    setTaskLayoutSimple();
            }

            // fill list for TTS
            setTextListForTTS();

            showNotification(mCurrentTask.isTimerTask());
        }
    }

    private void setTextListForTTS() {
        mTextList.add(mCurrentTask.getText());
        //TODO: handle the text of the action drawer for TTS
        Menu drawer = mWearableActionDrawer.getMenu();
        int size = drawer.size();

        if (size != 0) {
            mTextList.add(getString(R.string.tts_action_drawer_options));
            for (int i = 0; i < size; i++) {
                mTextList.add(drawer.getItem(i).getTitle().toString());
            }
        }
    }

    private void setTaskLayoutSimple() {
        getMenuInflater().inflate(R.menu.exec_action_drawer_simple, mWearableActionDrawer.getMenu());
    }

    private void setTaskLayoutConditional() {
        getMenuInflater().inflate(R.menu.exec_action_drawer_conditional, mWearableActionDrawer.getMenu());
    }

    private void setTaskLayoutTimer() {
        mTaskTimer.setVisibility(View.VISIBLE);
        timerFinishTime = SystemClock.elapsedRealtime() + mCurrentTask.getTimerInMillis();
        mTaskTimer.setBase(timerFinishTime);
        mTaskTimer.setCountDown(true);
        mTaskTimer.setOnChronometerTickListener(this);
        mTaskTimer.start();
    }

    private void setTaskLayoutParallel() {
        Menu menu = mWearableActionDrawer.getMenu();
        Task[] subtasks = mCurrentTask.getSubtasks();
        for (int i = 0; i < subtasks.length; i++) {
            Task task = subtasks[i];
            menu.add(0, task.getId(), i, task.getText());
            menu.getItem(i).setIcon(R.drawable.ic_workflow);
        }
        mTaskTextView.setText(getString(R.string.exec_task_parallel_todo, mTaskTextView.getText(), mCurrentTask.getQuantity(), mCurrentTask.getSubtasksSize()));
    }

    private void handleTask(Task task) {
        if (task != null) {
            Log.i(TAG, "handleTask: received task: " + task.getText());
            isLoading = false;
            setCurrentTask(task);
            speakText();
        } else {
            Log.e(TAG, "onCreate: empty task received...");
        }
    }

    private void handleReply() {
        if (mCurrentTask.isTimerTask()) {
            mWearableActionDrawer.closeDrawer();
        } else {
            mWearableActionDrawer.openDrawer();
        }
        speakText();
    }

    private void handleStop(Stop stop) {
        if (stop != null) {
            if (stop.getId() == mCurrentTask.getWorkflowId()) {
                Log.i(TAG, "handleStop: received stop call for current task");
                finish();
            }
        } else {
            Log.e(TAG, "handleStop: received stop call for another task");
        }
    }

    private void handleNotification(Notification notification) {
        if (notification != null && mReminderAlert == null) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(500);

            mReminderAlert = new WearableDialogHelper.DialogBuilder(this)
                    .setNeutralIcon(R.drawable.ic_cc_checkmark)
                    .setNeutralButton(R.string.exec_action_drawer_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendReply(mCurrentTask.getId(), NOTIFICATION_REPLY_OK);
                            mReminderAlert = null;
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            sendReply(mCurrentTask.getId(), NOTIFICATION_REPLY_OK);
                            mReminderAlert = null;
                        }
                    })
                    .setTitle(R.string.exec_workflow_notification)
                    .setMessage(notification.getText())
                    .setCancelable(false)
                    .create();
            mReminderAlert.show();
        }
    }

    private void speakText() {
        Intent speakTextIntent = new Intent(getApplicationContext(), TTSService.class);
        speakTextIntent.putExtra(TTS_ACTION_SPEAK, (mTextList.toArray(new String[mTextList.size()])));
        startService(speakTextIntent);
    }

    private void onSelectOK(final int id) {
        Log.i(TAG, "onSelectOK for task id: " + id);
        String response = TASK_REPLY_OK;

        if (mCurrentTask.isParallelTask()) {
            response = String.valueOf(id);
        }

        onFinishTask(mCurrentTask.getId(), response, getString(R.string.exec_action_drawer_ok));
    }

    private void onSelectYes() {
        Log.i(TAG, "onSelectYes");
        onFinishTask(mCurrentTask.getId(), TASK_REPLY_YES, getString(R.string.exec_action_drawer_yes));
    }

    private void onSelectNo() {
        Log.i(TAG, "onSelectNo");
        onFinishTask(mCurrentTask.getId(), TASK_REPLY_NO, getString(R.string.exec_action_drawer_no));
    }

    private void onFinishTask(int id, String response, String message) {
        isLoading = true;
        stopNotification();

        if (isExecutionFinished) {
            sendStop();
        } else {
            sendReply(id, response);
            showConfirmation(message, true);
//            updateProgressBar();
            updateDisplay();
        }

    }

    private void showConfirmation(String message, boolean success) {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        if (success) {
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.SUCCESS_ANIMATION);
        } else {
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.FAILURE_ANIMATION);
        }
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, message);
        startActivity(intent);

    }

    private void showNotification(final boolean withTimer) {
        final Intent notificationIntent = new Intent(this, ExecutionActivity.class);
        notificationIntent.setAction(REHAGOAL_ACTION_REPLY);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        android.app.Notification notification = new android.app.Notification.Builder(this)
                .setContentTitle(mCurrentTask.getText())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setUsesChronometer(withTimer)
                .setChronometerCountDown(withTimer)
                .setWhen(System.currentTimeMillis() + mCurrentTask.getTimerInMillis())
                .setShowWhen(withTimer)
                .setPriority(android.app.Notification.PRIORITY_HIGH)
                .build();

        if (withTimer) {
            Handler handler = new Handler();
            handler.postAtTime(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "showNotification: finished chronometer notification");
                    startActivity(notificationIntent);
                }
            }, SystemClock.elapsedRealtime() + mCurrentTask.getTimerInMillis());
        }

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void stopNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private void sendReply(int id, String response) {
        Intent intent = new Intent(this, RehaGoalResponderService.class);
        intent.setAction(REHAGOAL_ACTION_REPLY);
        intent.putExtra(REHAGOAL_ACTION_REPLY, new Reply(id, response));
        startService(intent);
    }

    private void sendStop() {
        Intent intent = new Intent(this, RehaGoalResponderService.class);
        intent.setAction(REHAGOAL_ACTION_STOP);
        intent.putExtra(REHAGOAL_ACTION_STOP, new Stop(mCurrentTask.getWorkflowId()));
        startService(intent);

    }

    private void finishExecution() {
        // DO NOT CALL THIS DIRECTLY, USE finish() instead (onDestroy calls this to clean up)
        // close notification
        stopNotification();


        if (mCurrentTask != null) {
            if (isExecutionFinished) {
                showConfirmation(getString(R.string.exec_workflow_finished_success), true);
            } else {
                showConfirmation(getString(R.string.exec_workflow_finished_failure), false);
            }
        }

        // notify webapp of stopping
        if (!REHAGOAL_ACTION_STOP.equals(getIntent().getAction())) {
            sendStop();
        }
        // close activity
        finish();
    }

    private void updateProgressBar() {
        if (isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBarText.setVisibility(View.VISIBLE);
            mTaskTextView.setVisibility(View.GONE);
            mTaskTimer.setVisibility(View.GONE);
            mWearableActionDrawer.lockDrawerClosed();
        } else {
            mProgressBar.setVisibility(View.GONE);
            mProgressBarText.setVisibility(View.GONE);
            mTaskTextView.setVisibility(View.VISIBLE);
            mWearableActionDrawer.unlockDrawer();
        }
    }
}
