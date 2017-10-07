package valet.intan.com.valetparking.util;

import android.app.Application;

/**
 * Created by fadlymunandar on 10/5/17.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PrefManager.getInstance(this).setRelaunch(true);
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
    }


}
