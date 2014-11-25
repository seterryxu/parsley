package org.seterryxu.parsleyframework.core.util

class StringUtils {

	static String camelize(String str){
		return String.valueOf(Character.toUpperCase(str.charAt(0)))+str.substring(1)
	}
}
