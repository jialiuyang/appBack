package com.myou.appback.modules.orm.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.myou.appback.modules.base.Page;
import com.myou.appback.modules.orm.hibernate.PropertyFilter.MatchType;
import com.myou.appback.modules.util.ReflectionUtils;

/**
 * Title: 爱花族网站平台<br>
 * Description: 封装扩展功能的Hibernat DAO泛型基类.<br>
 *              扩展功能包括分页查询,按属性过滤条件列表查询.<br>
 *              在Service层直接使用
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @param <T>
 * @param <PK>
 * @version 1.0
 */
public class SimpleDao<T, PK extends Serializable> extends HibernateDao<T, PK> {

    /** 数据源 */
    private DataSource dataSource;

    /** 空构 */
    public SimpleDao() {

    }

    /** hibernate与jdbc并存  */
    public SimpleDao(final Class<T> entityClass) {
        super(entityClass);
    }

    /**
     * 纯JDBC的构造函数.
     * 传入dataSource即可
     */
    public SimpleDao(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 用于省略Dao层, Service层直接使用通用HibernateDao的构造函数.仅仅hibernate
     * 在构造函数中定义对象类型Class.
     * eg.
     * HibernateDao<User, Long> userDao = new HibernateDao<User, Long>(sessionFactory, User.class);
     */
    public SimpleDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
        super(sessionFactory, entityClass);
    }

    /**
     * 设置数据源
     * @param dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 获取数据源
     * @return
     */
    public DataSource getDataSource(){
        return dataSource;
    }

    /**
     * 获取JdbcTemplate
     * @return
     */
    public JdbcTemplate getJdbcTemplate(){
        return new JdbcTemplate(dataSource);
    }

    /**
     * 分页获取全部对象.
     */
    public Page<T> getAll(final Page<T> page) {
        return find(page);
    }

    /**
     * 按HQL分页查询.
     *
     * @param page   分页参数.不支持其中的orderBy参数.
     * @param hql    hql语句.
     * @param values 数量可变的查询参数,按顺序绑定.
     * @return 分页查询结果, 附带结果列表及所有查询时的参数.
     */
    public Page<T> find(final Page<T> page, final String hql, final Object... values) {
        Assert.notNull(page, "page不能为空");

        Query q = createQuery(hql, values);

        if (page.isAutoCount()) {
            long totalCount = countHqlResult(hql, values);
            page.setTotalCount(totalCount);
        }

        setPageParameter(q, page);
        @SuppressWarnings("unchecked")
        List<T> result = q.list();
        page.setResult(result);
        return page;
    }

    /**
     * 按HQL分页查询.
     *
     * @param page   分页参数.(不支持orderBy参数)
     * @param hql    hql语句.
     * @param values 命名参数,按名称绑定.
     * @return 分页查询结果, 附带结果列表及所有查询时的参数.
     */
    @SuppressWarnings("unchecked")
    public Page<T> find(final Page<T> page, final String hql, final Map<String, Object> values) {
        Assert.notNull(page, "page不能为空");

        Query q = createQuery(hql, values);

        if (page.isAutoCount()) {
            long totalCount = countHqlResult(hql, values);
            page.setTotalCount(totalCount);
        }

        setPageParameter(q, page);

        List<T> result = q.list();
        page.setResult(result);
        return page;
    }

    /**
     * 按Criteria分页查询.
     *
     * @param page       分页参数.
     * @param criterions 数量可变的Criterion.
     * @return 分页查询结果.附带结果列表及所有查询时的参数.
     */
    @SuppressWarnings("unchecked")
    public Page<T> find(final Page<T> page, final Criterion... criterions) {
        Assert.notNull(page, "page不能为空");

        Criteria c = createCriteria(criterions);

        if (page.isAutoCount()) {
            int totalCount = countCriteriaResult(c);
            page.setTotalCount(totalCount);
        }

        setPageParameter(c, page);
        List<T> result = c.list();
        page.setResult(result);
        return page;
    }

    /**
     * 设置分页参数到Query对象,辅助函数.
     */
    protected Query setPageParameter(final Query q, final Page<T> page) {
        //hibernate的firstResult的序号从0开始
        q.setFirstResult(page.getFirst() - 1);
        q.setMaxResults(page.getPageSize());
        return q;
    }

