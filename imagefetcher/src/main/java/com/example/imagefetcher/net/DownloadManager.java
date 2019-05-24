package com.example.imagefetcher.net;

import android.net.Uri;

import com.example.imagefetcher.ImageFetcher;
import com.example.network.Callback;
import com.example.network.HttpClient;
import com.example.network.HttpUrl;
import com.example.network.MyNetException;
import com.example.network.Request;
import com.example.network.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DownloadManager {
    private static final int MAX_REQUEST = 5;
    private static volatile DownloadManager INSTANCE;

    public static DownloadManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DownloadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DownloadManager();
                }
            }
        }
        return INSTANCE;
    }

    private LinkedHashMap<String, List<Callback>> runningMap = new LinkedHashMap<>();
    private LinkedHashMap<String, List<Callback>> pendingMap = new LinkedHashMap<>();

    public synchronized void download(final String url, Callback callback) {
        if (runningMap.containsKey(url)) {
            List<Callback> callbacks = runningMap.get(url);
            callbacks.add(callback);
            return;
        }

        if (runningMap.size() > MAX_REQUEST) {
            List<Callback> callbacks = pendingMap.get(url);
            if (callbacks == null) {
                callbacks = new ArrayList<>();
            }
            callbacks.add(callback);
            pendingMap.put(url, callbacks);
            return;
        }

        List<Callback> list = new ArrayList<>();
        list.add(callback);
        runningMap.put(url, list);

        try {
            HttpUrl httpUrl = new HttpUrl(Uri.parse(url));
            Request request = new Request.Builder().url(httpUrl).get();
            HttpClient httpClient = ImageFetcher.getInstance().getHttpClient();
            httpClient.enqueue(request, new Callback() {
                @Override
                public void onSuccess(Response response) {
                    onFinished(response, url);
                }

                @Override
                public void onFailure(int code, MyNetException e) {
                    onFinished(code, e, url);
                }
            });
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure(-1, null);
            }
        }
    }

    public synchronized void download(final String url, List<Callback> callbackList) {
        if (runningMap.containsKey(url)) {
            List<Callback> callbacks = runningMap.get(url);
            callbacks.addAll(callbackList);
            return;
        }

        if (runningMap.size() > MAX_REQUEST) {
            List<Callback> callbacks = pendingMap.get(url);
            if (callbacks == null) {
                callbacks = new ArrayList<>();
            }
            callbacks.addAll(callbackList);
            pendingMap.put(url, callbacks);
            return;
        }

        List<Callback> list = new ArrayList<>();
        list.addAll(callbackList);
        runningMap.put(url, list);

        try {
            HttpUrl httpUrl = new HttpUrl(Uri.parse(url));
            Request request = new Request.Builder().url(httpUrl).get();
            HttpClient httpClient = ImageFetcher.getInstance().getHttpClient();
            httpClient.enqueue(request, new Callback() {
                @Override
                public void onSuccess(Response response) {
                    onFinished(response, url);
                }

                @Override
                public void onFailure(int code, MyNetException e) {
                    onFinished(code, e, url);
                }
            });
        } catch (Exception e) {
            for (Callback callback : callbackList) {
                if (callback != null) {
                    callback.onFailure(-1, null);
                }
            }
        }
    }

    private synchronized void onFinished(int code, MyNetException e, String url) {
        List<Callback> callbacks = runningMap.remove(url);
        startPendingDownload();

        for (Callback callback : callbacks) {
            callback.onFailure(code, e);
        }
    }

    private void startPendingDownload() {
        if (runningMap.size() < MAX_REQUEST) {
            if (!pendingMap.isEmpty()) {
                Map.Entry<String, List<Callback>> entry = pendingMap.entrySet().iterator().next();
                download(entry.getKey(), entry.getValue());
            }
        }
    }

    private synchronized void onFinished(Response response, String url) {
        List<Callback> callbacks = runningMap.remove(url);
        startPendingDownload();
        for (Callback callback : callbacks) {
            callback.onSuccess(response);
        }
    }
}
