package cy.com.morefan;

import java.util.ArrayList;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.adapter.AwardAdapter;
import cy.com.morefan.bean.AllScoreData;
import cy.com.morefan.bean.AwardData;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.constant.Constant.FromType;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.L;
import cy.com.morefan.view.CircleImageView;
import cy.com.morefan.view.ImageLoad;
import cy.com.morefan.view.TrendView;
import cy.com.morefan.view.TrendView.OnLoadMoreListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AllScoreActivity extends BaseActivity implements BroadcastListener, OnLoadMoreListener, Callback {
    private TrendView trendView;
    private UserService userService;
    private ArrayList<AllScoreData> datas;
    private ArrayList<AwardData> awardDatas;
    private AwardAdapter adapter;
    private String pageDate;
    private double maxScore;
    private double minScore;
    private int totalCount;
    private ListView listView;
    private ImageView layEmpty;

    private ImageView img_arrow;
    private MyBroadcastReceiver myBroadcastReceiver;
    private Handler mHandler = new Handler(this);

    private CircleImageView imglogo;
    private TextView tv_name;
    private TextView TurnAmount;
    private TextView BrowseAmount;
    private View headerView;

    @Override
    public boolean handleMessage(android.os.Message msg) {
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
            trendView.setTrendData(datas, minScore, maxScore);
            trendView.setOnFinishLoadMore();
            refreshView();

            //adapter.notifyDataSetChanged();
        } else if (msg.what == BusinessDataListener.ERROR_GET_ALLSCORE_TREND) {
            trendView.setOnFinishLoadMore();
            refreshView();
            dismissProgress();
            toast(msg.obj.toString());
        }
        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allscore);
        pageDate = "";
        listView = (ListView) findViewById(R.id.listView);
//        trendView = (TrendView) findViewById(R.id.trendView);
        layEmpty = (ImageView) findViewById(R.id.layEmpty);
        img_arrow = (ImageView) findViewById(R.id.img_arrow);
//        imglogo = (CircleImageView)findViewById(R.id.imglogo);
//        tv_name = (TextView)findViewById(R.id.tv_name);
//        TurnAmount = (TextView)findViewById(R.id.TurnAmount);
//        BrowseAmount=(TextView)findViewById(R.id.BrowseAmount);
//        imglogo.setBorderColor(getResources().getColor(R.color.white));
//        imglogo.setBorderWidth((int)getResources().getDimension(R.dimen.head_width));

        initHeader();


        userService = new UserService(this);




        awardDatas = new ArrayList<AwardData>();
        datas = new ArrayList<AllScoreData>();
        SyncImageLoaderHelper mImageLoader = new SyncImageLoaderHelper(this);
        adapter = new AwardAdapter(mImageLoader, this, awardDatas , "转发");
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        trendView.setListView(listView, adapter);
        //trendView.setTrendData(datas, -20, 50);
        trendView.setOnLoadMoreListener(this);
        initData();
        myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

        setLogo();
    }

    private void initHeader(){

        headerView = LayoutInflater.from(this).inflate(R.layout.layout_allscore_header,null);
        imglogo = (CircleImageView)headerView.findViewById(R.id.imglogo);
        tv_name = (TextView)headerView.findViewById(R.id.tv_name);
        TurnAmount = (TextView)headerView.findViewById(R.id.TurnAmount);
        BrowseAmount=(TextView)headerView.findViewById(R.id.BrowseAmount);
        imglogo.setBorderColor(getResources().getColor(R.color.white));
        imglogo.setBorderWidth((int)getResources().getDimension(R.dimen.head_width));
        trendView = (TrendView) headerView.findViewById(R.id.trendView);

        listView.addHeaderView(headerView);

    }

    private void setLogo(){
        UserData userData = UserData.getUserData();
        if (userData.isLogin) {
            String userName = userData.RealName;
            if (TextUtils.isEmpty(userName))
                userName = userData.UserNickName;
            else if (TextUtils.isEmpty(userName)){
                userName =userData.userName;
            }
            tv_name.setText( userName );

            if(TextUtils.isEmpty(userData.picUrl)){
                imglogo.setImageResource(R.drawable.user_icon);
            }else{
                //helper.loadImage(-1, img, null, userData.picUrl, Constant.BASE_IMAGE_PATH);
                ImageLoad.loadLogo(userData.picUrl, imglogo , this);
            }
        }else{

        }
    }

    public void refreshView() {
        trendView.setVisibility(datas.size() == 0 ? View.GONE : View.VISIBLE);
        listView.setVisibility(datas.size() == 0 ? View.GONE : View.VISIBLE);
        layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
        img_arrow.setVisibility(datas.size() == 0 ? View.GONE : View.GONE);
    }

    public void initData() {
        datas.clear();
        adapter.setDatas(awardDatas);
        //trendView.setTrendData(datas, 0, 0);
        pageDate = "";
        getDataFromSer();
    }

    public void getDataFromSer() {
        userService.getAllScoreTrendList(0,datas, UserData.getUserData().loginCode, Constant.PAGESIZE, pageDate);
        showProgress();
    }


    @Override
    public void onLoadMore() {
        //T.show(this, "加载更多");
        if (datas.size() < totalCount && !TextUtils.isEmpty(pageDate)) {
            getDataFromSer();
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRefresh:
                initData();
                break;
            case R.id.btnBack:
                finish();
                FragManager fragManager= new FragManager(this,R.id.layContent);
                fragManager.setCurrentFrag(FragManager.FragType.Task);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        mHandler.obtainMessage(type, des).sendToTarget();
        super.onDataFailed(type, des, extra);
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        if (type == DONE_GET_ALLSCORE_TREND && extra != null) {
            maxScore = Double.parseDouble(extra.getString("maxScore"));
            minScore = Double.parseDouble(extra.getString("minScore"));
            final String totalScore = extra.getString("totalScore");
            totalCount = extra.getInt("totalCount");
            final String totalBrowseAmount = extra.getString("totalBrowseAmount");
            final String totalTurnAmount = extra.getString("totalTurnAmount");
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    ((TextView) findViewById(R.id.txtTotal)).setText(totalScore);
                    TurnAmount.setText(  totalTurnAmount==null?"0":totalTurnAmount);
                    BrowseAmount.setText(totalBrowseAmount==null?"0":totalBrowseAmount);
                }
            });

        }
        mHandler.obtainMessage(type, datas).sendToTarget();
        super.onDataFinish(type, des, datas, extra);
    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        myBroadcastReceiver.unregisterReceiver();
    }

    @Override
    public void onFinishReceiver(ReceiverType type, Object msg) {
        if (type == ReceiverType.BackgroundBackToUpdate) {
            finish();

        }

    }

}
