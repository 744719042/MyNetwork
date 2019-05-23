package com.example.network;

import android.net.Uri;
import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class NetWorkTask implements Runnable {
    private Request request;
    private Callback callback;
    private DispatcherCallback dispatcherCallback;
    private int connectTimeout;
    private int readTimeout;
    private HostnameVerifier hostnameVerifier;
    private SSLSocketFactory sslSocketFactory;

    public NetWorkTask(Request request, Callback callback, DispatcherCallback dispatcherCallback,
                       int connectTimeout, int readTimeout, HostnameVerifier hostnameVerifier,
                       SSLSocketFactory sslSocketFactory) {
        this.request = request;
        this.callback = callback;
        this.dispatcherCallback = dispatcherCallback;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.hostnameVerifier = hostnameVerifier;
        this.sslSocketFactory = sslSocketFactory;
    }

    public NetWorkTask(Request request, int connectTimeout, int readTimeout,
                       HostnameVerifier hostnameVerifier,
                       SSLSocketFactory sslSocketFactory) {
        this(request, null, null, connectTimeout, readTimeout, hostnameVerifier, sslSocketFactory);
    }

    public Response execute() {
        if (request.isCancel()) {
            return null;
        }

        String url = request.getUrl().getUrl();
        int retryCount = request.getRetryCount();

        while (true) {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = openConnection(url);
                httpURLConnection.connect();
                int code = httpURLConnection.getResponseCode();
                if (request.isCancel()) {
                    return null;
                }

                if (code >= 200 && code < 300) { // 请求成功
                    Response response = new Response();
                    response.setStatus(code);
                    response.setMessage(httpURLConnection.getResponseMessage());
                    response.setResponseBody(new ResponseBody(httpURLConnection.getInputStream()));
                    response.setHeaders(Headers.of(httpURLConnection));
                    return response;
                } else if (code >= 300 && code < 400) {
                    String redirectUrl = httpURLConnection.getHeaderField("Location");
                    if (!TextUtils.isEmpty(redirectUrl)) {
                        url = redirectUrl;
                        request.setRedirectUrl(Uri.parse(redirectUrl)); // 解析失败抛出异常
                        continue;
                    }
                }
            } catch (SocketTimeoutException e) {
                if (request.isCancel()) {
                    return null;
                }
                if (retryCount > 0) {
                    retryCount--;
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }

    private HttpURLConnection openConnection(String url) throws IOException {
        RequestMethod method = request.getMethod();
        RequestBody requestBody = request.getBody();
        Headers headers = request.getHeaders();

        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        if (httpURLConnection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) httpURLConnection).setHostnameVerifier(hostnameVerifier);
            ((HttpsURLConnection) httpURLConnection).setSSLSocketFactory(sslSocketFactory);
        }
        httpURLConnection.setRequestMethod(method.name());
        httpURLConnection.setConnectTimeout(connectTimeout);
        httpURLConnection.setReadTimeout(readTimeout);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(requestBody != null);
        httpURLConnection.setInstanceFollowRedirects(false);

        if (requestBody != null) {
            OutputStream outputStream = httpURLConnection.getOutputStream();
            requestBody.writeTo(outputStream, headers);
        }
        for (Map.Entry<String, String> entry : headers.getValues().entrySet()) {
            httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
        }
        return httpURLConnection;
    }

    @Override
    public void run() {
        if (request.isCancel()) {
            notifyCancel();
            return;
        }

        String url = request.getUrl().getUrl();
        int retryCount = request.getRetryCount();

        int error = -1;
        Throwable throwable = null;
        while (true) {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = openConnection(url);
                httpURLConnection.connect();
                int code = httpURLConnection.getResponseCode();
                if (request.isCancel()) {
                    notifyCancel();
                    return;
                }

                if (code >= 200 && code < 300) { // 请求成功
                    Response response = new Response();
                    response.setStatus(code);
                    response.setMessage(httpURLConnection.getResponseMessage());
                    response.setResponseBody(new ResponseBody(httpURLConnection.getInputStream()));
                    response.setHeaders(Headers.of(httpURLConnection));
                    notifySuccess(response);
                } else if (code >= 300 && code < 400) {
                    String redirectUrl = httpURLConnection.getHeaderField("Location");
                    if (!TextUtils.isEmpty(redirectUrl)) {
                        url = redirectUrl;
                        request.setRedirectUrl(Uri.parse(redirectUrl)); // 解析失败抛出异常
                        continue;
                    }
                } else  {
                    error = -100;
                    throwable = new MyNetException("Http Server Error");
                }
            } catch (SocketTimeoutException e) {
                if (request.isCancel()) {
                    return;
                }
                if (retryCount > 0) {
                    retryCount--;
                    continue;
                }

                throwable = e;
            } catch (ProtocolException e) {
                e.printStackTrace();
                throwable = e;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                throwable = e;
            } catch (IOException e) {
                e.printStackTrace();
                throwable = e;
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }

            notifyFailure(error, new MyNetException("Network Access Exception", throwable));
            return;
        }
    }

    private void notifyFailure(int error, MyNetException e) {
        if (dispatcherCallback != null) {
            dispatcherCallback.onFailure(this, error, e);
        }
    }

    private void notifySuccess(Response response) {
        if (dispatcherCallback != null) {
            dispatcherCallback.onSuccess(this, response);
        }
    }

    private void notifyCancel() {
        if (dispatcherCallback != null) {
            dispatcherCallback.onCancel(this);
        }
    }

    Request getRequest() {
        return request;
    }

    Callback getCallback() {
        return callback;
    }
}
