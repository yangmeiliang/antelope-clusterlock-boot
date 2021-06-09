package com.antelope.clusterlock.autoconfigure;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @author yaml
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class ClusterLockAspect implements Ordered, ApplicationContextAware {

    private final RedisLockRegistry redisLockRegistry;
    private final ClusterLockProperties clusterLockProperties;

    private static ApplicationContext applicationContext;
    private static BeanFactoryResolver beanFactoryResolver;
    private static SpelParserConfiguration config;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ClusterLockAspect.applicationContext = applicationContext;
        ClusterLockAspect.beanFactoryResolver = new BeanFactoryResolver(applicationContext);
        ClusterLockAspect.config = new SpelParserConfiguration(
                SpelCompilerMode.MIXED,
                this.getClass().getClassLoader(),
                true,
                true,
                100);
    }

    @Pointcut("@annotation(ClusterLock)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result;
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ClusterLock clusterLock = method.getAnnotation(ClusterLock.class);
        final String key = getLockKey(clusterLock, method, joinPoint.getArgs());
        Lock lock = redisLockRegistry.obtain(key);
        boolean locked = false;
        try {
            if (log.isDebugEnabled()) {
                log.debug("[cluster lock]{} lockKey={}", method.getName(), key);
            }
            locked = lock.tryLock(clusterLock.waitTime(), TimeUnit.MILLISECONDS);
            if (locked) {
                result = joinPoint.proceed();
            } else {
                throw ClusterLockException.create();
            }
        } finally {
            if (locked) {
                if (log.isDebugEnabled()) {
                    log.debug("[cluster unlock]{} lockKey={}", method.getName(), key);
                }
                lock.unlock();
            }
        }
        return result;
    }

    private String getLockKey(ClusterLock lock, Method method, Object[] args) {
        StringBuilder keyBuilder = new StringBuilder(clusterLockProperties.getPrefixLockKey());
        final String keyPrefix = lock.keyPrefix();
        if (StringUtils.isEmpty(keyPrefix)) {
            // 如果key为空，则默认使用方法名
            keyBuilder.append(method.getName());
        } else {
            keyBuilder.append(keyPrefix);
        }

        final String elValue = lock.value();
        if ("".equals(elValue)) {
            for (Object arg : args) {
                keyBuilder.append(":").append(arg);
            }
            return keyBuilder.toString();
        }
        String[] parameterNames = new DefaultParameterNameDiscoverer().getParameterNames(method);
        if (parameterNames == null || parameterNames.length < 1) {
            keyBuilder.append(":").append(elValue);
            return keyBuilder.toString();
        }

        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext(applicationContext);
        for (int i = 0; i < parameterNames.length; i++) {
            standardEvaluationContext.setVariable(parameterNames[i], args[i]);
        }
        standardEvaluationContext.setBeanResolver(beanFactoryResolver);
        try {
            SpelExpressionParser spelExpressionParser = new SpelExpressionParser(config);
            Expression expression = spelExpressionParser.parseExpression(elValue);
            String value = expression.getValue(standardEvaluationContext, String.class);
            keyBuilder.append(":").append(value);
        } catch (Exception e) {
            log.error("el表达式解析失败", e);
        }
        return keyBuilder.toString();
    }
}
