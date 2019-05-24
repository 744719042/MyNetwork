package com.example.imagefetcher.loader;

import android.text.TextUtils;

import com.example.imagefetcher.LoadInfo;

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

        String path = info.getPath();
        String url = info.getUrl();
        if (TextUtils.isEmpty(path)) {
            if (path.startsWith("file:///android_asset/")) {
                return ASSET_LOADER;
            } else if (path.startsWith("file://")) {
                return FILE_LOADER;
            }
        }

        if (TextUtils.isEmpty(url)) {
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return NETWORK_LOADER;
            }
        }
        return null;
    }
}
