package com.example.network;

public interface Callback {
    void onSuccess(Response response);
    void onFailure(int code, MyNetException e);
}
