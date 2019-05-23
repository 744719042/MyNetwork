package com.example.network;

public interface Poster {
    enum Type {
        MAINTHREAD, BACKGROUND
    }
    void post(Runnable task);
}
