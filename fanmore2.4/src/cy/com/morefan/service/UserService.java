package cy.com.morefan.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.InterfaceAddress;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
import cy.com.morefan.bean.BuyData;
import cy.com.morefan.bean.BuyItemData;
import cy.com.morefan.bean.FeedbackData;
import cy.com.morefan.bean.HistoryData;
import cy.com.morefan.bean.MyJSONObject;
import cy.com.morefan.bean.PartInItemData;
import cy.com.morefan.bean.PartnerData;
import cy.com.morefan.bean.PrenticeData;
import cy.com.morefan.bean.PushMsgData;
import cy.com.morefan.bean.RankData;
import cy.com.morefan.bean.ShakePrenticeData;
import cy.com.morefan.bean.StoreData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.TicketData;
import cy.com.morefan.bean.ToolData;
import cy.com.morefan.bean.UserBaseInfoList;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.bean.WalletData;
import cy.com.morefan.bean.WeekTaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.PartnerFrag;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.UserInfoView.Type;
import cy.lib.libhttpclient.CyHttpClient;

public class UserService extends BaseService{

	public UserService(BusinessDataListener listener) {
		super(listener);
	}
	public void getWalletList(final String loginCode, final String pageTag, final int pageSize){


		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=MyWalletMoneyFlow" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("pageTag",pageTag );
					jsonUrl.put("pageSize",pageSize );
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					System.out.println("MyWalletMoneyFlow:"+url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								JSONArray array = data.getJSONArray("resultData");
								int length = array.length();
								WalletData[] results = new WalletData[length];
								for(int i = 0; i < length; i++){
									MyJSONObject tip = (MyJSONObject) array.get(i);
									WalletData item = new WalletData();
									item.id = tip.getString("id");
									item.type = tip.getInt("type");
									item.amount = Util.opeDouble(tip.getDouble("amount"));
									item.time = TimeUtil.FormatterTimeToMinute(tip.getString("time"));
									item.actionDes = tip.getString("actionDes");
									results[i] = item;

								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_WALLET_HISTORY, null, results, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_GET_WALLET_HISTORY, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_WALLET_HISTORY, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_GET_WALLET_HISTORY, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_WALLET_HISTORY, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});








	}
//	public void cashToWallet(final String loginCode){
//
//		ThreadPoolManager.getInstance().addTask(new Runnable() {
//
//			@Override
//			public void run() {
//				try {
//					String url = Constant.IP_URL + "/Api.ashx?req=ScoreWallet" + CONSTANT_URL();
//					JSONObject jsonUrl = new JSONObject();
//					//System.out.println(loginCode);
//					jsonUrl.put("loginCode",loginCode );
//					try {
//						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					}
//					System.out.println("ScoreWallet:"+url);
//					MyJSONObject data = getDataFromSer(url);
//					if(data != null){
//						int resultCode = data.getInt("resultCode");
//						if (resultCode == 1) {
//							int status = data.getInt("status");
//							if (status == 1) {
//								//设置内存量
//								UserData userData = UserData.getUserData();
////								userData.lockScore = userData.score;
////								userData.score = "0";
//								double total = Double.parseDouble(userData.score);
//								int useScore = ((int)total/10)*10;
//								userData.lockScore = String.valueOf(useScore);
//								userData.score = Util.opeDouble(total - useScore);
//								listener.onDataFinish(BusinessDataListener.DONE_TO_CRASH, data.getString("tip"), null, null);
//								}else{
//									listener.onDataFailed(BusinessDataListener.ERROR_TO_CRASH, data.getString("tip"), null);
//								}
//							}else{
//								listener.onDataFailed(BusinessDataListener.ERROR_TO_CRASH, data.getString("description"), null);
//							}
//
//					}else{
//						listener.onDataFailed(BusinessDataListener.ERROR_TO_CRASH, ERROR_NET, null);
//					}
//				} catch (Exception e) {
//					listener.onDataFailed(BusinessDataListener.ERROR_TO_CRASH, ERROR_DATA, null);
//					e.printStackTrace();
//				}
//
//			}
//		});
//
//
//
//
//
//
//
//	}

