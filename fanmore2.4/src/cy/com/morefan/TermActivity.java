package cy.com.morefan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.adapter.TaskAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.GroupTaskFrag;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.supervision.GroupActivity;
import cy.com.morefan.util.L;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.EmptyView;
import cy.com.morefan.view.PullDownUpListView;

public class TermActivity extends BaseActivity implements BusinessDataListener, Handler.Callback,PullDownUpListView.OnRefreshOrLoadListener,AdapterView.OnItemClickListener {


    @Bind(R.id.btnBack)
    CyButton btnBack;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.listView)
    PullDownUpListView listView;
    @Bind(R.id.layEmpty)
    EmptyView layEmpty;
    private int pageIndex;
    private TaskAdapter adapter;
    private List<TaskData> datas;
    private MyBroadcastReceiver myBroadcastReceiver;
    TaskService taskService;
    int userId=0;
    private Handler mHandler = new Handler(this);
    @Override
    public boolean handleMessage(Message msg){
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
            listView.onFinishLoad();
            listView.onFinishRefresh();
            adapter.notifyDataSetChanged();



        }else if(msg.what == BusinessDataListener.ERROR_GET_TASK_LIST){
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            toast(msg.obj.toString());
            listView.onFinishLoad();
            listView.onFinishRefresh();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term);
        ButterKnife.bind(this);
        pageIndex = 0;
        taskService =new TaskService(this);
        Bundle bundle = getIntent().getExtras();
        userId=bundle.getInt("userId");
        txtTitle.setText(bundle.getString("username"));
        listView.setOnRefreshOrLoadListener(this);
        listView.setOnItemClickListener(this);
        datas = new ArrayList<TaskData>();
        SyncImageLoaderHelper syncImageLoader = new SyncImageLoaderHelper(this);
        adapter = new TaskAdapter(syncImageLoader, this, datas, TaskAdapter.TaskAdapterType.Normal);
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

        taskService.getTaskList("", UserData.getUserData().loginCode,1,pageIndex+1, 0,userId,0);

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
    public void onRefresh() {
        initData();
    }

    @Override
    public void onLoad() {
        getDataFromSer();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(position < 1 || datas.size() == 0)
            return;
        Intent intentDetail = new Intent(this,TaskDetailActivity.class);
        TaskData taskData = datas.get(position - 1);
        taskData.position = position - 1;
        intentDetail.putExtra("taskData", taskData);
        startActivityForResult(intentDetail, 0);


    }
}

