package de.rehagoal.rehagoalwebapp.model;

import de.rehagoal.rehagoalwebapp.R;

/**
 * Contains all Navigation-Sections for the MainActivity Fragments
 */

public enum Navigation {
    Workflows(R.string.nav_workflow_title, R.drawable.ic_workflow),
    Settings(R.string.nav_settings_title, R.drawable.ic_settings);

    final int title;
    final int drawable;

    /**
     * Sets a new Navigation object
     *
     * @param title    title of the navigation (shown in NavigationDrawer)
     * @param drawable Icon of the navigation section
     */
    Navigation(final int title, final int drawable) {
        this.title = title;
        this.drawable = drawable;
    }

    /**
     * Get the current Navigation title
     *
     * @return the title of the selected navigation
     */
    public int getTitle() {
        return title;
    }

    /**
     * Get the current Navigation icon
     *
     * @return the icon of the selected navigation
     */
    public int getDrawable() {
        return drawable;
    }
}