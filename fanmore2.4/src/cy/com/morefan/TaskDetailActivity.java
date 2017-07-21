package cy.com.morefan;

import java.io.File;
import java.util.HashMap;



import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cy.com.morefan.AppUpdateActivity.UpdateType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.PreTaskStatusData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.TaskFrag;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.TimeRunner;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.util.TimeRunner.OnTimeRunnerListener;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.CyButton;
import cy.com.morefan.view.CyLoadingProgress;
import cy.com.morefan.view.TipDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.R.attr.type;
import static cy.com.morefan.R.id.viewPage;

@SuppressLint("SetJavaScriptEnabled")
public class TaskDetailActivity extends BaseActivity implements BusinessDataListener, Callback, BroadcastListener{
	public enum StatusType{
		/**
		 * 未转发
		 */
		Normal,
		/**
		 * 已转发，但未提交（此状态提示用户进行提交）
		 */
		Share,
		/**
		 * 提交成功
		 */
		Commit
	}
	private StatusType statusType;
	private TaskData taskData;
	private UserService userService;
	private Button btnShare;
	private MyBroadcastReceiver myBroadcastReceiver;
	private TaskService taskService;
	private PopupWindow popupWindow;
	private CyLoadingProgress layProgress;
	private boolean calCount;//是否计算次数，默认计算
	private WebView mWebView;
	private TextView txtSend;
	private TextView txtScan;
	private LinearLayout layDes;
	private ImageView imgLine;
	private CyButton btnFav;
	private TextView txtExtraDes;//闪购描述
	private LinearLayout layTop;
	private FrameLayout layWeb;
	private int topHeight;
	private boolean hasSetTopHeight;
	private boolean refreshList;//是否通过广播刷新任务列表
	private boolean isLoadWeb;

