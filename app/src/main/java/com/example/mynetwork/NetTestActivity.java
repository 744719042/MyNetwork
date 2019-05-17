package com.example.mynetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetTestActivity extends AppCompatActivity {
    private static final String TAG = "NetTestActivity";
    private static Executor sExecutor = Executors.newFixedThreadPool(5);
    private static final String HOST = "http://10.2.129.168:8080/HttpServer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_test);
    }

    public void requestGet(View view) {
        final String getUrl = HOST + "/hello.html";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(getUrl).openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setInstanceFollowRedirects(true);

                    printHeads(httpURLConnection.getHeaderFields());
                    InputStream inputStream = httpURLConnection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }

                    String text = new String(bos.toByteArray(), "UTF-8");
                    Log.e(TAG, text);
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void printHeads(Map<String, List<String>> headerFields) {
        if (headerFields != null) {
            for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                Log.e(TAG, entry.getKey() + ": " + entry.getValue());
                if ("set-cookie".equalsIgnoreCase(entry.getKey())) {
                    List<String> cookies = entry.getValue();
                    for (String cookie : cookies) {
                        if (cookie.contains("JSESSIONID")) {
                            sessionId = cookie.split("=")[1];
                        }
                    }
                }
            }
        }
    }

    private String sessionId = "";

    public void requestPost(View view) {
        final String postUrl = HOST + "/unsafelogin";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(postUrl).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setInstanceFollowRedirects(true);

                    if (!TextUtils.isEmpty(sessionId)) {
                        httpURLConnection.addRequestProperty("cookie", "JSESSIONID=" + sessionId);
                    }
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write("name=xxx&password=abcd1234".getBytes("UTF-8"));
                    outputStream.flush();

                    httpURLConnection.getResponseCode();
                    printHeads(httpURLConnection.getHeaderFields());
                    InputStream inputStream = httpURLConnection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }

                    String text = new String(bos.toByteArray(), "UTF-8");
                    Log.e(TAG, text);
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void uploadFile(View view) {
        final String postUrl = HOST + "/upload";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(postUrl).openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setInstanceFollowRedirects(true);

                    String boundary = "71f62f86-7932-4ba5-bdb9-0deda48ef376";
                    httpURLConnection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    StringBuilder builder = new StringBuilder("--" + boundary);
                    builder.append("\r\n");
                    builder.append("Content-Disposition: form-data; name=\"filename\"; filename=\"hello.mp4\"").append("\r\n");
                    builder.append("Content-Type: video/mp4").append("\r\n");


                    byte[] buf = new byte[1024];
                    InputStream data = getAssets().open("swim.mp4");
                    builder.append("Content-Length: ").append(data.available()).append("\r\n");
                    builder.append("\r\n");
                    outputStream.write(builder.toString().getBytes("UTF-8"));

                    int dataLen = -1;
                    while ((dataLen = data.read(buf)) != -1) {
                        outputStream.write(buf, 0, dataLen);
                    }
                    outputStream.write(("--" + boundary + "--").getBytes("UTF-8"));
                    outputStream.flush();

                    httpURLConnection.getResponseCode();
                    printHeads(httpURLConnection.getHeaderFields());
                    InputStream inputStream = httpURLConnection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int len = -1;
                    while ((len = inputStream.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }

                    String text = new String(bos.toByteArray(), "UTF-8");
                    Log.e(TAG, text);
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void requestHead(View view) {
        final String postUrl = HOST + "/download?filename=sport.mp4";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(postUrl).openConnection();
                    httpURLConnection.setRequestMethod("HEAD");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
//                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setInstanceFollowRedirects(true);
// 会抛出异常
//                    OutputStream outputStream = httpURLConnection.getOutputStream();
//                    outputStream.write("filename=sport.mp4".getBytes("UTF-8"));
//                    outputStream.flush();

                    httpURLConnection.getResponseCode();
                    printHeads(httpURLConnection.getHeaderFields());
                    InputStream inputStream = httpURLConnection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }

                    String text = new String(bos.toByteArray(), "UTF-8");
                    Log.e(TAG, text);
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private volatile boolean interrupt = false;
    private volatile int lastPos = 0;
    private volatile int fileSize = 0;

    public void downloadInterrupted(View view) {
        final String postUrl = HOST + "/download";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(postUrl).openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setInstanceFollowRedirects(true);

                    if (fileSize > 0 && lastPos > 0) {
                        httpURLConnection.addRequestProperty("Range", "bytes=" + lastPos + "-" + fileSize);
                    }
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write("filename=sport.mp4".getBytes("UTF-8"));
                    outputStream.flush();

                    httpURLConnection.getResponseCode();
                    printHeads(httpURLConnection.getHeaderFields());
                    httpURLConnection.getResponseCode();
                    fileSize = httpURLConnection.getContentLength();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    RandomAccessFile file = new RandomAccessFile(getCacheDir(), "video.mp4");
                    int len = -1;
                    byte[] buf = new byte[1024];
                    int pos = 0;
                    while ((len = inputStream.read(buf)) != -1) {
                        file.write(buf, 0, len);
                        pos += len;
                        if (interrupt) {
                            return;
                        }
                    }
                    file.close();
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void multiThreadDownload(View view) {
        final String postUrl = HOST + "/download";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(postUrl).openConnection();
                    httpURLConnection.setRequestMethod("HEAD");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setInstanceFollowRedirects(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write("filename=sport.mp4".getBytes("UTF-8"));
                    outputStream.flush();

                    httpURLConnection.getResponseCode();
                    int contentLength = httpURLConnection.getContentLength();
                    httpURLConnection.disconnect();

                    int size = contentLength / 4;
                    for (int i = 0; i < 4; i++) {
                        final int start = size * i;
                        int end = start + size - 1;
                        if (end > contentLength) {
                            end = contentLength;
                        }

                        final int startPos = start;
                        final int endPos = end;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(postUrl).openConnection();
                                    httpURLConnection.setRequestMethod("GET");
                                    httpURLConnection.setConnectTimeout(3000);
                                    httpURLConnection.setReadTimeout(3000);
                                    httpURLConnection.setDoInput(true);
                                    httpURLConnection.setDoOutput(true);
                                    httpURLConnection.setInstanceFollowRedirects(true);

                                    if (fileSize > 0 && lastPos > 0) {
                                        httpURLConnection.addRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
                                    }
                                    OutputStream outputStream = httpURLConnection.getOutputStream();
                                    outputStream.write("filename=sport.mp4".getBytes("UTF-8"));
                                    outputStream.flush();

                                    httpURLConnection.getResponseCode();
                                    printHeads(httpURLConnection.getHeaderFields());
                                    httpURLConnection.getResponseCode();
                                    fileSize = httpURLConnection.getContentLength();
                                    InputStream inputStream = httpURLConnection.getInputStream();
                                    int len = -1;
                                    byte[] buf = new byte[1024];
                                    final RandomAccessFile file = new RandomAccessFile(getCacheDir(), "video1.mp4");
                                    file.seek(startPos);
                                    while ((len = inputStream.read(buf)) != -1) {
                                        file.write(buf, 0, len);
                                    }
                                    file.close();
                                    httpURLConnection.disconnect();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void httpTransferChuck(View view) {
        final String postUrl = HOST + "/download";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(postUrl).openConnection();
                    httpURLConnection.setRequestMethod("HEAD");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setInstanceFollowRedirects(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write("filename=sport.mp4".getBytes("UTF-8"));
                    outputStream.flush();

                    httpURLConnection.getResponseCode();
                    printHeads(httpURLConnection.getHeaderFields());
                    InputStream inputStream = httpURLConnection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }

                    String text = new String(bos.toByteArray(), "UTF-8");
                    Log.e(TAG, text);
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void httpCache(View view) {

    }
}
