package cy.com.morefan;

import cy.com.morefan.ToCrashAuthActivity.CrashAuthType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.SecurityUtil;
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
	private EditText edtPwd;
	private MyBroadcastReceiver myBroadcastReceiver;

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
			edtPwd = (EditText) findViewById(R.id.edtPwd);
			edtPwd.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if(actionId == EditorInfo.IME_ACTION_DONE){
						commit();
					}
					return false;
				}
			});
		}
		userService = new UserService(this);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}
	public void onClickBottomTab(View v){
		switch (v.getId()) {
		case R.id.btnCommit:
			commit();
			break;

		default:
			break;
		}

	}
	public void commit(){
		String pwd = edtPwd.getText().toString().trim();
		if(TextUtils.isEmpty(pwd)){
			edtPwd.setError("密码不能为空");
			edtPwd.requestFocus();
			edtPwd.requestFocusFromTouch();
			return;
		}
		String locPwd = UserData.getUserData().pwd;
		if(!locPwd.equals(SecurityUtil.MD5Encryption(pwd))){
			edtPwd.setError("登录密码错误!");
			edtPwd.requestFocus();
			edtPwd.requestFocusFromTouch();
			return;
		}
		//加密上传提现密码
		userService.userCommitToCrashPwd(UserData.getUserData().loginCode, SecurityUtil.MD5Encryption(""), UserData.getUserData().toCrashPwd);
		showProgress();
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
