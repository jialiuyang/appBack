package com.myou.appback.modules.foundation.util;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ServerBeanFactoryUtils {
    static WebApplicationContext ac = null;
    public synchronized static WebApplicationContext getApplicationContext() {

        return WebApplicationContextUtils.getWebApplicationContext(SessionContext.getSessionContext().getServletContext());
    }

}