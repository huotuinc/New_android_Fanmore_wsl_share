package cy.com.morefan;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.NetworkUtil;
import android.R.anim;
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

public class FeedBackActivity extends BaseActivity implements Callback, OnEditorActionListener, BroadcastListener{
	private EditText edtName;
	private EditText edtContact;
	private EditText edtContent;
	private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_FEEDBACK){
			dismissProgress();
			toast("反馈成功");
			finish();
			return true;

		}else if(msg.what == BusinessDataListener.ERROR_FEEDBACK){
			dismissProgress();
			toast(msg.obj.toString());
			return true;
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.feedback);
		edtName = (EditText) findViewById(R.id.edtName);
		edtContact = (EditText) findViewById(R.id.edtContact);
		edtContent = (EditText) findViewById(R.id.edtContent);
		edtContent.setOnEditorActionListener(this);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}
	public void onClickBottomTab(View view){
		switch (view.getId()) {
		case R.id.btnCommit:
			commit();
			break;

		default:
			break;
		}

	}
	private void commit() {
		String name = edtName.getText().toString().trim();
		if(TextUtils.isEmpty(name)){
			edtName.requestFocus();
			edtName.setError("请填写联系人");
			return;
		}
		String contact = edtContact.getText().toString().trim();
		if(TextUtils.isEmpty(contact)){
			edtContact.requestFocus();
			edtContact.setError("请填写联系方式");
			return;

		}
		String content = edtContent.getText().toString().trim();
		if(TextUtils.isEmpty(content)){
			edtContent.requestFocus();
			edtContent.setError("请填写反馈内容");

		}
		//追加信息
		content = content + "\n"
				+ "客户端版本:" + Constant.APP_VERSION + "\n"
				+ "手机型号:" + android.os.Build.MODEL + "\n"
				+ "系统:"  + android.os.Build.VERSION.RELEASE + "\n"
				+ "网络:" + NetworkUtil.getNetworkType(this);
		UserService userService = new UserService(this);
		userService.Feedback(UserData.getUserData().loginCode, name, contact, content);
		showProgress();

	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		mHandler.obtainMessage(type).sendToTarget();
		super.onDataFinish(type, des, datas, extra);
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		mHandler.obtainMessage(type, des).sendToTarget();
		super.onDataFailed(type, des, extra);
	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_DONE){
			commit();
		}
		return false;
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