	public void getDuiBaUrl(final String loginCode){

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=duibaautologin" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					System.out.println("duibaautologin:"+url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								//MyJSONObject json = data.getJSONObject("resultData");
								Bundle extra = new Bundle();
								extra.putString("loginurl", data.getString("url"));
								listener.onDataFinish(BusinessDataListener.DONE_GET_DUIBA_URL, null, null, extra);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_GET_DUIBA_URL, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_DUIBA_URL, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_GET_DUIBA_URL, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_DUIBA_URL, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});





	}
	public void getTicket(final String loginCode, final TicketData ticket){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=ScratchTicket" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					System.out.println("ScratchTicket:"+url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");
								ticket.id = json.getInt("id");
								ticket.residueCount = json.getInt("residueCount");
								ticket.value = json.getInt("value");


								listener.onDataFinish(BusinessDataListener.DONE_GET_TICKET, null, null, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_GET_TICKET, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_TICKET, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_GET_TICKET, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_TICKET, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});



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
	/**
	 * 我的兑换
	 * @param loginCode
	 * @param pageSize
	 * @param  pageTag 提现表自增id（分页用）
	 */
	public void getExpHistory(final String loginCode, final String pageTag, final int pageSize){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=ExpHistory" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("oldId", pageTag);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					System.out.println("ExpHistory:"+url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								JSONArray jArray = data.getJSONObject("resultData").getJSONArray("expHistory");
								int size = jArray.length();
								HistoryData[] results = new HistoryData[size];
								for(int i = 0; i < size; i ++){
									MyJSONObject tip = (MyJSONObject) jArray.get(i);
									HistoryData item = new HistoryData();
									item.pageTag = tip.getString("id");
									item.action = tip.getString("reason");
									item.time = Util.DateFormatFull(tip.getString("date"));
									int amount = tip.getInt("amount");
									item.status = amount > 0 ? "+" + amount : "" + amount;
									results[i] = item;
								}


//								MoneyChangeData[] results = new MoneyChangeData[size];
//								for(int i = 0; i < size; i++){
//									MyJSONObject tip = (MyJSONObject) jArray.get(i);
//									MoneyChangeData item = new MoneyChangeData();
//									item.tagId = tip.getInt("autoId");
//									item.money = tip.getString("money");
//									item.time = Util.DateFormatFull(tip.getString("time"));
//									item.score = tip.getString("score");
//									item.status = tip.getInt("status");
//
//									item.des = tip.getString("statusName");
//
//									item.extras = Util.replaceBlank(tip.getString("extraMsg"));
//									results[i] = item;
//
//								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_MONEY_CHANGE_LIST, null, results, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});



	}

	public void getPreTaskList(final Context mContext, final String loginCode, final int pageSize, final int id){
		if(id == 0)
		 getToolList(loginCode, null, null);


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

	public void useTool(final String loginCode, final int type, final int id){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=UsePropItem" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("type", type);
					jsonUrl.put("rid", id);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("UsePropItem:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");
								Bundle extra = new Bundle();
								extra.putInt("count", json.getInt("count"));

//								result.id =  shake.getInt("id");
//								result.name = shake.getString("name");
//								result.imgUrl = shake.getString("pictureUrl");
//								result.sex = shake.getInt("sex");
//								result.sentCount = shake.getInt("sentCount");
//								result.regTime = shake.getString("registerDate");
								listener.onDataFinish(BusinessDataListener.DONE_USE_TOOL, null, null, extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_USE_TOOL, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_USE_TOOL, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_USE_TOOL, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_USE_TOOL, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});



	}


	public void shakePrentice(final String loginCode, final ShakePrenticeData result){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=RollPrentice" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("RollPrentice:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject shake = data.getJSONObject("resultData");
								result.hasResult = true;
								result.id =  shake.getInt("id");
								result.name = shake.getString("name");
								result.imgUrl = shake.getString("pictureUrl");
								result.sex = shake.getInt("sex");
								result.sentCount = shake.getInt("sentCount");
								result.regTime = shake.getString("registerDate").split(" ")[0];
								listener.onDataFinish(BusinessDataListener.DONE_SHAKE_PRENTICE, null, null, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_SHAKE_PRENTICE, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_SHAKE_PRENTICE, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_SHAKE_PRENTICE, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_SHAKE_PRENTICE, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 *
	 * @param loginCode
	 * @param type 1 摇徒弟2抽奖3任务转发提限4任务提前预览
	 * @param value
	 * @param listTools
	 * @param listMyTools
	 */
	public void buyTool(final String loginCode, final int type, final int value, final List<ToolData> listTools, final List<ToolData> listMyTools){


		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=BuyProp" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("type", type);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("BuyProp:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								//update user exp
								UserData.getUserData().exp -= value;
								Bundle extra = new Bundle();
								extra.putInt("type", type);

								MyJSONObject arrays = data.getJSONObject("resultData");

								if( null != listTools){
									listTools.clear();
									JSONArray tools = arrays.getJSONArray("items");
									for(int i = 0, length = tools.length(); i < length; i++ ){
										MyJSONObject item = (MyJSONObject) tools.get(i);
										ToolData tool = new ToolData();
										tool.name = item.getString("name");
										tool.value = item.getInt("exp");
										tool.desc = item.getString("desc");
										tool.residueCount = item.getInt("store");
										tool.totalCount = item.getInt("storeTotal");
										tool.type = item.getInt("type");
										listTools.add(tool);
									}

								}

								if(null != listMyTools){
									listMyTools.clear();
									JSONArray myTools = arrays.getJSONArray("myItems");
									for(int i = 0, length = myTools.length(); i < length; i++ ){
										MyJSONObject item = (MyJSONObject) myTools.get(i);
										ToolData tool = new ToolData();
										tool.name = item.getString("name");
										tool.desc = item.getString("desc");
										tool.type = item.getInt("type");
										tool.ownCount = item.getInt("amount");
										listMyTools.add(tool);
									}
								}
								listener.onDataFinish(BusinessDataListener.DONE_BUY_TOOL, null, null, extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_BUY_TOOL, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_BUY_TOOL, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_BUY_TOOL, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_BUY_TOOL, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});



	}


	public void getToolList(final String loginCode, final List<ToolData> listTools, final List<ToolData> listMyTools){


		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=PropItemList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("ItemList:" + url);
//					//test
//					Thread.sleep(3000);
//					int exp1 = 3;
//					Bundle extra1 = new Bundle();
//					extra1.putInt("exp", exp1);
//					listener.onDataFinish(BusinessDataListener.DONE_GET_TOOL_LIST, null, null, extra1);
//					//test end
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								if(listTools != null)
									listTools.clear();
								if(listMyTools != null)
									listMyTools.clear();
								MyJSONObject arrays = data.getJSONObject("resultData");
								JSONArray tools = arrays.getJSONArray("items");
								for(int i = 0, length = tools.length(); i < length; i++ ){
									MyJSONObject item = (MyJSONObject) tools.get(i);
									ToolData tool = new ToolData();
									tool.name = item.getString("name");
									tool.value = item.getInt("exp");
									tool.desc = item.getString("desc");
									tool.residueCount = item.getInt("store");
									tool.totalCount = item.getInt("storeTotal");
									tool.type = item.getInt("type");
									if(listTools != null)
										listTools.add(tool);
								}

								JSONArray myTools = arrays.getJSONArray("myItems");
								for(int i = 0, length = myTools.length(); i < length; i++ ){
									MyJSONObject item = (MyJSONObject) myTools.get(i);
									ToolData tool = new ToolData();
									tool.name = item.getString("name");
									tool.desc = item.getString("desc");
									tool.type = item.getInt("type");
									tool.ownCount = item.getInt("amount");
									if(tool.type == 4 && tool.ownCount > 0)
										UserData.getUserData().hasPreTool = true;
									if(listMyTools != null)
										listMyTools.add(tool);
								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_TOOL_LIST, null, null, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_TOOL_LIST, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_TOOL_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_TOOL_LIST, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_TOOL_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});



	}
	public void CheckIn(final String loginCode){

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=CheckIn" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("CheckIn:" + url);
//					//test
//					Thread.sleep(3000);
//					int exp1 = 3;
//					Bundle extra1 = new Bundle();
//					extra1.putInt("exp", exp1);
//					listener.onDataFinish(BusinessDataListener.DONE_CHECK_IN, null, null, extra1);
//					//test end
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
//								MyJSONObject array = data.getJSONObject("resultData");
//								int exp = array.getInt("exp");
								Bundle extra = new Bundle();
								extra.putInt("exp", data.getInt("resultData"));
								UserData.getUserData().dayCheckIn = true;
								UserData.getUserData().checkInDays ++;
								listener.onDataFinish(BusinessDataListener.DONE_CHECK_IN, null, null, extra);
							}else if(status == 54006){//今日已签到
								listener.onDataFailed(BusinessDataListener.ERROR_ALREADY_CHECK_IN, data.getString("tip"), null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_CHECK_IN, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_CHECK_IN, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_CHECK_IN, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_PUSH_MSG, ERROR_DATA, null);
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
	public void getPushMsg(final String loginCode, final int pageIndex, final int pageSize){

		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=MESSAGELIST" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageIndex", pageIndex);
					jsonUrl.put("pageSize", pageSize);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("MESSAGELIST:" + url);
				//	Thread.sleep(2000);
//					//test
//					PushMsgData[] datas = new PushMsgData[20];
//
//					for(int i = 0; i< 20 ; i++){
//						PushMsgData rankData = new PushMsgData();
//						rankData.title = "title" + i;
//						rankData.des = "des" + i;
//						rankData.time = "time" + i;
//						rankData.taskId = i;
//						rankData.taskStatus = i;
//						rankData.webUrl = "http://www.baidu.com";
//						datas[i] = rankData;
//					}
//					Bundle extra = new Bundle();
//					extra.putInt("myRank", 3);
//					extra.putString("des", "test desc");
//					listener.onDataFinish(BusinessDataListener.DONE_GET_PUSH_MSG, null, datas, extra);
//					//test end




					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								JSONArray array = data.getJSONArray("resultData");
								int length = array.length();
								PushMsgData[] results = new PushMsgData[length];
								for(int i = 0; i < length; i++){
									MyJSONObject tip = (MyJSONObject) array.getJSONObject(i);
									PushMsgData itemData = new PushMsgData();
									itemData.id = tip.getInt("id");
									itemData.title = tip.getString("title");
									itemData.des = tip.getString("des");
									itemData.time = tip.getString("time");
									itemData.type = tip.getInt("type");
									itemData.taskId = tip.getInt("taskid");
									itemData.taskStatus = tip.getInt("taskStatus");
									itemData.webUrl = tip.getString("webUrl");
									results[i] = itemData;
								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_PUSH_MSG, null, results, null);
								//listener.onDataFinish(BusinessDataListener.DONE_GET_RANK, null, null, null);


							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_PUSH_MSG, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_PUSH_MSG, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_PUSH_MSG, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_PUSH_MSG, ERROR_DATA, null);
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
	 * @param buyData
	 * @param loginCode
	 */
	public void getMallDetail(final BuyData buyData, final String loginCode){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=FlashMallDetail" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("orderNo", buyData.orderNo);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("FlashMallDetail:" + url);

					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");

								buyData.goodsName =  json.getString("goodsName");
								buyData.time = json.getString("time").split(" ")[0];
								buyData.name = json.getString("name");
								buyData.tempScore = Util.opeDouble(json.getDouble("tempScore"));//json.getInt("tempScore");
								buyData.realScore = Util.opeDouble(json.getDouble("realScore"));//json.getInt("realScore");
								buyData.timeCount = json.getString("timeCount");
								buyData.status = json.getInt("status2");
								buyData.statusDes = json.getString("status2Des");


								JSONArray array = json.getJSONArray("list");
								int length = array.length();
								List<BuyItemData> itemDatas = new ArrayList<BuyItemData>();
								for(int i = 0; i < length; i++){
									MyJSONObject tip = (MyJSONObject) array.getJSONObject(i);
									BuyItemData itemData = new BuyItemData();
									itemData.action = tip.getString("action");
									itemData.score = Util.opeDouble(tip.getDouble("score"));//tip.getString("score");
									itemData.time =  tip.getString("time").split(" ")[0];
									itemDatas.add(itemData);
								}
								buyData.listDatas = itemDatas;
								listener.onDataFinish(BusinessDataListener.DONE_GET_MALL_DETAIL, null, null, null);


							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MALL_DETAIL, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_MALL_DETAIL, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MALL_DETAIL, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MALL_DETAIL, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 *
	 * @param loginCode
	 * @param pageTag  默认-1
	 * @param pageSize
	 */
	public void getMallList(final String loginCode, final String pageTag, final int pageSize){
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=FlashMallList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageTag", pageTag);
					jsonUrl.put("pageSize", pageSize);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("FlashMallList:" + url);
//					//test
//					BuyData[] testResults = new BuyData[10];
//					for(int i = 0; i < 10; i++){
//						BuyData item = new BuyData();
//						item.orderNo = "orderNo" + i;
//						item.orderID = i;
//						item.goodsName =  "goodsName" + i;
//						item.time = "time" + i;
//						item.name = "name" + i;
//						item.tempScore = i;
//						item.realScore = i;
//						item.timeCount = "" + i;
//						item.status = 0;
//						testResults[i] = item;
//					}
//					listener.onDataFinish(BusinessDataListener.DONE_GET_MALL_LIST, null, testResults, null);
//					//test end

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
									extra.putString("count", json.getString("count"));
									extra.putString("tempScore", Util.opeDouble(json.getDouble("tempScore")));
									extra.putString("realScore", Util.opeDouble(json.getDouble("realScore")));
								}
								JSONArray array = json.getJSONArray("list");
									int length = array.length();
									List<BuyData> results = new ArrayList<BuyData>();
									for(int i = 0; i < length; i++){
										MyJSONObject tip = (MyJSONObject) array.getJSONObject(i);
										BuyData item = new BuyData();
										item.orderNo = tip.getString("orderNo");
										item.orderID = tip.getInt("orderID");
										item.goodsName =  tip.getString("goodsName");
										item.orderTime = tip.getString("updateTime");
										item.time = tip.getString("time").split(" ")[0];
										item.name = tip.getString("name");
										item.tempScore = Util.opeDouble(tip.getDouble("tempScore"));//tip.getInt("tempScore");
										item.realScore = Util.opeDouble(tip.getDouble("realScore"));//tip.getInt("realScore");
										item.timeCount = tip.getString("timeCount");
										item.status = tip.getInt("status2");
//										if(!buyDatas.contains(item))
											results.add(item);
									}
									listener.onDataFinish(BusinessDataListener.DONE_GET_MALL_LIST, null, results.toArray(new BuyData[]{}), extra);

							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MALL_LIST, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_MALL_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MALL_LIST, ERROR_NET, null);

				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MALL_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}


