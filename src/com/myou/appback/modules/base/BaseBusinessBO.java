package com.myou.appback.modules.base;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.orm.hibernate3.HibernateCallback;

import com.myou.appback.modules.exception.BusinessException;
import com.myou.appback.modules.foundation.util.DataSourceUtils;
import com.myou.appback.modules.util.ConfigUtils;
import com.myou.appback.modules.util.GeneralOperatUtils;
import com.myou.appback.modules.util.SystemPropertyUtil;

/**
 * <p>Title: 业务对象基类</p>
 *
 * <p>Description:完成共有功能 </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company:eprk </p>
 */
public abstract class BaseBusinessBO extends GeneralOperatUtils implements Business {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * query
     * 使用hibernate模版进行查询
     *
     * @param hql String
     * @return List
     */
    public List<?> query(String hql) {

        hql = repairSql(hql);
        hql = " select distinct vo " + hql;
        
        return null;
//        return getHibernateTemplate().find(hql);
    }
    
    protected Class getQueryClass(){
        return null;
    }

    protected String repairSql(String hql){
        String temp = hql.toUpperCase();
        if(temp.indexOf(" ORDER ") == -1
            && temp.startsWith("FROM")
            && temp.indexOf(" VO") > 0
            ){
            return  hql +  " order by vo.id";
        }
        return hql;
    }

    /**
     * 获得总条数 和 要查询的 数据
     * @param clazz
     * @param countHql
     * @param queryHql
     * @param first
     * @param max
     * @return
     */
    public Object[] queryListAndCount(Class<?> clazz, String countHql,final String queryHql, final int first, final int max){
        List<Object> list = pageQuery(queryHql, first, max);
        return new Object[]{queryCount(clazz, countHql), list};
    }
    
    /**
     * 查询某个对象的个数
     * @param clazz
     * @param hql
     * @return
     */
    public int queryCount(Class clazz, String hql) {

        String sql = repairSql(hql);
        if(sql.toLowerCase().startsWith("select ")){
            sql = "select count(*) " + hql.substring(hql.toLowerCase().indexOf("from"));
        }
        else{
            sql = "select count(*) " + hql;
        }
//        if(sql.toLowerCase().indexOf("join") > 0){
//            sql = sql.substring(0,sql.toLowerCase().indexOf("join"))
//                  + sql.substring(sql.toLowerCase().indexOf("where"));
//        }

//        List list = getHibernateTemplate().find(sql);
        return 0;
//        if(list.size() == 0){
//            return 0;
//        }
////        checkList(list);
//        return ((Long) list.get(0)).intValue();
    }

    /**
     * pageQuery
     * 通过起始行数进行分页查询
     *
     * @param hql String
     * @param first int
     * @param max int
     * @return List
     */
    public List<Object> pageQuery(final String hql, final int first, final int max) {
//        List list =  (List) getHibernateTemplate().executeFind(new HibernateCallback() {
//            public Object doInHibernate(Session session) throws HibernateException {
//                Query queryObject = session.createQuery(repairSql(hql));
//                queryObject.setFirstResult(first);
//                queryObject.setMaxResults(max);
//                return queryObject.list();
//            }
//        });
//        return list;
    	return null;
    }
    
    /**
     * pageQuery
     * 通过起始行数进行分页查询
     *
     * @param hql String
     * @param first int
     * @param max int
     * @return List
     */
    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> pageQueryBySql(final String sqlWhere, final int first, final int max) {
        String sql = "";
        return getJdbcTemplate().queryForList(sql);
    }
    
    /**
     * load
     * 通过hibernate模版加载对象`
     *
     * @param clazz Class
     * @param pk String
     * @return Object
     */
    public Object load(Class clazz, String pk) {
        // TODO:为了单元测试先删掉。这个接口不应该被暴露出来。
        // checkClass(clazz);
        Object obj = getHibernateTemplate().get(clazz, pk);
        getHibernateTemplate().flush();
        getHibernateTemplate().clear();
        return obj;
    }

    /**
     * genCode
     * 取编码
     *
     * @param tablename String
     * @param col String
     * @param length int
     * @return String
     */
    protected String genCode(final String tablename, final String col, final int length) {
        String sql = "select trim(max(" + col + ")) from " + tablename;
        String max = (String) getJdbcTemplate().queryForObject(sql, String.class);
        int imax = (max == null ? 1 : Integer.parseInt(max) + 1);
        String pattern = "";
        for (int i = 0; i < length; i++) {
            pattern = pattern + "0";
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return df.format(imax);

    }

    /**
     * getWorkday
     * 取工作日
     *
     * @return String
     */
    public String getWorkday() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
    }

    /**
     *
     * 调用存储过程统一用这个方法
     */
    public Object executePRC(String callString,CallableStatementCallback callback) {
        return getJdbcTemplate().execute(callString, callback);
    }

