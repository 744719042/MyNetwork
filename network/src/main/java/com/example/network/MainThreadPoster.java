package com.example.network;

import android.os.Handler;
import android.os.Looper;

public class MainThreadPoster implements Poster {
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void post(Runnable task) {
        if (task == null) {
            return;
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            handler.post(task);
        }
    }
}
