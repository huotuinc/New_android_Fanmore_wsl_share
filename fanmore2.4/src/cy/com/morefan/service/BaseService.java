package cy.com.morefan.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import cy.com.morefan.bean.MyJSONObject;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.L;
import cy.com.morefan.util.RSAUtil;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.util.Util;

public class BaseService {
	public static ConnectType apnType = ConnectType.CMNET;
	protected final static String ERROR_NET = "连接服务器失败,请重试...";
	protected final static String ERROR_DATA = "数据解析出错,请重试...";
	protected final static String ERROR_NULL = "没有查询到你所需要的结果...";
	protected static final String ERROR_SAFE_KEY = "登录已超时,请重新尝试...";

//	protected static String[] ImageUrls = new String[]{"http://pic14.nipic.com/20110611/4545012_184235301165_2.jpg",
//		"http://p4.zbjimg.com/task/2012-02/20/1429838/4f41df6678a36.jpg",
//		"http://www.sinaimg.cn/dy/slidenews/4_img/2013_48/704_1164134_736660.jpg",
//		"http://g.hiphotos.baidu.com/image/w%3D2048/sign=8aa2e6b3820a19d8cb03830507c2828b/d53f8794a4c27d1e940684c619d5ad6eddc4388b.jpg",
//		"http://e.hiphotos.baidu.com/image/w%3D2048/sign=0dd4f3d2c1cec3fd8b3ea075e2b0d53f/72f082025aafa40fff3dc0c3a964034f78f01930.jpg",
//		"http://b.hiphotos.baidu.com/image/w%3D2048/sign=452052281f950a7b753549c43ee962d9/f31fbe096b63f6246248301d8544ebf81a4ca392.jpg",
//		"http://b.hiphotos.baidu.com/image/w%3D2048/sign=1cd06cf483cb39dbc1c06056e42e0924/b64543a98226cffcdf03b7acbb014a90f603ea87.jpg",
//		"http://c.hiphotos.baidu.com/image/w%3D2048/sign=983759160ed79123e0e09374990c5882/cf1b9d16fdfaaf51a16988a18e5494eef01f7a21.jpg",
//		"http://e.hiphotos.baidu.com/image/w%3D2048/sign=02af4ba3ab773912c4268261cc218718/622762d0f703918f544030d4533d269759eec41f.jpg",
//		"http://f.hiphotos.baidu.com/image/w%3D2048/sign=0d04bb36347adab43dd01c43bfecb31c/503d269759ee3d6da0e68fd541166d224f4ade88.jpg",
//		"http://e.hiphotos.baidu.com/image/w%3D2048/sign=583c50af820a19d8cb03830507c2838b/d53f8794a4c27d1e469832da19d5ad6eddc43875.jpg"};
//

	public enum ConnectType {
		CMWAP, CMNET, WIFI, UNIWAP, Unknow
	}

