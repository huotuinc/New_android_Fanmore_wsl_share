package cy.com.morefan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.adapter.WeekTaskAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.bean.WeekTaskData;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.UserService;
import cy.com.morefan.view.CircleImageView;
import cy.com.morefan.view.ImageLoad;

public class WeekTaskActivity extends BaseActivity implements Handler.Callback {

    @Bind(R.id.listView)
    ListView listView;
private List<WeekTaskData> datas;
    private WeekTaskAdapter adapter;
    @Bind(R.id.btnBack)
    Button btnBack;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.mylevel)
    TextView mylevel;
    @Bind(R.id.myname)
    TextView myname;
    @Bind(R.id.imguser)
    CircleImageView imguser;
    UserData userData;
    private MyBroadcastReceiver myBroadcastReceiver;
    private Handler mHandler = new Handler(this);

//    @Bind(R.id.txtBrowseCount) TextView txtBrowseCount;
//    @Bind(R.id.BrowseCount) TextView browseCount;
//    @Bind(R.id.BrowseCountProgress)
//    ProgressBar browseCountProgress;
//    @Bind(R.id.txtTurnCount) TextView txtTurnCount;
//    @Bind(R.id.TurnCount) TextView turnCount;
//    @Bind(R.id.TurnCountProgress) ProgressBar turnCountProgress;
//    @Bind(R.id.txtPartnerCount) TextView txtPartnerCount;
//    @Bind(R.id.PartnerCount) TextView partnerCount;
//    @Bind(R.id.PartnerCountProgress) ProgressBar partnerCountProgress;
@Override
public boolean handleMessage(Message msg) {
    if(msg.what == BusinessDataListener.DONE_GET_WEEKTASK){
        dismissProgress();

        WeekTaskData[] results = (WeekTaskData[])msg.obj;
        int length = results.length;
        for (int i = 0; i < length; i++) {
            datas.add(results[i]);
        }
        adapter.setDatas(datas);
        listView.setAdapter(adapter);


    }else if(msg.what == BusinessDataListener.ERROR_GET_WEEKTASK){
        dismissProgress();
        toast(msg.obj.toString());

    }
    return false;
}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_task);
        ButterKnife.bind(this);
        imguser.setBorderColor(getResources().getColor(R.color.white));
        imguser.setBorderWidth((int)getResources().getDimension(R.dimen.head_width));
        userService = new UserService(this);
         userData= UserData.getUserData();
        datas = new ArrayList<WeekTaskData>();
        adapter = new WeekTaskAdapter(this, datas);
       String userName = userData.RealName;
        if (TextUtils.isEmpty(userName))
            userName = userData.UserNickName;
        else if (TextUtils.isEmpty(userName)){
            userName =userData.userName;
        }


    mylevel.setText(userData.levelName);
    myname.setText(userName);
    if(TextUtils.isEmpty(userData.picUrl)){
        imguser.setImageResource(R.drawable.user_icon);
    }else{
        //helper.loadImage(-1, img, null, userData.picUrl, Constant.BASE_IMAGE_PATH);
        ImageLoad.loadLogo(userData.picUrl, imguser, this);

    }
        initData();
        listView.setAdapter(adapter);

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                FragManager fragManager= new FragManager(this,R.id.layContent);
                fragManager.setCurrentFrag(FragManager.FragType.Task);
                break;
            default:
                break;
        }
    }
    public void initData(){
      String  logincode= UserData.getUserData().loginCode;
        userService.WeekTask(logincode);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
    @Override
    public void onDataFinish(int type, String des, BaseData[] datas,
                             Bundle extra) {
        super.onDataFinish(type, des, datas, extra);
        mHandler.obtainMessage(type, datas).sendToTarget();
    }
    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        mHandler.obtainMessage(type, des).sendToTarget();
    }
}
