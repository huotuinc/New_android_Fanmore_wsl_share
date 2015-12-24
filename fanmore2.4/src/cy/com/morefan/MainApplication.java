package cy.com.morefan;


import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.TempPushMsgData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.util.L;
import cy.com.morefan.util.PreferenceHelper;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

public class MainApplication extends Application implements BroadcastListener{
	private static ActivityManager activityManager;
	private static MyBroadcastReceiver myBroadcastReceiver;
	public Platform plat;
	public Typeface font;
	 public
	    AssetManager am;

	    @Override
	    public void onCreate() {
	    	super.onCreate();
	    	new CrashHandler();
	    	ShareSDK.initSDK ( getApplicationContext ( ) );
	    	PushAgent.getInstance(this).setNotificationClickHandler(notificationClickHandler);
	    	myBroadcastReceiver = new MyBroadcastReceiver(this.getApplicationContext(), this, MyBroadcastReceiver.ACTION_ALARM_UP);
	    }


	    //private TaskService taskService;
	    /**
		 * 该Handler是在BroadcastReceiver中被调用，故
		 * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
		 * */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
			@Override
			public void launchApp(Context context, UMessage msg) {

				//app是否在运行
				final boolean isRunning = Util.isAppRunning(getApplicationContext());
				L.i(">>isAppRunning:" + isRunning);
				HashMap<String, String> configs = (HashMap<String, String>) msg.extra;
				String mapType = configs.get("type");
				String mapTaskId = configs.get("taskid");
				final String webUrl = configs.get("url");

				TempPushMsgData pushMsg = TempPushMsgData.getIns();
				pushMsg.isRunning  = isRunning;
				pushMsg.fromNotify = true;
				pushMsg.type = TextUtils.isEmpty(mapType) ? 0 : Integer.valueOf(mapType);
				pushMsg.taskId = TextUtils.isEmpty(mapTaskId) ? 0 : Integer.valueOf(mapTaskId);
				pushMsg.webUrl = webUrl;

				Intent intent = new Intent(getApplicationContext(), PushMsgHandleActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);




			//final TempPushMsgData pushMsg = TempPushMsgData.getIns();

//			//是否需要检查任务状态
//			if(pushMsg.type == 1){//普通推送
//				if(isRunning){//直接打开web
//					Intent intentWeb = new Intent(getApplicationContext(), WebViewActivity.class);
//					intentWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intentWeb.putExtra("url", webUrl);
//					startActivity(intentWeb);
//				}else{//初始化
//					Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					startActivity(intent);
//				}
//			}else{//任务推送
//				//查询任务状态
//				taskService = new TaskService(new BusinessDataListener() {
//					@Override
//					public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
//						if(type == BusinessDataListener.DONE_CHECK_TASK_STATUS){
//
////							msg.fromNotify = true;
//							pushMsg.status = extra.getInt("status");
//							pushMsg.webUrl = extra.getString("webUrl");
////							msg.type = 0;
////							msg.taskId = taskId;
//							if(isRunning){
//								if(pushMsg.status == 0){//到task detail
//									//to task detail,得刷新任务列表
//									Intent intentDetail = new Intent(getApplicationContext(), TaskDetailActivity.class);
//									intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//									TaskData taskData = new TaskData();
//									taskData.id = pushMsg.taskId;
//									intentDetail.putExtra("taskData", taskData);
//									intentDetail.putExtra("refreshList", true);
//									getApplicationContext().startActivity(intentDetail);
//								}else if(pushMsg.status == 1){//任务预告
//									Intent intentWeb = new Intent(getApplicationContext(), WebViewActivity.class);
//									intentWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//									intentWeb.putExtra("url", pushMsg.webUrl);
//									intentWeb.putExtra("title", "任务预告");
//									startActivity(intentWeb);
//								}else{
//									Looper.prepare();
//									Toast.makeText(getApplicationContext(), "任务已下架!", Toast.LENGTH_SHORT).show();
//									Looper.loop();
//								}
//							}else{
//								//启动app
//								Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
//								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//								startActivity(intent);
//							}
//
//						}
//
//					}
//
//					@Override
//					public void onDataFailed(int type, String des, Bundle extra) {
//						if(type == BusinessDataListener.ERROR_CHECK_TASK_STATUS){
//							Looper.prepare();
//							Toast.makeText(getApplicationContext(), des, Toast.LENGTH_SHORT).show();
////							CustomDialog.showChooiceDialg(getApplicationContext(), null, "请求失败!请重试...",
////									"重试", "取消", null, new DialogInterface.OnClickListener() {
////										@Override
////										public void onClick(DialogInterface dialog, int which) {
////											taskService.checkTaskStatus(pushMsg.taskId);
////										}
////									}, null);
//							Looper.loop();
//						}
//					}
//				});
//				taskService.checkTaskStatus(pushMsg.taskId);
//
//
//			}

//			if(isRunning){//无需初始化
//				if(type == 1){//直接打开web
//					Intent intentWeb = new Intent(getApplicationContext(), WebViewActivity.class);
//					intentWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intentWeb.putExtra("url", webUrl);
//					startActivity(intentWeb);
//				}else{
//
//				}
//
//			}else{
//				Intent intent = new Intent(getApplicationContext(),LoadingActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.putExtra("isFromNotify", true);
//				intent.putExtra("type", type);//推送ID：  0=任务推送， 1=web推送
//				intent.putExtra("taskId", taskId);//任务ID，如果推送类型为1时，任务ID为0
//				intent.putExtra("webUrl", configs.get("url"));//跳转地址
//				startActivity(intent);
//			}

//
			//	super.launchApp(context, msg);
			//	Toast.makeText(context, msg.extra.toString() + Util.isAppRunning(getApplicationContext()), Toast.LENGTH_LONG).show();
			//	super.launchApp(context, msg);



				//Toast.makeText(context, msg.extra.toString() + , Toast.LENGTH_LONG).show();

			}
			 @Override
			    public void dealWithCustomAction(Context context, UMessage msg) {
			        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
			    }

		};



	    public static ActivityManager getActivityManager(){
	    	if(activityManager == null)
	    		activityManager = new ActivityManager();
	    	return activityManager;
	    }
	    /**
	     * 自定义activity管理堆栈
	     * @author edushi
	     *
	     */
	  static class ActivityManager{
	    	private ArrayList<Activity> activities;
	    	public ArrayList<Activity> getAll(){
	    		return activities;
	    	}
	    	/**
	    	 * 弹出activity
	    	 * @param activity
	    	 */
	    	public void popActivity(Activity activity){
	    		if(activities != null && activities.contains(activity))
	    			activities.remove(activity);
	    	}
	    	public void popAll(List<Activity> mActivities){
	    		if(activities != null)
	    			activities.removeAll(mActivities);
	    	}
	    	/**
	    	 * 加入activity
	    	 * @param activity
	    	 */
	    	public void pushActivity(Activity activity){
	    		if(activities == null)
	    			activities = new ArrayList<Activity>();
	    		if(!activities.contains(activity))
	    			activities.add(activity);


	    	}
	    }

	    class CrashHandler implements UncaughtExceptionHandler{
	    	public CrashHandler(){
	    		Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
	    	}

			@Override
			public void uncaughtException(Thread thread, final Throwable ex) {
				new Thread(){
					public void run() {
						System.err.println("froce close!" + ex.getMessage());
						Looper.prepare();
						finshApp();
						restartApp(getApplicationContext());
						Looper.loop();
				            //MyBroadcastReceiver.sendBroadcast(getApplicationContext(), MyBroadcastReceiver.);
//				            Intent intent = new Intent(MainApplication.this, LoadingActivity.class);
//				            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//				            startActivity(intent);

					};
				}.start();


			}

	    }
	    //获取用户unionId
	    public String readUserUnionId()
	    {
	        return PreferenceHelper.readString ( getApplicationContext (), Constant.SP_NAME_NORMAL, Constant.SP_NAME_UnionId );
	    }

	public String readMemberId()
	{
		return PreferenceHelper.readString (
				getApplicationContext ( ), Constant.SP_NAME_NORMAL,
				Constant.SP_NAME_BuserId
		);
	}
	public void writeMemberId(String userId)
	{
		PreferenceHelper.writeString ( getApplicationContext (), Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId, userId );
	}
	    //获取用户编号
	    public String readUserId()
	    {
	        return PreferenceHelper.readString ( getApplicationContext (), Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId );
	    }

	    //获取商户ID
	    public String readMerchantId()
	    {
			return  BusinessStatic.getInstance().customerId.toString();
//	        return PreferenceHelper.readString ( getApplicationContext ( ), Constant.SP_NAME_NORMAL,
//	                                             Constant.MERCHANT_INFO_ID );
	    }

	    /**
	     * 获取当前应用程序的版本号
	     */
	    public static String getAppVersion(Context context)
	    {
	        String version = "0";
	        try
	        {
	            version = context.getPackageManager().getPackageInfo(
	                    context.getPackageName(), 0).versionName;
	        } catch (PackageManager.NameNotFoundException e)
	        {
	            L.e ( e.getMessage ( ) );
	        }
	        return version;
	    }
	public void writeDomain(String domain)
	{
		PreferenceHelper.writeString ( getApplicationContext (), Constant.MERCHANT_INFO,  Constant.PREFIX, domain);
	}
	    public String obtainMerchantUrl()
	    {
	        return PreferenceHelper.readString ( getApplicationContext (), Constant.MERCHANT_INFO,  Constant.PREFIX );
	    }
	    public static void finshApp(){
	    	ArrayList<Activity> activities = getActivityManager().getAll();
	    	List<Activity> popList = new ArrayList<Activity>();
			for(Activity item : activities){
				if(null != item){
					popList.add(item);
					item.finish();
				}

			}
			getActivityManager().popAll(popList);
	        android.os.Process.killProcess(android.os.Process.myPid());
	    }
	    public static void restartApp(Context activity){
//	    	ArrayList<Activity> activities = getActivityManager().getAll();
//			for(Activity item : activities)
//				item.finish();
//	        android.os.Process.killProcess(android.os.Process.myPid());
//	        if(activities.size() != 0)
//	        	return;
	    	ArrayList<Activity> activities = getActivityManager().getAll();
	    	List<Activity> popList = new ArrayList<Activity>();
			for(Activity item : activities){
				if(null != item){
					popList.add(item);
					item.finish();
				}

			}
			getActivityManager().popAll(popList);

	    	Intent intent = new Intent(activity, LoadingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            //activity.finish();
	    }
	public void writeMemberLevel(String level)
	{
		PreferenceHelper.writeString ( getApplicationContext (), Constant.SP_NAME_NORMAL, Constant.MEMBER_level, level );
	}

	public void writeMemberInfo(String userName, String userId, String userIcon, String userToken, String unionid)
	{
		PreferenceHelper.writeString ( getApplicationContext (), Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId, userId );
		PreferenceHelper.writeString ( getApplicationContext (), Constant.SP_NAME_NORMAL, Constant.MEMBER_NAME, userName );
		PreferenceHelper.writeString(getApplicationContext(), Constant.SP_NAME_NORMAL, Constant.MEMBER_ICON, userIcon);
		PreferenceHelper.writeString(getApplicationContext(), Constant.SP_NAME_NORMAL, Constant.MEMBER_TOKEN, userToken);
		PreferenceHelper.writeString(getApplicationContext(), Constant.SP_NAME_NORMAL, Constant.MEMBER_UNIONID, unionid);
	}

	@Override
		public void onFinishReceiver(ReceiverType type, Object msg) {
			if(type == ReceiverType.AlarmUp){
				if(null != msg){
					Bundle extra = (Bundle) msg;
					int id = extra.getInt("id");
					String ids = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_ALARM);
					//save to sp
					String[] idsA = ids.split(",");
					if(null != idsA){
						StringBuilder sb = new StringBuilder();
						for(int i = 0, length = idsA.length; i < length; i++){
							if(!idsA[i].equals(id+"")){
								sb.append("," + idsA[i]);
							}
						}
						SPUtil.saveStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_ALARM, sb.toString());
						L.i(">>>>>application onFinishReceiver:" + sb.toString());
					}
				}
			}


		}
}
