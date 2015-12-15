package cy.com.morefan;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.SecurityUtil;
import cy.com.morefan.util.Util;
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

public class MyModifyPwdActivity extends BaseActivity implements OnEditorActionListener, Callback, BroadcastListener{
	private EditText edtOldPwd;
	private EditText edtNewPwd;
	private EditText edtNewPwdRe;
	private UserService userService;
	private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_MODIFY_PWD){
			dismissProgress();
			toast("修改成功");
			finish();
		}else if(msg.what == BusinessDataListener.ERROR_MODIFY_PWD){
			dismissProgress();
			toast(msg.obj.toString());
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.user_modify_pwd);
		edtOldPwd = (EditText) findViewById(R.id.edtOld);
		edtNewPwd = (EditText) findViewById(R.id.edtNew);
		edtNewPwdRe = (EditText) findViewById(R.id.edtNewRe);

//		edtOldPwd.setOnEditorActionListener(this);
//		edtNewPwd.setOnEditorActionListener(this);
		edtNewPwdRe.setOnEditorActionListener(this);
		userService = new UserService(this);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_USER_LOGOUT, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}
	public void onClickButton(View v){
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnComplete:
			modify();
			break;
		case R.id.txtForget:
			forgetLogin();
			break;

		default:
			break;
		}

	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_DONE ){
			//getCode();
			modify();
			return true;
		}
		return false;
	}
	private void modify() {
		edtOldPwd.setError(null);
		edtNewPwd.setError(null);
		edtNewPwdRe.setError(null);
		String oldPwd = edtOldPwd.getText().toString();
		String newPwd = edtNewPwd.getText().toString();
		String newPwdRe = edtNewPwdRe.getText().toString();
		if(TextUtils.isEmpty(oldPwd)){
			edtOldPwd.setError("旧密码不能为空!");
			edtOldPwd.requestFocus();
			edtOldPwd.requestFocusFromTouch();
			return;
		}
		if(oldPwd.equals(UserData.getUserData().pwd)){
			edtOldPwd.setError("旧密码不正确!");
			edtOldPwd.requestFocus();
			edtOldPwd.requestFocusFromTouch();
			return;
		}
		if(!Util.userPwdIsLegal(newPwdRe).equals("success")){
			edtNewPwdRe.setError(Util.userPwdIsLegal(newPwdRe));
			edtNewPwdRe.requestFocus();
			edtNewPwdRe.requestFocusFromTouch();
			return;
		}
		if(!newPwd.equals(newPwdRe)){
			edtNewPwdRe.setError("两次输入不同!");
			edtNewPwdRe.requestFocus();
			edtNewPwdRe.requestFocusFromTouch();
			return;
		}
		if(oldPwd.equals(newPwdRe)){
			edtNewPwdRe.setError("不能与原密码相同!");
			edtNewPwdRe.requestFocus();
			edtNewPwdRe.requestFocusFromTouch();
			return;
		}
		userService.userModifyPwd(this, UserData.getUserData().loginCode, SecurityUtil.MD5Encryption(newPwdRe));
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
		// TODO Auto-generated method stub
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
		if(type == ReceiverType.BackgroundBackToUpdate || type == ReceiverType.Logout){
			finish();

		}

	}

}
