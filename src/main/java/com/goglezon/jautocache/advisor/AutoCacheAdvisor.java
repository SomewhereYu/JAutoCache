package com.goglezon.jautocache.advisor;

import com.goglezon.jautocache.annotation.JAutoCache;
import com.goglezon.jautocache.annotation.JClearCache;
import com.goglezon.jautocache.annotation.JUpdateCache;
import com.goglezon.jautocache.annotation.JUseCache;
import com.goglezon.jautocache.exception.NullCacheException;
import com.goglezon.jautocache.model.AutoCacheModel;
import com.goglezon.jautocache.provider.AutoCacheProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * Created by Yuwenqi on 2015/10/9.
 */

public class AutoCacheAdvisor implements MethodInterceptor, InitializingBean {
    static Logger logger = LoggerFactory.getLogger(AutoCacheAdvisor.class);

    private AutoCacheProvider autoCacheProvider;

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Object result = null;
        logger.info(methodInvocation.getMethod().getDeclaringClass().getName());
        JAutoCache jAutoCache = methodInvocation.getMethod().getDeclaringClass().getAnnotation(JAutoCache.class);
        if (jAutoCache == null) {
            return methodInvocation.proceed();
        }
        JUseCache jUseCache = methodInvocation.getMethod().getAnnotation(JUseCache.class);
        JClearCache jClearCache = methodInvocation.getMethod().getAnnotation(JClearCache.class);
        JUpdateCache jUpdateCache = methodInvocation.getMethod().getAnnotation(JUpdateCache.class);


        boolean hasUpdateAnnotation = jUpdateCache != null && jUpdateCache.updateCache();
        boolean hasUseAnnotation = jUseCache != null && jUseCache.useCache();
        boolean hasClearAnnotation = jClearCache != null && jClearCache.clearCache();

        int totalAnnotation = hasUpdateAnnotation ? 1 : 0;
        totalAnnotation += hasUseAnnotation ? 1 : 0;
        totalAnnotation += hasClearAnnotation ? 1 : 0;

        //check annotations
        if (totalAnnotation == 0) {
            return methodInvocation.proceed();
        }

        if (totalAnnotation > 1) {
            throw new Exception("The annotations [JUseCache,JUpdateCache,JClearCache] can't be used simultaneously!");
        }

        Object[] args = methodInvocation.getArguments();
        String cacheKey;

        String unitedArgsKey;
        try {
            //不符合组合Key的规则，即方法的参数不符合组件规则时，不使用缓存。
            unitedArgsKey = getArgsUnitedKey(args);
        } catch (Exception e) {
            logger.error("The argument type are not permitted. Cache disabled.\n" + e.getMessage());
            return methodInvocation.proceed();
        }
        String classSimpleName = methodInvocation.getThis().getClass().getSimpleName();
        String methodName = methodInvocation.getMethod().getName();
        //组合成缓存里的完整的Key
        if (!jAutoCache.keyPrefix().equals("")) {
            cacheKey = jAutoCache.keyPrefix() + "_" + methodName + "(" + unitedArgsKey + ")";
        } else {
            //用调用者的包名类名方法名及参数作为key
            String className = methodInvocation.getMethod().getDeclaringClass().getName();
            cacheKey = className + "." + methodName + "(" + unitedArgsKey + ")";
        }
        int keepAlive=0;
        if (hasUseAnnotation) {
            keepAlive = jUseCache.keepAlive() > 0 ? jUseCache.keepAlive() : jAutoCache.keepAlive();
            try {
                result = autoCacheProvider.get(cacheKey);
                logger.info("[Get from cache]=>key:" + cacheKey);
                logger.info("[Get from cache]=>value:" + result);
            } catch (NullCacheException e) {
                //缓存对象中的data值是null时，autoCacheProvider.get会抛出异常，此处捕获异常，返回null.
                //比如查询一个记录，不存在，数据库没有抛异常，所以接口返回null是合理的。
                //下一次查询该对象时，就直接返回null，而不用再次查询数据库。
                logger.warn("AutoCacheProvider.get throws RuntimeException.\n" + e.getMessage());
                return result;
            }

            if(result!=null) {
                return result;
            }
        }

        try {
            result = methodInvocation.proceed();
        }catch (Exception e){
            logger.error("methodInvocation.proceed throws Exception.", e);
            return autoCacheProvider.onException(cacheKey,keepAlive,e);

        }
        //////////////////////////////////
        //set Cache
        if (hasUseAnnotation) {
            try {
                autoCacheProvider.set(cacheKey, result, keepAlive);
            } catch (Exception e) {
                logger.error("AutoCacheProvider.set throws RuntimeException.", e);
            }
        }

        //update Cache
        if (hasUpdateAnnotation) {
            //如果返回值不是AutoCacheBaseModel的子类实例
            if (!(result instanceof AutoCacheModel)) {
                throw new Exception("You must return an instance of AutoCacheModel when you use \"@JUpdateCache\"");
            }

            try {
                int keepAliveUpdate = jUpdateCache.keepAlive() > 0 ? jUpdateCache.keepAlive() : jAutoCache.keepAlive();
                autoCacheProvider.set(cacheKey, result, keepAliveUpdate);
            } catch (Exception e) {
                logger.warn("AutoCacheProvider.set while using JUpdateCache throws RuntimeException.\n" + e.getMessage());
            }
        }

        //clear Cache
        if (hasClearAnnotation) {
            try {
                autoCacheProvider.clear(cacheKey);
            } catch (Exception e) {
                logger.warn("AutoCacheProvider.clear throws RuntimeException.\n" + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 获得被拦截到的方法的所有参数组合成的KEY
     *
     * @param args
     * @return
     */
    private String getArgsUnitedKey(Object[] args) {
        String key = "";
        if (args.length == 0) return "";
        for (Object arg : args) {
            key = key + "_" + getParameterKeySeg(arg);
        }
        key = StringUtils.trimTrailingCharacter(key, '_');
        return StringUtils.trimLeadingCharacter(key, '_');
    }

    /**
     * 获得单个参数的Segment，其为整个缓存Key的一部分
     *
     * @param param
     * @return
     */
    private String getParameterKeySeg(Object param) {

        String keySegment = "";
        logger.info("[Parameter Type]:" + param.getClass().toString());
        if (param.getClass().isPrimitive() || LegalArgTypes.legal(param.getClass())) {
            keySegment += param;
        } else if (param.getClass().isArray()) {//可变参数
            Object[] array = (Object[]) param;
            for (Object item : array) {
                keySegment += getParameterKeySeg(item);
            }
        } else if (param instanceof AutoCacheModel) {
            keySegment += ((AutoCacheModel) param).keyGen();
        } else {
            throw new RuntimeException("The argument must be an instance of AutoCacheModel");
        }
        return keySegment;
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
