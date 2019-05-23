package com.example.network;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class FormRequestBody implements RequestBody {
    private Map<String, String> params = new HashMap<>();

    public FormRequestBody() {

    }

    public FormRequestBody addFormParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public FormRequestBody removeFormParam(String key) {
        params.remove(key);
        return this;
    }

    public void clear() {
        params.clear();
    }

    @Override
    public void writeTo(OutputStream outputStream, Headers headers) throws IOException {
        if (!params.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            builder.deleteCharAt(builder.length() - 1);

            String content = builder.toString();
            byte[] data = content.getBytes("UTF-8");
            headers.addHeader("Content-Type", "application/x-www-form-urlencoded");
            headers.addHeader("Content-Length", String.valueOf(data.length));
            outputStream.write(data);
        }
    }


}
