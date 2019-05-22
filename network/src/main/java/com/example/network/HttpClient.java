package com.example.network;

public class HttpClient {
    public void execute(Request request, Callback callback) {
        NetWorkTask workTask = new NetWorkTask(request, callback, Dispatcher.getInstance());
        Dispatcher.getInstance().enqueue(workTask);
    }
}
