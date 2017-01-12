package cy.com.morefan.listener;

import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.util.Util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;

public class MyBroadcastReceiver extends BroadcastReceiver{
	public static String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED" ;
	public static String ACTION_BACKGROUD_BACK_TO_UPDATE = "cy.com.morefan.BACKGROUD_BACK_TO_UPDATE" ;//后台返回
	public static String ACTION_USER_MAINDATA_UPDATE = "cy.com.morefan.MAINDATA_UPDATE" ;
	public static String ACTION_USER_LOGIN = "cy.com.morefan.LOGIN" ;
	public static String ACTION_USER_LOGOUT = "cy.com.morefan.LOGOUT";
	public static String ACTION_SHARE_TO_WEIXIN_SUCCESS = "cy.com.morefan.SHARE_TO_WEIXIN_SUCCESS" ;
	public static String ACTION_SHARE_TO_SINA_SUCCESS = "cy.com.morefan.SHARE_TO_SINA_SUCCESS" ;
	public static String ACTION_SHARE_TO_QZONE_SUCCESS = "cy.com.morefan.SHARE_TO_QZONE_SUCCESS" ;
	public static String ACTION_PAY_SUCCESS = "com.huotu.partner.ACTION_PAY_SUCCESS";
	public static String ACTION_REFRESH_TASK_LIST = "cy.com.morefan.REFRESH_TASK_LIST" ;
	public static String ACTION_ALARM_UP = "cy.com.morefan.ACTION_ALARM_UP" ;
	//分享成功后微信没有回调
	public static String ACTION_WX_NOT_BACK = "cy.com.morefan.ACTION_WX_NOT_BACK" ;
	public static String ACTION_REFRESH_USEDATA="cy.com.morefan.ACTION_REFRESH_USEDATA";
	public enum ReceiverType{
		WXNotBack,AlarmUp, RefreshTaskList,UserMainDataUpdate, Sms, Login, Logout, ShareToWeixinSuccess, ShareToSinaSuccess,
		ShareToQzoneSuccess, BackgroundBackToUpdate,wxPaySuccess,refreshusedata
	}
	public interface BroadcastListener{
		void onFinishReceiver(ReceiverType type, Object msg);
	}
	private Context mContext;
	private BroadcastListener listener;
	/**
	 * 发送广播
	 * @param context
	 * @param action
	 */
	public static void sendBroadcast(Context context, String action){
		if(context == null)
			return;
		Intent intent = new Intent(action);
		context.sendBroadcast(intent);
	}
	/**
	 * 发送广播
	 * @param context
	 * @param action
	 */
	public static void sendBroadcast(Context context, String action, Bundle extra){
		if(context == null)
			return;
		Intent intent = new Intent(action);
		intent.putExtras(extra);
		context.sendBroadcast(intent);
	}
	/**
	 * 注册广播
	 * @param context
	 * @param listener
	 */
	public MyBroadcastReceiver(Context context, BroadcastListener listener, String... actions){
		this.listener = listener;
		this.mContext = context;
		//注册广播
		IntentFilter intentFilter = new IntentFilter();
		for(int i = 0, length = actions.length; i < length; i++)
			intentFilter.addAction(actions[i]);
		context.registerReceiver(this,intentFilter);
	}
	public void unregisterReceiver(){
		mContext.unregisterReceiver(this);
		mContext = null;
	}


	@Override
	public void onReceive(Context context, Intent intent) {
		if (listener == null)
			return;
		if (intent.getAction().equals(ACTION_SMS_RECEIVED) && !TextUtils.isEmpty(BusinessStatic.getInstance().SMS_TAG)) {
			Bundle bundle = intent.getExtras();
			if(null == bundle)
				return;
			Object messages[] = (Object[]) bundle.get("pdus");
			if(messages == null)
				return;
			SmsMessage smsMessage[] = new SmsMessage[messages.length];
			for (int n = 0; n < messages.length; n++) {
				smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
				String body = smsMessage[n].getMessageBody();
				if (body.contains(BusinessStatic.getInstance().SMS_TAG)) {
					String code = body.substring(0,
							body.indexOf(BusinessStatic.getInstance().SMS_TAG));
					listener.onFinishReceiver(ReceiverType.Sms, Util.getNumber(code));
				}
			}
		}else if(intent.getAction().equals(ACTION_WX_NOT_BACK)){
			listener.onFinishReceiver(ReceiverType.WXNotBack, intent.getExtras());
		}else if (intent.getAction().equals(ACTION_REFRESH_USEDATA)){
			listener.onFinishReceiver(ReceiverType.refreshusedata, intent.getExtras());
		}
		else if(intent.getAction().equals(ACTION_ALARM_UP)){
			listener.onFinishReceiver(ReceiverType.AlarmUp, intent.getExtras());
		} else if(intent.getAction().equals(ACTION_REFRESH_TASK_LIST)){
			listener.onFinishReceiver(ReceiverType.RefreshTaskList, null);
		}else if (intent.getAction().equals(ACTION_USER_MAINDATA_UPDATE)) {
			listener.onFinishReceiver(ReceiverType.UserMainDataUpdate, null);
		} else if (intent.getAction().equals(ACTION_USER_LOGIN)) {
			listener.onFinishReceiver(ReceiverType.Login, null);
		} else if (intent.getAction().equals(ACTION_USER_LOGOUT)) {
			listener.onFinishReceiver(ReceiverType.Logout, null);
		} else if (intent.getAction().equals(ACTION_SHARE_TO_WEIXIN_SUCCESS)) {
			listener.onFinishReceiver(ReceiverType.ShareToWeixinSuccess, null);
		} else if (intent.getAction().equals(ACTION_SHARE_TO_SINA_SUCCESS)) {
			listener.onFinishReceiver(ReceiverType.ShareToSinaSuccess, null);
		}else if (intent.getAction().equals(ACTION_SHARE_TO_QZONE_SUCCESS)) {
			listener.onFinishReceiver(ReceiverType.ShareToQzoneSuccess, null);
		}else if( intent.getAction().equals(ACTION_BACKGROUD_BACK_TO_UPDATE)){
			listener.onFinishReceiver(ReceiverType.BackgroundBackToUpdate, null);
		}else if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
			 int status = intent.getIntExtra("status", -1);
			 int health = intent.getIntExtra("health", -1);
			 BusinessStatic.getInstance().ISEMULATOR = BatteryManager.BATTERY_HEALTH_OVERHEAT != health && status == BatteryManager.BATTERY_STATUS_UNKNOWN;
			 System.err.println(">>>isEmulatro:" +  BusinessStatic.getInstance().ISEMULATOR + "," + status + "," + health);

		}








	}
}
