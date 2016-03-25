package cy.com.morefan.supervision;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.CompanyPageAdapter;
import cy.com.morefan.adapter.GroupDataAdapter;
import cy.com.morefan.adapter.GroupPersonDataAdapter;
import cy.com.morefan.adapter.GroupPersonPageAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.EmptyView;

public class DepartmentActivity extends BaseActivity implements Handler.Callback , ViewPager.OnPageChangeListener {
    @Bind(R.id.company_tab)
    public PagerTabStrip tab;
    @Bind(R.id.company_viewpager)
    public ViewPager viewPager;
    @Bind(R.id.btnBack)
    public CyButton btnBack;
    @Bind(R.id.txtTitle)
    public TextView tvTitle;
    @Bind(R.id.btnQuery)
    public CyButton btnQuery;
    @Bind(R.id.layEmpty)
    EmptyView layEmpty;
    int pid;

    GroupPersonData groupPersonData;
    GroupPersonPageAdapter groupPersonPageAdapter;
    List<GroupPersonData> datas;
    Handler handler;
    GroupPersonDataAdapter groupPersonDataAdapter;
    SupervisionService supervisionService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department);
        ButterKnife.bind(this);
        if( getIntent().hasExtra("name") ) {
            String title = getIntent().getStringExtra("name");
            pid =getIntent().getIntExtra("pid", 0);
            tvTitle.setText(title);
        }
        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        datas=new ArrayList<GroupPersonData>();
        groupPersonDataAdapter = new GroupPersonDataAdapter(this,datas);

        //createLayEmpty();
        groupPersonPageAdapter = new GroupPersonPageAdapter(this,layEmpty);
        viewPager.setAdapter(groupPersonPageAdapter);
        viewPager.addOnPageChangeListener(this);

        firstRefreshData();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
    @Override
    public boolean handleMessage(Message msg) {
        if( msg.what == BusinessDataListener.DONE_GET_GROUP_PERSON ){
            GroupPersonData[] results = (GroupPersonData[]) msg.obj;
            int length = results.length;
            for (int i = 0; i < length; i++) {
                if(!datas.contains(results[i]))
                    datas.add(results[i]);
            }

            layEmpty.setVisibility(datas.size() < 1 ? View.VISIBLE : View.GONE);
            groupPersonPageAdapter.setDatas(datas);
            //adapter.notifyDataSetChanged();
            dismissProgress();
            return true;
        }else if(msg.what==BusinessDataListener.ERROR_GET_GROUP_PERSON){
            groupPersonPageAdapter.setDatas( datas );
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            toast(msg.obj.toString());
            //listview.onRefreshComplete();
            return true;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    protected void firstRefreshData(){
        showProgress();
        loadData();
    }
    protected void loadData(){
        datas.clear();
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.GetGroupPerson(loginCode,1, pid, 0, 0);
    }
    public void onClick(View v ){
        if( v.getId()==R.id.btnBack){
            this.finish();
        }else if(v.getId()==R.id.btnQuery){

        }
    }



    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GroupPersonData data = datas.get(position-1);
        Intent intent = new Intent(this, DepartmentActivity.class);
        intent.putExtra("data", data);
        this.startActivity(intent);
    }
    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        if( type == BusinessDataListener.ERROR_GET_GROUP_PERSON ){
            handler.obtainMessage(type, des).sendToTarget();
        }
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);
        if( type == BusinessDataListener.DONE_GET_GROUP_PERSON ) {
            handler.obtainMessage(type, datas).sendToTarget();
        }
    }

}
