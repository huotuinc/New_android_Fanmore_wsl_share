package cy.com.morefan;

import cy.com.morefan.AuthCodeSendActivity.AuthType;
import cy.com.morefan.ToCrashAuthActivity.CrashAuthType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AuthCodeReceiverActivity extends BaseActivity implements BusinessDataListener, OnEditorActionListener, BroadcastListener, Callback{
    private EditText edtCode;
    private EditText edtAccount;
    private EditText edtRealName;
    private UserService userService;
    private TextView btnReGet;
    private MyBroadcastReceiver myBroadcastReceiver;
    private AuthType authType;
    private String phone;
    private TextView txtWarn;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_BIND_PAYACCOUNT){
			dismissProgress();
			toast("绑定成功!");
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE);

			if(authType == AuthType.Ali){
				//提现认证
				toCrashAuth();
			}
			finish();
		}else if(msg.what == BusinessDataListener.DONE_BIND_PHONE){
			dismissProgress();
			toast("手机号绑定成功!");
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE);
			if(authType == AuthType.Phone2Ali){
				//支付宝号是否已绑定
				if(TextUtils.isEmpty(UserData.getUserData().payAccount)){
					Intent intentPhone2Ali = new Intent(this, Phone2AliActivity.class);
					startActivity(intentPhone2Ali);
//					Intent intentCode = new Intent(AuthCodeReceiverActivity.this, AuthCodeSendActivity.class);
//					intentCode.putExtra(Constant.AuthCodeType, AuthType.Ali);
//					startActivity(intentCode);
				}else{
					//提现认证
					toCrashAuth();
				}


			}
			finish();
		}else if(msg.what == BusinessDataListener.DONE_UNBIND_PHONE){

			dismissProgress();
			toast("手机号解除绑定成功!");
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE);
			//if(authType == AuthType.Phone2Ali){
				Intent intentCode = new Intent(AuthCodeReceiverActivity.this, AuthCodeSendActivity.class);
				intentCode.putExtra(Constant.AuthCodeType, AuthType.Phone);
				startActivity(intentCode);
			//}
			finish();

		}else if(msg.what == BusinessDataListener.ERROR_BIND_PAYACCOUNT
				|| msg.what == BusinessDataListener.ERROR_BIND_PHONE
				|| msg.what == BusinessDataListener.ERROR_UNBIND_PHONE){
			dismissProgress();
			toast(msg.obj.toString());
		}
		return false;
	};
	private void toCrashAuth() {
		Intent intentToCash = new Intent(this,ToCrashAuthActivity.class);
		//是否有提现密码
		if(Util.isEmptyMd5(UserData.getUserData().toCrashPwd)){
			//intentToCash.putExtra("type", NiePointActionType.Creat);
			intentToCash.putExtra("type", CrashAuthType.ToCrash);
		}else{//认证
			//intentToCash.putExtra("type", NiePointActionType.Auth);
			intentToCash.putExtra("type", CrashAuthType.Auth);
		}
		startActivity(intentToCash);
	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.authcode_step2);
		edtCode = (EditText) findViewById(R.id.edtCode);
		edtRealName = (EditText) findViewById(R.id.edtRealName);
		TextView txtMsg = (TextView) findViewById(R.id.txtMsg);
		edtCode.requestFocus();
		edtCode.requestFocusFromTouch();
		edtCode.setOnEditorActionListener(this);
		edtAccount = (EditText) findViewById(R.id.edtAccount);
		edtAccount.setOnEditorActionListener(this);
		btnReGet = (TextView) findViewById(R.id.btnReGet);
		txtWarn = (TextView) findViewById(R.id.txtWarn);


		authType = (AuthType) getIntent().getExtras().getSerializable(Constant.AuthCodeType);
		switch (authType) {
		case Phone:
		case Phone2Ali:
		case UnBindPhone:
			txtWarn.setVisibility(View.GONE);
			edtRealName.setVisibility(View.GONE);
			edtAccount.setVisibility(View.GONE);
			edtCode.setImeOptions(EditorInfo.IME_ACTION_DONE);
			break;
		case Ali:

			break;

		default:
			break;
		}

		userService = new UserService(this);


		phone = getIntent().getExtras().getString("phone");
		txtMsg.setText("已将验证码发送至您的手机\n" + phone);


		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_SMS_RECEIVED, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
		//开始倒计时
		handler.postDelayed(runnable, 1000);
		btnReGet.setClickable(false);
	}
	private int recLen = 90;
	 Handler handler = new Handler();
	    Runnable runnable = new Runnable() {
	        @Override
	        public void run() {
	        	recLen--;
	            if(recLen > 0){
	            	btnReGet.setText(String.format("%d秒后重新获取", recLen));
	 	            handler.postDelayed(this, 1000);
	            }else if(recLen == 0){
	            	btnReGet.setText("重新获取验证码");
	            	recLen = 90;
	            	btnReGet.setBackgroundResource(R.drawable.btn_enable);
	            	btnReGet.setClickable(true);
	            }

	        }
	    };
	    private void doCancel(){
	    	CustomDialog.showChooiceDialg(this, "提示", "短信验证码已经在路上了，确定要取消吗？", "取消", "再等等", null, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			}, null);
	    }
	    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	if(keyCode == KeyEvent.KEYCODE_BACK){
	    		doCancel();
	    		return true;
	    	}
		return super.onKeyDown(keyCode, event);

	};
	public void onClickButton(View v){
		switch (v.getId()) {
		case R.id.btnReGet:
			handler.postDelayed(runnable, 1000);
			btnReGet.setBackgroundResource(R.drawable.btn_disable);
			btnReGet.setClickable(false);

			break;
		case R.id.btnCancel:
			doCancel();
			break;
		case R.id.btnComplete:
		case R.id.btnCommit:
			bind();

			break;

		default:
			break;
		}

	}
	private void setCode(String code){
		edtCode.setText(code);
	}
	@Override
	protected void onDestroy() {
		dismissProgress();
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
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
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_DONE){
			bind();
			return true;
		}
		return false;
	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.Sms){
			setCode(msg.toString());
			edtAccount.requestFocus();
			//接收到验证码后自动提交
			//bind();
		}else if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}
	}
	public void bindPhone(String code){
		userService.userBindPhone(UserData.getUserData().loginCode, phone, code);
		showProgress();
	}
	public void unBindPhone(String code){
		userService.userUnBindPhone(UserData.getUserData().loginCode, phone, code);
		showProgress();
	}
	public void bindAli(String code){
		edtRealName.setError(null);
		edtAccount.setError(null);

		String account = edtAccount.getText().toString().trim();
		String realName = edtRealName.getText().toString().trim();

		if(TextUtils.isEmpty(realName)){
			//toast("请输入验证码!");
			edtRealName.setError("姓名不能为空!");
			edtRealName.requestFocus();
			edtRealName.requestFocusFromTouch();
			return;
		}
		if(!Util.isChinese(realName)){
			edtRealName.setError("请输入中文!");
			edtRealName.requestFocus();
			edtRealName.requestFocusFromTouch();
			return;
		}
		if(TextUtils.isEmpty(account)){
			edtAccount.setError("支付宝账号不能为空!");
			edtAccount.requestFocus();
			edtAccount.requestFocusFromTouch();
			//toast("请输入支付宝账号!");
			return;
		}
		userService.userBindPayAccount(UserData.getUserData().loginCode, UserData.getUserData().phone, account, realName,  code);
		showProgress();
	}
	public void bind(){
		edtCode.setError(null);
		String code = edtCode.getText().toString().trim();
		if(TextUtils.isEmpty(code)){
			//toast("请输入验证码!");
			edtCode.setError("验证码不能为空!");
			edtCode.requestFocus();
			edtCode.requestFocusFromTouch();
			return;
		}

		switch (authType) {
		case Phone:
		case Phone2Ali:
			bindPhone(code);
			break;
		case Ali:
		case ModifyAli:
			bindAli(code);
			break;
		case UnBindPhone:
			unBindPhone(code);
			break;

		default:
			break;
		}


	}

}
