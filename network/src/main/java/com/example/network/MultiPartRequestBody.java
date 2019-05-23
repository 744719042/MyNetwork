package com.example.network;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class MultiPartRequestBody implements RequestBody {
    public static final String BOUNDARY = "71f62f86-7932-4ba5-bdb9-0deda48ef376";

    public interface ProgressListener {
        void onProgressUpdate(int current, int total);
    }

    private String contentType;
    private InputStream stream;
    private String filename;
    private String name;
    private Map<String, String> params = new HashMap<>();
    private ProgressListener listener;

    public MultiPartRequestBody() {
    }

    public MultiPartRequestBody setMultiPart(String name, String contentType, String filename, InputStream file) {
        this.name = name;
        this.contentType = contentType;
        this.filename = filename;
        this.stream = file;
        return this;
    }

    public MultiPartRequestBody addFormPart(String name, String value) {
        params.put(name, value);
        return this;
    }

    public void setProgressListener(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void writeTo(OutputStream outputStream, Headers headers) throws IOException {
        StringBuilder builder = new StringBuilder();
        headers.addHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        if (!params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append("--" + BOUNDARY + "\r\n");
                builder.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"");
                builder.append("Content-Length: ").append(TextUtils.isEmpty(entry.getValue()) ? 0 : entry.getValue().length()).append("\r\n\r\n");
                builder.append(entry.getValue());
            }
        }

        builder.append("--" + BOUNDARY + "\r\n");
        builder.append("Content-Disposition: form-data; name=\"filename\"; filename=\"").append(name).append("\"").append("\r\n");
        builder.append("Content-Type: ").append(contentType).append("\r\n");

        byte[] buf = new byte[1024];
        int total = stream.available();
        builder.append("Content-Length: ").append(total).append("\r\n");
        builder.append("\r\n");
        outputStream.write(builder.toString().getBytes("UTF-8"));

        int dataLen = -1;
        int current = 0;
        while ((dataLen = stream.read(buf)) != -1) {
            outputStream.write(buf, 0, dataLen);
            current += dataLen;
            if (listener != null) {
                listener.onProgressUpdate(current, total);
            }
        }
        outputStream.write("\r\n".getBytes("UTF-8"));
        outputStream.write(("--" + BOUNDARY + "--").getBytes("UTF-8"));
    }
}
