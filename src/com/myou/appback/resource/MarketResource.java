package com.myou.appback.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.myou.appback.bo.AppBO;
import com.myou.appback.bo.MarketBO;

public class MarketResource extends BaseResource{

	/** 日志对象 */
	protected Logger logger = LoggerFactory.getLogger(BaseResource.class);
	
	private String type;
	
	@Override
    protected void doInit() throws ResourceException {
		type = (String) getRequestAttributes().get("type");
	}
	
	private Map<String, Object> getPic(String pic){
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("pic", pic);
		data.put("tag", pic);
		return data;
	}
	
	@Post
	public Representation post(Representation entity){
		Form form = new Form(entity);
		MarketBO bo = new MarketBO();
		Map<String, Object> map = new HashMap<String, Object>();
		if(type.equals("ppt")){
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			datas.add(getPic("http://www.tallapp.com/images/v7/201204171722356630.jpg"));
			datas.add(getPic("http://www.tallapp.com/images/v7/201204171724078048.jpg"));
			datas.add(getPic("http://www.tallapp.com/images/v7/201204191932122459.jpg"));
			datas.add(getPic("http://www.tallapp.com/images/v7/201204181749520784.jpg"));
			datas.add(getPic("http://www.tallapp.com/images/v7/201204191932518334.jpg"));
			datas.add(getPic("http://www.tallapp.com/images/v7/201204191933322010.jpg"));
			return getJson(datas);
		}else if(type.equals("appList")){
            // 请求数据 type(大类型)  1.首页推荐 2.首页最新 3.应用 4.游戏 5.专题 6.搜索 31应用分类 41游戏分类
			//(中类型)  0.此分类不需要 1.热门 2.日排行 3.最新
//		     backerType(后台动态类型) 比如为type=3，backerType=2001(手机工具的id) 查询的就是手机工具下的app
//           search      搜索的关键字        
//           pageNo(当前页数)
//           pageNum(页面显示的最多数量，-1表示不分页)
			String type = getFirstValue(form,"type");
			if(type.equals("31") || type.equals("41")){
				List<Map<String, Object>> datas = bo.getAppTypeList(type);
				return getJson(datas);
			}
			String typeType = getFirstValue(form,"typeType");
			String backerType = getFirstValue(form,"backerType");
			String search = getFirstValue(form,"search");
			String pageNo = getFirstValue(form,"pageNo");
			String pageNum = getFirstValue(form,"pageNum");
			List<Map<String, Object>> datas = bo.getAppList(type, typeType, backerType, search, pageNo, pageNum);
			return getJson(datas);
		}
		return getJson(map);
	}
	
}
