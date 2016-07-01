package cy.com.morefan;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.TempPushMsgData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.util.Util;
import cy.com.morefan.util.WindowProgress.progressOnKeyBack;
import cy.com.morefan.view.CustomDialog;
import android.R.integer;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

public class PushMsgHandleActivity extends BaseActivity implements Callback, progressOnKeyBack{
	private TaskService mTaskService;
	private TempPushMsgData pushMsg;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_CHECK_TASK_STATUS){
			dismissProgress();
			Bundle extra = (Bundle) msg.obj;
			pushMsg.status = extra.getInt("status");
			pushMsg.webUrl = extra.getString("webUrl");
			pushMsg.hasStatus = true;
			handlePushMsg();
		}else if(msg.what == BusinessDataListener.ERROR_CHECK_TASK_STATUS){
			dismissProgress();
			toast(msg.obj.toString());
			CustomDialog.showChooiceDialg(this, null, "请求失败!请重试...",
					"重试", "取消", null, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//mTaskService.checkTaskStatus(pushMsg.taskId);
							showProgress();
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(pushMsg.isRunning){
								finish();
								TempPushMsgData.clear();
							}else{
								startApp();
							}
						}
					});
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);

		boolean goOn = operationAlarm();
		if(goOn)
			return;

		mTaskService = new TaskService(this);
		pushMsg = TempPushMsgData.getIns();
		if(!pushMsg.fromNotify){
			finish();
			return;
		}
		setProgressOnkeyBack(this);
		//showProgress();
		handlePushMsg();
	}
	private boolean operationAlarm() {
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
			     		//intent1.putExtra("fromPre", true);
			     		intent1.putExtra("taskData", taskData);
			         }else{
			        	intent1 = new Intent(this, LoadingActivity.class);
			        	intent1.putExtra("alarmId", id);

			         }
				 startActivity(intent1);
				 finish();

				 return true;
			}
		}
		return false;
	}
	@Override
	protected void onNewIntent(Intent intent) {
		System.out.println(">>>>>>onNewIntent");
		//operationAlarm();
		super.onNewIntent(intent);


	}

	private void handlePushMsg(){
		if(pushMsg.type == 1){//普通推送
			if(pushMsg.isRunning){//直接打开web
				toWeb("");
			}else{//初始化
				startApp();
			}
		}else{//任务推送
			if(pushMsg.hasStatus){
				if(pushMsg.isRunning){
					if(pushMsg.status == 0){//到task detail
						toTaskDetail();
					}else if(pushMsg.status == 1){//任务预告
						toWeb("任务预告");
					}else{
						toast("任务已下架!");
						finish();
					}
				}else{
					startApp();
				}
			}else{//查询任务状态
				//mTaskService.checkTaskStatus(pushMsg.taskId);
				showProgress();
			}


		}
	}
	private void startApp(){
		//启动app
		Intent intent = new Intent(this, LoadingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
	private void toTaskDetail(){
		//to task detail,得刷新任务列表
		Intent intentDetail = new Intent(this, TaskDetailActivity.class);
		intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		TaskData taskData = new TaskData();
		taskData.id = pushMsg.taskId;
		intentDetail.putExtra("fromPre", true);
		intentDetail.putExtra("taskData", taskData);
		intentDetail.putExtra("refreshList", true);
		startActivity(intentDetail);
		finish();
		TempPushMsgData.clear();
	}
	private void toWeb(String title){
		Intent intentWeb = new Intent(this, WebViewActivity.class);
		intentWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intentWeb.putExtra("url", pushMsg.webUrl);
		intentWeb.putExtra("title", title);
		startActivity(intentWeb);
		finish();
		TempPushMsgData.clear();
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type, extra).sendToTarget();
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}
	@Override
	public void onkeyBack() {
		finish();
		TempPushMsgData.clear();
	}

}
