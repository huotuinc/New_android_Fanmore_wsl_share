package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.BaseActivity;


import cy.com.morefan.R;
import cy.com.morefan.TaskDetailActivity;
import cy.com.morefan.adapter.GroupDataAdapter;
import cy.com.morefan.adapter.TaskAdapter;
import cy.com.morefan.adapter.TaskAdapter.TaskAdapterType;
import cy.com.morefan.adapter.ToolAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.supervision.GroupActivity;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.view.EmptyView;
import cy.com.morefan.view.PullDownUpListView;



import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


public class GroupTaskFrag extends BaseFragment implements BusinessDataListener, Callback,AdapterView.OnItemClickListener {



    private static GroupTaskFrag frag;
    private View mRootView;
    private int pageIndex;//客户端现有发布时间最早任务id（做分页用）
    private SupervisionService supervisionService;
    private ListView listView;
    private TaskAdapter adapter;
    private List<TaskData> datas;
    private EmptyView layEmpty;
    private MyBroadcastReceiver myBroadcastReceiver;



    public static GroupTaskFrag newInstance(){
        if(frag == null)
            frag = new GroupTaskFrag();
        return frag;
    }


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

            adapter.notifyDataSetChanged();



        }else if(msg.what == BusinessDataListener.ERROR_GET_TASK_LIST){
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            toast(msg.obj.toString());

        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageIndex = 0;
        supervisionService = new SupervisionService(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)  {
        mRootView = inflater.inflate(R.layout.tab_grouptask, container, false);
        listView = (ListView) mRootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        layEmpty = (EmptyView) mRootView.findViewById(R.id.layEmpty);
        datas = new ArrayList<TaskData>();
        SyncImageLoaderHelper syncImageLoader = new SyncImageLoaderHelper(getActivity());
        adapter = new TaskAdapter(syncImageLoader, getActivity(), datas, TaskAdapterType.Normal);
        listView.setAdapter(adapter);
        initData();

        return mRootView;
    }

    @Override
    public void onReshow() {
        //initData();

    }

    @Override
    public void onFragPasue() {

    }

    @Override
    public void onClick(View view) {

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


    private void toast(String msg){
        if(getActivity() != null)
            ((BaseActivity)getActivity()).toast(msg);

    }
    private void showProgress(){
        if(getActivity() != null)
            ((BaseActivity)getActivity()).showProgress();
    }
    private void dismissProgress(){
        if(getActivity() != null )
            ((BaseActivity)getActivity()).dismissProgress();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        ((GroupActivity)getActivity()).setFrag();


    }
}
