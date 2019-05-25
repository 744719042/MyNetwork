package com.example.imagefetcher;

import android.graphics.Bitmap;

public interface BitmapLoadListener {
    void onSuccess(Bitmap bitmap);
    void onError(int code);
}
