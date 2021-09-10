package com.tracesafe.subscriber.sanity.checker.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;


@Service
public class BeanUtil implements ApplicationContextAware{
	
	private static ApplicationContext context;
	
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
    public static <T> T getBean(String name, Class<T> beanClass) {
    	if (null == context) {
    		return null;
    	}
        return context.getBean(name, beanClass);
    }
}
