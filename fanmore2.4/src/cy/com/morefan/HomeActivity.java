package cy.com.morefan;



import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.AuthCodeSendActivity.AuthType;
import cy.com.morefan.ToCrashAuthActivity.CrashAuthType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.PrenticeTopData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.TempPushMsgData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.constant.Constant.FromType;
import cy.com.morefan.frag.BaseFragment;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.frag.TaskNewFrag;
import cy.com.morefan.frag.ToolFrag;
import cy.com.morefan.frag.FragManager.FragType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;

import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.DragLayout;
import cy.com.morefan.view.ImageLoad;
import cy.com.morefan.view.PopExpUp;
import cy.com.morefan.view.DragLayout.DragListener;
//import cy.com.morefan.view.PopCheckIn;
//import cy.com.morefan.view.PopCheckIn.OnPopCheckListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * stillEmpty
 * @author edushi
 *666
 */
public class HomeActivity extends BaseActivity implements BroadcastListener, Callback, DragListener{
	private MyBroadcastReceiver myBroadcastReceiver;
	private DragLayout mDragLayout;
	private FragManager fragManager;
	private UserService userService;
	private TextView txtMine;
	private ImageView imgPhoto;
	private TextView txtName;
	private TextView txtScore;
	private TextView txtExp;
	private TextView txtTodayScan;
	private TextView txtYesScore;
	private PrenticeTopData topData;

	private boolean trendToMy = false;
	private CyButton btnRight;
	private LinearLayout layTab;
	private LinearLayout layMiddle;
	private TextView txtRight;
	private TextView txtTitle;
	private ImageView imgTag;
	//private TextView txtPrenticeCount;
	private ImageView imgCheckFlag;
	private ImageView img;
	private TextView txtCheckDes;

