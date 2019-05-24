package com.example.imagefetcher.loader;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.imagefetcher.BitmapLoadListener;
import com.example.imagefetcher.ImageFetcher;
import com.example.imagefetcher.LoadInfo;
import com.example.network.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class AssetLoader extends AbsBitmapLoader {
    @Override
    public void load(LoadInfo loadInfo, BitmapLoadListener loadListener) {
        AssetManager assetManager = ImageFetcher.getInstance().getContext().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(loadInfo.getPath());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            notifySuccess(loadListener, bitmap);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
        }

        notifyFailure(loadListener, -1, null);
    }
}
