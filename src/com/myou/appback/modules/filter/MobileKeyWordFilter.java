package com.myou.appback.modules.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

/**
 * Title: 爱花族网站平台<br>
 * Description: 关键字过滤<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public class MobileKeyWordFilter {

    private static MobileKeyWordFilter ourInstance = new MobileKeyWordFilter();

    private String[] words = null;

    public static MobileKeyWordFilter getInstance() {
        return ourInstance;
    }

    /**
     * 空构
     */
    private MobileKeyWordFilter() {

        List<String> list = new ArrayList<String>();
        try {

            Properties pro = new Properties();
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("mobile.properties");
            pro.load(new InputStreamReader (in, "UTF-8"));
            
            Enumeration enu = pro.propertyNames();
            while (enu.hasMoreElements()) {
                String s = enu.nextElement().toString();
                if(StringUtils.isNotBlank(s)) list.add(s);
            }
            this.words = list.toArray(new String[list.size()]);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public List<String> doFilter(String str) {
        List<String> list = new ArrayList<String>();
        String[] m = MobileKeyWordFilter.getInstance().getWords();
        for (String s : m) {
            if(str.contains(s)){
                list.add(s);
            }
        }
        return list;
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    /**
     * 半角转全角
     * @param input
     * @return
     */
    public static String ToSBC(String input) {
        // 半角转全角：
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }//65-90 97-122 48-57
            if (c[i] < 127 && !(c[i] >= 65 && c[i] <=127 || c[i] >= 97 && c[i] <=122 || c[i] >= 48 && c[i] <=57)){
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }
    
    public static void main(String [] args){
        System.out.println(MobileKeyWordFilter.getInstance().doFilter("满你系统平台"));
    }
}