package com.myou.appback.resource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.lang.StringUtils;
import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

import com.myou.appback.modules.annotation.NotNull;
import com.myou.appback.modules.annotation.Version;
import com.myou.appback.modules.base.MethodBean;
import com.myou.appback.modules.exception.BusinessException;
import com.myou.appback.modules.util.ClassUtil;

/**
 * @description：控制spring的bean，1.0版本的接口
 * @author： jiujiya
 * @update： 2014-6
 * @version： 1.0
 */
public class YunResource extends BaseResource {

	// json请求的名字
	private String type;

	// json请求的版本号
	private String version;
	
	private static List<MethodBean> methodNames;
	
	static{
		methodNames = new ArrayList<MethodBean>();
		List<Class<?>> classes = ClassUtil.getClasses("com.myou.appback.method");  
        for (Class<?> clazz :classes) {
        	Version annotation = clazz.getAnnotation(Version.class);
        	if(annotation != null){
        		String versionName = annotation.name();
        		int versionCode = annotation.code();
        		for(Method method : clazz.getMethods()){
        			methodNames.add(new MethodBean(method.getName(), versionName, clazz, versionCode));
        		}
        	}
        }
	}
	
	private static MethodBean getMethodBean(String methodName, String version){
		MethodBean methodBean = null;
		int methodVersionCode = Integer.MAX_VALUE;
		
		for(MethodBean bean : methodNames){
			if(bean.getName().equals(methodName)){
				// 如果该方法是当前版本，直接return
				if(bean.getVersion().equals(version)){
					return bean;
				}
				// 如果该版本小于上一个版本，需要考虑向下兼容
				if(bean.getVersionCode() < methodVersionCode){
					methodBean = bean;
					methodVersionCode = bean.getVersionCode();
				}
			}
		}
		
		return methodBean;
	}

	@Override
	protected void doInit() throws ResourceException {
		type = (String) getRequestAttributes().get("type");
		version = (String) getRequestAttributes().get("version");
		logger.info("version=" + version);
	}

	@Get
	public Representation get(Representation entity) {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		return runJob(form);
	}

	@Post
	public Representation post(Representation entity) {
		Form form = new Form(entity);
		return runJob(form);
	}
	
	public Representation runJob(Form form) {
		Representation returnRep = null;
		String methodName = null;
		Object returnObj = null;
		try {
			System.out.println(type+"类型.....");
			methodName = type.substring(0, type.length() - 5);
			MethodBean methodBean = getMethodBean(methodName, version);
			if(methodBean == null){
				return getJson(ertError("1002", "没有定义该接口：" + type));
			}

			// 反射调用相关方法
			Class<?> clazz = methodBean.getClazz();
			
			Method method = clazz.getDeclaredMethod(methodName, Form.class);
			
			//处理请求的数据不能为空的情况
			NotNull notNull = method.getAnnotation(NotNull.class);
			if(notNull != null){
				Map<String, Object> isNull = isNull(form, notNull.args());
				if (isNull != null) {
					return getJson(isNull);
				}
			}
			  returnObj = method.invoke(clazz.newInstance(), form);
			// 统一返回json
			if (returnObj != null) {
				returnRep = getJson(returnObj);
			}else {
				returnRep = getJson(ertError("1003", "未知异常，返回数据为空"));
			}
		} catch (Exception e) {
			if(e.getCause() instanceof BusinessException){
				returnRep = getJson(ertError("1004", e.getCause().getMessage()));
			}else{
				String error = type + "接口请求失败……" + e.getMessage();
				logger.error(error, e);
				returnRep = getJson(ertError("1001", error));
			}
		}
		try {
			String uid = getFirstValue(form, "uid");
			String ip = getClientInfo().getAddress();
			String source = form.getValuesMap().toString();
			String revert =  returnObj.toString();
			if(null!=returnRep ){
				revert = returnRep.getText();
			}
					
			logger.info("调用[" + version + "]方法" + methodName + "，传入参数：" + source + "，ip=" + ip + "，返回值：" + revert);
			// 保存日志记录
			if(StringUtils.isNotBlank(uid) && uid.length() == 20){
//				new AppMemberBO().addLog(uid, methodName, ip, source, revert);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return returnRep;
	}
	
	public Map<String, Object> ertError(String code, String error){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ret", 1);
		map.put("code", code);
		map.put("error", error);
		return map;
	}

	/**
	 * 判断是否为空
	 * @param form
	 * @param args
	 * @return
	 */
	private Map<String, Object> isNull(Form form, String[] args) {
		String error = "";
		for (String string : args) {
			String obj = getFirstValue(form, string);
			if (obj == null) {
				error += string + " ";
			}
		}
		if (StringUtils.isNotBlank(error)) {
			return ertError("1000", "no args " + error);
		}
		return null;
	}
	
	
}
