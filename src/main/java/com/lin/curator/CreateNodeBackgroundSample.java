package com.lin.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNodeBackgroundSample {
    static String path = "/zk-book";

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.31.13:2181")
            .sessionTimeoutMs(50000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    static CountDownLatch semaphore = new CountDownLatch(2);
    static ExecutorService tp = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {
        client.start();
        System.out.println("main thread: " + Thread.currentThread().getName());
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println("event[code: " + curatorEvent.getResultCode() + ", type: " + curatorEvent.getType() + "]");
                    System.out.println("Thread of processResult: " + Thread.currentThread().getName());
                    semaphore.countDown();
                }, tp)
                .forPath(path, "init".getBytes());

        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .inBackground((curatorFramework, curatorEvent) -> {
                    System.out.println("event[code: " + curatorEvent.getResultCode() + ", type: " + curatorEvent.getType() + "]");
                    System.out.println("Thread of processResult: " + Thread.currentThread().getName());
                    semaphore.countDown();
                })
                .forPath(path, "init".getBytes());

        semaphore.await();
        tp.shutdown();
    }
}
