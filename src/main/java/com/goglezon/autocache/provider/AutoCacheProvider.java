package com.goglezon.autocache.provider;

/**
 * Created by yuwenqi@jd.com on 2015/11/14 1:39.
 */
public interface AutoCacheProvider {
    Object get(String key);
    void set(String key,Object value,int expiredTimeSec);
    void clear(String key);
}
