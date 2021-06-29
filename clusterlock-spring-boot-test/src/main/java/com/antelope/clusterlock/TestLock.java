package com.antelope.clusterlock;

import com.antelope.clusterlock.autoconfigure.ClusterLock;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author yaml
 * @since 2021/6/29
 */
@Component
public class TestLock {

    @ClusterLock(value = "#id")
    public void test01(String id) {
        long start = System.currentTimeMillis();
        try {
            TimeUnit.SECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("####################test01, 耗时：" + (System.currentTimeMillis() - start) / 1000);
    }

    @ClusterLock(value = "#id", waitTime = 5000)
    public void test02(String id) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("####################test02");
    }

    @ClusterLock(value = "#id", waitTime = 10000)
    public void test03(String id) {
        try {
            TimeUnit.SECONDS.sleep(9);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("####################test03");
    }
}
