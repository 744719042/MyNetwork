package com.example.network.wrapper.core;

import com.example.network.MyNetException;

public interface DataCallback<T> {
    void onSuccess(T t);
    void onFailure(int code, MyNetException e);
}
