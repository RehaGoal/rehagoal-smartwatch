package de.rehagoal.rehagoalwebapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.rehagoal.rehagoalwebapp.R;

/**
 * Creates a Preferences / Settings Fragment for UI
 */

public class SettingsFragment extends PreferenceFragment {

    /**
     * Implemented by PreferenceFragement
     * This loads the pre-defined settings from static xml file
     * into application memory
     *
     * @param savedInstanceState could contain a saved state
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }


}

