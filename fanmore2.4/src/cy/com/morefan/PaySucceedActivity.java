package cy.com.morefan;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.MyFrag;
import cy.com.morefan.util.AuthParamUtils;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.view.CyButton;

public class PaySucceedActivity extends BaseActivity implements View.OnClickListener,Handler.Callback {
    public CyButton btnBack;
    public Button btntoshop;
    public
    MainApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_succeed);
        btnBack= (CyButton) findViewById(R.id.btnBack);
        btntoshop=(Button)findViewById(R.id.btntoshop);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
//                Intent intent= new Intent(PaySucceedActivity.this, MyFrag.class);
//                startActivity(intent);
                finish();
                break;
            case R.id.btntoshop:
            showProgress();
            if (TextUtils.isEmpty(SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId))) {
                userService.GetUserList(this, UserData.getUserData().loginCode, SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_UnionId));
            } else {
                Intent intentshop = new Intent(this, WebShopActivity.class);
                AuthParamUtils paramUtils = new AuthParamUtils(application, System.currentTimeMillis(), BusinessStatic.getInstance().URL_WEBSITE, this);
                String url = paramUtils.obtainUrl();
                intentshop.putExtra("url", url);
                intentshop.putExtra("title", "商城");
                startActivity(intentshop);
                dismissProgress();
            }
                break;
            default:
                break;
        }
    }
}
