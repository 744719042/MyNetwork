package com.example.network;

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

    @Override
    public void writeTo(OutputStream outputStream) throws Exception {
        if (!params.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            builder.deleteCharAt(builder.length() - 1);

            String content = builder.toString();
            outputStream.write(content.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();
        }
    }


}
