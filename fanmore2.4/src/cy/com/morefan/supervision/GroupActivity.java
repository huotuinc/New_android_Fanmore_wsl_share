package cy.com.morefan.supervision;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edmodo.cropper.cropwindow.handle.Handle;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.GroupDataAdapter;
import cy.com.morefan.adapter.ToolAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.EmptyView;

/**
 *
 */
public class GroupActivity extends BaseActivity implements Handler.Callback, AdapterView.OnItemClickListener{
    @Bind(R.id.btnBack)
    public CyButton btnBack;
    @Bind(R.id.btnQuery)
    public CyButton btnQuery;
    @Bind(R.id.txtTitle)
    public TextView tvTitle;
    @Bind(R.id.listview)
    public PullToRefreshListView listview;
    @Bind(R.id.layEmpty)
    public EmptyView layEmpty;

    List<GroupData> datas;
    Handler handler;
    GroupDataAdapter adapter;
    SupervisionService supervisionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);

        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        datas=new ArrayList<GroupData>();
        adapter = new GroupDataAdapter(this,datas);
        listview.setAdapter(adapter);
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setOnItemClickListener(this);

        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        firstRefreshData();
    }

    protected void firstRefreshData(){
        showProgress();
        loadData();
    }

    protected void loadData(){
        datas.clear();
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.getGroupData( 0 , loginCode , 1 ,0);
    }

    public void onClick(View view){
        if(view.getId()==R.id.btnBack){
            this.finish();
        }else if(view.getId()==R.id.btnQuery){

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GroupData data = datas.get(position-1);
        Intent intent = new Intent(this, CompanyActivity.class);
        intent.putExtra("data", data);
        this.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if( msg.what == BusinessDataListener.DONE_GET_GROUP_DATA ){

            GroupData[] results = (GroupData[]) msg.obj;
            int length = results.length;
            for (int i = 0; i < length; i++) {
                if(!datas.contains(results[i]))
                    datas.add(results[i]);
            }

            layEmpty.setVisibility( datas.size()<1? View.VISIBLE:View.GONE );
            listview.onRefreshComplete();
            adapter.notifyDataSetChanged();
            dismissProgress();

        }else if(msg.what==BusinessDataListener.ERROR_GET_GROUP_DATA){
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            toast(msg.obj.toString());
            listview.onRefreshComplete();
        }

        return false;
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);

        if( type == BusinessDataListener.DONE_GET_GROUP_DATA ){
            handler.obtainMessage(type, datas).sendToTarget();
        }
    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);

        handler.obtainMessage(type, des).sendToTarget();

    }
}
