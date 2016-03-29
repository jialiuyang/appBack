package com.myou.appback.modules.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @description：版本号<br>
 * @author： jiujiya
 * @update： 2014-12
 * @version： 1.0
 */
@Retention(RetentionPolicy.RUNTIME) 
public @interface Version {

	/**
	 * 版本号的名字
	 */
	String name();
	

	/**
	 * 版本号的编号，如果高版本里面没有这个方法，自动寻找低版本里的方法
	 */
	int code();
}