    /**
     * Desc:根据不同规则的生成编码
     * 
     * @author donghai
     * @param tablename
     *            表名
     * @param col
     *            编码字段名
     * @param length
     *            编码字段长度
     * @param prefix
     *            编码起始数字
     * @return Date:2009-5-7
     */
    protected String genSerialNum(final String tablename, final String col, final int length, final String prefix,
            String where) {
        String where1 = StringUtils.isBlank(where) ? "1=1" : where;
        String sql = "select trim(" + col + ") " + col + " from " + tablename + " t1 where t1." + col
                + " = (select trim(max(" + col + ")) from " + tablename + " where " + where1 + ") for update";
        List list = getJdbcTemplate().queryForList(sql);
        int imax = 1;
        // 判断是否有记录
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            String max = (String) map.get(col);
            imax = Integer.parseInt(max.substring(prefix.length(), max.length())) + 1;
        } else {
            // 当表为空是需要锁整个表
            getJdbcTemplate().update("LOCK TABLE " + tablename + " IN EXCLUSIVE MODE");
            // 然后从表中取数据（两个事物同时进行，需要重新从表中取数）
            list = getJdbcTemplate().queryForList(sql);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                String max = (String) map.get(col);
                imax = Integer.parseInt(max.substring(prefix.length(), max.length())) + 1;
            }
        }
        // 实际填充长度
        int actlength = length - prefix.length();
        String pattern = "";
        for (int i = 0; i < actlength; i++) {
            pattern = pattern + "0";
        }
        DecimalFormat df = new DecimalFormat(pattern);
        return prefix + df.format(imax);
    }

    /** 调用指定数据源中的存储过程
     * 
     * @param callString
     * @param callback
     * @return
     */
    public Object executePRC(String dataSourceName, String callString,CallableStatementCallback callback) {
        
        DataSource dataSource = DataSourceUtils.findDataSource(dataSourceName);
        return getJdbcTemplate(dataSource).execute(callString, callback);
    }

    /**
     * 统计sm_lock中锁的数量
     * @param where 查询条件，可以为空
     * @param args  查询参数，和条件对应
     * @return 锁的数量
     */
    protected int lockCount(String where, Object []args) {
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM SM_LOCK");
        if (StringUtils.isNotBlank(where)) {
            sql.append(" WHERE ").append(where);
        }
        int count = 0;
        if (args != null) {
            count = getJdbcTemplate().queryForInt(sql.toString(), args);
        } else {
            count = getJdbcTemplate().queryForInt(sql.toString());
        }
        return count;
    }

    /**
     * 校验花店余额（包括花族网余额，00000000000000000004）
     * 同时校验了TS
     * @param pkOrg
     * @param occurCash
     */
    protected final void checkOrgMoney(String pkOrg, BigDecimal occurCash, boolean isOrder){

        if (occurCash == null || pkOrg == null || occurCash.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("发生金额必须大于0");
        }
        String sql;
        if (isOrder && pkOrg.equals("00000000000000000004")){
            sql = " SELECT FROZEN_BALANCE FROM TB_ORG_ACCOUNT WHERE PK_ORG = ? ";
        } else {
            sql = " SELECT CASH_BALANCE FROM TB_ORG_ACCOUNT WHERE PK_ORG = ? ";
        }
        BigDecimal cashBalance = (BigDecimal) getJdbcTemplate().queryForObject(sql, new Object[]{pkOrg}, BigDecimal.class);
        if (cashBalance.compareTo(occurCash) < 0){
            throw new BusinessException("余额不足，请先充值");
        }
    }

    /**
     * 校验会员余额
     * 同时校验了TS
     * @param pkOrg
     * @param occurCash
     */
    protected final void checkMemberMoney(String pkMemberAccount, BigDecimal occurCash){

        if (occurCash == null || pkMemberAccount == null || occurCash.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessException("发生金额必须大于0");
        }
        String sql = " SELECT CASH_BALANCE FROM TB_MEMBER_ACCOUNT WHERE PK_MEMBER_ACCOUNT = ? ";
        BigDecimal cashBalance = (BigDecimal) getJdbcTemplate().queryForObject(sql, new Object[]{pkMemberAccount}, BigDecimal.class);
        if (cashBalance.compareTo(occurCash) < 0){
            throw new BusinessException("余额不足，请先充值");
        }
    }

   
    /**
     * 上传文件 （在uploadphoto 文件夹中 传图片）
     * @param  body 图片
     * @param  suffix 图片类型
     */
    public String upload(byte [] body, String suffix){
        return upload(body, suffix, "");
    }

    /**
     * 上传文件(同步)
     * @param  body 文件
     * @param  suffix 文件类型
     * @param  path 要上传文件的文件夹 例如：up/ 就是 在uploadphoto 文件夹中 的up 文件中 传文件
     */
    public String upload(byte [] body, String suffix, String path){
        return upload(body, suffix, path, true);
    }
    
    /**
     * 上传文件
     * @param  body 文件
     * @param  suffix 文件类型
     * @param  path 要上传文件的文件夹 例如：up/ 就是 在uploadphoto 文件夹中 的up 文件中 传文件
     * @param  isTongbu 是否同步
     */
    public String upload(byte [] body, String suffix, String path, boolean isTongbu){

        String str = ConfigUtils.getBdDirPath();
        File root = new File(str);
        File file;
        String name = null;
        String finalPath = "";
    	String year = new SimpleDateFormat("yyyy").format(new Date());
        String month = new SimpleDateFormat("MM").format(new Date());
        finalPath = path + year + "/" + month;
        file = new File(root, finalPath);
        if (!file.exists()){
            file.mkdirs();
        }
        name = System.currentTimeMillis() + suffix;
        file = new File(file, name);
        BufferedOutputStream os;
        String filePath=file.getName();
        String imgType = filePath.substring(filePath.lastIndexOf("."),filePath.length()).toUpperCase();
        //后台再次校验
        if(!imgType.equals(".JPG") && !imgType.equals(".PNG") && !imgType.equals(".GIF")){
        	throw new BusinessException("只能上传png，jpg，gif格式的图片！");
        }
        if(file.length()>1024*1024){
        	throw new BusinessException("图片不能大于1M");
        }
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            os.write(body);
            os.flush();
            os.close();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BusinessException("上传文件失败：" + e.getMessage());
        }
        String returnStr = "/uploadphoto/" + finalPath + "/" + name;
        logger.info("上传文件成功:" + returnStr);
        return returnStr;
    }
    
}
