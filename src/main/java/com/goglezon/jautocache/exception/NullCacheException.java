package com.goglezon.jautocache.exception;

/**
 * Created by yuwenqi@goglezon.com on 2016/3/18.
 */
public class NullCacheException extends Exception {

    public NullCacheException(){
        super();
    }

    public NullCacheException(String msg){
        super(msg);
    }

    public NullCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
