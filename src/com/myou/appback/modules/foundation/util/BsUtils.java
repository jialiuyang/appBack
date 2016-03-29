package com.myou.appback.modules.foundation.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
 


/**
 * Title: 爱花族网站平台<br>
 * Description: myou后台工具类<br>
 * Copyright: Copyright (c) 2011<br>
 * Company: MYOU<br>
 * 
 * @author JJY
 * @version 1.0
 */
public class BsUtils {

    /**
     * 根据对象ID集合,整理合并集合.
     * 
     * http请求发送变更后的子对象id列表时，hibernate不适合删除原来子对象集合再创建一个全新的集合 需采用以下整合的算法：
     * 在源集合中删除不在ID集合中的元素,创建在ID集合中的元素并对其ID属性赋值并添加到源集合中.
     * 
     * @param collection
     *            源对象集合
     * @param checkedIds
     *            目标集合
     * @param idName
     *            对象中ID的属性名
     */
    public static <ID> void mergeByCheckedIds(Collection<ID> collection, Collection<ID> checkedId, String idName
            ) throws Exception {
        mergeByCheckedIds(collection, checkedId, new String[]{idName});
    }
    
    
    /**
     * 根据对象ID集合,整理合并集合.
     * 
     * http请求发送变更后的子对象id列表时，hibernate不适合删除原来子对象集合再创建一个全新的集合
     * 需采用以下整合的算法：
     * 在源集合中删除不在ID集合中的元素,创建在ID集合中的元素并对其ID属性赋值并添加到源集合中.
     * 
     * @param collection 源对象集合
     * @param checkedIds  目标集合
     * @param idName 对象中ID的属性名 
     */
    public static <ID> void mergeByCheckedIds(Collection<ID> collection, Collection<ID> checkedId, String[] idName
            ) throws Exception {
        
        if (checkedId == null) {
            collection.clear();
            return;
        }
        
        Set<String> checkedIds = new HashSet<String>();
        Map<String, ID> maps = new HashMap<String, ID>();
        for (ID args : checkedId) {
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < idName.length; i++) {
                str.append(PropertyUtils.getProperty(args, idName[i]).toString()).append("||");
            }
            checkedIds.add(str.toString());
            maps.put(str.toString(), args);
        }
        
        Iterator<ID> it = collection.iterator();
        
        while (it.hasNext()) {
            ID obj = it.next();
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < idName.length; i++) {
                str.append(PropertyUtils.getProperty(obj, idName[i]).toString()).append("||");
            }
            if (checkedIds.contains(str.toString())) {
                checkedIds.remove(str.toString());
            } else {
                it.remove();
            }
        }
        
        for (String id : checkedIds) {
            ID obj = maps.get(id);
            collection.add(obj);
        }
        
    }
    
}