//	public void getPrenticeDetail(final String loginCode, final int id, final int pageTag, final int pageSize){
//
//
//
//		ThreadPoolManager.getInstance().addTask(new Runnable() {
//
//			@Override
//			public void run() {
//
//				try {
//					String url = Constant.IP_URL + "/Api.ashx?req=PrenticeDetail" + CONSTANT_URL();
//					JSONObject jsonUrl = new JSONObject();
//					jsonUrl.put("loginCode", loginCode);
//					jsonUrl.put("id", id);
//					jsonUrl.put("pageTag", pageTag);
//					jsonUrl.put("pageSize", pageSize);
//					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
//					L.i("getPrenticeDetail:" + url);
//					MyJSONObject data = getDataFromSer(url);
//					if(data != null){
//						int resultCode = data.getInt("resultCode");
//						if (resultCode == 1) {
//							int status = data.getInt("status");
//							if (status == 1) {
//								MyJSONObject json = data.getJSONObject("resultData");
//								Bundle extra = null;
//								if(pageTag == 0){
//									extra = new Bundle();
//									extra.putString("name", json.getString("userName"));
//									extra.putString("time", TimeUtil.FormatterTimeToDay(json.getString("time")));
//									extra.putInt("contri", json.getInt("totalScore"));
//								}
//								JSONArray array = json.getJSONArray("list");
//								int length = array.length();
//								PrenticeContriData[] results = new PrenticeContriData[length];
//								for(int i = 0; i < length; i++){
//									MyJSONObject tip = (MyJSONObject) array.getJSONObject(i);
//									PrenticeContriData item = new PrenticeContriData();
//									item.id = tip.getInt("flowId");
//									item.time = TimeUtil.FormatterTimeToDay(tip.getString("time"));
//									item.contri = Util.opeDouble(tip.getDouble("score"));//tip.getInt("score");
//									results[i] = item;
//								}
//								listener.onDataFinish(BusinessDataListener.DONE_GET_PRENTICE_DETAIL, null, results, extra);
//							}else{
//								listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_DETAIL, data.getString("tip"), null);
//							}
//						}else{
//							String description = data.getString("description");
//							listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_DETAIL, description, null);
//						}
//					}else
//						listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_DETAIL, ERROR_NET, null);
//
//				} catch (Exception e) {
//					listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_DETAIL, ERROR_DATA, null);
//					e.printStackTrace();
//				}
//			}
//		});
//	}
	/**
	 *
	 * @param loginCode
	 * @param orderType (0、拜师时间1、总贡献值)
	 * @param pageTag
	 * @param pageSize
	 */
	public void getPrenticeList(final String loginCode, final int orderType, final String pageTag, final int pageSize){



		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=PrenticeList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageTag", pageTag);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("orderType", orderType);
					url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					L.i("getPrenticeList:" + url);

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
									taskData.sendCount = tip.getString("sendAmount");
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
	public void getAllScoreTrendList(final ArrayList<AllScoreData> allScoreDatas, final String loginCode, final int pageSize, final String pageDate){

		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=NewTotalScoreList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("date", pageDate);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("NewTotalScoreList:" + url);

//					//test
//					try {
//						Thread.sleep(3000);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					//首页时获取max,min
//					Bundle extra2 = null;
//					if(TextUtils.isEmpty(pageDate)){
//						extra2 = new Bundle();
//						extra2.putInt("totalScore",1000);
//						extra2.putInt("maxScore", 90);
//						extra2.putInt("minScore", -80);
//					}
//					listener.onDataFinish(BusinessDataListener.DONE_GET_ALLSCORE_TREND, null, getTestData(), extra2);
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


//								//test
//								for(int i = 0; i < 10; i ++){
//									AllScoreData scoreData = new AllScoreData();
//									scoreData.scanCount = i;
//									scoreData.score = i;
//									scoreData.date = "2013-10-1"+i;
//									allScoreDatas.add(scoreData);
//									//scoreData.extra = tip.getString("extra").replaceAll(":", "").replaceAll("\\^", "\n");
//									//results[i] = scoreData;
////									scoreDatas.put(scoreData.date, scoreData);
////									if(!allScoreDatas.contains(scoreData))
////
//
//									//AwardData[] results = new AwardData[length2];
//									for(int j = 0; j < 2; j ++){
//										AwardData awardData = new AwardData();
//										awardData.id = j;
//										awardData.type = 0;
//										awardData.titile = "title" + j;
//										awardData.des = "description" + j;
//										awardData.imgUrl = null;
//										awardData.date = scoreData.date;
//										awardData.dateDes = "";//TimeUtil.FormatterTimeToMinute(tip2.getString("date"));
//										awardData.scanCount = j;//tip2.getInt("browseAmount");
//										awardData.score = j;//tip2.getInt("totalScore");
//										awardData.dayScanCount = scoreData.scanCount;
//										awardData.dayScore = scoreData.score;
//										results.add(awardData);
//									}
//								}
//								//test end

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
	 * @param pageSize
	 * @param autoId
	 */
	public void FeedbackList(final String loginCode, final int pageSize, final String autoId) {
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=FeedbackList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("autoId", autoId);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("Feedbacklist:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								//更新内存量
								UserData.getUserData().hasNewFeedback = false;
								JSONArray array = data.getJSONArray("resultData");
								int length = array.length();
								FeedbackData[] resultDatas = new FeedbackData[length];
								for(int i = 0; i < length; i++){
									MyJSONObject item = (MyJSONObject) array.get(i);
									FeedbackData feedbackData = new FeedbackData();
									feedbackData.id = item.getString("autoId");
									feedbackData.commitContent = item.getString("content");
									feedbackData.commitTime = Util.DateFormat(item.getString("time"));
									feedbackData.statusName = item.getString("statusName");
									feedbackData.status = item.getInt("status");
									feedbackData.backContent = item.getString("doContent");
									feedbackData.backTime = Util.DateFormat(item.getString("doTime"));

									resultDatas[i] = feedbackData;
								}

								listener.onDataFinish(BusinessDataListener.DONE_FEEDBACK_LIST, null, resultDatas, null);
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
	 * @param name
	 * @param contact
	 * @param content
	 */
	public void Feedback(final String loginCode, final String name, final String contact, final String content) {
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=Feedback" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("name", name);
					jsonUrl.put("contact", contact);
					jsonUrl.put("content", content);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("Feedback:" + url);
					MyJSONObject data = getDataFromSer(url);


					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {

								listener.onDataFinish(BusinessDataListener.DONE_FEEDBACK, null, null, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK, data.getString("tip"), null);
							}
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK, ERROR_NET, null);

				} catch (JSONException e) {
					listener.onDataFailed(BusinessDataListener.ERROR_FEEDBACK, ERROR_DATA, null);
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
					jsonUrl.put("birth", age);
					jsonUrl.put("industry", job);
					jsonUrl.put("favorite", fav);
					jsonUrl.put("income", income);

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
//								extra.putString("name", data.getString("name"));
//								extra.putInt("sex", data.getInt("sex"));
//								extra.putString("birth", data.getString("birth"));
//								extra.putString("industry", data.getString("industry"));
//								extra.putString("favorite", data.getString("favorite"));
//								extra.putString("income", data.getString("income"));
//								extra.putString("area", data.getString("area"));
//								extra.putString("industryList", data.getString("industryList"));
//								extra.putString("favoriteList", data.getString("favoriteList"));
//								extra.putString("incomeList", data.getString("incomeList"));
//								String description = data.getString("description");
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


	/**
	 * 我的收藏列表
	 * @param loginCode
	 * @param pageSize
	 * @param autoId 最早的收藏id
	 */
	public void getFavList(final String loginCode, final int pageSize, final String autoId){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=MyFavoriteStoreList" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//{"loginCode":"000","pageIndex":"1","pageSize":"5"}
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("autoId", autoId);
					url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("getFavList:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if(resultCode == 1){
							int status = data.getInt("status");
							if(status == 1){
								JSONArray jArray = data.getJSONArray("resultData");
								int length = jArray.length();
								StoreData[] results = new StoreData[length];
								for (int i = 0; i < length; i++) {
									MyJSONObject tip = (MyJSONObject) jArray.get(i);
									StoreData item = new StoreData();
									//item.opentId = tip.getString("Openid");
									item.taskCount = tip.getString("taskCount");
									item.id = tip.getString("id");
									item.name = tip.getString("name");
									item.imgUrl = tip.getString("logo");
									item.pageTag = tip.getString("autoId");
									results[i] = item;
								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_MY_FAV_LIST, null, results, null);
							}else
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_FAV_LIST, data.getString("tip"), null);
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_FAV_LIST, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_FAV_LIST, ERROR_NET, null);
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_FAV_LIST, ERROR_DATA, null);
				}
//				MySendData[] results = new MySendData[10];
//				for (int i = 0; i < results.length; i++) {
//					MySendData item = new MySendData();
//					item.taskId = i;
//					item.name ="任务" +i;
//					item.time ="2013-12-05 15:00:00";
//					item.url = ImageUrls[i];
//					results[i] = item;
//				}
//				listener.onDataFinish(BusinessDataListener.DONE_GET_MY_SEND_LIST, null, results, null);



			}
		});
	}
	/**
	 * 我的收藏详情
	 * @param loginCode
	 * @param pageSize
	 * @param oldTaskId 客户端现有最早分布任务id
	 */
	public void getFavDetail(final String loginCode, final String storeId, final int pageSize, final int oldTaskId){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=MyFavoriteTaskDetail" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//{"loginCode":"000","pageIndex":"1","pageSize":"5"}
					jsonUrl.put("loginCode", loginCode);
					jsonUrl.put("storeId", storeId);
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("oldTaskId", oldTaskId);

					url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
					L.i("getFavDetail:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if(resultCode == 1){
							int status = data.getInt("status");
							if(status == 1){
								MyJSONObject myData = data.getJSONObject("resultData");
								//task
								JSONArray jArrayTask = myData.getJSONArray("taskData");
								int length = jArrayTask.length();
								TaskData[] taskResults = new TaskData[length];
								for (int i = 0; i < length; i++) {
									MyJSONObject tip = (MyJSONObject) jArrayTask.get(i);
									taskResults[i] = setTaskData(tip);
								}
								//store
								MyJSONObject myStore = myData.getJSONObject("storeInfo");
								Bundle extra = new Bundle();
								extra.putString("name", myStore.getString("name"));
								extra.putString("taskCount", myStore.getString("taskAmuont"));
								extra.putString("imgUrl", myStore.getString("logo"));
								extra.putString("contact", myStore.getString("contact"));
								extra.putString("industry", myStore.getString("industry"));
								extra.putString("storeDes", myStore.getString("storeDes"));
								extra.putSerializable("taskData", taskResults);
 								listener.onDataFinish(BusinessDataListener.DONE_GET_MY_FAV_DETAIL, null, null, extra);
							}else
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_FAV_DETAIL, data.getString("tip"), null);
						}else{
							String description = data.getString("description");
							listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_FAV_DETAIL, description, null);
						}
					}else
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_FAV_DETAIL, ERROR_NET, null);
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MY_FAV_DETAIL, ERROR_DATA, null);
				}
			}
		});
	}


	public void userCommitToCrashPwd(final String loginCode, final String pwd, final String oldPwd){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=SetWithdrawalPassword" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("withdrawalPassword", pwd);
					jsonUrl.put("oldWithdrawalPassword", oldPwd);

					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i("userCommitToCrashPwd:"+url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								UserData.getUserData().toCrashPwd = pwd;
								listener.onDataFinish(BusinessDataListener.DONE_COMMIT_TOCRASHPWD, data.getString("tip"), null, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_TOCRASHPWD, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_TOCRASHPWD, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_TOCRASHPWD, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_COMMIT_TOCRASHPWD, ERROR_DATA, null);
					e.printStackTrace();
				}


			}
		});



	}
	/**
	 * 解除手机号绑定
	 * @param loginCode
	 * @param phone
	 * @param code
	 */
	public void userUnBindPhone(final String loginCode, final String phone, final String code){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=ReleaseBindMobile" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("phone", phone);
					jsonUrl.put("code", code);

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
								UserData.getUserData().phone = "";
								listener.onDataFinish(BusinessDataListener.DONE_UNBIND_PHONE, data.getString("tip"), null, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_UNBIND_PHONE, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_UNBIND_PHONE, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_UNBIND_PHONE, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_UNBIND_PHONE, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});


	}


	/**
	 * 我的兑换
	 * @param loginCode
	 * @param pageTag
	 * @param pageSize 提现表自增id（分页用）
	 */
	public void userCashHistory(final String loginCode, final String pageTag, final int pageSize){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=CashHistory" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("pageSize", pageSize);
					jsonUrl.put("autoId", pageTag);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					System.out.println("userCashHistory:"+url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								JSONArray jArray = data.getJSONArray("resultData");
								int size = jArray.length();
								HistoryData[] results = new HistoryData[size];
								for(int i = 0; i < size; i ++){
									MyJSONObject tip = (MyJSONObject) jArray.get(i);
									HistoryData item = new HistoryData();
									item.pageTag = tip.getString("autoId");
									item.action = String.format("兑换%s元, 消耗%s积分",Util.opeDouble(tip.getDouble("money")), Util.opeDouble(tip.getDouble("score")));
									item.time = Util.DateFormatFull(tip.getString("time"));
									item.status = tip.getString("statusName");
									item.extra = Util.replaceBlank(tip.getString("extraMsg"));
									results[i] = item;
								}


//								MoneyChangeData[] results = new MoneyChangeData[size];
//								for(int i = 0; i < size; i++){
//									MyJSONObject tip = (MyJSONObject) jArray.get(i);
//									MoneyChangeData item = new MoneyChangeData();
//									item.tagId = tip.getInt("autoId");
//									item.money = tip.getString("money");
//									item.time = Util.DateFormatFull(tip.getString("time"));
//									item.score = tip.getString("score");
//									item.status = tip.getInt("status");
//
//									item.des = tip.getString("statusName");
//
//									item.extras = Util.replaceBlank(tip.getString("extraMsg"));
//									results[i] = item;
//
//								}
								listener.onDataFinish(BusinessDataListener.DONE_GET_MONEY_CHANGE_LIST, null, results, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});



	}

			/**
			 * 收藏/取消收藏商家
			 * @param loginCode
			 * @param storeId 商家id
			 * @param isFav 0：收藏;1：取消收藏
			 */
	public void userFavOrNotStore(final String loginCode, final String storeId, final boolean isFav){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				int type = isFav ? 1 : 0;
				int doneType = isFav ? BusinessDataListener.DONE_CANCEL_FAV :   BusinessDataListener.DONE_FAV;
				int errorType = isFav ? BusinessDataListener.ERROR_CANCEL_FAV :   BusinessDataListener.ERROR_FAV;
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=OperFavorite" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("storeId", storeId);
					jsonUrl.put("type", type);

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

								listener.onDataFinish(doneType, data.getString("tip"), null, null);
								}else{
									listener.onDataFailed(errorType, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(errorType, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(errorType, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(errorType, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});
	}
	/**
	 * 修改密码
	 * @param mContext
	 * @param loginCode
	 * @param newPwd
	 */
	public void userModifyPwd(final Context mContext, final String loginCode, final String newPwd){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=ModifyPwd" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("newPwd", newPwd);

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
								UserData.getUserData().pwd = newPwd;
								UserData.getUserData().loginCode = data.getString("newLoginCode");

								//本地保存
								//SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME, userName);
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, newPwd);
								listener.onDataFinish(BusinessDataListener.DONE_MODIFY_PWD, data.getString("tip"), null, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_MODIFY_PWD, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_MODIFY_PWD, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_MODIFY_PWD, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_MODIFY_PWD, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});

	}
	public void intGoldInfo(final String loginCode,final String score){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=IntegralGoldInfo" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("score",score);
					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					System.out.println("MyWallet:"+url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								MyJSONObject json = data.getJSONObject("resultData");
								//UserData.getUserData().score = Util.opeDouble(json.getDouble("ApplyScore"));
								UserData.getUserData().wallet = Util.opeDouble(json.getDouble("money"));
								UserData.getUserData().scorerate=json.getDouble("scorerate");
								Bundle extra = new Bundle();
								extra.putString("des", json.getString("desc"));
								try {

									MyJSONObject ajson = json.getJSONObject("lastApply");
									extra.putString("recordTime", ajson.getString("ApplyTime"));
									extra.putString("recordDes", ajson.getString("ApplyMoney"));
									extra.putInt("recordResult", ajson.getInt("ApplyScore"));
									extra.putString("recordResultDes", json.getString("recordResultDes"));
								}catch (Exception ex){}

								listener.onDataFinish(BusinessDataListener.DONE_GET_WALLET, null, null, extra);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_GET_WALLET, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_GET_WALLET, data.getString("description"), null);
						}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_GET_WALLET, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_GET_WALLET, ERROR_DATA, null);
					e.printStackTrace();
				}

			}
		});






	}



