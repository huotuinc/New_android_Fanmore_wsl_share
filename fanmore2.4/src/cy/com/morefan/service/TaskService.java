package cy.com.morefan.service;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cindy.android.test.synclistview.SyncImageLoaderHelper;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import cy.com.morefan.MainApplication;
import cy.com.morefan.bean.AdlistModel;
import cy.com.morefan.bean.MyJSONObject;
import cy.com.morefan.bean.PreTaskStatusData;
import cy.com.morefan.bean.SendData;
import cy.com.morefan.bean.StoreListData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.AuthParamUtils;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.Util;
import hz.huotu.wsl.aifenxiang.wxapi.WXEntryActivity;


public class TaskService extends BaseService {
    long timestamp = System.currentTimeMillis();
    public TaskService(BusinessDataListener listener) {
        super(listener);
    }

    public void commitToken(final Context context,final String loginCode, final String token,final int type) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {

                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=AddDeviceToken" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("loginCode",loginCode);
                    jsonUrl.put("token", token);
                    jsonUrl.put("type",type);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("loginCode",loginCode);
                    paramMap.put("token", String.valueOf(token));
                    paramMap.put("type", String.valueOf(type));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    L.i("UpdatePushToken:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
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

    /**
     * 获取任务列表
     *
     * @param loginCode
     * @param orderby
     * @param pageIndex
     * @param sortType
     */
    public void getTaskList(final String keyword, final String loginCode, final int orderby, final int pageIndex, final int sortType,
                            final int storeId, final int taskStaus) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=TaskList" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("keyword",keyword);
                    jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
                    jsonUrl.put("orderby", orderby);
                    jsonUrl.put("pageIndex", pageIndex);
                    jsonUrl.put("sortType", sortType);
                    jsonUrl.put("storeId",storeId);
                    jsonUrl.put("taskStaus",taskStaus);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("keyword",keyword);
                    paramMap.put("loginCode", loginCode == null ? "" : loginCode);
                    paramMap.put("orderby", String.valueOf(orderby));
                    paramMap.put("pageIndex", String.valueOf(pageIndex));
                    paramMap.put("sortType", String.valueOf(sortType));
                    paramMap.put("storeId", String.valueOf(storeId));
                    paramMap.put("taskStaus", String.valueOf(taskStaus));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    try {
                        url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    L.i("getTaskList:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int pageIndex = data.getInt("pageIndex");
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
                                    taskData.pageIndex = pageIndex;
                                    //标记top任务
                                    if (tops.contains(taskData.id + ""))
                                        taskData.isTop = 1;
                                    results[i] = taskData;
                                }
                                listener.onDataFinish(BusinessDataListener.DONE_GET_TASK_LIST, null, results, null);
                            } else {
                                listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, data.getString("tip"), null);
                            }
                        } else {
                            String description = data.getString("description");
                            listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_LIST, description, null);
                        }
                    } else
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
     *
     * @param taskData
     * @param loginCode
     */
    public void getTaskDetail(final TaskData taskData, final String loginCode) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=TaskDetail" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("taskId", taskData.id);
                    jsonUrl.put("loginCode", loginCode);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("taskId", String.valueOf(taskData.id));
                    paramMap.put("loginCode", loginCode);
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    try {
                        url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    L.i("getTaskDetail:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                MyJSONObject myJSONObject = data.getJSONObject("resultData");
                                String sendList = myJSONObject.getString("sendList");
                                if (TextUtils.isEmpty(sendList)) {
                                    taskData.channelIds = new ArrayList<String>();
                                } else {
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
                                //taskData.isSend = myJSONObject.getBoolean("isSend");
                                taskData.isFav = myJSONObject.getBoolean("isFav");
                                taskData.isAccount = myJSONObject.getBoolean("isAccount");

                                JSONArray jSend = myJSONObject.getJSONArray("sendData");

                                List<SendData> sendDatas = new ArrayList<SendData>();
                                int count = jSend.length();
                                for (int i = 0; i < count; i++) {
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
                            } else
                                listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_DETAIL, data.getString("tip"), null);
                        } else {
                            String description = data.getString("description");
                            listener.onDataFailed(BusinessDataListener.ERROR_GET_TASK_DETAIL, description, null);
                        }
                    } else {
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
    public void PayConfig(final Context mContext) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {

                    String url = Constant.IP_URL + "/Api.ashx?req=PayConfig" + CONSTANT_URL();


                    L.i(">>>>>>>>>>PayConfig:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                Bundle extra = new Bundle();
                                //MyJSONObject myJSONObject = data.getJSONObject("resultData");
                                JSONArray groups = data.getJSONArray("resultData");

                                String partnerid = "";
                                String appid = null;
                                String notify = null;
                                String appkey = null;
                                Boolean webpagepay = null;
                                for (int i = 0, length = groups.length(); i < length; i++) {
                                    MyJSONObject tip = (MyJSONObject) groups.get(i);
                                    int paytype = tip.getInt("payType");
                                    if (paytype == 300) {

                                        partnerid = tip.getString("partnerId");
                                        appid = tip.getString("appId");
                                        notify = tip.getString("notify");
                                        appkey = tip.getString("appKey");
                                        webpagepay = tip.getBoolean("webPagePay");
                                        ((MainApplication) mContext.getApplicationContext()).writeWx(partnerid, appid, appkey, notify, webpagepay);
                                    } else {

                                    }
                                }

                            } else {

                            }
                        } else {

                        }
                    } else {

                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        });
    }

    public void init(final Context mContext, final String userName, final String pwd, final int width, final int height, final SyncImageLoaderHelper loader) {
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
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("userName", userName);
                    paramMap.put("pwd", pwd);
                    paramMap.put("width", String.valueOf(width));
                    paramMap.put("height", String.valueOf(height));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    try {
                        url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    L.i(">>>>>>>>>>init:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                Bundle extra = new Bundle();
                                MyJSONObject myJSONObject = data.getJSONObject("resultData");
                                /**
                                 * 设置栏目数据
                                 */
//                                JSONArray groups = myJSONObject.getJSONArray("groups");
//                                for (int i = 0, length = groups.length(); i < length; i++) {
//                                    MyJSONObject tip = (MyJSONObject) groups.get(i);
//                                    BusinessStatic.getInstance().GROUPS.put(tip.getString("name"), tip.getInt("type"));
//                                }
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
                                //BusinessStatic.getInstance().grenadeRewardInfo = myJSONObject.getString("grenadeRewardInfo");
                                BusinessStatic.getInstance().disasterFlag = myJSONObject.getInt("disasterFlag");
                                BusinessStatic.getInstance().disasterUrl = myJSONObject.getString("disasterUrl");
                                //BusinessStatic.getInstance().URL_PUTIN = myJSONObject.getString("putInUrl");
                                //BusinessStatic.getInstance().URL_TOOL = myJSONObject.getString("toolUrl");
                                //BusinessStatic.getInstance().URL_MANUALSERVICE = myJSONObject.getString("manualServiceUrl");
                                BusinessStatic.getInstance().CRASH_TYPE = myJSONObject.getInt("CashType");
                                BusinessStatic.getInstance().WEIXIN_IGNORE_VERSION = myJSONObject.getString("wxVersionCode");
                                //是否完善个人信息z
                                extra.putInt("isCompleteUserInfo", myJSONObject.getInt("isCompleteUserInfo"));
                                BusinessStatic.getInstance().SMS_TAG = myJSONObject.getString("smsTag");
                                //BusinessStatic.getInstance().CHANGE_BOUNDARY = myJSONObject.getInt("changeBoundary");
//                                BusinessStatic.getInstance().AWARD_SEND = myJSONObject.getInt("taskTurnScore");
//                                BusinessStatic.getInstance().AWARD_SCAN = myJSONObject.getInt("taskBrowseScore");
//                                BusinessStatic.getInstance().AWARD_LINK = myJSONObject.getInt("taskLinkScore");
                                //BusinessStatic.getInstance().URL_RULE = myJSONObject.getString("ruleUrl");
                                BusinessStatic.getInstance().URL_WEBSITE = myJSONObject.getString("website");
                                BusinessStatic.getInstance().customerId = myJSONObject.getInt("customerId");
                                //BusinessStatic.getInstance().URL_ABOUTUS = myJSONObject.getString("aboutUsUrl");
                                //BusinessStatic.getInstance().URL_SERVICE = myJSONObject.getString("serviceUrl");
                                BusinessStatic.getInstance().TASK_TIME_LAG = myJSONObject.getInt("taskTimeLag");
                                String[] channels = myJSONObject.getString("channelList").split(",");
                                BusinessStatic.getInstance().CHANNEL_LIST = Arrays.asList(channels);
                                BusinessStatic.getInstance().SINA_KEY_SECRET = myJSONObject.getString("appSecret");
                                int smsEnable = myJSONObject.getInt("smsEnable");
                                //smsEnable	     0，正常;-1短信灾难
                                BusinessStatic.getInstance().SMS_ENALBE = !(smsEnable == -1);
                                //BusinessStatic.getInstance().ADIMG= myJSONObject.getString("adimg");
                                //BusinessStatic.getInstance().adclick=myJSONObject.getString("adclick");
                                BusinessStatic.getInstance().AppEnableRank= myJSONObject.getInt("AppEnableRank");
                                BusinessStatic.getInstance().AppEnableWeekTask = myJSONObject.getInt("AppEnableWeekTask");
                                BusinessStatic.getInstance().guide = myJSONObject.getString("guide");
                                BusinessStatic.getInstance().adTime = myJSONObject.getInt("adTime");
                                BusinessStatic.getInstance().weixinKey = myJSONObject.getString("weixinKey");
                                BusinessStatic.getInstance().weixinAppSecret = myJSONObject.getString("weixinAppSecret");
                                /**
                                 *3用户信息
                                 */
                                //1登录;0未登录
                                int loginStatus = myJSONObject.getInt("loginStatus");
                                if (loginStatus == 1) {
                                    setUserData(userName, pwd, myJSONObject.getJSONObject("userData"));
                                }
                                /**
                                 * 4开机图
                                 */
                                JSONArray jsonArray = null;

                                    jsonArray = myJSONObject.getJSONArray("AdList");

                                int length = jsonArray.length();
                                List<AdlistModel> results = new ArrayList<AdlistModel>();
                                for (int i = 0; i < length; i++) {
                                    MyJSONObject tip = (MyJSONObject) jsonArray.get(i);
                                    AdlistModel item = new AdlistModel();
                                    item.setItemCreateTime(tip.getString("itemCreateTime"));
                                    item.setItemId(tip.getInt("itemId"));
                                    item.setItemImgDescLink(tip.getString("itemImgDescLink"));
                                    item.setItemImgUrl(tip.getString("itemImgUrl"));
                                    item.setItemShowSort(tip.getInt("itemShowSort"));
                                    item.setItemShowTime(tip.getInt("itemShowTime"));
                                    results.add(item);
                                }
                                extra.putSerializable("adlist", (Serializable) results);

                                listener.onDataFinish(BusinessDataListener.DONE_INIT, null, null, extra);

                            } else {
                                listener.onDataFailed(BusinessDataListener.ERROR_INIT, data.getString("tip"), null);
                            }
                        } else {
                            listener.onDataFailed(BusinessDataListener.ERROR_INIT, data.getString("description"), null);
                        }
                    } else {
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
    public void checkUpdate() {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=checkUpdate" + CONSTANT_URL();
                    L.i(">>>>>>checkUpdate:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
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

                            } else
                                listener.onDataFailed(BusinessDataListener.ERROR_CHECK_UP, data.getString("tip"), null);
                        } else {
                            String description = data.getString("description");
                            listener.onDataFailed(BusinessDataListener.ERROR_CHECK_UP, description, null);
                        }
                    } else
                        listener.onDataFailed(BusinessDataListener.ERROR_CHECK_UP, ERROR_NET, null);

                } catch (Exception e) {
                    listener.onDataFailed(BusinessDataListener.ERROR_CHECK_UP, ERROR_DATA, null);
                }


            }
        });
    }

    /**
     * 获取商户列表
     */
    public void getStoreList(final String loginCode,final int pageIndex){
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=StoreList" + CONSTANT_URL();
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
                    url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int pageIndex = data.getInt("pageIndex");
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                JSONArray jArray = data.getJSONArray("resultData");
                                int length = jArray.length();
                                StoreListData[] results = new StoreListData[length];
                                for (int i = 0; i < length; i++) {
                                    MyJSONObject tip = (MyJSONObject) jArray.get(i);
                                    StoreListData item = new StoreListData();
                                    item.setLogo(tip.getString("Logo"));
                                    item.setUserId(tip.getInt("UserId"));
                                    item.setUserName(tip.getString("UserName"));
                                    item.setUserNickName(tip.getString("UserNickName"));
                                    item.pageIndex =pageIndex;
                                    results[i] = item;
                                }
                                listener.onDataFinish(BusinessDataListener.DONE_GET_STORE_LIST, null, results, null);

                            } else
                                listener.onDataFailed(BusinessDataListener.ERROR_GET_STORE_LIST, data.getString("tip"), null);
                        } else {
                            String description = data.getString("description");
                            listener.onDataFailed(BusinessDataListener.ERROR_GET_STORE_LIST, description, null);
                        }
                    } else
                        listener.onDataFailed(BusinessDataListener.ERROR_GET_STORE_LIST, ERROR_NET, null);

                } catch (Exception e) {
                    listener.onDataFailed(BusinessDataListener.ERROR_GET_STORE_LIST, ERROR_DATA, null);
                }
            }
        });
    }

}
