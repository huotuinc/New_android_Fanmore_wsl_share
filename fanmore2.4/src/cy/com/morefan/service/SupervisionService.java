package cy.com.morefan.service;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.MyJSONObject;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.JJSONUtil;
import cy.com.morefan.util.JsonUtil;
import cy.com.morefan.util.L;

/**
 * Created by Administrator on 2016/3/9.
 */
public class SupervisionService extends BaseService {

    public SupervisionService(BusinessDataListener listener){
        super(listener);
    }

    /**
     *获取企业组织架构及每级下的总人数，总转发和浏览量
     * @param level 当前组织级别 默认1
     * @param logincode 登录代码
     * @param pid 组织架构ID
     */
    public void getGroupData(final int level , final String logincode, final int pid , final int taskId){
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = Constant.IP_URL + "/Api.ashx?req=UserOrganize" + CONSTANT_URL();
                    JSONObject jsonUrl = new JSONObject();
                    jsonUrl.put("level", level);
                    jsonUrl.put("loginCode", logincode);
                    jsonUrl.put("pid", pid);
                    jsonUrl.put("taskId",taskId);
                    url += URLEncoder.encode(jsonUrl.toString(), "UTF-8");
                    L.i("UserOrganize:" + url);
                    MyJSONObject data = getDataFromSer(url);
                    if (data != null) {
                        int resultCode = data.getInt("resultCode");
                        if (resultCode == 1) {
                            int status = data.getInt("status");
                            if (status == 1) {
                                JSONArray jArray = data.getJSONArray("resultData");
                                int length = jArray.length();
                                GroupData[] results = new GroupData[length];
                                for (int i = 0; i < length; i++) {
                                    MyJSONObject obj = (MyJSONObject) jArray.get(i);
                                    GroupData item = setGroupData(obj);
                                    results[i]=item;
                                }
                                //Bundle extra = new Bundle();
                                //extra.putInt("status", result.getInt("status"));
                                //extra.putString("webUrl", result.getString("webUrl"));
                                listener.onDataFinish(BusinessDataListener.DONE_GET_GROUP_DATA, null, results, null);
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


    protected GroupData setGroupData(MyJSONObject obj){
        GroupData data = new GroupData();
        data.setId(obj.getInt("id"));
        data.setLevel(obj.getInt("level"));
        data.setName(obj.getString("name"));
        data.setPersonCount(obj.getInt("personCount"));
        data.setTotalBrowseCount(obj.getInt("totalBrowseCount"));
        data.setTotalTurnCount(obj.getInt("totalTurnCount"));
        return data;
    }
}
