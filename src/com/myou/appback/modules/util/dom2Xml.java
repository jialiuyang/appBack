package com.myou.appback.modules.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class dom2Xml {
	/**
	 * 纯文本回复
	 * @param toName
	 * @param fromName
	 * @param content
	 * @return
	 */
	public  String getBackXMLTypeText(String toName, String fromName,
			String content) {
		String returnStr = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String times = format.format(new Date());
		Element rootXML = new Element("xml");
		rootXML.addContent(new Element("ToUserName").setText(toName));
		rootXML.addContent(new Element("FromUserName").setText(fromName));
		rootXML.addContent(new Element("CreateTime").setText(times));
		rootXML.addContent(new Element("MsgType").setText("text"));
		rootXML.addContent(new Element("Content").setText(content));
		Document doc = new Document(rootXML);
		XMLOutputter XMLOut = new XMLOutputter();
		returnStr = XMLOut.outputString(doc);
		System.out.println(returnStr);
		return returnStr;
	}
	
	/**
	 * 回复一个图文、跳转链接
	 * @param toName
	 * @param fromName
	 * @param content
	 * @return
	 */
	public  String getBackXMLTypeTextPicUrl(String toName, String fromName,
			String title,String description,String picUrl,String url) {
//		<xml>
//		<ToUserName><![CDATA[toUser]]></ToUserName>
//		<FromUserName><![CDATA[fromUser]]></FromUserName>
//		<CreateTime>12345678</CreateTime>
//		<MsgType><![CDATA[news]]></MsgType>
//		<ArticleCount>2</ArticleCount>
//		<Articles>
//		<item>
//		<Title><![CDATA[title1]]></Title> 
//		<Description><![CDATA[description1]]></Description>
//		<PicUrl><![CDATA[picurl]]></PicUrl>
//		<Url><![CDATA[url]]></Url>
//		</item>
//		<item>
//		<Title><![CDATA[title]]></Title>
//		<Description><![CDATA[description]]></Description>
//		<PicUrl><![CDATA[picurl]]></PicUrl>
//		<Url><![CDATA[url]]></Url>
//		</item>
//		</Articles>
//		</xml> 
		String returnStr = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		String times = format.format(new Date());
		Element rootXML = new Element("xml");
		rootXML.addContent(new Element("ToUserName").setText(toName));
		rootXML.addContent(new Element("FromUserName").setText(fromName));
		rootXML.addContent(new Element("CreateTime").setText(times));
		rootXML.addContent(new Element("MsgType").setText("news"));
		rootXML.addContent(new Element("ArticleCount").setText("1"));
		Element articles = new Element("Articles");
		Element item = new Element("item");
		item.addContent(new Element("Title").setText(title));
		item.addContent(new Element("Description").setText(description));
		item.addContent(new Element("PicUrl").setText(picUrl));
		item.addContent(new Element("Url").setText(url));
		articles.addContent(item);
		rootXML.addContent(articles);
		Document doc = new Document(rootXML);
		XMLOutputter XMLOut = new XMLOutputter();
		returnStr = XMLOut.outputString(doc);
		return returnStr;
	}
	
	  
}
