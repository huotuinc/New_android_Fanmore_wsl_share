package cy.com.morefan.frag;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.List;


import cy.com.morefan.R;
import cy.com.morefan.adapter.GroupDataAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.supervision.CompanyActivity;
import cy.com.morefan.supervision.DepartmentActivity;
import cy.com.morefan.supervision.GroupActivity;
import cy.com.morefan.view.PullDownUpListView;

/**
 * Created by 47483 on 2016/5/9.
 */
public class ArchitectureFrag extends BaseFragment implements View.OnClickListener, Handler.Callback, BusinessDataListener,AdapterView.OnItemClickListener,PullDownUpListView.OnRefreshOrLoadListener {

    public PullDownUpListView listview;
    public ImageView layEmpty;
    List<GroupData> datas;
    List<GroupPersonData> groupPersonDatas;
    int taskID=0;
    Handler handler;
    GroupDataAdapter adapter;
    SupervisionService supervisionService;
    private static ArchitectureFrag frag;
    private View mRootView;

    public static ArchitectureFrag newInstance(){
        if(frag == null)
            frag = new ArchitectureFrag();
        return frag;
    }
    public void settaskID(int taskID){
        this.taskID = taskID;
    }
    @Override
    public boolean handleMessage(Message msg) {
        if( msg.what == BusinessDataListener.DONE_GET_GROUP_DATA ){
            Bundle bundle = (Bundle) msg.obj;
            GroupData[] results = (GroupData[]) bundle.getSerializable("Data");
            GroupPersonData[] results1 = (GroupPersonData[]) bundle.getSerializable("PersonData");
            int length = results.length;
            for (int i = 0; i < length; i++) {
                if(!datas.contains(results[i]))
                    datas.add(results[i]);
            }

            layEmpty.setVisibility( datas.size()<1? View.VISIBLE:View.GONE );
            adapter.notifyDataSetChanged();
            listview.onFinishLoad();
            listview.onFinishRefresh();
            dismissProgress();

        }else if(msg.what==BusinessDataListener.ERROR_GET_GROUP_DATA){
            layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
            dismissProgress();
            toast(msg.obj.toString());
            listview.onFinishLoad();
            listview.onFinishRefresh();
        }

        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.tab_architecture, container, false);
        listview= (PullDownUpListView) mRootView.findViewById(R.id.listView);
        layEmpty = (ImageView) mRootView.findViewById(R.id.layEmpty);
        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        datas=new ArrayList<GroupData>();
        groupPersonDatas = new ArrayList<GroupPersonData>();
        adapter = new GroupDataAdapter(getActivity(),datas,groupPersonDatas);
        listview.setAdapter(adapter);
        listview.setOnRefreshOrLoadListener(this);
        listview.setOnItemClickListener(this);
        loadData();
        //return super.onCreateView(inflater, container, savedInstanceState);
        return  mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        GroupData data = datas.get(position-1);
        if (data.getChildren()==1) {
            Intent intent = new Intent(getActivity(), CompanyActivity.class);
            intent.putExtra("data", data);
            this.startActivity(intent);
        }else {
            Intent intent = new Intent(getActivity(), DepartmentActivity.class);
            intent.putExtra("name", data.getName());
            intent.putExtra("data", data);
            this.startActivity(intent);
        }
    }


    protected void loadData(){
        datas.clear();
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.getGroupData(loginCode,0,taskID);
        showProgress();
    }
    @Override
    public void onReshow() {

    }

    @Override
    public void onFragPasue() {

    }

    @Override
    public void onRefresh() {
        loadData();

    }
    @Override
    public void onLoad() {

    }
    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        if( null != getActivity())

        if( type == BusinessDataListener.DONE_GET_GROUP_DATA ){
            handler.obtainMessage(type,extra).sendToTarget();
        }
    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        if( null != getActivity())

        handler.obtainMessage(type, des).sendToTarget();

    }

    @Override
    public void onDataFail(int type, String des, Bundle extra) {

    }

    private void showProgress(){
        if(getActivity() != null)
            ((GroupActivity)getActivity()).showProgress();
    }
    private void dismissProgress(){
        if(getActivity() != null)
            ((GroupActivity)getActivity()).dismissProgress();
    }
    private void toast(String msg){
        if(getActivity() != null)
            ((GroupActivity)getActivity()).toast(msg);
    }
}
