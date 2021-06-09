package com.antelope.clusterlock.autoconfigure;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yaml
 * @since 2021/6/8
 */
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "cluster.lock")
public class ClusterLockProperties {
    /**
     * 分布式系统 key分组前缀
     */
    private String prefixLockKey = "";
}
