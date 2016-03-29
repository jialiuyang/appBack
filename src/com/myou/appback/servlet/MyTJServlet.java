package com.myou.appback.servlet;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vo.ReturnVO;

import com.myou.appback.bo.MyTJBO;
import com.myou.appback.modules.exception.BusinessException;

/**
 * Title: 爱花族网站平台<br>
 * Description: 手机花讯用的servlet<br>
 * Copyright: Copyright (c) 2012<br>
 * Company: MYOU<br>
 * 
 * @author JJY
 * @version 1.0
 */
public class MyTJServlet extends HttpServlet {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 1L;

    /** 当前记录日志的logger对象 */
    private final static Logger logger = LoggerFactory.getLogger(MyTJServlet.class);

    /** 接口 */
    private MyTJBO bo;
    
    /** 统一日志风格 */
    public final static String SJHUAXUN = "【手机】";
    
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // 初始化一下
        if (bo == null){
        	bo = new MyTJBO();
        }
        ObjectInputStream ois = null;
        // 数据的传送方式
        ReturnVO vo = new ReturnVO();
        
        String baseType = null;
        String uid = null;
        String version = null;
        String logStart = null;
        
        
        try {
            ois = new ObjectInputStream(request.getInputStream());
            Map<String, Object> map = (Map<String, Object>) ois.readObject();
            baseType = (String) getParameter(map, "base_type");
            uid = (String) getParameter(map, "uid");
        	String sid = (String) getParameter(map, "sid");
            logStart = SJHUAXUN + version + " 用户：" + uid + "请求" + baseType + "，";
            
            // 最终的数据
            Object data = null;
            long startTime = System.currentTimeMillis();
            if(StringUtils.isBlank(baseType)){
                vo.setBusinessException(true);
                vo.setData("非法请求");
            }else if(!bo.checkUId(baseType, uid)){
                vo.setBusinessException(true);
                vo.setData("非法操作");
            }else if("upFile".equals(baseType)){
            	byte[] body = (byte[]) getParameter(map, "body");
            	String path = (String) getParameter(map, "path");
            	String suffix = (String) getParameter(map, "suffix");
                data = bo.upFile(body, path, suffix);
            }else if("shopList".equals(baseType)){
            	String searchString = (String) getParameter(map, "searchString");
            	String type = (String) getParameter(map, "type");
            	String longpoi = (String) getParameter(map, "longpoi");
            	String latipoi = (String) getParameter(map, "latipoi");
            	String arrage = (String) getParameter(map, "arrage");
            	String city = (String) getParameter(map, "city");
            	String querytype = (String) getParameter(map, "querytype");
            	int index = getParameterInt(map, "index");
            	int pageNum = getParameterInt(map, "pageNum");
                data = bo.shopList(searchString, type, longpoi, latipoi, arrage, city, querytype, index, pageNum);
            }else if("preferShopList".equals(baseType)){
            	String id = (String) getParameter(map, "id");
            	String city = (String) getParameter(map, "city");
            	String longpoi = (String) getParameter(map, "longpoi");
            	String latipoi = (String) getParameter(map, "latipoi");
            	int index = getParameterInt(map, "index");
            	int pageNum = getParameterInt(map, "pageNum");
                data = bo.preferShopList(id, city, longpoi, latipoi, index, pageNum);
            }else if("preferList".equals(baseType)){
            	String querytype = (String) getParameter(map, "querytype");
            	int index = getParameterInt(map, "index");
            	int pageNum = getParameterInt(map, "pageNum");
                data = bo.preferList(sid, querytype, index, pageNum);
            }else if("userShopPreferList".equals(baseType)){
            	String bid = (String) getParameter(map, "bid");
            	int index = getParameterInt(map, "index");
            	int pageNum = getParameterInt(map, "pageNum");
                data = bo.userShopPreferList(bid, uid, index, pageNum);
            }else if("userLikeShopList".equals(baseType)){
                data = bo.userLikeShopList(uid);
            }else if("userHasPerferShopList".equals(baseType)){
                data = bo.userHasPerferShopList(uid);
            }else if("shopPptList".equals(baseType)){
                data = bo.shopPptList(sid);
            }else if("shopGoodList".equals(baseType)){
            	int index = getParameterInt(map, "index");
            	int pageNum = getParameterInt(map, "pageNum");
                data = bo.shopGoodList(sid, index, pageNum);
            }else if("commentList".equals(baseType)){
            	int index = getParameterInt(map, "index");
            	int pageNum = getParameterInt(map, "pageNum");
                data = bo.commentList(sid, index, pageNum);
            }else if("buyGoodList".equals(baseType)){
            	int index = getParameterInt(map, "index");
            	int pageNum = getParameterInt(map, "pageNum");
                data = bo.buyGoodList(uid, sid, index, pageNum);
            }else if("perfer".equals(baseType)){
            	String cid = (String) getParameter(map, "cid");
                data = bo.perfer(cid, uid);
            }else if("shop".equals(baseType)){
                data = bo.shop(sid, uid);
            }else if("login".equals(baseType)){
            	String uname = (String) getParameter(map, "uname");
            	String password = (String) getParameter(map, "password");
                data = bo.login(uname, password);
            }else if("registerCheck".equals(baseType)){
            	String phone = (String) getParameter(map, "phone");
                data = bo.registerCheck(phone);
            }else if("register".equals(baseType)){
            	String phone = (String) getParameter(map, "phone");
            	String uname = (String) getParameter(map, "uname");
            	String password = (String) getParameter(map, "password");
                data = bo.register(phone, uname, password);
            }else if("commentLike".equals(baseType)){
            	String did = (String) getParameter(map, "did");
                data = bo.commentLike(did, uid);
            }else if("comment".equals(baseType)){
            	String dcontent = (String) getParameter(map, "dcontent");
            	String dscore = (String) getParameter(map, "dscore");
                data = bo.comment(sid, uid, dcontent, dscore);
            }else if("deletePpt".equals(baseType)){
            	String id = (String) getParameter(map, "id");
                data = bo.deletePpt(id);
            }else if("deleteGood".equals(baseType)){
            	String id = (String) getParameter(map, "id");
                data = bo.deleteGood(id);
            }else if("deleteBusiness".equals(baseType)){
            	String id = (String) getParameter(map, "id");
                data = bo.deleteBusiness(id);
            }else if("deletePerfer".equals(baseType)){
            	String cid = (String) getParameter(map, "cid");
                data = bo.deletePerfer(cid, uid);
            }else if("downPerfer".equals(baseType)){
            	String cid = (String) getParameter(map, "cid");
                data = bo.downPerfer(cid, uid);
            }else if("usePerfer".equals(baseType)){
            	String cid = (String) getParameter(map, "cid");
                data = bo.usePerfer(cid, uid);
            }else if("likeShop".equals(baseType)){
                data = bo.likeShop(sid, uid);
            }else if("likeBusiness".equals(baseType)){
            	String bid = (String) getParameter(map, "bid");
                data = bo.likeBusiness(bid, uid);
            }else if("deleteShop".equals(baseType)){
            	String id = (String) getParameter(map, "id");
                data = bo.deleteShop(id);
            }else if("addPpt".equals(baseType)){
            	String bid = (String) getParameter(map, "bid");
            	String pimg = (String) getParameter(map, "pimg");
            	String pdescribe = (String) getParameter(map, "pdescribe");
            	String isall = (String) getParameter(map, "isall");
                data = bo.addPpt(bid, sid, pimg, pdescribe, isall);
            }else if("addGood".equals(baseType)){
            	String bid = (String) getParameter(map, "bid");
            	String gname = (String) getParameter(map, "gname");
            	String goriprice = (String) getParameter(map, "goriprice");
            	String gprice = (String) getParameter(map, "gprice");
            	String gimg = (String) getParameter(map, "gimg");
            	String isall = (String) getParameter(map, "isall");
                data = bo.addGood(bid, sid, gname, goriprice, gprice, gimg, isall);
            }else if("addPerfer".equals(baseType)){
            	String bid = (String) getParameter(map, "bid");
            	String cname = (String) getParameter(map, "cname");
            	String ccontent = (String) getParameter(map, "ccontent");
            	String cdescirbe = (String) getParameter(map, "cdescirbe");
            	String cdeadline = (String) getParameter(map, "cdeadline");
            	String cimg = (String) getParameter(map, "cimg");
            	String isall = (String) getParameter(map, "isall");
                data = bo.addPerfer(bid, sid, cname, ccontent, cdescirbe, cdeadline, cimg, isall);
            }else if("addBuyGoods".equals(baseType)){
            	List<Map<String,Object>> list = (List<Map<String,Object>>) getParameter(map, "buyGoods");
                data = bo.addBuyGoods(list, uid);
            }else if("askForAuthShop".equals(baseType)){
            	String bid = (String) getParameter(map, "bid");
            	String cimg = (String) getParameter(map, "cimg");
                data = bo.askForAuthShop(bid, cimg, uid);
            }else if("askForUnAuthShop".equals(baseType)){
            	String bname = (String) getParameter(map, "bname");
            	String btype = (String) getParameter(map, "btype");
            	String[] bimg = (String[]) getParameter(map, "bimg");
            	String bontime = (String) getParameter(map, "bontime");
            	String bphone = (String) getParameter(map, "bphone");
            	String bresume = (String) getParameter(map, "bresume");
            	String baddress = (String) getParameter(map, "baddress");
            	String blongpoi = (String) getParameter(map, "blongpoi");
            	String blatipoi = (String) getParameter(map, "blatipoi");
                data = bo.askForUnAuthShop(bname, btype, bimg, bphone, bresume, bontime, baddress, blongpoi, blatipoi);
            }else if("addShop".equals(baseType)){
            	String bid = (String) getParameter(map, "bid");
            	String ssubname = (String) getParameter(map, "ssubname");
            	String[] simg = (String[]) getParameter(map, "simg");
            	String saddress = (String) getParameter(map, "saddress");
            	String slongpoi = (String) getParameter(map, "slongpoi");
            	String slatipoi = (String) getParameter(map, "slatipoi");
            	String scity = (String) getParameter(map, "scity");
            	String stype = (String) getParameter(map, "stype");
            	String sphone = (String) getParameter(map, "sphone");
            	String sresume = (String) getParameter(map, "sresume");
            	String sontime = (String) getParameter(map, "sontime");
                data = bo.addShop(bid, ssubname, simg, saddress, slongpoi, slatipoi, scity, stype, sphone, sresume, sontime);
            }else if("addType".equals(baseType)){
            	String tname = (String) getParameter(map, "tname");
                data = bo.addType(tname);
            }else if("getTypes".equals(baseType)){
                data = bo.getTypes();
            }else if("getCitys".equals(baseType)){
                data = bo.getCitys();
            }else if("upGood".equals(baseType)){
            	String gid = (String) getParameter(map, "gid");
            	String gname = (String) getParameter(map, "gname");
            	String goriprice = (String) getParameter(map, "goriprice");
            	String gprice = (String) getParameter(map, "gprice");
            	String gimg = (String) getParameter(map, "gimg");
            	String isall = (String) getParameter(map, "isall");
                data = bo.upGood(gid, gname, goriprice, gprice, gimg, isall);
            }else if("upPerfer".equals(baseType)){
            	String cid = (String) getParameter(map, "cid");
            	String cname = (String) getParameter(map, "cname");
            	String ccontent = (String) getParameter(map, "ccontent");
            	String cdescirbe = (String) getParameter(map, "cdescirbe");
            	String cdeadline = (String) getParameter(map, "cdeadline");
            	String cimg = (String) getParameter(map, "cimg");
            	String isall = (String) getParameter(map, "isall");
                data = bo.upPerfer(cid, cname, ccontent, cdescirbe, cdeadline, cimg, isall);
            }else if("upBusiness".equals(baseType)){
            	String bid = (String) getParameter(map, "bid");
            	String bname = (String) getParameter(map, "bname");
            	String btype = (String) getParameter(map, "btype");
            	String[] bimg = (String[]) getParameter(map, "bimg");
            	String bontime = (String) getParameter(map, "bontime");
            	String bphone = (String) getParameter(map, "bphone");
            	String bresume = (String) getParameter(map, "bresume");
            	String baddress = (String) getParameter(map, "baddress");
            	String blongpoi = (String) getParameter(map, "blongpoi");
            	String blatipoi = (String) getParameter(map, "blatipoi");
                data = bo.upBusiness(bid, bname, btype, bimg, bphone, bresume, bontime, baddress, blongpoi, blatipoi);
            }else if("upShop".equals(baseType)){
            	String ssubname = (String) getParameter(map, "ssubname");
            	String[] simg = (String[]) getParameter(map, "simg");
            	String saddress = (String) getParameter(map, "saddress");
            	String slongpoi = (String) getParameter(map, "slongpoi");
            	String slatipoi = (String) getParameter(map, "slatipoi");
            	String scity = (String) getParameter(map, "scity");
            	String stype = (String) getParameter(map, "stype");
            	String sphone = (String) getParameter(map, "sphone");
            	String sresume = (String) getParameter(map, "sresume");
            	String sontime = (String) getParameter(map, "sontime");
                data = bo.upShop(sid, ssubname, simg, saddress, slongpoi, slatipoi, scity, stype, sphone, sresume, sontime);
            }else{
            	throw new BusinessException("请求类型异常！");
            }
            if(!vo.isBusinessException()){
                logger.info(logStart + "耗时：" + (System.currentTimeMillis() - startTime));
                if(data == null){
                    vo.setIsNull(true); 
                }else{
                    vo.setIsNull(false);
                    vo.setData(hiddleData(data));
                }
            }else{
                logger.error(SJHUAXUN + (String) vo.getData());
            }
        } catch (Exception e) {
            // 如果是业务异常
            if (e instanceof BusinessException) {
                logger.error(logStart + "业务异常：" + e.getMessage());
                vo.setData(e.getMessage());
            // 代码异常，客户端用户当做服务器异常，处理
            }else{
                logger.error(logStart + "服务器异常：" + e.getMessage(), e);
                vo.setData("操作异常。");
            }
            vo.setBusinessException(true);
        } finally{
            close(ois);
        }
        writeObject(vo, response);
    }
  
    @SuppressWarnings("unchecked")
	private Object hiddleData(Object data) {
    	if(data instanceof Map){
    		return toHashMap((Map<String, Object>) data);
    	}else if(data instanceof List){
    		return toArrayList((List<Map<String, Object>>) data);
    	}
    	return data;
	}
	
    /**
     * 吧 hibernate的list转成 ArrayList
     * @param list
     * @return
     */
    public static List<Map<String, Object>> toArrayList(List<Map<String, Object>> list){
        List<Map<String, Object>> returnList = new ArrayList<Map<String,Object>>();
        for (Map<String, Object> map : list) {
            returnList.add(toHashMap(map));
        }
        return returnList;
    }
    
    /**
     * 把 hibernate的map转成 HashMap
     * @param list
     * @return
     */
    public static Map<String, Object> toHashMap(Map<String, Object> map){
        Map<String, Object> returnMap = new HashMap<String, Object>();
        for(Map.Entry<String, Object> entry: map.entrySet()) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }
    

	/**
     * 关闭资源
     * @param close
     */
    public static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
                // 不可能存在的异常
                logger.error("不可能存在的异常", e);
            }
        }
    }

    /**
     * 数据发送
     * @param vo
     * @param response
     */
    private void writeObject(ReturnVO vo, HttpServletResponse response){
        try {
            ObjectOutputStream ois = new ObjectOutputStream(response.getOutputStream());
            ois.writeObject(vo);
            ois.flush();
            ois.close();
        } catch (Exception e) {
            logger.error(SJHUAXUN + "数据无法发送..." + e.getMessage(), e);
        }
    }

    /**
     * 获得map的键值
     */
    private Object getParameter(Map<String, Object> map, String str){
        if(map == null) return null;
        return map.get(str);
    }
    
    /**
     * 获得map的键值
     */
    private int getParameterInt(Map<String, Object> map, String str){
        if(map == null) return 0;
        if(map.get(str) == null) return 0;
        return (Integer) map.get(str);
    }
}
