package de.rehagoal.rehagoalwebapp.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import de.rehagoal.rehagoalwebapp.R;

import java.util.Locale;

import static de.rehagoal.rehagoalwebapp.Constants.TTS_ACTION_SPEAK;

/**
 * Text to Speech IntentService
 */

public class TTSService extends Service implements TextToSpeech.OnInitListener {
    private static final String TAG = TTSService.class.getSimpleName();
    private SharedPreferences mSharedPreferences;
    private TextToSpeech mTts;
    private boolean isServiceInitiated;
    private boolean isTTSEnabled;

    /**
     * Creates a either a new instance of the TTService, or processes the data of the intent
     *
     * @param intent  contains the data which should be processed
     * @param flags   intended for Service usage
     * @param startId intended for Service usage
     * @return an instance of the TTSService
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isTTSEnabled = mSharedPreferences.getBoolean(getString(R.string.settings_key_tts), false);

        if (isTTSEnabled) {
            initService();


            if (intent.hasExtra(TTS_ACTION_SPEAK)) {
                mTts.stop();

                Bundle extras = intent.getExtras();
                if (extras.get(TTS_ACTION_SPEAK) instanceof String) {
                    speakText(extras.getString(TTS_ACTION_SPEAK));
                } else if (extras.get(TTS_ACTION_SPEAK) instanceof String[]) {
                    speakTextList(extras.getStringArray(TTS_ACTION_SPEAK));
                } else {
                    Log.i(TAG, "onStartCommand: started service without intent-data");
                }
            }
        }
        return Service.START_NOT_STICKY;
    }

    /**
     * If TTS was initialised, it stops and shuts down the TextToSpeech service object
     */
    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
            Log.d(TAG, "onDestroy: shutting down tts");
        }
        super.onDestroy();
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Instantiates the service and pulls the SharedSettings of this application
     */
    private void initService() {
        if (!isServiceInitiated) {
            mTts = new TextToSpeech(this, this);
            isServiceInitiated = true;
            Log.d(TAG, "initService: tts instantiated + settings loaded");
        }
    }

    /**
     * This function will call the textToSpeech object with the provided text
     *
     * @param text words to be spoken by the handheld
     */
    private void speakText(String text) {
        mTts.speak(text, TextToSpeech.QUEUE_ADD, null, null);
    }

    /**
     * Helper function for multiple words
     *
     * @param textArray contains n text strings
     */
    private void speakTextList(String[] textArray) {
        for (String text : textArray) {
            speakText(text);
        }
    }

    /**
     * TTS (text to speech) initializer method. It checks if tts could be loaded and sets the language to german.
     * If this message is not available it will show a notification. If tts could have been loaded it notifies as well.
     *
     * @param status contains the tts-init state from super class
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "onInit: TTS success");

            int result = mTts.setLanguage(Locale.GERMAN);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d(TAG, "onInit: TTS error occurred");
                Toast.makeText(this, getString(R.string.tts_loading_language_not_supported), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d(TAG, "onInit: TTS failed");
            Toast.makeText(this, getString(R.string.tts_loading_failed), Toast.LENGTH_SHORT).show();

            // close Service if an error occurred
            stopSelf();
        }
    }
}
