package com.example.imagefetcher.loader;

import android.graphics.Bitmap;

import com.example.imagefetcher.BitmapLoadListener;

public abstract class AbsBitmapLoader implements BitmapLoader {

    protected void notifyFailure(BitmapLoadListener listener, int code, Throwable throwable) {
        if (listener != null) {
            listener.onError(code, throwable);
        }
    }

    protected void notifySuccess(BitmapLoadListener listener, Bitmap bitmap) {
        if (listener != null) {
            listener.onSuccess(bitmap);
        }
    }
}
