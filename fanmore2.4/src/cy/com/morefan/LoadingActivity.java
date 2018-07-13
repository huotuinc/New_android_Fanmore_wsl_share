package cy.com.morefan;

import java.io.File;
import java.io.Serializable;
import java.util.List;


import cindy.android.test.synclistview.SyncImageLoaderHelper;


import com.facebook.drawee.view.SimpleDraweeView;
import com.lib.cylibimagedownload.ImageUtil;
import com.lib.cyliblocation.LibLocation;
import com.yhao.floatwindow.PermissionUtil;
import com.yhao.floatwindow.TipAlertDialog;

import cn.jpush.android.api.JPushInterface;
import cy.com.morefan.AppUpdateActivity.UpdateType;
import cy.com.morefan.bean.AdlistModel;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.guide.GuideActivity;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.IMEIUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.CustomDialog.OnkeyBackListener;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.AndroidCharacter;
import android.text.TextUtils;
import android.view.View;

public class LoadingActivity extends BaseActivity implements
		BusinessDataListener, OnkeyBackListener, Callback, BroadcastListener{

	private Handler mHandler = new Handler(this);
	private boolean isFinish;
	private boolean isCompleteUserInfo;
	private long time;
	private int alarmId;
	List<AdlistModel> datas;
	int REQUEST_STORAGE_PERMISSION=11;
	int REQUEST_STORAGE_PERMISSION2 = 12;
    int REQUEST_STORAGE_PERMISSION3 = 13;
    //int REQUEST_SYSTEM_WINDOW = 14;


	@Override
	public boolean handleMessage(Message msg) {

		if (msg.what == BusinessDataListener.DONE_INIT) {

			Bundle bundle = (Bundle) msg.obj;
			int updateType = bundle.getInt("updateType");
			String updateMd5 = bundle.getString("updateMd5");
			String updateUrl = bundle.getString("updateUrl");
			String updateTips = bundle.getString("updateTips");
			datas=(List<AdlistModel>) bundle.getSerializable("adlist");
			// 是否完善个人信息
			isCompleteUserInfo = bundle.getInt("isCompleteUserInfo") == 1;
			switch (updateType) {
			case 0:// 无更新
				//toHome();
				checkSystemWindow();
				break;
			case 1:// 1.增量更新
				choiceToUpdate(UpdateType.DiffUpdate, updateMd5, updateUrl,
						updateTips);
				break;
			case 2:// 2.整包更新
				choiceToUpdate(UpdateType.FullUpate, updateMd5, updateUrl,
						updateTips);
				break;
			case 3:// 3.强制增量更新
				toUpdate(UpdateType.DiffUpdate, updateMd5, updateUrl,
						updateTips, true);
				break;
			case 4:// 4.强制整包更新
				toUpdate(UpdateType.FullUpate, updateMd5, updateUrl,
						updateTips, true);
				break;

			default:
				break;
			}

		} else if (msg.what == BusinessDataListener.ERROR_INIT) {
			isFinish = true;
			toast(msg.obj.toString());
			CustomDialog.setOnKeyBackListener(LoadingActivity.this);
			CustomDialog.showChooiceDialg(LoadingActivity.this, "提示",
					"初始化失败，是否重试?", "重试", "退出", null,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//initData();
							StorageRequest();
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});

		}

		return false;
	}


	private void choiceToUpdate(final UpdateType type, final String md5, final String url, final String tips) {
		CustomDialog.setOnKeyBackListener(LoadingActivity.this);
		CustomDialog.showChooiceDialg(LoadingActivity.this, "温馨提示",
				"发现新版本，马上更新?\n" + tips, "马上更新", "跳过该版本", null,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						toUpdate(type, md5, url, tips, false);
					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						checkSystemWindow();
						//toHome();
					}
				});

	}

	private void toUpdate(UpdateType type, String md5, String url, String tips, boolean isForce) {
		Intent intent = new Intent(LoadingActivity.this,
				AppUpdateActivity.class);
		intent.putExtra("isForce", isForce);
		intent.putExtra("type", type);
		intent.putExtra("md5", md5);
		intent.putExtra("url", url);
		intent.putExtra("tips", tips);
		startActivityForResult(intent, Constant.REQUEST_CODE_CLIENT_DOWNLOAD);
		// finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		if (requestCode == Constant.REQUEST_CODE_CLIENT_DOWNLOAD
				&& resultCode == Constant.RESULT_CODE_CLIENT_DOWNLOAD_FAILED) {
			Bundle extra = arg2.getExtras();
			if (extra != null) {
				boolean isForce = extra.getBoolean("isForce");
				if (isForce) {
					finish();
				} else {
					//toHome();
					checkSystemWindow();
				}

			}

		}
		super.onActivityResult(requestCode, resultCode, arg2);
	}

	private void toHome() {




		if (!SPUtil.getBooleanFromSpByName(this, Constant.SP_NAME_NORMAL,
				Constant.SP_NAME_NOT_SHOW_USER_GUIDE, false)) {
			Intent intentGuide = new Intent(LoadingActivity.this, GuideActivity.class);
			intentGuide.putExtra("isCompleteUserInfo", isCompleteUserInfo);
			intentGuide.putExtra("alarmId", alarmId);
			startActivity(intentGuide);
			finish();
		}
//		else if (
//				!TextUtils.isEmpty( SPUtil.getStringToSpByName(LoadingActivity.this,Constant.SP_NAME_NORMAL,Constant.SP_NAME_USERNAME)) &&
//				!TextUtils.isEmpty( SPUtil.getStringToSpByName(LoadingActivity.this,Constant.SP_NAME_NORMAL,Constant.SP_NAME_USERPWD) ) ) {
//			Intent intent = new Intent(LoadingActivity.this, HomeActivity.class);
//			intent.putExtra("isCompleteUserInfo", isCompleteUserInfo);
//			intent.putExtra("alarmId", alarmId);
//			//UserData.restore();
//			startActivity(intent);
//			finish();
//		}
 		else {
			Intent intent = new Intent(LoadingActivity.this, AdActivity.class);
			intent.putExtra("isCompleteUserInfo", isCompleteUserInfo);
			intent.putExtra("alarmId", alarmId);
			intent.putExtra("data", (Serializable) datas);
			startActivity(intent);
			finish();
		}


	}

	private TaskService taskService;
	private SyncImageLoaderHelper loader;
	private MyBroadcastReceiver broadcastReceiver;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.loading);

		SimpleDraweeView d = (SimpleDraweeView) findViewById(R.id.loading_icon);
		d.setImageURI("res://"+getPackageName(this)+"/"+ R.drawable.icon);

