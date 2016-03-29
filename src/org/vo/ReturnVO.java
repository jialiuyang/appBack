package org.vo;

import java.io.Serializable;

/**
 * Title: 爱花族网站平台<br>
 * Description: 将两边的数据已 ReturnVO 的 方式传送<br>
 * Copyright: Copyright (c) 2012<br>
 * Company: MYOU<br>
 * 
 * @author jjy
 * @version 1.0
 */
public class ReturnVO implements Serializable{
    
    /**
     * 序列号
     */
    private static final long serialVersionUID = -2849495175317134008L;

    /** 数据是否为空 */
    private boolean isNull;
    
    /** 是否为业务异常 */
    private boolean isBusinessException;
    
    /** 数据 */
    private Object data;
    
    public ReturnVO() {
    }
    
    public ReturnVO(boolean isBusinessException, Object data){
    	this.isBusinessException = isBusinessException;
    	this.data = data;
    }

    @Override
    public String toString() {
        if(isBusinessException){
            return "业务异常：" + data;
        }else if(isNull){
            return null;
        }else{
            return data.toString();
        }
    }
    
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isBusinessException() {
        return isBusinessException;
    }

    public void setBusinessException(boolean isBusinessException) {
        this.isBusinessException = isBusinessException;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setIsNull(boolean isNull) {
        this.isNull = isNull;
    }
}
