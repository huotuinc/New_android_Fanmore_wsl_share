package cy.com.morefan;

import cy.com.morefan.AuthCodeSendActivity.AuthType;
import cy.com.morefan.ToCrashAuthActivity.CrashAuthType;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.util.Util;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MySafeActivity extends BaseActivity implements BroadcastListener{
	private MyBroadcastReceiver myBroadcastReceiver;
	private TextView txtAli;
	private TextView txtPhone;
	private TextView txtCashPwd;
	private TextView txtUserName;
	private RelativeLayout layCrashPwd;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.user_safe);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_USER_LOGOUT, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
		initView();
	}

	@Override
	protected void onResume() {
		refreshView();
		super.onResume();
	}
	private void initView() {

		layCrashPwd = (RelativeLayout) findViewById(R.id.layCrashPwd);
		txtAli = (TextView) findViewById(R.id.txtAli);
		txtPhone = (TextView) findViewById(R.id.txtPhone);
		txtCashPwd = (TextView) findViewById(R.id.txtCashPwd);
		txtUserName = (TextView) findViewById(R.id.txtUserName);
		String userName = UserData.getUserData().RealName;
		if (TextUtils.isEmpty(userName))
			userName = UserData.getUserData().UserNickName;
		else if (TextUtils.isEmpty(userName)){
			userName =UserData.getUserData().userName;
		}
		txtUserName.setText(userName + ":");

	}
	private void refreshView() {
		layCrashPwd.setVisibility(BusinessStatic.getInstance().CRASH_TYPE == 1 ? View.GONE : View.VISIBLE);
		UserData userData = UserData.getUserData();
		String textAli = TextUtils.isEmpty(userData.payAccount) ? "绑定支付宝" : "修改支付宝绑定";
		txtAli.setText(textAli);
		String textPhone = TextUtils.isEmpty(userData.phone) ? "绑定手机" : "修改手机绑定";
		txtPhone.setText(textPhone);
		String textCashPwd = Util.isEmptyMd5(userData.toCrashPwd) ? "设置提现密码" : "修改提现密码";
		txtCashPwd.setText(textCashPwd);

	}
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.layAli:
			if(TextUtils.isEmpty(UserData.getUserData().phone)){
				toast("请先绑定手机号!");
				return;

			}
			Intent intentAli = new Intent(this, AuthCodeSendActivity.class);
			intentAli.putExtra(Constant.AuthCodeType, AuthType.ModifyAli);
			startActivity(intentAli);
			break;
		case R.id.layPhone:
			Intent intentPhone = new Intent(this, AuthCodeSendActivity.class);
			if(TextUtils.isEmpty(UserData.getUserData().phone)){//未绑定
				intentPhone.putExtra(Constant.AuthCodeType, AuthType.Phone);
			}else{//已绑定，先解除绑定
				intentPhone.putExtra(Constant.AuthCodeType, AuthType.UnBindPhone);
			}
			startActivity(intentPhone);

			break;
		case R.id.layLoginPwd:
			Intent intentLoginPwd = new Intent(this, MyModifyPwdActivity.class);
			startActivity(intentLoginPwd);
			break;
		case R.id.layCrashPwd:
			Intent intentCrashPwd = new Intent(this, ToCrashAuthActivity.class);
			if(Util.isEmptyMd5(UserData.getUserData().toCrashPwd)){//创建
				//intentCrashPwd.putExtra("type", NiePointActionType.Creat);
				intentCrashPwd.putExtra("type", CrashAuthType.Creat);
			}else{//修改
				//intentCrashPwd.putExtra("type", NiePointActionType.Modify);
				intentCrashPwd.putExtra("type", CrashAuthType.Modify);
			}
			startActivity(intentCrashPwd);
			break;
		default:
			break;
		}

	}
	@Override
	protected void onDestroy() {
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.Logout || type == ReceiverType.BackgroundBackToUpdate){
			finish();
		}

	}

}
