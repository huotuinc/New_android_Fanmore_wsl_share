package cy.com.morefan.service;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.MyJSONObject;
import cy.com.morefan.bean.PartnerData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.AuthParamUtils;
import cy.com.morefan.util.JJSONUtil;
import cy.com.morefan.util.JsonUtil;
import cy.com.morefan.util.L;

/**
 * Created by Administrator on 2016/3/9.
 */
public class SupervisionService extends BaseService {
     long timestamp = System.currentTimeMillis();

    public SupervisionService(BusinessDataListener listener){
        super(listener);
    }


    /**
     * 获取平台所有的任务（按任务查看）
     * @param loginCode
     * @param keyword
     * @param pageIndex
     */
public void AllTask(final String loginCode, final String keyword,final int pageIndex){
    ThreadPoolManager.getInstance().addTask(new Runnable() {
        @Override
        public void run() {
            try {
                String url = Constant.IP_URL + "/Api.ashx?req=UserOrganizeAllTask" + CONSTANT_URL();
                JSONObject jsonUrl = new JSONObject();
                jsonUrl.put("loginCode",loginCode);
                jsonUrl.put("keyword", keyword);
                jsonUrl.put("pageIndex", pageIndex);
                jsonUrl.put("timestamp", timestamp);
                AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                Map< String, String > paramMap = new HashMap< String, String >( );
                paramMap.put("loginCode",loginCode);
                paramMap.put("keyword", keyword);
                paramMap.put("pageIndex", String.valueOf(pageIndex));
                paramMap.put("timestamp",String.valueOf(timestamp));
                String url2 = authParamUtils.getMapSign1(paramMap);
                jsonUrl.put("sign",url2);
                try {
                    url = url + URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MyJSONObject data = getDataFromSer(url);
                if (data != null) {
                    int pageIndex = data.getInt("pageIndex");
                    int resultCode = data.getInt("resultCode");
                    if (resultCode == 1) {
                        int status = data.getInt("status");
                        if (status == 1) {
                            JSONArray jArray = data.getJSONArray("resultData");
                            int length = jArray.length();
                            TaskData[] results = new TaskData[length];
                            for (int i = 0; i < length; i++) {
                                MyJSONObject tip = (MyJSONObject) jArray.get(i);
                                TaskData taskData = setTaskData(tip);
                                taskData.pageIndex = pageIndex;
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
     *获取企业组织架构及每级下的总人数，总转发和浏览量
     * @param logincode
     * @param orid
     * @param taskId
     */
    public void getGroupData( final String logincode, final int orid , final int taskId){
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=UserOrganize" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("loginCode", logincode);
                    jsonUrl.put("orid", orid);
                    jsonUrl.put("taskId", taskId);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("loginCode",logincode);
                    paramMap.put("orid", String.valueOf(orid));
                    paramMap.put("taskId", String.valueOf(taskId));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    L.i("UserOrganize:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int resultCode = data.getInt("resultCode");

                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                Bundle extra = new Bundle();
                                JSONArray jArray = data.getJSONArray("resultData");
                                int length = jArray.length();
                                GroupData[] results = new GroupData[length];
                                for (int i = 0; i < length; i++) {
                                    MyJSONObject obj = (MyJSONObject) jArray.get(i);
                                    GroupData item = setGroupData(obj);
                                    results[i] = item;
                                }
                                JSONArray jsonArray = data.getJSONArray("resultPersonData");
                                int length1 = jsonArray.length();
                                GroupPersonData[] results1 = new GroupPersonData[length1];
                                for (int i = 0; i < length1; i++) {
                                    MyJSONObject obj = (MyJSONObject) jsonArray.get(i);
                                    GroupPersonData item = setGroupPersonData(obj);
                                    results1[i] = item;
                                }
                                extra.putSerializable("Data",results);
                                extra.putSerializable("PersonData",results1);
                                listener.onDataFinish(BusinessDataListener.DONE_GET_GROUP_DATA, null, null, extra);
                            } else {
                                listener.onDataFailed(BusinessDataListener.ERROR_GET_GROUP_DATA, data.getString("tip"), null);
                            }
                        } else {
                            listener.onDataFailed(BusinessDataListener.ERROR_GET_GROUP_DATA, data.getString("description"), null);
                        }
                    } else {
                        listener.onDataFailed(BusinessDataListener.ERROR_GET_GROUP_DATA, ERROR_NET, null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDataFailed(BusinessDataListener.ERROR_GET_GROUP_DATA, ERROR_DATA, null);
                }
            }
        });
    }
    /**
     * 获取员工徒弟列表
     * @param loginCode
     * @param masterId
     * @param pageIndex
     *
     */
    public void GetUserListByMasterId(final String loginCode,final int masterId,final int pageIndex){
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String url=Constant.IP_URL + "/Api.ashx?req=GetUserListByMasterId" + CONSTANT_URL();
                    JSONObject jsonUrl=new JSONObject();
                    jsonUrl.put("loginCode",loginCode);
                    jsonUrl.put("masterId", masterId);
                    jsonUrl.put("timestamp", timestamp);
                    jsonUrl.put("pageIndex", pageIndex);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("loginCode",loginCode);
                    paramMap.put("masterId", String.valueOf(masterId));
                    paramMap.put("pageIndex", String.valueOf(pageIndex));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    try {
                        url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    L.i("GetGroupPerson:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int pageIndex = data.getInt("pageIndex");
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                JSONArray jArray = data.getJSONArray("resultData");
                                int length = jArray.length();
                                PartnerData[] results = new PartnerData[length];
                                for (int i = 0; i < length; i++) {
                                    MyJSONObject tip = (MyJSONObject) jArray.get(i);
                                    PartnerData item =new PartnerData();
                                    item.pageIndex = pageIndex;
                                    item.userName =tip.getString("userName");
                                    item.time =tip.getString("time");
                                    item.historyTotalBrowseAmount=tip.getString("historyTotalBrowseAmount");
                                    item.yesterdayBrowseAmount =tip.getString("yesterdayBrowseAmount");
                                    item.historyTotalTurnAmount=tip.getString("historyTotalTurnAmount");
                                    item.yesterdayTurnAmount=tip.getString("yesterdayTurnAmount");
                                    item.headFace =tip.getString("headFace");

                                    results[i] = item;
                                }
                                //Bundle extra = new Bundle();
                                //extra.putInt("status", result.getInt("status"));
                                //extra.putString("webUrl", result.getString("webUrl"));
                                listener.onDataFinish(BusinessDataListener.DONE_GET_PRENTICE_LIST, null, results, null);
                            } else {
                                listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_LIST, data.getString("tip"), null);
                            }
                        } else {
                            listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_LIST, data.getString("description"), null);
                        }
                    } else {
                        listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_LIST, ERROR_NET, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onDataFailed(BusinessDataListener.ERROR_GET_PRENTICE_LIST, ERROR_DATA, null);
                }
            }
        });
    }
    /**
     * 获取部门下的人员、人员积分、总转发、总浏览数据
     * @param loginCode 登录code
     * @param pageIndex 当前页码
     * @param pid 部门id
     * @param sort 排序类型 ，0默认排序,1转发，2浏览，3徒弟，4积分
     * @param taskId 任务ID 默认0
     */
    public void GetGroupPerson(final String loginCode,final int pageIndex,final int pid,final int sort,final int taskId){
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=GetGroupPerson" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("loginCode", loginCode);
                    jsonUrl.put("pageIndex", pageIndex);
                    jsonUrl.put("pid", pid);
                    jsonUrl.put("sort",sort);
                    jsonUrl.put("taskId", taskId);
                    jsonUrl.put("timestamp", timestamp);
                    AuthParamUtils authParamUtils =new AuthParamUtils(null,0,"",null);
                    Map< String, String > paramMap = new HashMap< String, String >( );
                    paramMap.put("loginCode",loginCode);
                    paramMap.put("pid", String.valueOf(pid));
                    paramMap.put("sort", String.valueOf(sort));
                    paramMap.put("taskId", String.valueOf(taskId));
                    paramMap.put("pageIndex", String.valueOf(pageIndex));
                    paramMap.put("timestamp",String.valueOf(timestamp));
                    String url2 = authParamUtils.getMapSign1(paramMap);
                    jsonUrl.put("sign",url2);
                    url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    L.i("GetGroupPerson:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int pageIndex = data.getInt("pageIndex");
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                JSONArray jArray = data.getJSONArray("resultData");
                                int length = jArray.length();
                                GroupPersonData[] results = new GroupPersonData[length];
                                for (int i = 0; i < length; i++) {
                                    MyJSONObject obj = (MyJSONObject) jArray.get(i);
                                    GroupPersonData item = setGroupPersonData(obj);
                                    item.pageIndex = pageIndex;
                                    results[i] = item;
                                }
                                //Bundle extra = new Bundle();
                                //extra.putInt("status", result.getInt("status"));
                                //extra.putString("webUrl", result.getString("webUrl"));
                                listener.onDataFinish(BusinessDataListener.DONE_GET_GROUP_PERSON, null, results, null);
                            } else {
                                listener.onDataFailed(BusinessDataListener.ERROR_GET_GROUP_PERSON, data.getString("tip"), null);
                            }
                        } else {
                            listener.onDataFailed(BusinessDataListener.ERROR_GET_GROUP_PERSON, data.getString("description"), null);
                        }
                    } else {
                        listener.onDataFailed(BusinessDataListener.ERROR_GET_GROUP_PERSON, ERROR_NET, null);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onDataFailed(BusinessDataListener.ERROR_GET_GROUP_PERSON, ERROR_DATA, null);
                }
            }
        });
    }


    protected GroupData setGroupData(MyJSONObject obj) throws JSONException {
        GroupData data = new GroupData();
        data.setChildren(obj.getInt("children"));
        data.setId(obj.getInt("orgid"));
        //data.setLevel(obj.getInt("level"));
        data.setName(obj.getString("name"));
        data.setPersonCount(obj.getInt("personCount"));
        data.setTotalBrowseCount(obj.getInt("totalBrowseCount"));
        data.setTotalTurnCount(obj.getInt("totalTurnCount"));
        return data;
    }
    protected GroupPersonData setGroupPersonData(MyJSONObject obj){
        GroupPersonData data = new GroupPersonData();
        data.setLogo(obj.getString("logo"));
        data.setName(obj.getString("name"));
        data.setPrenticeCount(obj.getInt("prenticeCount"));
        data.setTotalScore(obj.getInt("totalScore"));
        data.setTotalBrowseCount(obj.getInt("totalBrowseCount"));
        data.setTotalTurnCount(obj.getInt("totalTurnCount"));
        data.setUserid(obj.getInt("userid"));
        return data;
    }
}
