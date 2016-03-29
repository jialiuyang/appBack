package com.myou.appback.resource;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description：基础resource<br>
 * @author： jiujiya
 * @update： 2014-6
 * @version： 1.0
 */
public class BaseResource extends ServerResource{

	/** 日志对象 */
	protected Logger logger = LoggerFactory.getLogger(BaseResource.class);

	@SuppressWarnings("unchecked")
	public JsonRepresentation getJson(Object obj){
		if(obj instanceof List<?>){
			List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
			return getJson(list);
		}else if(obj instanceof Map){
			Map<String, Object> map = (Map<String, Object>) obj;
			return getJson(map);
		}
		return null;
	}
	
	public String getFirstValue(Form form, String mstr){
		String restr = null;
		String str = form.getFirstValue(mstr);
		if(str == null) return restr;
		try {
			restr = new String(str.getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			restr = str;
		} 
		return restr;
	}
	
	public JsonRepresentation getJson(List<Map<String, Object>> list){
		JSONArray returnObj = new JSONArray(list);
		return new JsonRepresentation(returnObj);
	}
	
	public JsonRepresentation getJson(Map<String, Object> map){
		JSONObject returnObj = new JSONObject(map);
		return new JsonRepresentation(returnObj);
	}
	
	
}
