package com.example.network;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers {
    private Map<String, String> mValues = new HashMap<>();

    public static Headers of(HttpURLConnection urlConnection) {
        Headers headers = new Headers();
        for (Map.Entry<String, List<String>> entry : urlConnection.getHeaderFields().entrySet()) {
            StringBuilder builder = new StringBuilder();
            for (String value : entry.getValue()) {
                builder.append(value).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            headers.mValues.put(entry.getKey(), builder.toString());
        }
        return headers;
    }

    public static Headers of() {
        return new Headers();
    }

    public void addHeader(String header, String value) {
        mValues.put(header, value);
    }

    public void removeHeader(String header) {
        mValues.remove(header);
    }

    public Map<String, String> getValues() {
        return mValues;
    }
}
