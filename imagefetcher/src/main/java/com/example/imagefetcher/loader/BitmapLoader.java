package com.example.imagefetcher.loader;

import android.graphics.Bitmap;

import com.example.imagefetcher.BitmapLoadListener;
import com.example.imagefetcher.LoadInfo;

public interface BitmapLoader {
    void load(LoadInfo loadInfo, BitmapLoadListener listener);
}
