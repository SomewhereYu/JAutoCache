package com.goglezon.jautocache.provider;

import com.goglezon.jautocache.exception.NullCacheException;
import com.goglezon.jautocache.utils.BeanUtils;
import com.goglezon.jautocache.utils.LRULinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Created by yuwenqi@jd.com on 2015/11/19 10:56.
 */

public class JVMCacheProvider implements AutoCacheProvider, InitializingBean {
    private static Logger logger = LoggerFactory.getLogger(JVMCacheProvider.class);
    private static Map<String, JVMCacheObject> cacheMap;
    private int maxCapacity = 100000;
    private int keepAliveTimesOnException = 10;

    /**
     * Map里存的是jvmCacheObject
     *
     * @param key
     * @return
     */
    public Object getRawObject(String key) throws NullCacheException {
        JVMCacheObject jvmCacheObject = cacheMap.get(key);
        if (jvmCacheObject == null || jvmCacheObject.expired()
                || jvmCacheObject.getServiceUnavailableTimes() > this.keepAliveTimesOnException) {
            return null;
        }

        //将null值存入缓存的概率比较小
        if (jvmCacheObject.getData() == null) {
            throw new NullCacheException("[JVMCacheProvider] -> Data in Cache is null. key:" + key);
        }

        return jvmCacheObject.getData()==null?null:BeanUtils.clone(jvmCacheObject.getData());
    }

    /**
     * @param key
     * @param obj
     * @param keepAlive 为存活时间秒数
     */
    public void setRawObject(String key, Object obj, int keepAlive) {
        JVMCacheObject jvmCacheObject = new JVMCacheObject(key);
        jvmCacheObject.delay(keepAlive);
        jvmCacheObject.setData(obj==null?null:BeanUtils.clone(obj));
        cacheMap.put(key, jvmCacheObject);
        logger.info("[JVMCacheProvider.SET]-> keepAlive:" + keepAlive + " ,jvmCacheObject:" + jvmCacheObject.toString());
    }

    /**
     * 返回缓存的结果
     * 没有缓存，直接抛出异常
     *
     * @param key
     * @param e
     * @return
     * @throws Exception
     */
    public Object onException(String key,int keepAlive, Exception e) throws Exception {
        JVMCacheObject jvmCacheObject = cacheMap.get(key);
        //缓存里没有该对象时，且真实接口抛异常时，将异常继续抛出
        if (jvmCacheObject == null
                || jvmCacheObject.getServiceUnavailableTimes() >= this.keepAliveTimesOnException) {
            throw e;
        }

        jvmCacheObject.onException(keepAlive);
        return getRawObject(key);
    }

    /**
     * @param s
     */
    public void clearRawObject(String s) {
        cacheMap.remove(s);
    }

    public void afterPropertiesSet() throws Exception {
        int maxCap=getMaxCapacity();
        cacheMap = new LRULinkedHashMap<String, JVMCacheObject>(maxCap);
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getKeepAliveTimesOnException() {
        return keepAliveTimesOnException;
    }

    public void setKeepAliveTimesOnException(int keepAliveTimesOnException) {
        this.keepAliveTimesOnException = keepAliveTimesOnException;
    }
}
