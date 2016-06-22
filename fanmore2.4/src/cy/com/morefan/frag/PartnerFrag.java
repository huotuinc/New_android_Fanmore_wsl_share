package cy.com.morefan.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.ListAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.service.UserService;
import cy.com.morefan.supervision.MasterActivity;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.view.PullDownUpListView;

/**
 * Created by Administrator on 2016/6/22 0022.
 */

public class PartnerFrag extends BaseFragment implements PullDownUpListView.OnRefreshOrLoadListener, BusinessDataListener, Handler.Callback {
    private View mRootView;
    private ImageView layEmpty;
    private PullDownUpListView listView;
    SupervisionService supervisionService;
    private ListAdapter adapter;
    private List<BaseData> datas;
    private int pageTag;//分页标识
    private Handler mHandler = new Handler(this);
    private int userId=0;
    @Override
    public void onReshow() {

    }
    public void setUserid(int userId){
        this.userId = userId;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supervisionService = new SupervisionService(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_architecture, container, false);
        listView = (PullDownUpListView) mRootView.findViewById(R.id.listView);
        layEmpty = (ImageView) mRootView.findViewById(R.id.layEmpty);
        listView.setOnRefreshOrLoadListener(this);
        datas = new ArrayList<BaseData>();
        adapter = new ListAdapter(getActivity(), datas);
        listView.setAdapter(adapter);
        initData();
        return mRootView;
    }
    public void initData(){
        datas.clear();
        adapter.setDatas(datas);
        pageTag = 0;
        getDataFromSer();
    }
    public void getDataFromSer(){
        supervisionService.GetUserListByMasterId(UserData.getUserData().loginCode, userId ,pageTag+1);
        showProgress();
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
    public void onFragPasue() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case BusinessDataListener.DONE_GET_PRENTICE_LIST:
                dismissProgress();
                if(null != msg.obj){
                    BaseData[] results = (BaseData[]) msg.obj;
                    int length = results.length;
                    for (int i = 0; i < length; i++) {
                        BaseData item = results[i];

                        if(i== length -1)
                            pageTag = Integer.valueOf(item.getPageTag()).intValue();
                    }
                    adapter.setDatas(datas);
                }
                layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);


                    listView.onFinishLoad();
                listView.onFinishRefresh();


                break;

            case BusinessDataListener.ERROR_GET_PRENTICE_LIST:
                dismissProgress();
                toast(msg.obj.toString());
                 listView.onFinishLoad();
                 listView.onFinishRefresh();
                layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
                break;

            default:
                break;
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


}
