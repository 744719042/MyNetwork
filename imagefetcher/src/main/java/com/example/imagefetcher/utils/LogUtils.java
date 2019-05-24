package com.example.imagefetcher.utils;

import android.util.Log;

import com.example.imagefetcher.BuildConfig;


public class LogUtils {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private LogUtils() {

    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void printException(String tag, Exception e) {
        if (DEBUG) {
            Log.e(tag, e.getMessage(), e);
        }
    }
}
