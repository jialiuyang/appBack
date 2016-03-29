package com.myou.appback.modules.util;

import java.util.Properties;

public class SystemPropertyUtil {

    public void setPropertys(Properties props) {
        String svrname = get("ServerName");
        if (svrname != null) {
            props.put("ServerName", svrname);
        }
        System.getProperties().putAll(props);
    }

    public static String get(String key) {
        return (String) System.getProperty(key);
    }

    public static void set(String key, String value) {
        System.setProperty(key, value);
    }
    
    public static void main(String[] args) {
        String strHttpsport = SystemPropertyUtil.get("httpsport");
        String strHttpport = SystemPropertyUtil.get("httpport");
        System.out.println(strHttpsport);
        System.out.println(strHttpport);
        System.out.println(args[0]);
        System.out.println(args[1]);
    }
}
