package com.example.network;

public abstract class Request<T> {
    private Headers mHeaders;
    private RequestBody mBody;

    private HttpProtocol mProtocol = HttpProtocol.HTTP;
    private Version mProtocolVersion = Version.HTTP_2;
    private String mUrl;
    private RequestMethod mMethod;

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

    public abstract void execute(RequestCallback<T> callback);
}
