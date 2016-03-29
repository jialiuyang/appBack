package com.myou.appback.modules.util;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.myou.appback.modules.base.BusinessBO;
import com.myou.appback.modules.base.ClientENV;
import com.myou.appback.modules.base.RunnableWithReturn;
import com.myou.appback.modules.exception.BusinessException;
import com.myou.appback.modules.foundation.oid.util.OIDGenerator;
import com.myou.appback.modules.foundation.util.DataSourceUtils;
import com.myou.appback.modules.foundation.util.SessionContext;
import com.myou.appback.modules.orm.hibernate.HibernateDao;

/**
 * @author huangweiguang
 *
 */
public abstract class GeneralOperatUtils extends BusinessBO{
    
	static Logger logger = LoggerFactory.getLogger(GeneralOperatUtils.class);
	
    /**
     * getHibernateTemplate
     * 取hibernate模版
     * @param dataSource DataSource
     * @return HibernateTemplate
     */
    public static HibernateTemplate getHibernateTemplate(DataSource dataSource) {
        SessionFactory sessionFactory = DataSourceUtils.getSessionFactory(dataSource);
        return new HibernateTemplate(sessionFactory);
    }

    /**
     * getHibernateTemplate
     * 取hibernate模板
     *
     * @return HibernateTemplate
     */
    public static Session getHibernateTemplate() {
        return SessionContext.getSessionContext().getSessionFactory().getCurrentSession();
    }

    /**
     * getJdbcTemplate
     * 取jdbc模板
     *
     * @param dataSource DataSource
     * @return JdbcTemplate
     */
    public static JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * getJdbcTemplate
     * 取jdbc模板
     *
     * @return JdbcTemplate
     */
    public static JdbcTemplate getJdbcTemplate(){
        return new JdbcTemplate(SessionContext.getSessionContext().getDataSource());
    }

    /**
     * getJdbcTemplate
     * 取jdbc模板
     *
     * @return JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplates(){
        return new JdbcTemplate(SessionContext.getSessionContext().getDataSource());
    }

    /**
     * getOID
     * 取值对象的pk
     *
     * @return String
     */
    public String getOID() {
        return OIDGenerator.getOID(getJdbcTemplates());
    }
    
    /**
     * 在给定的数据源上使用独立事物
     * @param callback ：回调函数类
     * @param dataSourceName： 将要处理的数据源名称
     * @return ：返回独立事物处理完成后返回的对象
     */
    public static Object independenceTrasaction(String dataSourceName, TransactionCallback callback ) {
        ServletContext servletContext = SessionContext.getSessionContext().getServletContext();
        SessionContext.getSessionContext().setServletContext(servletContext);
        String olddsName = SessionContext.getSessionContext().getDataSourceName();
        try {
            SessionContext.getSessionContext().initDataSourceByName(dataSourceName);
            PlatformTransactionManager platformTransactionManager = DataSourceUtils.getTransactionManager();
            TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
            return transactionTemplate.execute(callback);
        } finally {
            SessionContext.getSessionContext().initDataSourceByName(olddsName);
        }
    }
    /**
     * 在当前系统对应的数据源上使用独立事物
     * @param callback ：回调函数类
     * @return ：返回独立事物处理完成后返回的对象
     */
    public static Object independenceTrasaction(TransactionCallback callback) {
        String dataSourceName = SessionContext.getSessionContext().getDataSourceName();
        ServletContext servletContext = SessionContext.getSessionContext().getServletContext();
        SessionContext.getSessionContext().setServletContext(servletContext);
        SessionContext.getSessionContext().initDataSourceByName(dataSourceName);
        PlatformTransactionManager platformTransactionManager = DataSourceUtils.getTransactionManager();
        TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        return transactionTemplate.execute(callback);
    }
    

    /**
     * getClientENV
     * 取客户端上下文
     *
     * @return ClientENV
     */
    public static ClientENV getClientENV() {
        return SessionContext.getSessionContext().getClientenv();
    }

