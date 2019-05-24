package com.example.network;

import android.net.Uri;

public class HttpUrl {
    private String schema;
    private String host;
    private int port;
    private String path;
    private String query;
    private String url;

    private HttpUrl(Builder builder) {
        this.schema = builder.schema;
        this.host = builder.host;
        this.port = builder.port;
        this.path = builder.path;
        this.query = builder.query;

        Uri uri = new Uri.Builder().scheme(schema).authority(host + ":" + port).path(path).query(query).build();
        url = uri.toString();
    }

    public HttpUrl(Uri uri) {
        this.schema = uri.getScheme();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.path = uri.getPath();
        this.query = uri.getQuery();
    }

    public String getUrl() {
        return url;
    }

    public String getSchema() {
        return schema;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public static class Builder {
        private String schema;
        private String host;
        private int port;
        private String path;
        private String query;

        public Builder() {

        }

        public Builder schema(HttpProtocol protocol) {
            this.schema = protocol == HttpProtocol.HTTP ? "http" : "https";
            this.port = protocol == HttpProtocol.HTTP ? 80 : 443;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public HttpUrl build() {
            return new HttpUrl(this);
        }
    }
}
