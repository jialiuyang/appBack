package com.myou.appback.modules.foundation.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;

import com.myou.appback.modules.base.ClientENV;
import com.myou.appback.modules.util.SpringContext;

/**
 * <p>Title: 处理与web不直接关联的上下文</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SessionContext {

    static ThreadLocal actionContext = new SessionContextThreadLocal();
    PlatformTransactionManager platformTransactionManager;
    // ServletContext
    DataSource dataSource;
    String dataSourceName;
    SessionFactory sessionFactory;
    String pk_corp;
    Map<Object,Object> attibute;
    ServletContext servletContext;



    HttpSession httpSession;
    ClientENV clientenv;

    //~ Inner Classes //////////////////////////////////////////////////////////

    private static class SessionContextThreadLocal extends ThreadLocal {
        protected Object initialValue() {
            HashMap map = new HashMap();
            return new SessionContext(map);
        }
    }


    public SessionContext() {
    }

    public SessionContext(Map<Object,Object> attibute) {
        this.attibute = attibute;
    }

    public static SessionContext getSessionContext() {
        return (SessionContext) actionContext.get();
    }


    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }


    public void setAttibuteMap(Map<Object,Object> attibute) {
        getSessionContext().attibute = attibute;
    }

    public Map<Object,Object> getAttibuteMap() {
        return attibute;
    }

    public Object get(Object key) {
        return attibute.get(key);
    }


    public void put(Object key, Object value) {
        attibute.put(key, value);
    }


    public DataSource getDataSource() {
        // TODO:为单元测试而写
        if (dataSource == null){
            dataSource = (DataSource)SpringContext.getBean("dataSource");
        }
        return dataSource;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public PlatformTransactionManager getPlatformTransactionManager() {
        return platformTransactionManager;
    }

    public SessionFactory getSessionFactory() {
        if (sessionFactory == null){
            sessionFactory = (SessionFactory) SpringContext.getBean("sessionFactory");
        }
        return sessionFactory;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public ClientENV getClientenv() {
        return clientenv;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setClientenv(ClientENV clientenv) {
        this.clientenv = clientenv;
    }

    public void initDataSourceByName(String dataSourceName) {
        setDataSourceName(dataSourceName);
        dataSource = DataSourceUtils.findDataSource(dataSourceName);
        platformTransactionManager = DataSourceUtils.findTransactionManager(dataSourceName);
        sessionFactory = DataSourceUtils.findSessionFactory(dataSourceName);
    }

    public synchronized static void setAttributeForServletContext(String key,Object obj) {
        SessionContext.getSessionContext().getServletContext().setAttribute(key,obj);
    }

    public synchronized static Object getAttributeForServletContext(String key) {
        return SessionContext.getSessionContext().getServletContext().getAttribute(key);
    }
}