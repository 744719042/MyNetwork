package com.example.network;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class Dispatcher implements DispatcherCallback {
    private static final int MAX_REQUESTS = 64;
    private static final int MAX_PER_HOST = 5;

    private BlockingQueue<NetWorkTask> readyQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<NetWorkTask> runningQueue = new LinkedBlockingDeque<>();

    private Executor executor = Executors.newFixedThreadPool(5);

    private static class DispatcherHolder {
        private static final Dispatcher INSTANCE = new Dispatcher();
    }

    public static Dispatcher getInstance() {
        return DispatcherHolder.INSTANCE;
    }

    public void enqueue(NetWorkTask task) {
        Request request = task.getRequest();
        if (runningQueue.size() < MAX_REQUESTS && isPerHostFull(request.getUrl().getHost())) {
            executor.execute(task);
        } else {
            readyQueue.add(task);
        }
    }

    private boolean isPerHostFull(String host) {
        int count = 0;
        for (Iterator<NetWorkTask> i = runningQueue.iterator(); i.hasNext(); ) {
            NetWorkTask task = i.next();
            Request request = task.getRequest();
            if (request.getUrl().getHost().equals(host)) {
                count++;
                if (count >= MAX_PER_HOST) {
                    return true;
                }
            }
        }
        return false;
    }

    private void startReadyTasks() {
        for (Iterator<NetWorkTask> i = readyQueue.iterator(); i.hasNext(); ) {
            NetWorkTask task = i.next();
            Request request = task.getRequest();
            if (runningQueue.size() < MAX_REQUESTS) {
                if (isPerHostFull(request.getUrl().getHost())) {
                    i.remove();
                    runningQueue.add(task);
                    executor.execute(task);
                }
            } else {
                break;
            }
        }
    }

    @Override
    public void onSuccess(NetWorkTask task, Response response) {
        runningQueue.remove(task);
        startReadyTasks();
        final Callback callback = task.getCallback();
        if (callback != null) {
            callback.onSuccess(response);
        }
    }

    @Override
    public void onFailure(NetWorkTask task, int error, MyNetException exception) {
        runningQueue.remove(task);
        startReadyTasks();
        Callback callback = task.getCallback();
        if (callback != null) {
            callback.onFailure(error, exception);
        }
    }

    @Override
    public void onCancel(NetWorkTask task) {
        runningQueue.remove(task);
        startReadyTasks();
    }
}
