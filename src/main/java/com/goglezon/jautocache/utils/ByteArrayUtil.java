package com.goglezon.jautocache.utils;

import java.io.*;

/**
 * Created by yuwenqi@goglezon.com on 2015/11/30 13:56.
 */
public class ByteArrayUtil {
    public static Object fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Object obj = objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return obj;
    }

    public static byte[] toBytes(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return bytes;
    }
}
