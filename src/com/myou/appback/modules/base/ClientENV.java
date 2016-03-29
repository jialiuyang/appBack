package com.myou.appback.modules.base;

import java.io.Serializable;

/**
 * Title: 爱花族网站平台<br>
 * Description: 与前台交互的ENV<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public class ClientENV implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 3272990249325617429l;

    private static ClientENV instance = null;

    public Boolean admin = false;

    public Long loginno;

    public String ip;
    
    public String curMethodName;
    public String curOperate;
    public String curBillType;
    public String curBillPk;

    /*
     * 当前是否在线，作为检查登出超时和登入的同步锁
     */
    public static Object onlineLock = new Object();
    
    public static boolean isOnline = false;
    
    public boolean isLixian = false;

    public static final Boolean IS_GROUP_ORG = false;

    public String timeFrist;
    public String getTimeFrist() {
		return timeFrist;
	}

	public void setTimeFrist(String timeFrist) {
		this.timeFrist = timeFrist;
	}

	public String getCurMethodName() {
        return curMethodName;
    }

    public void setCurMethodName(String curMethodName) {
        this.curMethodName = curMethodName;
    }

    public void setLogInfo(String curMethodName, String curOperate, String curBillPk) {
        this.curMethodName = curMethodName;
        this.curOperate = curOperate;
        this.curBillPk = curBillPk;
    }

    public String getCurBillPk() {
        return curBillPk;
    }

    public void setCurBillPk(String curBillPk) {
        this.curBillPk = curBillPk;
    }


    public String getCurBillType() {
        return curBillType;
    }

    public void setCurBillType(String curBillType) {
        this.curBillType = curBillType;
    }


    public String getCurOperate() {
        return curOperate;
    }

    public void setCurOperate(String curOperate) {
        this.curOperate = curOperate;
    }

    public static ClientENV getInstance() {
        if (instance == null) {
            instance = new ClientENV();
        }
        return instance;
    }

    public static ClientENV newInstance() {
        instance = new ClientENV();
        return instance;
    }

}