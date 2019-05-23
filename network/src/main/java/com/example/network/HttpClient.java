package com.example.network;

public class HttpClient {
    private Dispatcher dispatcher;

    private HttpClient() {
        dispatcher = new Dispatcher();
    }

    public void execute(Request request, Callback callback) {
        NetWorkTask workTask = new NetWorkTask(request, callback, dispatcher);
        dispatcher.enqueue(workTask);
    }
}
