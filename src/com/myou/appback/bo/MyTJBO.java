package com.myou.appback.bo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.barcodelib.barcode.a.a.a.b;
import com.barcodelib.barcode.a.g.m.q;
import com.myou.appback.modules.base.BaseBusinessBO;
import com.myou.appback.modules.exception.BusinessException;
import com.myou.appback.modules.util.SpringContext;

/**
 * Title: 爱花族网站平台<br>
 * Description: app接口数据操作类<br>
 * Copyright: Copyright (c) 2013<br>
 * Company: MYOU<br>
 * 
 * @author jjy
 * @version 1.0
 */
public class MyTJBO extends BaseBusinessBO{

	/** 要验证的uid方法 */
    private final static String[] CHECKUIDS = new String[]{
    		                      "deletePpt", "deleteGood", "deletePerfer", "deleteShop", "deleteBusiness", "addPpt", "addGood", "addPerfer",
    		                      "addBuyGoods", "askForAuthShop", "askForUnAuthShop", "addShop", "addType", "upGood", "upPerfer", "upBusiness",
    		                      "upShop", "askForAuthShop", "askForUnAuthShop", "addShop", "addType", "upGood", "upPerfer", "upBusiness",
    		                      }; 
    /**
     * getJdbcTemplate
     * 取jdbc模板
     *
     * @return JdbcTemplate
     */
    public static JdbcTemplate getJdbcTemplate(){
        return new JdbcTemplate((DataSource)SpringContext.getBean("mytjDataSource"));
    }
	
	/**
	 * 判断用户uid是否存在
	 * @param meName
	 * @param uid
	 * @return
	 */
	public boolean checkUId(String meName, String uid){
		for (String string : CHECKUIDS) {
			if(meName.equals(string)){
				String sql = " SELECT COUNT(1) FROM tb_user WHERE pk_user = ? ";
				if(getJdbcTemplate().queryForInt(sql, new Object[]{uid}) == 0){
					return false;
				}
			}
		}
		return true;
	}

    /**
     * getJdbcTemplate
     * 取jdbc模板
     *
     * @return JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplates(){
        return new JdbcTemplate((DataSource)SpringContext.getBean("mytjDataSource"));
    }
    
	/**
		1 上传图片接口 /app/upFile
	    	请求参数：body文件字节，path路径，suffix 文件类型
			返回：String：网络图片的url
	 */
	public String upFile(byte[] body, String path, String suffix) {
		return upload(body, suffix, path);
	}

