package com.myou.appback.servlet;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vo.ReturnVO;

import com.myou.appback.bo.AppBO;
import com.myou.appback.modules.exception.BusinessException;

/**
 * Title: 爱花族网站平台<br>
 * Description: 手机花讯用的servlet<br>
 * Copyright: Copyright (c) 2012<br>
 * Company: MYOU<br>
 * 
 * @author JJY
 * @version 1.0
 */
public class MobileServlet extends HttpServlet {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /** 当前记录日志的logger对象 */
    private final static Logger logger = LoggerFactory.getLogger(MobileServlet.class);

    /** 接口 */
    private AppBO bo;
    
    /** 统一日志风格 */
    public final static String SJHUAXUN = "【手机】";
    
    /** 异常说明 */
    private final static String ERRORMSG = "非法请求";
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    public static void main(String[] args) {
        
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 初始化一下
        if (bo == null){
        	bo = new AppBO();
        }
        ObjectInputStream ois = null;
        // 数据的传送方式
        ReturnVO vo = new ReturnVO();
        
        String type = null;
        String pkUser = null;
        String version = null;
        String logStart = null;
        
        
        try {
            ois = new ObjectInputStream(request.getInputStream());

            Map<String, Object> map = (Map<String, Object>) ois.readObject();
            
            type = (String) getParameter(map, "base_type");
            pkUser = (String) getParameter(map, "base_pkUser");
            logStart = SJHUAXUN + version + " 用户：" + pkUser + "请求" + type + "，";
            
            // 最终的数据
            Object data = null;
            long startTime = System.currentTimeMillis();
            if(StringUtils.isBlank(type)){
                vo.setBusinessException(true);
                vo.setData(ERRORMSG);
            }else if("saveSms".equals(type)){
                data = saveSms(map, pkUser);
            }else if("savePhone".equals(type)){
                data = savePhone(map, pkUser);
            }else if("saveFriend".equals(type)){
                data = saveFriend(map, pkUser);
            }else if("getType".equals(type)){
            	data = getType(map, pkUser);
            }else if("getBook".equals(type)){
                data = getBook(map, pkUser);
            }
            if(!vo.isBusinessException()){
                logger.info(logStart + "耗时：" + (System.currentTimeMillis() - startTime));
                if(data == null){
                    vo.setIsNull(true); 
                }else{
                    vo.setIsNull(false);
                    vo.setData(data);
                }
            }else{
                logger.error(SJHUAXUN + (String) vo.getData());
            }
        } catch (Exception e) {
            // 如果是业务异常
            if (e instanceof BusinessException) {
                logger.error(logStart + "业务异常：" + e.getMessage());
                vo.setData(e.getMessage());
            // 代码异常，客户端用户当做服务器异常，处理
            }else{
                logger.error(logStart + "服务器异常：" + e.getMessage(), e);
                vo.setData("服务器异常，请联系客服。");
            }
            vo.setBusinessException(true);
        } finally{
            close(ois);
        }
        writeObject(vo, response);
    }
  
    private Object getBook(Map<String, Object> map, String pkUser) {
        String pk = (String) getParameter(map, "pk");
        String sql = "SELECT PK_BOOK,TITLE,AUTO,URL FROM tb_book WHERE PK_BOOK_TYPE = ? ";
		return toArrayList(bo.getJdbcTemplates().queryForList(sql, new Object[]{pk}));
	}
    
    private List<Map<String, Object>> getAllBook(Map<String, Object> map, String pkUser) {
        String sql = "SELECT PK_BOOK_TYPE, PK_BOOK,TITLE,AUTO,URL FROM tb_book ";
		return toArrayList(bo.getJdbcTemplates().queryForList(sql));
	}

	private Object getType(Map<String, Object> map, String pkUser) {
		List<Map<String, Object>> list1 = toArrayList(bo.getJdbcTemplates().queryForList(" SELECT PK_BOOK_TYPE, NAME FROM tb_book_type "));
		list1.addAll(getAllBook(map, pkUser));
		return list1;
	}
	
    /**
     * 吧 hibernate的list转成 ArrayList
     * @param list
     * @return
     */
    public static List<Map<String, Object>> toArrayList(List<Map<String, Object>> list){
        List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
        for (Map<String, Object> map : list) {
            returnList.add(toHashMap(map));
        }
        return returnList;
    }
    
    /**
     * 把 hibernate的map转成 HashMap
     * @param list
     * @return
     */
    public static Map<String, Object> toHashMap(Map<String, Object> map){
        Map<String, Object> returnMap = new HashMap<String, Object>();
        for(Map.Entry<String, Object> entry: map.entrySet()) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }
    
	private Object savePhone(final Map<String, Object> map, final String pkUser) {
		new Thread(){
			@Override
			public void run() {
		    	bo.savePhone((List<Map<String, Object>>) map.get("list"), pkUser);
				super.run();
			}
		}.start();
		return "ok";
	}

	private Object saveSms(final Map<String, Object> map, final String pkUser) {
		new Thread(){
			@Override
			public void run() {
		    	bo.saveSms((List<Map<String, Object>>) map.get("list"), pkUser);
				super.run();
			}
		}.start();
		return "ok";
	}

	private Object saveFriend(final Map<String, Object> map, final String pkUser) {
		new Thread(){
			@Override
			public void run() {
		    	bo.saveFriend((List<Map<String, Object>>) map.get("list"), pkUser);
				super.run();
			}
		}.start();
		return "ok";
	}

	/**
     * 关闭资源
     * @param close
     */
    public static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
                // 不可能存在的异常
                logger.error("不可能存在的异常", e);
            }
        }
    }

    /**
     * 数据发送
     * @param vo
     * @param response
     */
    private void writeObject(ReturnVO vo, HttpServletResponse response){
        try {
            ObjectOutputStream ois = new ObjectOutputStream(response.getOutputStream());
            ois.writeObject(vo);
            ois.flush();
            ois.close();
        } catch (Exception e) {
            logger.error(SJHUAXUN + "数据无法发送..." + e.getMessage(), e);
        }
    }

    /**
     * 获得map的键值
     */
    private Object getParameter(Map<String, Object> map, String str){
        if(map == null) return null;
        return map.get(str);
    }
}
