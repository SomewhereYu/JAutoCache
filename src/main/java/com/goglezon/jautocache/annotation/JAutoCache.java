package com.goglezon.jautocache.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yuwenqi@jd.com on 2015/11/10.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JAutoCache {
    int keepAlive() default 300;
    String keyPrefix() default "";
}