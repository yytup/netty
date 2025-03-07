package com.jzlx.socket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedDataQueue {
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public static void add(String data) throws InterruptedException {
        queue.put(data);
    }

    public static String take() throws InterruptedException {
        return queue.take();
    }

    public static void offer(String data) {
        queue.offer(data);
    }
}
