package com.lin.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class RecipesBarrier2 {
    static String barrierPath = "/curator_recipes_barrier_path";

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
                    DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, barrierPath, 5);
                    Thread.sleep(Math.round(Math.random() * 3000));
                    System.out.println(Thread.currentThread().getName() + "号进入barrier");
                    barrier.enter();
                    System.out.println("启动...");
                    Thread.sleep(Math.round(Math.random() * 3000));
                    barrier.leave();
                    System.out.println("退出...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
