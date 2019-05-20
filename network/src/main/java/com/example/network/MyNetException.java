package com.example.network;

public class MyNetException extends Exception {

    public MyNetException(String message) {
        super(message);
    }

    public MyNetException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyNetException(Throwable cause) {
        super(cause);
    }
}
