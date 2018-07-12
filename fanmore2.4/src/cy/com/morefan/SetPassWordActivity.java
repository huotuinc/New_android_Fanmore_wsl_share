package cy.com.morefan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.huotu.android.library.libedittext.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.EncryptUtil;
import cy.com.morefan.util.VolleyUtil;

public class SetPassWordActivity extends BaseActivity implements View.OnClickListener , Handler.Callback {

    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.edtpsw)
    EditText edtpsw;
    @BindView(R.id.edtinvitationCode)
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
            if (isUpdate==1) {
                ActivityUtils.getInstance().skipActivity(SetPassWordActivity.this, MoblieLoginActivity.class);
            }else {
                ActivityUtils.getInstance().skipActivity(SetPassWordActivity.this, HomeActivity.class);
            }
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
        unbinder = ButterKnife.bind(this);
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
        if(password.length() <6 || password.length() > 12){
            edtPassword.requestFocus();
            edtPassword.setError("密码为6-12位");
            return;
        }
        if(TextUtils.isEmpty(psw)){
            edtpsw.requestFocus();
            edtpsw.setError("密码不能为空");
            return;
        }
        if(psw.length() <6 || psw.length() > 12){
            edtpsw.requestFocus();
            edtpsw.setError("密码为6-12位");
            return;
        }

        if (password.equals(psw)) {    

            String token = JPushInterface.getRegistrationID(SetPassWordActivity.this);
            if (isUpdate == 1) {
                showProgress();
                userService.userMoblieReg(SetPassWordActivity.this, moblie, code, EncryptUtil.getInstance().encryptMd532(password), isUpdate, "", token);
            } else {
//                if(TextUtils.isEmpty(invitationCode)){
//                    edtinvitationCode.requestFocus();
//                    edtinvitationCode.setError("邀请码不能为空");
//                    return;
//                }
                showProgress();
                userService.userMoblieReg(SetPassWordActivity.this, moblie, code, EncryptUtil.getInstance().encryptMd532(password), isUpdate, invitationCode, token);
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
