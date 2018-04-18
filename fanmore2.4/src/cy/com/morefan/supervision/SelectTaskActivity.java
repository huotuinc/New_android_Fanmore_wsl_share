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


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.GroupDataAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.util.ActivityUtils;

public class    SelectTaskActivity extends BaseActivity implements Handler.Callback,BusinessDataListener,AdapterView.OnItemClickListener{

    @BindView(R.id.ff1)
    FrameLayout ff1;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.layEmpty)
    ImageView layEmpty;
    List<GroupData> datas;
    List<GroupPersonData> personDatas;
    int taskID=0;
    Handler handler;
    GroupDataAdapter adapter;
    SupervisionService supervisionService;

    @Override
    public boolean handleMessage(Message msg) {
        if( msg.what == BusinessDataListener.DONE_GET_GROUP_DATA ){

            Bundle bundle = (Bundle) msg.obj;
            GroupData[] results = (GroupData[]) bundle.getSerializable("Data");
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
        unbinder = ButterKnife.bind(this);
        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        datas=new ArrayList<GroupData>();
        personDatas =new ArrayList<GroupPersonData>();
        adapter = new GroupDataAdapter(this,datas,personDatas);
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
            handler.obtainMessage(type, extra).sendToTarget();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GroupData data = datas.get(position);
        if (data.getChildren()==1) {
            Intent intent = new Intent(this, CompanyActivity.class);
            intent.putExtra("data", data);
            intent.putExtra("taskId",taskID);
            this.startActivity(intent);
        }else {
            Intent intent = new Intent(this, DepartmentActivity.class);
            intent.putExtra("name", data.getName());
            intent.putExtra("data", data);
            intent.putExtra("taskId",taskID);
            this.startActivity(intent);
        }
    }
}
