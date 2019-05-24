package com.example.imagefetcher.loader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.imagefetcher.BitmapLoadListener;
import com.example.imagefetcher.LoadInfo;
import com.example.network.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader extends AbsBitmapLoader {
    @Override
    public void load(LoadInfo loadInfo, BitmapLoadListener listener) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(loadInfo.getPath());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            notifySuccess(listener, bitmap);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
        }

        notifyFailure(listener, -1, null);
    }
}
