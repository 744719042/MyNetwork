package com.example.imagefetcher;

import android.widget.ImageView;

import java.util.UUID;

public class LoadInfo {
    private String path;
    private int resourceId;
    private String url;
    private ImageView imageView;
    private BitmapLoadListener loadListener;
    private int error;
    private int placeholder;

    // 标识一次加载
    private String key = UUID.randomUUID().toString();
    private Object tag;

    public String getPath() {
        return path;
    }

    public int getResourceId() {
        return resourceId;
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

    public String getKey() {
        return key;
    }

    public Object getTag() {
        return tag;
    }
}
