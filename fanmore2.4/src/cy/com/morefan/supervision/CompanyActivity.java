package cy.com.morefan.supervision;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import cy.com.morefan.view.PullDownUpListView;

public class CompanyActivity extends BaseActivity implements Handler.Callback ,AdapterView.OnItemClickListener,View.OnClickListener,PullDownUpListView.OnRefreshOrLoadListener  {


    @Bind(R.id.lay_mr)
    LinearLayout layMr;
    @Bind(R.id.txtmr)
    TextView txtmr;
    @Bind(R.id.mr_line)
    ImageView mrLine;
    @Bind(R.id.lay_zf)
    LinearLayout layZf;
    @Bind(R.id.txtzf)
    TextView txtzf;
    @Bind(R.id.zf_bottom_line)
    ImageView zfBottomLine;
    @Bind(R.id.lay_ll)
    LinearLayout lay_ll;
    @Bind(R.id.txtll) TextView txtll;
    @Bind(R.id.ll_bottom_line)
    ImageView llBottomLine;
    @Bind(R.id.listView)
    PullDownUpListView listView;
    @Bind(R.id.btnBack)
    public Button btnBack;
    @Bind(R.id.txtTitle)
    public TextView tvTitle;
    @Bind(R.id.btnQuery)
    public CyButton btnQuery;
    @Bind(R.id.layEmpty)
    EmptyView layEmpty;

    GroupData groupData;
    List<GroupData> datas;
    Handler handler;
    GroupDataAdapter groupDataAdapter;
    SupervisionService supervisionService;
    int tag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);
        lay_ll.setOnClickListener(this);
        layMr.setOnClickListener(this);
        layZf.setOnClickListener(this);

        if( getIntent().hasExtra("data") ) {
            groupData = (GroupData)getIntent().getSerializableExtra("data");
            String title = groupData.getName();
            tvTitle.setText(title);
        }
        if (txtmr.getTextColors()== ColorStateList.valueOf(getResources().getColor(R.color.theme_back))){
            tag=0;
        }else if (txtzf.getTextColors()== ColorStateList.valueOf(getResources().getColor(R.color.theme_back))){
            tag=1;
        }else if (txtll.getTextColors()== ColorStateList.valueOf(getResources().getColor(R.color.theme_back))){
            tag=2;
        }

        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        datas=new ArrayList<GroupData>();
        groupDataAdapter = new GroupDataAdapter(this,datas);
        listView.setAdapter(groupDataAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshOrLoadListener(this);
        listView.setPullUpToLoadEnable(false);
        firstRefreshData();
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
            layEmpty.setVisibility(datas.size() < 1 ? View.VISIBLE : View.GONE);
            if (tag==1){
                Collections.sort(datas, new Comparator<GroupData>() {
                    @Override
                    public int compare(GroupData lhs, GroupData rhs) {
                        if( lhs.getTotalTurnCount() < rhs.getTotalTurnCount() ) return 1;
                        else if( lhs.getTotalTurnCount() == rhs.getTotalTurnCount()) return 0;
                        else return -1;
                    }
                });
            }else if (tag==2){
                Collections.sort(datas, new Comparator<GroupData>() {
                    @Override
                    public int compare(GroupData lhs, GroupData rhs) {
                        if( lhs.getTotalBrowseCount() < rhs.getTotalBrowseCount() ) return 1;
                        else if( lhs.getTotalBrowseCount() == rhs.getTotalBrowseCount()) return 0;
                        else return -1;
                    }
                });
            }
            groupDataAdapter.notifyDataSetChanged();
            listView.onFinishLoad();
            listView.onFinishRefresh();
            //adapter.notifyDataSetChanged();
            dismissProgress();
            return true;
        }else if(msg.what==BusinessDataListener.ERROR_GET_GROUP_DATA){
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            listView.onFinishLoad();
            listView.onFinishRefresh();
            toast(msg.obj.toString());
            return true;
        }
        return false;
    }

    protected void firstRefreshData(){

        loadData();
    }

    protected void loadData(){
        showProgress();
        datas.clear();
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.getGroupData( loginCode, groupData.getId(), 0);
    }

    public void onClick(View v ){
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;
            case R.id.lay_mr:
                tag=0;
                txtmr.setTextColor(getResources().getColor(R.color.theme_back));
                mrLine.setBackgroundColor(getResources().getColor(R.color.theme_back));
                txtzf.setTextColor(getResources().getColor(R.color.black));
                txtll.setTextColor(getResources().getColor(R.color.black));
                zfBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
                llBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
                loadData();
                break;
            case R.id.lay_zf:
                tag=1;
                txtmr.setTextColor(getResources().getColor(R.color.black));
                mrLine.setBackgroundColor(getResources().getColor(R.color.white));
                txtzf.setTextColor(getResources().getColor(R.color.theme_back));
                txtll.setTextColor(getResources().getColor(R.color.black));
                zfBottomLine.setBackgroundColor(getResources().getColor(R.color.theme_back));
                llBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
                Collections.sort(datas, new Comparator<GroupData>() {
                    @Override
                    public int compare(GroupData lhs, GroupData rhs) {
                        if( lhs.getTotalTurnCount() < rhs.getTotalTurnCount() ) return 1;
                        else if( lhs.getTotalTurnCount() == rhs.getTotalTurnCount()) return 0;
                        else return -1;
                    }
                });

                groupDataAdapter.notifyDataSetChanged();

                break;
            case R.id.lay_ll:
                tag=2;
                txtmr.setTextColor(getResources().getColor(R.color.black));
                mrLine.setBackgroundColor(getResources().getColor(R.color.white));
                txtzf.setTextColor(getResources().getColor(R.color.black));
                txtll.setTextColor(getResources().getColor(R.color.theme_back));
                zfBottomLine.setBackgroundColor(getResources().getColor(R.color.white));
                llBottomLine.setBackgroundColor(getResources().getColor(R.color.theme_back));
                Collections.sort(datas, new Comparator<GroupData>() {
                    @Override
                    public int compare(GroupData lhs, GroupData rhs) {
                        if( lhs.getTotalBrowseCount() < rhs.getTotalBrowseCount() ) return 1;
                        else if( lhs.getTotalBrowseCount() == rhs.getTotalBrowseCount()) return 0;
                        else return -1;
                    }
                });

                groupDataAdapter.notifyDataSetChanged();
                break;
            default:
                break;

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GroupData data = datas.get(position-1);
        if(data.getChildren()==0) {
            Intent intent = new Intent(CompanyActivity.this, DepartmentActivity.class);
            intent.putExtra("name", data.getName());
            intent.putExtra("pid", data.getId());
            intent.putExtra("data", data);
            startActivity(intent);
        }else {
            Intent intent = new Intent(CompanyActivity.this, CompanyActivity.class);
            intent.putExtra("name", data.getName());
            intent.putExtra("pid", data.getId());
            intent.putExtra("data", data);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    @Override
    public void onLoad() {
        loadData();
    }
}
