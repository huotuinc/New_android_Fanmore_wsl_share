package cy.com.morefan;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.Util;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.huotu.android.library.libedittext.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class AuthCodeSendActivity extends BaseActivity implements OnEditorActionListener, BusinessDataListener, Callback, BroadcastListener{
	private EditText edtPhone;
	private UserService userService;
	private TextView btnNext;
	private TextView txtReminder;
	private MyBroadcastReceiver myBroadcastReceiver;//获取验证码成功后关闭此activity
	private AuthType currentType;
	private TextView txtTitle;
	/**
	 * 手机号绑定获取验证
	 * 支付宝号绑定获取验证
	 * 支付宝绑定前的手机号绑定获取验证
	 * @author edushi
	 *
	 */
	public enum AuthType{
		Phone, Ali, Phone2Ali,UnBindPhone, ModifyAli
	}
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == BusinessDataListener.DONE_GET_CODE) {
			dismissProgress();
			toast("验证码发送,请注意查收短信!");
			goToNext();
		}else if(msg.what == BusinessDataListener.ERROR_GET_CODE){
			dismissProgress();
			toast(msg.obj.toString());
		}
		return false;
	};
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.authcode_step1);
		txtReminder = (TextView) findViewById(R.id.txtReminder);
		edtPhone = (EditText) findViewById(R.id.edtPhone);
		txtTitle = (TextView) findViewById(R.id.txtTitle);

		currentType = (AuthType) getIntent().getExtras().getSerializable(Constant.AuthCodeType);
		switch (currentType) {
		case UnBindPhone:
			txtReminder.setText("修改手机号,您需要输入手机号,获取验证码");
			edtPhone.requestFocus();
			edtPhone.requestFocusFromTouch();
			txtTitle.setText("修改手机号");
//			edtPhone.setText(UserData.getUserData().phone);
//			edtPhone.setTextColor(Color.GRAY);
//			txtReminder.setText("修改手机号,您需要输入手机号,获取验证码");
//			edtPhone.setFocusable(false);
//			txtTitle.setText("修改手机号");
			break;
		case Phone:
			txtReminder.setText("获取验证码，绑定手机号");
			edtPhone.requestFocus();
			edtPhone.requestFocusFromTouch();
			txtTitle.setText("绑定手机号");
			break;
		case ModifyAli:
		case Ali:
			if(TextUtils.isEmpty(UserData.getUserData().payAccount)){
				txtReminder.setText("获取验证码，绑定支付宝账号");
				txtTitle.setText("绑定支付宝帐号");
			}else{
				txtReminder.setText("获取验证码，修改支付宝账号");
				txtTitle.setText("修改支付宝帐号");
			}
			edtPhone.setText(UserData.getUserData().phone);
			//txtReminder.setText("获取验证码，修改支付宝账号");
			edtPhone.setTextColor(Color.GRAY);
			edtPhone.setFocusable(false);
			//txtTitle.setText("修改支付宝帐号");
			break;
//		case Ali:
//			edtPhone.setText(UserData.getUserData().phone);
//			txtReminder.setText("获取验证码，绑定支付宝账号");
//			edtPhone.setTextColor(Color.GRAY);
//			edtPhone.setFocusable(false);
//			txtTitle.setText("绑定支付宝帐号");
//			break;
		case Phone2Ali:
			txtReminder.setText("绑定支付宝账号前,您需要先绑定手机号!");
			edtPhone.requestFocus();
			edtPhone.requestFocusFromTouch();
			txtTitle.setText("绑定手机号");
			break;

		default:
			break;
		}


		//TextView txtYinSi = (TextView) findViewById(R.id.txtYinSi);
		//txtYinSi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
		btnNext = (TextView) findViewById(R.id.btnNext);
		//txtYinSi.setOnClickListener(this);
		//CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox);
//		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if(isChecked){
//					btnNext.setClickable(true);
//					btnNext.setBackgroundResource(R.drawable.title_right_sel);
//				}else{
//					btnNext.setClickable(false);
//					btnNext.setBackgroundResource(R.drawable.btn_disable);
//				}
//
//			}
//		});



		edtPhone.setOnEditorActionListener(this);
		userService = new UserService(this);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

	}
	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_NEXT && btnNext.isClickable()){
			getCode();
			return true;
		}
		return false;
	}
	public void onClickButton(View v){
		super.onClickButton(v);
		switch (v.getId()) {
//		case R.id.txtYinSi:
//			Intent intentAbout = new Intent(this, WebViewActivity.class);
//			intentAbout.putExtra("url", BusinessStatic.getInstance().URL_SERVICE);
//			intentAbout.putExtra("title", "服务条款");
//			startActivity(intentAbout);
//			break;
		case R.id.btnNext:
			getCode();
			break;
		case R.id.btnGet:
			getCode();
			break;
		}

	}
	private void getCode(){
		edtPhone.setError(null);
		String phone = edtPhone.getText().toString().trim();
		if(!Util.isPhoneNum(phone)){
			edtPhone.setError("手机号错误！");
			edtPhone.requestFocus();
			edtPhone.requestFocusFromTouch();
			//toast("手机号错误！");
			return;
		}
		//1：手机号相关;2：支付宝相关
		int type = (currentType == AuthType.Ali || currentType == AuthType.ModifyAli) ? 2 : 1;
		//userService.getAuthCode(UserData.getUserData().loginCode, phone, type);
		showProgress();
	}
	private void goToNext() {
		Intent intentCode = new Intent(this, AuthCodeReceiverActivity.class);
		intentCode.putExtra(Constant.AuthCodeType, currentType);
		intentCode.putExtra("phone", edtPhone.getText().toString().trim());
		startActivity(intentCode);
		//finish();

	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type, des).sendToTarget();

	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.UserMainDataUpdate || type == ReceiverType.BackgroundBackToUpdate){
			finish();
		}

	}
	@Override
	protected void onDestroy() {
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}

}
