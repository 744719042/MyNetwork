package com.example.mynetwork;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
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

    public static Context getContext() {
        return sContext;
    }
}
