package cy.com.morefan;


import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.util.CrashHandler;
import cy.com.morefan.util.L;
import cy.com.morefan.util.PreferenceHelper;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.VolleyUtil;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orm.SugarContext;

public class MainApplication extends Application implements BroadcastListener{
	private static ActivityManager activityManager;
	private static MyBroadcastReceiver myBroadcastReceiver;
	private static final String TAG = "JPush";
	public Platform plat;
	public Typeface font;
	 public
	    AssetManager am;

	    @Override
	    public void onCreate() {
	    	super.onCreate();
			Fresco.initialize(getApplicationContext());
			ShareSDK.initSDK(getApplicationContext());
			VolleyUtil.init(getApplicationContext());
			CrashHandler crashHandler = CrashHandler.getInstance ();
			crashHandler.init(getApplicationContext());
			JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
			JPushInterface.init(this);
			myBroadcastReceiver = new MyBroadcastReceiver(this.getApplicationContext(), this, MyBroadcastReceiver.ACTION_ALARM_UP);
			SugarContext.init(getApplicationContext());
	    }


	@Override
	public void onTerminate() {
		super.onTerminate();
		SugarContext.terminate();
	}


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


	public String readAlipayAppKey()
	{
		return PreferenceHelper.readString (
				getApplicationContext ( ), Constant.MERCHANT_INFO,
				Constant.ALIPAY_KEY
		);
	}
	public void writeWx(String parentId, String appId, String appKey, String notify, boolean isWebPay)
	{
		PreferenceHelper.writeString ( getApplicationContext (), Constant.MERCHANT_INFO, Constant.WEIXIN_MERCHANT_ID,  parentId);
		PreferenceHelper.writeString ( getApplicationContext (), Constant.MERCHANT_INFO, Constant.MERCHANT_WEIXIN_ID, appId );
		PreferenceHelper.writeString (
				getApplicationContext ( ), Constant.MERCHANT_INFO,
				Constant.WEIXIN_KEY, appKey
		);
		PreferenceHelper.writeString (
				getApplicationContext ( ), Constant.MERCHANT_INFO,
				Constant.WEIXIN_NOTIFY, notify
		);
		PreferenceHelper.writeBoolean (
				getApplicationContext ( ), Constant.MERCHANT_INFO,
				Constant.IS_WEB_WEIXINPAY, isWebPay
		);
	}
	public void writeUserIcon(String userIcon)
	{
		PreferenceHelper.writeString ( getApplicationContext (), Constant.MEMBER_INFO, Constant.MEMBER_ICON, userIcon );
	}
	public void writeUserName(String userName)
	{
		PreferenceHelper.writeString ( getApplicationContext (), Constant.MEMBER_INFO, Constant.MEMBER_NAME, userName );
	}
	public String readAlipayParentId()
	{
		return PreferenceHelper.readString ( getApplicationContext (), Constant.MERCHANT_INFO, Constant.ALIPAY_MERCHANT_ID );
	}

	public String readWxpayAppKey()
	{
		return PreferenceHelper.readString ( getApplicationContext ( ), Constant.MERCHANT_INFO,
				Constant.WEIXIN_KEY );
	}
	public String getUserName()
	{
		return PreferenceHelper.readString ( getApplicationContext (), Constant.MEMBER_INFO, Constant.MEMBER_NAME );
	}
	public String readWxpayParentId()
	{
		return PreferenceHelper.readString ( getApplicationContext ( ), Constant.MERCHANT_INFO, Constant.WEIXIN_MERCHANT_ID );
	}
	public String readWxpayAppId()
	{
		return PreferenceHelper.readString ( getApplicationContext (), Constant.MERCHANT_INFO, Constant.MERCHANT_WEIXIN_ID );
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
	public String obtainMainColor()
	{
		return PreferenceHelper.readString ( getApplicationContext (), Constant.COLOR_INFO, Constant.COLOR_MAIN );
	}
	public void writeMemberId(String userId)
	{
		PreferenceHelper.writeString ( getApplicationContext (), Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId, userId );
	}
	public String readWeixinNotify()
	{
		return PreferenceHelper.readString ( getApplicationContext (), Constant.MERCHANT_INFO, Constant.WEIXIN_NOTIFY );
	}
	    //获取用户编号
	    public String readUserId()
	    {
	        return PreferenceHelper.readString ( getApplicationContext (), Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId );
	    }
	public boolean scanWx()
	{
		String parentId = PreferenceHelper.readString ( getApplicationContext (), Constant.MERCHANT_INFO, Constant.WEIXIN_MERCHANT_ID);
		String appid =  PreferenceHelper.readString ( getApplicationContext ( ), Constant
				.MERCHANT_INFO, Constant
				.MERCHANT_WEIXIN_ID );
		String appKey = PreferenceHelper.readString (
				getApplicationContext ( ), Constant.MERCHANT_INFO,
				Constant.WEIXIN_KEY
		);
		String notify = PreferenceHelper.readString (
				getApplicationContext ( ), Constant.MERCHANT_INFO,
				Constant.WEIXIN_NOTIFY );

		if(!TextUtils.isEmpty ( parentId ) && !TextUtils.isEmpty ( appid ) && !TextUtils.isEmpty ( appKey ) && !TextUtils.isEmpty ( notify ))
		{
			return true;
		}
		else
		{
			return false;
		}
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
	public void logout()
	{
		//取消授权
		if(null != plat)
		{
			plat.removeAccount ();
		}
		PreferenceHelper.clean(getApplicationContext(), Constant.MEMBER_INFO);
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
