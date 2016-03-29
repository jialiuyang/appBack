package com.myou.appback.servlet;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myou.appback.get.WeizhangTexst;
import com.myou.appback.modules.foundation.util.ImageUtil;
import com.myou.appback.modules.util.ConfigUtils;
import com.myou.appback.modules.util.dom2Xml;
 
public class queryCarXmlServlet extends HttpServlet {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /** 当前记录日志的logger对象 */
    private final Logger logger = LoggerFactory.getLogger(queryCarXmlServlet.class);
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    	response.setContentType("text/html;charset=UTF-8");
    	response.setCharacterEncoding("UTF-8");// 解决乱码问题
    	request.setCharacterEncoding("UTF-8");
    		PrintWriter out= response.getWriter();
        	String hphm  = new String(request.getParameter("carno").getBytes("ISO-8859-1"),"utf-8");//解决get连接中文乱码
        	String classno=request.getParameter("classno");
        	String engineno=request.getParameter("engineno");
        	String cityid=request.getParameter("cityid");
        	String cartype=request.getParameter("cartype");
        	String carInfo = "{hphm="+hphm+"&classno="+classno+"&engineno="+engineno+"&city_id="+cityid+"&car_type="+cartype+"}";
    		String sb =WeizhangTexst. getWeizhangInfoPost(carInfo);
        	JSONObject jsonObject = JSONObject.fromObject(sb);  
        	System.out.println(hphm);
            String xmlstr = new XMLSerializer().write(jsonObject); 
        	 out.print(xmlstr);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
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
