package de.rehagoal.rehagoalwebapp;

/**
 * Utility class which checks the remote api version with the local api version
 * Provides a single instance with the Singleton pattern
 */

public class RehaGoalApiChecker {
    private static final RehaGoalApiChecker RehaGoalApiCheckerSingleton = new RehaGoalApiChecker();
    private static boolean apiVersionValid;

    private RehaGoalApiChecker() { }

    public static RehaGoalApiChecker getInstance() {
        return RehaGoalApiCheckerSingleton;
    }


    public static boolean isApiVersionValid() {
        return apiVersionValid;
    }

    public static void setApiVersionValid(boolean apiVersionValid) {
        RehaGoalApiChecker.apiVersionValid = apiVersionValid;
    }
}
