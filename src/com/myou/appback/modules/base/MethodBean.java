package com.myou.appback.modules.base;

/**
 * @description：方法bean<br>
 * @author： jiujiya
 * @update： 2014-12
 * @version： 1.0
 */
public class MethodBean {

	// 方法名
	private String name;
	
	// 版本号
	private String version;
	
	// 版本号的code，如果高版本里面没有这个方法，自动寻找低版本里的方法
	private int versionCode;
	
	// 方法所在的类
	private Class<?> clazz;

	public MethodBean(String name, String version, Class<?> clazz, int versionCode) {
		this.name = name;
		this.version = version;
		this.clazz = clazz;
		this.versionCode = versionCode;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
	
}
