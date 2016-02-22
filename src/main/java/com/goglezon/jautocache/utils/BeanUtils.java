package com.goglezon.jautocache.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuxianjun on 2016/1/28.
 */
public class BeanUtils {

    /**
     * 记录各个类的clone的method
     */
    private static ConcurrentHashMap<Class, Method> cloneMethodMap = new ConcurrentHashMap<Class, Method>(10);

    //记录没有clone方法的类
    private static ConcurrentHashMap<Class, Object> noCloneMethodMap = new ConcurrentHashMap<Class, Object>(10);

    private static final Object PRESENT = new Object();

    /**
     * 对象克隆,有两种实现方式
     * <p/>
     * 1、clone方法：对象需要继承Cloneable接口并实现clone方法，否则不走该方式；
     * 2、byte克隆：对象需要继承Serializable，将通过byte方式序列化；
     * 3、无法克隆，返回源对象
     *
     * @param src
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T clone(T src) {

        if ((src instanceof Cloneable) && getCloneMethod(src.getClass()) != null) {
            return methodClone(src);
        }

        return jsonClone(src);
    }

    /**
     * 通过clone方法克隆
     *
     * @param src
     * @param <T>
     * @return
     */
    public static <T> T methodClone(T src) {
        try {
            Method method = getCloneMethod(src.getClass());
            return (T) method.invoke(src, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return src;
    }

    private static Method getCloneMethod(Class clazz) {
        Method method = cloneMethodMap.get(clazz);
        if (method == null && !noCloneMethodMap.contains(clazz)) {
            try {
                method = clazz.getMethod("clone", null);
                cloneMethodMap.put(clazz, method);
                if (!Modifier.isPublic(method.getModifiers())) {
                    method = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                noCloneMethodMap.put(clazz, PRESENT);
            }
        }
        return method;
    }


    /**
     * 通过序列化克隆
     *
     * @param src
     * @param <T>
     * @return
     * @throws RuntimeException
     */
    public static <T> T byteClone(T src) throws RuntimeException {
        if (!(src instanceof Serializable)) {
            return src;
        }
        ByteArrayOutputStream memoryBuffer = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        T dist = null;
        try {
            out = new ObjectOutputStream(memoryBuffer);
            out.writeObject(src);
            out.flush();
            in = new ObjectInputStream(new ByteArrayInputStream(memoryBuffer.toByteArray()));
            dist = (T) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return src;
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        return dist;
    }

    public static <T> T jsonClone(Object src) {
        String json = JSON.toJSONString(src, SerializerFeature.WriteClassName);
        return (T) JSON.parseObject(json, src.getClass());
    }


}
