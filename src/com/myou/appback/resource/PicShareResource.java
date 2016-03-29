package com.myou.appback.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myou.appback.bo.AppBO;

public class PicShareResource extends BaseResource{

	/** 日志对象 */
	protected Logger logger = LoggerFactory.getLogger(BaseResource.class);
	
	private String type;
	
	@Override
    protected void doInit() throws ResourceException {
		type = (String) getRequestAttributes().get("type");
		
	}
	
	@Post
	public Representation post(Representation entity){
		Form form = new Form(entity);
		AppBO bo = new AppBO();
		Map<String, Object> map = new HashMap<String, Object>();
		if(type.equals("login")){
			String name = getFirstValue(form,"name");
			String password = getFirstValue(form,"password");
			logger.info("用户尝试登陆：" + name);
			System.out.println(name);
			String sql = " SELECT PK_MEMBER, NICK_NAME, LOGO_PATH from tb_member WHERE NAME = ? AND PASSWORD = ? ";
			List<Map<String, Object>> list = bo.getJdbcTemplates().queryForList(sql, new Object[]{name, password});
			if(list.isEmpty()){
				map.put("return", "1");
			}else{
				map.put("return", "0");
				map.putAll(list.get(0));
			}
			return getJson(map);
		}else if(type.equals("register")){
			String name = getFirstValue(form,"name");
			String password = getFirstValue(form,"password");
			String truename = getFirstValue(form,"truename");
			String countsql = "SELECT COUNT(1) FROM tb_member WHERE NAME = ?";
			if(bo.getJdbcTemplates().queryForInt(countsql, new Object[]{name}) == 0){
				String oid = bo.getOID();
				String sql = " INSERT INTO tb_member(PK_MEMBER, NAME, NICK_NAME, PASSWORD, TS) VALUES (?, ?, ?, ?, NOW()) ";
				bo.getJdbcTemplates().update(sql, new Object[]{oid, name, truename, password});
				map.put("return", "0");
				map.put("PK_MEMBER", oid);
				map.put("NICK_NAME", truename);
			}else{
				map.put("return", "1");
				map.put("errorMsg", "用户名已存在");
			}
			return getJson(map);
		}else if(type.equals("register")){
			String name = getFirstValue(form,"name");
			String password = getFirstValue(form,"password");
			String truename = getFirstValue(form,"truename");
			String countsql = "SELECT COUNT(1) FROM tb_member WHERE NAME = ?";
			if(bo.getJdbcTemplates().queryForInt(countsql, new Object[]{name}) == 0){
				String oid = bo.getOID();
				String sql = " INSERT INTO tb_member(PK_MEMBER, NAME, NICK_NAME, PASSWORD, TS) VALUES (?, ?, ?, ?, NOW()) ";
				bo.getJdbcTemplates().update(sql, new Object[]{oid, name, truename, password});
				map.put("return", "0");
				map.put("PK_MEMBER", oid);
				map.put("NICK_NAME", truename);
			}else{
				map.put("return", "1");
				map.put("errorMsg", "用户名已存在");
			}
			return getJson(map);
		}else if(type.equals("uphead")){
			String urlPath = getFirstValue(form,"url");
			String userName = getFirstValue(form,"userName");
			String member = getFirstValue(form,"member");
			String oid = bo.getOID();
			if(StringUtils.isBlank(urlPath)){
				logger.info(userName + " 分享头像失败：" + urlPath);
				map.put("return", "1");
			}else{
				logger.info(userName + " 分享头像成功：" + urlPath);
				String sql = "UPDATE tb_member SET LOGO_PATH = ? WHERE PK_MEMBER = ?";
				bo.getJdbcTemplates().update(sql, new Object[]{urlPath, member});
				String picName = "分享头像";
				if(StringUtils.isNotBlank(userName)){
					picName = userName + picName;
				}
				String isql = "INSERT INTO tb_pic(PK_PIC, PK_MEMBER, NICK_NAME, PIC_PATH, PIC_NAME, PIC_MEMO, TS) VALUES (?,?,?,?,?,?,NOW())";
				bo.getJdbcTemplates().update(isql, new Object[]{oid, member, userName, urlPath, picName, null});
				map.put("return", "0");
			}
			return getJson(map);
		}else if(type.equals("getPic")){
			String imgType = getFirstValue(form,"imgType");
			String member = getFirstValue(form,"member");
			
			List<Map<String, Object>> reList = new ArrayList<Map<String,Object>>();
			String sql = " SELECT A.PK_PIC, A.PK_MEMBER, A.NICK_NAME, A.PIC_PATH, A.PIC_NAME, A.PIC_MEMO, A.TS FROM tb_pic A ";
			if(imgType.equals("all")){
				int pageNo = Integer.parseInt(getFirstValue(form,"pageNo"));
				int pageNum = Integer.parseInt(getFirstValue(form,"pageNum"));
				sql = sql + " ORDER BY A.TS DESC LIMIT ?, ? ";
				reList = bo.getJdbcTemplates().queryForList(sql, new Object[]{(pageNo - 1) * pageNum, pageNo * pageNum});
			}else if(imgType.equals("myAdd")){
				sql = sql + " WHERE A.PK_MEMBER = ? ";
				reList = bo.getJdbcTemplates().queryForList(sql, new Object[]{member});
			}else if(imgType.equals("mySc")){
				sql = sql + " INNER JOIN tb_pic_collect B ON A.PK_PIC = B.PK_PIC WHERE B.PK_MEMBER = ? ";
				reList = bo.getJdbcTemplates().queryForList(sql, new Object[]{member});
			}
			return getJson(reList);
		}else if(type.equals("about")){
			String sql = "SELECT CONTENT, PHONE, WEB_URL FROM tb_about";
			map = bo.getJdbcTemplates().queryForMap(sql);
			return getJson(map);
		}else if(type.equals("comment")){
			map.put("return", "0");
			String content = getFirstValue(form,"content");
			String member = getFirstValue(form,"member");
			logger.info(member + "反馈：" + content);
			return getJson(map);
		}else if(type.equals("picSc")){
			String PK_PIC = getFirstValue(form,"PK_PIC");
			String member = getFirstValue(form,"member");
			String sql = "INSERT INTO tb_pic_collect (PK_PIC_COLLECT, PK_MEMBER, PK_PIC, TS) VALUES (?,?,?,NOW())";
			bo.getJdbcTemplates().update(sql, new Object[]{bo.getOID(), member, PK_PIC});
			map.put("return", "0");
			return getJson(map);
		}else if(type.equals("sharePic")){
			String urlPath = getFirstValue(form,"url");
			String picName = getFirstValue(form,"picName");
			String picMemo = getFirstValue(form,"picMemo");
			String userName = getFirstValue(form,"userName");
			String member = getFirstValue(form,"member");
			String oid = bo.getOID();
			if(StringUtils.isBlank(urlPath)){
				logger.info(userName + " 分享图片失败：" + urlPath);
				map.put("return", "1");
			}else{
				logger.info(userName + " 分享图片成功：" + urlPath);
				String sql = "UPDATE tb_member SET LOGO_PATH = ? WHERE PK_MEMBER = ?";
				bo.getJdbcTemplates().update(sql, new Object[]{urlPath, member});
				String isql = "INSERT INTO tb_pic(PK_PIC, PK_MEMBER, NICK_NAME, PIC_PATH, PIC_NAME, PIC_MEMO, TS) VALUES (?,?,?,?,?,?,NOW())";
				bo.getJdbcTemplates().update(isql, new Object[]{oid, member, userName, urlPath, picName, picMemo});
				map.put("return", "0");
			}
			return getJson(map);
		}
		return getJson(map);
	}
	
}
