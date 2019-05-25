package com.example.imagefetcher.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.example.imagefetcher.LoadInfo;

import java.io.InputStream;

public class LoaderHelper {
    private static final AssetLoader ASSET_LOADER = new AssetLoader();
    private static final FileLoader FILE_LOADER = new FileLoader();
    private static final NetworkLoader NETWORK_LOADER = new NetworkLoader();
    private static final ResourceLoader RESOURCE_LOADER = new ResourceLoader();
    private LoaderHelper() {

    }

    public static BitmapLoader findLoader(LoadInfo info) {
        if (info.getResourceId() != 0) {
            return RESOURCE_LOADER;
        }

        String url = info.getUri().toString();
        if (TextUtils.isEmpty(url)) {
            if (url.startsWith("file:///android_assets/")) {
                return ASSET_LOADER;
            } else if (url.startsWith("file://")) {
                return FILE_LOADER;
            } else if (url.startsWith("http://") || url.startsWith("https://")) {
                return NETWORK_LOADER;
            }
        }

        return null;
    }

    public static Bitmap decodeStream(InputStream inputStream, LoadInfo loadInfo) {
        int vwidth = loadInfo.getTargetWidth(), vheight = loadInfo.getTargetHeight();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, null, options);
        int dwidth = options.outWidth, dheight = options.outHeight;
        int sampleSize = calcSampleSize(vwidth, vheight, dwidth, dheight);
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeStream(inputStream, null, options);
    }

    private static int calcSampleSize(int vwidth, int vheight, int dwidth, int dheight) {
        int sampleSize = 1;
        if (dheight > vheight || dwidth > vwidth) {
            int halfHeight = dheight / 2;
            int halfWidth = dwidth / 2;
            while ((halfHeight / sampleSize) >= vheight && (halfWidth / sampleSize) >= vwidth) {
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }
}
