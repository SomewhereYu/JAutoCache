package com.goglezon.jautocache.exception;

/**
 * Created by yuwenqi@goglezon.com on 2016/3/18.
 */
public class OpCacheException extends Exception {

    public OpCacheException(){
        super();
    }

    public OpCacheException(String msg){
        super(msg);
    }

    public OpCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