	protected BusinessDataListener listener;
	public BaseService() {
	}
	public BaseService(BusinessDataListener dataListener) {
		this.listener = dataListener;
	}
	public void setUserData(String userName, String pwd, MyJSONObject myUserData) throws JSONException{
		//MyJSONObject myUserData = myJSONObject.getJSONObject("userData");
		UserData userData = UserData.getUserData();
		userData.TotalBrowseAmount = myUserData.getString("TotalBrowseAmount");
		userData.TotalTurnAmount  = myUserData.getString("TotalTurnAmount");
		userData.PrenticeAmount  = myUserData.getString("PrenticeAmount");
		userData.levelName   = myUserData.getString("levelName");
		userData.completeInfo = myUserData.getBoolean("completeInfo");
//		userData.dayCheckIn = myUserData.getInt("dayCheckIn") == 1;
//		userData.checkInDays = myUserData.getInt("checkInDays");
//		userData.exp = myUserData.getInt("exp");
		userData.picUrl = myUserData.getString("userHead"); // myUserData.getString("picUrl");
		userData.isLogin = true;
		userData.loginCode = myUserData.getString("loginCode");
		userData.phone = myUserData.getString("mobile");
		userData.userName = userName;
		userData.pwd = pwd;
//		userData.totalScore = Util.opeDouble(myUserData.getDouble("totalScore"));//myUserData.getInt("totalScore");
		userData.shareDes = myUserData.getString("shareDes");
		userData.UserNickName=myUserData.getString("UserNickName");
		userData.RealName=myUserData.getString("RealName");
		userData.shareContent = myUserData.getString("shareContent");
//		userData.lockScore = Util.opeDouble(myUserData.getDouble("lockScore"));//myUserData.getInt("lockScore");
		userData.completeTaskCount = myUserData.getInt("completeTaskCount");
		userData.totalTaskCount = myUserData.getInt("totalTaskCount");
		userData.payAccount = myUserData.getString("alipayId");
		userData.toCrashPwd = myUserData.getString("withdrawalPassword");
		userData.regTime = Util.DateFormatFull(myUserData.getString("regTime"));
		//userData.yesScore = Util.opeDouble(myUserData.getDouble("yesScore"));//myUserData.getInt("yesScore");
		//userData.score = Util.opeDouble(myUserData.getDouble("score"));//myUserData.getInt("score");
		//userData.favCount = myUserData.getInt("favoriteAmount");
		userData.sendCount = myUserData.getInt("turnAmount");
		//userData.crashCount = myUserData.getString("crashCount");
		//userData.welfareCount = myUserData.getInt("welfareCount");
		userData.todayScanCount = myUserData.getInt("todayBrowseCount");
		//judgeEmulator是否进行模拟器判断，0进行;1不进行
		userData.ignoreJudgeEmulator = myUserData.getInt("judgeEmulator") == 1;
		userData.isSuper =myUserData.getBoolean("isSuper");
//		try {
//			JSONArray msg = myUserData.getJSONArray("msg");
//			if (msg != null)
//				userData.hasNewFeedback = msg.length() != 0;
//		}catch (Exception ex){}
	}

	public TaskData setTaskData(MyJSONObject tip) throws JSONException {
		TaskData item = new TaskData();
		item.rebate = tip.getString("rebate");
		item.flagLimitCount = tip.getInt("flagLimitCount");
		item.flagHaveIntro = tip.getInt("flagHaveIntro");
		item.flagShowSend = tip.getInt("flagShowSend");

		item.totalScanCount = tip.getInt("totalScanCount");
		item.id = tip.getInt("taskId");
		item.taskName = tip.getString("taskName");
		item.smallImgUrl = tip.getString("taskSmallImgUrl");
//		NumberFormat nf=NumberFormat.getInstance();
//		nf.setGroupingUsed(false);
//		item.totalScore = nf.format(tip.getDouble("totalScore"));//tip.getInt("totalScore");
//		item.lastScore = nf.format(tip.getDouble("lastScore"));//tip.getInt("lastScore");
//		item.lastScore = Double.parseDouble(item.lastScore) < 0 ? "0" : item.lastScore;
		String sendList = tip.getString("sendList");
		if(TextUtils.isEmpty(sendList)){
			item.channelIds = new ArrayList<String>();
		}else{
			String[] channelIds = sendList.split(",");
			item.channelIds = new ArrayList<String>(Arrays.asList(channelIds));
		}
		item.isSend = item.channelIds.size() == 0 ? false : true;
//		item.startDate = Util.DateFormatFull(tip.getString("startTime"));
//		item.endDate = Util.DateFormatFull(tip.getString("endTime"));
		String time = tip.getString("orderTime");
		item.startTime = Util.DateFormatFull(time);
		item.isTop = tip.getInt("IsTopTask");
		//s
 		//item.advTime = Util.DateFormatFull(tip.getString("advancedseconds"));//tip.getInt("advancedseconds") + "";
		item.creatTime = Util.DateFormat(time);
		item.dayCount = Util.getDayCount(time);
		item.dayDisDes = Util.getDayDisDes(time);
		item.awardSend = Util.opeDouble(tip.getDouble("awardSend"));
		item.awardScan = Util.opeDouble(tip.getDouble("awardScan"));
		item.awardLink = Util.opeDouble(tip.getDouble("awardLink"));
		item.storeId = tip.getString("storeId");
		item.store = tip.getString("storeName");
		item.content = tip.getString("taskInfo");
		item.sendCount = tip.getInt("sendCount");
		item.browseCount = tip.getInt("browseCount");
		item.ShowTurnButton=tip.getInt("ShowTurnButton");
		item.status = tip.getInt("status");
		item.type = tip.getInt("type");
		//item.type = 1001000;
		//状态
		item.isFav = tip.getBoolean("isFav");
		//item.isSend = tip.getBoolean("isSend");
		item.isAccount = tip.getBoolean("isAccount");//是否结算
		//收益
		item.myAwardSend = Util.opeDouble(tip.getDouble("awardSendResult"));
		item.myAwardScan = Util.opeDouble(tip.getDouble("awardScanResult"));
		item.myAwardLink = Util.opeDouble(tip.getDouble("awardLinkResult"));
		//昨日收益
		item.myYesAwardSend = Util.opeDouble(tip.getDouble("awardYesSendResult"));
		item.myYesAwardScan = Util.opeDouble(tip.getDouble("awardYesScanResult"));
		item.myYesAwardLink = Util.opeDouble(tip.getDouble("awardYesLinkResult"));
		//我的参与表中自增id，分页用
		item.partInAutoId = tip.getInt("partInAutoId");
		//online 0 不可提前转发 1 可提前转发 2 已上线
		item.online = tip.getInt("online");

		//预告闹钟时间
		item.alarmTime = TimeUtil.FormatterTimeToMinute2(item.startTime);
		item.alarmTimePre = TimeUtil.FormatterTimeToMinute2(item.advTime);
		return item;

	};



