package com.example.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResponseBody {
    private InputStream inputStream;

    public ResponseBody(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String string() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            return new String(bos.toByteArray(), "UTF-8");
        } finally {
            IOUtils.close(inputStream);
            IOUtils.close(bos);
        }
    }

    public InputStream stream() {
        return inputStream;
    }
}
