package com.myou.appback.vo.base;

import java.io.Serializable;

/**
 * Title: 爱花族网站平台<br>
 * Description: 抽象参照类，规定了三个必须有的属性<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public abstract class AbstractRefDataVO implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -5502077355046266929L;

    /** PK */
    public String pk;
    /** 编号 */
    public String code;
    /** 名称 */
    public String name;

    /** 比较PK */
    public int compareTo(Object o) {
        AbstractRefDataVO oldvp = (AbstractRefDataVO) o;
        String pk = getPk();
        String oldpk = oldvp.getPk();
        if (pk == null) {
            pk = "";
        }
        if (oldpk == null) {
            oldpk = "";
        }
        return pk.compareTo(oldpk);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPk() {
        return pk;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setCode(Integer code) {
        this.code = String.valueOf(code);
    }

    public String getPrimaryKey() {
        return pk;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject == null) {
            return false;
        }
        if (!this.getClass().equals(anObject.getClass())) {
            return false;
        }
        AbstractRefDataVO aRefVO = (AbstractRefDataVO) anObject;
        if (aRefVO.getPk() == null || this.getPk() == null) {
            return false;
        }
        return aRefVO.getPk().equals(this.getPk());
    }

}
