package cy.com.morefan.supervision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.SearchActivity;
import cy.com.morefan.adapter.SuperTaskAdapter;
import cy.com.morefan.adapter.TaskAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.L;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.EmptyView;
import cy.com.morefan.view.PullDownUpListView;

import static cy.com.morefan.R.id.btnRight;

public class ByTaskActivity extends BaseActivity implements Handler.Callback,AdapterView.OnItemClickListener,PullDownUpListView.OnRefreshOrLoadListener,View.OnClickListener {

    private int pageIndex;//客户端现有发布时间最早任务id（做分页用）
    private SupervisionService supervisionService;
    private PullDownUpListView listView;
    private SuperTaskAdapter adapter;
    private List<TaskData> datas;
    private EmptyView layEmpty;
    private Handler mHandler = new Handler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_grouptask);
        supervisionService = new SupervisionService(this);
        listView = (PullDownUpListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshOrLoadListener(this);
        listView.setPullUpToLoadEnable(false);
        CyButton btnRight = (CyButton)findViewById(R.id.btnRight);
        btnRight.setOnClickListener(this);
        layEmpty = (EmptyView) findViewById(R.id.layEmpty);
        datas = new ArrayList<TaskData>();
        SyncImageLoaderHelper syncImageLoader = new SyncImageLoaderHelper(this);
        adapter = new SuperTaskAdapter(syncImageLoader, this, datas, SuperTaskAdapter.TaskAdapterType.Normal);
        listView.setAdapter(adapter);
        initData();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Constant.RESULT_CODE_TASK_STATUS_CHANGE){
            if(data == null)
                return;

            TaskData taskData = (TaskData) data.getExtras().getSerializable("taskData");
            if(taskData != null){
                int postion = -1;
                for(TaskData item : datas){
                    if(item.id == taskData.id){
                        postion = item.position;
                        break;
                    }
                }
                if(-1 != postion){
                    datas.set(postion, taskData);
                    adapter.notifyDataSetChanged();
                }

            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    public void initData(){


        datas.clear();
        adapter.notifyDataSetChanged();
        pageIndex = 0;
        L.i("initData:" + datas.size());
        getDataFromSer();
    }
    public void getDataFromSer(){

        supervisionService.AllTask(UserData.getUserData().loginCode,"",pageIndex+1);

        showProgress();
    }
    @Override
    public void onDataFinish(int type, String des, BaseData[] datas,
                             Bundle extra) {
        mHandler.obtainMessage(type, datas).sendToTarget();

    }
    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        mHandler.obtainMessage(type, des).sendToTarget();
    }

    @Override
    public void onDataFail(int type, String des, Bundle extra) {

    }
    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == BusinessDataListener.DONE_GET_TASK_LIST) {
            dismissProgress();
            TaskData[] results = (TaskData[]) msg.obj;
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
            layEmpty.setVisibility( datas.size()<1? View.VISIBLE:View.GONE );

            adapter.notifyDataSetChanged();
            listView.onFinishLoad();
            listView.onFinishRefresh();

        }else if(msg.what == BusinessDataListener.ERROR_GET_TASK_LIST){
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            listView.onFinishLoad();
            listView.onFinishRefresh();
            toast(msg.obj.toString());

        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TaskData taskData =datas.get(position-1);
        Bundle bundle =new Bundle();
        bundle.putInt("taskId",taskData.id);
        ActivityUtils.getInstance().showActivity(ByTaskActivity.this, SelectTaskActivity.class,bundle);
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoad() {
        getDataFromSer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRight:
                Bundle bundle = new Bundle();
                bundle.putInt("type",1);
                ActivityUtils.getInstance().showActivity(ByTaskActivity.this, SearchActivity.class,bundle);
                break;
            default:
                break;
        }
    }
}
