package cy.com.morefan.service;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cy.com.morefan.bean.FavoriteData;
import cy.com.morefan.bean.MyJSONObject;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.AuthParamUtils;
import cy.com.morefan.util.L;
import cy.com.morefan.util.NetworkUtil;

public class ScoreService extends BaseService {
    long timestamp = System.currentTimeMillis();
    public ScoreService(BusinessDataListener listener) {
        super(listener);
    }

    public void getFavoriteDate(final Context mContext , final String loginCode ){
        ThreadPoolManager.getInstance().addTask(new Runnable() {

            @Override
            public void run() {

                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=FavoriteDate" + CONSTANT_URL();
                    //String url = Constant.IP_URL + "/Api.ashx?req=TodayNotice" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("loginCode", loginCode == null ? "" : loginCode);
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

                                JSONArray jArray = data.getJSONArray("resultData");

                                //JSONArray jArray = myJSONObject.getJSONArray("taskData");
                                //JSONArray jArray = data.getJSONArray("resultData");
                                int length = jArray.length();
                                String[] results = new String[length];
                                for (int i = 0; i < length; i++) {
                                    results[i] = jArray.get(i).toString();
                                }

                                Bundle bd = new Bundle();
                                bd.putSerializable("date" , results);

                                listener.onDataFinish(BusinessDataListener.DONE_GET_FAVORITE_DATE , null, null, bd);
                            }else{
                                listener.onDataFailed(BusinessDataListener.ERROR_GET_FAVORITE_DATE , data.getString("tip"), null);
                            }
                        }else{
                            String description = data.getString("description");
                            listener.onDataFailed(BusinessDataListener.ERROR_GET_FAVORITE_DATE, description, null);
                        }
                    }else
                        listener.onDataFailed(BusinessDataListener.ERROR_GET_FAVORITE_DATE, ERROR_NET, null);

                } catch (JSONException e) {
                    listener.onDataFailed(BusinessDataListener.ERROR_GET_FAVORITE_DATE, ERROR_DATA, null);
                    e.printStackTrace();
                }



            }
        });

    }

    public void getFavoriteList(final Context mContext, final String loginCode , final String month ){

            ThreadPoolManager.getInstance().addTask(new Runnable() {

                @Override
                public void run() {

                    try {
                        String url = Constant.IP_URL + "/Api.ashx?req=MyFavorite" + CONSTANT_URL();
                        //String url = Constant.IP_URL + "/Api.ashx?req=TodayNotice" + CONSTANT_URL();
                        JSONObject jsonUrl = new JSONObject();
                        jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
                        jsonUrl.put("month", month == null ? "" : month);
                        jsonUrl.put("timestamp", timestamp);
                        AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                        Map< String, String > paramMap = new HashMap< String, String >( );
                        paramMap.put("loginCode", loginCode == null ? "" : loginCode);
                        paramMap.put("month", month == null ? "" : month);
                        paramMap.put("timestamp",String.valueOf(timestamp));
                        String url2 = authParamUtils.getMapSign1(paramMap);
                        jsonUrl.put("sign",url2);
                        try {
                            url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        L.i("MyFavorite:" + url);
                        MyJSONObject data = getDataFromSer(url);


                        if(data != null){
                            int resultCode = data.getInt("resultCode");
                            if (resultCode == 1) {
                                int status = data.getInt("status");
                                if (status == 1) {
                                    //MyJSONObject myJSONObject = data.getJSONObject("resultData");
                                    JSONArray jArray = data.getJSONArray("resultData");
                                    //JSONArray jArray = data.getJSONArray("resultData");
                                    int length = jArray.length();
                                    FavoriteData[] results = new FavoriteData[length];
                                    for (int i = 0; i < length; i++) {
                                        MyJSONObject tip = (MyJSONObject) jArray.get(i);
                                        results[i] = setFavoriteData(tip);
                                    }



                                    listener.onDataFinish(BusinessDataListener.DONE_GET_FAVORITE_LIST, null, results, null);
                                }else{
                                    listener.onDataFailed(BusinessDataListener.ERROR_GET_FAVORITE_LIST , data.getString("tip"), null);
                                }
                            }else{
                                String description = data.getString("description");
                                listener.onDataFailed(BusinessDataListener.ERROR_GET_FAVORITE_LIST, description, null);
                            }
                        }else
                            listener.onDataFailed(BusinessDataListener.ERROR_GET_FAVORITE_LIST, ERROR_NET, null);

                    } catch (JSONException e) {
                        listener.onDataFailed(BusinessDataListener.ERROR_GET_FAVORITE_LIST, ERROR_DATA, null);
                        e.printStackTrace();
                    }

                }
            });

    }


    public void collection(final Context mContext,final String loginCode , final int taskId ){

        ThreadPoolManager.getInstance().addTask(new Runnable() {

            @Override
            public void run() {

                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=Collection" + CONSTANT_URL();
                    //String url = Constant.IP_URL + "/Api.ashx?req=TodayNotice" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
                    jsonUrl.put("taskId", taskId);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("loginCode" , loginCode == null ? "" : loginCode);
                    paramMap.put("taskId", String.valueOf( taskId ));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    try {
                        url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    L.i("Collection" + url);
                    MyJSONObject data = getDataFromSer(url);


                    if(data != null){
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                //MyJSONObject myJSONObject = data.getJSONObject("resultData");
                                Bundle bd = new Bundle();
                                bd.putInt("taskId",taskId);

                                listener.onDataFinish(BusinessDataListener.DONE_COLLECTION, null, null, bd);
                            }else{
                                listener.onDataFailed(BusinessDataListener.ERROR_COLLECTION , data.getString("tip"), null);
                            }
                        }else{
                            String description = data.getString("description");
                            listener.onDataFailed(BusinessDataListener.ERROR_COLLECTION, description, null);
                        }
                    }else
                        listener.onDataFailed(BusinessDataListener.ERROR_COLLECTION, ERROR_NET, null);

                } catch (JSONException e) {
                    listener.onDataFailed(BusinessDataListener.ERROR_COLLECTION, ERROR_DATA, null);
                    e.printStackTrace();
                }



            }
        });

    }


    public FavoriteData setFavoriteData(MyJSONObject tip ) throws JSONException {
        FavoriteData item = new FavoriteData();
        item.CreateTime = tip.getString("CreateTime");
        item.Id = tip.getInt("Id");
        item.TaskId = tip.getInt("TaskId");
        item.TaskName = tip.getString("TaskName");
        item.TaskPicUrl=tip.getString("TaskPicUrl");
        item.TurnUserId=tip.getInt("TurnUserId");
        item.time = tip.getString("time");
        item.UserNickName = tip.getString("UserNickName");
        item.Logo = tip.getString("Logo");
        return item;
    }



    public void getScoreInfo(final Context mContext,final String loginCode , final int mallUserId ){

        ThreadPoolManager.getInstance().addTask(new Runnable() {

            @Override
            public void run() {

                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=GetScoreInfo" + CONSTANT_URL();
                    //String url = Constant.IP_URL + "/Api.ashx?req=TodayNotice" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
                    jsonUrl.put("mallUserId", mallUserId);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("loginCode" , loginCode == null ? "" : loginCode);
                    paramMap.put("mallUserId", String.valueOf( mallUserId ));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    try {
                        url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    L.i("GetScoreInfo" + url);
                    MyJSONObject data = getDataFromSer(url);


                    if(data != null){
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                MyJSONObject myJSONObject = data.getJSONObject("resultData");

                                Bundle bd = new Bundle();
                                bd.putString("appScore", myJSONObject.getString("appScore"));
                                bd.putString("mallScore",myJSONObject.getString("mallScore"));
                                bd.putString("scoreRate", myJSONObject.getString("scoreRate"));

                                UserData.getUserData().score = myJSONObject.getString("appScore");

                                listener.onDataFinish(BusinessDataListener.DONE_SCORE, null, null, bd);
                            }else{
                                listener.onDataFailed(BusinessDataListener.ERROR_SCORE , data.getString("tip"), null);
                            }
                        }else{
                            String description = data.getString("description");
                            listener.onDataFailed(BusinessDataListener.ERROR_SCORE, description, null);
                        }
                    }else
                        listener.onDataFailed(BusinessDataListener.ERROR_SCORE, ERROR_NET, null);

                } catch (JSONException e) {
                    listener.onDataFailed(BusinessDataListener.ERROR_SCORE, ERROR_DATA, null);
                    e.printStackTrace();
                }

            }
        });

    }



    public void recharge(final Context mContext,final String loginCode , final String appScore , final int mallUserId ){

        ThreadPoolManager.getInstance().addTask(new Runnable() {

            @Override
            public void run() {

                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=Recharge" + CONSTANT_URL();
                    //String url = Constant.IP_URL + "/Api.ashx?req=TodayNotice" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("loginCode", loginCode == null ? "" : loginCode);
                    jsonUrl.put("score", appScore);
                    jsonUrl.put("mallUserId", mallUserId);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("loginCode" , loginCode == null ? "" : loginCode);
                    paramMap.put("score", appScore );
                    paramMap.put("mallUserId", String.valueOf( mallUserId ));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    try {
                        url = url+ URLEncoder.encode(jsonUrl.toString(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    L.i("Recharge" + url);
                    MyJSONObject data = getDataFromSer(url);


                    if(data != null){
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                MyJSONObject myJSONObject = data.getJSONObject("resultData");

                                Bundle bd = new Bundle();


                                listener.onDataFinish(BusinessDataListener.DONE_RECHARGE , null, null, bd);
                            }else{
                                listener.onDataFailed(BusinessDataListener.ERROR_RECHARGE , data.getString("tip"), null);
                            }
                        }else{
                            String description = data.getString("description");
                            listener.onDataFailed(BusinessDataListener.ERROR_RECHARGE, description, null);
                        }
                    }else
                        listener.onDataFailed(BusinessDataListener.ERROR_RECHARGE, ERROR_NET, null);

                } catch (JSONException e) {
                    listener.onDataFailed(BusinessDataListener.ERROR_RECHARGE, ERROR_DATA, null);
                    e.printStackTrace();
                }

            }
        });

    }

}
