package com.example.imagefetcher;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.LruCache;

import com.example.imagefetcher.disk.DiskLruCache;
import com.example.network.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class ImageCache {
    private LruCache<String, Bitmap> memCache;
    private Map<String, WeakReference<Bitmap>> weakCache = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;
    private final Object diskLock = new Object();
    private final Object memLock = new Object();
    private DiskLruCache diskLruCache;

    public ImageCache(int memMaxSize, final long maxDiskSize, final File diskCacheDir, Executor executor) {
        memCache = new LruCache<String, Bitmap>(memMaxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);
                if (evicted) {
                    weakCache.put(key, new WeakReference<>(oldValue));
                }
            }
        };

        executor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (diskLock) {
                    diskLruCache = DiskLruCache.open(maxDiskSize, diskCacheDir);
                    diskLruCache.initDiskLruCache();
                    initialized = true;
                    diskLock.notifyAll();
                }
            }
        });
    }

    public void addCache(String url, Bitmap bitmap) {
        if (TextUtils.isEmpty(url) || bitmap == null) {
            return;
        }

        synchronized (memLock) {
            memCache.put(url, bitmap);
        }

        addDiskCache(url, bitmap);
    }

    private void addDiskCache(String url, Bitmap bitmap) {
        waitForDiskReady();
        if (diskLruCache.isFileExist(url)) {
            return;
        }

        OutputStream outputStream = null;
        try {
            outputStream = diskLruCache.newOutputStream(url);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            diskLruCache.commitOutputStream(url);
        } catch (IOException e) {
            e.printStackTrace();
            diskLruCache.clearOutputStream(url);
        } finally {
            IOUtils.close(outputStream);
        }
    }

    public Bitmap getMemoryCache(String key) {
        if (!TextUtils.isEmpty(key)) {
            synchronized (memLock) {
                return memCache.get(key);
            }
        }
        return null;
    }

    public Bitmap getWeakCache(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        WeakReference<Bitmap> reference = weakCache.remove(key);
        if (reference == null) {
            return null;
        }

        return reference.get();
    }

    public Bitmap getDiskCache(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        waitForDiskReady();

        InputStream inputStream = null;
        try {
            inputStream = diskLruCache.newInputStream(url);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
        }
        return null;
    }

    private void waitForDiskReady() {
        if (!initialized) {
            synchronized (diskLock) {
                while (!initialized) {
                    try {
                        diskLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
