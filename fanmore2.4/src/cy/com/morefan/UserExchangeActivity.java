package cy.com.morefan;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserBaseInfoList;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.UserInfoView;

public class UserExchangeActivity extends BaseActivity implements UserInfoView.OnUserInfoBackListener , View.OnClickListener,Handler.Callback {
    public CyButton btnBack;
    public TextView txtLastWallet;
    public Button btnexchange;
    protected UserService userService;
    protected UserData userData;
    public TextView txtLastScore;
    public TextView txtDes;
    public LinearLayout layRecord;
    public LinearLayout laymoney;
    private Handler mHandler = new Handler(this);
    private UserInfoView userInfoView;


    @Override
    public void onUserInfoBack(UserInfoView.Type type, UserSelectData data) {
        if(data == null)
            return;
        Intent intentcrashpsd = new Intent(UserExchangeActivity.this, ToCrashAuthActivity.class);
        intentcrashpsd.putExtra("type", ToCrashAuthActivity.CrashAuthType.Auth);
        startActivity(intentcrashpsd);


       // userService.userchange(UserData.getUserData().loginCode,data.id,UserData.getUserData().score,UserData.getUserData().toCrashPwd);
    }

    public boolean handleMessage(Message msg) {
        dismissProgress();
        switch (msg.what) {
            case BusinessDataListener.ERROR_USER_LOGIN:
            case BusinessDataListener.ERROR_GET_DUIBA_URL:
            case BusinessDataListener.ERROR_GET_WALLET:
            case BusinessDataListener.ERROR_TO_RECHANGE:
            case BusinessDataListener.ERROR_TO_GETUSERLIST:

                toast(msg.obj.toString());

                break;
            case BusinessDataListener.DONE_USER_LOGIN:
                MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_LOGIN);
                refresh();
                break;
            case BusinessDataListener.DONE_TO_RECHANGE:
                toast(msg.obj.toString());
                refresh();

                break;
            case BusinessDataListener.DONE_TO_GETUSERLIST:

                userInfoView = new UserInfoView(this);
                userInfoView.setOnUserInfoBackListener(this);

                UserBaseInfoList data = (UserBaseInfoList)((Bundle) msg.obj).getSerializable("list");

                userInfoView.show(UserInfoView.Type.malluser, data.malluserlist , "");
                break;
            case BusinessDataListener.DONE_GET_WALLET:
                refreshView((Bundle)msg.obj);

                break;
            case BusinessDataListener.DONE_GET_DUIBA_URL:
                String loginUrl = ((Bundle)msg.obj).getString("loginurl");
                Intent intent = new Intent();
                intent.setClass(UserExchangeActivity.this, CreditActivity.class);
                intent.putExtra("navColor", "#0acbc1");    //配置导航条的背景颜色，请用#ffffff长格式。
                intent.putExtra("titleColor", "#ffffff");    //配置导航条标题的颜色，请用#ffffff长格式。
                intent.putExtra("url", loginUrl);    //配置自动登陆地址，每次需动态生成。
                startActivity(intent);

//			Intent intentDuiba = new Intent(this, CreditActivity.class);
//			intentDuiba.putExtra("url", loginUrl);
//			intentDuiba.putExtra("title", "兑换商城");
//			startActivity(intentDuiba);
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_exchange);
        txtLastScore= (TextView) findViewById(R.id.txtLastScore);
        txtLastWallet=(TextView)findViewById(R.id.txtLastWallet);
        txtDes=(TextView)findViewById(R.id.txtDes);
        layRecord=(LinearLayout)findViewById(R.id.layRecord);
        laymoney= (LinearLayout) findViewById(R.id.layMoney);
        userService = new UserService(this);
        btnBack= (CyButton) findViewById(R.id.btnBack);
        btnexchange= (Button) findViewById(R.id.btnexchange);
        btnexchange.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //history= (TextView) findViewById(R.id.history);

    }
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        refresh();
    }

    private void refresh() {
        userService.intGoldInfo(UserData.getUserData().loginCode, UserData.getUserData().score);

        showProgress();

    }
    private void refreshView(Bundle obj) {

        UserData userData = UserData.getUserData();
        if(!userData.isLogin){
            String msg = "0";
            txtLastScore.setText(msg);
            txtLastWallet.setText(msg);
        }else{
            txtLastScore.setText(userData.score);
            txtLastWallet.setText(userData.wallet);
        }
        txtDes.setText(obj.getString("des"));
        if(null == obj)
            return;
        //txtDes.setText(obj.getString("des"));



        if(obj.containsKey("recordTime")&&obj.containsKey("recordDes")&&obj.containsKey("recordResultDes")){

            laymoney.setVisibility(View.VISIBLE);
            //String record = obj.getString("recordDes");
            TextView txtTime = (TextView) findViewById(R.id.txtTime);
            TextView txtRecordDes = (TextView) findViewById(R.id.txtRecordDes);
            TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
            txtTime.setText((obj.getString("recordTime")));
            txtRecordDes.setText("转入钱包" + obj.getString("recordDes") + "元");
            txtStatus.setText(String.format(obj.getString("recordResultDes")));

        }else {
            laymoney.setVisibility(View.GONE);
        }



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;
//            case R.id.history:
//                break;
            case R.id.btnexchange:
                showProgress();

                userService.GetUserList(UserExchangeActivity.this,UserData.getUserData().loginCode,SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_UnionId));
                //userService.userchange();
                break;
            default:
                break;
        }

    }
    public boolean checkStatus(){
        if(!UserData.getUserData().isLogin){
            Intent intentlogin = new Intent(this, LoginActivity.class);
            startActivity(intentlogin);
            return false;
        }
        //积分是否已达标准下限
        if(Double.parseDouble(UserData.getUserData().score) < BusinessStatic.getInstance().CHANGE_BOUNDARY){
            toast(String.format("需达到%d积分才能转入钱包，赶快去赚积分吧!",BusinessStatic.getInstance().CHANGE_BOUNDARY));
            return false;
        }
        return true;
    }
    @Override
    public void onDataFinish(int type, String des, BaseData[] datas,
                             Bundle extra) {
        super.onDataFinish(type, des, datas, extra);
        if(type == BusinessDataListener.DONE_GET_DUIBA_URL
                || type == BusinessDataListener.DONE_GET_WALLET
                || type == BusinessDataListener.DONE_TO_GETUSERLIST
                ){
            mHandler.obtainMessage(type, extra).sendToTarget();
        }else if(type == BusinessDataListener.DONE_TO_RECHANGE){
            mHandler.obtainMessage(type, des).sendToTarget();
        }else{
            mHandler.obtainMessage(type).sendToTarget();
        }
    }
    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        mHandler.obtainMessage(type, des).sendToTarget();
    }

}