    /**
     * 设置分页参数到Criteria对象,辅助函数.
     */
    protected Criteria setPageParameter(final Criteria c, final Page<T> page) {
        //hibernate的firstResult的序号从0开始
        c.setFirstResult(page.getFirst() - 1);
        c.setMaxResults(page.getPageSize());

        if (page.isOrderBySetted()) {
            String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
            String[] orderArray = StringUtils.split(page.getOrder(), ',');

            Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

            for (int i = 0; i < orderByArray.length; i++) {
                if (Page.ASC.equals(orderArray[i])) {
                    c.addOrder(Order.asc(orderByArray[i]));
                } else {
                    c.addOrder(Order.desc(orderByArray[i]));
                }
            }
        }
        return c;
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.
     * <p/>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     */
    protected long countHqlResult(final String hql, final Object... values) {
        Long count = 0L;
        String fromHql = hql;
        //select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBefore(fromHql, "order by");

        String countHql = "select count(*) " + fromHql;

        try {
            count = findUnique(countHql, values);
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
        }
        return count;
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.
     * <p/>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     */
    protected long countHqlResult(final String hql, final Map<String, Object> values) {
        Long count = 0L;
        String fromHql = hql;
        //select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBefore(fromHql, "order by");

        String countHql = "select count(*) " + fromHql;

        try {
            count = findUnique(countHql, values);
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
        }

        return count;
    }

    /**
     * 执行count查询获得本次Criteria查询所能获得的对象总数.
     */
    @SuppressWarnings("unchecked")
    protected int countCriteriaResult(final Criteria c) {
        CriteriaImpl impl = (CriteriaImpl) c;

        // 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
        Projection projection = impl.getProjection();
        ResultTransformer transformer = impl.getResultTransformer();

        List<CriteriaImpl.OrderEntry> orderEntries = null;
        try {
            orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
            ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
        } catch (Exception e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }

        // 执行Count查询
        int totalCount = (Integer) c.setProjection(Projections.rowCount()).uniqueResult();

        // 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
        c.setProjection(projection);

        if (projection == null) {
            c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        }
        if (transformer != null) {
            c.setResultTransformer(transformer);
        }
        try {
            ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
        } catch (Exception e) {
            logger.error("不可能抛出的异常:{}", e.getMessage());
        }

        return totalCount;
    }

    // 属性过滤条件查询函数 //

    /**
     * 按属性查找对象列表,支持多种匹配方式.
     *
     * @param matchType 匹配方式,目前支持的取值为"EQUAL"与"LIKE".
     */
    public List<T> findBy(final String propertyName, final Object value, final MatchType matchType) {
        Criterion criterion = buildPropertyFilterCriterion(propertyName, value, matchType);
        return find(criterion);
    }

    /**
     * 按属性过滤条件列表查找对象列表.
     */
    public List<T> find(List<PropertyFilter> filters) {
        Criterion[] criterions = buildPropertyFilterCriterions(filters);
        return find(criterions);
    }

    /**
     * 按属性过滤条件列表分页查找对象.
     */
    public Page<T> find(final Page<T> page, final List<PropertyFilter> filters) {
        Criterion[] criterions = buildPropertyFilterCriterions(filters);
        return find(page, criterions);
    }

    /**
     * 按Criterion分页查询.
     *
     * @param page 分页参数.包括pageSize、firstResult、orderBy、asc、autoCount.
     *             其中firstResult可直接指定,也可以指定pageNo.
     *             autoCount指定是否动态获取总结果数.
     * @param c    数量可变的Criteria.
     * @return 分页查询结果.附带结果列表及所有查询时的参数.
     */
    public Page<T> pageByCriteria(Page page, Criteria c) {
        Assert.notNull(page);

        if (page.isAutoCount()) {
            int totalCount = countCriteriaResult(c);
            page.setTotalCount(totalCount);
        }

        setPageParameter(c, page);
        List result = c.list();
        page.setResult(result);
        return page;
    }

    /**
     * 按属性条件列表创建Criterion数组,辅助函数.
     */
    protected Criterion[] buildPropertyFilterCriterions(final List<PropertyFilter> filters) {
        List<Criterion> criterionList = new ArrayList<Criterion>();
        for (PropertyFilter filter : filters) {
            String propertyName = filter.getPropertyName();

            boolean multiProperty = StringUtils.contains(propertyName, PropertyFilter.OR_SEPARATOR);
            if (!multiProperty) { //properNameName中只有一个属性的情况.
                Criterion criterion = buildPropertyFilterCriterion(propertyName, filter.getValue(), filter
                        .getMatchType());
                criterionList.add(criterion);
            } else {//properName中包含多个属性的情况,进行or处理.
                Disjunction disjunction = Restrictions.disjunction();
                String[] params = StringUtils.split(propertyName, PropertyFilter.OR_SEPARATOR);

                for (String param : params) {
                    Criterion criterion = buildPropertyFilterCriterion(param, filter.getValue(), filter.getMatchType());
                    disjunction.add(criterion);
                }
                criterionList.add(disjunction);
            }
        }
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    /**
     * 按属性条件参数创建Criterion,辅助函数.
     */
    protected Criterion buildPropertyFilterCriterion(final String propertyName, final Object value,
                                                     final MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");
        Criterion criterion = null;

        if (MatchType.EQ.equals(matchType)) {
            criterion = Restrictions.eq(propertyName, value);
        }
        if (MatchType.LIKE.equals(matchType)) {
            criterion = Restrictions.like(propertyName, (String) value, MatchMode.ANYWHERE);
        }

        return criterion;
    }

    /**
     * 判断对象的属性值在数据库内是否唯一.
     * <p/>
     * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
     */
    public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {
        if (newValue == null || newValue.equals(oldValue))
            return true;
        Object object = findByUnique(propertyName, newValue);
        return (object == null);
    }
}