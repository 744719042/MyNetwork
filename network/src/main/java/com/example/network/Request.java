package com.example.network;

import android.net.Uri;

public class Request {
    private Headers header;
    private RequestBody body;
    private Version protocolVersion;
    private HttpUrl url;
    private RequestMethod method;
    private HttpUrl redirectUrl;
    private Poster.Type postType;

    private volatile boolean cancel = false;
    private int retryCount = 0;

    private Request(Builder builder) {
        header = builder.header;
        body = builder.body;
        protocolVersion = builder.protocolVersion;
        url = builder.url;
        method = builder.method;
        postType = builder.postType;
        retryCount = builder.retryCount;
    }

    public Headers getHeaders() {
        return header;
    }

    public RequestBody getBody() {
        return body;
    }

    public Version getProtocolVersion() {
        return protocolVersion;
    }

    public HttpUrl getUrl() {
        return url;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public boolean isCancel() {
        return cancel;
    }

    public void cancel() {
        this.cancel = true;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public HttpUrl getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(Uri uri) {
        this.redirectUrl = new HttpUrl(uri);
    }

    public Poster.Type getPosterType() {
        return postType;
    }

    public static class Builder {
        private Headers header = Headers.of();
        private RequestBody body;
        private Version protocolVersion = Version.HTTP_1_1;
        private HttpUrl url;
        private RequestMethod method;
        private Poster.Type postType = Poster.Type.MAINTHREAD;
        private int retryCount = 0;

        public Builder() {

        }

        public Builder header(Headers headers) {
            this.header = headers;
            return this;
        }

        public Builder url(HttpUrl url) {
            this.url = url;
            return this;
        }

        public Builder protocolVersion(Version version) {
            this.protocolVersion = version;
            return this;
        }

        public Builder postType(Poster.Type postType) {
            this.postType= postType;
            return this;
        }

        public Builder retryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Request get() {
            this.method = RequestMethod.GET;
            return new Request(this);
        }

        public Request head() {
            this.method = RequestMethod.HEADER;
            return new Request(this);
        }

        public Request post(RequestBody requestBody) {
            this.method = RequestMethod.POST;
            this.body = requestBody;
            return new Request(this);
        }
    }
}
