package com.myou.appback.method;

import java.util.HashMap;
import java.util.Map;
import org.restlet.data.Form;

/**
 * @description：json具体的方法基础类
 * @author： 
 * @update： 2014-7
 * @version： 1.0
 */
public class BaseMethod {
	
	/**
	 * 获得属性
	 * @param form
	 * @param mstr
	 * @return
	 */
	public String getFirstValue(Form form, String mstr){
		String str = form.getFirstValue(mstr);
		return str;
	}
	
	public Map<String, Object> errorRet(String error){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 1);
		map.put("error", error);
		return map;
	}

}
