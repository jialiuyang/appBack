package com.myou.appback.modules.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * 搜索排序工具
 * @author Administrator
 *
 */
public class SearchUtil {
	public List<Map<String, Object> > getMacth(List<Map<String, Object> > list, String keyText) {
		List<Map<String,Object>> newList= new ArrayList<Map<String,Object>>();
		for(int i=0 ;i<list.size();i++){
			String name= list.get(i).get("NAME").toString();
			if(name.equals(keyText)){
				newList.add(list.get(i));
				list.remove(i);				
			}	
		}
		for( int a = 0 ; a<list.size() ;a++){
			newList.add(list.get(a));
		}
		return newList;

	}
}
