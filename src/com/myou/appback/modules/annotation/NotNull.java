package com.myou.appback.modules.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @description：不能为空的注解类<br>
 * @author： jiujiya
 * @update： 2014-7
 * @version： 1.0
 */
@Retention(RetentionPolicy.RUNTIME) 
public @interface NotNull {

	/**
	 * 参数
	 */
	String[] args();
}
