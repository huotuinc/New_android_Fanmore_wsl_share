package cy.com.morefan.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cindy.android.test.synclistview.SyncImageLoaderHelper;

import com.lib.cylibimagedownload.ImageUtil;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import cy.com.morefan.bean.MyJSONObject;
import cy.com.morefan.bean.PreTaskStatusData;
import cy.com.morefan.bean.SendData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.wxapi.WXEntryActivity;

public class TaskService extends BaseService {

	public TaskService(BusinessDataListener listener) {
		super(listener);
	}

	public void checkTaskStatus(final String loginCode, final int taskId){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=AdvanceForward" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("taskId", taskId);
					url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("AdvanceForward:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");
								boolean isPre = json.getInt("taskType") == 1;
								Bundle extra = new Bundle();
								extra.putBoolean("isPre", isPre);
								listener.onDataFinish(BusinessDataListener.DONE_CHECK_NOTICE_TASK_STATUS, null, null, extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_CHECK_NOTICE_TASK_STATUS, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_CHECK_NOTICE_TASK_STATUS, data.getString("description"), null);
						}
					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_CHECK_NOTICE_TASK_STATUS, ERROR_NET, null);
					}

				} catch (Exception e) {
					e.printStackTrace();
					listener.onDataFailed(BusinessDataListener.ERROR_CHECK_NOTICE_TASK_STATUS, ERROR_DATA, null);
				}
			}
		});


	}

	public void checkPreTaskStatus(final String loginCode, final int taskId,  final PreTaskStatusData statusData){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=AdvanceForward" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("taskId", taskId);
					url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("AdvanceForward:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");
								statusData.taskType = json.getInt("taskType");
								statusData.forwardLimit = json.getInt("forwardLimit");
								statusData.advanceTool = json.getInt("advanceTool");
								statusData.advanceToolExp = json.getInt("advanceToolExp");
								statusData.advanceUseTip = json.getString("advanceUseTip");
								statusData.advanceBuyTip = json.getString("advanceBuyTip");
								statusData.addOneTool = json.getInt("addOneTool");
								statusData.addOneToolExp = json.getInt("addOneToolExp");
								statusData.addOneUseTip = json.getString("addOneUseTip");
								statusData.addOneBuyTip = json.getString("addOneBuyTip");

								listener.onDataFinish(BusinessDataListener.DONE_CHECK_PRE_TASK_STATUS, null, null, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_CHECK_PRE_TASK_STATUS, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_CHECK_PRE_TASK_STATUS, data.getString("description"), null);
						}
					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_CHECK_PRE_TASK_STATUS, ERROR_NET, null);
					}

				} catch (Exception e) {
					e.printStackTrace();
					listener.onDataFailed(BusinessDataListener.ERROR_CHECK_PRE_TASK_STATUS, ERROR_DATA, null);
				}
			}
		});


	}


	public void checkTaskStatus(final int taskId){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=CHECKTASKSTATUS" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("taskId", taskId);
					url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("CHECKTASKSTATUS:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject result = data.getJSONObject("resultData");
								Bundle extra = new Bundle();
								extra.putInt("status", result.getInt("status"));
								extra.putString("webUrl", result.getString("webUrl"));
								listener.onDataFinish(BusinessDataListener.DONE_CHECK_TASK_STATUS, null, null, extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_CHECK_TASK_STATUS, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_CHECK_TASK_STATUS, data.getString("description"), null);
						}
					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_CHECK_TASK_STATUS, ERROR_NET, null);
					}

				} catch (Exception e) {
					e.printStackTrace();
					listener.onDataFailed(BusinessDataListener.ERROR_CHECK_TASK_STATUS, ERROR_DATA, null);
				}
			}
		});


	}
	public void commitToken(final Context context, final String token){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=UpdatePushToken" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("token", token);
					url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("UpdatePushToken:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								L.i("Done commit token!");
								SPUtil.saveBooleanToSpByName(context, Constant.SP_NAME_NORMAL, Constant.SP_NAME_TOKEN, true);
								SPUtil.saveStringToSpByName(context, Constant.SP_NAME_NORMAL, Constant.SP_NAME_TOKEN_VALUE, token);
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
//	public void getFlashMallList(final String loginCode, final int screenType, final int pageSize, final int oldTaskId){
//
//		ThreadPoolManager.getInstance().addTask(new Runnable() {
//			@Override
//			public void run() {
//				try {
//					String url = Constant.IP_URL + "/Api.ashx?req=FlashMallTask" + CONSTANT_URL();
//					JSONObject jsonUrl = new JSONObject();
//					jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
//					jsonUrl.put("screenType", screenType);
//					jsonUrl.put("pageSize", pageSize);
//					jsonUrl.put("oldTaskId", oldTaskId);
//					try {
//						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					}
//					L.i("getTaskList:" + url);
//					MyJSONObject data = getDataFromSer(url);
//					if(data != null){
//						int resultCode = data.getInt("resultCode");
//						if (resultCode == 1) {
//							int status = data.getInt("status");
//							if (status == 1) {
//								MyJSONObject myJSONObject = data.getJSONObject("resultData");
//								BusinessStatic.getInstance().TODAY_TOTAL_SCORE = myJSONObject.getInt("todayTotalScore");
//								String[] topIds = myJSONObject.getString("topIds").split(",");
//								List<String> tops = Arrays.asList(topIds);
//								JSONArray jArray = myJSONObject.getJSONArray("taskData");
//								int length = jArray.length();
//								TaskData[] results = new TaskData[length];
//								for (int i = 0; i < length; i++) {
//									MyJSONObject tip = (MyJSONObject) jArray.get(i);
//									TaskData taskData = setTaskData(tip);
//									//标记top任务
//									if(tops.contains(taskData.id + ""))
//										taskData.isTop = true;
//									results[i] = taskData;
//								}
//								listener.onDataFinish(BusinessDataListener.DONE_GET_TASK_LIST, null, results, null);
//							}else{
//								listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, data.getString("tip"), null);
//							}
//						}else{
//							String description = data.getString("description");
//							listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, description, null);
//						}
//					}else
//						listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, ERROR_NET, null);
//
//				} catch (JSONException e) {
//					listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, ERROR_DATA, null);
//					e.printStackTrace();
//				}
//			}
//		});
//
//
//
//
//	}
	/**
	 * 获取任务列表
	 * @param loginCode
	 * @param screenType
	 * @param pageSize
	 * @param oldTaskId
	 */
	public void getTaskList(final String loginCode, final int screenType, final int pageSize, final int oldTaskId) {
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=AllTask220" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
					jsonUrl.put("screenType", screenType);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("oldTaskId", oldTaskId);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("getTaskList:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject myJSONObject = data.getJSONObject("resultData");
								BusinessStatic.getInstance().TODAY_TOTAL_SCORE = myJSONObject.getInt("todayTotalScore");
								String[] topIds = myJSONObject.getString("topIds").split(",");
								List<String> tops = Arrays.asList(topIds);
								JSONArray jArray = myJSONObject.getJSONArray("taskData");
								int length = jArray.length();
								TaskData[] results = new TaskData[length];
								for (int i = 0; i < length; i++) {
									MyJSONObject tip = (MyJSONObject) jArray.get(i);
									TaskData taskData = setTaskData(tip);
									//标记top任务
									if(tops.contains(taskData.id + ""))
										taskData.isTop = true;
									results[i] = taskData;
								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_TASK_LIST, null, results, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, ERROR_NET, null);

				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});


	}

	/**
	 * 任务详情
	 * @param taskData
	 * @param loginCode
	 */
	public void getTaskDetail(final TaskData taskData, final String loginCode){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=TaskDetail" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("taskId", taskData.id);
					jsonUrl.put("loginCode", loginCode);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("getTaskDetail:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject myJSONObject = data.getJSONObject("resultData");
								String sendList = myJSONObject.getString("sendList");
								if(TextUtils.isEmpty(sendList)){
									taskData.channelIds = new ArrayList<String>();
								}else{
									String[] channelIds = sendList.split(",");
									taskData.channelIds = new ArrayList<String>(Arrays.asList(channelIds));
								}
								taskData.curTime = myJSONObject.getString("curTime");
								taskData.taskName = myJSONObject.getString("taskName");
								taskData.extraDes = myJSONObject.getString("extraDes");
								taskData.flagLimitCount = myJSONObject.getInt("flagLimitCount");
								taskData.flagHaveIntro = myJSONObject.getInt("flagHaveIntro");
								taskData.flagShowSend = myJSONObject.getInt("flagShowSend");
								taskData.taskPreview = myJSONObject.getString("taskPreview");
								taskData.startDate = Util.DateFormatFull(myJSONObject.getString("startTime"));
								taskData.endDate = Util.DateFormatFull(myJSONObject.getString("endTime"));
								taskData.lastScore = Util.opeDouble(myJSONObject.getDouble("lastScore"));//myJSONObject.getInt("lastScore");
								//剩余量小于0，取0
								taskData.lastScore = Double.parseDouble(taskData.lastScore) < 0 ? "0" : taskData.lastScore;
								taskData.totalScore = Util.opeDouble(myJSONObject.getDouble("totalScore"));//myJSONObject.getInt("totalScore");
								taskData.smallImgUrl = myJSONObject.getString("taskSmallImgUrl");
								taskData.largeImgUrl = myJSONObject.getString("taskLargeImgUrl");
								taskData.type = myJSONObject.getInt("type");
								taskData.sendCount = myJSONObject.getInt("sendCount");
								taskData.status = myJSONObject.getInt("status");
								taskData.content = myJSONObject.getString("taskInfo");
								taskData.awardSend = myJSONObject.getString("awardSend");
								taskData.awardScan = myJSONObject.getString("awardScan");
								taskData.awardLink = myJSONObject.getString("awardLink");
								taskData.sendstatus = myJSONObject.getInt("sendStatus");


								taskData.store = myJSONObject.getString("storeName");
								taskData.storeImgUrl = myJSONObject.getString("storeLargeImgUrl");
								taskData.storeId = myJSONObject.getString("storeId");
								//用户相关
								taskData.myAwardSend = myJSONObject.getString("myAwardSend");
								taskData.myAwardScan = myJSONObject.getString("myAwardScan");
								taskData.myAwardLink = myJSONObject.getString("myAwardLink");
								taskData.isSend = myJSONObject.getBoolean("isSend");
								taskData.isFav = myJSONObject.getBoolean("isFav");
								taskData.isAccount = myJSONObject.getBoolean("isAccount");

								JSONArray jSend = myJSONObject.getJSONArray("sendData");

								List<SendData> sendDatas = new ArrayList<SendData>();
								int count = jSend.length();
								for(int i = 0; i < count; i++){
									MyJSONObject item = (MyJSONObject) jSend.get(i);
									SendData sendData = new SendData();
									sendData.phone = item.getString("name");
									sendData.time = Util.DateFormatFull(item.getString("time"));
									sendData.des = item.getString("score");
									sendDatas.add(sendData);
								}
								//System.out.println("weburl:" + taskData.content);
								taskData.sendDatas = sendDatas;
								listener.onDataFinish(BusinessDataListener.DONE_GET_TASK_DETAIL, null, null, null);
							}else
								listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_DETAIL, data.getString("tip"), null);
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_DETAIL, description, null);
						}
					}else {
						listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_DETAIL, ERROR_NET, null);
					}

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_DETAIL, ERROR_DATA, null);
				}
			}
		});

	}




	/**
	 * 初始化
	 */
	public void init(final Context mContext, final String userName, final String pwd, final int width, final int height, final SyncImageLoaderHelper loader){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {

					String url = Constant.IP_URL + "/Api.ashx?req=init" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("userName", userName);
					jsonUrl.put("pwd", pwd);
					jsonUrl.put("width", width);
					jsonUrl.put("height", height);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i(">>>>>>>>>>init:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								Bundle extra = new Bundle();
								MyJSONObject myJSONObject = data.getJSONObject("resultData");
								/**
								 * 设置栏目数据
								 */
								JSONArray groups = myJSONObject.getJSONArray("groups");
								for(int i = 0, length = groups.length(); i < length; i++){
									MyJSONObject tip = (MyJSONObject) groups.get(i);
									BusinessStatic.getInstance().GROUPS.put(tip.getString("name"), tip.getInt("type"));
								}
								//BusinessStatic.getInstance().checkExps = myJSONObject.getString("checkExps").split("\\|");
//								//test
//								for(int i = 0, length = 6; i < length; i++){
//									BusinessStatic.GROUPS.put("name" + i, i);
//								}
//								//test end
								/**
								 * 1版本管理
								 * 0，无更新
								 * 1.增量更新
								 * 2.整包更新
								 * 3.强制增量更新
								 * 4.强制整包更新
								 */
								//删除增量更新的残余文件
								try {
									//ImageUtil.deleteFileByPath(Constant.PATH_PKG_TEMP);
								} catch (Exception e) {
									System.out.println(e.toString());
								}

								extra.putInt("updateType", myJSONObject.getInt("updateType"));
								extra.putString("updateUrl", myJSONObject.getString("updateUrl"));
								extra.putString("updateTips", myJSONObject.getString("updateTips"));
								extra.putString("updateMd5", myJSONObject.getString("updateMD5"));
								/**
								 * 2其他
								 */
								BusinessStatic.getInstance().grenadeRewardInfo = myJSONObject.getString("grenadeRewardInfo");
								BusinessStatic.getInstance().disasterFlag = myJSONObject.getInt("disasterFlag") == 1;
								BusinessStatic.getInstance().disasterUrl = myJSONObject.getString("disasterUrl");
								BusinessStatic.getInstance().URL_PUTIN = myJSONObject.getString("putInUrl");
								BusinessStatic.getInstance().URL_TOOL = myJSONObject.getString("toolUrl");
								BusinessStatic.getInstance().URL_MANUALSERVICE = myJSONObject.getString("manualServiceUrl");
								BusinessStatic.getInstance().CRASH_TYPE = myJSONObject.getInt("CashType");
								BusinessStatic.getInstance().getInstance().WEIXIN_IGNORE_VERSION = myJSONObject.getString("wxVersionCode");
								//是否完善个人信息
								WXEntryActivity.WX_APP_ID = myJSONObject.getString("weixinKey");
								extra.putInt("isCompleteUserInfo", myJSONObject.getInt("isCompleteUserInfo"));
								BusinessStatic.getInstance().SMS_TAG = myJSONObject.getString("smsTag");
								BusinessStatic.getInstance().CHANGE_BOUNDARY = myJSONObject.getInt("changeBoundary");
								BusinessStatic.getInstance().AWARD_SEND = myJSONObject.getInt("taskTurnScore");
								BusinessStatic.getInstance().AWARD_SCAN = myJSONObject.getInt("taskBrowseScore");
								BusinessStatic.getInstance().AWARD_LINK = myJSONObject.getInt("taskLinkScore");
								BusinessStatic.getInstance().URL_RULE = myJSONObject.getString("ruleUrl");
								BusinessStatic.getInstance().URL_WEBSITE= myJSONObject.getString("website");
								BusinessStatic.getInstance().customerId=myJSONObject.getInt("customerId");
								BusinessStatic.getInstance().URL_ABOUTUS = myJSONObject.getString("aboutUsUrl");
								BusinessStatic.getInstance().URL_SERVICE = myJSONObject.getString("serviceUrl");
								BusinessStatic.getInstance().TASK_TIME_LAG = myJSONObject.getInt("taskTimeLag");
								String[] channels = myJSONObject.getString("channelList").split(",");
								BusinessStatic.getInstance().CHANNEL_LIST = Arrays.asList(channels);
								BusinessStatic.getInstance().SINA_KEY_SECRET = myJSONObject.getString("appSecret");
								int smsEnable = myJSONObject.getInt("smsEnable");
								//smsEnable	     0，正常;-1短信灾难
								BusinessStatic.getInstance().SMS_ENALBE = !(smsEnable == -1);
								/**
								 *3用户信息
								 */
								//1登录;0未登录
								int loginStatus = myJSONObject.getInt("loginStatus");
								if(loginStatus == 1){
									setUserData(userName, pwd, myJSONObject.getJSONObject("userData"));
								}
								/**
								 * 4开机图
								 */
								//MyJSONObject myLoadingImg = null;
//								if(false){
//									int type = myLoadingImg.getInt("updateType");//是否强制更新
//									if(type == 1){//强制更新删除旧图
//										SPUtil.clearSpByName(mContext, Constant.SP_NAME_LOADING);
//										//ImageUtil.deleteFileByPath(Constant.IMAGE_PATH_LOADING);
//										deleteSDCardFolder(new File(Constant.IMAGE_PATH_LOADING));
//									}
//									String loadUrl = myLoadingImg.getString("imgUrl");
//									if(!TextUtils.isEmpty(loadUrl)){
//										loader.loadImage( loadUrl, Constant.IMAGE_PATH_LOADING);
//										try {
//											String[] showTime = myLoadingImg.getString("showTime").split(",");
//											String start = showTime[0];
//											String end = showTime[1];
//											//save tosp
//											String fileName = loadUrl.substring(loadUrl.lastIndexOf("/") + 1);
//											String time = Util.getIntTime(start) + "," + Util.getIntTime(end);
//											SPUtil.saveStringToSpByName(mContext,Constant.SP_NAME_LOADING, fileName,time);
//										} catch (Exception e) {
//											// TODO: handle exception
//										}
//									}
//								}
								listener.onDataFinish(BusinessDataListener.DONE_INIT, null, null, extra);

								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_INIT, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_INIT, data.getString("description"), null);
							}
					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_INIT, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_INIT, ERROR_DATA, null);
					e.printStackTrace();
				}


			}
		});
	}
	 public static void deleteSDCardFolder(File dir) {
	       File to = new File(dir.getAbsolutePath() + System.currentTimeMillis());
	       dir.renameTo(to);
	       if (to.isDirectory()) {
	           String[] children = to.list();
	           for (int i = 0; i < children.length; i++) {
	               File temp = new File(to, children[i]);
	               if (temp.isDirectory()) {
	                   deleteSDCardFolder(temp);
	               } else {
	                   boolean b = temp.delete();
	                   if (b == false) {
	                       Log.d("deleteSDCardFolder", "DELETE FAIL");
	                   }
	               }
	           }
	           to.delete();
	       }
	       }


	/**
	 * app更新检测
	 */
	public void checkUpdate(){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=VersionChecking" + CONSTANT_URL();
					L.i(">>>>>>checkUpdate:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if(resultCode == 1){
							int status = data.getInt("status");
							if(status == 1){
								Bundle extra = new Bundle();
								MyJSONObject myJSONObject = data.getJSONObject("resultData");
								/**
								 * 1版本管理
								 * 0，无更新
								 * 1.增量更新
								 * 2.整包更新
								 * 3.强制增量更新
								 * 4.强制整包更新
								 */
								extra.putInt("updateType", myJSONObject.getInt("updateType"));
								extra.putString("updateUrl", myJSONObject.getString("updateUrl"));
								extra.putString("updateTips", myJSONObject.getString("updateTips"));
								extra.putString("updateMd5", myJSONObject.getString("updateMD5"));
								listener.onDataFinish(BusinessDataListener.DONE_CHECK_UP, null, null, extra);

							}else
								listener.onDataFailed(BusinessDataListener.ERROR_CHECK_UP, data.getString("tip"), null);
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_CHECK_UP, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_CHECK_UP, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_CHECK_UP, ERROR_DATA, null);
				}


			}
		});
	}

	/**
	 * 任务详情，转发列表（暂未调用）
	 */
	public void getReSendList(final int taskId, final int pageIndex, final int pageSize){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=ForwardList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("taskId", taskId);
					jsonUrl.put("pageIndex", pageIndex);
					jsonUrl.put("pageSize", pageSize);
					url = url + URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if(resultCode == 1){
							int status = data.getInt("status");
							if(status == 1){
								JSONArray jArray = data.getJSONArray("resultData");
								int length = jArray.length();
								SendData[] results = new SendData[length];
								for(int i = 0 ; i < length; i++){
									MyJSONObject tip = (MyJSONObject) jArray.get(i);
									SendData item = new SendData();
									item.phone = tip.getString("name");
									item.time = tip.getString("time");
									item.des = tip.getString("score");
									results[i] = item;
								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_RESEND_LIST, null, results, null);

							}else
								listener.onDataFailed(BusinessDataListener.ERROR_GET_RESEND_LIST, data.getString("tip"), null);
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_RESEND_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_RESEND_LIST, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_RESEND_LIST, ERROR_DATA, null);
				}
			}
		});
	}
}
