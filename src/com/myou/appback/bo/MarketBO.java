package com.myou.appback.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import com.myou.appback.modules.base.BaseBusinessBO;
import com.myou.appback.modules.util.SpringContext;

 
public class MarketBO extends BaseBusinessBO{
    
    /**
     * getJdbcTemplate
     * 取jdbc模板
     *
     * @return JdbcTemplate
     */
    public static JdbcTemplate getJdbcTemplate(){
        return new JdbcTemplate((DataSource)SpringContext.getBean("marketDataSource"));
    }

    /**
     * getJdbcTemplate
     * 取jdbc模板
     *
     * @return JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplates(){
        return new JdbcTemplate((DataSource)SpringContext.getBean("marketDataSource"));
    }

	/**
	 * @param type
	 * @param typeType
	 * @param backerType
	 * @param search
	 * @param pageNo
	 * @param pageNum
	 * @return
	 */
	public List<Map<String, Object>> getAppList(String type, String typeType, String backerType, String search, String pageNo, String pageNum) {
        // 请求数据 type(大类型)  1.首页推荐 2.首页最新 3.应用 4.游戏 5.专题 6.搜索
		//(中类型)  0.此分类不需要 1.热门 2.日排行 3.最新
//	     backerType(后台动态类型) 比如为type=3，backerType=2001(手机工具的id) 查询的就是手机工具下的app
//       search      搜索的关键字        
//       pageNo(当前页数)
//       pageNum(页面显示的最多数量，-1表示不分页)
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = " SELECT B.aid id, B.title name, B.picurl icon, B.levels level, A.softurl downUrl  FROM qb_article_content_101 A ";
		sql += " INNER JOIN qb_article B ON A.aid = B.aid ";
//		if(type.equals("1") || type.equals("2")){
			sql += " ORDER BY A.id DESC LIMIT 0, 20 ";
			list = getJdbcTemplate().queryForList(sql);
//		}
		for (Map<String, Object> map : list) {
			map.put("icon", "http://www.tallapp.com/upload_files/" + map.get("icon"));
		}
		return list;
	}

	public List<Map<String, Object>> getAppTypeList(String type) {
		String sql = "SELECT name, logo FROM qb_sort WHERE fup = ?";
		String fup = "12";
		if(type.equals("41")){
			fup = "26";
		}
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql, new Object[]{fup});
		for (Map<String, Object> map : list) {
			map.put("icon", "http://www.tallapp.com/upload_files/" + map.get("logo"));
		}
		return list;
	}
}
