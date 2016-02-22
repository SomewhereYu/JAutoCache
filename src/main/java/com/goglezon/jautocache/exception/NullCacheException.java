package com.goglezon.jautocache.exception;

/**
 * 表示缓存存放为空
 * Created by xuxianjun on 2016/2/3.
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
