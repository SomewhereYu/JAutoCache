package com.goglezon.jautocache.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * Created by yuwenqi@goglezon.com on 2015/11/19 10:56.
 */
@Repository
public class RedisCacheProvider implements AutoCacheProvider {
    private static Logger logger = LoggerFactory.getLogger(RedisCacheProvider.class);
    @Resource
//    private ShardedXCommands jAutoCacheRedisClient;

    public Object getRawObject(String key) {
//        Object ret;
//        byte[] bytes;
//        bytes = jAutoCacheRedisClient.get(key.getBytes());
//        if (bytes == null) {
//            return null;
//        }
//        try {
//            ret = ByteArrayUtil.fromBytes(bytes);
//            return ((RedisCacheObject) ret).getData();
//        } catch (StreamCorruptedException streamCorruptedException) {
//            logger.warn("StreamCorruptedException occured.");
//        } catch (IOException e) {
//            logger.warn("IOException occured while fromBytes.");
//        } catch (ClassNotFoundException e) {
//            logger.warn("ClassNotFoundException occured.");
//        }


        return null;
    }


    public void setRawObject(String key, Object obj, int keepAlive) {
//        if (obj == null) return;
//        RedisCacheObject redisCacheObject = new RedisCacheObject();
//        redisCacheObject.setData(obj);
//
//        try {
//            jAutoCacheRedisClient.set(key.getBytes(), ByteArrayUtil.toBytes(redisCacheObject));
//        } catch (IOException e) {
//            logger.warn("IOException occured while toBytes.");
//        }
//
//        jAutoCacheRedisClient.expire(key, keepAlive);
        logger.error("[SET]-> keepAlive:" + keepAlive + " ,object: " + obj.toString());
    }

    public void clearRawObject(String key) {
//        jAutoCacheRedisClient.del(key);
    }

    public Object onException(String key,int keepAlive,Exception e){
        return null;
    }
}
