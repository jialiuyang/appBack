package com.myou.appback.modules.exception;

/**
 * Title: 企业营销信息管理系统<br>
 * Description: 业务异常<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: WINNER<br>
 * 
 * @author cjy
 * @version 1.0
 */
public class BusinessException extends RuntimeException {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 构造器
     * @param msg
     */
    public BusinessException(String msg) {
        super(msg);
    }
}