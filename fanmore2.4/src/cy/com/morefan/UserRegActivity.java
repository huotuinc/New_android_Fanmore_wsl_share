package cy.com.morefan;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.SecurityUtil;
import cy.com.morefan.util.Util;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import com.huotu.android.library.libedittext.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.security.cert.CertificateEncodingException;

public class UserRegActivity extends BaseActivity implements BusinessDataListener, Callback, BroadcastListener{
	private UserService userService;
	private EditText edtPhone;
	private EditText edtCode;
//	private EditText edtPwd;
//	private EditText edtRePwd;
	private TextView btnGet;
	private TextView txtDes;
	private RelativeLayout layCode;
	private ImageView imgPhoneLine;
	private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what==BusinessDataListener.DONE_TO_MOBLIELOGIN){
			dismissProgress();
			Intent intenthome = new Intent(UserRegActivity.this, HomeActivity.class);
			startActivity(intenthome);
			finish();
		}else if (msg.what==BusinessDataListener.ERROR_TO_MOBLIELOGIN){
			dismissProgress();
			toast(msg.obj.toString());
		}else if (msg.what==BusinessDataListener.NULL_USER){
			dismissProgress();
			String usephone = edtPhone.getText().toString();
			String usecode=edtCode.getText().toString().trim();
			popReg(2, usephone, usecode);
		}
		if(msg.what == BusinessDataListener.DONE_USER_REG){
			dismissProgress();
			toast("注册成功!");
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_LOGIN);
			Intent intenthome = new Intent(UserRegActivity.this, HomeActivity.class);
			startActivity(intenthome);
			finish();
		}else if(msg.what == BusinessDataListener.DONE_GET_CODE){
			dismissProgress();
			toast("验证码已发送,请注意查收短信!");
		}else if(msg.what == BusinessDataListener.ERROR_USER_REG || msg.what == BusinessDataListener.ERROR_GET_CODE){
			dismissProgress();
			toast(msg.obj.toString());
			if(msg.what == BusinessDataListener.ERROR_GET_CODE){
				dismissProgress();
				recLen = 1;
				handler.postAtTime(runnable, 0);
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.user_reg);
		edtPhone = (EditText) findViewById(R.id.edtPhone);
		edtCode = (EditText) findViewById(R.id.edtCode);
		//edtCode.setText("WSL0LOVE");
//		edtPwd = (EditText) findViewById(R.id.edtPwd);
//		edtRePwd = (EditText) findViewById(R.id.edtRePwd);
		btnGet = (TextView) findViewById(R.id.btnGet);
		txtDes = (TextView) findViewById(R.id.txtDes);
		layCode = (RelativeLayout) findViewById(R.id.layCode);
		imgPhoneLine = (ImageView) findViewById(R.id.imgPhoneLine);
		//((EditText)findViewById(R.id.edtInvitationCode)).setOnEditorActionListener(actionClickListener);

		layCode.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams params = (LayoutParams) imgPhoneLine.getLayoutParams();
		int left = BusinessStatic.getInstance().SMS_ENALBE ? DensityUtil.dip2px(this,	10f) : 0;
		params.setMargins(left, 0, 0, 0);
		imgPhoneLine.setLayoutParams(params);
//		edtCode.setVisibility(!BusinessStatic.SMS_ENALBE ? View.GONE : View.VISIBLE);
//		btnGet.setVisibility(!BusinessStatic.SMS_ENALBE ? View.GONE : View.VISIBLE);
		txtDes.setVisibility(TextUtils.isEmpty(BusinessStatic.getInstance().grenadeRewardInfo) ? View.GONE : View.GONE);
		txtDes.setText(BusinessStatic.getInstance().grenadeRewardInfo);

		userService = new UserService(this);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_SMS_RECEIVED, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

	}
