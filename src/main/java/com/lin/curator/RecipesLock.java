package com.lin.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class RecipesLock {
    static String masterPath = "/curator_recipes_lock_path";

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("192.168.31.13:2181")
            .sessionTimeoutMs(50000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();

    public static void main(String[] args) throws InterruptedException {
        client.start();
        final InterProcessLock lock = new InterProcessMutex(client, masterPath);
        final CountDownLatch downLatch = new CountDownLatch(1);
        for (int i = 0; i < 30; i++) {
            new Thread(() -> {
                try {
                    downLatch.await();;
                    lock.acquire();
                } catch (Exception e) {}
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                String orderNo = sdf.format(new Date());
                System.out.println("生成订单号是：" + orderNo);
                try {
                    lock.release();
                } catch (Exception e) {}
            }).start();
        }
        downLatch.countDown();
    }
}
