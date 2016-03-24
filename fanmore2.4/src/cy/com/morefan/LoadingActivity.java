package cy.com.morefan;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.Key;
import java.text.NumberFormat;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import cindy.android.test.synclistview.SyncImageLoaderHelper;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.lib.cylibimagedownload.ImageUtil;
import com.lib.cyliblocation.LibLocation;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import cy.com.morefan.AppUpdateActivity.UpdateType;
import cy.com.morefan.bean.AllScoreData;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.StoreData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.JsonUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.RSAUtil;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.IMEIUtil;
import cy.com.morefan.util.NetworkUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.CustomDialog.OnkeyBackListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LoadingActivity extends BaseActivity implements
		BusinessDataListener, OnkeyBackListener, BDLocationListener, Callback, BroadcastListener{

	private Handler mHandler = new Handler(this);
	private boolean isFinish;
	private boolean isCompleteUserInfo;
	private long time;
	private int alarmId;

	@Override
	public boolean handleMessage(Message msg) {

		if (msg.what == BusinessDataListener.DONE_INIT) {
			Bundle bundle = (Bundle) msg.obj;
			int updateType = bundle.getInt("updateType");
			String updateMd5 = bundle.getString("updateMd5");
			String updateUrl = bundle.getString("updateUrl");
			String updateTips = bundle.getString("updateTips");
			// 是否完善个人信息
			isCompleteUserInfo = bundle.getInt("isCompleteUserInfo") == 1;
			switch (updateType) {
			case 0:// 无更新
				toHome();
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
							initData();
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


	private void choiceToUpdate(final UpdateType type, final String md5,
			final String url, final String tips) {
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
						toHome();
					}
				});

	}

	private void toUpdate(UpdateType type, String md5, String url, String tips,
			boolean isForce) {
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
					toHome();
				}

			}

		}
		super.onActivityResult(requestCode, resultCode, arg2);
	}

	private void toHome() {
		if (!SPUtil.getBooleanFromSpByName(this, Constant.SP_NAME_NORMAL,
				Constant.SP_NAME_NOT_SHOW_USER_GUIDE, false)) {
			Intent intentGuide = new Intent(LoadingActivity.this, UserGuideActivity.class);
			intentGuide.putExtra("isCompleteUserInfo", isCompleteUserInfo);
			intentGuide.putExtra("alarmId", alarmId);
			startActivity(intentGuide);
			finish();
		}else if (
				!TextUtils.isEmpty( SPUtil.getStringToSpByName(LoadingActivity.this,Constant.SP_NAME_NORMAL,Constant.SP_NAME_USERNAME)) &&
				!TextUtils.isEmpty( SPUtil.getStringToSpByName(LoadingActivity.this,Constant.SP_NAME_NORMAL,Constant.SP_NAME_USERPWD) ) ) {
			Intent intent = new Intent(LoadingActivity.this, HomeActivity.class);
			intent.putExtra("isCompleteUserInfo", isCompleteUserInfo);
			intent.putExtra("alarmId", alarmId);
			//UserData.restore();
			startActivity(intent);
			finish();
		}
 		else {
			Intent intent = new Intent(LoadingActivity.this, LoginActivity.class);
			intent.putExtra("isCompleteUserInfo", isCompleteUserInfo);
			intent.putExtra("alarmId", alarmId);
			startActivity(intent);
			finish();
		}


	}

	private TaskService taskService;
	private UserService userService;
	private SyncImageLoaderHelper loader;
	private MyBroadcastReceiver broadcastReceiver;
	

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.loading);
		((TextView)findViewById(R.id.txtVersion)).setText(String.format("V %s Beta", Constant.APP_VERSION));
		//推送：1、enable ;2、BaseActivity_oncreate:onAppStart
		boolean pushSwitch = SPUtil.getBooleanFromSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_PUSH_SWITCH, true);
		if(pushSwitch){
			PushAgent.getInstance(this).enable();
		}else{
			PushAgent.getInstance(this).disable();
		}

		if(null != getIntent().getExtras())
			alarmId = getIntent().getExtras().getInt("alarmId");
		broadcastReceiver = new MyBroadcastReceiver(this, this,  Intent.ACTION_BATTERY_CHANGED);

		//JsonUtil.convertToObj(null, StoreData.class);
		// toast(getPackageName() + "1.0.1");
