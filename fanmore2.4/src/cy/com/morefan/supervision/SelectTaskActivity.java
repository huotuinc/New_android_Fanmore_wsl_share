package cy.com.morefan.supervision;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.GroupDataAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.util.ActivityUtils;

public class    SelectTaskActivity extends BaseActivity implements Handler.Callback,BusinessDataListener,AdapterView.OnItemClickListener{

    @Bind(R.id.ff1)
    FrameLayout ff1;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.layEmpty)
    ImageView layEmpty;
    List<GroupData> datas;
    int taskID=0;
    Handler handler;
    GroupDataAdapter adapter;
    SupervisionService supervisionService;

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
            adapter.notifyDataSetChanged();
            dismissProgress();

        }else if(msg.what==BusinessDataListener.ERROR_GET_GROUP_DATA){
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            toast(msg.obj.toString());
        }

        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_task);
        ButterKnife.bind(this);
        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        datas=new ArrayList<GroupData>();
        adapter = new GroupDataAdapter(this,datas);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);

        Intent intent = getIntent();
        Bundle bundle=intent.getExtras();
        taskID=bundle.getInt("taskId");
         loadData();
    }
    protected void loadData(){
        datas.clear();
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.getGroupData(loginCode,0,taskID);
        showProgress();
    }
    @OnClick(R.id.btnBack) void onBtnBackClick() {
        finish();
        //TODO implement
    }


    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        if( type == BusinessDataListener.ERROR_GET_GROUP_DATA ){
            handler.obtainMessage(type, des).sendToTarget();
        }
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);
        if( type == BusinessDataListener.DONE_GET_GROUP_DATA ) {
            handler.obtainMessage(type, datas).sendToTarget();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GroupData data = datas.get(position);
        if(data.getChildren()==0) {
            Bundle bundle =new Bundle();
            bundle.putInt("taskId",taskID);
            bundle.putString("name", data.getName());
            bundle.putInt("pid", data.getId());
            ActivityUtils.getInstance().showActivity(this,DepartmentActivity.class,bundle);
        }else {
            Bundle bundle =new Bundle();
            bundle.putInt("taskId",taskID);
            bundle.putString("name", data.getName());
            bundle.putInt("pid", data.getId());
            ActivityUtils.getInstance().showActivity(this,CompanyActivity.class,bundle);

        }
    }
}
