package com.myou.appback.modules.base;

/**
 * Title: 爱花族网站平台<br>
 * Description: 业务最终父接口，提供所有系统通用的公共方法<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public interface Business {

    /**
     * 记录业务日志
     * 
     * @param operate
     * @param billpk
     * @param billtype
     * @param pk_user
     * @param pk_org
     * @param ip
     */
    public void log(String businessName, String billPk, String billType, String pkUser, String pkOrg, String ip);

}