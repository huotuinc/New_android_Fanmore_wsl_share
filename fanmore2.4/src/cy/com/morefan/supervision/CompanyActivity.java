package cy.com.morefan.supervision;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.CompanyPageAdapter;
import cy.com.morefan.adapter.GroupDataAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.EmptyView;

public class CompanyActivity extends BaseActivity implements Handler.Callback , ViewPager.OnPageChangeListener {

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

    GroupData groupData;
    CompanyPageAdapter companyPageAdapter;
    List<GroupData> datas;
    Handler handler;
    GroupDataAdapter groupDataAdapter;
    SupervisionService supervisionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);

        if( getIntent().hasExtra("data") ) {
            groupData = (GroupData)getIntent().getSerializableExtra("data");
            String title = groupData.getName();
            tvTitle.setText(title);
        }

        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        datas=new ArrayList<GroupData>();
        groupDataAdapter = new GroupDataAdapter(this,datas);

        //createLayEmpty();
        companyPageAdapter = new CompanyPageAdapter(this,layEmpty);
        viewPager.setAdapter(companyPageAdapter);
        viewPager.addOnPageChangeListener(this);

        firstRefreshData();
    }

    protected void createLayEmpty(){
        layEmpty = new EmptyView(this);
        //layEmpty.setAnimation(AnimationUtils.loadAnimation(this , R.anim.anim_empty));
        //ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //layEmpty.setLayoutParams(layoutParams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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

    @Override
    public boolean handleMessage(Message msg) {
        if( msg.what == BusinessDataListener.DONE_GET_GROUP_DATA ){
            GroupData[] results = (GroupData[]) msg.obj;
            int length = results.length;
            for (int i = 0; i < length; i++) {
                if(!datas.contains(results[i]))
                    datas.add(results[i]);
            }

            layEmpty.setVisibility(datas.size() < 1 ? View.VISIBLE : View.GONE);
            companyPageAdapter.setDatas(datas);
            //adapter.notifyDataSetChanged();
            dismissProgress();
            return true;
        }else if(msg.what==BusinessDataListener.ERROR_GET_GROUP_DATA){
            companyPageAdapter.setDatas( datas );
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            toast(msg.obj.toString());
            //listview.onRefreshComplete();
            return true;
        }
        return false;
    }

    protected void firstRefreshData(){
        showProgress();
        loadData();
    }

    protected void loadData(){
        datas.clear();
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.getGroupData( groupData.getLevel() , loginCode , groupData.getId() , 0);
    }

    public void onClick(View v ){
        if( v.getId()==R.id.btnBack){
            this.finish();
        }else if(v.getId()==R.id.btnQuery){

        }
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
}
