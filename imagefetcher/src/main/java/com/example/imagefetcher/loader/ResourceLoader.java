package com.example.imagefetcher.loader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.imagefetcher.ImageFetcher;
import com.example.imagefetcher.LoadInfo;

public class ResourceLoader implements BitmapLoader {
    @Override
    public Bitmap load(LoadInfo loadInfo) {
        Resources resources = ImageFetcher.getInstance().getContext().getResources();
        Drawable drawable = resources.getDrawable(loadInfo.getResourceId());
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.draw(canvas);
        }
        return bitmap;
    }
}
