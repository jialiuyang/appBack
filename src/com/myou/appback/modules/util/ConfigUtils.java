package com.myou.appback.modules.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Title: 爱花族网站平台<br>
 * Description: 配置文件读取类<br>
 * Copyright: Copyright (c) 2011<br>
 * Company: MYOU<br>
 * 
 * @author JJY
 * @version 1.0
 */
public class ConfigUtils {

    /** 当前记录日志的logger对象 */
    private final static Logger logger = LoggerFactory.getLogger(ConfigUtils.class);
    
    private final static Map<String, Object> congfigMap = new HashMap<String, Object>();

    /**
     * 初始化
     */
    public void initConfig(){
        //获得配置文件
        try {
            Properties properties = new Properties();
            InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties");
            properties.load(new InputStreamReader (in, "UTF-8"));
            for (Entry<Object, Object> entry : properties.entrySet()) {
                congfigMap.put( entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (Exception e) {
            logger.error("初始化配置文件失败", e);
        }
    }
    
    static{
        new ConfigUtils().initConfig();
    }
    
    public static void main(String[] args) {
        System.out.println("key = " + getBdDirPath());
    }
    
    /**
     * @return
     */
    public static String getBdDirPath(){
        return (String) congfigMap.get("bdDirPath");
    }
    
    /**
     * @return
     */
    public static String getUrlDirPath(){
        return (String) congfigMap.get("urlDirPath");
    }
    
    public static String getValue(String key){
    	return (String) congfigMap.get(key);
    }
}
