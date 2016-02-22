package com.goglezon.jautocache.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by yuwenqi@jd.com on 2015/11/19 10:56.
 */
public class JVMCacheObject implements Serializable{
    Logger logger= LoggerFactory.getLogger(JVMCacheObject.class);

    private String key;
    //过期时间，非存活时间
    private long expiredTime=0;
    private Object data;
    private int serviceUnavailableTimes=0;

    public JVMCacheObject(String key){
        this.key=key;
    }

    public Object getData(){
        return data;
    }

    public void setData(Object value) {
        this.data = value;
    }

    public boolean expired(){
        if(System.currentTimeMillis()/1000 >= this.expiredTime){
            logger.info("[Cache expired] :"+this.toString());
            return true;
        }
        return false;
    }
    //延期
    public void postpone(int keepAlive){
        this.expiredTime =System.currentTimeMillis()/1000+keepAlive;
    }

    public void onException(int keeAlive){
        this.serviceUnavailableTimes++;
        postpone(keeAlive);
    }

    public int getServiceUnavailableTimes() {
        return serviceUnavailableTimes;
    }

    public void setServiceUnavailableTimes(int serviceUnavailableTimes) {
        this.serviceUnavailableTimes = serviceUnavailableTimes;
    }

    @Override
    public String toString() {
        return "JVMCacheObject{" +
                "key='" + key + '\'' +
                ", expiredTime=" + expiredTime +
                ", data=" + data +
                ", serviceUnavailableTimes=" + serviceUnavailableTimes +
                '}';
    }
}
