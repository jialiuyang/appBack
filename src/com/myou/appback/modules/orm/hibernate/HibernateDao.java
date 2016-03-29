package com.myou.appback.modules.orm.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * Title: 爱花族网站平台<br>
 * Description: 弱化DAO层的抽象父类，提供hibernate的基础方法（增删查改）<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @param <T>
 * @param <PK>
 * @version 1.0
 */
public class HibernateDao<T, PK extends Serializable> {

    /** 日志对象 */
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /** HibernateDao的sesswionFactory对象 */
    protected SessionFactory sessionFactory;
    /** 泛型的class */
    protected Class<T> entityClass;

    /**
     * 空构，用于纯JDBC DAO
     */
    public HibernateDao(){

    }

    /**
     * Hibernate 与 JDBC 并存
     */
    public HibernateDao(final Class<T> entityClass){
    	this.entityClass = entityClass;
    }

    /**
     * 用于用于省略Dao层, 在Service层直接使用通用SimpleHibernateDao的构造函数.仅仅只有hibernate的情况
     * 在构造函数中定义对象类型Class.
     * eg.
     * SimpleHibernateDao<User, Long> userDao = new SimpleHibernateDao<User, Long>(sessionFactory, User.class);
     */
    public HibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * 采用@Autowired按类型注入SessionFactory,当有多个SesionFactory的时候Override本函数.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 取得当前Session.
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 保存新增或修改的对象.
     */
    public void save(final T entity) {
        Assert.notNull(entity, "entity不能为空");
        getSession().saveOrUpdate(entity);
        logger.debug("save entity: {}", entity);
    }

    /**
     * 删除对象.
     *
     * @param entity 对象必须是session中的对象或含id属性的transient对象.
     */
    public void delete(final T entity) {
        Assert.notNull(entity, "entity不能为空");
        getSession().delete(entity);
        logger.debug("delete entity: {}", entity);
    }

    /**
     * 按id删除对象.
     */
    public void delete(final PK id) {
        Assert.notNull(id, "id不能为空");
        delete(get(id));
        logger.debug("delete entity {},id is {}", entityClass.getSimpleName(), id);
    }

    /**
     * 按id获取对象.
     */
    public T get(final PK id) {
        Assert.notNull(id, "id不能为空");
        return (T) getSession().load(entityClass, id);
    }

    /**
     *  获取全部对象.
     */
    public List<T> getAll() {
        return find();
    }

    /**
     * 按属性查找对象列表,匹配方式为相等.
     */
    public List<T> findBy(final String propertyName, final Object value) {
        Assert.hasText(propertyName, "propertyName不能为空");
        Criterion criterion = Restrictions.eq(propertyName, value);
        return find(criterion);
    }

    /**
     * 按属性查找唯一对象,匹配方式为相等.
     */
    public T findByUnique(final String propertyName, final Object value) {
        Assert.hasText(propertyName, "propertyName不能为空");
        Criterion criterion = Restrictions.eq(propertyName, value);
        return (T) createCriteria(criterion).uniqueResult();
    }

    /**
     * 按HQL查询对象列表.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    public <X> List<X> find(final String hql, final Object... values) {
        return createQuery(hql, values).list();
    }

    /**
     * 按HQL查询对象列表.
     *
     * @param values 命名参数,按名称绑定.
     */
    public <X> List<X> find(final String hql, final Map<String, Object> values) {
        return createQuery(hql, values).list();
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    public <X> X findUnique(final String hql, final Object... values) {
        return (X) createQuery(hql, values).uniqueResult();
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param values 命名参数,按名称绑定.
     */
    public <X> X findUnique(final String hql, final Map<String, Object> values) {
        return (X) createQuery(hql, values).uniqueResult();
    }

    /**
     * 执行HQL进行批量修改/删除操作.
     */
    public int batchExecute(final String hql, final Object... values) {
        return createQuery(hql, values).executeUpdate();
    }

    /**
     * 执行HQL进行批量修改/删除操作.
     * @return 更新记录数.
     */
    public int batchExecute(final String hql, final Map<String, Object> values) {
        return createQuery(hql, values).executeUpdate();
    }

    /**
     * 根据查询HQL与参数列表创建Query对象.
     *
     * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
     *
     * @param values 数量可变的参数,按顺序绑定.
     */
    public Query createQuery(final String queryString, final Object... values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSession().createQuery(queryString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        return query;
    }

    /**
     * 根据查询HQL与参数列表创建Query对象.
     *
     * @param values 命名参数,按名称绑定.
     */
    public Query createQuery(final String queryString, final Map<String, Object> values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSession().createQuery(queryString);
        if (values != null) {
            query.setProperties(values);
        }
        return query;
    }

    /**
     * 按Criteria查询对象列表.
     *
     * @param criterions 数量可变的Criterion.
     */
    public List<T> find(final Criterion... criterions) {
        return createCriteria(criterions).list();
    }

    /**
     * 按Criteria查询唯一对象.
     *
     * @param criterions 数量可变的Criterion.
     */
    public T findUnique(final Criterion... criterions) {
        return (T) createCriteria(criterions).uniqueResult();
    }

    /**
     * 根据Criterion条件创建Criteria.
     *
     * 本类封装的find()函数全部默认返回对象类型为T,当不为T时使用本函数.
     *
     * @param criterions 数量可变的Criterion.
     */
    public Criteria createCriteria(final Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(entityClass);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    /**
     * 初始化对象.
     * 使用load()方法得到的仅是对象Proxy后, 在传到View层前需要进行初始化.
     * initObject(user) ,初始化User的直接属性，但不会初始化延迟加载的关联集合和属性.
     * initObject(user.getRoles())，初始化User的直接属性和关联集合.
     * initObject(user.getDescription())，初始化User的直接属性和延迟加载的Description属性.
     */
    public void initObject(Object object) {
        Hibernate.initialize(object);
    }

    /**
     * 批量初始化对象.
     * @see #initObject(Object)
     */
    public void initObjects(List list) {
        for (Object object : list) {
            Hibernate.initialize(object);
        }
    }

    /**
     * 为Query添加distinct transformer.
     */
    public Query distinct(Query query) {
        query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return query;
    }

    /**
     * 为Criteria添加distinct transformer.
     */
    public Criteria distinct(Criteria criteria) {
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria;
    }

    /**
     * 通过Set将不唯一的对象列表唯一化.
     * 主要用于HQL/Criteria预加载关联集合形成重复记录,又不方便使用distinct查询语句时.
     */
    public <X> List<X> distinct(List list) {
        Set<X> set = new LinkedHashSet<X>(list);
        return new ArrayList<X>(set);
    }

    /**
     * 取得对象的主键名.
     */
    public String getIdName() {
        ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
        return meta.getIdentifierPropertyName();
    }
}