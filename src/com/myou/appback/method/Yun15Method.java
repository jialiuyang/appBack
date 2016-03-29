package com.myou.appback.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.restlet.data.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myou.appback.modules.annotation.Version;
import com.myou.appback.modules.util.dom2Xml;

/**
 * @description：json具体的方法
 * @author： 
 * @update： 2014-7
 * @version： 1.0
 */
@Version(name="app1.5", code = 10)
public class Yun15Method extends BaseMethod {

	/** 日志对象 */
	protected Logger logger = LoggerFactory.getLogger(Yun15Method.class);
 
	 
	public  Map<String, Object> getTest(Form form){
		 Map<String,Object> map= new HashMap<String, Object>();
		 map.put("ret", 0);
		 map.put("test", "版本兼容测试");
		return map;
	}
 
	public   Map<String,Object>  getEasyJson(Form form){
		 Map<String,Object> returnMap= new HashMap<String, Object>();
		List<Map<String,Object>> list= new  ArrayList<Map<String,Object>>();
		 Map<String,Object> map= new HashMap<String, Object>();
		 map.put("name", 123);
		 map.put("picUrl", "www.baidu.com");
		 list.add(map);
		 list.add(map);
		 list.add(map);
		 returnMap.put("ret", 0);
		 returnMap.put("test", "ok");
		 returnMap.put("data", list);
		 for(Entry<String, Object> entry : map.entrySet()){
	           String strkey = entry.getKey();
	           Object strval = entry.getValue();
	       }
		 return returnMap;
	}
 
}
