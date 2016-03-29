package com.myou.appback.method;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.restlet.data.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myou.appback.bo.AppBO;
import com.myou.appback.get.WeizhangTexst;
import com.myou.appback.modules.annotation.NotNull;
import com.myou.appback.modules.annotation.Version;

/**
 * @description：json具体的方法
 * @author： jiujiya
 * @update： 2014-7
 * @version： 1.0
 */
@Version(name="app", code = 10)
public class YunMethod extends BaseMethod {

	/** 日志对象 */
	protected Logger logger = LoggerFactory.getLogger(YunMethod.class);
	 
	public  Map<String, Object> getTest(Form form){
		 Map<String,Object> map= new HashMap<String, Object>();
		 map.put("ret", 0);
		 map.put("test", "测试");
		return map;
	}
	
	/**用户是否存在
	 * @param form
	 * @return
	 */
	@NotNull(args = { "phone"}) 
	public  Map<String, Object> findUser(Form form){
		String phone = getFirstValue(form, "phone");
		 return new AppBO().findByPhone(phone);
	}
	
	
	/**用户登录
	 * @param form
	 * @return
	 */
	@NotNull(args = { "mobile",  "pwssword" })//不能为空
	public  Map<String, Object> getoLogin(Form form){
		String mobile = getFirstValue(form, "mobile");
		Map<String,Object> map= new HashMap<String, Object>();
		if(true){
			 map.put("ret", 0);
			 map.put("mobile是", mobile);
		}
		return map;
	}

	/**
	 * 查询违章信息
	 * 请求数据："carno","classno","engineno","cityid","cartype" 车牌号、车架号、发动机号、城市id、车型
	 * @param form
	 * @return
	 */
	@NotNull(args={"carno","classno","engineno","cityid","cartype"} )
	public Map<String,Object> 	  queryCar(Form form){
		String carInfo = "{hphm="+getFirstValue(form, "carno")+"&classno="+getFirstValue(form, "classno")+"&engineno="+getFirstValue(form, "engineno")+"&city_id="+getFirstValue(form, "cityid")+"&car_type="+getFirstValue(form, "cartype")+"}";
		String sb =WeizhangTexst. getWeizhangInfoPost(carInfo);
		return WeizhangTexst.getMap(sb);										
	}	
}
