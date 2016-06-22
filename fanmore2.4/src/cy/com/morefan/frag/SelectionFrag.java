package cy.com.morefan.frag;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.widget.AdapterView;
import android.widget.GridView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import cy.com.morefan.HomeActivity;
import cy.com.morefan.R;
import cy.com.morefan.TermActivity;
import cy.com.morefan.adapter.SelectionAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.StoreListData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.util.ActivityUtils;


/**
 * Created by 47483 on 2016/5/13.
 */
public class SelectionFrag extends BaseFragment implements View.OnClickListener, BusinessDataListener, android.os.Handler.Callback {

    private static SelectionFrag frag;
    private View mRootView;
    private int pageIndex = 0;//客户端现有发布时间最早任务id（做分页用）
    private int userId;
    private TaskService taskService;
    List<StoreListData> datas;
    StoreListData storeListData;
    Handler handler;
    SelectionAdapter adapter;
    private PullToRefreshGridView listView;


    public static SelectionFrag newInstance() {
        if (frag == null)
            frag = new SelectionFrag();
        return frag;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == BusinessDataListener.DONE_GET_STORE_LIST) {
            dismissProgress();
            StoreListData[] results = (StoreListData[]) msg.obj;
            int length = results.length;
            if (length>0) {
                if (results[0].pageIndex == 1) {
                    datas.clear();

                }
            }
            for (int i = 0; i < length; i++) {
                if(!datas.contains(results[i]))
                    datas.add(results[i]);
                pageIndex=results[i].pageIndex;

            }
            adapter.notifyDataSetChanged();
            listView.onRefreshComplete();




        }else if(msg.what == BusinessDataListener.ERROR_GET_STORE_LIST){
            dismissProgress();
            toast(msg.obj.toString());
            listView.onRefreshComplete();
        }
        return false;
    }

    @Override
    public void onReshow() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_selection, container, false);
        listView = (PullToRefreshGridView) mRootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(itemListener);
        taskService = new TaskService(this);
        handler = new Handler(this);
        storeListData= new StoreListData();




        initData();
        return mRootView;
    }


    private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Bundle bundle=new Bundle();
            bundle.putInt("userId",datas.get(position).getUserId());
            bundle.putString("username",datas.get(position).getUserNickName());
            ActivityUtils.getInstance().showActivity(getActivity(), TermActivity.class,bundle);



        }
    };
    protected void initData(){
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {

                initProducts();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                loadData();
            }
        });
        datas = new ArrayList<StoreListData>();
        adapter = new SelectionAdapter(getActivity(), datas);
        listView.setAdapter(adapter);
        firstGetData();
    }
    protected void firstGetData() {
        initProducts();
    }
    protected void initProducts(){
        pageIndex=0;
        datas.clear();
        adapter.notifyDataSetChanged();
        loadData();
    }
    protected void loadData() {
        String loginCode = UserData.getUserData().loginCode;
        taskService.getStoreList(loginCode, pageIndex+1);
        showProgress();
    }

    @Override
    public void onFragPasue() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        handler.obtainMessage(type, datas).sendToTarget();
    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        handler.obtainMessage(type, datas).sendToTarget();
    }

    @Override
    public void onDataFail(int type, String des, Bundle extra) {

    }

    private void showProgress() {
        if (getActivity() != null)
            ((HomeActivity) getActivity()).showProgress();
    }

    private void dismissProgress() {
        if (getActivity() != null)
            ((HomeActivity) getActivity()).dismissProgress();
    }

    private void toast(String msg) {
        if (getActivity() != null)
            ((HomeActivity) getActivity()).toast(msg);
    }


}
