package cy.com.morefan;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.SecurityUtil;
import cy.com.morefan.view.NinePointLineView;
import cy.com.morefan.view.NinePointLineView.NiePointActionType;
import cy.com.morefan.view.NinePointLineView.OnSecretFinishListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.widget.TextView;

public class ToCrashAuthActivity extends BaseActivity implements OnSecretFinishListener, BusinessDataListener, Callback, BroadcastListener{
	private UserService userService;
	private Handler mHandler = new Handler(this);
	private MyBroadcastReceiver myBroadcastReceiver;
	private CrashAuthType currentType;
	public enum CrashAuthType{
		ToCrash, Creat, Auth, Modify
	}

	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_COMMIT_TOCRASHPWD){
			dismissProgress();
			toast("设置成功");
			//
			if(currentType == CrashAuthType.ToCrash)
				toCrash();
			else {
				finish();
			}


		}else if(msg.what == BusinessDataListener.ERROR_COMMIT_TOCRASHPWD){
			dismissProgress();
			toast(msg.obj.toString());
		}
		return false;
	}
	private void toCrash() {
		Intent intent = new Intent(this, UserExchangeActivity.class);
		startActivity(intent);
		finish();

	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.user_tocrash_auth);
		NinePointLineView myView = (NinePointLineView) findViewById(R.id.myView);
		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		myView.setOnSecretFinishListener(this);
		userService = new UserService(this);

		currentType = (CrashAuthType) getIntent().getExtras().getSerializable("type");
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

		switch (currentType) {
		case Creat:
		case ToCrash:
			//设置提现密码
			txtTitle.setText("创建提现密码");
			myView.setActionType(NiePointActionType.Creat);
			break;
		case Auth:
			txtTitle.setText("提现密码");
			myView.setActionType(NiePointActionType.Auth);
			myView.setAuthKey(UserData.getUserData().toCrashPwd);
			break;
		case Modify:
			txtTitle.setText("修改提现密码");
			myView.setActionType(NiePointActionType.Modify);
			myView.setAuthKey(UserData.getUserData().toCrashPwd);
			break;

		default:
			break;
		}
	}

	@Override
	public void OnSecretFinish(NiePointActionType type, String key) {
		if(type == NiePointActionType.Creat){
			//加密上传提现密码
			userService.userCommitToCrashPwd(UserData.getUserData().loginCode, SecurityUtil.MD5Encryption(key), UserData.getUserData().toCrashPwd);
			showProgress();

		}else if(type == NiePointActionType.Auth){
			toCrash();
		}

	}

	@Override
	public void OnSecretAuthFail() {
		finish();

	}
	@Override
	public void onClick() {
		Intent intentForget = new Intent(this, ForgetCashPwdActivity.class);
		startActivity(intentForget);
		finish();
//		//加密上传提现密码
//		userService.userCommitToCrashPwd(UserData.getUserData().loginCode, SecurityUtil.MD5Encryption(""));
//		System.out.println("key:" + SecurityUtil.MD5Encryption(""));
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
