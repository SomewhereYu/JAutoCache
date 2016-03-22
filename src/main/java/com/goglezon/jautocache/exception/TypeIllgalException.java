package com.goglezon.jautocache.exception;

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
