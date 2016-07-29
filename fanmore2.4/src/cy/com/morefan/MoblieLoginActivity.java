package cy.com.morefan;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huotu.android.library.libedittext.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.EncryptUtil;
import cy.com.morefan.util.Util;



public class MoblieLoginActivity extends BaseActivity implements Handler.Callback {


    @Bind(R.id.edtCode)
    EditText edtCode;
    @Bind(R.id.edtPhone)
    EditText edtPhone;
    @Bind(R.id.btnLogin)
    Button btnLogin;
    @Bind(R.id.Txt_forget_psw)
    TextView Txt_forget_psw;
    @Bind(R.id.Txt_reg)
    TextView Txt_reg;
    @Bind(R.id.txtInformation)
    TextView txtInformation;
    private UserService userService;
    private Handler mHandler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moblie_login);
        ButterKnife.bind(this);
        userService = new UserService(this);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            ActivityUtils.getInstance().showActivity(MoblieLoginActivity.this,NewWebActivity.class,bundle);
        }
    }
    @OnClick(R.id.Txt_reg)
    void reg(){
        Bundle bundle=new Bundle();
        bundle.putString("title","用户注册");
        bundle.putInt("isUpdate",0);
        ActivityUtils.getInstance().showActivity(this,UserRegActivity.class,bundle);
    }
    @OnClick(R.id.Txt_forget_psw)
    void forgetpsw(){
        Bundle bundle=new Bundle();
        bundle.putString("title","设置密码");
        bundle.putInt("isUpdate",1);
        ActivityUtils.getInstance().showActivity(this,UserRegActivity.class,bundle);
    }
    @OnClick(R.id.txtInformation)
    void phone(){
         String callphone = txtInformation.getText().toString();
        Intent intent =new Intent("android.intent.action.CALL", Uri.parse("tel:"+callphone));
        startActivity(intent);
    }
    @OnClick(R.id.btnLogin)
    void login() {
        String phone = edtPhone.getText().toString();
        String code = edtCode.getText().toString().trim();
        if(!Util.isPhoneNum(phone)){
            edtPhone.requestFocus();
            edtPhone.setError("手机号不能为空");
            return;
        }
        if(BusinessStatic.getInstance().SMS_ENALBE && edtCode.getVisibility() == View.VISIBLE && TextUtils.isEmpty(code)){
            edtCode.setError("密码不能为空");
            edtCode.requestFocus();
            edtCode.requestFocusFromTouch();
            return;
        }
        String usephone = edtPhone.getText().toString();
        String usecode= EncryptUtil.getInstance().encryptMd532(edtCode.getText().toString().trim());
        userService.MobileLogin(MoblieLoginActivity.this,usecode,usephone);
        showProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        //VolleyUtil.cancelAllRequest();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what== BusinessDataListener.DONE_TO_MOBLIELOGIN){
            dismissProgress();
            Intent intenthome = new Intent(MoblieLoginActivity.this, HomeActivity.class);
            startActivity(intenthome);
            finish();
        }else if (msg.what==BusinessDataListener.ERROR_TO_MOBLIELOGIN){
            dismissProgress();
            toast(msg.obj.toString());
        }else if (msg.what==BusinessDataListener.NULL_USER){
            dismissProgress();
            toast(msg.obj.toString());
        }
        return false;
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
    private long exitTime = 0 ;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                if (System.currentTimeMillis() - exitTime > 2000) {
                    toast("再按一次返回键退出");
                    exitTime = System.currentTimeMillis();
                } else {
                    MainApplication.finshApp();
                }

            return true;
        }else if(keyCode == KeyEvent.KEYCODE_MENU){

        }
        return super.onKeyDown(keyCode, event);
    }
}

