package cy.com.morefan;


import java.util.List;

import cn.jpush.android.api.JPushInterface;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.service.ThreadPoolManager;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.SecurityUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.util.WindowProgress;
import cy.com.morefan.util.WindowProgress.progressOnKeyBack;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.PopExpUp;
import cy.com.morefan.view.PopGuide;
import cy.com.morefan.view.PopUserLogin;
import cy.com.morefan.view.PopUserLogin.OnPopLoginListener;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public class BaseActivity extends SwipeBackActivity implements BusinessDataListener{
	protected static final String NULL_NETWORK = "无网络或当前网络不可用!";

	public WindowProgress progress;
	public Handler handler = new Handler();
	private GestureDetector mGestureDetector;
	private int screenWidth  ;
	protected UserService userService;
	private boolean isHomeReg;
	protected void onBack(){

	};

	@Override
	protected void onCreate(Bundle arg0) {
		MainApplication.getActivityManager().pushActivity(this);
		userService = new UserService(this);
		 mGestureDetector = new GestureDetector(this, new MyOnGestureListener());
		 //int screenWidth  = (int) getScreenWidth();
		 DisplayMetrics displayMetrics = new DisplayMetrics();
		 getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		 screenWidth  =  displayMetrics.widthPixels;
		 if(!isHomeReg){
			 isHomeReg = true;
			//注册广播
		        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
						Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		 }
		super.onCreate(arg0);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(outState != null){
			BusinessStatic.save(outState);
			UserData.save(outState);
		}
		super.onSaveInstanceState(outState);
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState != null){
			BusinessStatic.restore(savedInstanceState);
			UserData.restore(savedInstanceState);

		}
		super.onRestoreInstanceState(savedInstanceState);
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setSwipeBackEnable(false);
	}


	@Override
	protected void onDestroy() {
		MainApplication.getActivityManager().popActivity(this);
		if (mMediaPlayer != null)
			mMediaPlayer.release();
		unregisterReceiver(mHomeKeyEventReceiver);
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		isMarkedHomeLong = false;
		JPushInterface.onResume(this);
		super.onResume();
	}
	@Override
	protected void onPause() {
		JPushInterface.onPause(this);
		super.onPause();
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}


	    class MyOnGestureListener extends SimpleOnGestureListener {
	        @Override
	        public boolean onSingleTapUp(MotionEvent e) {
	            return false;
	        }

	        @Override
	        public void onLongPress(MotionEvent e) {
	        }

	        @Override
	        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	            return false;
	        }

	        @Override
	        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	             int distanceX    = (int) (e2.getX() - e1.getX());
	             int distanceY    = (int) (e2.getY() - e1.getY());
	             if(Math.abs(distanceY) > screenWidth * 0.3)
	             	return false;
	             if(distanceX > screenWidth * 0.3){
	                 if(distanceX > 0  ){
	                   onBack();
	                 }
	             }
	             return true;
	        }

	        @Override
	        public void onShowPress(MotionEvent e) {
	        }

	        @Override
	        public boolean onDown(MotionEvent e) {
	            return false;
	        }

	        @Override
	        public boolean onDoubleTap(MotionEvent e) {
	            return false;
	        }

	        @Override
	        public boolean onDoubleTapEvent(MotionEvent e) {
	            return false;
	        }

	        @Override
	        public boolean onSingleTapConfirmed(MotionEvent e) {
	            return false;
	        }
	    }






	public void onClickButton(View v){
		if(v.getId() == R.id.btnBack){
			finish();
		}
	}

	public void showProgress() {
		//网络访问前先检测网络是否可用
		if(!Util.isConnect(BaseActivity.this)){
			toast(NULL_NETWORK);
			return;
		}

		if(progress == null){
			//progress = new WindowProgress(BaseActivity.this);
			progress = new WindowProgress(this);
		}

		handler.post(new Runnable() {
			@Override
			public void run() {
				if(!BaseActivity.this.isFinishing())
					try {
						progress.showProgress();
					} catch (Exception e) {
						System.out.println(e.toString());
					}

			}
		});

	}
	public void setProgressOnkeyBack(progressOnKeyBack keyBack){
		if(progress == null){
			//progress = new WindowProgress(BaseActivity.this);
			progress = new WindowProgress(this);
		}
		progress.setProgressOnkeyBack(keyBack);
	}
	public void dismissProgress(){
		if(progress == null)
			return;

		handler.post(new Runnable() {
			@Override
			public void run() {
				progress.dismissProgress();
			}
		});
	}
	public void toast(final String msg) {
		if(TextUtils.isEmpty(msg))
			return;
		/**
		 * message里有一个target的，也就是目标handler。
		 * 如果new一个message，你必须使用handler.sendmessage(msg)才可以把message发到相应的handler。
		 * 但obtain会自动为你把message的target设置为当前的handler，所以直接sendtotarget就行了。
		 * 另外handler内有个池，所有用完的message并没有回收，而是放到这个池里。
		 * 使用obtain会从池里拿一个出来用，这样就避免的复制申请释放内存，性能要好很多。
		 */
		handler.post(new Runnable() {
			@Override
			public void run() {
				ToastUtil.show(BaseActivity.this, msg);
			}
		});
		//handler.obtainMessage(TOAST, msg).sendToTarget();
	}
	public void forgetLogin(){
		Intent intentForget = new Intent(BaseActivity.this, ForgetLoginPwdActivity.class);
		startActivity(intentForget);
	}


	/**
	 *
	 * @param name 用户名
	 * @param pwd 密码
	 * @param needProgress 是否需要进度条
	 *
	 */
	public void login(String name, String pwd, boolean needProgress){
		userService.MobileLogin(this, name, SecurityUtil.MD5Encryption(pwd));
		if(needProgress)
			showProgress();
	}

	private void loginFail(String msg) {
		CustomDialog.showChooiceDialg(this, "温馨提示", msg, "重登", "取消", null, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//userLogin();

			}
		}, null);

	}

	@Override
	public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
		if(type == BusinessDataListener.DONE_EXP_UP){
			final int exp = extra.getInt("exp");
			handler.post(new Runnable() {
				@Override
				public void run() {
					//expUp(exp);
				}
			});
		}


	}
	@Override
	public void onDataFailed(int type, final String des, Bundle extra) {
			if(type == ERROR_USER_LOGIN){
				handler.post(new Runnable() {
					@Override
					public void run() {
						loginFail(des);
					}
				});

			}


	}

	@Override
	public void onDataFail(int type, String des, Bundle extra) {

	}


	@Override
	protected void onRestart() {
		super.onRestart();
		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean isCurrentRunningForeground = SPUtil.getBooleanFromSpByName(this, Constant.SP_NAME_NORMAL, "isCurrentRunningForeground", false);//prefs.getBoolean("isCurrentRunningForeground", false);
		if (!isCurrentRunningForeground) {
			//get sp time
			long oldTime = SPUtil.getLongToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BACK_TIME);
			long dis =  System.currentTimeMillis() - oldTime;
			if(dis/1000 > Constant.BACK_TIME_DIS){
				MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
				//update old time
				SPUtil.saveLongToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BACK_TIME, System.currentTimeMillis());
				MainApplication.restartApp(this);
			}
		}
	}


	@Override
	protected void onStop() {
		super.onStop();
		ThreadPoolManager.getInstance().addTask(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SPUtil.saveBooleanToSpByName(BaseActivity.this, Constant.SP_NAME_NORMAL, "isCurrentRunningForeground", isRunningForeground());
			}
		});

	}

	private boolean isMarkedHomeLong;
	private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
		String SYSTEM_REASON = "reason";
		String SYSTEM_HOME_KEY = "homekey";
		String SYSTEM_HOME_KEY_LONG = "recentapps";
		String SYSTEM_HOME_KEY_LONG_SAMSUNG = "assist";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
					 //表示按了home键,程序到了后台
					if(!isMarkedHomeLong){
						isMarkedHomeLong = true;
						//toast("home");
						SPUtil.saveBooleanToSpByName(BaseActivity.this, Constant.SP_NAME_NORMAL, "isCurrentRunningForeground", false);
						SPUtil.saveLongToSpByName(BaseActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BACK_TIME, System.currentTimeMillis());

					}
				//Toast.makeText(getApplicationContext(), "home", 1).show();
				}else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG) || TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG_SAMSUNG)){
					  // 长按Home键 或者 activity切换键
					 // samsung 长按Home键
					//toast("home long");
					if(!isMarkedHomeLong){
						isMarkedHomeLong = true;
						//toast("home long for");
						SPUtil.saveBooleanToSpByName(BaseActivity.this, Constant.SP_NAME_NORMAL, "isCurrentRunningForeground", false);
						SPUtil.saveLongToSpByName(BaseActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BACK_TIME, System.currentTimeMillis());

					}
				}
			}
		}
	};

