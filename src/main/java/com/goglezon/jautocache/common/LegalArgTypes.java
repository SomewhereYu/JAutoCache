package com.goglezon.jautocache.common;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yuwenqi@goglezon.com on 2015/11/20 23:53.
 */
public class LegalArgTypes {
    private static final Set<Class> basin = new HashSet<Class>();

    static {
        basin.add(Integer.class);
        basin.add(Long.class);
        basin.add(String.class);
        basin.add(Double.class);
        basin.add(Float.class);
        basin.add(Character.class);
        basin.add(Number.class);
        basin.add(Boolean.class);
    }

    public static boolean legal(Class clz) {
        if (basin.contains(clz)) {
            return true;
        }
        return false;
    }

    /**
     * 返回一盆允许的基本类型
     *
     * @return
     */
    public static Set getBasin() {
        return basin;
    }
}
