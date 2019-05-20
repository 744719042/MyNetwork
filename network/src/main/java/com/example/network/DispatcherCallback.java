package com.example.network;

public interface DispatcherCallback {
    void onSuccess(Request request, Response response);
    void onFailure(Request request, int error, MyNetException exception);
    void onCancel(Request request);
}
