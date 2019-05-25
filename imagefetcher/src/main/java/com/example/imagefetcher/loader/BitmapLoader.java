package com.example.imagefetcher.loader;

import android.graphics.Bitmap;

import com.example.imagefetcher.LoadInfo;

public interface BitmapLoader {
    Bitmap load(LoadInfo loadInfo);
}
