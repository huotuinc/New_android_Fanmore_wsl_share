package cy.com.morefan;

import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.util.SPUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

public class UserGuideActivity extends BaseActivity implements BroadcastListener{
	private MyBroadcastReceiver myBroadcastReceiver;
	private boolean isCompleteUserInfo;
	private boolean isFromMore;
	private int alarmId;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.user_new_guide);
		isCompleteUserInfo = getIntent().getExtras().getBoolean("isCompleteUserInfo");
		alarmId = getIntent().getExtras().getInt("alarmId");
		isFromMore = getIntent().getExtras().getBoolean("isFromMore");
		SPUtil.saveBooleanToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_NOT_SHOW_USER_GUIDE, true);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}

	@Override
	public void onClickButton(View v) {
		switch (v.getId()) {
		case R.id.btnStart:
			if(!isFromMore){
				Intent intent = new Intent(this, MoblieLoginActivity.class);
				intent.putExtra("isCompleteUserInfo", isCompleteUserInfo);
				intent.putExtra("alarmId", alarmId);
				startActivity(intent);
			}
			finish();

			break;

		default:
			break;
		}
		super.onClickButton(v);
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