//	public void userToCrash(final String loginCode, final String crashPwd){
//		ThreadPoolManager.getInstance().addTask(new Runnable() {
//
//			@Override
//			public void run() {
//
//				try {
//					String url = Constant.IP_URL + "/Api.ashx?req=ScoreCash" + CONSTANT_URL();
//					JSONObject jsonUrl = new JSONObject();
//					//System.out.println(loginCode);
//					jsonUrl.put("loginCode",loginCode );
//					jsonUrl.put("pwd", crashPwd);
//
//					try {
//						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					}
//					MyJSONObject data = getDataFromSer(url);
//					if(data != null){
//						int resultCode = data.getInt("resultCode");
//						if (resultCode == 1) {
//							int status = data.getInt("status");
//							if (status == 1) {
//								//设置内存量
//								UserData userData = UserData.getUserData();
////								userData.lockScore = userData.score;
////								userData.score = "0";
//								double total = Double.parseDouble(userData.score);
//								int useScore = ((int)total/10)*10;
//								userData.lockScore = String.valueOf(useScore);
//								userData.score = Util.opeDouble(total - useScore);
//
//
//								listener.onDataFinish(BusinessDataListener.DONE_TO_CRASH, data.getString("tip"), null, null);
//								}else{
//									listener.onDataFailed(BusinessDataListener.ERROR_TO_CRASH, data.getString("tip"), null);
//								}
//							}else{
//								listener.onDataFailed(BusinessDataListener.ERROR_TO_CRASH, data.getString("description"), null);
//							}
//
//					}else{
//						listener.onDataFailed(BusinessDataListener.ERROR_TO_CRASH, ERROR_NET, null);
//					}
//				} catch (Exception e) {
//					listener.onDataFailed(BusinessDataListener.ERROR_TO_CRASH, ERROR_DATA, null);
//					e.printStackTrace();
//				}
//
//
//			}
//		});
//	}
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
	public void userchange(final String loginCode,final String mallUserId  ,final String score, final String crashPwd){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {

				try {
					String url = Constant.IP_URL + "/Api.ashx?req=Recharge" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("mallUserId",mallUserId);
					jsonUrl.put("score",score);
					jsonUrl.put("cashpassword", crashPwd);

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
								//设置内存量
								UserData userData = UserData.getUserData();
//								userData.lockScore = userData.score;
//								userData.score = "0";
								BigDecimal total =new BigDecimal( userData.score);
								BigDecimal scorerate=new BigDecimal( userData.scorerate);
								BigDecimal scorerate1=new BigDecimal(1);
								BigDecimal useScore = (total.remainder((scorerate1.divide(scorerate))));
								userData.lockScore = String.valueOf(useScore);
								DecimalFormat df = new DecimalFormat("0.00");
								userData.score = String.valueOf(df.format(useScore));


								listener.onDataFinish(BusinessDataListener.DONE_TO_RECHANGE, data.getString("tip"), null, null);
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_TO_RECHANGE, data.getString("tip"), null);
							}
						}else{
							listener.onDataFailed(BusinessDataListener.ERROR_TO_RECHANGE, data.getString("description"), null);
						}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_TO_RECHANGE, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_TO_RECHANGE, ERROR_DATA, null);
					e.printStackTrace();
				}


			}
		});
	}
	/**
	 * 绑定手机号
	 * @param loginCode
	 * @param phone
	 * @param code
	 */
	public void userBindPhone(final String loginCode, final String phone, final String code){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=BindMobile" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("phone", phone);
					jsonUrl.put("code", code);

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
								UserData.getUserData().phone = phone;
								listener.onDataFinish(BusinessDataListener.DONE_BIND_PHONE, data.getString("tip"), null, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_BIND_PHONE, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_BIND_PHONE, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_BIND_PHONE, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_BIND_PHONE, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});


	}
	/**
	 * 绑定支付宝账号
	 * @param loginCode
	 * @param phone
	 * @param payAccount
	 * @param name
	 * @param code
	 *
	 */

	public void userBindPayAccount(final String loginCode, final String phone, final String payAccount,final String name, final String code){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=BindAlipay" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("phone", phone);
					jsonUrl.put("alipayAccount", payAccount);
					jsonUrl.put("alipayName", name);
					jsonUrl.put("code", code);

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
								UserData.getUserData().payAccount = payAccount;
								listener.onDataFinish(BusinessDataListener.DONE_BIND_PAYACCOUNT, data.getString("tip"), null, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_BIND_PAYACCOUNT, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_BIND_PAYACCOUNT, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_BIND_PAYACCOUNT, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_BIND_PAYACCOUNT, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});

	}
	/**
	 * 检查验证码是否有效
	 */
	public void checkverifyCode(final String verifycode, final String mobile){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=CHECKVERIFYCODE" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("verifyCode",verifycode);
					jsonUrl.put("mobile", mobile);
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
	 * 获取验证码
	 * @param type 1：手机号相关;2：支付宝相关,3注册相关，4登录密码相关
	 */
	public void getAuthCode(final String loginCode, final String phone, final int type){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=SMS" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();

					//jsonUrl.put("loginCode",loginCode );
					jsonUrl.put("mobile", phone);
					//jsonUrl.put("type", type);

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
	/**
	 *
	 */
	public void userReset(final Context mContext, final String userName, final String pwd, final String code){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=ResetPwd" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);
					jsonUrl.put("phone",userName );
					jsonUrl.put("newPwd", pwd);
					jsonUrl.put("code", code);

					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i(">>>>>>>reset:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								//setUserData(userName, pwd, data.getJSONObject("resultData"));

								listener.onDataFinish(BusinessDataListener.DONE_USER_RESET, null, null, null);
								}else{
									listener.onDataFailed(BusinessDataListener.ERROR_USER_RESET, data.getString("tip"), null);
								}
							}else{
								listener.onDataFailed(BusinessDataListener.ERROR_USER_RESET, data.getString("description"), null);
							}

					}else{
						listener.onDataFailed(BusinessDataListener.ERROR_USER_RESET, ERROR_NET, null);
					}
				} catch (Exception e) {
					listener.onDataFailed(BusinessDataListener.ERROR_USER_RESET, ERROR_DATA, null);
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 *
	 */
	public void userReg(final Context mContext, final String userName, final String psd, final String code,
						final String invitationCode,final String unionid,final String openid,final String picUrl,
						final String nickname){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=register" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					//System.out.println(loginCode);


					jsonUrl.put("userName",userName );
					jsonUrl.put("psd", psd);
					jsonUrl.put("code", code);
					jsonUrl.put("invitationCode", invitationCode);
					jsonUrl.put("unionId",unionid);
					jsonUrl.put("openid",openid);
					jsonUrl.put("picUrl",picUrl);
					jsonUrl.put("nickname",nickname);
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
								String loginCode= data.getJSONObject("resultData").getString("loginCode");
								String loginUsername = data.getJSONObject("resultData").getString("userName");
								String unionId = data.getJSONObject("resultData").getString("unionId");
								loginCode = loginCode.split("\\^")[1];
								setUserData(loginUsername, loginCode, data.getJSONObject("resultData"));
								//本地保存

								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME, loginUsername);
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, loginCode);
								SPUtil.saveStringToSpByName(mContext,Constant.SP_NAME_NORMAL,Constant.SP_NAME_UnionId,unionId);
								listener.onDataFinish(BusinessDataListener.DONE_USER_REG, null, null, null);
								}else if (status==56000){
								listener.onDataFail(BusinessDataListener.NOT_USER_REG,null,null);
							}
							else{
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
	public void userLogin(final Context mContext, final String userName, final String pwd, final boolean needCallBack){
		ThreadPoolManager.getInstance().addTask(new Runnable() {

			@Override
			public void run() {
				try {
					String url = Constant.IP_URL + "/Api.ashx?req=Login" + CONSTANT_URL();
					JSONObject jsonUrl = new JSONObject();
					jsonUrl.put("userName",userName );
					jsonUrl.put("pwd", pwd);

					try {
						url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					L.i(">>>>>>>Login:" + url);
					MyJSONObject data = getDataFromSer(url);
					if(data != null){
						int resultCode = data.getInt("resultCode");
						if (resultCode == 1) {
							int status = data.getInt("status");
							if (status == 1) {
								setUserData(userName, pwd, data.getJSONObject("resultData"));
								//本地保存
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME, userName);
								SPUtil.saveStringToSpByName(mContext, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, pwd);

								if(needCallBack)
									listener.onDataFinish(BusinessDataListener.DONE_USER_LOGIN, null, null, null);
								}else{
									if(needCallBack)
										listener.onDataFailed(BusinessDataListener.ERROR_USER_LOGIN, data.getString("tip"), null);
								}
							}else{
								if(needCallBack)
									listener.onDataFailed(BusinessDataListener.ERROR_USER_LOGIN, data.getString("description"), null);
							}

					}else{
						if(needCallBack)
							listener.onDataFailed(BusinessDataListener.ERROR_USER_LOGIN, ERROR_NET, null);
					}
				} catch (Exception e) {
					if(needCallBack)
						listener.onDataFailed(BusinessDataListener.ERROR_USER_LOGIN, ERROR_DATA, null);
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
