package com.example.network;

import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

public class NetWorkTask implements Runnable {
    private Request request;
    private DispatcherCallback callback;

    public NetWorkTask(Request request, DispatcherCallback callback) {
        this.request = request;
        this.callback = callback;
    }

    @Override
    public void run() {
        if (request.isCancel()) {
            notifyCancel();
            return;
        }

        int retryCount = request.getRetryCount();
        String url = request.getUrl();
        RequestMethod method = request.getMethod();
        RequestBody requestBody = request.getBody();
        Headers headers = request.getHeaders();

        int error = -1;
        Throwable throwable = null;
        while (true) {
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
                httpURLConnection.setRequestMethod(method.name());
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setReadTimeout(3000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(requestBody != null);
                httpURLConnection.setInstanceFollowRedirects(false);
                for (Map.Entry<String, String> entry : headers.getValues().entrySet()) {
                    httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
                if (requestBody != null) {
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    outputStream.write(requestBody.body().getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                }

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
                        request.setRedirectUrl(url);
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
        if (callback != null) {
            callback.onFailure(request, error, e);
        }
    }

    private void notifySuccess(Response response) {
        if (callback != null) {
            callback.onSuccess(request, response);
        }
    }

    private void notifyCancel() {
        if (callback != null) {
            callback.onCancel(request);
        }
    }
}
