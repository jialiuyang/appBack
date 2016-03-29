package com.myou.appback.modules.base;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.myou.appback.modules.exception.BusinessException;
import com.myou.appback.modules.filter.MobileKeyWordFilter;
import com.myou.appback.modules.foundation.oid.util.OIDGenerator;

/**
 * Title: 爱花族网站平台<br>
 * Description: 业务最终父接口实现类，提供所有系统级通用的公共方法<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public abstract class BusinessBO implements Business{
    
    /** 短信最大数量 69个字是没有问题的。 */
    public static final int MAXMSGCOUNT = 69;

    /** 日志对象 */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static final SimpleDateFormat SDFYou = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-SSS");
    
    /**
     * 获取JDBC模板，子类分别实现
     * @return
     */
    public abstract JdbcTemplate getJdbcTemplates();
    
    /**
     * getOID 取值对象的pk
     * @return String
     */
    public String getOID() {
        return OIDGenerator.getOID(getJdbcTemplates());
    }

    /**
     * 记录业务日志
     * 
     * @param operate
     * @param billpk
     * @param billtype
     * @param pk_user
     * @param pk_org
     */
    public void log(String businessName, String billPk, String billType, String pkUser, String pkOrg, String ip){

        logger.info(businessName + "-" + billPk + "-" + billType + "-" + pkUser + "-" + pkOrg);
        StringBuffer sql = new StringBuffer();
        // DELAYED 业务日志如果要求不严谨并苛求性能，可加上DELAYED关键字
        sql.append(" INSERT  INTO SM_BUSINESS_LOG ");
        sql.append(" ( ");
        sql.append("   BUSINESS_NAME,   SOURCE_BILL, SOURCE_BILLTYPE,  ");
        sql.append("   PK_OPERATOR,     PK_ORG,      TS,  ");
        sql.append("   IP              ");
        sql.append(" ) ");
        sql.append(" VALUES ");
        sql.append(" ( ");
        sql.append("   ?,     ?,     ?,  ");
        sql.append("   ?,     ?,     ?, ");
        sql.append("   ? ) ");
        Object [] args = new Object[]{businessName, billPk, billType, pkUser, pkOrg, this.getDateTime(), ip};
        getJdbcTemplates().update(sql.toString(), args);
    }

    /**
     * 纯JDBC分页，与hibernate无关,传回map
     */
    protected List<Map<String, Object>> findByJdbcTemplatePagination(JdbcTemplate template, String sql, final Object[] args, int first, int max) {

        StringBuffer sb = new StringBuffer(sql);
        sb.append(" limit ");
        sb.append(first);
        sb.append(" , ");
        sb.append(max);
        sql = sb.toString();
        // logger.info("查询sql: " + sql + "------参数" + args);
        return template.queryForList(sql, args);
    }

    /**
     * 发送邮件
     * @param toMail 邮箱地址
     * @param data 填充变量
     * @param type 类型，为常量表里的data,确定到邮件发送类型
     */
    @Transactional
    public void sendMail(String sendMail, String toMail, String title, Map<String, String> argsMap,  String emailVm) {
        sendMail(sendMail, toMail, "00000000000000000001", title, argsMap, emailVm);
    }

    /**
     * 发送邮件
     * 
     * @param sendMail
     *            发送邮箱地址
     * @param toMail
     *            目标邮箱地址
     * @param pkOrg
     *            花店主键
     * @param title
     *            标题
     * @param argsMap
     *            填充变量
     * @param emailVm
     *            模板名称
     */
    @Transactional
    public void sendMail(String sendMail, String toMail, String pkOrg, String title, Map<String, String> argsMap, String emailVm) {
        String match = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        StringBuffer sb = new StringBuffer();
        for (Entry<String, String> entry : argsMap.entrySet()) {
            sb.append(entry.getKey()).append("==").append(entry.getValue()).append(";;");
        }
        if (sb.length() > 2) {
            sb.delete(sb.length() - 2, sb.length());
        }

        StringBuffer sql = new StringBuffer();
        sql.append(" INSERT INTO SM_WAITING_MAIL ");
        sql.append(" ( ");
        sql.append("    TO_EMAIL,     ARGS,          TITLE,   PK_ORG,  ");
        sql.append("    EMAIL_VM,     BILLSTATUS,    TS,      SEND_EMAIL ");
        sql.append(" ) VALUES ( ");
        sql.append("    ?,              ?,             ?,     ?,");
        sql.append("    ?,              ?,             NOW(), ? ");
        sql.append(" ) ");
        Object[] arg = new Object[] { toMail, sb.toString(), title.toString(), pkOrg, emailVm,
                1, sendMail };
        getJdbcTemplates().update(sql.toString(), arg);
    }
    
    /**
     * 发送给 3个 系统管理员
     * @param msg
     * @param isQun
     */
    public void sendSms(String msg){
        String mobileNo = "18757113775;13735885160";
        msg += "【系统】";
        sendSms(mobileNo, msg, true, "00000000000000000001", true);
    }

    /**
     * 内部使用。
     * @param mobileNo
     * @param msg
     */
    public void sendSms(String mobileNo, String msg){
        msg += "【系统】";
        sendSms(mobileNo, msg, false, "00000000000000000001", true);
    }
    
    /**
     * 发送短信（无需扣除余额）
     * @param mobileNo
     * @param msg 
     * @param isQun 是否群发
     * @param pkOrg
     * @param isAutoSend 余额不足是否发送im消息
     * return 内容和手机是否正确
     */
    public boolean sendSms(String mobileNo, final String msg, Boolean isQun, final String pkOrg , final boolean isAutoSend){
        
        String error = "";
        if(mobileNo != null){
            mobileNo = mobileNo.trim();
        }
        logger.info(mobileNo);
        if (StringUtils.isEmpty(mobileNo) || !mobileNo.matches("^[0-9;]+$")) {
            error += "号码不正确，请注意格式，多个号码以\";\"号分隔！\n";
        }
        
        if(msg.length() > MAXMSGCOUNT){
            error += "内容不能超过" + MAXMSGCOUNT + "个字！\n";
        }
        
        List<String> list = MobileKeyWordFilter.getInstance().doFilter(msg);
        if(list.size() != 0){
            StringBuffer sb = new StringBuffer();
            sb.append("发送短信失败，您包含以下关键字：");
            for (String s : list) {
                sb.append(s + "，");
            }
            error += sb.toString() + "\n";
        }

        if(!error.equals("")){
            if(isAutoSend){
                logger.info("短息内容错误，无法发送：" + error);
                return false;
            }else{
                throw new BusinessException(error);
            }
        }
        
        StringBuffer sql = new StringBuffer();
        sql.append(" INSERT INTO SMS_WAITSEND( ");
        sql.append("     MOBILENO,    MSGTEXT,   billstatus, ");
        sql.append("     CHARTYPE,    COMLIMIT,  pk_org,      is_unions ");
        sql.append(" ) VALUES( ");
        sql.append("     ?,           ?,   1, ");
        sql.append("     'U',         0 ,  ?  , ?");
        sql.append(" ) ");

//        Object [] args = new Object[]{mobileNo, msg, pkOrg, 0};
//        getJdbcTemplates().update(sql.toString(), args);
        
        // 把群发改成单发
        final String[] mobilenos = mobileNo.split(";");
        BatchPreparedStatementSetter psss = new BatchPreparedStatementSetter(){
            public int getBatchSize() {
                return mobilenos.length;
            }
            public void setValues(PreparedStatement ps, int arg1) throws SQLException {
                ps.setString(1, mobilenos[arg1]);
                ps.setString(2, msg);
                ps.setString(3, pkOrg);
                ps.setString(4, "0");
                logger.info("发送短信：" + mobilenos[arg1] + "，字数：" + msg.length() +  "，" + msg);
            }
            
        };
        getJdbcTemplates().batchUpdate(sql.toString(), psss);

        return true;
    }
    
    /**
     * 发送短信
     * @param mobileNo
     * @param msg
     */
    @Transactional
    public String sendSms(String mobileNo, Short type, Object [] args, String pkOrg) {
        String error = "";
        if (StringUtils.isEmpty(mobileNo) || !mobileNo.matches("^[0-9;]+$")) {
            throw new BusinessException("号码不正确，请注意格式，多个号码以\";\"号分隔！");
        }
        // TODO:后面要使用缓存，目前使用sql，因为模板会经常变动
        String ssql = " SELECT CONTENT FROM TB_PLATFORM_MOBILE_SALUTATION WHERE MOBILE_USE = ? ";
        
        String content = getJdbcTemplates().queryForObject(ssql,new Object[]{type}, String.class).toString();
        content = String.format(content, args);
        if (content.length() > (MAXMSGCOUNT - 5)){
            content = content.substring(0,(MAXMSGCOUNT - 5));
        }
        logger.info("测试：type：" + type + "\t" + type.compareTo((short)8));
        if (type.compareTo((short)8) == 0){
            content += "【花族网】";
        } else {
            content += "【爱花族】";
        }
        
        List<String> list = MobileKeyWordFilter.getInstance().doFilter(content);
        if(list.size() != 0){
            StringBuffer sb = new StringBuffer();
            sb.append("发送短信失败，您包含以下关键字：");
            for (String s : list) {
                sb.append(s + "，");
            }
            error += sb.toString();
        }
      
        if(error.equals("")){
            StringBuffer sql = new StringBuffer();
            sql.append(" INSERT INTO SMS_WAITSEND( ");
            sql.append("     MOBILENO,    MSGTEXT, ");
            sql.append("     CHARTYPE,    COMLIMIT, ");
            sql.append("     BILLSTATUS,  PK_ORG ");
            sql.append(" ) VALUES( ");
            sql.append("     ?,           ?, ");
            sql.append("     'U',         0, ");
            sql.append("     1,           ? ");
            sql.append(" ) ");
            args = new Object[]{mobileNo, content, pkOrg};
            getJdbcTemplates().update(sql.toString(), args);
        }else{
        	throw new BusinessException( error +"请您联系客服95105386处理");
        }
        return error;
    }

    /**
     * 获取17位订单号
     * @return
     */
    public String getOrderNo(){
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
    }
    
    /**
     * getDateTime
     * 取系统时间(当前服务器时间)
     *
     * @return String
     */
    public static String getCurDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }
    /**
    * getDate
    * 取系统日期(当前服务器时间)
    *
    * @return String
    */
    public static String getCurDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    }
    /**
     * getTs
     * 生成时间戳
     *
     * @return String
     */
    public static String getCurTs() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
    }

    /**
     * getDateTime
     * 取系统时间(当前服务器时间)
     *
     * @return String
     */
    public String getDateTime() {
        return getCurDateTime();
    }

    /**
    * getDate
    * 取系统日期(当前服务器时间)
    *
    * @return String
    */
    public String getDate() {
        return getCurDate();
    }

    /**
     * getTs
     * 生成时间戳
     *
     * @return String
     */
    public String getTs() {
        return getCurTs();
    }

    /**
     * getTimestamp
     * 生成时间戳
     *
     * @return String "yyyy-MM-dd HH:mm:ss.SSS"
     */
    public String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date());
    }
    
    /**
     * 校验时间戳
     * @param sourceBill
     * @param ts
     * @param tableName
     * @param filed
     * @return
     */
    protected Date checkTs(String sourceBill, Date ts, String tableName, String filed) {

        String sql = " SELECT TS, NOW() NOW_DATE FROM " + tableName + " WHERE " + filed + " = ? ";
        Map<String, Object> map = getJdbcTemplates().queryForMap(sql, new Object[]{sourceBill});
        String dateStr = (String) map.get("TS");
        Date date = null;
        try {
            date = SDF.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Assert.notNull(date, "元数据错误，没有TS信息！");
        if (date.after(ts)){
        	logger.info("数据已经被修改，请刷新后重新操作！传入时间：" + SDF.format(ts) + "  查询当前时间：" + SDF.format(date));
            throw new BusinessException("数据已经被修改，请刷新后重新操作！");
        }
        Date now = (Date) map.get("NOW_DATE");
        return now;
    }

}