	/**
		2、一般店铺列表接口 /app/shopList  
	　　      请求参数：搜索内容searchString
	         所属分类type
		　　  经纬度位置:longpoi，latipoi
		　　  地理范围（多少千米之内）arrage
		　　  城市city
		　　  排序方式querytype 0-按最新 1-按关注度 2-按评论量
		　　  (int类型)当前页数 index 默认是0
		　　  (int类型)单页数量 pageNum
	        （建议增加单页数量参数、返回时无需当前页数、和是否有下一页参数）
	       返回：List<Map<String,Object>>：Map键说明：店铺ID sid、店铺名称 bsname
	              店铺位置 slongpoi,slatipoi、店铺地址 saddress、图片id iid
	 * 
	 */
	public List<Map<String, Object>> shopList(String searchString, String type, String longpoi, 
			                                  String latipoi, String arrage, String city, String querytype, int index,  int pageNum) {
		StringBuffer sql = new StringBuffer();
		List<Object> args = new ArrayList<Object>();
		sql.append(" SELECT DISTINCT A.PK_SHOP SID,  B.NAME BSNAME, A.ADDRESS SADDRESS, A.LONGPOI SLONGPOI, A.LATIPOI SLATIPOI, A.IMG_URL IID FROM TB_SHOP A  ");
		sql.append(" LEFT JOIN TB_SHOP_VIEW B ON A.PK_SHOP = B.PK_SHOP  ");
		sql.append(" LEFT JOIN TB_TYPE C ON C.PK_TYPE = B.PK_TYPE  ");
		if(StringUtils.isNotBlank(searchString)){
			sql.append(" WHERE ( B.NAME LIKE '%" + searchString + "%' OR A.ADDRESS LIKE '%" + searchString + "%' OR C.TYPENAME LIKE '%" + searchString + "%' )  ");
		}
		if(StringUtils.isNotBlank(type)){
			sql.append(" AND C.PK_TYPE = ?  ");
			args.add(type);
		}
		if(StringUtils.isNotBlank(city)){
			sql.append(" AND B.CITYNAME LIKE '%" + city + "%'  ");
		}
		if(StringUtils.isNotBlank(longpoi) && StringUtils.isNotBlank(latipoi) && StringUtils.isNotBlank(arrage)){
			sql.append(" AND  SQRT( POW(A.LATIPOI - ?, 2) + POW(A.LONGPOI - ?, 2) )  < ? ");
			args.add(latipoi);
			args.add(longpoi);
			args.add(arrage);
		}
		if(StringUtils.isNotBlank(querytype)){
			if(querytype.equals("0")){
				sql.append(" ORDER BY A.TS DESC   ");
			}else if(querytype.equals("1")){
				sql.append(" ORDER BY B.ALLFOLLOW DESC  ");
			}else if(querytype.equals("1")){
				sql.append(" ORDER BY B.ALLDISCUSS DESC  ");
			} 
		}
		sql.append(" limit ?,? ");
		args.add(index * pageNum);
		args.add(pageNum);

		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args.toArray());
		return list;
	}

	/**
		3 某优惠卷可用的店铺列表接口 /app/preferShopList
　　         请求参数：优惠卷id
　　    		  所在城市city
        		  位置 longpoi,latipoi
　　    		  (int类型)当前页数 index 默认是0
　　    		  (int类型)单页数量 pageNum
　　   返回 List<Map<String,Object>>：Map键说明： 店铺ID sid、
　　          店铺名称 bsname、所属 企业id bid、店铺位置 slongpoi,slatipoi、
            店铺地址 address、图片id iid
	 * 
	 */
	public List<Map<String, Object>> preferShopList(String id, String city,  String longpoi, 
			String latipoi, int index,  int pageNum) {
		String isASql = " SELECT ISALL, PK_BUSINESS, PK_SHOP FROM TB_COUPON WHERE PK_COUPON = ? ";
		List<Map<String, Object>> isAlist = getJdbcTemplates().queryForList(isASql, new Object[]{id});
		if(isAlist.isEmpty()){
			throw new BusinessException("没有找到该优惠券");
		}
		
		StringBuffer sql = new StringBuffer();
		Object[] args = null;
		Map<String, Object> map = isAlist.get(0);
		String isAll = (String) map.get("isall");
		String bid = (String) map.get("pk_business");
		String sid = (String) map.get("pk_shop");
		sql.append(" SELECT DISTINCT A.PK_SHOP SID,  B.NAME BSNAME, B.IMG_URLS IID, A.ADDRESS SADDRESS, A.LONGPOI SLONGPOI, A.LATIPOI SLATIPOI FROM TB_SHOP A ");
		sql.append(" INNER JOIN TB_SHOP_VIEW B ON A.PK_SHOP = B.PK_SHOP  ");
		// 如果是支持所有店铺，查找该企业下的所有店铺
		if(isAll != null && isAll.equals("1")){
			sql.append(" WHERE A.PK_BUSINESS = ?  ");
			args = new Object[]{bid};
			if(StringUtils.isNotBlank(city)){
				sql.append(" AND B.CITYNAME = ? ");
				args = new Object[]{bid, city};
			}
		}else{
			sql.append(" WHERE A.PK_SHOP = ?  ");
			args = new Object[]{sid};
		}
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}
	
	/**
		4、某店铺的所有可用优惠卷列表接口 /app/preferList
　　       请求参数：店铺id sid，
　　 	   排序方式querytype 0-按最新，1-按热门
　　       (int类型)第几页 index
　　       (int类型)单页数量 pageNum
　　     返回 List<Map<String,Object>>：Map键说明： cd、came、有效期cdeadline、
　　    图片iid
	 * 
	 */
	public List<Map<String, Object>> preferList(String sid, String querytype, int index, int pageNum) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT A.PK_COUPON CID, A.NAME CNAME, A.DEADLINE CDEADLINE, A.IMG_URL CIMG , A.READTIME FROM TB_COUPON A  ");
		sql.append("  WHERE A.PK_SHOP = ? AND A.ISDELETE = 0   ");
		sql.append(" OR ( A.PK_BUSINESS = (SELECT PK_BUSINESS FROM TB_SHOP WHERE PK_SHOP = ? ) AND A.ISALL = 1) ");
		
		if(querytype.equals("0")){
			sql.append(" ORDER BY A.TS DESC  ");
		}else{
			sql.append(" ORDER BY A.READTIME DESC  ");
		}
		sql.append(" limit ?,? ");
		Object[] args = new Object[]{sid, sid, index * pageNum, pageNum};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}

	/**
		5、用户拥有某企业的所有优惠卷列表接口 /app/userShopPreferList 
　　  请求参数：企业bid 
　　            用户uid 
　　            (int类型)index（TODO：是否需要分页）
　　            (int类型)单页数量 pageNum 
　　  （登陆是否成功，前台可以判定）
　　    返回 List<Map<String,Object>>：Map键说明： cd、came、有效期cdeadline、
　　    图片iid、是否使用isuse
	 * 
	 */
	public List<Map<String, Object>> userShopPreferList(String bid, String uid, int index, int pageNum) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT B.PK_COUPON CD, B.NAME CNAME, B.DEADLINE CDEADLINE, B.IMG_URL, A.ISUSE FROM TB_USER_COUPON A   ");
		sql.append(" INNER JOIN TB_COUPON B ON A.PK_COUPON = B.PK_COUPON ");
		sql.append(" WHERE B.ISDELETE = 0 AND A.PK_USER = ? AND A.PK_BUSINESS = ? ORDER BY A.TS DESC  ");
		sql.append(" limit ?,? ");
		Object[] args = new Object[]{uid, bid, index * pageNum, pageNum};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}

	/**
		6、 某用户关注的企业列表接口接口 /app/userLikeShopList
　　      请求参数：用户uid
　　      返回 List<Map<String,Object>>：Map键说明： 企业id bid、企业时间戳 、
        所属分类btype、图片id iid、该企业被关注的总数 count
	 * 
	 */
	public List<Map<String, Object>> userLikeShopList(String uid) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT B.PK_BUSINESS BID, B.TS , B.TYPE BTYPE, B.IMG_URLS IID, B.LIKECOUNT COUNT  FROM TB_FOLLOW A  ");
		sql.append(" INNER JOIN TB_BUSINESS B ON A.PK_BUSINESS = B.PK_BUSINESS  ");
		sql.append(" WHERE A.PK_USER = ? ORDER BY A.TS DESC  ");
		Object[] args = new Object[]{uid};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}

	/**
		7、某用户拥有的所有优惠卷所属于的企业列表接口 /app/userHasPerferShopList
		　　  请求参数：用户uid
		　　返回 List<Map<String,Object>>：Map键说明： 企业id bid、企业时间戳 、
　　    所属分类btype、图片id iid、该企业被关注的总数 count
	 */
	public List<Map<String, Object>> userHasPerferShopList(String uid) {
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT DISTINCT B.PK_BUSINESS BID, B.TS , B.TYPE BTYPE, B.IMG_URLS IID, B.LIKECOUNT COUNT FROM TB_USER_COUPON A  ");
		sql.append("  INNER JOIN TB_BUSINESS B ON A.PK_BUSINESS = B.PK_BUSINESS ");
		sql.append("  WHERE A.PK_USER = ? ORDER BY A.TS DESC  ");
		Object[] args = new Object[]{uid};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}

	/**
		8 某店铺的海报列表接口 /app/shopPptList
	      请求参数：店铺id sid
	　　返回 List<Map<String,Object>>：Map键说明： pid、图片iid、描述pdescribe
	 */
	public List<Map<String, Object>> shopPptList(String sid) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT A.PK_POSTER, A.IMG_URL IID, A.BEWRITE PDESCRIBE FROM TB_POSTER A  ");
		sql.append(" LEFT JOIN TB_SHOP B ON A.PK_SHOP = B.PK_SHOP ");
		sql.append(" WHERE A.PK_SHOP = ?  ");
		sql.append(" OR ( A.PK_BUSINESS = (SELECT PK_BUSINESS FROM TB_SHOP WHERE PK_SHOP = ? ) AND A.ISALL = 1)  ");
		sql.append(" ORDER BY A.TS DESC  ");
		Object[] args = new Object[]{sid, sid};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}
	
	/**
		9 某店铺的产品列表接口 /app/shopGoodList
		　　  请求参数：店铺id sid
		返回 List<Map<String,Object>>：Map键说明： gid、名称gname、图片gimg、原价goriprice
     现价gprice
	 */
	public List<Map<String, Object>> shopGoodList(String sid, int index, int pageNum) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT A.PK_GOODS GID, A.NAME GNAME, A.IMG_URL GIMG, A.ORIPEICE GORIPRICE, A.PEICE GPRICE FROM TB_GOODS A   ");
		sql.append(" LEFT JOIN TB_SHOP B ON A.PK_SHOP = B.PK_SHOP ");
		sql.append(" WHERE A.PK_SHOP = ?  ");
		sql.append(" OR ( A.PK_BUSINESS = (SELECT PK_BUSINESS FROM TB_SHOP WHERE PK_SHOP = ? ) AND A.ISALL = 1)  ");
		sql.append(" ORDER BY A.TS DESC  ");
		sql.append(" limit ?,? ");
		Object[] args = new Object[]{sid, sid, index * pageNum, pageNum};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}
	
	/**
		10.评论列表接口  /app/commentList
		　　  请求参数：店铺id sid
		返回 List<Map<String,Object>>：Map键说明：did、用户昵称uname、内容dcontent、打分dscore、赞的数量likecount
	 */
	public List<Map<String, Object>> commentList(String sid, int index, int pageNum) {
		StringBuffer sql = new StringBuffer(); 
		sql.append(" SELECT DISTINCT A.PK_DISCUSS DID, B.NAME UNAME, A.CONTENT DCONTENT, A.SCORE DSCORE, A.ALLLIKE LIKECOUNT FROM TB_DISCUSS A ");
		sql.append(" INNER JOIN TB_USER B ON A.PK_USER = B.PK_USER ");
		sql.append(" WHERE A.PK_SHOP = ?  ");
		sql.append(" ORDER BY A.TS DESC  ");
		sql.append(" limit ?,? ");
		Object[] args = new Object[]{sid, index * pageNum, pageNum};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}
	
	/**
		11 购物清单接口 /app/buyGoodList
		　　  请求参数：店铺id sid
		返回 List<Map<String,Object>>：Map键说明：mid本地ID、uid用户id、mcontent内容、
		    mmark是否标记、时间戳timestamp
	 */
	public List<Map<String, Object>> buyGoodList(String uid, String sid, int index, int pageNum) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT A.MID, A.PK_USER UID, A.CONTENT MCONTENT, A.ISMARK MMARK, A.TS TIMESTAMP FROM TB_GOODSBUY A  ");
		sql.append(" WHERE A.PK_USER = ? ");
		sql.append(" ORDER BY A.TS DESC  ");
		sql.append(" limit ?,? ");
		Object[] args = new Object[]{uid, index * pageNum, pageNum};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}
	
	/**
		12 单个优惠卷  /app/perfer
		　　  请求参数：优惠卷cid
		　　    uid(如果登录了就有，不然就没有)
		返回 Map<String,Object>：Map键说明：cid、名称cname、内容描述ccontent、
		     说明cdescirbe、有效期时间戳cdeadline、图片cimg、bid、isuse
	 */
	public Map<String, Object> perfer(String cid, String uid) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT A.PK_COUPON CID, A.NAME CNAME, A.CONTENT CCONTENT, A.DESCIRBE CDESCIRBE, ");
		sql.append("   A.DEADLINE CDEADLINE, A.IMG_URL CIMG,  A.PK_BUSINESS BID,  ");
		sql.append("  (SELECT COUNT(1) FROM TB_USER_COUPON WHERE PK_COUPON = ? AND PK_USER = ? AND ISUSE = 1) ISUSE FROM TB_COUPON A ");
		sql.append(" WHERE A.ISDELETE = 0 AND A.PK_COUPON = ?  ");
		Object[] args = new Object[]{cid, uid, cid};
		try {
			Map<String, Object> map = getJdbcTemplates().queryForMap(sql.toString(), args);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("没有找到对应的优惠券：" + cid);
		}
	}
	
	/**
		13 单个店铺  /app/shop
		　　  请求参数：sid
		返回 Map<String,Object>：Map键说明：sid、bid、名称bsname、地址saddress
		     地址经纬度坐标*2 slongpoi,slatipoi、所属分类stype、主要图片simg、联系电话sphone
		　　 详细介绍sresume、营业时间*3 sondate,sonstarttime,sonendtime、
		　　 sscore(用户给他打分的平均分)
	 */
	public Map<String, Object> shop(String sid, String uid) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT A.PK_SHOP SID, A.PK_BUSINESS BID, B.NAME BSNAME, A.ADDRESS SADDRESS, ");
		sql.append("        A.LONGPOI SLONGPOI, A.LATIPOI SLATIPOI, B.PK_TYPE STYPE, B.IMG_URLS IMG, ");
		sql.append("        B.PHONE SPHONE, B.RESUME SRESUME, B.ONTIME SONTIME, B.SCORE SSCORE ");
		sql.append("    FROM TB_SHOP A ");
		sql.append(" INNER JOIN TB_SHOP_VIEW B ON A.PK_SHOP = B.PK_SHOP ");
		sql.append("  WHERE A.PK_SHOP = ? ");
		Object[] args = new Object[]{sid};
		try {
			Map<String, Object> map = getJdbcTemplates().queryForMap(sql.toString(), args);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("没有找到对应的店铺：" + sid);
		}
	}
	
	/**
		14 登录接口  /app/login
       		请求参数：uid
                密码hash hash
		返回 TODO 是否需要返回用户的基本信息，登陆错误的话直接返回业务异常
	 */
	public String login(String uname, String password) {
		String countSql = " SELECT count(1) FROM tb_user where name = ? ";
		if(getJdbcTemplate().queryForInt(countSql, new Object[]{uname}) == 0){
			throw new BusinessException("用户名不存在");
		}
		String sql = " SELECT pk_user uid FROM tb_user WHERE name = ? AND passwordcode = ? ";
		String pkUser = getJdbcTemplate().queryForObject(sql, new Object[]{uname,password}, String.class);
		if(pkUser == null){
			throw new BusinessException("用户名或密码错误");
		}
		return pkUser;
	}
	
	/**
		15 手机号注册验证接口 /app/registerCheck
		       请求参数：手机号phone
		返回 Boolean： confirm（为true就成功，否则说明失败了，下面相同的都一样）
	 */
	public Boolean registerCheck(String phone) {
		String countSql = " SELECT count(1) FROM tb_user where phone = ? ";
		return getJdbcTemplate().queryForInt(countSql, new Object[]{phone}) == 0;
	}
	
	/**
		16  手机号注册接口 /app/register
		       请求参数：手机号phone
		                昵称 uname
		                密码hash
		    返回 Boolean： confirm
	 */
	public String register(String phone, String uname, String password) {
		String sql = " INSERT INTO tb_user(pk_user, name, passwordcode, phone, ts) VALUES (?, ?, ?, ?, NOW()) ";
		if(!registerCheck(phone)){
			throw new BusinessException("手机号已存在");
		}
		String countSql = " SELECT count(1) FROM tb_user where name = ? ";
		if(getJdbcTemplate().queryForInt(countSql, new Object[]{uname}) != 0){
			throw new BusinessException("用户名已存在");
		}
		String id = getOID();
		getJdbcTemplate().update(sql, new Object[]{id, uname, password, phone});
		return id;
	}
	
	/**
		17 对评论的赞接口 /app/commentLike
       		请求参数：评论id did
		返回 Boolean： confirm
	 */
	public Boolean commentLike(String did, String uid) {
		String upSql = " UPDATE tb_discuss SET alllike = alllike + 1 WHERE pk_discuss = ? ";
		getJdbcTemplate().update(upSql, new Object[]{did});
		String inSql = " INSERT INTO tb_discuss_like (pk_discuss_like, pk_user, pk_discuss, ts) VALUES (?,?,?,NOW()) ";
		getJdbcTemplate().update(inSql, new Object[]{getOID(), uid, did});
		return true;
	}
	
	/**
		18 评论接口 /app/comment
			       请求参数：sid店铺ID
			               uid用户ID
			               dcontent内容
			               dscore打分
			返回 Boolean： confirm
	 */
	public String comment(String sid, String uid, String dcontent, String score) {
		String id = getOID();
		String inSql = " INSERT INTO tb_discuss (pk_discuss, pk_user, pk_shop, content, score, alllike, ts) VALUES (?,?,?,?,?,0, NOW()) ";
		getJdbcTemplate().update(inSql, new Object[]{id, uid, sid, dcontent, score});
		
		// 算平均分
		String sql;
		if(StringUtils.isNotBlank(score)){
			String scoreSql = " SELECT count(1) FROM tb_shop_view where score = '0.00' and pk_shop = ? ";
			if(getJdbcTemplate().queryForInt(scoreSql, new Object[]{sid}) == 0){
				sql = " UPDATE tb_shop_view set score = ?  WHERE pk_shop = ? ";
			}else{
				sql = " UPDATE tb_shop_view set score = (score + ?)/2  WHERE pk_shop = ? ";
			}
			getJdbcTemplate().update(sql, new Object[]{score, sid});
		}
		return id;
	}
	
	/**
		19 删除海报接口 /app/deletePpt
			       请求参数：id （全部用id吧，方便）
			返回 Boolean： confirm
	 */
	public Boolean deletePpt(String id) {
		String inSql = " DELETE FROM tb_poster WHERE pk_poster = ? ";
		getJdbcTemplate().update(inSql, new Object[]{id});
		return true;
	}
	
	/**
		20 删除产品接口 /app/deleteGood
			       请求参数：id
			返回 Boolean： confirm
	 */
	public Boolean deleteGood(String id) {
		String inSql = " DELETE FROM tb_goods WHERE pk_goods = ? ";
		getJdbcTemplate().update(inSql, new Object[]{id});
		return true;
	}
	
	/**
		21 提前放弃优惠卷 /app/deletePerfer
			       请求参数：id
			返回 Boolean： confirm
	 */
	public Boolean deletePerfer(String cid, String uid) {
		String inSql = " DELETE FROM tb_user_coupon WHERE pk_user = ? AND pk_coupon = ? ";
		getJdbcTemplate().update(inSql, new Object[]{uid, cid});
		return true;
	}
	
	/**
		22 用户下载优惠卷接口 /app/downPerfer
			       请求参数：id
			返回 Boolean： confirm
	 */
	public Boolean downPerfer(String cid, String uid) {
		String id = getOID();
		// 查找商家id
		String sSql = " SELECT pk_business FROM tb_coupon WHERE pk_coupon = ? ";
		String bid = getJdbcTemplate().queryForObject(sSql, String.class, new Object[]{cid});
		if(StringUtils.isBlank(bid)){
			String sSql2 = "SELECT B.pk_business FROM tb_coupon A INNER JOIN tb_shop B ON A.pk_shop = B.pk_shop WHERE A.pk_coupon = ?";
			bid = getJdbcTemplate().queryForObject(sSql2, String.class, new Object[]{cid});
		}
		String inSql = " INSERT INTO tb_user_coupon(pk_user_coupon, pk_user, pk_business, pk_coupon, isuse,  ts) VALUES (? ,? ,? ,? ,0 ,NOW()) ";
		getJdbcTemplate().update(inSql, new Object[]{id, uid, bid, cid});
		return true;
	}
	
	/**
		23 用户使用优惠卷接口 /app/usePerfer
		       请求参数：id
		返回 Boolean： confirm
	 */
	public Boolean usePerfer(String cid, String uid) {
		String inSql = " UPDATE tb_user_coupon SET isuse = 1 WHERE pk_user = ? AND pk_coupon = ?";
		getJdbcTemplate().update(inSql, new Object[]{uid, cid});
		return true;
	}
	
	/**
		24 关注一个店铺接口 /app/likeShop
			       请求参数：店铺id sid
			       			用户id uid
			返回 Boolean： confirm
	 */
	public Boolean likeShop(String sid, String uid) {
		String upSql = " UPDATE tb_shop_view SET allfollow = allfollow + 1 WHERE pk_shop = ? ";
		getJdbcTemplate().update(upSql, new Object[]{sid});
		String inSql = " INSERT INTO tb_follow(pk_follow, pk_user, pk_shop, ts) VALUES (? ,? ,?, NOW()); ";
		getJdbcTemplate().update(inSql, new Object[]{getOID(), uid, sid});
		return true;
	}
	
	/**
		39 关注一个企业接口 /app/likeBusiness
			       请求参数：企业id bid
			       			用户id uid
			返回 Boolean： confirm
	 */
	public Boolean likeBusiness(String bid, String uid) {
		String upSql = " UPDATE tb_business SET likecount = likecount + 1 WHERE pk_business = ? ";
		getJdbcTemplate().update(upSql, new Object[]{bid});
		String inSql = " INSERT INTO tb_follow(pk_follow, pk_user, pk_business, ts) VALUES (? ,? ,?, NOW()); ";
		getJdbcTemplate().update(inSql, new Object[]{getOID(), uid, bid});
		return true;
	}
	
	/**
		25 删除一个企业接口 /app/deleteShop
			       请求参数：id
			返回 Boolean： confirm
	 */
	public Boolean deleteShop(String id) {
		String sql = " DELETE FROM tb_shop WHERE pk_shop = ? ";
		getJdbcTemplate().update(sql, new Object[]{id});
		String inSql = " DELETE FROM tb_shop_view WHERE pk_shop = ? ";
		getJdbcTemplate().update(inSql, new Object[]{id});
		return true;
	}
	
	/**
		39 删除一个店铺接口 /app/deleteBusiness
			       请求参数：id
			返回 Boolean： confirm
	 */
	public Boolean deleteBusiness(String id) {
		String inSql = " DELETE FROM tb_business WHERE pk_business = ? ";
		getJdbcTemplate().update(inSql, new Object[]{id});
		return true;
	}
	
	/**
		26 添加海报接口 /app/addPpt
		       请求参数：bid商家ID、sid店铺id、pimg图片、描述pdescribe、isall 是否属于所有该企业门店 1-是，0-否
		（设计图片的有两种方式：1、先调用上传接口，2、直接将byte对象参数传入）
			返回 Boolean： confirm
	 */
	public String addPpt(String bid, String sid, String pimg, String pdescribe, String isall) {
		String id = getOID();
		String inSql = " INSERT INTO tb_poster(pk_poster, pk_business, pk_shop, bewrite, img_url, isall, ts) VALUES (? ,? ,? ,? ,? ,? ,NOW()) ";
		getJdbcTemplate().update(inSql, new Object[]{id, bid, sid, pdescribe, pimg, isall});
		return id;
	}
	
	/**
		27、添加产品接口 /app/addGood
		       请求参数：gname名称、价格goriprice、gprice、gimg图片、商家bid、isall 是否属于所有该企业门店 1-是，0-否
		返回 Boolean： confirm
	 */
	public String addGood(String bid, String sid, String gname, String goriprice, String gprice, String gimg, String isall) {
		String id = getOID();
		String inSql = " INSERT INTO tb_goods(pk_goods, pk_business, pk_shop, name, img_url, oripeice, peice, isall, ts) VALUES (? ,? ,? ,? ,? ,?, ? , ?, NOW()) ";
		getJdbcTemplate().update(inSql, new Object[]{id, bid, sid, gname, gimg, goriprice, gprice, isall});
		return id;
	}

	/**
		28、添加优惠卷接口 /app/addPerfer
		       请求参数：名称cname、内容描述ccontent、说明cdescirbe、
		       有效期时间戳cdeadline、图片cimg、bid商家bid、isall 是否属于所有该企业门店 1-是，0-否
		返回 Boolean： confirm
	 */
	public String addPerfer(String bid, String sid, String cname, String ccontent, String cdescirbe, String cdeadline, String cimg, String isall) {
		String id = getOID();
		String inSql = " INSERT INTO tb_coupon(pk_coupon, pk_business, pk_shop, name, " +
					   " content, descirbe, deadline, img_url, timestamp, readtime, isall, ts) VALUES (? ,? ,? ,? ,? ,?, ?, ? , NOW() ,?, ? ,NOW()) ";
		getJdbcTemplate().update(inSql, new Object[]{id, bid, sid, cname, ccontent,cdescirbe,cdeadline, cimg, 0, isall});
		return id;
	}

	/**
		29、上传购物清单接口 /app/addBuyGoods
		       请求参数：List<Map<String,Object>> ：Map键说明：mid本地ID、mcontent内容、
		        mmark是否标记、时间戳timestamp、gid商品id
		返回 Boolean： confirm
	 */
	public Boolean addBuyGoods(List<Map<String,Object>> list, String uid) {
		String inSql = " INSERT INTO tb_goodsbuy(pk_goodsbuy, pk_user, pk_good, pk_shop, name, " +
					   " mid, content, ismark, count, ts) VALUES (? ,? ,? ,? ,? ,?, ?, ? , ?, ? ) ";
		for (Map<String, Object> map : list) {
			String mid = (String) map.get("mid");
			String mcontent = (String) map.get("mcontent");
			String mmark = (String) map.get("mmark");
			String timestamp = (String) map.get("timestamp");
			String gid = (String) map.get("gid");
			Integer mcount = (Integer) map.get("mcount");
			getJdbcTemplate().update(inSql, new Object[]{getOID(), uid, gid, null, null, mid, mcontent, mmark, mcount, timestamp});
		}
		return true;
	}

	/**
		30、申请认证成商家接口 /app/askForAuthShop
		       请求参数：商家id：bid、营业执照图片cimg、uid用户id
		返回 Boolean： confirm
	 */
	public Boolean askForAuthShop(String bid, String cimg, String uid) {
		String inSql = " UPDATE tb_business SET pk_user = ? , auto_img_url = ? WHERE pk_business = ? ";
		getJdbcTemplate().update(inSql, new Object[]{uid, cimg, bid});
		return true;
	}

	/**
		31、添加非认证商家接口 /app/askForUnAuthShop
		       请求参数：商家名称bname、所属分类btype、主要图片bimg、
		       联系电话bphone、详细介绍bresume、地址baddress、位置 slongpoi,slatipoi
		返回 Boolean： confirm
	 */
	public String askForUnAuthShop(String bname, String btype, String[] bimg, String bphone, String bresume, String bontime, String baddress, String blongpoi, String blatipoi) {
		String id = getOID();
		String inSql = " INSERT INTO tb_business(pk_business, name, type, " +
					   " img_urls, phone, resume, ontime, longpoi, latipoi, address,  ts) VALUES (? ,? ,? ,? ,? ,?, ?, ?, ?, ?, NOW()) ";
		getJdbcTemplate().update(inSql, new Object[]{id, bname, btype, getImgString(bimg), bphone, bresume, bontime, blongpoi, blatipoi, baddress});
		return id;
	}

	/**
		32、添加店铺接口 /app/addShop
		返回 String： sid
	 */
	public String addShop(String bid, String ssubname, String[] simg, String saddress, String slongpoi, String slatipoi, String scity, String stype, String sphone,
			              String sresume, String sontime) {
		if(simg == null || simg.length == 0){
			throw new BusinessException("必须上传一张图片");
		}
		String id = getOID();
		String shopSql = " INSERT INTO tb_shop(pk_shop, pk_business, subname, img_url, address, longpoi, latipoi, ts) VALUES (? ,? ,? ,? ,?, ? , ?, NOW()) ";
		getJdbcTemplate().update(shopSql, new Object[]{id, bid, ssubname, simg[0], saddress, slongpoi, slatipoi});
		
		String bname = getJdbcTemplate().queryForObject(" SELECT name FROM tb_business WHERE pk_business = ? ", String.class, new Object[]{bid});
		String name = bname + " " + ssubname;
		String inSql = " INSERT INTO tb_shop_view(pk_shop_view, pk_shop, name, " + 
					   " cityname, pk_type, img_urls, phone, resume, ontime,  ts) VALUES (? ,? ,? ,? ,? ,?, ?, ?, ?, NOW()) ";
		getJdbcTemplate().update(inSql, new Object[]{getOID(), id, name, scity, stype, getImgString(simg), sphone, sresume, sontime});
		return id;
	}

	/**
		33、添加类别接口 /app/addType
		       请求参数：名称tname
		返回 Boolean： confirm
	 */
	public Boolean addType(String tname) {
		String countSql = " SELECT count(1) FROM tb_type where typename = ? ";
		if(getJdbcTemplate().queryForInt(countSql, new Object[]{tname}) != 0){
			throw new BusinessException("类别名字已存在");
		}
		String inSql = " INSERT INTO tb_type(pk_type, typename) VALUES (? ,?) ";
		getJdbcTemplate().update(inSql, new Object[]{getOID(), tname});
		return true;
	}

	/**
		34、获得所有类别接口 /app/getTypes
		返回 Boolean： confirm
	 */
	public List<Map<String, Object>> getTypes() {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT typename tname FROM tb_type  ");
		Object[] args = new Object[]{};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}

	/**
		35、修改产品接口 /app/upGood
	       请求参数：gname名称、价格goriprice、gprice、gimg图片、isall 是否属于所有该企业门店 1-是，0-否
		返回 Boolean： confirm
	 */
	public Boolean upGood(String gid, String gname, String goriprice, String gprice, String gimg, String isall) {
		String inSql = " UPDATE tb_goods SET name = ? , img_url = ?, oripeice = ?, peice = ?, isall = ? WHERE pk_goods = ? ";
		getJdbcTemplate().update(inSql, new Object[]{gname, gimg, goriprice, gprice, isall, gid});
		return true;
	}
	
	/**
		36、修改优惠卷接口 /app/upPerfer
		       请求参数：名称cname、内容描述ccontent、说明cdescirbe、
		       有效期时间戳cdeadline、图片cimg、bid商家bid、isall 是否属于所有该企业门店 1-是，0-否
		返回 Boolean： confirm
	 */
	public Boolean upPerfer(String cid, String cname, String ccontent, String cdescirbe, String cdeadline, String cimg, String isall) {
		String inSql = " UPDATE tb_coupon SET name = ? , content = ?, descirbe = ?, deadline = ?," +
					   " img_url = ?, isall = ? WHERE pk_coupon = ? ";
		getJdbcTemplate().update(inSql, new Object[]{cname, ccontent,cdescirbe,cdeadline, cimg, isall, cid});
		return true;
	}
	
	/**
		37、修改商家接口 /app/upBusiness
		       请求参数：商家名称bname、所属分类btype、主要图片bimg、
		       联系电话bphone、详细介绍bresume、地址baddress、位置 slongpoi,slatipoi
		返回 Boolean： confirm
	 */
	public Boolean upBusiness(String bid, String bname, String btype, String[] bimg, String bphone, String bresume, String bontime, String baddress, String blongpoi, String blatipoi) {
		String inSql = " UPDATE tb_business SET name = ? , type = ?, img_urls = ?, phone = ?," +
		   " resume = ?, ontime = ?, longpoi = ?, latipoi = ?,address = ? WHERE pk_business = ? ";
		getJdbcTemplate().update(inSql, new Object[]{bname, btype, getImgString(bimg), bphone, bresume, bontime, blongpoi, blatipoi, baddress, bid});
		return true;
	}
	
	/**
		38、修改店铺接口 /app/upShop
		返回 String： sid
	 */
	public Boolean upShop(String sid, String ssubname, String[] simg, String saddress, String slongpoi, String slatipoi, String scity, String stype, String sphone,
			              String sresume, String sontime) {
		if(simg == null || simg.length == 0){
			throw new BusinessException("必须上传一张图片");
		}
		String shopSql = " UPDATE tb_shop SET subname = ? , img_url = ?, address = ?, longpoi = ?, latipoi = ? WHERE pk_shop = ? ";
		getJdbcTemplate().update(shopSql, new Object[]{ssubname, simg[0], saddress, slongpoi, slatipoi, sid});
		
		String bname = getJdbcTemplate().queryForObject(" SELECT B.name FROM tb_shop A INNER JOIN tb_business B ON A.pk_business = B.pk_business WHERE A.pk_shop = ? ", String.class, new Object[]{sid});
		String name = bname + " " + ssubname;
		String inSql = " UPDATE tb_shop_view SET name = ? ,cityname = ?, pk_type = ?, img_urls = ?, phone = ?," +
		   " resume = ?, ontime = ? WHERE pk_shop = ? ";
		getJdbcTemplate().update(inSql, new Object[]{name, scity, stype, getImgString(simg), sphone, sresume, sontime,sid});
		return true;
	}

	/**
		40、获得所有城市 /app/getCitys
		返回 Boolean： confirm
	 */
	public List<Map<String, Object>> getCitys() {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT NAME, FULL_NAME, GRADE, PK_PROVINCE, PK_PARENT_REGION from sm_region  ");
		Object[] args = new Object[]{};
		List<Map<String, Object>> list = getJdbcTemplates().queryForList(sql.toString(), args);
		return list;
	}

	public String getImgString(String[] imgs){
		return StringUtils.join(imgs, ',');
	}

}
