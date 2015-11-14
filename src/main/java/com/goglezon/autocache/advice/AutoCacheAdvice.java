package com.goglezon.autocache.advice;


import com.goglezon.autocache.annotation.JClearCache;
import com.goglezon.autocache.annotation.JUseCache;
import com.goglezon.autocache.model.AutoCacheBaseModel;
import com.goglezon.autocache.provider.AutoCacheProvider;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Yuwenqi on 2015/10/9.
 * 注意：仅适用于对DTO单行记录的查询和修改和删除操作，因此将此缓存置于DAO层！
 * 注意：不适用于批量操作及其它操作！
 */
public class AutoCacheAdvice implements MethodInterceptor {
    Logger logger= LoggerFactory.getLogger(AutoCacheAdvice.class);

    @Autowired
    private AutoCacheProvider autoCacheProvider;
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        logger.info("AOP is good enough to make newbie to be a fool.");
        Object result;
        JUseCache jUseCache=methodInvocation.getMethod().getAnnotation(JUseCache.class);
        JClearCache jClearCache=methodInvocation.getMethod().getAnnotation(JClearCache.class);

        //check annotations
        if(jUseCache !=null && jClearCache!=null){
            throw new Exception("The annotations \"JUseCache\" and \"JClearCache\" can't be used simultaneously!");
        }

        Object[] args=methodInvocation.getArguments();
        if(!(args[0] instanceof AutoCacheBaseModel)){
            throw new Exception("The argument in DAO method must be an instance of AutoCacheBaseModel");
        }
        AutoCacheBaseModel bm=(AutoCacheBaseModel)args[0];
        String className=bm.getClass().getName();
        String cacheKeyGetSet=className+"_"+bm.getCacheId();

        //Dao.get
        if(jUseCache!=null && jUseCache.useCache()){
            result= autoCacheProvider.get(cacheKeyGetSet);
            if(result!=null){
                return result;
            }
        }

        result=methodInvocation.proceed();
        //set Cache
        if(jUseCache!=null && jUseCache.useCache()){
            cacheKeyGetSet=className+"_"+((AutoCacheBaseModel)result).getCacheId();
            autoCacheProvider.set(cacheKeyGetSet,result,jUseCache.expiredTimeSec());
        }

        //clear Cache
        if(jClearCache!=null && jClearCache.clearCache()){
            String cacheKeyClear=className+"_"+((AutoCacheBaseModel)result).getCacheId();
            autoCacheProvider.clear(cacheKeyClear);
        }

        return result;
    }
}
