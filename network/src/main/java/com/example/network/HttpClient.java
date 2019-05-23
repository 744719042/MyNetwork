package com.example.network;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public class HttpClient {
    private Dispatcher dispatcher;
    private Builder builder;

    private HttpClient(Builder builder) {
        this.builder = builder;
        this.dispatcher = new Dispatcher();
    }

    public void execute(Request request, Callback callback) {
        NetWorkTask workTask = new NetWorkTask(request, callback, dispatcher, builder.connectTimeout,
                builder.readTimeout, builder.hostnameVerifier, builder.sslSocketFactory);
        dispatcher.enqueue(workTask);
    }

    public static class Builder {
        private int connectTimeout;
        private int readTimeout;
        private HostnameVerifier hostnameVerifier;
        private SSLSocketFactory sslSocketFactory;

        public Builder() {

        }

        public Builder connectTimeout(int timeout) {
            this.connectTimeout = timeout;
            return this;
        }

        public Builder readTimeout(int timeout) {
            this.readTimeout = timeout;
            return this;
        }

        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }
        public Builder SSLSocketFactory(SSLSocketFactory sslSocketFactory) {
            this.sslSocketFactory = sslSocketFactory;
            return this;
        }

        public HttpClient build() {
            return new HttpClient(this);
        }
    }
}