//		if(null != getIntent().getExtras())
//			alarmId = getIntent().getExtras().getInt("alarmId");
		broadcastReceiver = new MyBroadcastReceiver(this, this,  Intent.ACTION_BATTERY_CHANGED);
		//PhoneRequest();

		time = System.currentTimeMillis();

		//checkSystemWindow();
        SDRequest();
	}

	/**
	 *
	 */
	void checkSystemWindow(){
		if (PermissionUtil.hasPermission(this)) {
			toHome();
			return;
		}

		final TipAlertDialog dialog = new TipAlertDialog(this);

		dialog.show("申请权限","请允许App使用\"显示悬浮窗\"权限！","", false ,true ,null, new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				toHome();
			}
		});

	}


	public static String getDeviceInfo(Context context) {
		try{
		  org.json.JSONObject json = new org.json.JSONObject();
		  android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
			  .getSystemService(Context.TELEPHONY_SERVICE);

		  String device_id = tm.getDeviceId();

		  android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		  String mac = wifi.getConnectionInfo().getMacAddress();
		  json.put("mac", mac);

		  if( TextUtils.isEmpty(device_id) ){
			device_id = mac;
		  }

		  if( TextUtils.isEmpty(device_id) ){
			device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
		  }

		  json.put("device_id", device_id);

		  return json.toString();
		}catch(Exception e){
		  e.printStackTrace();
		}
	  return null;
	}


	void SDRequest(){

		int checkSelfPremission = ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(  checkSelfPremission != PackageManager.PERMISSION_GRANTED ){
			if(ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.WRITE_EXTERNAL_STORAGE)){
				ToastUtil.show(getApplication(),"请授权应用使用存储权限，否则将无法使用应用。");
			}
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION3);
		}else{
			PhoneRequest();
		}
	}


	void StorageRequest(){
		int checkSelfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				ToastUtil.show(getApplication(), "请授权应用使用存储权限，否则将无法使用应用。");
			}

			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION2);

		} else {
			//有权限了，获取
			initData();
		}
	}

	void PhoneRequest() {

		int checkSelfPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);

		if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.READ_PHONE_STATE}, REQUEST_STORAGE_PERMISSION);
		} else {
			//有权限了，获取
			//TelephonyManager telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			//BusinessStatic.getInstance().IMEI = telMgr.getDeviceId();
			BusinessStatic.getInstance().IMEI = IMEIUtil.getIMEI(getApplicationContext());

			//initData();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode==REQUEST_STORAGE_PERMISSION){
			if ( grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
				//TelephonyManager telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
				//BusinessStatic.getInstance().IMEI = telMgr.getDeviceId();
				BusinessStatic.getInstance().IMEI = IMEIUtil.getIMEI(getApplicationContext());
			}else {
				toast("您没有授权访问电话权限。");
			}
		}else if( requestCode == REQUEST_STORAGE_PERMISSION2){
			if ( grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
				initData();
			}else{
				toast("由于您没有授权应用使用存储权限，因此应用无法使用");
			}
		}else if( requestCode == REQUEST_STORAGE_PERMISSION3 ){
            if ( grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                PhoneRequest();
            }else{
                toast("由于您没有授权应用使用存储权限，因此应用无法使用");
            }
        }
