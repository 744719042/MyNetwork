package com.example.mynetwork;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class CrashLogManager {
    private static final String TAG = "CrashLogManager";
    private static final String CRASH_LOG = "crash_log";
    private static final String KEY_CRASH_LOG = "key_crash_log";
    private SharedPreferences preferences;
    private static CrashLogManager crashLogManager;
    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public CrashLogManager() {
        preferences = sContext.getSharedPreferences(CRASH_LOG, Context.MODE_PRIVATE);
    }

    public synchronized static CrashLogManager getInstance() {
        if (crashLogManager == null) {
            crashLogManager = new CrashLogManager();
        }
        return crashLogManager;
    }

    public void save(Throwable e) {
        StringBuilder stringBuilder = new StringBuilder();
        String msg = e.getLocalizedMessage();
        stringBuilder.append(msg).append("\n");
        StackTraceElement[] stackTraceElement = e.getStackTrace();
        for (StackTraceElement element : stackTraceElement) {
            stringBuilder.append(element.toString()).append("\n");
        }

        /**
         * 保存到数据库
         */
        Log.e(TAG, "save: " + stringBuilder.toString());
        preferences.edit().putString(KEY_CRASH_LOG, stringBuilder.toString()).commit();
    }

    public void upload() {
        /**
         * 从数据库读取保存的异常日志
         */
        String log = preferences.getString(KEY_CRASH_LOG, "");
        if (!TextUtils.isEmpty(log)) {
            Log.e(TAG, "upload: " + log);
            /**
             * 上传到网络服务器
             */
        }
    }
}
