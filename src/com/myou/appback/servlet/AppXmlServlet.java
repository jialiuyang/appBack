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
 
public class AppXmlServlet extends HttpServlet {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /** 当前记录日志的logger对象 */
    private final Logger logger = LoggerFactory.getLogger(AppXmlServlet.class);
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//    	response.setContentType("text/html;charset=UTF-8");
    	PrintWriter out= response.getWriter();
   	 out.print(new dom2Xml().getBackXMLTypeText("贾鎏洋", "from贾鎏洋", "测"));
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
    }
 
}
