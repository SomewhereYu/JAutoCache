package com.goglezon.jautocache.provider;

import com.goglezon.jautocache.exception.NullCacheException;
import com.goglezon.jautocache.exception.OpCacheException;

/**
 * Created by yuwenqi@goglezon.com on 2015/11/14 1:39.
 */

public interface AutoCacheProvider {
    Object getRawObject(String key) throws NullCacheException;
    void setRawObject(String key,Object value,int expiredTimeSec) throws OpCacheException;
    void clearRawObject(String key) throws OpCacheException;
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
