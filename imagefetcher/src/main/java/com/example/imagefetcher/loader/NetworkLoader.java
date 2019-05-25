package com.example.imagefetcher.loader;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.imagefetcher.ImageCache;
import com.example.imagefetcher.ImageFetcher;
import com.example.imagefetcher.LoadInfo;
import com.example.network.HttpClient;
import com.example.network.HttpUrl;
import com.example.network.IOUtils;
import com.example.network.Request;
import com.example.network.Response;

import java.io.InputStream;

public class NetworkLoader implements BitmapLoader {

    @Override
    public Bitmap load(LoadInfo loadInfo) {
        final String url = loadInfo.getUri().toString();
        final ImageCache imageCache = ImageFetcher.getInstance().getImageCache();

        Bitmap bitmap = imageCache.getMemoryCache(url);
        if (bitmap != null) {
            return bitmap;
        }

        bitmap = imageCache.getWeakCache(url);
        if (bitmap != null) {
            return bitmap;
        }

        InputStream inputStream = imageCache.getDiskCache(url);
        if (inputStream == null) {
            HttpUrl httpUrl = new HttpUrl(Uri.parse(url));
            Request request = new Request.Builder().url(httpUrl).get();
            HttpClient httpClient = ImageFetcher.getInstance().getHttpClient();
            Response response = httpClient.execute(request);
            inputStream = response.getResponseBody().stream();
        }

        if (inputStream != null) {
            try {
                imageCache.addDiskCache(url, inputStream);
                bitmap = LoaderHelper.decodeStream(inputStream, loadInfo);
                imageCache.addMemCache(loadInfo.getKey(), bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(inputStream);
            }
        }

        return bitmap;
    }
}
