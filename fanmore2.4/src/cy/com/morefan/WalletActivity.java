package cy.com.morefan;

import java.util.Locale;

import cy.com.morefan.DataListActivity.ActivityType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.L;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore.RightsStatus;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WalletActivity extends BaseActivity implements Callback{
	private UserService mUserService;
	private TextView txtLastScore;
	private TextView txtLastWallet;
	private TextView txtDes;
	private LinearLayout layRecord;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		dismissProgress();
		switch (msg.what) {
		case BusinessDataListener.ERROR_USER_LOGIN:
		case BusinessDataListener.ERROR_GET_DUIBA_URL:
		case BusinessDataListener.ERROR_GET_WALLET:
		case BusinessDataListener.ERROR_TO_RECHANGE:
			toast(msg.obj.toString());

			break;
		case BusinessDataListener.DONE_USER_LOGIN:
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_LOGIN);
			refresh();
			break;
		case BusinessDataListener.DONE_TO_RECHANGE:
			toast(msg.obj.toString());
			refresh();
			break;
		case BusinessDataListener.DONE_GET_WALLET:
			refreshView((Bundle)msg.obj);

			break;
		case BusinessDataListener.DONE_GET_DUIBA_URL:
			String loginUrl = ((Bundle)msg.obj).getString("loginurl");
			Intent intent = new Intent();
			intent.setClass(WalletActivity.this, CreditActivity.class);
	        intent.putExtra("navColor", "#0acbc1");    //配置导航条的背景颜色，请用#ffffff长格式。
	        intent.putExtra("titleColor", "#ffffff");    //配置导航条标题的颜色，请用#ffffff长格式。
	        intent.putExtra("url", loginUrl);    //配置自动登陆地址，每次需动态生成。
			startActivity(intent);

//			Intent intentDuiba = new Intent(this, CreditActivity.class);
//			intentDuiba.putExtra("url", loginUrl);
//			intentDuiba.putExtra("title", "兑换商城");
//			startActivity(intentDuiba);
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.wallet);
		mUserService = new UserService(this);

		txtLastScore = (TextView) findViewById(R.id.txtLastScore);
		txtLastWallet = (TextView) findViewById(R.id.txtLastWallet);
		txtDes = (TextView) findViewById(R.id.txtDes);
		layRecord = (LinearLayout) findViewById(R.id.layRecord);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refresh();
	}

	private void refresh() {
		//mUserService.getWallet(UserData.getUserData().loginCode);
		showProgress();

	}
	private void refreshView(Bundle obj) {

		UserData userData = UserData.getUserData();
		if(!userData.isLogin){
			String msg = "0";
			txtLastScore.setText(msg);
			txtLastWallet.setText(msg);
		}else{
			txtLastScore.setText(userData.score);
			txtLastWallet.setText(userData.wallet);
		}
		if(null == obj)
			return;
		txtDes.setText(obj.getString("des"));

		String record = obj.getString("recordDes");
		layRecord.setVisibility(TextUtils.isEmpty(record) || record.equals("null")? View.GONE : View.VISIBLE);
		if(!TextUtils.isEmpty(record) || record.equals("null")){
			TextView txtTime = (TextView) findViewById(R.id.txtTime);
			TextView txtRecordDes = (TextView) findViewById(R.id.txtRecordDes);
			TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
			txtTime.setText(TimeUtil.FormatterTimeToMinute(obj.getString("recordTime")));
			txtRecordDes.setText(obj.getString("recordDes"));
			txtStatus.setText(String.format("(%s)", obj.getString("recordResultDes")));
		}

	}
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.layRule:
			Intent intentRule = new Intent(this, WebViewActivity.class);
			intentRule.putExtra("url", BusinessStatic.getInstance().URL_RULE + "#tixianmimi");
			intentRule.putExtra("title", "规则说明");
			startActivity(intentRule);
			break;
		case R.id.btnHistory:
			if(!UserData.getUserData().isLogin){
				Intent intentlogin = new Intent(WalletActivity.this, LoginActivity.class);
				startActivity(intentlogin);
				return ;
			}
			Intent intentHistory = new Intent(this, DataListActivity.class);
			intentHistory.putExtra(DataListActivity.ACTVITY_TYPE, ActivityType.WalletHistory);
			startActivity(intentHistory);
			break;
		case R.id.btnWallet:
			if(!checkStatus())
				return;

			String scoreCount = UserData.getUserData().score;
			double total = Double.parseDouble(scoreCount);
			int money = ((int)total)/10;
			double last = total - money * 10;
			L.i(">>>>>" + scoreCount + "," + (Double.parseDouble(scoreCount)/10.0));
			String msg = String.format("您可转入钱包%d元,消耗%d积分,剩余%s积分。转入操作将在1－3个工作日内完成!确定转入吗?", money, money*10, Util.opeDouble(last));

			CustomDialog.showChooiceDialg(this, "转入钱包", msg, "确定", "取消", null,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//mUserService.cashToWallet(UserData.getUserData().loginCode);
							showProgress();

						}
					}, null);

			break;
		case R.id.btnDuiBa:
			if(!UserData.getUserData().isLogin){
				Intent intentlogin = new Intent(WalletActivity.this, LoginActivity.class);
				startActivity(intentlogin);
				return;
			}
			mUserService.getDuiBaUrl(UserData.getUserData().loginCode);
			showProgress();
			break;

		default:
			break;
		}

	}
	public boolean checkStatus(){
		if(!UserData.getUserData().isLogin){
			Intent intentlogin = new Intent(WalletActivity.this, LoginActivity.class);
			startActivity(intentlogin);
			return false;
		}
		//积分是否已达标准下限
		if(Double.parseDouble(UserData.getUserData().score) < BusinessStatic.getInstance().CHANGE_BOUNDARY){
			toast(String.format("需达到%d积分才能转入钱包，赶快去赚积分吧!",BusinessStatic.getInstance().CHANGE_BOUNDARY));
			return false;
		}
		return true;
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		if(type == BusinessDataListener.DONE_GET_DUIBA_URL
		|| type == BusinessDataListener.DONE_GET_WALLET){
			mHandler.obtainMessage(type, extra).sendToTarget();
		}else if(type == BusinessDataListener.DONE_TO_RECHANGE){
			mHandler.obtainMessage(type, des).sendToTarget();
		}else{
			mHandler.obtainMessage(type).sendToTarget();
		}
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}

}
