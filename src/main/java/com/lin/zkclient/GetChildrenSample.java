package com.lin.zkclient;

import org.I0Itec.zkclient.ZkClient;

public class GetChildrenSample {
    public static void main(String[] args) throws Exception {
        String path = "/zk-book";
        ZkClient zkClient = new ZkClient("192.168.31.13:2181", 5000);
        zkClient.subscribeChildChanges(path, (parentPath, currentChilds) -> {
            System.out.println(parentPath + " 's child changed, currentChilds:" + currentChilds);
        });

        zkClient.createPersistent(path);
        Thread.sleep(1000);
        System.out.println(zkClient.getChildren(path));
        Thread.sleep(1000);
        zkClient.createPersistent(path + "/c1");
        Thread.sleep(1000);
        zkClient.delete(path + "/c1");
        Thread.sleep(1000);
        zkClient.delete(path);
        Thread.sleep(1000);
    }
}
