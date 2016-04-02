package com.goglezon.jautocache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yuwenqi@goglezon.com on 2015/11/10.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface JUseCache {
    boolean useCache() default true;
    int keepAlive() default 0;

}