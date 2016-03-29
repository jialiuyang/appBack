package com.myou.appback.vo.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Title: 企业营销信息管理系统<br>
 * Description: VO父类<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public abstract class AbstractValueObject implements Comparable<Object>, Serializable {

    /** 序列号 */
    private static final long serialVersionUID = -2567834559471839093L;

    /** 时间戳(hibernate自动进行版本控制，同时查看最后修改日期) */
    Date ts;

    /**
     * 构造器
     */
    public AbstractValueObject() {

    }

    /**
     * 构造器，传入billId
     * 
     * @param billId
     */
    public AbstractValueObject(String pk) {
        setPrimaryKey(pk);
    }

    /**
     * get某一属性的值
     * 
     * @param attribute
     * @return
     */
    public Object get(String attribute) {
        try {
            BeanProperty property = new BeanProperty(this.getClass(), attribute, true, false);
            return property.get(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * set某一属性的值
     * 
     * @param attribute
     * @param value
     * @return
     */
    public Object set(String attribute, Object value) {
        BeanProperty property = new BeanProperty(this.getClass(), attribute, false, true);
        return property.set(this, value);
    }

    /**
     * 查看某一属性是否能写
     * 
     * @param PropertyName
     * @return
     */
    public boolean isWritableProperty(String PropertyName) {
        BeanProperty property = new BeanProperty(this.getClass(), PropertyName, false, true);
        return property.isWritable();
    }

    abstract public String getPrimaryKey();

    abstract public void setPrimaryKey(String key);

    public String toString() {
        return getPrimaryKey();
    }

    /**
     * 克隆一个完全相同的VO（深度克隆）
     * 
     * @return Object
     */
    public Object clone() {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream outs = new ObjectOutputStream(out);
            outs.writeObject(this);
            outs.close();
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
            return in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int compareTo(Object o) {
        AbstractValueObject oldvp = (AbstractValueObject) o;
        String pk = getPrimaryKey();
        String oldpk = oldvp.getPrimaryKey();
        DecimalFormat df = new DecimalFormat("0000000000");
        if (pk == null) {
            pk = "Z" +df.format(hashCode()) ;
        }
        if (oldpk == null) {
            oldpk = "Z" + df.format(o.hashCode());
        }
        return pk.compareTo(oldpk);
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

}