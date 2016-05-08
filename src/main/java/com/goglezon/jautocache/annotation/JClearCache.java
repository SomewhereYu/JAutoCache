package com.goglezon.jautocache.annotation;

import java.lang.annotation.*;

/**
 * Created by yuwenqi@goglezon.com on 2015/11/10.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface JClearCache {
    boolean clearCache() default true;
    String what() default "";
}