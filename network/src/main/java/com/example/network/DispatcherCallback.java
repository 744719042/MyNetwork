package com.example.network;

public interface DispatcherCallback {
    void onSuccess(NetWorkTask task, Response response);
    void onFailure(NetWorkTask task, int error, MyNetException exception);
    void onCancel(NetWorkTask task);
}
