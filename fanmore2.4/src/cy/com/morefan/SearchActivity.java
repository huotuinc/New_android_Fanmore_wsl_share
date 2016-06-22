package cy.com.morefan;

import android.content.Intent;
import android.os.Bundle;

import butterknife.Bind;
import butterknife.ButterKnife;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import cy.com.morefan.adapter.TaskAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.view.EmptyView;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;

public class SearchActivity extends BaseActivity implements Handler.Callback ,BusinessDataListener, OnRefreshOrLoadListener,View.OnClickListener {

    @Bind(R.id.btn_cancel) TextView btn_cancel;
    @Bind(R.id.search_et_input)
    EditText searchEtInput;
    @Bind(R.id.listView)
    PullDownUpListView listView;
    @Bind(R.id.layEmpty) EmptyView layEmpty;
    private int pageIndex;
    private TaskService taskService;
    private TaskAdapter adapter;
    private List<TaskData> datas;
    TaskData taskData;
    Handler handler;
    String keyword;
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
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);

//			//热门，推荐没有分页
//			if(currentTaskType == TaskType.Recommend || currentTaskType == TaskType.Hot){
//				listView.setPullUpToLoadEnable(false);
//			}else {
//				//listView.setPullUpToLoadEnable(length >= Constant.PAGESIZE);
//			}


            adapter.notifyDataSetChanged();
            listView.onFinishLoad();
            listView.onFinishRefresh();

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
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        listView.setOnRefreshOrLoadListener(this);
        listView.setOnItemClickListener(itemListener);
        btn_cancel.setOnClickListener(this);
        pageIndex = 0;
        taskService = new TaskService(this);
        handler = new Handler(this);
        datas = new ArrayList<TaskData>();
        adapter = new TaskAdapter(listView.getImageLoader(), this, datas, TaskAdapter.TaskAdapterType.Normal);
        listView.setAdapter(adapter);

        searchEtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                    try {
                        keyword = URLDecoder.decode(searchEtInput.getText().toString().trim(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(keyword)) {
                        Toast.makeText(SearchActivity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();

                        return false;
                    }
                    // 搜索功能主体
                    initData();


                    return true;
                }
                return false;
            }
        });
    }

    public void initData(){
        datas.clear();
        adapter.notifyDataSetChanged();
        pageIndex = 0;
        getDataFromSer();
    }
    public void reLoadData(){
        getDataFromSer();
    }
    private AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //head不能点击
            if (position < 1 || datas.size() == 0)
                return;
            Intent intentDetail = new Intent(SearchActivity.this, TaskDetailActivity.class);
            TaskData taskData = datas.get(position - 1);
            taskData.position = position - 1;
            intentDetail.putExtra("taskData", taskData);
            startActivityForResult(intentDetail, 0);

        }
    };
    public void getDataFromSer(){
        int taskType = 0;
        taskService.getTaskList(keyword, UserData.getUserData().loginCode,1,pageIndex+1, taskType,0,1);

        showProgress();
    }
    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        if( type == BusinessDataListener.ERROR_GET_TASK_LIST ){
            handler.obtainMessage(type, des).sendToTarget();
        }
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);
        if( type == BusinessDataListener.DONE_GET_TASK_LIST ) {
            handler.obtainMessage(type, datas).sendToTarget();
        }
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
        reLoadData();

    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_cancel:
               this.finish();
                break;
            default:
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
