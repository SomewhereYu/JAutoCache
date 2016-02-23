package com.goglezon.jautocache.manage;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by yuwenqi@jd.com on 2015/11/30 11:00.
 */
public class CacheRule implements Serializable {
    private Enum cacheType;
    private int keepAlive;
    private Class clazz;
    private Method method;
    private String cacheRuleKey;

    public String getCacheRuleKey() {
        return cacheRuleKey;
    }

    public void setCacheRuleKey(String cacheRuleKey) {
        this.cacheRuleKey = cacheRuleKey;
    }

    public Enum getCacheType() {
        return cacheType;
    }

    public void setCacheType(Enum cacheType) {
        this.cacheType = cacheType;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "CacheRule{" +
                "cacheType=" + cacheType +
                ", keepAlive=" + keepAlive +
                ", clazz=" + clazz +
                ", method=" + method +
                ", cacheRuleKey='" + cacheRuleKey + '\'' +
                '}';
    }
}