//        else if( requestCode==REQUEST_SYSTEM_WINDOW){
//			if ( grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
//				SDRequest();
//			}else{
//				toast("由于您没有授权应用显示浮动窗口权限，因此部分功能无法使用");
//				//System.exit(0);
//                SDRequest();
//			}
//		}
	}

	@Override
	protected void onResume() {

		BusinessStatic.getInstance().API_LEVEL = Integer.parseInt(VERSION.SDK);

		if(TextUtils.isEmpty(BusinessStatic.getInstance().IMEI))
			BusinessStatic.getInstance().IMEI = JPushInterface.getRegistrationID(this);
		L.i(">>>>>>>>>imei:" + BusinessStatic.getInstance().IMEI);

		/**
		 * 获取citycode
		 */
		String cityCode = SPUtil.getStringToSpByName(LoadingActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_CITY_CODE);
		if(!TextUtils.isEmpty(cityCode))
			BusinessStatic.getInstance().CITY_CODE = cityCode;
		int locationCount = SPUtil.getIntFromSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LOCATION_COUNT);
		if( locationCount == 5){
			BusinessStatic.getInstance().USER_LAT = -1;
			BusinessStatic.getInstance().USER_LNG = -1;
		}
		BusinessStatic.getInstance().USER_LAT = SPUtil.getLongToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LAT);
		BusinessStatic.getInstance().USER_LNG = SPUtil.getLongToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LNG);
		if(BusinessStatic.getInstance().USER_LAT == 0 && BusinessStatic.getInstance().USER_LNG == 0){
			SPUtil.saveIntToSpByName(LoadingActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LOCATION_COUNT, locationCount ++);
		}
//		SharedPreferences sp = getSharedPreferences(Constant.SP_NAME_NORMAL, Context.MODE_PRIVATE);
//		BusinessStatic.CITY_CODE =  sp.getString(Constant.SP_NAME_CITY_CODE, BusinessStatic.CITY_CODE);

		taskService = new TaskService(this);
		userService = new UserService(this);
		/**
		 * 提交token
		 * Device Token为友盟生成的用于标识设备的id，长度为44位，不能定制和修改。同一台设备上每个应用对应的Device Token不一样。
			获取Device Token的代码需要放在mPushAgent.enable();后面，注册成功以后调用才能获得Device Token。
			如果返回值为空， 说明设备还没有注册成功， 需要等待几秒钟，同时请确保测试手机网络畅通。

			所以迟3秒后再获取token并提交
		 */

				//String tokens = UmengRegistrar.getRegistrationId(LoadingActivity.this);




		//String loginCode = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LOGINCODE);
		//initData();

		StorageRequest();
		//FloatWindowRequest();

		//LibLocation.startLocation(this, this);
		super.onResume();
	}

	private void initData() {
		String userName = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME);
		String pwd = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD);
		int[] size = DensityUtil.getSize(this);
		String	token= JPushInterface.getRegistrationID(LoadingActivity.this);
		L.i(">>>>>token:" + token);
		if(!TextUtils.isEmpty(token) && (!SPUtil.getBooleanFromSpByName(LoadingActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_TOKEN, false)
				||  !token.equals(SPUtil.getStringToSpByName(LoadingActivity.this,  Constant.SP_NAME_NORMAL, Constant.SP_NAME_TOKEN_VALUE)))){
			L.i(">>>>commitToken:" + token);
			taskService.commitToken(LoadingActivity.this,UserData.getUserData().loginCode, token,1);

		}
//		if(loader == null)
//			loader = new SyncImageLoaderHelper(this);
		taskService.PayConfig(this);
		taskService.init(this, userName, pwd,size[0], size[1], loader);

		//userService.getScanCount();
		//userService.GetUserTodayBrowseCount(UserData.getUserData().loginCode);
	}

	private Bitmap checkLoadingImage() {

		Bitmap result = null;
		File imgDir = new File(Constant.IMAGE_PATH_LOADING);
		if (!imgDir.exists())
			return null;
		if (!imgDir.isDirectory())
			return null;
		File[] files = imgDir.listFiles();
		if (files == null)
			return null;
		int currentTime = Util.getCurrentTime();
		for (int i = 0; i < files.length; i++) {
			if (!files[i].isDirectory()) {
				// 获取文件名
				String name = files[i].getName();
				// 根据文件名到sp里获取图处使用时间
				int[] time = SPUtil.getDateFromSpByName(this,
						Constant.SP_NAME_LOADING, name);
				if (time == null) {
					files[i].delete();
					continue;
				}
				if (currentTime >= time[0] && currentTime <= time[1])// 需展示
					result = ImageUtil.readBitmapByPath(files[i]
							.getAbsolutePath());
				if (time[1] < currentTime)// 已过期
					files[i].delete();
			}
		}
		return result;
	}

	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		int seconds = (int) ((System.currentTimeMillis() - time)/1000);
		if(seconds == 0)
			mHandler.sendMessageDelayed(mHandler.obtainMessage(type, extra), 1000);
		else
			mHandler.obtainMessage(type, extra).sendToTarget();
	}

	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}

	@Override
	public void onKeyBack() {
		if (isFinish)
			finish();
		else
			checkSystemWindow();
			//toHome();
	}

	@Override
	protected void onDestroy() {
		// CustomDialog.setOnKeyBackListener(null);
		broadcastReceiver.unregisterReceiver();
		LibLocation.stopLocation();
		super.onDestroy();
	}

	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		// TODO Auto-generated method stub

	}
}
