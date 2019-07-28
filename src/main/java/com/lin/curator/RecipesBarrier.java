package com.lin.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class RecipesBarrier {
    static String barrierPath = "/curator_recipes_barrier_path";
    static DistributedBarrier barrier;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    CuratorFramework client = CuratorFrameworkFactory.builder()
                            .connectString("192.168.31.13:2181")
                            .sessionTimeoutMs(50000)
                            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                            .build();
                    client.start();
                    barrier = new DistributedBarrier(client, barrierPath);
                    System.out.println(Thread.currentThread().getName() + "号barrier设置");
                    barrier.setBarrier();
                    barrier.waitOnBarrier();
                    System.out.println("启动...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(2000);
        barrier.removeBarrier();
    }
}
