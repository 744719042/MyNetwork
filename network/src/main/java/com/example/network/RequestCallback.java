package com.example.network;

public interface RequestCallback<T> {
    void onSuccess(T data);
    void onFailure(int code, Throwable e);
}