	//private PopCheckIn popCheckIn;
	private SyncImageLoaderHelper helper;


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		setSwipeBackEnable(false);


	}
	public static class Utils {
		private static long lastClickTime;
		public synchronized static boolean isFastClick() {
			long time = System.currentTimeMillis();
			if ( time - lastClickTime < 500) {
				return true;
			}
			lastClickTime = time;
			return false;
		}
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.home_main);
		helper = new SyncImageLoaderHelper(this);
		initView();
		operationPushMsg();
		operationAlarm();
		setScores();
		userService = new UserService(this);


		if(null == arg0){
			fragManager = new FragManager(this, R.id.layContent);
			fragManager.setCurrentFrag(FragType.Task);
		}

		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_USER_LOGIN, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
		//showUserGuide(R.drawable.user_guide_task_list);

		//userReg();
		//userLogin();


	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		L.i("<<<<<<<<<<<<<<<<<<onSaveInstanceState");
		outState.putInt("test", 2);
		super.onSaveInstanceState(outState);
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		L.i("<<<<<<<<<<<<<<<<<<onSaveInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
	}
	@Override
	protected void onResume() {
		L.i("<<<<<<<<<<<<<<<<<<onResume");
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		L.i("<<<<<<<<<<<<<<<<<<onDestroy");
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}
	private void initView() {
		img = (ImageView) findViewById(R.id.img);
		imgCheckFlag = (ImageView) findViewById(R.id.imgCheckFlag);
		btnRight = (CyButton) findViewById(R.id.btnRight);
		//txtPrenticeCount = (TextView) findViewById(R.id.txtPrenticeCount);
		imgTag = (ImageView) findViewById(R.id.imgTag);
		txtRight=(TextView)findViewById(R.id.txtRight);
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		layTab = (LinearLayout) findViewById(R.id.layTab);
		layMiddle = (LinearLayout) findViewById(R.id.layMiddle);
		mDragLayout	 = (DragLayout) findViewById(R.id.dragLayout);
		mDragLayout.setDragListener(this);
		txtMine 	 = (TextView) findViewById(R.id.txtMine);
		imgPhoto 	 = (ImageView) findViewById(R.id.imgPhoto);
		txtName		 = (TextView) findViewById(R.id.txtName);
		txtScore	 = (TextView) findViewById(R.id.txtScore);
		txtExp		 = (TextView) findViewById(R.id.txtExp);
		txtTodayScan = (TextView) findViewById(R.id.txtTodayScan);
		txtYesScore	 = (TextView) findViewById(R.id.txtYesScore);
		//userLogin();

	}
	
	private void operationAlarm() {
		if(null == getIntent().getExtras())
			return;
		int id = getIntent().getExtras().getInt("alarmId");
		if(id == 0)
			return;
		//to task detail,得刷新任务列表
		Intent intentDetail = new Intent(this, TaskDetailActivity.class);
		intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		TaskData taskData = new TaskData();
		taskData.id = id;
		intentDetail.putExtra("taskData", taskData);
		intentDetail.putExtra("refreshList", true);
		startActivity(intentDetail);
	}
	private void operationPushMsg() {
		/**
		 * 此处只处理app未在运行时接收到的推送消息;
		 * （app运行时的推送消息已直接被处理）
		 *
		 */
		TempPushMsgData msg = TempPushMsgData.getIns();
		L.i(msg.toString());
		if(msg.fromNotify){//推送消息处理
			if(msg.type == 1){//web消息
				Intent intentWeb = new Intent(getApplicationContext(), WebViewActivity.class);
				intentWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intentWeb.putExtra("url", msg.webUrl);
				startActivity(intentWeb);
			}else{//任务消息
				/**
				 *0已开始
				 *1未开始
				 *2已下架
				 */
				if(msg.status == 0){
					//to task detail,得刷新任务列表
					Intent intentDetail = new Intent(this, TaskDetailActivity.class);
					intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					TaskData taskData = new TaskData();
					taskData.id = msg.taskId;
					intentDetail.putExtra("taskData", taskData);
					intentDetail.putExtra("refreshList", true);
					startActivity(intentDetail);
				}else if(msg.status == 1){
					Intent intentWeb = new Intent(getApplicationContext(), WebViewActivity.class);
					intentWeb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intentWeb.putExtra("url", msg.webUrl);
					intentWeb.putExtra("title", "任务预告");
					startActivity(intentWeb);
				}else{
					toast("任务已下架!");
				}
			}
			TempPushMsgData.clear();

//			Intent intentAbout = new Intent(this, WebViewActivity.class);
//			intentAbout.putExtra("url", "http://www.baidu.com");
//			intentAbout.putExtra("title", "任务预告");
//			startActivity(intentAbout);
		}

	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.Login){
				handler.post(new Runnable() {
					@Override
					public void run() {
						txtMine.setText("我的帐号");
					//点击“我的帐号”进行注册后，自动切至“我的帐号”
					if (trendToMy) {
						trendToMy = false;
						showMyFrag();
					}
					//任务列表
					((TaskNewFrag)fragManager.getFragmentByType(FragType.Task)).initData();
					setScores();
					//refreshMyData();
					}
				});

		}else if(type == ReceiverType.BackgroundBackToUpdate){
			//showTaskFrag();
			MainApplication.restartApp(this);
		}
		else if( type == ReceiverType.Logout){
			//this.finish();
		}

	}

	public void onClick(View v){
		if (Utils.isFastClick()) {
			return ;
		}
		switch (v.getId()) {

		case R.id.btnLeft:

				mDragLayout.openOrClose();


			break;
		case R.id.layHome:
			fragManager.setCurrentFrag(FragType.Task);
			setTitleButton(FragType.Task);
			openOrCloseMenu();
			break;
		case R.id.layPre:
			//任务提前预览
			Intent intentPre = new Intent(this, TaskActivity.class);
			intentPre.putExtra(Constant.TYPE_FROM, FromType.PreTask);
	        startActivity(intentPre);
			break;
//		case R.id.layJoin:
//			Intent intentPutIn = new Intent(this, WebViewActivity.class);
//			intentPutIn.putExtra("url", BusinessStatic.getInstance().URL_PUTIN);
//			intentPutIn.putExtra("title", "投放指南");
//			startActivity(intentPutIn);
//			break;
		case R.id.layMine:
		case R.id.img:
			trendToMy = false;
			if(UserData.getUserData().isLogin){
				//是否为模拟器
				if( !UserData.getUserData().ignoreJudgeEmulator && BusinessStatic.getInstance().ISEMULATOR){
					toast("模拟器不支持该操作!");
					return;
				}
				fragManager.setCurrentFrag(FragType.My);
				setTitleButton(FragType.My);
				openOrCloseMenu();
			}else{
				trendToMy = true;
				Intent intentlogin = new Intent(HomeActivity.this, LoginActivity.class);
				startActivity(intentlogin);
				openOrCloseMenu();
			}

			break;
		case R.id.layPrentice:
			if (UserData.getUserData().isLogin) {
				//是否为模拟器
				if( !UserData.getUserData().ignoreJudgeEmulator && BusinessStatic.getInstance().ISEMULATOR){
					toast("模拟器不支持该操作!");
					return;
				}
				fragManager.setCurrentFrag(FragType.Prentice);
				setTitleButton(FragType.Prentice);
			
				openOrCloseMenu();
			} else {
				Intent intentlogin = new Intent(HomeActivity.this, LoginActivity.class);
				startActivity(intentlogin);
				finish();
			}
			break;
//		case R.id.layTool:
//			fragManager.setCurrentFrag(FragType.Tool);
//			setTitleButton(FragType.Tool);
//			openOrCloseMenu();
//			break;
		case R.id.layRule:
			fragManager.setCurrentFrag(FragType.Rule);
			setTitleButton(FragType.Rule);
			openOrCloseMenu();
			break;
		case R.id.layMore:
			fragManager.setCurrentFrag(FragType.More);
			setTitleButton(FragType.More);
			openOrCloseMenu();
			break;
//		case R.id.layCheckIn:
//			if (UserData.getUserData().isLogin) {
//				if(null == popCheckIn){
//					popCheckIn = new PopCheckIn(this);
//					popCheckIn.setOnPopCheckListener(new OnPopCheckListener() {
//						@Override
//						public void onPopCheck() {
//							checkIn();
//						}
//					});
//				}
//				popCheckIn.show(UserData.getUserData().checkInDays);
//			} else {
//				userLogin(1);
//			}
//			break;
		case R.id.layTodayScan:
			 if(UserData.getUserData().isLogin){
  	        	Intent intent = new Intent(HomeActivity.this, TaskActivity.class);
  	        	intent.putExtra(Constant.TYPE_FROM, FromType.TodayScan);
  	        	startActivity(intent);

  	        }else{
				 Intent intentlogin = new Intent(HomeActivity.this, LoginActivity.class);
				 startActivity(intentlogin);
				 openOrCloseMenu();
  	        }
			break;
		case R.id.layYesScore:
			if(UserData.getUserData().isLogin){
//	        	Intent intent = new Intent(HomeActivity.this, MyPartInActivity.class);
//	        	intent.putExtra("PartInType", TaskAdapterType.Yes);
//	        	startActivity(intent);


	        	Intent intent = new Intent(HomeActivity.this, TaskActivity.class);
	        	intent.putExtra(Constant.TYPE_FROM, FromType.YesAward);
	        	startActivity(intent);

	        }else{
				Intent intentlogin = new Intent(HomeActivity.this, LoginActivity.class);
				startActivity(intentlogin);
				openOrCloseMenu();
	        }
			break;
		
		case R.id.layScore:
			 if(UserData.getUserData().isLogin){
    			 Intent intent = new Intent(HomeActivity.this, AllScoreActivity.class);
     	         startActivity(intent);
 	        }else{
				 Intent intentlogin = new Intent(HomeActivity.this, LoginActivity.class);
				 startActivity(intentlogin);
				 openOrCloseMenu();
 	        }
			break;

		default:
			break;
		}
		if(null != fragManager.getCurrentFrag())
			fragManager.getCurrentFrag().onClick(v);
	}



	public void openOrCloseMenu(){
		mDragLayout.openOrClose();
		}
	public FragType getCurrentFragType(){
		return fragManager.getCurrentFragType();
	}
	public void setTitleButton(FragType type){
		layMiddle.setVisibility(View.VISIBLE);
		layTab.setVisibility(View.GONE);
		//txtPrenticeCount.setVisibility(View.GONE);
		switch (type) {
		case Task:
			layMiddle.setVisibility(View.GONE);
			layTab.setVisibility(View.VISIBLE);
			txtRight.setVisibility(View.GONE);
			BaseFragment frag = fragManager.getCurrentFrag();
			String title = getResources().getString(R.string.app_title_name);
			if(frag instanceof TaskNewFrag){
				title = ((TaskNewFrag)frag).getTitleText();

			}
			txtTitle.setText(title);
			//btnLeft.setVisibility(View.VISIBLE);
			btnRight.setVisibility(View.GONE);
			btnRight.setBackgroundResource(R.drawable.title_right_refresh);
			imgTag.setVisibility(View.VISIBLE);
			//btnLeft.setBackgroundResource(R.drawable.title_left_menu);

			break;
		case My:
			txtRight.setVisibility(View.GONE);
			imgTag.setVisibility(View.GONE);
			txtTitle.setText("我的帐号");
			btnRight.setVisibility(View.GONE);
			break;
		case Rule:
			txtRight.setVisibility(View.GONE);
			imgTag.setVisibility(View.GONE);
			txtTitle.setText("规则说明");
			//btnLeft.setVisibility(View.GONE);
			btnRight.setVisibility(View.GONE);
			break;

		case More:
			txtRight.setVisibility(View.GONE);
			imgTag.setVisibility(View.GONE);
			txtTitle.setText("更多");
			//btnLeft.setVisibility(View.GONE);
			btnRight.setVisibility(View.GONE);
			break;
		case Prentice:
			imgTag.setVisibility(View.GONE);
			txtTitle.setText("收徒");
			btnRight.setVisibility(View.GONE);
			txtRight.setVisibility(View.VISIBLE);
			//btnLeft.setVisibility(View.GONE);
			//btnRight.setVisibility(View.GONE);
			break;

		default:
			break;
		}
//
	}
	 /**
//	    * 点击“我的帐号”进行登录或注册后，自动切至“我的帐号”
//	    */
	   public void showMyFrag(){
		   fragManager.setCurrentFrag(FragType.My);
			setTitleButton(FragType.My);
			mDragLayout.openOrClose();
	   }
	public void toCrash(){
		//钱包不要求登录
	if(BusinessStatic.getInstance().CRASH_TYPE != 1 && !UserData.getUserData().isLogin){
		Intent intentlogin = new Intent(HomeActivity.this, LoginActivity.class);
		startActivity(intentlogin);

		return;
	}
	//是否为模拟器
	if( !UserData.getUserData().ignoreJudgeEmulator && BusinessStatic.getInstance().ISEMULATOR){
		toast("模拟器不支持该操作!");
		return;
	}
	//积分是否已达标准下限
	if(BusinessStatic.getInstance().CRASH_TYPE != 1 && Double.parseDouble(UserData.getUserData().score) < BusinessStatic.getInstance().CHANGE_BOUNDARY){
		toast(String.format("需达到%d积分才能提现，赶快去赚积分吧!",BusinessStatic.getInstance().CHANGE_BOUNDARY));
		return;
	}



	if(BusinessStatic.getInstance().CRASH_TYPE == 1){//到钱包
		Intent intentWallet = new Intent(this, WalletActivity.class);
		startActivity(intentWallet);
	}else{//提现
		//先判断是否已绑定手机号
		if(TextUtils.isEmpty(UserData.getUserData().phone)){
			toast("请先绑定手机号!");
			Intent intentBind = new Intent(this,AuthCodeSendActivity.class);
			intentBind.putExtra(Constant.AuthCodeType, AuthType.Phone2Ali);
			startActivity(intentBind);
			return;

		}
		if(TextUtils.isEmpty(UserData.getUserData().payAccount)){
			toast("请先绑定支付宝账号!");
			Intent intentBind = new Intent(this,AuthCodeSendActivity.class);
			intentBind.putExtra(Constant.AuthCodeType, AuthType.Ali);
			startActivity(intentBind);
			return;
		}

			Intent intentToCash = new Intent(this,ToCrashAuthActivity.class);
			//是否有提现密码
			if(Util.isEmptyMd5(UserData.getUserData().toCrashPwd)){
				//intentToCash.putExtra("type", NiePointActionType.Creat);
				intentToCash.putExtra("type", CrashAuthType.ToCrash);
			}else{//认证
				//intentToCash.putExtra("type", NiePointActionType.Auth);
				intentToCash.putExtra("type", CrashAuthType.Auth);
			}
			startActivityForResult(intentToCash, 0);
	}

}

	/**
	    * 用户注销后刷新任务列表
	    */
	   public void userLoginOut2ShowTaskFrag(){
		   setScores();
		   ((TaskNewFrag)fragManager.getFragmentByType(FragType.Task)).initData();

		   fragManager.setCurrentFrag(FragType.Task);
			setTitleButton(FragType.Task);
	   }
	   /**
	    * 刷新菜单
	    * 更新头像，姓名，总积分，经验值，今日浏览量，昨日收益
	    */
	public void setScores() {

		String yes = "未登录";
		String total = "未登录";
		String scanCount = "未登录";
		String userName = "未登录";
		String exp = "未登录";
		UserData userData = UserData.getUserData();
		txtMine.setText(userData.isLogin ? "我的帐号" : "登录/注册");
		if (userData.isLogin) {
			exp = Util.MoneyFormat(userData.exp);
			yes = userData.yesScore;//Util.MoneyFormat(userData.yesScore);
			total = userData.score;//Util.MoneyFormat(userData.totalScore);
			scanCount = Util.MoneyFormat(userData.todayScanCount);
			userName = userData.RealName;
			if (TextUtils.isEmpty(userName))
				userName = userData.UserNickName;
			else if (TextUtils.isEmpty(userName)){
				userName =userData.userName;
			}
		}


		txtName.setText(userName);
		txtScore.setText("可用分红." + total);
		txtExp.setText("Exp." + exp);
		txtTodayScan.setText(scanCount);
		txtYesScore.setText(yes);
		imgCheckFlag.setVisibility(userData.dayCheckIn ? View.VISIBLE :View.GONE);
		L.i(">>>>>>>>>picUrl:" + userData.picUrl);
		if(TextUtils.isEmpty(userData.picUrl)){
			img.setImageResource(R.drawable.user_icon);
		}else{
			//helper.loadImage(-1, img, null, userData.picUrl, Constant.BASE_IMAGE_PATH);
			ImageLoad.loadLogo(userData.picUrl, img, this);

		}

	}
	   public boolean isOpened(){
		   return true;
	   }
	   public void closeMenu(){

	   }

	public void setDragable(boolean dragalbe){
		mDragLayout.setDragable(dragalbe);
	}
	private long exitTime = 0 ;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(mDragLayout.isOpen()){
				mDragLayout.close();
			}else{
				if (System.currentTimeMillis() - exitTime > 2000) {
					toast("再按一次返回键退出");
					exitTime = System.currentTimeMillis();
				} else {
					MainApplication.finshApp();
					//android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			mDragLayout.openOrClose();
			}
		return super.onKeyDown(keyCode, event);
	}


	private boolean checking;
