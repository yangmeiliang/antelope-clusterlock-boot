package com.antelope.clusterlock.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @author yaml
 */
@Slf4j
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(ClusterLockProperties.class)
@ConditionalOnProperty(value = "cluster.lock.enable", havingValue = "true", matchIfMissing = true)
public class ClusterLockAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        log.info("redis锁自动装配...");
        return new RedisLockRegistry(redisConnectionFactory, "cluster-lock");
    }

    @Bean
    public ClusterLockAspect clusterLockAspect(RedisLockRegistry redisLockRegistry,
                                               ClusterLockProperties clusterLockProperties) {
        log.info("ClusterLockAspect自动装配");
        return new ClusterLockAspect(redisLockRegistry, clusterLockProperties);
    }

}
