package com.example.network;

import android.net.Uri;

public class HttpUrl {
    private String url;
    private String schema;
    private String host;
    private int port;
    private String path;
    private String query;
    private Uri uri;

    public HttpUrl(String url) {
        this.url = url;
        this.uri = Uri.parse(url);
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.schema = uri.getScheme();
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

    public String getQueryParameter(String name) {
        return uri.getQueryParameter(name);
    }
}
