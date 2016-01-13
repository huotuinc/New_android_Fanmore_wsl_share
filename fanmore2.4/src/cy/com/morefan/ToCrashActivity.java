package cy.com.morefan;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.L;
import cy.com.morefan.util.Util;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.widget.TextView;

public class ToCrashActivity extends BaseActivity implements  BusinessDataListener, Callback, BroadcastListener{
	private UserService userService;
	private TextView txtMsg;
	private TextView btnGet;
	private TextView txtWarn;
	private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
//		if(msg.what == BusinessDataListener.DONE_TO_CRASH){
//			dismissProgress();
//			//toast("提现申请成功!");
//			//
//			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE);
//			//to share activity
//			Intent intent = new Intent(ToCrashActivity.this, WebViewActivity.class);
//			intent.putExtra("url", UserData.getUserData().shareContent);
//			intent.putExtra("title", "提现申请成功");
//			intent.putExtra("isShare", true);
//			startActivity(intent);
//			finish();
//
//
//
//		}else if(msg.what == BusinessDataListener.ERROR_TO_CRASH){
//			dismissProgress();
//			toast(msg.obj.toString());
//			if(!UserData.getUserData().score.equals("0")){
//				btnGet.setClickable(true);
//				btnGet.setBackgroundResource(R.drawable.btn_enable);
//			}
	//	}
		return false;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.user_crash);
		txtMsg = (TextView) findViewById(R.id.txtMsg);
		btnGet = (TextView) findViewById(R.id.btnGet);
		txtWarn = (TextView) findViewById(R.id.txtWarn);

		txtWarn.setText(String.format("* 为了您能正常提现，请确保%s帐号已通过支付宝实名认证!", UserData.getUserData().payAccount));
		userService = new UserService(this);
		String scoreCount = UserData.getUserData().score;
		double total = Double.parseDouble(scoreCount);
		int money = ((int)total)/10;
		double last = total - money * 10;
		L.i(">>>>>" + scoreCount + "," + (Double.parseDouble(scoreCount)/10.0));
		String msg = String.format("您可提现%d元,消耗%d积分,剩余%s积分。", money, money*10, Util.opeDouble(last));
		txtMsg.setText(msg);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

	}
	public void onClickButton(View v){
		switch (v.getId()) {
		case R.id.btnGet:
			btnGet.setClickable(false);
			btnGet.setBackgroundResource(R.drawable.btn_disable);
			//userService.userToCrash(UserData.getUserData().loginCode, UserData.getUserData().toCrashPwd);
			showProgress();
			break;
		case R.id.btnBack:
			finish();
			break;

		default:
			break;
		}

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
