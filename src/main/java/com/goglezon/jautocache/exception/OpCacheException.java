package com.goglezon.jautocache.exception;

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
