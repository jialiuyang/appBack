package com.myou.appback.vo.base;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
/**
 * Title: 企业营销信息管理系统<br>
 * Description: 重写arraySet，hibernate保存子表<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: WINNER<br>
 * 
 * @author cjy
 * @version 1.0
 */
public class ArraySet<T> extends ArrayList<T> implements SortedSet<T>{

    /** 序列号 */
    private static final long serialVersionUID = -27656518239211583L;

    public T first() {
        return this.size()==0?null:this.get(0);
    }

    public T last() {
        return this.size()==0?null:this.get(this.size());
    }

    public Comparator<T> comparator() {
        return null;
    }

    public SortedSet<T> headSet(T toElement) {
        return null;
    }

    public SortedSet<T> tailSet(T fromElement) {
        return null;
    }

    public SortedSet<T> subSet(T fromElement, T toElement) {
        return null;
    }

}