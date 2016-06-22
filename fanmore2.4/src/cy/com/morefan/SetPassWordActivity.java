package cy.com.morefan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.huotu.android.library.libedittext.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.EncryptUtil;
import cy.com.morefan.util.VolleyUtil;
import cy.com.morefan.view.CyButton;

public class SetPassWordActivity extends BaseActivity implements View.OnClickListener , Handler.Callback {

    @Bind(R.id.btnBack)
    CyButton btnBack;
    @Bind(R.id.edtPassword)
    EditText edtPassword;
    @Bind(R.id.edtpsw)
    EditText edtpsw;
    @Bind(R.id.edtinvitationCode)
    EditText edtinvitationCode;
    Bundle bundle;
    private UserService userService;
    private Handler mHandler = new Handler(this);
    private int isUpdate;
    @Override
    public boolean handleMessage(Message msg) {
        if(msg.what == BusinessDataListener.DONE_USER_REG){
            dismissProgress();
            toast("设置密码成功!");
            ActivityUtils.getInstance().skipActivity(SetPassWordActivity.this,MoblieLoginActivity.class);
        }else if (msg.what == BusinessDataListener.ERROR_USER_REG){
            dismissProgress();
            toast(msg.obj.toString());
        }
        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pass_word);
        ButterKnife.bind(this);
        userService=new UserService(this);
        Bundle bundle=getIntent().getExtras();
        isUpdate=bundle.getInt("isUpdate");
        if (isUpdate==1){
            edtinvitationCode.setVisibility(View.GONE);
        }else {
            edtinvitationCode.setVisibility(View.VISIBLE);
        }
    }



    @OnClick(R.id.btnReg) void onBtnRegClick() {
        Intent intent = getIntent();
        bundle=intent.getExtras();
        String moblie = bundle.getString("moblie");
        String code = bundle.getString("code");
        String password =edtPassword.getText().toString();
        String psw =edtpsw.getText().toString();
        String invitationCode =edtinvitationCode.getText().toString();
        if(TextUtils.isEmpty(password)){
            edtPassword.requestFocus();
            edtPassword.setError("密码不能为空");
            return;
        }
        if(TextUtils.isEmpty(psw)){
            edtpsw.requestFocus();
            edtpsw.setError("密码不能为空");
            return;
        }
        if(TextUtils.isEmpty(invitationCode)){
            edtinvitationCode.requestFocus();
            edtinvitationCode.setError("邀请码不能为空");
            return;
        }
        if (password.equals(psw)) {
            showProgress();
            String token = JPushInterface.getRegistrationID(SetPassWordActivity.this);
            if (isUpdate == 1) {

                userService.userMoblieReg(SetPassWordActivity.this, moblie, code, EncryptUtil.getInstance().encryptMd532(password), isUpdate, invitationCode, token);
            } else {

                userService.userMoblieReg(SetPassWordActivity.this, moblie, code, EncryptUtil.getInstance().encryptMd532(password), isUpdate, "", token);
            }
        }else {
            toast("两次输入的密码不一致");
        }


    }

    @Override
    public void onClick(View v) {

    }
    @Override
    public void onDataFinish(int type, String des, BaseData[] datas,
                             Bundle extra) {
        super.onDataFinish(type, des, datas, extra);
        mHandler.obtainMessage(type, des).sendToTarget();

    }
    @Override
    public void onDataFailed(int type, String des, Bundle extra) {
        super.onDataFailed(type, des, extra);
        mHandler.obtainMessage(type,des).sendToTarget();

    }
    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        VolleyUtil.cancelAllRequest();
        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            finish();
        }
        return true;
    }
}