	protected MyJSONObject getDataFromSer(String url) {
		MyJSONObject data = null;
		InputStreamReader isReader = null;
		BufferedReader bufferReader = null;
		try {
			DefaultHttpClient client = httpConnection();
			//HttpPost post = new HttpPost(url);
			HttpGet post = new HttpGet(url);
			String uAgent = Build.PRODUCT + "," + Build.VERSION.SDK_INT;
			System.out.println(">>>>>" + Build.PRODUCT);
			post.setHeader("User-Agent", uAgent);
			// String key = URLEncoder.encode(UserData.getUser().getPassword() +
			// UserData.getUser().getName(), "UTF-8");
			// post.setHeader("Authorization", key);

			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();
			MyJSONObject result = readData(entity);
			//检查该请求是否有经验返回
			checkExp(result);
			return result;
		} catch (IOException e) {
			// ////////
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			try {
				if (isReader != null)
					isReader.close();
				if (bufferReader != null)
					bufferReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
	private void checkExp(MyJSONObject result) {
		int exp = result.getInt("exp");
		if(exp > 0 && null != listener){
			UserData.getUserData().exp += exp;
			Bundle extra = new Bundle();
			extra.putInt("exp", exp);
			listener.onDataFinish(BusinessDataListener.DONE_EXP_UP, null, null, extra);
		}


	}
	private DefaultHttpClient httpConnection() {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 10000;// 连接超时
		HttpConnectionParams.setConnectionTimeout(httpParameters,timeoutConnection);
		int timeoutSocket = 15000;// 数据传输超时
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		chooseAPNType(client);
		return client;
	}

	protected void chooseAPNType(DefaultHttpClient client) {
		if (apnType == ConnectType.CMWAP || apnType == ConnectType.UNIWAP) {
			HttpHost proxy = new HttpHost("10.0.0.172", 80);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}
	}

	private MyJSONObject readData(HttpEntity entity) throws IOException,JSONException {
		InputStreamReader isReader = null;
		BufferedReader bufferReader = null;

		isReader = new InputStreamReader(entity.getContent());
		bufferReader = new BufferedReader(isReader, 2048);
		StringBuffer sb = new StringBuffer();

		String result = bufferReader.readLine();
		while (result != null) {
			sb.append(result);
			result = bufferReader.readLine();
		}
		return new MyJSONObject(sb.toString());
	}

	protected HttpURLConnection openMyConnection(String cUrl)throws IOException {
		HttpURLConnection hc = null;
		if (apnType == ConnectType.CMWAP || apnType == ConnectType.UNIWAP) {
			hc = requestHttpByWap(cUrl);
		} else {
			URL url = new URL(cUrl);
			hc = (HttpURLConnection) url.openConnection();
		}
		return hc;
	}

	public HttpURLConnection requestHttpByWap(String st) throws IOException {
		String urlt = st;
		String pUrl = "http://10.0.0.172";
		urlt = urlt.replace("http://", "");
		pUrl = pUrl + urlt.substring(urlt.indexOf("/"));
		urlt = urlt.substring(0, urlt.indexOf("/"));

		URL url = new URL(pUrl);
		HttpURLConnection hc = (HttpURLConnection) url.openConnection();

		hc.setRequestProperty("X-Online-Host", urlt);
		hc.setDoInput(true);

		return hc;
	}



	// protected MyJSONObject getDataFromSer(String url,List<NameValuePair>
	// postData) {
	// MyJSONObject data = null;
	// InputStreamReader isReader = null;
	// BufferedReader bufferReader = null;
	// try {
	// DefaultHttpClient client = httpConnection();
	// HttpPost post = new HttpPost(url);
	// String uAgent = Build.PRODUCT + "," + Build.VERSION.SDK_INT;
	// post.setHeader("User-Agent", uAgent);
	// // String key = URLEncoder.encode(UserData.getUser().getPassword() +
	// // UserData.getUser().getName(), "UTF-8");
	// // post.setHeader("Authorization", key);
	//
	// post.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));
	//
	// HttpResponse resp = client.execute(post);
	// HttpEntity entity = resp.getEntity();
	//
	// return readData(entity);
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// if (isReader != null)
	// isReader.close();
	// if (bufferReader != null)
	// bufferReader.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return data;
	// }


//	protected final void encodedStr(String key) {
//		if (key != null) {
//			char[] desChar = resKey.toCharArray();
//			Encode(key.toCharArray(), desChar);
//			desKey = String.valueOf(desChar);
//		}
//	}

//	protected void Encode(char[] key, char[] data) {
//		int iLen = data.length;
//		for (int i = 0; i < iLen; i++) {
//			int iIndex = FindCharSet(data[i]);
//			data[i] = CharSet[(key[i % 8] + iIndex) % 62];
//		}
//	}

//	protected int FindCharSet(char key) {
//		for (int i = 0; i < 62; i++) {
//			if (CharSet[i] == key)
//				return i;
//		}
//		return 0;
//	}

	protected String CONSTANT_URL() {
		//operation=HuoTu2013AD/HuoTu2013IP&version=appVersion&imei=*****
		//BusinessStatic.IMEI = BusinessStatic.IMEI + 2;
		 String url = "&operation=" + Constant.OPERATION + "&qd=" + Constant.QD +"&version="
		 + Constant.APP_VERSION + "&citycode=" + BusinessStatic.getInstance().CITY_CODE
		 + "&imei=" + BusinessStatic.getInstance().IMEI + "&lat=" + BusinessStatic.getInstance().USER_LAT + "&lng=" + BusinessStatic.getInstance().USER_LNG
		 + "&sign="+ getSign() + "&p=";
		return url;
	}
	protected String CONSTANT_URL2() {
		//operation=HuoTu2013AD/HuoTu2013IP&version=appVersion&imei=*****
		//BusinessStatic.IMEI = BusinessStatic.IMEI + 2;
		 String url = "&operation=" + Constant.OPERATION + "&qd=" + Constant.QD +"&version="
		 + Constant.APP_VERSION + "&citycode=" + BusinessStatic.getInstance().CITY_CODE
		 + "&imei=" + BusinessStatic.getInstance().IMEI + "&lat=" + BusinessStatic.getInstance().USER_LAT + "&lng=" + BusinessStatic.getInstance().USER_LNG
		 + "&sign="+ getSign() + "&";
		return url;
	}


	private String getSign(){
		JSONObject sign = new JSONObject();
		try {
			sign.put("serial", System.currentTimeMillis());
			sign.put("imei6", BusinessStatic.getInstance().IMEI.substring(BusinessStatic.getInstance().IMEI.length() - 6));
			sign.put("key", "caonimei");
			Key publicKey = RSAUtil.getPublicKeyByModuleAndExponent(RSAUtil.module, RSAUtil.exponentString);
			String result = RSAUtil.encrypt(sign.toString(), publicKey);
			return URLEncoder.encode(result , "utf-8");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	protected String CONSTANT_URL_POST() {
		String url = null;// "&operation=" + Constant.OPERATION + "&version="
		// + Constant.VERSION + "&citycode=" + BusinessStatic.CITY_CODE
		// + "&imei=" + BusinessStatic.IMEI;

		return url;
	}
}
