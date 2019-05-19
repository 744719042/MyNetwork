package com.example.network;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Dispatcher {
    private BlockingQueue<Request> readyQueue = new LinkedBlockingDeque<>();
    private BlockingQueue<Request> runningQueue = new LinkedBlockingDeque<>();

    
}
