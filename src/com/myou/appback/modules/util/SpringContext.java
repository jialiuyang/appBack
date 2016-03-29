package com.myou.appback.modules.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Title: 爱花族网站平台<br>
 * Description: 控制spring的bean<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public class SpringContext implements ApplicationContextAware {

    /**
     * context
     */
    @Autowired 
    private static ApplicationContext   context;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 获取bean
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 是否包含bean
     * @param name
     * @return
     */
    public static Boolean containsBean(String name){
        return context.containsBean(name);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        SpringContext.context = context;
    }
}