    /**
     * acquireLock
     * 加业务锁(悲观锁)
     *
     * @param lockpk String
     * @param pk_user String
     * @return boolean
     */
    public static boolean acquireLock(final String lockpk, final String pk_user) {
        Boolean block = (Boolean)independenceTrasaction(new TransactionCallback(){
            public Object doInTransaction(TransactionStatus status) {
                if (lockpk == null || lockpk.trim().length() == 0) {
                    throw new NullPointerException("lockpk不能为空");
                }
                List count = getJdbcTemplate().queryForList("select pk_lock from sm_lock where pk_lock='" + lockpk + "'");
                if (count.size() > 0) {
                    return Boolean.valueOf(false);
                }
                String ts = getCurTs();
                getJdbcTemplate().update("insert into sm_lock(pk_lock,pk_user,ts) values(?,?,?)",
                                         new Object[] {lockpk, pk_user, ts});
//                BusinessLockVO vo = new BusinessLockVO();
//                vo.setPk_lock(lockpk);
//                vo.setPk_user(pk_user);
//                vo.setTs(date);
//                _logger.info("lock(" + lockpk +")");
                return Boolean.valueOf(true);
            }
        });        
        return block.booleanValue();
    }

    /**
     * getTs
     * 生成时间戳
     *
     * @return String
     */
    public static String getCurTs() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }
    
    /**
     * freeLock 释放业务锁(悲观锁)
     * 
     * @param lockpk
     *            String
     */
    public static void freeLock(final String lockpk) {
        independenceTrasaction(new TransactionCallback() {
            public Object doInTransaction(TransactionStatus status) {
                getJdbcTemplate().update("delete from sm_lock where pk_lock='" + lockpk + "'");
                //_logger.info("freeLock(" + lockpk + ")");
                return null;
            }
        });
    }

    /**
     * lockProcess
     * 业务锁(悲观锁)
     *
     * @param lockpk String
     * @param pk_user String
     * @param runnable Runnable
     */
    public static void lockProcess(String lockpk, String pk_user, Runnable runnable) {

        boolean lock = false;
        try {
            lock = acquireLock(lockpk, pk_user);
            if (!lock) {
                throw new BusinessException("SYS_0000_E141");
            }
            runnable.run();
        } finally {
            if (lock) {
                freeLock(lockpk);
            }
        }
    }

    /**
     * lockProcess
     * 业务锁(悲观锁)
     *
     * @param lockpk String
     * @param pk_user String
     * @param runnable RunnableWithReturn
     * @return Object
     */
    public static Object lockProcess(String lockpk, String pk_user, RunnableWithReturn runnable) {

        boolean lock = false;
        try {
            lock = acquireLock(lockpk, pk_user);
            if (!lock) {
                throw new BusinessException("SYS_0000_E142");
            }
            return runnable.run();
        } finally {
            if (lock) {
                freeLock(lockpk);
            }
        }
    }
    
    
    /**
     * lockProcessSleep    主要是  00000000000000000001 和  00000000000000000004  同时调用余额流水出现问题
     * 业务锁(悲观锁)
     * @param lockpk String
     * @param pk_user String
     * @param runnable Runnable
     */
    public static void lockProcessSleep(String lockpk, String pk_user, Runnable runnable) {

        boolean lock = false;
        try {
            lock = acquireLock(lockpk, pk_user);
            while (!lock){
            	try {
            		logger.info("进入休眠业务锁！" + lockpk);
            		//00000000000000000001   00000000000000000004  充值或付款等待  10s
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	lock = acquireLock(lockpk, pk_user);
            }
            runnable.run();
        } finally {
        	if (lock) {
                freeLock(lockpk);
            }
        }
    }

    /**
     * lockProcessSleep
     * 业务锁(悲观锁)
     *
     * @param lockpk String
     * @param pk_user String
     * @param runnable RunnableWithReturn
     * @return Object
     */
    public static Object lockProcessSleep(String lockpk, String pk_user, RunnableWithReturn runnable) {

        boolean lock = false;
        try {
            lock = acquireLock(lockpk, pk_user);
            while (!lock){
            	try {
            		logger.info("进入休眠业务锁！" + lockpk);
            		//00000000000000000001   00000000000000000004  充值或付款等待  10s
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            	lock = acquireLock(lockpk, pk_user);
            }
            return runnable.run();
        } finally {
            if (lock) {
                freeLock(lockpk);
            }
        }
    }

}
