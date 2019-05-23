package com.example.network;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestBody {
    void writeTo(OutputStream outputStream, Headers headers) throws IOException;
}
