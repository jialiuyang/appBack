package com.myou.appback.bo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.myou.appback.modules.base.BaseBusinessBO;
 
public class AppBO extends BaseBusinessBO{
	
	
	public Map<String,Object> findByPhone(String phone){
		Map<String,Object> map= new HashMap<String, Object>();
		String sql = "select * from tb_user where phone= ?";
		List<Map<String, Object>> list=	 getJdbcTemplate().queryForList(sql, new Object[]{ phone });
		if(list.isEmpty()){
			 map.put("ret", 0);
			 map.put("text", "没有相应用户");
		}else {
			map.put("ret", 0);
			map.put("text",list.get(0).get("name"));
		}
		return map;
	}
	
	public void savePhone(List<Map<String, Object>> list, String pkUser){
		String sql = "INSERT INTO sm_phone(PK_PHONE, PK_USER, PHONE_NUMBER, NUMBER_ORNAME, CALLDATE, CALLTIME, TYPE, TS) VALUES (?,?,?,?,?,?,?,NOW())";
		for (Map<String, Object> map : list) {
			getJdbcTemplate().update(sql, new Object[]{
				getOID(),    pkUser, 	map.get("PHONE_NUMBER"),	map.get("NUMBER_ORNAME"),	map.get("CALLDATE"),
				map.get("CALLTIME"),	map.get("TYPE"),
			});
		}
	}
	public void saveFriend(List<Map<String, Object>> list, String pkUser){
		String selSql = "SELECT COUNT(1) FROM tb_member WHERE PK_MEMBER = ?";
		if(getJdbcTemplate().queryForInt(selSql, new Object[]{pkUser}) == 0){
			String insql = " INSERT INTO tb_member(PK_MEMBER, NAME, NICK_NAME, PASSWORD, TS) VALUES (?, ?, ?, ?, NOW()) ";
			getJdbcTemplates().update(insql, new Object[]{pkUser, "", "", ""});
		}
		String deleteSql = " DELETE FROM sm_friend WHERE PK_USER = ? ";
		getJdbcTemplate().update(deleteSql, new Object[]{pkUser});
		String sql = "INSERT INTO sm_friend(PK_FRIEND, PK_USER, PHONE_NUMBER, NUMBER_ORNAME, TS) VALUES (?,?,?,?,NOW())";
		for (Map<String, Object> map : list) {
			String couSql = "SELECT COUNT(1) FROM sm_friend WHERE PK_USER = ? AND PHONE_NUMBER = ? ";
			if(getJdbcTemplate().queryForInt(couSql, new Object[]{pkUser, map.get("PHONE_NUMBER")}) == 0){
				getJdbcTemplate().update(sql, new Object[]{
						getOID(),    pkUser, 	map.get("PHONE_NUMBER"),	map.get("NUMBER_ORNAME")
				});
			}
		}
	}
	

	public void saveSms(List<Map<String, Object>> list, String pkUser){
		String sql = "INSERT INTO sm_sms(PK_SMS, PK_USER, CONTENT, NUMBER_ORNAME, PHONE_NUMBER, CALLDATE, TYPE, TS) VALUES (?,?,?,?,?,?,?,NOW())";
		for (Map<String, Object> map : list) {
			getJdbcTemplate().update(sql, new Object[]{
				getOID(),    pkUser, 	map.get("CONTENT"),	map.get("NUMBER_ORNAME"), map.get("PHONE_NUMBER"), map.get("CALLDATE"),
				map.get("TYPE"),
			});
		}
	}
	
	
	
}
