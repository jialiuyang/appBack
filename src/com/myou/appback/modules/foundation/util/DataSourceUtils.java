package com.myou.appback.modules.foundation.util;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.transaction.PlatformTransactionManager;


public class DataSourceUtils {

    public final static String TM_BEAN_SUFFIX = "TransactionManager";
    public final static String JTA_TM_BEAN_SUFFIX = "jtaTransactionManager";
    public final static String SESSION_FACTORY_BEAN_SUFFIX = "SessionFactory";
    public final static String DATASOURCE_BEAN_SUFFIX = "DataSource";

    public static PlatformTransactionManager getTransactionManager() {
        return findTransactionManager(SessionContext.getSessionContext().getDataSourceName());
    }

    public static PlatformTransactionManager getTransactionManager(DataSource dataSource) {
        if (SessionContext.getSessionContext().getDataSource() == dataSource) {
            return SessionContext.getSessionContext().getPlatformTransactionManager();
        } else {
            String dataSourceName = findDataSourceName(dataSource);
            if (dataSourceName == null) {
                throw new IllegalStateException("can not find dataSource in beanfactory:" + dataSourceName);
            }
            return findTransactionManager(dataSourceName);
        }
    }

    public static String findDataSourceName(DataSource dataSource) {
        String[] beanNames = ServerBeanFactoryUtils.getApplicationContext().getBeanNamesForType(DataSource.class);

        for (int i = 0; i < beanNames.length; i++) {
            if (ServerBeanFactoryUtils.getApplicationContext().getBean(beanNames[i]) == dataSource) {
                return beanNames[i];
            }
        }
        return null;
    }

    public static SessionFactory getSessionFactory(DataSource dataSource) {
        if (SessionContext.getSessionContext().getDataSource() == dataSource) {
            return SessionContext.getSessionContext().getSessionFactory();
        } else {
            String dataSourceName = findDataSourceName(dataSource);
            if (dataSourceName == null) {
                throw new IllegalStateException("can not find dataSource in beanfactory:" + dataSourceName);
            }
            return findSessionFactory(dataSourceName);
        }
    }

    public static SessionFactory findSessionFactory(String dataSourceName) {
        if (dataSourceName == null) {
            throw new IllegalArgumentException("can not find dataSource in beanfactory:" + dataSourceName);
        }
        if (ServerBeanFactoryUtils.getApplicationContext().containsBean(dataSourceName + SESSION_FACTORY_BEAN_SUFFIX)) {
            return (SessionFactory) ServerBeanFactoryUtils.getApplicationContext().getBean(dataSourceName +
                SESSION_FACTORY_BEAN_SUFFIX);
        }else{
            return SessionFactoryCache.getSessionFactoryByDsName(dataSourceName);
        }
//        throw new IllegalStateException("can not find SessionFactory match this dataSource  in beanfactory:" +
//                                        dataSourceName);
    }


    public static DataSource findDataSource(String dataSourceName) {
        if (dataSourceName == null) {
            throw new IllegalArgumentException("can not find dataSource in beanfactory:" + dataSourceName);
        }
        System.out.println("test" + ServerBeanFactoryUtils.getApplicationContext());
        if (ServerBeanFactoryUtils.getApplicationContext().containsBean(dataSourceName + DATASOURCE_BEAN_SUFFIX)) {
            return (DataSource) ServerBeanFactoryUtils.getApplicationContext().getBean(dataSourceName +
                DATASOURCE_BEAN_SUFFIX);
        }
        throw new IllegalStateException("can not find DataSource match this dataSource  in beanfactory:" +
                                        dataSourceName);
    }


    public static PlatformTransactionManager findTransactionManager(String dataSourceName) {

        if (dataSourceName == null) {
            throw new IllegalArgumentException("can not find dataSource in beanfactory:" + dataSourceName);
        }
        if (ServerBeanFactoryUtils.getApplicationContext().containsBean(dataSourceName + TM_BEAN_SUFFIX)) {
            return (PlatformTransactionManager) ServerBeanFactoryUtils.getApplicationContext().getBean(dataSourceName +
                TM_BEAN_SUFFIX);
        }
        if (ServerBeanFactoryUtils.getApplicationContext().containsBean(JTA_TM_BEAN_SUFFIX)) {
            return (PlatformTransactionManager) ServerBeanFactoryUtils.getApplicationContext().getBean(
                JTA_TM_BEAN_SUFFIX);
        }
        return null;
//        throw new IllegalStateException("can not find transactionManager match this dataSource  in beanfactory:" +
//                                        dataSourceName);

    }
}