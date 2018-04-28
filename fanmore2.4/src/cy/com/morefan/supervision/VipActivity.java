package cy.com.morefan.supervision;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.view.CircleImageView;
import cy.com.morefan.view.ImageLoad;

public class VipActivity extends BaseActivity implements Handler.Callback{

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.imglogo)
    CircleImageView imglogo;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.TurnAmount)
    TextView TurnAmount;
    @BindView(R.id.BrowseAmount)
    TextView BrowseAmount;
    @BindView(R.id.taskLL)
    LinearLayout taskLL;
    @BindView(R.id.groupLL)
    LinearLayout groupLL;
    SupervisionService supervisionService;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        unbinder = ButterKnife.bind(this);
        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        txtTitle.setText("监督管理");
        imglogo.setBorderColor(getResources().getColor(R.color.white));
        imglogo.setBorderWidth((int)getResources().getDimension(R.dimen.head_width));
        loadData();
    }
    public void loadData() {
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.OrganizeSum(loginCode);
    }
    @OnClick({R.id.taskLL,R.id.groupLL})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.taskLL:
                ActivityUtils.getInstance().showActivity(VipActivity.this,ByTaskActivity.class);
                break;
            case R.id.groupLL:
                ActivityUtils.getInstance().showActivity(VipActivity.this,GroupActivity.class);
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {

        if(handler!=null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
        @Override
        public void onDataFinish(int type, String des, BaseData[] datas,Bundle extra) {
            super.onDataFinish(type, des, datas, extra);
            handler.obtainMessage(type, extra).sendToTarget();
        }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        handler.obtainMessage(type, des).sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if( msg.what == BusinessDataListener.DONE_GET_ORGANIZESUM){
             dismissProgress();
            Bundle ex=(Bundle) msg.obj;
            if (ex!=null) {
                tv_name.setText(ex.getString("Name"));
                if (ex.getString("Logo") == null || ex.getString("Logo").equals("")) {
                    imglogo.setImageResource(R.drawable.user_icon);
                } else {
                    ImageLoad.loadLogo(ex.getString("Logo"), imglogo, this);
                }
                TurnAmount.setText(ex.getString("TurnAmount"));
                BrowseAmount.setText(ex.getString("BrowseAmount"));
            }
            }else if( msg.what == BusinessDataListener.ERROR_GET_ORGANIZESUM ){
            dismissProgress();
            toast(msg.obj.toString());
        }
        return false;
    }
}
