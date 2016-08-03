package cy.com.morefan.supervision;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.SupervisionService;
import cy.com.morefan.view.CircleImageView;

public class VipActivity extends BaseActivity implements Handler.Callback{

    @Bind(R.id.btnBack)
    Button btnBack;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.imglogo)
    CircleImageView imglogo;
    @Bind(R.id.tv_name)
    TextView tv_name;
    @Bind(R.id.TurnAmount)
    TextView TurnAmount;
    @Bind(R.id.BrowseAmount)
    TextView BrowseAmount;
    @Bind(R.id.taskLL)
    TextView taskLL;
    @Bind(R.id.groupLL)
    TextView groupLL;
    SupervisionService supervisionService;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        ButterKnife.bind(this);
        supervisionService = new SupervisionService(this);
        handler = new Handler(this);
        txtTitle.setText("监督管理");
        loadData();
        imglogo.setBorderColor(getResources().getColor(R.color.white));
        imglogo.setBorderWidth((int)getResources().getDimension(R.dimen.head_width));
    }
    public void loadData() {
        String loginCode = UserData.getUserData().loginCode;
        supervisionService.OrganizeSum(loginCode);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
        @Override
        public void onDataFinish(int type, String des, BaseData[] datas,Bundle extra) {
            if( null != this)
                this.onDataFinish(type, des, datas, extra);
            if(extra != null){

            }
            handler.obtainMessage(type, extra).sendToTarget();
        }
        @Override
        public void onDataFailed(int type, String des, Bundle extra) {
            if( null != this)
               this.onDataFailed(type, des, extra);
            handler.obtainMessage(type, des).sendToTarget();
        }

        @Override
        public void onDataFail(int type, String des, Bundle extra) {
            if( null != this)
                this.onDataFailed(type, des, extra);
            handler.obtainMessage(type, des).sendToTarget();
        }

    @Override
    public boolean handleMessage(Message msg) {
        if( msg.what == BusinessDataListener.DONE_GET_TASK_LIST ){
             dismissProgress();
            Bundle ex=(Bundle) msg.obj;
            tv_name.setText(ex.getString("Name"));
            imglogo.setImageURI(Uri.parse(ex.getString("logo")));

            }else if( msg.what == BusinessDataListener.ERROR_GET_TASK_LIST ){

        }
        return false;
    }
}
