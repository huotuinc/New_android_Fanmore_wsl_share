package cy.com.morefan;

import cy.com.morefan.bean.TaskData;
import cy.com.morefan.util.Util;
import android.content.Intent;
import android.os.Bundle;

public class TestActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		System.out.println(">>>>>>onCreate");
		operationAlarm();
//		if(goOn)
//			return;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		System.out.println(">>>>>>onPostCreate");
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		System.out.println(">>>>>>onNewIntent");
		operationAlarm();
		super.onNewIntent(intent);


	}
	private void operationAlarm() {
		if(null != getIntent().getExtras()){
			final boolean isLoaded = Util.isActivityLoaded(this);
			 System.out.println(">>>>isRunning:" + isLoaded);
			int id = getIntent().getExtras().getInt("alarmId");
			if(id != 0){
				 Intent intent1 = null;
				 if(isLoaded){
			        	intent1 = new Intent(this, TaskDetailActivity.class);
			     		TaskData taskData = new TaskData();
			     		taskData.id = id;
			     		intent1.putExtra("taskData", taskData);
			         }else{
			        	intent1 = new Intent(this, LoadingActivity.class);
			        	intent1.putExtra("alarmId", id);

			         }
				 startActivity(intent1);
				 finish();

				// return true;
			}
		}
		//return false;
	}
}
