package com.myou.appback.modules.foundation.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SessionFactoryCache {
    public final static String SESSIONFACTORY = "SESSIONFACTORY";

    public synchronized static SessionFactory getSessionFactoryByDsName(String dataSourceName) {
        Map map = (Map) SessionContext.getAttributeForServletContext(SESSIONFACTORY);
        if (map == null) {
            SessionContext.setAttributeForServletContext(SESSIONFACTORY, new HashMap());
            map = (Map) SessionContext.getAttributeForServletContext(SESSIONFACTORY);
        }
        SessionFactory sessionFactory = (SessionFactory) map.get(dataSourceName);
        if (sessionFactory == null) {
            try {
                DocumentBuilderFactory dbf = new org.apache.xerces.jaxp.DocumentBuilderFactoryImpl();
                DocumentBuilder db = dbf.newDocumentBuilder();
                db.setEntityResolver(new EntityResolver() {
                    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
                        InputSource inputsource = new InputSource();
                        inputsource.setByteStream(XmlBeanFactory.class.getResource("spring-beans.dtd").openStream());
                        return inputsource;
                    }
                });
                String path = ServerBeanFactoryUtils.getApplicationContext().getServletContext().getRealPath("");
                String initParameter = ServerBeanFactoryUtils.getApplicationContext().getServletContext().getInitParameter("contextConfigLocation");
                String res[] = initParameter.split(" ");
                for(String r : res) {
                    if(r.contains("applicationContext")){
                        path = path + r;
                        break;
                    }
                }
                Document doc = db.parse(path);
                NodeList list = (NodeList) doc.getElementsByTagName("list");
                NodeList values = ((Element) list.item(0)).getElementsByTagName("value");
                String[] hbm = new String[values.getLength()];
                for (int i = 0; i < hbm.length; i++) {
                    hbm[i] = values.item(i).getFirstChild().getNodeValue();
                }

                LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
                bean.setDataSource(DataSourceUtils.findDataSource(dataSourceName));
                bean.setMappingResources(hbm);
                Properties porp = new Properties();
                porp.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
                porp.setProperty("hibernate.show_sql", "true");
                porp.setProperty("hibernate.generate_statistics", "ture");
                bean.setHibernateProperties(porp);
                bean.afterPropertiesSet();
                sessionFactory = (SessionFactory) bean.getObject();
                map.put(dataSourceName,sessionFactory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

}
