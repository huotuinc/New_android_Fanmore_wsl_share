package cy.com.morefan;

import cy.com.morefan.AuthCodeSendActivity.AuthType;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Phone2AliActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.phone2ali);
		TextView txtReminder = (TextView) findViewById(R.id.txtReminder);
		String msg = String.format("已成功绑定您的手机%s。\n绑定支付宝号,体验积分兑现,赶快行动吧!", UserData.getUserData().phone);
		txtReminder.setText(msg);
	}
	public void onClickBottomTab(View v){
		switch (v.getId()) {
		case R.id.btnCommit:
			Intent intentCode = new Intent(this, AuthCodeSendActivity.class);
			intentCode.putExtra(Constant.AuthCodeType, AuthType.Ali);
			startActivity(intentCode);
			finish();
			break;

		default:
			break;
		}
		
	}
}
