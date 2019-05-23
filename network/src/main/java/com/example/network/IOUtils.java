package com.example.network;

import java.io.Closeable;

public class IOUtils {
    private IOUtils() {

    }

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
