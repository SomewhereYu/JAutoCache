package com.goglezon.jautocache.manage;

import com.goglezon.jautocache.annotation.JAutoCache;
import com.goglezon.jautocache.annotation.JUseCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuwenqi@jd.com on 2015/11/30 13:27.
 */
public class CacheRuleManager {
    private static Logger logger = LoggerFactory.getLogger(CacheRuleManager.class);
    private  ConcurrentHashMap<String, CacheRule> ruleMap
            = new ConcurrentHashMap<String, CacheRule>();

    //实现类名->接口类名
    private  ConcurrentHashMap<Class, Class> classInferfaceMap
            = new ConcurrentHashMap<Class, Class>();
    public CacheRuleManager(){

    }

    /**
     * 支持将注解标记于实现类或者接口
     * @param packageName
     */
    public synchronized void init(String packageName) {
        Set<Class<?>> set = ClassVan.getClasses(packageName);
        for (Class clazz : set) {
            Class[] iFaces = clazz.getInterfaces();
            if (iFaces != null) {
                for (Class iFace : iFaces) {
                    if (iFace.getName().startsWith(packageName)) {
                        //接口的所有方法加入map，用于在展示列表时取实现类与接口的映射关系。（合并展示为一条）
                        classInferfaceMap.put(clazz, iFace);
                    }
                }
            }
            //实现类和接口中的所有方法都加入到ruleMap中
            classMethodToRuleMap(clazz);
        }
        persist();
        logger.warn("CacheRuleMap loaded.");
        //start Worker runs per five minutes.
        //new CacheRuleWorker(ruleMap).start();
    }

    /**
     * 将注解JAutoCache的类or接口的所有方法都加入到rulemap中，用于将没有启用cache的方法启用cache。
     * @param clazz
     * @return
     */
    private  void classMethodToRuleMap(Class clazz) {
        JAutoCache jAutoCache = (JAutoCache) clazz.getAnnotation(JAutoCache.class);
        if (jAutoCache != null) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                CacheRule cacheRule = toCacheObjectConfig(jAutoCache, method);
                ruleMap.put(cacheRule.getCacheRuleKey(), cacheRule);
            }
        }
    }

    /**
     * 组成配置的基本对象
     *
     * @param method
     * @return
     */
    private static CacheRule toCacheObjectConfig(JAutoCache jAutoCache, Method method) {

        String cacheKey = genCacheManageKey(method);

        CacheRule cacheRule = new CacheRule();
        cacheRule.setMethod(method);
        cacheRule.setClazz(method.getDeclaringClass());
        cacheRule.setCacheRuleKey(cacheKey);

        JUseCache jUseCache = method.getAnnotation(JUseCache.class);
        if (jUseCache == null || jUseCache.useCache() == false) {
            cacheRule.setKeepAlive(0);
            cacheRule.setCacheType(CacheType.NoUse);
        } else {
            int keepAlive = jUseCache.keepAlive() == 0 ? jAutoCache.keepAlive() : jUseCache.keepAlive();
            cacheRule.setKeepAlive(keepAlive);
            cacheRule.setCacheType(CacheType.Use);
        }
        return cacheRule;
    }

    /**
     * 统一缓存管理的key的规则
     * @param method
     * @return
     */
    public static String genCacheManageKey(Class clazz,Method method){
        String className=clazz.getName();
        String methodName=method.getName();
        return String.format("%s.%s()",className,methodName);
    }

    /**
     * 统一缓存管理的key的规则
     * @param method
     * @return
     */
    public static String genCacheManageKey(Method method){
        String className=method.getDeclaringClass().getName();
        String methodName=method.getName();
        return String.format("%s.%s()",className,methodName);
    }


    /**
     * 需要写入到Mongo中，用于管理端的展示管理.
     * 写入实现类的接口列表
     * 因为AOP拦截到的实际是实现类的方法
     * RuleMap中interface_key->CacheRule，class_key->CacheRule
     * InterfaceMap中interface_key->CacheRule
     * 当注解应用于接口时，RuleMap中的class_key对应的规则可能是空，所以要取接口上的规则。
     */
    private void persist() {
        for (Map.Entry<String, CacheRule> entry : ruleMap.entrySet()) {
            CacheRule clazzCacheRule = entry.getValue();
            if (clazzCacheRule.getClazz().isInterface()) {
                continue;
            }
            /**
             * 实现类上没有注解，对应的接口可能有注解，将接口的注解映射给实现类
             */
            Class iFace = classInferfaceMap.get(entry.getValue().getClazz());
            if (iFace != null) {//表示实现了接口
                String cacheKey = genCacheManageKey(iFace,clazzCacheRule.getMethod());
                //取接口对应的规则
                CacheRule cacheRule = ruleMap.get(cacheKey);
                //判断是否实现类自己的私有方法等
                if (cacheRule != null) {
                    //write to mongo
                    logger.info(cacheRule.getCacheRuleKey() + "=>" + cacheRule.getKeepAlive());
                }
            } else {//没有实现接口
                //write to mongo
                logger.info(clazzCacheRule.getCacheRuleKey() + "=>" + clazzCacheRule.getKeepAlive());
            }
        }
    }

    /**
     * key 从实现类生成的Key
     * @param cacheManageKey
     * @return
     */
    public CacheRule getCacheRule(String cacheManageKey){
        return ruleMap.get(cacheManageKey);
    }


}
