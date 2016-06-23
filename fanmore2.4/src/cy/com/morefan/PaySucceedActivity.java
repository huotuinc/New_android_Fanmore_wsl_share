package cy.com.morefan;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.AuthParamUtils;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.view.CyButton;

public class PaySucceedActivity extends BaseActivity implements View.OnClickListener,Handler.Callback {
    public Button btnBack;
    public Button btntoshop;
    public
    MainApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(this);
        setContentView(R.layout.activity_pay_succeed);
        btntoshop=(Button)findViewById(R.id.btntoshop);
        btntoshop.setOnClickListener(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what== BusinessDataListener.DONE_TO_GETUSERLIST){
            intomall();
        }else if(msg.what == BusinessDataListener.ERROR_TO_GETUSERLIST){
            dismissProgress();
            //scrollView.onRefreshComplete();
            //head.onRefreshComplete();
            //dismissProgress();
            toast(msg.obj.toString());
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btntoshop:
          intomall();
                break;
            default:
                break;
        }
    }
    void intomall(){
        showProgress();
        if (TextUtils.isEmpty( SPUtil.getStringToSpByName( this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId))) {
            userService.GetUserList(this , UserData.getUserData().loginCode, SPUtil.getStringToSpByName(this , Constant.SP_NAME_NORMAL, Constant.SP_NAME_UnionId));
        }else{
            Intent intentshop = new Intent( this , WebShopActivity.class);
            AuthParamUtils paramUtils = new AuthParamUtils ( (MainApplication)this.getApplication() , System.currentTimeMillis(),BusinessStatic.getInstance().URL_WEBSITE, this );
            String url = paramUtils.obtainUrl();
            intentshop.putExtra("url", url);
            intentshop.putExtra("title", "商城");
            startActivity(intentshop);
            dismissProgress();
        }
    }

    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);
        if( type == BusinessDataListener.DONE_TO_GETUSERLIST){
            handler.obtainMessage(type).sendToTarget();
        }
    }

    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        if( type== BusinessDataListener.ERROR_TO_GETUSERLIST){
            handler.obtainMessage( type , des ).sendToTarget();
        }
    }
}