	private PreTaskStatusData statusData;
	private String advTime;
	private TimeRunner timeRunner;
	private boolean isPre;
	private boolean fromPre;
	private boolean wxNotBack;
	private boolean wxBack;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_BUY_TOOL){
			dismissProgress();
			int type = ((Bundle)msg.obj).getInt("type");
			toast("购买成功!");
			if(type == 4){//提前道具购买成功
				//checkSendCount(statusData);
			}

		}else if(msg.what == BusinessDataListener.DONE_CHECK_TASK_STATUS){
//			//状态：0：已开始  1：未开始2:已下架
			int status = ((Bundle)msg.obj).getInt("status");
			isPre = status == 1;
			if(fromPre && !isPre){
				btnShare.setVisibility(View.GONE);
			}
		}else if(msg.what == BusinessDataListener.DONE_GET_TASK_DETAIL){
			layProgress.dismiss();
			dismissProgress();
			if(!isLoadWeb){
				isLoadWeb = true;
				mWebView.loadUrl(taskData.taskPreview);
			}

			//mWebView.loadUrl("javascript:document.getElementById('xibaibai').style.height = '100dpx'");
			//小图是否已下载，若没下载则下载
			String fullPath = Constant.IMAGE_PATH_TASK + File.separator + taskData.smallImgUrl.substring(taskData.smallImgUrl.lastIndexOf("/") + 1);
			File smallFile = new File(fullPath);
			if(!smallFile.exists()){
				SyncImageLoaderHelper loader = new SyncImageLoaderHelper(this);
				loader.loadImage( taskData.smallImgUrl, Constant.IMAGE_PATH_TASK);
			}
			refreshView();


		}else if(msg.what == BusinessDataListener.ERROR_GET_TASK_DETAIL){
				dismissProgress();
				toast(msg.obj.toString());
		}else if(msg.what == BusinessDataListener.DONE_USER_LOGIN){
			dismissProgress();
			toast("登录成功!");
			//
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_LOGIN);
		}else if(msg.what == BusinessDataListener.DONE_COMMIT_SEND){
			toast("分享成功");
			statusType = StatusType.Commit;
			taskData.sendCount=taskData.sendCount+1;
			taskData.isSend=true;
			dismissProgress();
			if(!isFree()){

			}
			refreshView();
		}else if (msg.what == BusinessDataListener.ERROR_COMMIT_SEND
				|| msg.what == BusinessDataListener.ERROR_USER_LOGIN) {
			dismissProgress();
			toast(msg.obj.toString());
			if(msg.what == BusinessDataListener.ERROR_COMMIT_SEND)
				reCommit(msg.obj.toString());
		} else if (msg.what == BusinessDataListener.ERROR_RE_COMMIT_SEND) {
			dismissProgress();
			reCommit(msg.obj.toString());
		}
		return false;
	}


	private void checkSendCount(final PreTaskStatusData statusData) {
		if(statusData.forwardLimit == 0){//未达转发上限
			showPopup();
		}else{
			//是否有+1道具
			if(statusData.addOneTool == 1){//使用提示
				showTipDialog(2, 0, statusData.addOneUseTip, "使用", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
							showPopup();
					}
				});
			}else{//购买提示
				showTipDialog(2, statusData.addOneToolExp, statusData.addOneBuyTip, "购买", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//购买提前道具
						//userService.buyTool(UserData.getUserData().loginCode, 3, statusData.advanceToolExp, null, null);
						showProgress();
					}
				});

			}
		}

	}
	private void showTipDialog(int type, int exp, String advanceUseTip, String btnDes, DialogInterface.OnClickListener listener) {

		View contentView = getLayoutInflater().inflate(R.layout.custom_dialog_tool_buy, null);
		ImageView img = (ImageView) contentView.findViewById(R.id.img);
		TextView txtDes = (TextView) contentView.findViewById(R.id.txtDes);
		TextView txtValue = (TextView) contentView.findViewById(R.id.txtValue);

		img.setBackgroundResource(type == 1 ? R.drawable.tool_sendtime2 : R.drawable.tool_sendcount2);
		txtDes.setText(advanceUseTip);
		txtValue.setVisibility(exp == 0 ? View.GONE : View.VISIBLE);
		txtValue.setText("消耗:" + exp + "经验");
		CustomDialog.showChooiceDialg(this, null, null, btnDes, "取消", contentView, listener, null);
	}


	@Override
	protected void onCreate(Bundle arg0) {
        // TODO Auto-generated method stub
        super.onCreate(arg0);
        Util.isAppRunning(this);
        setContentView(R.layout.task_detail);

        initView();
        isLoadWeb = false;
        wxNotBack = false;
        statusType = StatusType.Normal;
        userService = new UserService(this);
        //mViewPager = (ViewPager) findViewById(R.id.viewPager);
        btnShare = (Button) findViewById(R.id.btnShare);
        taskData = (TaskData) getIntent().getExtras().getSerializable("taskData");
        refreshList = getIntent().getBooleanExtra("refreshList", false);
        advTime = getIntent().getStringExtra("advTime");
        fromPre = getIntent().getExtras().getBoolean("fromPre");

        taskService = new TaskService(this);
        taskService.getTaskDetail(taskData, UserData.getUserData().loginCode);
//		if(fromPre)
//			taskService.checkTaskStatus(taskData.id);
        if (!layProgress.isShowing())
            showProgress();
        myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_USER_LOGIN, MyBroadcastReceiver.ACTION_SHARE_TO_WEIXIN_SUCCESS, MyBroadcastReceiver.ACTION_SHARE_TO_QZONE_SUCCESS, MyBroadcastReceiver.ACTION_SHARE_TO_SINA_SUCCESS, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE, MyBroadcastReceiver.ACTION_WX_NOT_BACK);
        //showUserGuide(R.drawable.user_guide_task_detail);
        fromOncreate = true;

    }
	private boolean fromOncreate;
	@Override
	protected void onResume() {
		if(!fromOncreate)
			dismissProgress();
		fromOncreate = false;

		L.i(">>>onResume:" + wxNotBack);
		//微信不回调，补救提交
		if(wxNotBack){
			wxNotBack = false;
			commit();
		}

		if(mWebView!=null){
			mWebView.onResume();//mWebView.resumeTimers();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		if(mWebView!=null){
			mWebView.onPause();
		}
		super.onPause();
	}

	private void initView() {
		layTop = (LinearLayout) findViewById(R.id.layTop);
		layWeb = (FrameLayout) findViewById(R.id.layWeb);
		txtExtraDes = (TextView) findViewById(R.id.txtExtraDes);
		//btnFav = (CyButton) findViewById(R.id.btnFav);
		layDes = (LinearLayout) findViewById(R.id.layDes);
		imgLine = (ImageView) findViewById(R.id.imgLine);
		txtSend = (TextView) findViewById(R.id.txtSend);
		txtScan = (TextView) findViewById(R.id.txtScan);
		layProgress = (CyLoadingProgress) findViewById(R.id.layProgress);
		btnShare = (Button) findViewById(R.id.btnShare);
		mWebView = (WebView) findViewById(R.id.webView);
//		ViewTreeObserver vto = layTop.getViewTreeObserver();
//		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            public void onGlobalLayout() {
//            	 topHeight = layTop.getMeasuredHeight();
//            	if(topHeight != 0){
//            		layTop.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//            		 LayoutParams params = (LayoutParams) layWeb.getLayoutParams();
//                 	params.setMargins(0, topHeight, 0, 0);
//
//            	}
//
//            	//layWeb.setLayoutParams(params);
//            	L.i(">>>height1:" + topHeight);
//
//            }
//        });

		final ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.getSettings().setAllowFileAccess(true);
		mWebView.getSettings().setAppCacheEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setSaveFormData(false);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// android 5.0以上默认不支持Mixed Content
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
			mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
		}

		mWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				mWebView.setVisibility(View.VISIBLE);
					if(bar.getVisibility() == View.GONE){
						bar.setVisibility(View.VISIBLE);
					}
					bar.setProgress(progress);
					if(!hasSetTopHeight && topHeight != 0){
						L.i("progress:" + progress);
						hasSetTopHeight = !hasSetTopHeight;

					}

					if(progress == 100){
						bar.setVisibility(View.GONE);
//						mWebView.loadUrl(String.format("javascript:setXibaibaiWH(%d)", topHeight));
//						mHandler.postDelayed(new Runnable() {
//
//							@Override
//							public void run() {
//								LayoutParams params = (LayoutParams) layWeb.getLayoutParams();
//			                 	params.setMargins(0, 0, 0, 0);
//			                 	layWeb.setLayoutParams(params);
//							}
//						}, 100);

					}
			}
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
			 public void onConsoleMessage(String message, int lineNumber, String sourceID) {
			       Log.d("MyApplication", message + " -- From line "
			                            + lineNumber + " of "
			                            + sourceID);
			     }
		});

		//开启下载功能
		mWebView.setDownloadListener(new DownloadListener() {

			@Override
			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype, long contentLength) {
				L.i(">>>>url:" + url+","+contentDisposition);

				Intent intent = new Intent(TaskDetailActivity.this, AppUpdateActivity.class);
				// intent.putExtra("isForce", isForce);
				intent.putExtra("type", UpdateType.FullUpate);
				intent.putExtra("md5", "");
				intent.putExtra("url", url);
				intent.putExtra("tips", "");
				startActivity(intent);

			}
		});
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				return super.shouldOverrideUrlLoading(view, url);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
				//CommonUtil.toast(BusinessWebviewActivity.this,"Oh no!" + description);
			}
		});

		mWebView.addJavascriptInterface(new Object() {

			@JavascriptInterface
            public void clickLinkOnAndroid() {

                mHandler.post(new Runnable() {

                    public void run() {

                        //btn.setVisibility(View.VISIBLE);


                    }

                });

            }

        }, "android");

		mWebView.setOnTouchListener ( new View.OnTouchListener () {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction ()) {
                case MotionEvent.ACTION_DOWN :
                case MotionEvent.ACTION_UP :
                    if (!v.hasFocus ()) {
                       v.requestFocus ();
                    }
                    break ;
                }
                return false ;
            }
        });

	}


	private ImageView imgWeiXin;
	private ImageView imgSina;
	private ImageView imgQzone;

	@SuppressWarnings("deprecation")
	private void initPopupWindow() {
		LayoutInflater mInflater = LayoutInflater.from(this);
		View layout = mInflater.inflate(R.layout.pop_share, null);
		layout.findViewById(R.id.layAll).getBackground().setAlpha(220);
		imgWeiXin = (ImageView) layout.findViewById(R.id.imgWeiXin);
		imgSina = (ImageView) layout.findViewById(R.id.imgSina);
		imgQzone = (ImageView) layout.findViewById(R.id.imgQzone);
		popupWindow = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//设置PopupWindow显示和隐藏时的动画
    	popupWindow.setAnimationStyle(R.style.AnimationPop);
	}
	 //显示菜单
    private void showPopup(){
    	if(popupWindow == null)
			initPopupWindow();
    	updatePopView();
           //设置位置
    	//popupWindow.showAsDropDown(btnShare,0,-250);
    	popupWindow.showAtLocation(btnShare, Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,DensityUtil.dip2px(this, 66)); //设置在屏幕中的显示位置
    }
    private void updatePopView() {
    	imgWeiXin.setBackgroundResource(taskData.channelIds.contains(ShareUtil.CHANNEL_WEIXIN + "") ? R.drawable.share_ico_weixin : R.drawable.share_ico_weixin);
    	imgSina.setBackgroundResource(taskData.channelIds.contains(ShareUtil.CHANNEL_SINA + "") ? R.drawable.share_ico_sina : R.drawable.share_ico_sina);
    	imgQzone.setBackgroundResource(taskData.channelIds.contains(ShareUtil.CHANNEL_QZONE + "") ? R.drawable.share_ico_qzone : R.drawable.share_ico_qzone);

    	//判断渠道是否支持
    	if(!BusinessStatic.getInstance().CHANNEL_LIST.contains(ShareUtil.CHANNEL_WEIXIN + ""))
    		imgWeiXin.setBackgroundResource(R.drawable.share_ico_weixin_off);
    	if(!BusinessStatic.getInstance().CHANNEL_LIST.contains(ShareUtil.CHANNEL_SINA + ""))
    		imgSina.setBackgroundResource(R.drawable.share_ico_sina_off);
    	if(!BusinessStatic.getInstance().CHANNEL_LIST.contains(ShareUtil.CHANNEL_QZONE + ""))
    		imgQzone.setBackgroundResource(R.drawable.share_ico_qzone_off);
    }

	@Override
	public void finish() {
		if(statusType == StatusType.Share){
			CustomDialog.showChooiceDialg(this, null, "您的转发任务接交并未成功,确定离开吗?", "再次提交", "离开", null,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							commit();
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							setResult();

						}
					});
		}else{
			setResult();
		}

		//
	}

	private void setResult(){
		if(refreshList){
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_REFRESH_TASK_LIST);
		}else{
			// 更新转发状态
			Intent intent = new Intent();
			intent.putExtra("taskData", taskData);
			setResult(Constant.RESULT_CODE_TASK_STATUS_CHANGE, intent);
		}
		super.finish();


	}
	private void refreshView() {
		if(this.isFinishing())
			return;
		txtSend.setText(taskData.awardSend);
		txtScan.setText(taskData.awardScan);
		//转发按钮文字显示
		//btnShare.setVisibility(taskData.sendstatus==2||(taskData.sendstatus == 0 && fromPre) ? View.VISIBLE :View.GONE);
		btnShare.setVisibility(taskData.ShowTurnButton==1? View.VISIBLE:View.GONE);


		if(!TextUtils.isEmpty(advTime) && !TextUtils.isEmpty(taskData.curTime)){
			long advLTime = TimeUtil.FormatterTimeToLong("yyy-MM-dd HH:mm:ss", advTime);
			L.i(">>>>>>Time:" + advLTime + "," + taskData.curTime);
			long curLTime = TimeUtil.FormatterTimeToLong("yyy-MM-dd HH-mm-ss", taskData.curTime);
			if(curLTime < advLTime){
				timeRunner = new TimeRunner(advLTime - curLTime , new OnTimeRunnerListener() {
					@Override
					public void onTimeRunnerBack(final String timeDes) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
//								btnBuySend.setText(timeDes+ "后转发");
//								btnShare.setText(timeDes + "后转发");
							}
						});
					}

					@Override
					public void onTimeRunnerFinish() {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								advTime = null;
								refreshView();
							}
						});
					}
				});
				timeRunner.start();

			}
		}
		//mWebView.loadUrl(taskData.taskPreview);
		layDes.setVisibility(taskData.flagShowSend == 0 ? View.GONE : View.VISIBLE);
		imgLine.setVisibility(taskData.flagShowSend == 0 ? View.GONE : View.VISIBLE);
		if(taskData.type == 1000300  && !TextUtils.isEmpty(taskData.extraDes)){
				txtExtraDes.setVisibility(View.VISIBLE);
				txtExtraDes.setText(taskData.extraDes);
		}


	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type,extra).sendToTarget();

	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type ,des).sendToTarget();
	}
	public void refreshTaskData(TaskData taskData){
		this.taskData = taskData;
	}

	private CustomDialog mDialog;
	public void onClickBottomTab(View view){
		switch (view.getId()) {
		case R.id.btnShare:


			if(!UserData.getUserData().isLogin){
				Intent intentlogin = new Intent(TaskDetailActivity.this, MoblieLoginActivity.class);
				startActivity(intentlogin);
			}else{
				if( !UserData.getUserData().ignoreJudgeEmulator && BusinessStatic.getInstance().ISEMULATOR){
					toast("模拟器不支持该操作!");
					return;
				}
                if(BusinessStatic.getInstance().disasterFlag==1){
                    copy();
                }else {
                    //share();
					popAskInfo();
                }





			}

			break;

		default:
			break;
		}
	}

	/**
	 * 是否为无偿转发
	 * @return
	 */
