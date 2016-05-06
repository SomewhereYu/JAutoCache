package com.goglezon.jautocache.advisor;

import com.goglezon.jautocache.annotation.JAutoCache;
import com.goglezon.jautocache.annotation.JClearCache;
import com.goglezon.jautocache.annotation.JUseCache;
import com.goglezon.jautocache.common.ArgumentParser;
import com.goglezon.jautocache.exception.NullCacheException;
import com.goglezon.jautocache.exception.OpCacheException;
import com.goglezon.jautocache.provider.AutoCacheProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;


/**
 * Created by Yuwenqi on 2015/10/9.
 */

public class AutoCacheAdvisor implements MethodInterceptor, InitializingBean {
    final static Logger logger = LoggerFactory.getLogger(AutoCacheAdvisor.class);

    private AutoCacheProvider autoCacheProvider;

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object result = null;
        //注解相关校验
        logger.debug(methodInvocation.getMethod().getDeclaringClass().getName());
        JAutoCache jAutoCache = methodInvocation.getMethod().getDeclaringClass().getAnnotation(JAutoCache.class);
        if (jAutoCache == null) {
            return methodInvocation.proceed();
        }

        JUseCache jUseCache = methodInvocation.getMethod().getAnnotation(JUseCache.class);
        JClearCache jClearCache = methodInvocation.getMethod().getAnnotation(JClearCache.class);

        boolean hasUseAnnotation = jUseCache != null && jUseCache.useCache();
        boolean hasClearAnnotation = jClearCache != null && jClearCache.clearCache();

        if (!(hasUseAnnotation || hasClearAnnotation)){
            return methodInvocation.proceed();
        }

        //参数相关处理
        String unitedArgsKey;
        try {
            unitedArgsKey = new ArgumentParser(methodInvocation.getArguments()).parse();
        } catch (Exception e) {
            logger.error("The argument type are not permitted. Cache disabled.\n" + e.getMessage());
            return methodInvocation.proceed();
        }
        String methodName = methodInvocation.getMethod().getName();
        String cacheKey="." + methodName + "(" + unitedArgsKey + ")";
        if (!jAutoCache.keyPrefix().equals("")) {
            cacheKey = jAutoCache.keyPrefix() + cacheKey;
        } else {
            cacheKey = methodInvocation.getMethod().getDeclaringClass().getName() + cacheKey;
        }

        //处理缓存
        if (hasUseAnnotation) {
            //缓存存活时间
            int keepAlive = jUseCache.keepAlive() > 0 ? jUseCache.keepAlive() : jAutoCache.keepAlive();
            result=handleUseCacheAnnotation(methodInvocation,cacheKey, keepAlive);
        } else if (hasClearAnnotation) {
            result=handleClearCacheAnnotation(methodInvocation,cacheKey);
        }
        return result;
    }
    /**
     * 处理缓存注解
     *
     * @param cacheKey
     * @param keepAlive
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    private Object handleUseCacheAnnotation(MethodInvocation methodInvocation,String cacheKey, Integer keepAlive) throws Throwable {
        Object result = null;
        try {
            result = autoCacheProvider.getRawObject(cacheKey);
            logger.debug("[Get from cache]=>key:" + cacheKey);
            logger.debug("[Get from cache]=>value:" + result);
            if (result != null) {
                return result;
            }
            result = methodInvocation.proceed();
            autoCacheProvider.setRawObject(cacheKey, result, keepAlive);
        } catch (NullCacheException nullCacheException) {
            logger.warn("AutoCacheProvider.get throws NullCacheException.\n" + nullCacheException.getMessage());
        } catch (OpCacheException opCacheException) {
            logger.error("AutoCacheProvider.set throws OpCacheException.", opCacheException);
        } catch (Exception e) {
            logger.error("methodInvocation.proceed throws Exception.", e);
            result = autoCacheProvider.onException(cacheKey, keepAlive, e);
        }
        return result;
    }

    /**
     * 处理清楚缓存注解
     *
     * @param cacheKey
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    private Object handleClearCacheAnnotation(MethodInvocation methodInvocation,String cacheKey) throws Throwable {
        Object result = null;
        try {
            result = methodInvocation.proceed();
            autoCacheProvider.clearRawObject(cacheKey);
        } catch (OpCacheException opCacheException) {
            logger.warn("AutoCacheProvider.clear throws RuntimeException.\n" + opCacheException.getMessage());
        } catch (Exception e) {
            logger.error("methodInvocation.proceed throws Exception.", e);
        }
        return result;
    }

    public void afterPropertiesSet() throws Exception {
    }

    public AutoCacheProvider getAutoCacheProvider() {
        return autoCacheProvider;
    }

    public void setAutoCacheProvider(AutoCacheProvider autoCacheProvider) {
        this.autoCacheProvider = autoCacheProvider;
    }
}
