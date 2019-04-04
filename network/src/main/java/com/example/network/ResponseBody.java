package com.example.network;

import java.io.InputStream;

public interface ResponseBody {
    String body();
    InputStream stream();
}
