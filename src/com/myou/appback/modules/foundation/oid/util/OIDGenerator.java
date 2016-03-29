package com.myou.appback.modules.foundation.oid.util;


import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Title: 爱花族网站平台<br>
 * Description: 主键生成器<br>
 * Copyright: Copyright (c) 2009<br>
 * Company: MYOU<br>
 * 
 * @author cjy
 * @version 1.0
 */
public class OIDGenerator {

    /** 种子 */
    private static String seed = null;
    /** 自增长 */
    private static final AtomicInteger ATO =  new AtomicInteger(100000000);

    private static Logger logger = LoggerFactory.getLogger(OIDGenerator.class);
    /**
     * hibernate调用
     * @param connection 
     * @return
     */
    public static String getOID(Connection connection) {
 
        if (seed == null){
            initSeed(connection);
        }
        return getOID();
    }

    /**
     * jdbc调用
     * @return
     */
    public static String getOID(JdbcTemplate jdbc) {

        if (seed == null){
            initSeed(jdbc);
        }
        return getOID();
    }

    /**
     * 初始化种子
     * @param jdbc
     */
    private static synchronized void initSeed(JdbcTemplate jdbc) {

        try {
            String sql = " SELECT COUNT(1) FROM SM_OID ";
            int count = jdbc.queryForInt(sql);
            if (count == 0) {
                seed = "10001";
            } else {
                sql = " SELECT MAX(SEED) FROM SM_OID ";
                int max = jdbc.queryForInt(sql);
                seed = String.valueOf(max + 1);
                sql = " DELETE FROM SM_OID ";
                jdbc.update(sql);
            }
            sql = " INSERT INTO SM_OID VALUES (?) ";
            jdbc.update(sql, new Object[] { seed });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            seed = null;
            throw new RuntimeException(e);
        }
    }

    /**
     * 初始化种子
     * @param jdbc
     */
    private static synchronized void initSeed(Connection connection) {
        
        PreparedStatement countPs = null;
        ResultSet countRes = null;
        PreparedStatement maxPs = null;
        ResultSet maxRes = null;
        PreparedStatement trunPs = null;
        PreparedStatement insertPs = null;
        try {
            String sql = " SELECT COUNT(1) FROM SM_OID ";
            countPs = connection.prepareStatement(sql);
            countRes = countPs.executeQuery();
            short count = 0;
            while (countRes.next()){
                count = countRes.getShort(1);
            }
            if (count == 0) {
                seed = "10001";
            } else {
                sql = " SELECT MAX(SEED) FROM SM_OID ";
                maxPs = connection.prepareStatement(sql);
                maxRes = maxPs.executeQuery();
                int max = 10001;
                while (maxRes.next()){
                    max = maxRes.getInt(1);
                }
                seed = String.valueOf(max + 1);
                sql = " DELETE FROM SM_OID ";
                trunPs = connection.prepareStatement(sql);
                trunPs.execute();
            }
            sql = " INSERT INTO SM_OID VALUES (?) ";
            insertPs = connection.prepareStatement(sql);
            insertPs.setString(1, seed);
            insertPs.execute();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            seed = null;
            throw new RuntimeException(e);
        } finally {
            closePre(countPs);
            closePre(maxPs);
            closePre(trunPs);
            closeRes(countRes);
            closeRes(maxRes);
            // 这里的connection不能关闭，否则会出现问题。
        }
    }

    /**
     * 关闭PreparedStatement
     * @param countPs
     */
    private static void closePre(PreparedStatement ps) {
        if (ps != null){
            try {
                ps.close();
            } catch (SQLException e) {
                logger.error("不应该出现的异常", e);
            }
        }
    }

    /**
     * 关闭Res
     * @param countPs
     */
    private static void closeRes(ResultSet res) {
        if (res != null){
            try {
                res.close();
            } catch (SQLException e) {
                logger.error("不应该出现的异常", e);
            }
        }
    }

    /**
     * 获取OID
     * @return
     */
    public static String getOID() {

        Random random = new SecureRandom();
        byte bytes[] = new byte[3];
        random.nextBytes(bytes);

        StringBuffer result = new StringBuffer();
        result.append(seed).append(ATO.incrementAndGet());
        // 转化为16进制字符串
        for (int i = 0; i < bytes.length; i++) {
            byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
            byte b2 = (byte) (bytes[i] & 0x0f);
            if (b1 < 10){
                result.append((char) ('0' + b1));
            } else {
                result.append((char) ('A' + (b1 - 10)));
            }
            if (b2 < 10) {
                result.append((char) ('0' + b2));
            } else {
                result.append((char) ('A' + (b2 - 10)));
            }
        }
        return result.toString();
    }

}