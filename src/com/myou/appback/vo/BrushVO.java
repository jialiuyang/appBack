package com.myou.appback.vo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.myou.appback.vo.base.AbstractValueObject;

/**
 * Title: 爱花族网站平台<br>
 * Description: 刷子VO<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author sgl
 * @version 1.0
 */
@Entity
@Table(name = "tb_org_scalping_flow", schema = "SCOTT")
public class BrushVO extends AbstractValueObject{

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;
    /** 单据主键 时间 备注 收入  支出   剩余(把) */
    private String serialNumber;
    /** source_bill */
    private String sourceBill;
    /** 方向( 0、负 1、正 ) */
    private Short orient;
    /** 发生刷子 */
    private String scalping;
    /** 发生后刷子 剩余(把) */
    private String afterScalping;
    /** MEMO */
    private String memo;
    /** pkOrg */
    private String pkOrg;
   
    /** 空构 */
    public BrushVO (){
        
    }

    public String getPrimaryKey() {
        return serialNumber;
    }

    public void setPrimaryKey(String key) {
        this.serialNumber = key;
    }
    
    @Column(name = "AFTER_SCALPING")
	public String getAfterScalping() {
		return afterScalping;
	}
	
	public String getMyTs(){
		return (super.getTs() + "").replace(".0", "");
	}

	public void setAfterScalping(String afterScalping) {
		this.afterScalping = afterScalping;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Short getOrient() {
		return orient;
	}

	public void setOrient(Short orient) {
		this.orient = orient;
	}

	public String getPkOrg() {
		return pkOrg;
	}

	public void setPkOrg(String pkOrg) {
		this.pkOrg = pkOrg;
	}

	public String getScalping() {
		return scalping;
	}

	public void setScalping(String scalping) {
		this.scalping = scalping;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSourceBill() {
		return sourceBill;
	}

	public void setSourceBill(String sourceBill) {
		this.sourceBill = sourceBill;
	}

}
