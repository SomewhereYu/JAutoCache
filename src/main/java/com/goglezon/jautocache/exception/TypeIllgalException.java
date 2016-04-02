package com.goglezon.jautocache.exception;

/**
 * Created by yuwenqi@goglezon.com on 2016/3/18.
 */
public class TypeIllgalException extends Exception {

    public TypeIllgalException(){
        super();
    }

    public TypeIllgalException(String msg){
        super(msg);
    }

    public TypeIllgalException(String message, Throwable cause) {
        super(message, cause);
    }
}
