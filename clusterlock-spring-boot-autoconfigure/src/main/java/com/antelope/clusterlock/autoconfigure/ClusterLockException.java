package com.antelope.clusterlock.autoconfigure;

import lombok.Getter;
import lombok.Setter;

/**
 * @author yaml
 * @since 2021/6/8
 */
@Getter
@Setter
public class ClusterLockException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "操作过于频繁，请稍后再试";

    public ClusterLockException(String message) {
        super(message);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public static ClusterLockException create() {
        return create(DEFAULT_MESSAGE);
    }

    public static ClusterLockException create(String message) {
        return new ClusterLockException(message);
    }
}
