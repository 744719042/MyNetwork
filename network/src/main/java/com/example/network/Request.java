package com.example.network;

public abstract class Request {
    private Headers mHeaders;
    private RequestBody mBody;

    private HttpProtocol mProtocol = HttpProtocol.HTTP;
    private Version mProtocolVersion = Version.HTTP_2;
    private String mUrl;
    private RequestMethod mMethod;
    private String mRedirectUrl;

    private volatile boolean mCancel = false;
    private int mRetryCount = 0;

    public Request(String url, RequestMethod method) {
        this.mUrl = url;
        this.mMethod = method;
    }

    public Headers getHeaders() {
        return mHeaders;
    }

    public void setHeaders(Headers headers) {
        this.mHeaders = headers;
    }

    public RequestBody getBody() {
        return mBody;
    }

    public void setBody(RequestBody mBody) {
        this.mBody = mBody;
    }

    public HttpProtocol getProtocol() {
        return mProtocol;
    }

    public void setProtocol(HttpProtocol protocol) {
        this.mProtocol = protocol;
    }

    public Version getProtocolVersion() {
        return mProtocolVersion;
    }

    public void setProtocolVersion(Version protocolVersion) {
        this.mProtocolVersion = protocolVersion;
    }

    public String getUrl() {
        return mUrl;
    }

    public RequestMethod getMethod() {
        return mMethod;
    }

    public boolean isCancel() {
        return mCancel;
    }

    public void cancel() {
        this.mCancel = true;
    }

    public int getRetryCount() {
        return mRetryCount;
    }

    public void setRetryCount(int retryCount) {
        this.mRetryCount = retryCount;
    }

    public String getRedirectUrl() {
        return mRedirectUrl;
    }

    public void setRedirectUrl(String mRedirectUrl) {
        this.mRedirectUrl = mRedirectUrl;
    }
}
