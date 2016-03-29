package com.myou.appback.modules.orm.hibernate;

/**
 * Title: 爱花族网站平台<br>
 * Description: 与具体ORM实现无关的属性过滤条件封装类.<br>
 *              PropertyFilter主要记录页面中简单的搜索过滤条件,比Hibernate的Criterion要简单..<br>
 *              TODO:扩展其他对比方式如大于、小于及其他数据类型如数字和日期.
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public class PropertyFilter {

    /**
     * 多个属性间OR关系的分隔符.
     */
    public static final String OR_SEPARATOR = "_OR_";

    /**
     * 属性比较类型.
     */
    public enum MatchType {
        EQ, LIKE;
    }

    private String propertyName;
    private Object value;
    private MatchType matchType = MatchType.EQ;

    public PropertyFilter() {
    }

    public PropertyFilter(final String propertyName, final MatchType matchType, final Object value) {
        this.propertyName = propertyName;
        this.matchType = matchType;
        this.value = value;
    }

    /**
     * 获取属性名称,可用'_OR_'分隔多个属性,此时属性间是or的关系.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * 设置属性名称,可用'_OR_'分隔多个属性,此时属性间是or的关系.
     */
    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(final MatchType matchType) {
        this.matchType = matchType;
    }
}