package com.goglezon.autocache.annotation;

import java.lang.annotation.*;

/**
 * Created by yuwenqi@jd.com on 2015/11/10.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

public @interface JUseCache {
    boolean useCache() default true;
    int expiredTimeSec() default 300;
}