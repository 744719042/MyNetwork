package com.example.mynetwork;

import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashLogManager.init(this);
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Log.e(TAG, e.getMessage());
                CrashLogManager.getInstance().save(e);
                RestartService.restart(getApplicationContext());
                defaultHandler.uncaughtException(t, e);
            }
        });
        CrashLogManager.getInstance().upload();
    }
}
