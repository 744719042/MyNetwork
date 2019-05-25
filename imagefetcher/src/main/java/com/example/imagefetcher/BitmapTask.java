package com.example.imagefetcher;

import android.graphics.Bitmap;

import com.example.imagefetcher.loader.BitmapLoader;
import com.example.imagefetcher.loader.LoaderHelper;
import com.example.imagefetcher.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class BitmapTask implements Runnable {

    Future<?> future;
    LoadInfo loadInfo;
    List<LoadInfo> otherLoadInfo;
    private Dispatcher dispatcher;
    public volatile Bitmap result;

    public BitmapTask(LoadInfo loadInfo, Dispatcher dispatcher) {
        this.loadInfo = loadInfo;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        BitmapLoader bitmapLoader = LoaderHelper.findLoader(loadInfo);
        if (bitmapLoader != null) {
            result = bitmapLoader.load(loadInfo);
        }

        if (result != null) {
            dispatcher.complete(this);
        } else {
            dispatcher.fail(this);
        }
    }

    public boolean cancel() {
        if (loadInfo != null) {
            return false;
        }

        if (!CollectionUtils.isEmpty(otherLoadInfo)) {
            return false;
        }

        return future.cancel(true);
    }

    public boolean isCancelled() {
        return future.isCancelled();
    }

    public void addInfo(LoadInfo info) {
        if (otherLoadInfo == null) {
            otherLoadInfo = new ArrayList<>();
        }
        otherLoadInfo.add(info);
    }

    public void removeInfo(LoadInfo info) {
        if (otherLoadInfo != null) {
            otherLoadInfo.remove(info);
        }
    }
}
