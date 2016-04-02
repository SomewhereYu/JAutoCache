package com.goglezon.jautocache.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuwenqi@goglezon.com on 2015/12/1 16:06.
 */

public class CacheRuleWorker extends Thread {

    private static Logger logger= LoggerFactory.getLogger(CacheRuleWorker.class);
    private ConcurrentHashMap<String,CacheRule> ruleMap;

    public CacheRuleWorker(ConcurrentHashMap ruleMap){
        this.ruleMap=ruleMap;
    }

    /**
     * 合并到缓存规则里
     * @param updatedMap
     */
    private void mergeToRuleMap(ConcurrentHashMap<String,CacheRule> updatedMap){
        if(updatedMap==null||updatedMap.size()==0) return;
        logger.error("ruleMap:"+ruleMap);
        for(Map.Entry<String,CacheRule> entry:updatedMap.entrySet()){
            String ruleKey=entry.getKey();
            CacheRule ruleNew=entry.getValue();
            CacheRule rule=ruleMap.get(ruleKey);
            if(rule==null){
                return;
            }
            rule.setCacheType(ruleNew.getCacheType());
            rule.setKeepAlive(ruleNew.getKeepAlive());
            ruleMap.put(ruleKey,rule);
        }
    }

    public void run() {
        Thread.currentThread().setName("CacheRuleWorker");
        logger.warn("Go Worker.");
        while(true) {
            try {
                synchronized (Thread.currentThread()) {
                    Thread.currentThread().wait(10000l);
                    ConcurrentHashMap<String, CacheRule> cacheRuleUpdateMap = loadFromMongo();
                    mergeToRuleMap(cacheRuleUpdateMap);
                    logger.warn("Loaded cache rule from mongo.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized ConcurrentHashMap loadFromMongo(){
        CacheRule rule=new CacheRule();
        rule.setCacheRuleKey("com.jd.pop.ware.category.serviceclient.jsf.CategoryServiceClient.getValueById()");
        rule.setKeepAlive(99999);
        rule.setCacheType(CacheType.Use);
        ConcurrentHashMap<String,CacheRule> cacheRuleUpdateMap=new ConcurrentHashMap<String, CacheRule>();
        //cacheRuleUpdateMap.put(rule.getCacheRuleKey(),rule);
        return cacheRuleUpdateMap;
    }
}
