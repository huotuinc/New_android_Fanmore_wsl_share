package cy.com.morefan.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import cy.com.morefan.bean.AllScoreData;
import cy.com.morefan.bean.AwardData;
import cy.com.morefan.bean.MyJSONObject;
import cy.com.morefan.bean.PartInItemData;
import cy.com.morefan.bean.PartnerData;
import cy.com.morefan.bean.PrenticeData;
import cy.com.morefan.bean.RankData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserBaseInfoList;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.bean.WeekTaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.AuthParamUtils;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.UserInfoView.Type;
import cy.lib.libhttpclient.CyHttpClient;

public class UserService extends BaseService{
	long timestamp = System.currentTimeMillis();
	public UserService(BusinessDataListener listener) {
		super(listener);
	}
	public void commitPhoto(final String loginCode, final String imgs){

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
			    try {
			    	String url = Constant.IP_URL + "/Api.ashx?req=UploadPicture" + CONSTANT_URL2();
			    	JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("pic", imgs);
//					jsonUrl.put("timestamp", timestamp);
//					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
//					Map< String, String > paramMap = new HashMap< String, String >( );
//					paramMap.put("loginCode",loginCode);
//					paramMap.put("pic", imgs);
//					paramMap.put("timestamp",String.valueOf(timestamp));
//					String url2 = authParamUtils.getMapSign1(paramMap);
//					jsonUrl.put("sign",url2);
					String	p = URLEncoder.encode(jsonUrl.toString(),"UTF-8");

			    	NameValuePair imgs1 = new BasicNameValuePair("p",p);
			    	//NameValuePair imgs2 = new BasicNameValuePair("pic",imgs);

			    	 cy.lib.libhttpclient.MyJSONObject data = CyHttpClient.post(url,imgs1);
			    	// L.i(">>>>result:" + response);
			    	 //test
			    	 //response = "{resultCode:1,description:\"成功\",status:1,version:1,updateCode:0,bannerData:[{imgUrl:\"http://img0.bdstatic.com/img/image/shouye/dengni23.jpg\",title:\"美女0\",detail:\"http://img0.bdstatic.com/img/image/shouye/dengni23.jpg\"},{imgUrl:\"http://img0.bdstatic.com/img/image/fushixiazhuang.jpg\",title:\"美女1\",detail:\"http://img0.bdstatic.com/img/image/fushixiazhuang.jpg\"},{imgUrl: \"http://img0.bdstatic.com/img/image/shouye/muqz54.jpg\",title:\"美女2\",detail:\"http://img0.bdstatic.com/img/image/shouye/muqz54.jpg\"}]}";
			    	 if( null != data){
			    		// MyJSONObject data = new MyJSONObject(response);
			    		 int resultCode = data.getInt("resultCode");
				    	 if(resultCode == 1){
							int status = data.getInt("status");
							if (status == 1) {
								UserData.getUserData().picUrl=data.getJSONObject("resultData").getString("picUrl");
								listener.onDataFinish(BusinessDataListener.DONE_COMMIT_PHOTO,null, null, null);
							} else {
								listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_PHOTO,data.getString("tip"), null);
							}
						} else {
							listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_PHOTO,data.getString("description"), null);
						}
			    	 }else{
			    		listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_PHOTO, ERROR_NET, null);

			    	 }

			    } catch (Exception e) {
			    	listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_PHOTO, ERROR_DATA, null);
			    }
			}
		});
	}


	public void getPreTaskList(final Context mContext, final String loginCode, final int pageSize, final int id){
		if(id == 0)



		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=PreviewTaskList" + CONSTANT_URL();
					//String url = Constant.IP_URL + "/Api.ashx?req=TodayNotice" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
					jsonUrl.put("screenType", 0);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("oldTaskId", id);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode == null ? "" : loginCode);
					paramMap.put("screenType", String.valueOf(0));
					paramMap.put("pageSize", String.valueOf(pageSize));
					paramMap.put("oldTaskId", String.valueOf(id));
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("PreviewTaskList:" + url);
					MyJSONObject data = getDataFromSer(url);


					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject myJSONObject = data.getJSONObject("resultData");
								JSONArray jArray = myJSONObject.getJSONArray("taskData");
								//JSONArray jArray = data.getJSONArray("resultData");
								int length = jArray.length();
								TaskData[] results = new TaskData[length];
								for (int i = 0; i < length; i++) {
									MyJSONObject tip = (MyJSONObject) jArray.get(i);
									results[i] = setTaskData(tip);
									results[i].partInAutoId = results[i].id;
								}

								//更新本地闹钟
								String preDate = SPUtil.getStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_DATE);
								String curDate = TimeUtil.getCurDate();
								if(!preDate.equals(curDate)){
									SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_ALARM, null);
								}
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_DATE, curDate);


								listener.onDataFinish(BusinessDataListener.DONE_GET_MY_PARTIN_LIST, null, results, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_LIST, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_LIST, ERROR_NET, null);

				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}



			}
		});

	}



	public void WeekTask(final String loginCode){

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=WeekTask" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode );
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");

					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								JSONArray array = data.getJSONArray("resultData");
								int length = array.length();
								WeekTaskData[] results = new WeekTaskData[length];
								for(int i = 0; i < length; i++) {
									MyJSONObject tip = (MyJSONObject) array.getJSONObject(i);
									WeekTaskData itemData = new WeekTaskData();
									itemData.id = tip.getInt("id");
									itemData.title = tip.getString("title");
									itemData.sort = tip.getInt("sort");
									itemData.myFinishCount = tip.getInt("myFinishCount");
									itemData.target = tip.getInt("target");

									results[i] = itemData;
								}





								listener.onDataFinish(BusinessDataListener.DONE_GET_WEEKTASK, null, results, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_WEEKTASK, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_WEEKTASK, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_WEEKTASK, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_WEEKTASK, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});


	}

	/**
	 *
	 * @param loginCode
	 * @param type 1：每日积分排名
                   2：总积分排名
                   3：收徒排名

	 */
	public void getRank(final String loginCode, final int type){

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=ranking" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("type", type);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("type", String.valueOf(type));
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("RANKING:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");
								MyJSONObject myrank =json.getJSONObject("myRank");
								Bundle extra = new Bundle();
								if (myrank==null) {
									return;
								}else {
									extra.putString("myRankvalue", myrank.getString("rankValue"));
									extra.putString("myRanklogo", myrank.getString("logo"));
									extra.putString("myRankname", myrank.getString("name"));
									extra.putString("myRankDes", myrank.getString("value"));
								}
								JSONArray array = json.getJSONArray("rankList");
								int length = array.length();
								RankData[] results = new RankData[length];
								for(int i = 0; i < length; i++){
									MyJSONObject tip = (MyJSONObject) array.getJSONObject(i);
									RankData itemData = new RankData();
									itemData.rankValue = tip.getInt("rankValue");
									itemData.value =tip.getInt("value");
									itemData.name =tip.getString("name");
									itemData.logo =tip.getString("logo");

									results[i] = itemData;
								}

								extra.putSerializable("list", results);
								listener.onDataFinish(BusinessDataListener.DONE_GET_RANK, null, results, extra);


							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_RANK, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_RANK, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_RANK, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_RANK, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});


	}


	/**
	 *
	 * @param loginCode
	 * @param pageIndex (0、拜师时间1、总贡献值)
	 */
	public void getPrenticeList(final String loginCode,final int pageIndex){



		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=PrenticeList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageIndex", pageIndex);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("pageIndex", String.valueOf(pageIndex));
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("getPrenticeList:" + url);

					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						Bundle extra = null;
						extra = new Bundle();
						extra.putInt("pageIndex", data.getInt("pageIndex"));
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");

								if(pageIndex==0){

									extra.putInt("prenticeCount", json.getInt("totalCount"));
								}
								JSONArray array = json.getJSONArray("list");
								int length = array.length();
								PartnerData[] testResults = new PartnerData[length];
								for(int i = 0; i < length; i++){
									MyJSONObject tip = (MyJSONObject) array.getJSONObject(i);
									PartnerData item = new PartnerData();
									item.headFace=tip.getString("headFace");
									item.userName = tip.getString("userName");
									item.yesterdayBrowseAmount=tip.getString("yesterdayBrowseAmount");
									item.historyTotalBrowseAmount=tip.getString("historyTotalBrowseAmount");
									item.yesterdayTurnAmount=tip.getString("yesterdayTurnAmount");
									item.historyTotalTurnAmount=tip.getString("historyTotalTurnAmount");

									testResults[i] = item;
								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_PRENTICE_LIST, null, testResults, extra);


							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_LIST, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_LIST, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}






	public void getPrentice(final String loginCode, final String pageTag, final int pageSize){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=ScorePrentice" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageTag", pageTag);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("pageTag", pageTag);
					paramMap.put("pageSize", String.valueOf(pageSize));
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("getPrentice:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");
								Bundle extra = null;
								if(pageTag.equals("0")){
									extra = new Bundle();
									extra.putString("invitationCode", json.getString("inviteCode"));
									extra.putString("prenticeDes", json.getString("desc"));
									extra.putString("prenticeShareDes", json.getString("shareDesc"));
									extra.putString("prenticeShareUrl", json.getString("shareUrl"));
									extra.putInt("prenticeAmount", json.getInt("prenticeAmount"));
									extra.putString("lastContri", Util.opeDouble(json.getDouble("yesterdayTotalScore")));
									extra.putString("totalContri", Util.opeDouble(json.getDouble("totalScore")));
									extra.putString("yesterdayBrowseAmount",Util.opeDouble(json.getDouble("yesterdayBrowseAmount")));
									extra.putString("historyTotalBrowseAmount",Util.opeDouble(json.getDouble("historyTotalBrowseAmount")));
									extra.putString("yesterdayTurnAmount",Util.opeDouble(json.getDouble("yesterdayTurnAmount")));
									extra.putString("historyTotalTurnAmount",Util.opeDouble(json.getDouble("historyTotalTurnAmount")));
									extra.putString("shareTitle",json.getString("shareTitle"));
								}
								JSONArray array = json.getJSONArray("list");
								int length = array.length();
								PrenticeData[] results = new PrenticeData[length];
								for(int i = 0; i < length; i++){
									MyJSONObject tip = (MyJSONObject) array.getJSONObject(i);
									PrenticeData item = new PrenticeData();
									item.invitationCode = tip.getString("inviteCode");
									item.prenticeDes = tip.getString("desc");
									item.prenticeShareDes = tip.getString("shareDesc");
									item.prenticeShareUrl = tip.getString("shareUrl");//tip.getInt("score");
									item.prenticeAmount = tip.getInt("prenticeAmount");//tip.getInt("totalScore");
									item.lastContri = Util.opeDouble(tip.getDouble("yesterdayTotalScore"));
									item.totalContri=Util.opeDouble(tip.getDouble("yesterdayTotalScore"));
									item.yesterdayBrowseAmount=Util.opeDouble(tip.getDouble("yesterdayBrowseAmount"));
									item.historyTotalBrowseAmount=Util.opeDouble(tip.getDouble("historyTotalBrowseAmount"));
									item.yesterdayTurnAmount=Util.opeDouble(tip.getDouble("yesterdayTurnAmount"));
									item.historyTotalTurnAmount=Util.opeDouble(tip.getDouble("historyTotalTurnAmount"));
									item.shareTitle = tip.getString("shareTitle");

									results[i] = item;
								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_PRENTICE, null, results, extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}





	/**
	 *
	 * @param loginCode
	 * @param type 1昨日，2指定日期
	 * @param date 指定日期收益记录
	 */
	public void getDayAward(final String loginCode, final int type, final String date){



		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=TotalScoreDay" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("type", type);
					jsonUrl.put("date", date);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("type", String.valueOf(type));
					paramMap.put("date", date);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("TotalScoreDay:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								JSONArray jsonArray = data.getJSONArray("resultData");
								int length = jsonArray.length();
								AwardData[] results = new AwardData[length];
								double score = 0;
								int scan = 0;
								for(int i = 0; i < length; i ++){
									MyJSONObject tip = (MyJSONObject) jsonArray.get(i);
									AwardData awardData = new AwardData();
									awardData.id = tip.getInt("id");
									awardData.type = tip.getInt("type");
									awardData.titile = tip.getString("title");
									awardData.des = tip.getString("description");
									awardData.imgUrl = tip.getString("imageUrl");
									awardData.scanCount = tip.getInt("browseAmount");
									awardData.score = Util.opeDouble(tip.getDouble("totalScore"));
									if(type == 1){
										awardData.date = tip.getString("date").split(" ")[0];
										score += Double.parseDouble(awardData.score);
										scan += awardData.scanCount;
									}else{
										awardData.date = tip.getString("date");
										awardData.dateDes = TimeUtil.FormatterTimeToMinute(awardData.date);
									}
									results[i] = awardData;
								}
								if(type == 1 && results.length > 0){
									results[0].dayScore = Util.opeDouble(score);
									results[0].dayScanCount = scan;
								}


								listener.onDataFinish(BusinessDataListener.DONE_GET_AWARD_LIST, null, results, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_AWARD_LIST, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_AWARD_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_AWARD_LIST, ERROR_NET, null);

				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_AWARD_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}



			}
		});






	}
	public void getTodayScanList(final String loginCode){


		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=TodayBrowseList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("TodayBrowseList:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {

								JSONArray jsonArray = data.getJSONArray("resultData");
								int length = jsonArray.length();
								TaskData[] results = new TaskData[length];
								for(int i = 0; i < length; i ++){
									MyJSONObject tip = (MyJSONObject) jsonArray.get(i);
									TaskData taskData = new TaskData();
									taskData.id= tip.getInt("id");
									taskData.store = tip.getString("title");
									taskData.taskName = tip.getString("desc");
									taskData.smallImgUrl = tip.getString("taskSmallImgUrl");
									taskData.status = tip.getInt("status");
									String time = tip.getString("date");
									taskData.creatTime = Util.DateFormat(time);
									taskData.dayCount = Util.getDayCount(time);
									taskData.dayDisDes = Util.getDayDisDes(time);
									taskData.sendCount = tip.getInt("sendAmount");
									String sendList = tip.getString("sendList");
									if(TextUtils.isEmpty(sendList)){
										taskData.channelIds = new ArrayList<String>();
									}else{
										String[] channelIds = sendList.split(",");
										taskData.channelIds = new ArrayList<String>(Arrays.asList(channelIds));
									}
									taskData.isSend = taskData.channelIds.size() == 0 ? false : true;
									taskData.dayScanCount = tip.getInt("todayBrowseAmount");
									taskData.totalScanCount = tip.getInt("totalAmount");

									results[i] = taskData;
								}

								listener.onDataFinish(BusinessDataListener.DONE_GET_TODAY_SCAN, null, results, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_TODAY_SCAN, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_TODAY_SCAN, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_TODAY_SCAN, ERROR_NET, null);

				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_TODAY_SCAN, ERROR_DATA, null);
					e.printStackTrace();
				}



			}
		});




	}
	/**
	 *
	 * @param loginCode
	 * @param pageSize
	 * @param pageDate 上一页最后date
	 */
	public void getAllScoreTrendList(final int userId, final ArrayList<AllScoreData> allScoreDatas, final String loginCode, final int pageSize, final String pageDate){

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=NewTotalScoreList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("currentUserId",userId);
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("date", pageDate);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("currentUserId",String.valueOf(userId));
					paramMap.put("loginCode", loginCode);
					paramMap.put("pageSize", String.valueOf(pageSize));
					paramMap.put("date", pageDate);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("NewTotalScoreList:" + url);

					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject resultData = data.getJSONObject("resultData");
								//首页时获取max,min
								Bundle extra = null;
								if(TextUtils.isEmpty(pageDate)){
									extra = new Bundle();
									extra.putInt("totalCount", resultData.getInt("totalCount"));
									extra.putString("totalScore", Util.opeDouble(resultData.getDouble("totalScore")));
									extra.putString("maxScore", Util.opeDouble(resultData.getDouble("maxScore")));
									extra.putString("minScore", Util.opeDouble(resultData.getDouble("minScore")));
								}

								JSONArray jsonArray = resultData.getJSONArray("itemData");
								int length = jsonArray.length();
								//AllScoreData[] results = new AllScoreData[length];
								//HashMap<String, AllScoreData> scoreDatas = new HashMap<String, AllScoreData>();
								ArrayList<AwardData> results = new ArrayList<AwardData>();
								for(int i = 0; i < length; i ++){
									MyJSONObject tip = (MyJSONObject) jsonArray.get(i);
									AllScoreData scoreData = new AllScoreData();
									scoreData.scanCount = tip.getInt("browseAmount");
									scoreData.score = Util.opeDouble(tip.getDouble("browseAmount"));//tip.getInt("totalScore");
									scoreData.date = tip.getString("date").split(" ")[0];
									allScoreDatas.add(scoreData);
									//scoreData.extra = tip.getString("extra").replaceAll(":", "").replaceAll("\\^", "\n");
									//results[i] = scoreData;
//									scoreDatas.put(scoreData.date, scoreData);
//									if(!allScoreDatas.contains(scoreData))
//

									JSONArray awardArray = tip.getJSONArray("awardList");
									int length2 = awardArray.length();
									//AwardData[] results = new AwardData[length2];
									for(int j = 0; j < length2; j ++){
										MyJSONObject tip2 = (MyJSONObject) awardArray.get(j);
										AwardData awardData = new AwardData();
										awardData.id = tip2.getInt("id");
										awardData.type = tip2.getInt("type");
										awardData.titile = tip2.getString("title");
										awardData.des = tip2.getString("description");
										awardData.imgUrl = tip2.getString("imageUrl");
										awardData.date = tip2.getString("date").split(" ")[0];
										awardData.dateDes = TimeUtil.FormatterTimeToMinute(tip2.getString("date"));
										awardData.scanCount = tip2.getInt("browseAmount");
										awardData.score = Util.opeDouble(tip2.getDouble("totalScore"));//tip2.getInt("totalScore");
										awardData.dayScanCount = scoreData.scanCount;
										awardData.dayScore = scoreData.score;
										results.add(awardData);
									}
								}

								listener.onDataFinish(BusinessDataListener.DONE_GET_ALLSCORE_TREND, null, results.toArray(new AwardData[]{}), extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_ALLSCORE_TREND, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_ALLSCORE_TREND, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_ALLSCORE_TREND, ERROR_NET, null);

				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_ALLSCORE_TREND, ERROR_DATA, null);
					e.printStackTrace();
				}



			}
		});


	}
	public void getScanCount() {
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				String url = Constant.IP_URL + "/Api.ashx?req=PreviewTaskCount" + CONSTANT_URL();
				JSONObject jsonUrl = new JSONObject();
				try {
                    url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
				L.i("GetUserTodayBrowseCount:" + url);

				MyJSONObject data = getDataFromSer(url);
				if(data != null){
                    int resultCode = data.getInt("resultCode");
                    if (resultCode == 1) {
                        int status = data.getInt("status");
                        if (status == 1) {
                            //更新内存量
							try {
								JSONObject resultData =data.getJSONObject("resultData");
								UserData.getUserData().TaskCount = resultData.getInt("count");
							} catch (JSONException e) {
								e.printStackTrace();
							}


                            listener.onDataFinish(BusinessDataListener.DONE_GET_SCANCOUNT, null, null, null);
                        }else{
                            listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK_LIST, data.getString("tip"), null);
                        }
                    }else{
                        String description = data.getString("description");
                        listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK_LIST, description, null);
                    }
                }else
                    listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK_LIST, ERROR_NET, null);


			}
		});
	}
	public void GetUserTodayBrowseCount(final String loginCode) {
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
				String url = Constant.IP_URL + "/Api.ashx?req=GetUserTodayBrowseCount" + CONSTANT_URL();
				JSONObject jsonUrl = new JSONObject();

					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
				try {
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				L.i("GetUserTodayBrowseCount:" + url);

				MyJSONObject data = getDataFromSer(url);
				if(data != null){
					int resultCode = data.getInt("resultCode");
					if (resultCode == 1) {
						int status = data.getInt("status");
						if (status == 1) {
							//更新内存量
							try {
								JSONObject resultData =data.getJSONObject("resultData");
								UserData.getUserData().todayScanCount = resultData.getInt("count");
							} catch (JSONException e) {
								e.printStackTrace();
							}


							listener.onDataFinish(BusinessDataListener.DONE_GET_SCANCOUNT, null, null, null);
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK_LIST, data.getString("tip"), null);
						}
					}else{
						String description = data.getString("description");
						listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK_LIST, description, null);
					}
				}else
					listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK_LIST, ERROR_NET, null);
				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});
	}


	/**
	 *
	 * @param loginCode
	 * @param taskId
	 * @param pageSize
	 * @param autoId 最早的参与id
	 * @param type 1昨日，2指定日期，3历史
	 * @param date
	 */
	public void getMyPartInDetail(final String loginCode, final int taskId, final int pageSize, final String autoId, final int type, final String date) {
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=NewScoreFlow" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
					jsonUrl.put("taskId", taskId);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("autoId", autoId);
					jsonUrl.put("type", type);
					jsonUrl.put("date", date);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("taskId", String.valueOf(taskId));
					paramMap.put("pageSize", String.valueOf(pageSize));
					paramMap.put("autoId", autoId);
					paramMap.put("type", String.valueOf(type));
					paramMap.put("date", date);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("NewScoreFlow:" + url);
					MyJSONObject data = getDataFromSer(url);


					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								Bundle extra = new Bundle();
								MyJSONObject jData = data.getJSONObject("resultData");
								extra.putInt("scanCount", jData.getInt("browseCount"));//浏览量
								extra.putInt("linkCount", jData.getInt("linkCount"));//外链点击量
								extra.putString("scoreCount", Util.opeDouble(jData.getDouble("dayScore")));//积分量
								extra.putString("totalScore", Util.opeDouble(jData.getDouble("totalScore")));//总收益

								JSONArray jArray = jData.getJSONArray("operationList");
								int length = jArray.length();
								PartInItemData[] results = new PartInItemData[length];
								for (int i = 0; i < length; i++) {
									MyJSONObject tip = (MyJSONObject) jArray.get(i);
									PartInItemData item = new PartInItemData();
									item.pageTag = tip.getString("id");
									String time = tip.getString("time");
									item.time = Util.DateFormatFull(time);
									item.date = TimeUtil.FormatterTimeToDay(time);
									item.des = tip.getString("operation");
									item.score = Util.opeDouble(tip.getDouble("score"));//tip.getString("score");
									item.channel = tip.getInt("channelType");

									results[i] = item;
								}
								extra.putSerializable("datas", results);
								listener.onDataFinish(BusinessDataListener.DONE_GET_MY_PARTIN_DETAIL, null, null, extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_DETAIL, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_DETAIL, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_DETAIL, ERROR_NET, null);

				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_DETAIL, ERROR_DATA, null);
					e.printStackTrace();
				}



			}
		});
	}

	/**
	 * 我的参与
	 *
	 * @param loginCode
	 * @param type 0全部，1昨日
	 * @param pageSize
	 * @param autoId 最早的参与id
	 */
	public void getMyPartIn(final String loginCode, final int type, final int pageSize, final int autoId) {
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=MyPartake" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
					jsonUrl.put("type", type);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("autoId", autoId);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("type", String.valueOf(type));
					paramMap.put("pageSize", String.valueOf(pageSize));
					paramMap.put("autoId", String.valueOf(autoId));
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("getMyPartIn:" + url);
					MyJSONObject data = getDataFromSer(url);


					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								JSONArray jArray = data.getJSONArray("resultData");
								int length = jArray.length();
								TaskData[] results = new TaskData[length];
								for (int i = 0; i < length; i++) {
									MyJSONObject tip = (MyJSONObject) jArray.get(i);
									results[i] = setTaskData(tip);
								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_MY_PARTIN_LIST, null, results, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_LIST, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_LIST, ERROR_NET, null);

				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_PARTIN_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}



			}
		});
	}
	/**
	 * 获取用户基本信息
	 * @param loginCode
	 */
	public void getUserBaseInfo(final String loginCode){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				//{"taskId":"1","loginCode":"0000"}
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=UserInfo" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("getUserBaseInfo>>>>>" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if(resultCode == 1){
							int status = data.getInt("status");
							if(status == 1){
								MyJSONObject myData = data.getJSONObject("resultData");
								Bundle extra = new Bundle();
								extra.putString("name", myData.getString("name"));
								extra.putInt("sex", myData.getInt("sex"));
								extra.putString("birth", Util.DateFormat(myData.getString("birth")));
								extra.putString("industry", myData.getString("industry"));
								extra.putString("industryId", myData.getString("industryId"));
								extra.putString("favorite", myData.getString("favorite"));
								extra.putString("favoriteId", myData.getString("favoriteId"));
								extra.putString("income", myData.getString("income"));
								extra.putString("incomeId", myData.getString("incomeId"));
								extra.putString("area", myData.getString("area"));


								UserBaseInfoList baseInfoList = new UserBaseInfoList();
								JSONArray jIndustry = myData.getJSONArray("industryList");
								int indLength = jIndustry.length();
								List<UserSelectData> indList = new ArrayList<UserSelectData>();
								for(int i = 0; i < indLength; i ++){
									MyJSONObject tip = (MyJSONObject) jIndustry.get(i);
									indList.add(new UserSelectData(tip.getString("name"), tip.getString("value")));
 								}
								baseInfoList.industryList = indList;

								JSONArray jFav = myData.getJSONArray("favoriteList");
								int favLength = jFav.length();
								List<UserSelectData> favList = new ArrayList<UserSelectData>();
								for(int i = 0; i < favLength; i ++){
									MyJSONObject tip = (MyJSONObject) jFav.get(i);
									favList.add(new UserSelectData(tip.getString("name"), tip.getString("value")));
 								}
								baseInfoList.favoriteList = favList;

								JSONArray jIncome = myData.getJSONArray("incomeList");
								int incomeLength = jIncome.length();
								List<UserSelectData> incomeList = new ArrayList<UserSelectData>();
								for(int i = 0; i < incomeLength; i ++){
									MyJSONObject tip = (MyJSONObject) jIncome.get(i);
									incomeList.add(new UserSelectData(tip.getString("name"), tip.getString("value")));
 								}
								baseInfoList.incomeList = incomeList;

								extra.putSerializable("list", baseInfoList);
//								extra.putString("industryList", myData.getString("industryList"));
//								extra.putString("favoriteList", myData.getString("favoriteList"));
//								extra.putString("incomeList", myData.getString("incomeList"));
								String description = data.getString("description");
								listener.onDataFinish(BusinessDataListener.DONE_USER_INFO, description, null, extra);
							}else
								listener.onDataFailed(BusinessDataListener.ERROR_USER_INFO, data.getString("tip"), null);
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_USER_INFO, description, null);
						}

					}else
						listener.onDataFailed(BusinessDataListener.ERROR_USER_INFO, ERROR_NET, null);
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_USER_INFO, ERROR_DATA, null);
					e.printStackTrace();
				}


			}
		});
	}

	/**
	 *
	 * @param loginCode
	 * @param name 姓名
	 * @param sex 性别
	 * @param age 年龄
	 * @param job 工作
	 * @param fav 爱好
	 * @param income 收入范围
	 */
	public void modifyUserInfo(final Type type, final UserSelectData selectData, final String loginCode, final String name, final String sex, final String age, final String job, final String fav, final String income){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				//{"taskId":"1","loginCode":"0000"}
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=UpdateUserInfo" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("name", name);
					jsonUrl.put("sex", sex);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("name", name);
					paramMap.put("sex", sex);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("commit>>>>>" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if(resultCode == 1){
							int status = data.getInt("status");
							if(status == 1){
								Bundle extra = new Bundle();
								extra.putSerializable("type", type);
								extra.putSerializable("data", selectData);

								listener.onDataFinish(BusinessDataListener.DONE_MODIFY_USER_INFO, null, null, extra);
							}else
								listener.onDataFailed(BusinessDataListener.ERROR_MODIFY_USER_INFO, data.getString("tip"), null);
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_MODIFY_USER_INFO, description, null);
						}

					}else
						listener.onDataFailed(BusinessDataListener.ERROR_MODIFY_USER_INFO, ERROR_NET, null);
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_MODIFY_USER_INFO, ERROR_DATA, null);
					e.printStackTrace();
				}


			}
		});

	}
	/**
	 * 转发成功后向服务器提交
	 * @param taskId
	 * @param loginCode
	 */
	public void commitSend(final int taskId, final String loginCode, final int type){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				//{"taskId":"1","loginCode":"0000"}
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=TurnTask" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("taskId", taskId);
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("type", type);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("taskId", String.valueOf(taskId));
					paramMap.put("loginCode", loginCode);
					paramMap.put("type", String.valueOf(type));
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("commit>>>>>" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if(resultCode == 1){
							int status = data.getInt("status");
							if(status == 1){
								String description = data.getString("description");
								Bundle extra = new Bundle();
								extra.putInt("type", type);
								listener.onDataFinish(BusinessDataListener.DONE_COMMIT_SEND, description, null, extra);
							}else {

//								50001  系统请求失败
//								56000  用户登录失败
//								56001  超出系统限定枪手当天转发有效任务数量
//								56005  任务已经转发过
								if(status == 50001 || status == 56000)
									listener.onDataFailed(BusinessDataListener.ERROR_RE_COMMIT_SEND, data.getString("tip"), null);
								else
									listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_SEND, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_RE_COMMIT_SEND, description, null);
						}

					}else
						listener.onDataFailed(BusinessDataListener.ERROR_RE_COMMIT_SEND, ERROR_NET, null);
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_SEND, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});
	}

	public void GetUserList(final Context mContext,final String loginCode,final String unionId ){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=GetUserList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("unionId",unionId);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("loginCode", loginCode);
					paramMap.put("unionId",unionId);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);

					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {

								Bundle extra = new Bundle();
								UserBaseInfoList userBaseInfoList = new UserBaseInfoList();
								JSONArray jarray = data.getJSONArray("resultData");
								Integer buserId = ((MyJSONObject)jarray.get(0)).getInt("userid");
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId, buserId.toString());
								int indLength = jarray.length();
								List<UserSelectData> indList = new ArrayList<UserSelectData>();
								for(int i = 0; i < indLength; i ++){
									MyJSONObject tip = (MyJSONObject) jarray.get(i);
									indList.add(new UserSelectData(tip.getString("wxNickName"), tip.getString("userid")));
								}
								userBaseInfoList.malluserlist = indList;
								extra.putSerializable("list", userBaseInfoList);


								listener.onDataFinish(BusinessDataListener.DONE_TO_GETUSERLIST, data.getString("tip"), null, extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_TO_GETUSERLIST, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_TO_GETUSERLIST, data.getString("description"), null);
						}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_TO_GETUSERLIST, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_TO_GETUSERLIST, ERROR_DATA, null);
					e.printStackTrace();
				}


			}
		});
	}


	public void checkverifyCode(final String verifycode, final String mobile){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=CHECKVERIFYCODE" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("verifyCode",verifycode);
					jsonUrl.put("mobile", mobile);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("verifyCode",verifycode);
					paramMap.put("mobile", mobile);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i(">>>>>>getCode:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								listener.onDataFinish(BusinessDataListener.DONE_CHECK_VERIFYCODE, data.getString("tip"), null, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_CHECK_VERIFYCODE, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_CHECK_VERIFYCODE, data.getString("description"), null);
						}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_CHECK_VERIFYCODE, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_CHECK_VERIFYCODE, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 验证手机号码是否注册接口
	 * @param mobile 手机号
	 */
	public void VerifyMobile(final String mobile){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=VerifyMobile" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();

					jsonUrl.put("mobile", mobile);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("mobile", mobile);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i(">>>>>>VerifyMobile:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								listener.onDataFinish(BusinessDataListener.DONE_VERIFY_MOBILE, data.getString("tip"), null, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_VERIFY_MOBILE, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_VERIFY_MOBILE, data.getString("description"), null);
						}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_VERIFY_MOBILE, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_VERIFY_MOBILE, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 获取验证码
	 * @param type 1：手机号相关;2：支付宝相关,3注册相关，4登录密码相关
	 */
	public void getAuthCode(final String loginCode, final String phone, final int type ,final long timestamp){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=SMS" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();

					jsonUrl.put("mobile", phone);
					jsonUrl.put("timestamp",timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("mobile", phone);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
						try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i(">>>>>>getCode:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								listener.onDataFinish(BusinessDataListener.DONE_GET_CODE, data.getString("tip"), null, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_GET_CODE, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_CODE, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_GET_CODE, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_CODE, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}
	public void userMoblieReg(final Context mContext, final String mobile, final String verifyCode,
							  final String password,final int isUpdate,final String invitationCode,final String token){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=MobileRegister" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("mobile",mobile );
					jsonUrl.put("verifyCode", verifyCode);
					jsonUrl.put("password", password);
					jsonUrl.put("isUpdate",isUpdate);
					jsonUrl.put("invitationCode",invitationCode);
					jsonUrl.put("token",token);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("mobile",mobile );
					paramMap.put("verifyCode", verifyCode);
					paramMap.put("password", password);
					paramMap.put("isUpdate", String.valueOf(isUpdate));
					paramMap.put("invitationCode",invitationCode);
					paramMap.put("token",token);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i(">>>>>>>register:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");

						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								String mallUserId=String.valueOf(data.getJSONObject("resultData").getString("mallUserId"));
								String unionId=data.getJSONObject("resultData").getString("unionId");

								String loginCode= data.getJSONObject("resultData").getString("loginCode");
								String loginUsername = data.getJSONObject("resultData").getString("userName");
								boolean isSuper=data.getJSONObject("resultData").getBoolean("isSuper");
								loginCode = loginCode.split("\\^")[1];
								setUserData(loginUsername, loginCode, data.getJSONObject("resultData"));
								//本地保存
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME, loginUsername);
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, loginCode);
								SPUtil.saveStringToSpByName(mContext,Constant.SP_NAME_NORMAL,Constant.SP_NAME_UnionId,unionId);
								SPUtil.saveStringToSpByName(mContext,Constant.SP_NAME_NORMAL,Constant.SP_NAME_BuserId,mallUserId);
								listener.onDataFinish(BusinessDataListener.DONE_USER_REG, null, null, null);
							}else{
								L.i(">>>tip:" + data.getString("tip"));
								listener.onDataFailed(BusinessDataListener.ERROR_USER_REG, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_USER_REG, data.getString("description"), null);
						}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_USER_REG, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_USER_REG, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}

	public void MobileLogin(final Context mContext, final String pwd, final String userName ){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=Login" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("pwd",pwd );
					jsonUrl.put("userName", userName);
					jsonUrl.put("timestamp", timestamp);
					AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
					Map< String, String > paramMap = new HashMap< String, String >( );
					paramMap.put("pwd",pwd );
					paramMap.put("userName", userName);
					paramMap.put("timestamp",String.valueOf(timestamp));
					String url2 = authParamUtils.getMapSign1(paramMap);
					jsonUrl.put("sign",url2);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i(">>>>>>>MobileLogin:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								String loginCode= data.getJSONObject("resultData").getString("loginCode");
								loginCode = loginCode.split("\\^")[1];
								setUserData(userName, loginCode, data.getJSONObject("resultData"));
								String mallUserId=String.valueOf(data.getJSONObject("resultData").getString("mallUserId"));
								String unionId=data.getJSONObject("resultData").getString("unionId");
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME, userName);
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, loginCode);
								SPUtil.saveStringToSpByName(mContext,Constant.SP_NAME_NORMAL,Constant.SP_NAME_BuserId,mallUserId);
								SPUtil.saveStringToSpByName(mContext,Constant.SP_NAME_NORMAL,Constant.SP_NAME_UnionId,unionId);
								listener.onDataFinish(BusinessDataListener.DONE_TO_MOBLIELOGIN, null, null, null);
							}
							else if (status == 90003) {
								String loginCode= data.getJSONObject("resultData").getString("loginCode");
								loginCode = loginCode.split("\\^")[1];
								setUserData(userName, loginCode, data.getJSONObject("resultData"));
								String mallUserId=String.valueOf(data.getJSONObject("resultData").getString("mallUserId"));
								String unionId=data.getJSONObject("resultData").getString("unionId");
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME, userName);
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, loginCode);
								SPUtil.saveStringToSpByName(mContext,Constant.SP_NAME_NORMAL,Constant.SP_NAME_BuserId,mallUserId);
								SPUtil.saveStringToSpByName(mContext,Constant.SP_NAME_NORMAL,Constant.SP_NAME_UnionId,unionId);
								listener.onDataFinish(BusinessDataListener.DONE_TO_MOBLIELOGIN, null, null, null);
							}
							else if (status==54003){
								listener.onDataFinish(BusinessDataListener.NULL_USER,data.getString("tip"),null,null);
							}
							else{
								listener.onDataFailed(BusinessDataListener.ERROR_TO_MOBLIELOGIN, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_TO_MOBLIELOGIN, data.getString("description"), null);
						}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_TO_MOBLIELOGIN, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_TO_MOBLIELOGIN, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}



}
