package com.example.mynetwork;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetTestActivity extends AppCompatActivity {
    private static final String TAG = "NetTestActivity";
    private static Executor sExecutor = Executors.newFixedThreadPool(5);
    private static final String HOST = "http://192.168.137.240:8080/HttpServer";
    private static final String FILE_NAME = "mount.jpg";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_test);
        imageView = findViewById(R.id.image);
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
                    outputStream.write("\r\n".getBytes("UTF-8"));
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

    final String DOWNLOAD_URL = HOST + "/download?filename=" + FILE_NAME;

    public void requestHead(View view) {
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(DOWNLOAD_URL).openConnection();
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
        final String DOWNLOAD_URL = HOST + "/download?filename=cat.jpg";
        interrupt = false;
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(DOWNLOAD_URL).openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.setInstanceFollowRedirects(true);
                    httpURLConnection.addRequestProperty("Connection", "close");

                    if (fileSize > 0 && lastPos > 0) {
                        httpURLConnection.addRequestProperty("Range", "bytes=" + lastPos + "-" + fileSize);
                    }

                    httpURLConnection.getResponseCode();
                    printHeads(httpURLConnection.getHeaderFields());
                    httpURLConnection.getResponseCode();
                    fileSize = httpURLConnection.getContentLength();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    RandomAccessFile file = new RandomAccessFile(getCacheDir().getAbsolutePath() + File.separator + "cat.jpg", "rw");
                    if (lastPos > 0) {
                        file.seek(lastPos);
                    }
                    int len = -1;
                    byte[] buf = new byte[1024];
                    int pos = 0;
                    while ((len = inputStream.read(buf)) != -1) {
                        file.write(buf, 0, len);
                        pos += len;
                        if (interrupt) {
                            lastPos = pos + 1;
                            Log.e(TAG, "interrupt at pos = " + lastPos);
                            return;
                        }
                    }
                    file.close();


                    final File image = new File(getCacheDir(), "cat.jpg");
                    if (image.length() == fileSize) {
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                    httpURLConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void interrupt(View view) {
        interrupt = true;
    }

    public void multiThreadDownload(View view) {
        final String postUrl = HOST + "/download?filename=panda.jpg";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(postUrl).openConnection();
                    httpURLConnection.setRequestMethod("HEAD");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.setInstanceFollowRedirects(true);

                    httpURLConnection.getResponseCode();
                    int contentLength = httpURLConnection.getContentLength();
                    httpURLConnection.disconnect();

                    final CountDownLatch countDownLatch = new CountDownLatch(4);
                    int size = contentLength / 4;
                    for (int i = 0; i < 4; i++) {
                        final int start = size * i;
                        int end = start + size - 1;
                        if (i == 3) {
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
                                    httpURLConnection.setDoOutput(false);
                                    httpURLConnection.setInstanceFollowRedirects(true);
                                    httpURLConnection.addRequestProperty("Range", "bytes:" + startPos + "-" + endPos);
                                    httpURLConnection.getResponseCode();
                                    printHeads(httpURLConnection.getHeaderFields());
                                    httpURLConnection.getResponseCode();
                                    fileSize = httpURLConnection.getContentLength();
                                    InputStream inputStream = httpURLConnection.getInputStream();
                                    int len = -1;
                                    byte[] buf = new byte[1024];
                                    final RandomAccessFile file = new RandomAccessFile(getCacheDir().getAbsolutePath()  + File.separator + "panda.jpg", "rw");
                                    file.seek(startPos);
                                    while ((len = inputStream.read(buf)) != -1) {
                                        file.write(buf, 0, len);
                                    }
                                    file.close();
                                    httpURLConnection.disconnect();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                countDownLatch.countDown();
                            }
                        }).start();
                    }

                    countDownLatch.await();
                    final File image = new File(getCacheDir(), "panda.jpg");
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void httpTransferChuck(View view) {
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(DOWNLOAD_URL).openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(false);
                    httpURLConnection.setInstanceFollowRedirects(true);

                    httpURLConnection.getResponseCode();
                    printHeads(httpURLConnection.getHeaderFields());
                    httpURLConnection.getResponseCode();
                    fileSize = httpURLConnection.getContentLength(); // 返回-1
                    InputStream inputStream = httpURLConnection.getInputStream();
                    RandomAccessFile file = new RandomAccessFile(getCacheDir().getAbsolutePath() + File.separator + "mount.jpg", "rw");
                    int len = -1;
                    byte[] buf = new byte[1024];
                    while ((len = inputStream.read(buf)) != -1) {
                        file.write(buf, 0, len);
                    }
                    file.close();


                    final File image = new File(getCacheDir(), "mount.jpg");
//                    if (image.length() == fileSize) {
                        imageView.post(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                                imageView.setImageBitmap(bitmap);
                            }
                        });
//                    }
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
