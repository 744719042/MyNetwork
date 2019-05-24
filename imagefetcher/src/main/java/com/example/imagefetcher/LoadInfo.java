package com.example.imagefetcher;

import android.widget.ImageView;

public class LoadInfo {
    private String path;
    private int resoureId;
    private String url;
    private ImageView imageView;
    private BitmapLoadListener loadListener;
    private int error;
    private int placeholder;

    public String getPath() {
        return path;
    }

    public int getResoureId() {
        return resoureId;
    }

    public String getUrl() {
        return url;
    }

    public int getError() {
        return error;
    }

    public int getPlaceholder() {
        return placeholder;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public BitmapLoadListener getLoadListener() {
        return loadListener;
    }
}
