package cy.com.morefan.supervision;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.AwardAdapter;
import cy.com.morefan.bean.AllScoreData;
import cy.com.morefan.bean.AwardData;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.UserService;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.ImageLoad;
import cy.com.morefan.view.PullDownUpListView;

import static cy.com.morefan.R.id.layEmpty;

public class MasterActivity extends BaseActivity implements Handler.Callback,View.OnClickListener, MyBroadcastReceiver.BroadcastListener,PullDownUpListView.OnRefreshOrLoadListener {
    @Bind(R.id.btnBack)
    public CyButton btnBack;
    @Bind(R.id.imguser)
    public ImageView imguser;
    @Bind(R.id.listView)
    PullDownUpListView listView;
    TextView txt_partner;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.layEmpty)
    ImageView layEmpty;
    @Bind(R.id.rw)
    TextView rw;
    @Bind(R.id.hb)
    TextView hb;
    @Bind(R.id.browseCount) TextView browseCount;
    @Bind(R.id.TurnAmount) TextView turnAmount;
    @Bind(R.id.prenticeAmount) TextView prenticeAmount;
    @Bind(R.id.rw_bottom_line)
            ImageView rw_bottom_line;
    @Bind(R.id.hb_bottom_line)
            ImageView hb_bottom_line;
    int tag=0;
    GroupPersonData groupData;
    private ArrayList<AllScoreData> datas;
    private ArrayList<AwardData> awardDatas;
    private AwardAdapter adapter;
    private String pageDate;
    private MyBroadcastReceiver myBroadcastReceiver;
    private int totalCount;
    private Handler mHandler = new Handler(this);
    public boolean handleMessage(Message msg) {
        if (msg.what == BusinessDataListener.DONE_GET_ALLSCORE_TREND) {
            dismissProgress();
            if (TextUtils.isEmpty(pageDate))
                awardDatas.clear();
            AwardData[] results = (AwardData[]) msg.obj;
            for (int i = 0, length = results.length; i < length; i++) {
                awardDatas.add(results[i]);
                if (i == length - 1)
                    pageDate = results[i].date;
            }
            adapter.setDatas(awardDatas);
            adapter.notifyDataSetChanged();
            listView.onFinishLoad();
            listView.onFinishRefresh();



            //adapter.notifyDataSetChanged();
        } else if (msg.what == BusinessDataListener.ERROR_GET_ALLSCORE_TREND) {
            dismissProgress();
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            toast(msg.obj.toString());
            listView.onFinishLoad();
            listView.onFinishRefresh();
        }
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        ButterKnife.bind(this);
        userService = new UserService(this);
        pageDate = "";
        awardDatas = new ArrayList<AwardData>();
        datas = new ArrayList<AllScoreData>();
        SyncImageLoaderHelper mImageLoader = new SyncImageLoaderHelper(this);
        adapter = new AwardAdapter(mImageLoader, this, awardDatas);
        hb.setOnClickListener(this);
        rw.setOnClickListener(this);
        listView.setAdapter(adapter);
        listView.setOnRefreshOrLoadListener(this);
        adapter.notifyDataSetChanged();
        initData();
        myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
        if( getIntent().hasExtra("data") ) {
            groupData = (GroupPersonData)getIntent().getSerializableExtra("data");
            String title = groupData.getName();
            txtTitle.setText(title);
            if(TextUtils.isEmpty(groupData.getLogo())){
                imguser.setImageResource(R.drawable.user_icon);
            }else{
                ImageLoad.loadLogo(groupData.getLogo(), imguser, this);
            }
            browseCount.setText(String.valueOf(groupData.getTotalBrowseCount()));
            turnAmount.setText(String.valueOf(groupData.getTotalTurnCount()));
            prenticeAmount.setText(String.valueOf(groupData.getPrenticeCount()));
        }
    }
    public void initData() {
        datas.clear();
        adapter.setDatas(awardDatas);
        pageDate = "";
        getDataFromSer();
    }
    public void getDataFromSer() {
            userService.getAllScoreTrendList(datas, UserData.getUserData().loginCode, Constant.PAGESIZE, pageDate);
        showProgress();
    }
    @Override
    public void onRefresh() {
        initData();

    }
    @Override
    public void onLoad() {
        reLoadData();

    }
    public void reLoadData(){
        getDataFromSer();
    }
    public void onClick(View v ){
        if( v.getId()==R.id.btnBack){
            this.finish();
        }else if(v.getId()==R.id.btnQuery){

        }else if (v.getId()==R.id.hb){
            tag=2;
            hb_bottom_line.setBackgroundColor(getResources().getColor(R.color.theme_back));
            rw_bottom_line.setBackgroundColor(getResources().getColor(R.color.white));
        }else if (v.getId()==R.id.rw){
            tag=1;
            hb_bottom_line.setBackgroundColor(getResources().getColor(R.color.white));
            rw_bottom_line.setBackgroundColor(getResources().getColor(R.color.theme_back));
        }
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        if (type == DONE_GET_ALLSCORE_TREND && extra != null) {
            final String totalScore = extra.getString("totalScore");
            totalCount = extra.getInt("totalCount");
        }
        mHandler.obtainMessage(type, datas).sendToTarget();
        super.onDataFinish(type, des, datas, extra);
    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        mHandler.obtainMessage(type, des).sendToTarget();
        super.onDataFailed(type, des, extra);
    }

    @Override
    public void onDataFail(int type, String des, Bundle extra) {
        super.onDataFail(type, des, extra);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        myBroadcastReceiver.unregisterReceiver();
    }

    @Override
    public void onFinishReceiver(MyBroadcastReceiver.ReceiverType type, Object msg) {
        if (type == MyBroadcastReceiver.ReceiverType.BackgroundBackToUpdate) {
            finish();

        }
    }
}
