package com.example.imagefetcher;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.example.imagefetcher.utils.CollectionUtils;
import com.example.network.MainThreadPoster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class Dispatcher {
    private static final int MSG_SUBMIT_NEW_LOAD = 100;
    private static final int MSG_SUBMIT_LOAD_LIST = 101;
    private static final int MSG_CANCEL_TAG = 102;
    private static final int MSG_PAUSE_TAG = 103;
    private static final int MSG_RESUME_TAG = 104;
    private static final int MSG_CANCEL_LOAD = 105;
    private static final int MSG_COMPLETE_TASK = 106;
    private static final int MSG_FAIL_TASK = 107;

    private Handler dispatchHandler;
    private MainThreadPoster mainThreadPoster;
    private ImageFetcher imageFetcher;

    private Map<ImageView, LoadInfo> imageViewMap = new ConcurrentHashMap<>();
    private Map<BitmapLoadListener, LoadInfo> listenerMap = new ConcurrentHashMap<>();

    private Set<Object> pauseTag = new HashSet<>();
    private LinkedHashMap<Object, List<LoadInfo>> pauseMap = new LinkedHashMap<>();
    private LinkedHashMap<String, BitmapTask> taskMap = new LinkedHashMap<>();

    public Dispatcher(ImageFetcher imageFetcher) {
        this.imageFetcher = imageFetcher;
        HandlerThread handlerThread = new HandlerThread("ImageFetcher-Dispatcher-Thread");
        handlerThread.start();
        dispatchHandler = new DispatcherHandler(handlerThread.getLooper());
        mainThreadPoster = new MainThreadPoster();
    }

    public void submit(LoadInfo loadInfo) {
        cancelPreLoad(loadInfo);
        Message message = dispatchHandler.obtainMessage(MSG_SUBMIT_NEW_LOAD, loadInfo);
        dispatchHandler.sendMessage(message);
    }

    private void cancelPreLoad(LoadInfo loadInfo) {
        ImageView imageView = loadInfo.getImageView();
        BitmapLoadListener listener = loadInfo.getLoadListener();
        if(imageView != null) {
            LoadInfo preloadInfo = imageViewMap.remove(imageView);
            imageViewMap.put(imageView, loadInfo);
            if (preloadInfo != null) {
                cancelLoad(preloadInfo);
            }
        }

        if (listener != null) {
            LoadInfo preloadInfo = listenerMap.remove(listener);
            listenerMap.put(listener, loadInfo);
            if (preloadInfo != null) {
                cancelLoad(preloadInfo);
            }
        }
    }

    public void batch(List<LoadInfo> loadInfoList) {
        Message message = dispatchHandler.obtainMessage(MSG_SUBMIT_LOAD_LIST, loadInfoList);
        dispatchHandler.sendMessage(message);
    }

    public void cancelLoad(LoadInfo loadInfo) {
        Message message = dispatchHandler.obtainMessage(MSG_CANCEL_LOAD, loadInfo);
        dispatchHandler.sendMessage(message);
    }

    public void cancel(Object tag) {
        Message message = dispatchHandler.obtainMessage(MSG_CANCEL_TAG, tag);
        dispatchHandler.sendMessage(message);
    }

    public void pause(Object tag) {
        Message message = dispatchHandler.obtainMessage(MSG_PAUSE_TAG, tag);
        dispatchHandler.sendMessage(message);
    }

    public void resume(Object tag) {
        Message message = dispatchHandler.obtainMessage(MSG_RESUME_TAG, tag);
        dispatchHandler.sendMessage(message);
    }

    public void complete(final BitmapTask bitmapTask) {
        Message message = dispatchHandler.obtainMessage(MSG_COMPLETE_TASK, bitmapTask);
        dispatchHandler.sendMessage(message);

    }

    public void fail(final BitmapTask bitmapTask) {
        Message message = dispatchHandler.obtainMessage(MSG_FAIL_TASK, bitmapTask);
        dispatchHandler.sendMessage(message);
    }

    public class DispatcherHandler extends Handler {
        DispatcherHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUBMIT_NEW_LOAD: {
                    LoadInfo info = (LoadInfo) msg.obj;
                    handleLoadInfo(info);
                    break;
                }
                case MSG_CANCEL_LOAD: {
                    LoadInfo info = (LoadInfo) msg.obj;
                    handleCancelLoad(info);
                    break;
                }
                case MSG_COMPLETE_TASK: {
                    BitmapTask task = (BitmapTask) msg.obj;
                    handleSuccess(task);
                    break;
                }
                case MSG_FAIL_TASK: {
                    BitmapTask task = (BitmapTask) msg.obj;
                    handleFail(task);
                    break;
                }
                case MSG_CANCEL_TAG: {
                    handleCancel(msg.obj);
                    break;
                }
                case MSG_PAUSE_TAG: {
                    handlePause(msg.obj);
                    break;
                }
                case MSG_RESUME_TAG: {
                    handleResume(msg.obj);
                    break;
                }
            }
        }
    }

    private void handleFail(final BitmapTask bitmapTask) {
        taskMap.remove(bitmapTask.loadInfo.getKey());
        mainThreadPoster.post(new Runnable() {
            @Override
            public void run() {
                if (bitmapTask.loadInfo != null) {
                    handleFail(bitmapTask.loadInfo, bitmapTask);
                }

                if (!CollectionUtils.isEmpty(bitmapTask.otherLoadInfo)) {
                    for (LoadInfo loadInfo : bitmapTask.otherLoadInfo) {
                        handleFail(loadInfo, bitmapTask);
                    }
                }
            }
        });
    }

    private void handleFail(LoadInfo loadInfo, BitmapTask bitmapTask) {
        if (!loadInfo.isCancel()) {
            if (loadInfo.getImageView() != null) {
                imageViewMap.remove(loadInfo.getImageView());
                loadInfo.getImageView().setImageResource(loadInfo.getError());
            }

            if (loadInfo.getLoadListener() != null) {
                listenerMap.remove(loadInfo.getLoadListener());
                loadInfo.getLoadListener().onError(-1);
            }
        }
    }

    private void handleSuccess(final BitmapTask bitmapTask) {
        taskMap.remove(bitmapTask.loadInfo.getKey());
        mainThreadPoster.post(new Runnable() {
            @Override
            public void run() {
                if (bitmapTask.loadInfo != null) {
                    handleSuccess(bitmapTask.loadInfo, bitmapTask);
                }

                if (!CollectionUtils.isEmpty(bitmapTask.otherLoadInfo)) {
                    for (LoadInfo loadInfo : bitmapTask.otherLoadInfo) {
                        handleSuccess(loadInfo, bitmapTask);
                    }
                }
            }
        });
    }

    private void handleSuccess(LoadInfo loadInfo, BitmapTask bitmapTask) {
        if (!loadInfo.isCancel()) {
            if (loadInfo.getImageView() != null) {
                imageViewMap.remove(loadInfo.getImageView());
                loadInfo.getImageView().setImageBitmap(bitmapTask.result);
            }

            if (loadInfo.getLoadListener() != null) {
                listenerMap.remove(loadInfo.getLoadListener());
                loadInfo.getLoadListener().onSuccess(bitmapTask.result);
            }
        }
    }

    private void handleCancelLoad(LoadInfo info) {
        String key = info.getKey();
        BitmapTask task = taskMap.get(key);

        if (task == null) { // 还未被调度执行，被暂停
            info.cancel();
        } else {
            task.removeInfo(info);
            if (task.cancel()) {
                taskMap.remove(key);
            }
        }
    }

    private void handleLoadInfo(final LoadInfo info) {
        if (pauseTag.contains(info.getTag())) {
            List<LoadInfo> infoList = pauseMap.remove(info.getTag());
            if (infoList == null) {
                infoList = new ArrayList<>();
            }
            infoList.add(info);
            pauseMap.put(info.getTag(), infoList);
            return;
        }

        executeOnExecutors(info);
    }

    private void executeOnExecutors(LoadInfo info) {
        String key = info.getKey();
        BitmapTask task = taskMap.get(key);
        if (task != null) {
            task.addInfo(info);
            return;
        }

        BitmapTask bitmapTask = new BitmapTask(info, this);
        final ExecutorService executor = imageFetcher.getExecutorService();
        bitmapTask.future = executor.submit(bitmapTask);
        taskMap.put(info.getKey(), bitmapTask);
    }

    private void handleResume(Object tag) {
        if (!pauseTag.remove(tag)) {
            return;
        }

        List<LoadInfo> loadInfos = pauseMap.remove(tag);
        if (!CollectionUtils.isEmpty(loadInfos)) {
            for (Iterator<LoadInfo> loadInfoIterator = loadInfos.iterator(); loadInfoIterator.hasNext(); ) {
                LoadInfo loadInfo = loadInfoIterator.next();
                if (loadInfo.isCancel()) {
                    continue;
                }

                executeOnExecutors(loadInfo);
            }
        }
    }

    private void handlePause(Object tag) {
        if (!pauseTag.add(tag)) {
            return;
        }

        stopExecute(tag);

    }

    private void stopExecute(Object tag) {
        for (Iterator<BitmapTask> i = taskMap.values().iterator(); i.hasNext(); ) {
            BitmapTask task = i.next();
            if (task.loadInfo != null && task.loadInfo.getTag().equals(tag)) {
                task.loadInfo = null;
            }

            if (!CollectionUtils.isEmpty(task.otherLoadInfo)) {
                for (Iterator<LoadInfo> loadInfoIterator = task.otherLoadInfo.iterator(); loadInfoIterator.hasNext(); ) {
                    LoadInfo loadInfo = loadInfoIterator.next();
                    if (loadInfo.getTag().equals(tag)) {
                        loadInfoIterator.remove();
                    }
                }
            }

            if (task.cancel()) {
                i.remove();
            }
        }
    }

    private void handleCancel(Object tag) {
        if (!pauseTag.remove(tag)) {
            return;
        }

        List<LoadInfo> loadInfos = pauseMap.remove(tag);
        if (!CollectionUtils.isEmpty(loadInfos)) {
            for (LoadInfo loadInfo : loadInfos) {
                loadInfo.cancel();
            }
        }

        stopExecute(tag);
    }
}
