package com.example.imagefetcher.loader;

import android.graphics.Bitmap;

import com.example.imagefetcher.LoadInfo;
import com.example.network.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader implements BitmapLoader {
    @Override
    public Bitmap load(LoadInfo loadInfo) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(loadInfo.getUri().toString());
            return LoaderHelper.decodeStream(inputStream, loadInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inputStream);
        }
        return null;
    }
}
