package com.antelope.clusterlock;

import com.antelope.clusterlock.autoconfigure.ClusterLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author yaml
 * @since 2021/6/9
 */
@RestController
public class TestController {

    @ClusterLock(value = "#id")
    @GetMapping("/redis/lock/test01")
    public void test01(String id) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("####################test01");
    }

    @ClusterLock(value = "#id", waitTime = 5000)
    @GetMapping("/redis/lock/test02")
    public void test02(String id) {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("####################test02");
    }

    @ClusterLock(value = "#id", waitTime = 10000)
    @GetMapping("/redis/lock/test03")
    public void test03(String id) {
        try {
            TimeUnit.SECONDS.sleep(9);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("####################test03");
    }
}
