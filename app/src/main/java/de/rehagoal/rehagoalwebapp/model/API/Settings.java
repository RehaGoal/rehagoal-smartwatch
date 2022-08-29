package de.rehagoal.rehagoalwebapp.model.API;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Settings stub
 */

public class Settings implements Serializable {
    @SerializedName("tts")
    boolean textToSpeech;
}
