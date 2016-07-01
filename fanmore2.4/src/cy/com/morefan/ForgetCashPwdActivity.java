package cy.com.morefan;

import cy.com.morefan.ToCrashAuthActivity.CrashAuthType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.SecurityUtil;
import cy.com.morefan.util.Util;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ForgetCashPwdActivity extends BaseActivity implements Callback, BroadcastListener{
	private UserService userService;
	private Handler mHandler = new Handler(this);
	//private EditText edtPwd;
	private MyBroadcastReceiver myBroadcastReceiver;
	private EditText edtPhone;
	private EditText edtCode;
	private TextView btnGet;
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_COMMIT_TOCRASHPWD){
			dismissProgress();
			toast("重置成功");
			Intent intentCrashPwd = new Intent(this, ToCrashAuthActivity.class);
			intentCrashPwd.putExtra("type", CrashAuthType.Creat);
			startActivity(intentCrashPwd);
			finish();


		}else if(msg.what == BusinessDataListener.DONE_COMMIT_TOCRASHPWD){
			dismissProgress();
			toast(msg.obj.toString());
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);

		Bundle extra = getIntent().getExtras();
		boolean isForgetLogin = false;
		if(extra != null)
			isForgetLogin = extra.getBoolean("isForgetLogin");

		if(isForgetLogin){
			setContentView(R.layout.user_forget_login);
		}else{
			setContentView(R.layout.user_forget);
			edtPhone = (EditText) findViewById(R.id.edtPhone);
			edtCode = (EditText) findViewById(R.id.edtCode);
			btnGet = (TextView) findViewById(R.id.btnGet);
		}
		userService = new UserService(this);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}
	OnEditorActionListener actionClickListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_ACTION_DONE){
				userReg();
				return true;
			}
			return false;
		}
	};
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

		showProgress();

	}
	public void onClickBottomTab(View v){
		switch (v.getId()) {
			case R.id.btnGet:
				String phone = edtPhone.getText().toString().trim();
				if(!Util.isPhoneNum(phone)){
					edtPhone.requestFocus();
					edtPhone.setError("手机号错误");
					return;
				}
				//test logincode,正式不需要logincode
				//String logincode = "test01^" + SecurityUtil.MD5Encryption("123456");
				//userService.getAuthCode("",phone,3);
				showProgress();

				recLen = 90;
				handler.postDelayed(runnable, 1000);
				btnGet.setClickable(false);
				btnGet.setBackgroundResource(R.drawable.shape_gray);
				break;
		case R.id.btnCommit:
			commit();
			break;

		default:
			break;
		}

	}
	public void commit(){

		userReg();

//		//加密上传提现密码
//		userService.userCommitToCrashPwd(UserData.getUserData().loginCode, SecurityUtil.MD5Encryption(""), UserData.getUserData().toCrashPwd);
//		showProgress();
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type).sendToTarget();

	}

	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();

	}
	@Override
	protected void onDestroy() {
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}

	}

}