//	OnEditorActionListener actionClickListener = new OnEditorActionListener() {
//		@Override
//		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//			if(actionId == EditorInfo.IME_ACTION_DONE){
//				userReg();
//				return true;
//			}
//			return false;
//		}
//	};
	public void onClickButton(View v){
		super.onClickButton(v);
		switch (v.getId()) {
		case R.id.imgTag:
			Intent intentRule = new Intent(this, WebViewActivity.class);
			intentRule.putExtra("url", BusinessStatic.getInstance().URL_RULE + "#shoutumimi");
			intentRule.putExtra("title", "规则说明");
			startActivity(intentRule);
			break;
		case R.id.btnGet:
			String phone = edtPhone.getText().toString().trim();
				if(!Util.isPhoneNum(phone)){
					edtPhone.requestFocus();
				edtPhone.setError("手机号错误");
				return;
			}
			//test logincode,正式不需要logincode
			//String logincode = "test01^" + SecurityUtil.MD5Encryption("123456");
			userService.getAuthCode("",phone,3);
			showProgress();

			recLen = 90;
			handler.postDelayed(runnable, 1000);
			btnGet.setClickable(false);
			btnGet.setBackgroundResource(R.drawable.shape_gray);
			break;
		case R.id.btnReg:
			userReg();


			break;
//		case R.id.txtYinSi:
//			Intent intentlogin = new Intent(UserRegActivity.this, LoginActivity.class);
//			startActivity(intentlogin);
//			finish();
//			break;

		default:
			break;
		}
	}



	private int recLen;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
        	recLen--;
            if(recLen > 0){
            	btnGet.setText(String.format("%d秒后重新获取", recLen));
 	            handler.postDelayed(this, 1000);
            }else if(recLen == 0){
            	btnGet.setText("重新获取");
            	btnGet.setBackgroundResource(R.drawable.shape_red);
            	btnGet.setClickable(true);
            }

        }
    };

	public void userReg() {
		edtPhone.setError(null);
		edtCode.setError(null);
		String phone = edtPhone.getText().toString();
		//String userPwd = edtPwd.getText().toString();
		String code = edtCode.getText().toString().trim();
		//String userRePwd = edtRePwd.getText().toString();
		if(!Util.isPhoneNum(phone)){
			edtPhone.requestFocus();
			edtPhone.setError("手机号不能为空");
			return;
		}
		if(BusinessStatic.getInstance().SMS_ENALBE && edtCode.getVisibility() == View.VISIBLE && TextUtils.isEmpty(code)){
			edtCode.setError("验证码不能为空");
			edtCode.requestFocus();
			edtCode.requestFocusFromTouch();
			return;
		}
		String usephone = edtPhone.getText().toString();
		String usecode=edtCode.getText().toString().trim();
		userService.MobileLogin(UserRegActivity.this,usephone,usecode);
		showProgress();
//		if(!Util.userPwdIsLegal(userPwd).equals("success")){
//			edtPwd.setError(Util.userPwdIsLegal(userPwd));
//			edtPwd.requestFocus();
//			edtPwd.requestFocusFromTouch();
//			return;
//		}
//
//		if(!Util.userPwdIsLegal(userRePwd).equals("success")){
//			edtRePwd.setError(Util.userPwdIsLegal(userRePwd));
//			edtRePwd.requestFocus();
//			edtRePwd.requestFocusFromTouch();
//			return;
//		}
//		if(!userPwd.equals(userRePwd)){
//			edtRePwd.setError("两次输入不同!");
//			edtRePwd.requestFocus();
//			edtRePwd.requestFocusFromTouch();
//			return;
//		}
		//String invitationCode = ((EditText)findViewById(R.id.edtInvitationCode)).getText().toString().trim();
		//userService.userReg(this, userName,null, code, null,null,null );
		//showProgress();

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
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {

			finish();
		}
		return true;
	}

	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}else if(type == ReceiverType.Sms){
			edtCode.setText(msg.toString());
			//edtPwd.requestFocus();

		}

	}

}
