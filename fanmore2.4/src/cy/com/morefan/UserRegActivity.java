package cy.com.morefan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.huotu.android.library.libedittext.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.EncryptUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.util.VolleyUtil;
import cy.com.morefan.view.CyButton;

public class UserRegActivity extends BaseActivity implements BusinessDataListener, Callback, BroadcastListener {

	@Bind(R.id.btnBack)
	CyButton btnBack;
	@Bind(R.id.txtTitle) TextView txtTitle;
	@Bind(R.id.edtPhone) EditText edtPhone;
	@Bind(R.id.btn_getcode) TextView btnGetcode;
	@Bind(R.id.edtPassword) EditText edtPassword;
	@Bind(R.id.edtCode) EditText edtCode;
	@Bind(R.id.edtinvitationCode) EditText edtinvitationCode;
	@Bind(R.id.linegone)
	ImageView linegone;
	private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);
	private UserService userService;
	private int isUpdate;

	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_USER_REG){
			dismissProgress();
			if (isUpdate==1){
				toast("修改密码成功!");
			}else {
				toast("注册成功!");
			}
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_LOGIN);
			Intent intenthome = new Intent(UserRegActivity.this, HomeActivity.class);
			startActivity(intenthome);
			handler.removeCallbacks(runnable);
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
		}else if (msg.what == BusinessDataListener.DONE_CHECK_VERIFYCODE){
			dismissProgress();
			toast("验证码正确，请设置密码");
			Bundle bundle=new Bundle();
			bundle.putString("moblie",edtPhone.getText().toString());
			bundle.putString("code",edtCode.getText().toString());
			handler.removeCallbacks(runnable);
			bundle.putInt("isUpdate",isUpdate);
			handler.removeCallbacks(runnable);
			ActivityUtils.getInstance().skipActivity(UserRegActivity.this,SetPassWordActivity.class,bundle);


		}else if (msg.what == BusinessDataListener.ERROR_CHECK_VERIFYCODE){
			dismissProgress();
			toast("验证码错误");
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_reg);
		ButterKnife.bind(this);

		userService = new UserService(this);
		Intent intent = getIntent();
		Bundle bundle=intent.getExtras();
		isUpdate=bundle.getInt("isUpdate");
		edtPassword.setVisibility(View.GONE);
		edtinvitationCode.setVisibility(View.GONE);
		linegone.setVisibility(View.GONE);

		txtTitle.setText(bundle.getString("title"));
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_SMS_RECEIVED, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}

	public void onClickButton(View v){
		super.onClickButton(v);
		switch (v.getId()) {

			case R.id.btn_getcode:
				String phone = edtPhone.getText().toString().trim();
				if (TextUtils.isEmpty(phone)){
					edtPhone.requestFocus();
					edtPhone.setError("手机号不能为空");
					return;
				}
				else if(!Util.isPhoneNum(phone)){
					edtPhone.requestFocus();
					edtPhone.setError("手机号错误");
					return;
				}
				userService.getAuthCode("",phone,3);
				showProgress();

				recLen = 60;
				handler.postDelayed(runnable, 1000);
				btnGetcode.setClickable(false);
				break;
			case R.id.btnReg:

					userupdata();

				break;


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
				btnGetcode.setText(String.format("%d秒后重新获取", recLen));
				handler.postDelayed(this, 1000);
			}else if(recLen == 0){
				btnGetcode.setText("重新获取");
				btnGetcode.setBackgroundResource(R.drawable.btn_code);
				btnGetcode.setClickable(true);
			}

		}
	};
//	public void userReg() {
//		String phone = edtPhone.getText().toString();
//		String userPwd = edtPassword.getText().toString();
//		String code = edtCode.getText().toString().trim();
//		String invitationCode =edtinvitationCode.getText().toString();
//		if(!Util.isPhoneNum(phone)){
//			edtPhone.requestFocus();
//			edtPhone.setError("手机号为11位数字且不能为空");
//			return;
//		}
//		if(TextUtils.isEmpty(userPwd)){
//			edtPassword.requestFocus();
//			edtPassword.setError("密码不能为空");
//			return;
//		}
//		if(BusinessStatic.getInstance().SMS_ENALBE && edtCode.getVisibility() == View.VISIBLE && TextUtils.isEmpty(code)){
//			edtCode.setError("验证码不能为空");
//			edtCode.requestFocus();
//			edtCode.requestFocusFromTouch();
//			return;
//		}
//		if (TextUtils.isEmpty(invitationCode)){
//			edtinvitationCode.setError("邀请码不能为空");
//			edtinvitationCode.requestFocus();
//			return;
//		}
//		userService.userMoblieReg(UserRegActivity.this,phone,code, EncryptUtil.getInstance().encryptMd532(userPwd),isUpdate,invitationCode);
//		showProgress();
//	}
	public void userupdata() {
		String phone = edtPhone.getText().toString();
		String code = edtCode.getText().toString().trim();
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
		userService.checkverifyCode(code,phone);
		showProgress();
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
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {

			finish();
			handler.removeCallbacks(runnable);
		}
		return true;
	}

	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}else if(type == ReceiverType.Sms){
			edtCode.setText(msg.toString());
		}

	}
}
