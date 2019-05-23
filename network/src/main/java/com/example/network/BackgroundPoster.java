package com.example.network;

import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;

public class BackgroundPoster implements Poster, Runnable {
    private BlockingQueue<Runnable> tasks = new LinkedBlockingDeque<>();
    private volatile boolean executorRunning = false;
    private Executor executor;

    public BackgroundPoster(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void post(Runnable task) {
        if (task == null) {
            return;
        }

        if (Looper.getMainLooper() == Looper.myLooper()) {
            tasks.offer(task);
            if (!executorRunning) {
                executor.execute(this);
                executorRunning = true;
            }
        } else {
            runTask(task);
        }
    }

    @Override
    public void run() {
        while (true) {
            Runnable task = tasks.poll();
            if (task == null) {
                break;
            }
            runTask(task);
        }

        executorRunning = false;
    }

    private void runTask(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