private boolean isFree(){
	//status==9联盟任务已下架
	return (Double.parseDouble(taskData.lastScore) == 0 || taskData.status == 8 ) && taskData.channelIds.size() != 3;
}
private void share(){

	String imgUrl = taskData.smallImgUrl;
	String shareDes =taskData.taskName;
	String shareUrl =taskData.content;
	String fullPath1 = Constant.IMAGE_PATH_TASK + File.separator + taskData.smallImgUrl.substring(taskData.smallImgUrl.lastIndexOf("/") + 1);
	OnekeyShare oks = new OnekeyShare();

	//关闭sso授权
	oks.disableSSOWhenAuthorize();


	// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
	oks.setTitle(shareDes);
	// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
	oks.setTitleUrl(shareUrl);
	// text是分享文本，所有平台都需要这个字段
	oks.setText(shareDes);
	// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
	oks.setImageUrl(imgUrl);//确保SDcard下面存在此张图片
	// url仅在微信（包括好友和朋友圈）中使用
	oks.setUrl(shareUrl);
	// comment是我对这条分享的评论，仅在人人网和QQ空间使用
	oks.setComment("");
	// site是分享此内容的网站名称，仅在QQ空间使用
	oks.setSite(getString(R.string.app_name));
	// siteUrl是分享此内容的网站地址，仅在QQ空间使用
	oks.setSiteUrl(shareUrl);

// 启动分享GUIfem
	oks.show(this);
}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}



	public void onClickButton(View view){
		switch (view.getId()) {
		case R.id.btnBack:
			finish();
			break;
//		case R.id.btnFav:
//			if(!UserData.getUserData().isLogin){
//				userLogin();
//				return;
//			}
//			userService.userFavOrNotStore(UserData.getUserData().loginCode, taskData.storeId, taskData.isFav);
//			showProgress();
//			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		if(timeRunner != null)
			timeRunner.stop();
		dismissProgress();
		myBroadcastReceiver.unregisterReceiver();

		if(mWebView!=null) {
			mWebView.destroy();
		}

		super.onDestroy();
	}
	private int channleType;
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.Login){//用户注册成功
			taskService.getTaskDetail(taskData, UserData.getUserData().loginCode);
			showProgress();
		}else if(type == ReceiverType.WXNotBack){
			channleType = ShareUtil.CHANNEL_WEIXIN;
			wxNotBack = true;
			L.i(">>>onFinishReceiver:" + wxNotBack);
		}else if(type == ReceiverType.ShareToWeixinSuccess){
			channleType = ShareUtil.CHANNEL_WEIXIN;
			commit();
		}else if(type == ReceiverType.ShareToSinaSuccess){
			channleType = ShareUtil.CHANNEL_SINA;
			commit();
//			userService.commitSend(taskData.id,UserData.getUserData().loginCode, ShareUtil.CHANNEL_SINA);
//			showProgress();
		}else if(type == ReceiverType.ShareToQzoneSuccess){
			channleType = ShareUtil.CHANNEL_QZONE;
			commit();
//			userService.commitSend(taskData.id,UserData.getUserData().loginCode, ShareUtil.CHANNEL_QZONE);
//			showProgress();
		}else if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}

	}
	private void reCommit(String msg){
		CustomDialog.showChooiceDialg(TaskDetailActivity.this, "提交失败", msg, "重试", "取消", null,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						userService.commitSend(taskData.id,UserData.getUserData().loginCode, 1);
//						showProgress();
						commit();
					}
				}, null);
	}

	private void commit() {
		statusType = StatusType.Share;
		userService.commitSend(taskData.id,UserData.getUserData().loginCode, channleType);
		showProgress();

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		super.onActivityResult(requestCode, resultCode, arg2);
		if(resultCode == 1){
			if(taskData.isSend){
				copy();
			}else{
				channleType = ShareUtil.CHANNEL_WEIXIN;
				commit();
			}

		}
	}

	private void copy() {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(taskData.content);
        toast("复制链接 转发到朋友圈/好友");

	}


    public void shareCompleteToSave(){
		//test save
		//命名格式 userName_lastId
		String idName = UserData.getUserData().userName + "_lastId";
		String timeName = UserData.getUserData().userName + "_lastTime";
		SPUtil.saveIntToSpByName(this, Constant.SP_NAME_NORMAL, idName, taskData.id);
		SPUtil.saveLongToSpByName(this, Constant.SP_NAME_NORMAL, timeName, System.currentTimeMillis());
	}

	/***
	 *  分享前先弹出提示框
	 */
	private void popAskInfo(){
		boolean showPop = ((MainApplication)getApplication()).readShareTipDialog();
		if(!showPop){
			share();
			return;
		}


		String content="转发给好友需点击返回App才能计入转发。\r\n已转发文章再次分享不计入转发。";

		TipDialog.show(this, "提示", content, "知道了", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				share();
			}
		}, new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				((MainApplication)TaskDetailActivity.this.getApplication()).writeShareTipDialog(!b);
			}
		});
	}
}
