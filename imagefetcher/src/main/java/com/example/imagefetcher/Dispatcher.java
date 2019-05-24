package com.example.imagefetcher;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.example.imagefetcher.loader.BitmapLoader;
import com.example.imagefetcher.loader.LoaderHelper;
import com.example.network.MainThreadPoster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

public class Dispatcher {
    private static final int MSG_SUBMIT_NEW_LOAD = 100;
    private static final int MSG_SUBMIT_LOAD_LIST = 101;
    private static final int MSG_CANCEL_TAG = 102;
    private static final int MSG_PAUSE_TAG = 103;
    private static final int MSG_RESUME_TAG = 104;

    private Handler dispatchHandler;
    private MainThreadPoster mainThreadPoster;
    private ImageFetcher imageFetcher;

    private Map<ImageView, LoadInfo> imageViewMap = new HashMap<>();
    private Map<BitmapLoadListener, LoadInfo> listenerMap = new HashMap<>();
    private Set<Object> pauseTag = new HashSet<>();
    private LinkedHashMap<Object, List<LoadInfo>> pendingMap = new LinkedHashMap<>();

    public Dispatcher(ImageFetcher imageFetcher) {
        this.imageFetcher = imageFetcher;
        HandlerThread handlerThread = new HandlerThread("ImageFetcher-Dispatcher-Thread");
        handlerThread.start();
        dispatchHandler = new DispatcherHandler(handlerThread.getLooper());
        mainThreadPoster = new MainThreadPoster();
    }

    public void submit(LoadInfo loadInfo) {
        Message message = dispatchHandler.obtainMessage(MSG_SUBMIT_NEW_LOAD, loadInfo);
        dispatchHandler.sendMessage(message);
    }

    public void batch(List<LoadInfo> loadInfoList) {
        Message message = dispatchHandler.obtainMessage(MSG_SUBMIT_LOAD_LIST, loadInfoList);
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
                case MSG_SUBMIT_LOAD_LIST:
                    break;
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

    private void handleLoadInfo(final LoadInfo info) {
        if (pauseTag.contains(info.getTag())) {
            List<LoadInfo> infoList = pendingMap.remove(info.getTag());
            if (infoList == null) {
                infoList = new ArrayList<>();
            }
            infoList.add(info);
            pendingMap.put(info.getTag(), infoList);
            return;
        }

        final ImageView imageView = info.getImageView();
        final BitmapLoadListener listener = info.getLoadListener();

        Executor executor = imageFetcher.getExecutor();
        if (imageView != null) {
            imageViewMap.put(imageView, info);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    processImageViewLoad(imageView, info);
                }
            });
        } else if (listener != null) {
            listenerMap.put(listener, info);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    processListenerLoad(listener, info);
                }
            });
        }
    }

    private void processListenerLoad(final BitmapLoadListener listener, LoadInfo info) {
        BitmapLoader bitmapLoader = LoaderHelper.findLoader(info);
        if (bitmapLoader != null) {
            bitmapLoader.load(info, new BitmapLoadListener() {
                @Override
                public void onSuccess(final Bitmap bitmap) {
                    mainThreadPoster.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onSuccess(bitmap);
                            }
                        }
                    });
                }

                @Override
                public void onError(final int code, final Throwable throwable) {
                    mainThreadPoster.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onError(code, throwable);
                            }
                        }
                    });
                }
            });
        } else {
            mainThreadPoster.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onError(-1, null);
                    }
                }
            });
        }
    }

    private void processImageViewLoad(final ImageView imageView, final LoadInfo info) {
        BitmapLoader bitmapLoader = LoaderHelper.findLoader(info);
        if (bitmapLoader != null) {
            bitmapLoader.load(info, new BitmapLoadListener() {
                @Override
                public void onSuccess(final Bitmap bitmap) {
                    mainThreadPoster.post(new Runnable() {
                        @Override
                        public void run() {
                            if (imageView != null) {
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                }

                @Override
                public void onError(final int code, final Throwable throwable) {
                    mainThreadPoster.post(new Runnable() {
                        @Override
                        public void run() {
                            if (imageView != null) {
                                imageView.setImageResource(info.getError());
                            }
                        }
                    });
                }
            });
        } else {
            mainThreadPoster.post(new Runnable() {
                @Override
                public void run() {
                    if (imageView != null) {
                        imageView.setImageResource(info.getError());
                    }
                }
            });
        }
    }

    private void handleResume(Object tag) {
        if (!pauseTag.remove(tag)) {
            return;
        }


    }

    private void handlePause(Object tag) {
        if (!pauseTag.add(tag)) {
            return;
        }


    }

    private void handleCancel(Object tag) {

    }
}