//>>>>>>>>>>>uuid:29ff36e3-b2b5-48bc-8a3c-684fec8f1cdf

//		System.out.println(getDeviceInfo(this));
//		//06-20 15:00:25.633: I/System.out(8046): >>>>token:AktcOBC1j1Em9FseNgAo19zhSuhdvZsd_YLZdXOb-Se2
//		String device_token = UmengRegistrar.getRegistrationId(this);
//		System.out.println(">>>>token:" + device_token);
//		L.i(">>>>>>uuid:" + IMEIUtil.getUUID(this));
		//06-20 14:46:06.384: I/System.out(5201): {"device_id":"359836042904932","mac":"1c:b0:94:14:b1:18"}


		// check file
		Bitmap bitmap = checkLoadingImage();
		if (bitmap != null) {
			BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
			findViewById(R.id.lay).setBackgroundDrawable(drawable);
			L.i(">>>>>setBitmap:"+ bitmap );
		}
		time = System.currentTimeMillis();

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

	@Override
	protected void onResume() {

		BusinessStatic.getInstance().API_LEVEL = Integer.parseInt(VERSION.SDK);



		TelephonyManager telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		BusinessStatic.getInstance().IMEI = telMgr.getDeviceId();

		if(TextUtils.isEmpty(BusinessStatic.getInstance().IMEI))
			BusinessStatic.getInstance().IMEI = UmengRegistrar.getRegistrationId(this);
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
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				String token = UmengRegistrar.getRegistrationId(LoadingActivity.this);
				L.i(">>>>>token:" + token);
				if(!TextUtils.isEmpty(token) && (!SPUtil.getBooleanFromSpByName(LoadingActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_TOKEN, false)
					||  !token.equals(SPUtil.getStringToSpByName(LoadingActivity.this,  Constant.SP_NAME_NORMAL, Constant.SP_NAME_TOKEN_VALUE)))){
					L.i(">>>>commitToken:" + token);
					taskService.commitToken(LoadingActivity.this, token);
				}

			}
		}, 3000);

		//String loginCode = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LOGINCODE);
		initData();
		LibLocation.startLocation(this, this);
		super.onResume();
	}

	private void initData() {
		String userName = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME);
		String pwd = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD);
		int[] size = DensityUtil.getSize(this);
		if(loader == null)
			loader = new SyncImageLoaderHelper(this);
		taskService.init(this, userName, pwd,size[0], size[1], loader);

		taskService.PayConfig(this);
		userService.getScanCount();
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
			toHome();
	}

	@Override
	protected void onDestroy() {
		// CustomDialog.setOnKeyBackListener(null);
		broadcastReceiver.unregisterReceiver();
		LibLocation.stopLocation();
		super.onDestroy();
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location != null) {
			String cityCode = location.getCityCode();
			System.out.println(">>>>>district:" + location.getDistrict());
			if (!TextUtils.isEmpty(cityCode)) {
				BusinessStatic.getInstance().CITY_CODE = cityCode;
				SPUtil.saveStringToSpByName(LoadingActivity.this,
						Constant.SP_NAME_NORMAL, Constant.SP_NAME_CITY_CODE,
						cityCode);
				LibLocation.stopLocation();

				BusinessStatic.getInstance().USER_LAT = location.getLatitude();
				BusinessStatic.getInstance().USER_LNG = location.getLongitude();
				if(BusinessStatic.getInstance().USER_LAT == 0 && BusinessStatic.getInstance().USER_LNG == 0);
				else{
					SPUtil.saveIntToSpByName(LoadingActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LOCATION_COUNT, 0);
					SPUtil.saveLongToSpByName(LoadingActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LAT, (long) BusinessStatic.getInstance().USER_LAT);
					SPUtil.saveLongToSpByName(LoadingActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_LNG, (long) BusinessStatic.getInstance().USER_LNG);

				}

				L.i(">>>>>>>>>>stop baidu");
			}
		}

	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		// TODO Auto-generated method stub

	}


	
}
