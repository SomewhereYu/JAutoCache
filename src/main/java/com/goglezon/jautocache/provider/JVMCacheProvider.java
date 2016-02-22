package com.goglezon.jautocache.provider;

import com.goglezon.jautocache.exception.NullCacheException;
import com.goglezon.jautocache.utils.BeanUtils;
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

    {
        // new StoreCacheThread().start();
    }

    /**
     * Map里存的是jvmCacheObject
     *
     * @param key
     * @return
     */
    public Object get(String key) throws NullCacheException {
        JVMCacheObject jvmCacheObject = cacheMap.get(key);
        if (jvmCacheObject == null) {
            return null;
        } else if (jvmCacheObject.expired()) {
            return null;
        } else if (jvmCacheObject.getServiceUnavailableTimes() >= this.keepAliveTimesOnException) {
            return null;
        }

        //将null值存入缓存的概率比较小
        if (jvmCacheObject.getData() == null) {
            throw new NullCacheException("[JVMCacheProvider] -> Data in Cache is null. key:" + key);
        }

        return BeanUtils.clone(jvmCacheObject.getData());
    }

    /**
     * @param key
     * @param obj
     * @param keepAlive 为存活时间秒数
     */
    public void set(String key, Object obj, int keepAlive) {
        JVMCacheObject jvmCacheObject = new JVMCacheObject(key);
        jvmCacheObject.postpone(keepAlive);
        jvmCacheObject.setData(obj);
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
        if (jvmCacheObject == null) {
            throw e;
        }
        jvmCacheObject.onException(keepAlive);
        return get(key);
    }

    /**
     * @param s
     */
    public void clear(String s) {
        cacheMap.remove(s);
    }

    public void afterPropertiesSet() throws Exception {

        cacheMap = new LRULinkedHashMap<String, JVMCacheObject>(getMaxCapacity());
        //getLocalCache();
    }

    void getLocalCache() {
        FileInputStream fis;
        logger.warn("[JVMCacheProvider] -> Load jvmCache from file.");
        try {
            fis = new FileInputStream("jvmCache.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            cacheMap = (LRULinkedHashMap<String, JVMCacheObject>) ois.readObject();
            logger.warn("[StoreCacheThread] -> Store jvmCache to file.size=" + cacheMap.size());
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    /**
     * 停服务时保存内存数据到文件
     */
    private class StoreCacheThread extends Thread {

        public void run() {
            Thread.currentThread().setName("StoreCacheThread");
            FileOutputStream fis;
            while (true) {
                synchronized (Thread.currentThread()) {
                    try {
                        Thread.currentThread().wait(1 * 60 * 1000);
                        fis = new FileOutputStream("jvmCache.dat");
                        ObjectOutputStream oos = new ObjectOutputStream(fis);
                        oos.writeObject(cacheMap);
                        oos.close();
                        fis.close();
                        logger.warn("[StoreCacheThread] -> Store jvmCache to file.size=" + cacheMap.size());
                    } catch (Exception e) {
                        logger.warn(e.getMessage());
                    }
                }
            }
        }
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
