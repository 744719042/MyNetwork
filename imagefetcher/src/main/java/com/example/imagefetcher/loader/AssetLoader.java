package com.example.imagefetcher.loader;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.example.imagefetcher.ImageFetcher;
import com.example.imagefetcher.LoadInfo;
import com.example.network.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class AssetLoader implements BitmapLoader {
    @Override
    public Bitmap load(LoadInfo loadInfo) {
        AssetManager assetManager = ImageFetcher.getInstance().getContext().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(loadInfo.getUri().toString());
            return LoaderHelper.decodeStream(inputStream, loadInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
        }
        return null;
    }
}
