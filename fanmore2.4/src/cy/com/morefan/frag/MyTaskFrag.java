package cy.com.morefan.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.ArrayList;
import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.AwardAdapter;
import cy.com.morefan.bean.AllScoreData;
import cy.com.morefan.bean.AwardData;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.UserService;
import cy.com.morefan.supervision.MasterActivity;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;


/**
 * Created by Administrator on 2016/6/22 0022.
 */

public class MyTaskFrag extends BaseFragment implements OnRefreshOrLoadListener, BusinessDataListener, Handler.Callback {
    private ImageView layEmpty;
    private PullDownUpListView listView;
    private View mRootView;
    private ArrayList<AwardData> awardDatas;
    private ArrayList<AllScoreData> datas;
    AwardAdapter adapter;
    UserService userService;
    private String pageDate;
    private MyBroadcastReceiver myBroadcastReceiver;
    private Handler mHandler = new Handler(this);

    public PullDownUpListView getListView(){
        return this.listView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageDate = "";
        userService = new UserService(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


    mRootView = inflater.inflate(R.layout.tab_mytask, null);
    listView = (PullDownUpListView) mRootView.findViewById(R.id.listView);
    layEmpty = (ImageView) mRootView.findViewById(R.id.layEmpty);
        awardDatas = new ArrayList<AwardData>();
        datas = new ArrayList<AllScoreData>();
        SyncImageLoaderHelper mImageLoader = new SyncImageLoaderHelper(getActivity());
    listView.setOnRefreshOrLoadListener(this);
    adapter = new AwardAdapter(listView.getImageLoader(), getActivity(), awardDatas);
    listView.setAdapter(adapter);
        initData();
    return mRootView;
    }
    public void initData(){
        datas.clear();
        adapter.setDatas(awardDatas);
        pageDate = "";
        getDataFromSer();
    }
    public void getDataFromSer(){
        userService.getAllScoreTrendList(datas, UserData.getUserData().loginCode, Constant.PAGESIZE, pageDate);
        showProgress();
    }

    @Override
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
    public void onDataFinish(int type, String des, BaseData[] datas,
                             Bundle extra) {
        if( null != getActivity())
            ((MasterActivity)getActivity()).onDataFinish(type, des, datas, extra);
        mHandler.obtainMessage(type, datas).sendToTarget();

    }
    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        if( null != getActivity())
            ((MasterActivity)getActivity()).onDataFailed(type, des, extra);
        mHandler.obtainMessage(type, des).sendToTarget();
    }
    @Override
    public void onDataFail(int type, String des, Bundle extra) {

    }

    @Override
    public void onRefresh() {
        initData();

    }
    private void toast(String msg){
        if(getActivity() != null)
            ((BaseActivity)getActivity()).toast(msg);

    }
    private void showProgress(){
        if(getActivity() != null)
            ((BaseActivity)getActivity()).showProgress();
    }
    private void dismissProgress(){
        if(getActivity() != null)
            ((BaseActivity)getActivity()).dismissProgress();
    }
    @Override
    public void onLoad() {
        getDataFromSer();
    }

    @Override
    public void onReshow() {

    }

    @Override
    public void onFragPasue() {

    }

    @Override
    public void onClick(View view) {

    }
}
