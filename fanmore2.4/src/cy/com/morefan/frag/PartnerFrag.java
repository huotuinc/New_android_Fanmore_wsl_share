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
import cy.com.morefan.adapter.PartnerAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.PartnerData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.supervision.MasterActivity;
import cy.com.morefan.view.PullDownUpListView;

/**
 * Created by Administrator on 2016/6/22 0022.
 */

public class PartnerFrag extends BaseFragment implements PullDownUpListView.OnRefreshOrLoadListener, BusinessDataListener, Handler.Callback {
    private View mRootView;
    private ImageView layEmpty;
    private PullDownUpListView listView;
    SupervisionService supervisionService;
    private PartnerAdapter adapter;
    private List<PartnerData> datas;
    private int pageIndex;//分页标识
    private Handler mHandler = new Handler(this);
    private int userId;
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
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if(bundle!=null){
            userId=(bundle.getInt("userid"));
        }
        datas = new ArrayList<PartnerData>();
        adapter = new PartnerAdapter(getActivity(), datas);
        listView.setAdapter(adapter);
        initData();
        return mRootView;
    }
    public void initData(){
        datas.clear();
        pageIndex = 0;
        getDataFromSer();
    }
    public void getDataFromSer(){
        supervisionService.GetUserListByMasterId(UserData.getUserData().loginCode, userId ,pageIndex+1);
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
                if (null != msg.obj) {
                    PartnerData[] results = (PartnerData[]) msg.obj;
                    int length = results.length;
                    if (length > 0) {
                        if (results[0].pageIndex == 1) {
                            datas.clear();

                        }
                    }


                    for (int i = 0; i < length; i++) {
                        if (!datas.contains(results[i]))
                            datas.add(results[i]);
                        pageIndex = results[i].pageIndex;
                    }
                    layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);


                    adapter.notifyDataSetChanged();
                    listView.onFinishLoad();
                    listView.onFinishRefresh();
                }
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
