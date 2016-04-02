package com.goglezon.jautocache.provider;

import java.io.Serializable;

/**
 * Created by yuwenqi@goglezon.com on 2015/11/19 10:56.
 */
public class RedisCacheObject implements Serializable{
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RedisCacheObject{" +
                ", data=" + data +
                '}';
    }
}
