package com.antelope.clusterlock.autoconfigure;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁
 *
 * @author yaml
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClusterLock {

    /**
     * 拼接redis key，el表达式
     */
    String value() default "";

    /**
     * 如果为空则默认拼接参数
     */
    @AliasFor("value")
    String key() default "";

    /**
     * redis key前缀
     * 空或者空字符串则默认取方法名
     */
    String keyPrefix() default "";

    /**
     * 等待上一个锁的自旋时间 默认不等待 单位：毫秒
     */
    long waitTime() default 0;
}
