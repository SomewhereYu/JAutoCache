package com.goglezon.jautocache.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yuwenqi@goglezon.com on 2015/12/4 18:17.
 */
@Resource
public class JSpringPropertiesReader extends PropertyPlaceholderConfigurer {
    private static Logger logger = LoggerFactory.getLogger(JSpringPropertiesReader.class);

    private static Map<String, String> ctxPropertiesMap;

    public static String getProperty(String name) {
        return ctxPropertiesMap.get(name);
    }

    public static String getProperty(String name, String defaultValue) {
        return getProperty(name) == null ? defaultValue : getProperty(name);
    }

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactory,
                                     Properties props) throws BeansException {

        super.processProperties(beanFactory, props);
        ctxPropertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            ctxPropertiesMap.put(keyStr, value);
        }
    }

}
