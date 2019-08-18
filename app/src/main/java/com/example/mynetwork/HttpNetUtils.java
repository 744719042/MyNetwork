package com.example.mynetwork;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpNetUtils {
public interface Callback {
void onSuccess(String text);
void onFailure();
}

public static void get(final String url, final Callback callback) {
    new Thread(new Runnable() {
        @Override
        public void run() {
            ThreadUtils.sleep(20000); // 模拟网络比较慢
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) new URL(url).openConnection();
                http.setInstanceFollowRedirects(true);
                http.setConnectTimeout(5000);
                http.setReadTimeout(5000);
                http.setRequestMethod("GET");
                http.setUseCaches(false);
                int statusCode = http.getResponseCode();
                if (statusCode == 200) {
                    callback.onSuccess(http.getResponseMessage());
                } else {
                    callback.onFailure();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (http != null) {
                    http.disconnect();
                }
            }
        }
        }).start();
    }
}
