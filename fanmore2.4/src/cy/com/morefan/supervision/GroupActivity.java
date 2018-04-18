package cy.com.morefan.supervision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.huibin.androidsegmentcontrol.SegmentControl;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.GroupDataAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.frag.ArchitectureFrag;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.view.PullDownUpListView;

/**
 *
 */
public class GroupActivity extends BaseActivity implements View.OnClickListener, Handler.Callback, BusinessDataListener,AdapterView.OnItemClickListener,PullDownUpListView.OnRefreshOrLoadListener {

    @BindView(R.id.listView)
    PullDownUpListView listview;
    @BindView(R.id.layEmpty)
    ImageView layEmpty;
    List<GroupData> datas;
    List<GroupPersonData> groupPersonDatas;
    int taskID=0;
    Handler handler;
    GroupDataAdapter adapter;
    SupervisionService supervisionService;
    private static ArchitectureFrag frag;
    private View mRootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_architecture);
        unbinder = ButterKnife.bind(this);
        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        datas=new ArrayList<GroupData>();
        groupPersonDatas = new ArrayList<GroupPersonData>();
        adapter = new GroupDataAdapter(this,datas,groupPersonDatas);
        listview.setAdapter(adapter);
        listview.setOnRefreshOrLoadListener(this);
        listview.setOnItemClickListener(this);
        loadData();
    }


    protected void loadData(){
        datas.clear();
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.getGroupData(loginCode,0,taskID);
        showProgress();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if( msg.what == BusinessDataListener.DONE_GET_GROUP_DATA ){
            Bundle bundle = (Bundle) msg.obj;
            GroupData[] results = (GroupData[]) bundle.getSerializable("Data");
            if (bundle.getSerializable("PersonData")!=null) {
                GroupPersonData[] results1 = (GroupPersonData[]) bundle.getSerializable("PersonData");
            }
            int length = results.length;
            for (int i = 0; i < length; i++) {
                if(!datas.contains(results[i]))
                    datas.add(results[i]);
            }

            layEmpty.setVisibility( datas.size()<1? View.VISIBLE:View.GONE );
            adapter.notifyDataSetChanged();
            listview.onFinishLoad();
            listview.onFinishRefresh();
            dismissProgress();

        }else if(msg.what==BusinessDataListener.ERROR_GET_GROUP_DATA){
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            toast(msg.obj.toString());
            listview.onFinishLoad();
            listview.onFinishRefresh();
        }

        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        GroupData data = datas.get(position-1);
        if (data.getChildren()==1) {
            Intent intent = new Intent(this, CompanyActivity.class);
            intent.putExtra("data", data);
            this.startActivity(intent);
        }else {
            Intent intent = new Intent(this, DepartmentActivity.class);
            intent.putExtra("name", data.getName());
            intent.putExtra("data", data);
            this.startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onLoad() {

    }


    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);
        handler.obtainMessage(type, extra).sendToTarget();
    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        handler.obtainMessage(type, des).sendToTarget();

    }


    public void onClick(View view){
        if(view.getId()==R.id.btnBack){
            this.finish();
            FragManager fragManager= new FragManager(this,R.id.layContent);
            fragManager.setCurrentFrag(FragManager.FragType.Task);
        }else if(view.getId()==R.id.btnQuery){

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ButterKnife.unbind(this);
    }




}