//	public void checkIn(){
//		if(!checking){
//			checking = true;
//			userService.CheckIn(UserData.getUserData().loginCode);
//			showProgress();
//		}
//
//	}
	private Handler mHandler = new Handler(this);
//	@Override
//	public boolean handleMessage(Message msg) {
//		if(msg.what == BusinessDataListener.DONE_CHECK_IN){
//			if(null != popCheckIn)
//				popCheckIn.doneCheckIn();
//			checking = false;
//			dismissProgress();
//			//int exp = ((Bundle)msg.obj).getInt("exp");
//			setScores();
//			//expUp(exp);
//			//toast("Done check In!exp:" + exp);
//		}else if(msg.what == BusinessDataListener.ERROR_CHECK_IN){
//			checking = false;
//			dismissProgress();
//			CustomDialog.showChooiceDialg(HomeActivity.this, "签到失败", msg.obj.toString(), "重签", "取消", null,
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							checkIn();
//
//						}
//					}, null);
//		}else if(msg.what == BusinessDataListener.ERROR_ALREADY_CHECK_IN){
//			checking = false;
//			dismissProgress();
//			CustomDialog.showChooiceDialg(HomeActivity.this, "签到失败", msg.obj.toString(), "朕知道了", null, null,null, null);
//		}
//		return false;
//	}

	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		if(type == BusinessDataListener.DONE_CHECK_IN){
			mHandler.obtainMessage(type, extra).sendToTarget();
		}else if(type == BusinessDataListener.DONE_USER_LOGIN ){
			dismissProgress();
				handler.post(new Runnable() {
					@Override
					public void run() {
						txtMine.setText("我的帐号");
						//点击“我的帐号”进行登录后，自动切至“我的帐号”
						if(trendToMy){
							trendToMy = false;
						showMyFrag();
						}
						//任务列表
						((TaskNewFrag)fragManager.getFragmentByType(FragType.Task)).initData();
						//刷新道具中心
						BaseFragment frag = fragManager.getFragmentByType(FragType.Tool);
						if(null != frag)
							((ToolFrag)frag).initData();
						setScores();
					}
				});
		}else if(type == BusinessDataListener.DONE_GET_SCANCOUNT){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					setScores();
				}
			});

		}

	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		if(type == BusinessDataListener.ERROR_CHECK_IN || type == BusinessDataListener.ERROR_ALREADY_CHECK_IN){
			mHandler.obtainMessage(type, des).sendToTarget();
		}else if(type == BusinessDataListener.ERROR_USER_LOGIN){
			dismissProgress();
			toast(des);
		}

	}
	@Override
	public void onOpen() {
		//setSwipeBackEnable(true);
		if(UserData.getUserData().isLogin){
			userService.getScanCount(UserData.getUserData().loginCode);
    		//是否有意见反馈
    	}
		setScores();
		//showUserGuide(R.drawable.user_guide_task_menu);
	}
	@Override
	public void onClose() {
		setSwipeBackEnable(false);

	}
	@Override
	public void onDrag(float percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
}
