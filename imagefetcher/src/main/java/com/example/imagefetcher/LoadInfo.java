package com.example.imagefetcher;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

public class LoadInfo {
    private int resourceId;
    private Uri uri;
    private ImageView imageView;
    private BitmapLoadListener loadListener;
    private int error;
    private int placeholder;
    private int targetWidth;
    private int targetHeight;
    private volatile boolean cancel = false;

    // 标识一次加载
    private String key;
    private Object tag;

    public int getResourceId() {
        return resourceId;
    }

    public Uri getUri() {
        return uri;
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
        if (TextUtils.isEmpty(key)) {
            StringBuilder builder = new StringBuilder();
            if (resourceId > 0) {
                builder.append(resourceId);
            } else {
                builder.append(uri.toString());
            }

            builder.append(":").append(targetWidth);
            builder.append(":").append(targetHeight);
            key = builder.toString();
        }
        return key;
    }

    public Object getTag() {
        return tag;
    }

    public void cancel() {
        cancel = true;
    }

    public boolean isCancel() {
        return cancel;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }
}
