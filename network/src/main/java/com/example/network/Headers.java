package com.example.network;

import java.util.HashMap;
import java.util.Map;

public class Headers {
    private Map<String, String> mValues = new HashMap<>();

    public void addHeader(String header, String value) {
        mValues.put(header, value);
    }

    public void removeHeader(String header) {
        mValues.remove(header);
    }
}
