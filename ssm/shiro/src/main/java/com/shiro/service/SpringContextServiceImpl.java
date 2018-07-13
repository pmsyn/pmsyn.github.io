package com.shiro.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author:pms
 * @createtime:2018/7/13-9:55
 * @qq 718195578
 * @since 1.0
 */
@Service
public class SpringContextServiceImpl implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final Logger log = LoggerFactory.getLogger(SpringContextServiceImpl.class);
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    public static <T> T getBean(String beanName){
        log.info(beanName);
        return (T)applicationContext.getBean(beanName);
    }
}
