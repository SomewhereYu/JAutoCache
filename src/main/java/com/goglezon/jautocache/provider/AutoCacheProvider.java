package com.goglezon.jautocache.provider;

import com.goglezon.jautocache.exception.NullCacheException;

/**
 * Created by yuwenqi@jd.com on 2015/11/14 1:39.
 */

public interface AutoCacheProvider {
    Object get(String key) throws NullCacheException;
    void set(String key,Object value,int expiredTimeSec);
    void clear(String key);
    /**
     * 返回缓存的结果
     * 没有缓存，直接抛出异常
     *
     * @param key
     * @param e
     * @return
     * @throws Exception
     */
    Object onException(String key,int keepAlive,Exception e) throws Exception;
}
