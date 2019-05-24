package com.example.imagefetcher.loader;

import com.example.imagefetcher.BitmapLoadListener;
import com.example.imagefetcher.LoadInfo;

public interface BitmapLoader {
    void load(LoadInfo loadInfo, BitmapLoadListener listener);
}
