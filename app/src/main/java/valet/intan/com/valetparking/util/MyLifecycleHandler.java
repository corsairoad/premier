package valet.intan.com.valetparking.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by fadlymunandar on 10/5/17.
 */

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = MyLifecycleHandler.class.getSimpleName();

    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        android.util.Log.w("test", "application is in foreground: " + (resumed > paused));
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        android.util.Log.w(TAG, "application is visible: " + (started > stopped));
    }


    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        return resumed > paused;
    }

    public static void relaunchAppIfNotVisible(Context context) {
        boolean relaunch = PrefManager.getInstance(context).getRelaunch();
        if (!isApplicationVisible() && relaunch ) {
            RelaunchAlarm.getInstance(context).launchApp(); // launch in 4 second
        }
    }

    public static void relaunchToWelcomeActivity(Context context) {
        boolean relaunch = PrefManager.getInstance(context).getRelaunch();
        if (!isApplicationVisible() && relaunch) {
            RelaunchAlarm.getInstance(context).launchAppToWelcomeActivity();
        }
    }

}
