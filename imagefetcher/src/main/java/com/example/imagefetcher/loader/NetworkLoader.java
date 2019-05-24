package com.example.imagefetcher.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.imagefetcher.BitmapLoadListener;
import com.example.imagefetcher.ImageCache;
import com.example.imagefetcher.ImageFetcher;
import com.example.imagefetcher.LoadInfo;
import com.example.imagefetcher.net.DownloadManager;
import com.example.network.Callback;
import com.example.network.MyNetException;
import com.example.network.Response;

public class NetworkLoader extends AbsBitmapLoader {

    @Override
    public void load(LoadInfo loadInfo, final BitmapLoadListener listener) {
        final String url = loadInfo.getUrl();
        final ImageCache imageCache = ImageFetcher.getInstance().getImageCache();

        Bitmap bitmap = imageCache.getMemoryCache(url);
        if (bitmap != null) {
            notifySuccess(listener, bitmap);
            return;
        }

        bitmap = imageCache.getWeakCache(url);
        if (bitmap != null) {
            notifySuccess(listener, bitmap);
            return;
        }

        bitmap = imageCache.getDiskCache(url);
        if (bitmap != null) {
            notifySuccess(listener, bitmap);
            return;
        }

        DownloadManager.getInstance().download(url, new Callback() {

            @Override
            public void onSuccess(Response response) {
                Bitmap result = BitmapFactory.decodeStream(response.getResponseBody().stream());
                if (result != null) {
                    imageCache.addCache(url, result);
                    notifySuccess(listener, result);
                } else {
                    notifyFailure(listener, -1, null);
                }
            }

            @Override
            public void onFailure(int code, MyNetException e) {
                notifyFailure(listener, code, e);
            }
        });
    }
}
