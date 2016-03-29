package com.myou.appback.get;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;


import net.sf.json.JSONObject;

 
public class WeizhangTexst {

 public static String URL ="http://www.cheshouye.com";
private static final String appId="505";     
private static final String appKey="e11317049537f9d2352ad6711a6b4ae4"; 	 
	/**  
	 * title:测试方法
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
//		bubbleSort(null, 0);
		
		String carInfo = "{hphm=豫BH1117&classno=LGBH12E29AY053314&engineno=569432D&city_id=15&car_type=02}";
//		System.out.println(Encode.encode(carInfo, false)+"\n"+
//				md5(carInfo)
//				);
		try {
			
			System.out.println("请稍后...");
			String sb = getWeizhangInfoPost(carInfo);
			System.out.println("返回违章结果：" +  sb +"\n");
			getMap(sb).get("historys");
			List<Map<String, Object>> oneList=(List<Map<String, Object>>) getMap(sb).get("historys");
			
			System.out.println(oneList.get(0).get("id"));
			
//			获取省份城市字典配置
//			System.out.println("附录一   获取省份城市参数接口参数="+getConfig());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Object> getMap(String sb){
		
		return   (JSONObject)JSONObject.fromObject(sb);
	}
	/**
	 * title:获取违章信息
	 * 
	 * @param carInfo
	 * @return
	 */
	public static String getWeizhangInfoPost(String carInfo ) {
		long timestamp = System.currentTimeMillis();

		String line = null;
		String signStr = appId + carInfo + timestamp + appKey;
		String sign = md5(signStr);
		try {
			URL postUrl = new URL(URL + "/api/weizhang/query_task?");
			String content = "car_info=" + URLEncoder.encode(carInfo, "utf-8") + "&sign=" + sign + "&timestamp=" + timestamp + "&app_id=" + appId;
		
			System.out.println("请求URL="+postUrl+content);
			
			line = post(postUrl, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

	/**
	 * title:获取省份城市对应ID配置
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getConfig() throws IOException {
		String line = null;
		try {
			URL postUrl = new URL(URL + "/api/weizhang/get_all_config?");
			String content = "";
			line = post(postUrl, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;

	}

	/**
	 * title:post请求
	 * 
	 * @param postUrl
	 * @param content
	 * @return
	 */
	private static String post(URL postUrl, String content) {
		String line = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			out.writeBytes(content);
			out.flush();
			out.close();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			while ((line = reader.readLine()) != null) {
				return line;
			}
			reader.close();
			connection.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;

	}

	/**
	 * title:md5加密,应与 (http://tool.chinaz.com/Tools/MD5.aspx) 上32加密结果一致
	 * 
	 * @param password
	 * @return
	 */
	private static String md5(String msg) {
		try {
			MessageDigest instance = MessageDigest.getInstance("MD5");
			instance.update(msg.getBytes("UTF-8"));
			byte[] md = instance.digest();
			return byteArrayToHex(md);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder();
		for (byte b : a) {
			sb.append(String.format("%02x", b & 0xff));
		}
		return sb.toString();
	}
	
	
	public static void bubbleSort(int a[], int n){  
	int	b[] ={25,6,1,9,9,7,8,2,1,3};
	a=b;
		n=a.length;
		for (int i = 0; i < n-1; i++) { 
				for (int j = 0; j < n-i-1; j++) {
					if(a[j]>a[j+1]){
						int t= a[j] ;  a[j]=a[j+1]; a[j+1]=t;
					}
				}
		}
		for (int i:a) {
			System.out.println(i);
		}
	}  
	 
public static int fina(){
	int a =1;
	try {
		return  a;
	} catch (Exception e) {
		// TODO: handle exception
	} finally{
		return  ++a;
	}
}
	
}