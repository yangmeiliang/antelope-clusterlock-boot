package com.antelope.clusterlock;

import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author yaml
 * @since 2021/6/29
 */
public class TestLockTest extends BaseTest{

    @Resource
    TestLock testLock;
    @Test
    public void test01() {
        testLock.test01("01");
        testLock.test02("02");
        testLock.test03("03");
    }

    @Test
    public void test02() {
    }

    @Test
    public void test03() {
    }
}