public boolean isRunningForeground(){
	String packageName=getPackageName(this);
	String topActivityClassName=getTopActivityName(this);
	if(packageName!=null&&topActivityClassName!=null&&topActivityClassName.startsWith(packageName)) {
		return true;
		} else {
			//save time
			SPUtil.saveLongToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BACK_TIME, System.currentTimeMillis());
			return false;
			}
	}







	public String getTopActivityName(Context context) {
		ActivityManager activityManager = (ActivityManager) (context
				.getSystemService(android.content.Context.ACTIVITY_SERVICE));
		List<RunningTaskInfo> rti = activityManager.getRunningTasks(1);
		return rti.get(0).topActivity.getClassName();
	}

	public String getPackageName(Context context) {
		String packageName = context.getPackageName();
		return packageName;
	}


	private PopGuide popGuide;
	public void showUserGuide(int resId){
		boolean beenShowed = SPUtil.getBooleanFromSpByName(this, Constant.SP_NAME_NORMAL, resId+"", false);
		if(beenShowed)
			return;
		if(null == popGuide)
		  popGuide = new PopGuide(this);
		popGuide.show(this, resId);
//
//		Intent intentAppUserGuide = new Intent(this, AppUserGuideActivity.class);
//		intentAppUserGuide.putExtra("resId", resId);
//		startActivity(intentAppUserGuide);

	}




	private MediaPlayer mMediaPlayer;
//	public void showMoneyAnim(final ImageView imgMoney){
//		if(mMediaPlayer == null){
//			mMediaPlayer = MediaPlayer.create(this,R.raw.money_receive);
//		}
//
//		imgMoney.setImageResource(R.anim.money);
//		AnimationDrawable animationDrawable = (AnimationDrawable) imgMoney.getDrawable();
//		animationDrawable.start();
//
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				try {
//		            if (mMediaPlayer != null) {
//		            	mMediaPlayer.stop();
//		            }
//		            mMediaPlayer.prepare();
//		        } catch (Exception e) {
//		            // TODO Auto-generated catch block
//		            e.printStackTrace();
//		        }
//					mMediaPlayer.start();
//			}
//		}, 200);
//		handler.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				imgMoney.setVisibility(View.GONE);
//
//			}
//		}, 1100);
//
//
//	}

	private PopExpUp popExpUp;
	public void expUp(int exp){
		if(exp < 1)
			return;
		if(null == popExpUp)
			popExpUp = new PopExpUp(this);
		popExpUp.show(exp);
	}


}
