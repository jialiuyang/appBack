package com.myou.appback.servlet;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myou.appback.modules.foundation.util.ImageUtil;
import com.myou.appback.modules.util.ConfigUtils;

/**
 * Title: 爱花族网站平台<br>
 * Description: 上传图片专用<br>
 * Copyright: Copyright (c) 2011<br>
 * Company: MYOU<br>
 * 
 * @author JJY
 * @version 1.0
 */
public class UpServlet extends HttpServlet {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /** 当前记录日志的logger对象 */
    private final Logger logger = LoggerFactory.getLogger(UpServlet.class);
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        ServletInputStream in = request.getInputStream();
        FileOutputStream out = null;

		
        String baseDir = ConfigUtils.getBdDirPath();
        File file = new File(baseDir);
        if (!file.exists()){
            file.mkdirs();
        }
        String suffix = (String) request.getParameter("suffix");
        String name = System.currentTimeMillis() + suffix;
		String urlPath = ConfigUtils.getUrlDirPath() + name;
        file = new File(baseDir + name);
        
        try {
            out = new FileOutputStream(file);
            long startTime = System.currentTimeMillis();
            
            byte [] buffer = new byte[1048576];
            int size = in.read(buffer);
            while (size != -1) {
                out.write(buffer, 0, size);
                size = in.read(buffer);
            }
            logger.info("上传文件成功：" + (baseDir + name) + "耗时：" + (System.currentTimeMillis() - startTime));
            ImageUtil.getFixedIcon(baseDir + name,200,200);

        	int lastLength = urlPath.lastIndexOf(".");   
            String fileType = urlPath.substring(lastLength);  //图片类型
            ObjectOutputStream ois = new ObjectOutputStream(response.getOutputStream());
            ois.writeObject(urlPath +"_4444"  + fileType);
            ois.flush();
            ois.close();
        }catch (Exception e) {
            logger.error("上传文件失败..." + e.getMessage(), e);
        }finally{
            close(in);
            close(out);
        }
    }

    private void close(Closeable out) {
        try {
            if(out != null){
                out.close();
            }
        } catch (IOException e) {
            logger.error("上传文件无法关闭..." + e.getMessage(), e);
        }
    }

}
