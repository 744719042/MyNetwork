package com.example.network;

public class Response {
    private Headers mHeaders;
    private ResponseBody mResponseBody;
    private int mStatus;
    private String mMessage;

    public Headers getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Headers mHeaders) {
        this.mHeaders = mHeaders;
    }

    public ResponseBody getResponseBody() {
        return mResponseBody;
    }

    public void setResponseBody(ResponseBody mResponseBody) {
        this.mResponseBody = mResponseBody;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
