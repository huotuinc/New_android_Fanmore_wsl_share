package cy.com.morefan;

import java.io.File;
import java.util.HashMap;

import com.sina.weibo.sdk.openapi.models.User;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cy.com.morefan.AppUpdateActivity.UpdateType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.PreTaskStatusData;
import cy.com.morefan.bean.TaskData;
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
import cy.com.morefan.view.VerifyView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class TaskDetailActivity extends BaseActivity implements BusinessDataListener, Callback, BroadcastListener, OnClickListener{
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
	private TextView btnBuySend;
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
				checkSendCount(statusData);
			}

		}else if(msg.what == BusinessDataListener.DONE_CHECK_TASK_STATUS){
//			//状态：0：已开始  1：未开始2:已下架
//			int status = ((Bundle)msg.obj).getInt("status");
//			isPre = status == 1;
//			if(fromPre && !isPre){
//				btnBuySend.setVisibility(View.GONE);
//				btnShare.setVisibility(View.GONE);
//			}
		}else if(msg.what == BusinessDataListener.ERROR_BUY_TOOL){
			dismissProgress();
			toast(msg.obj.toString());
		}else if(msg.what == BusinessDataListener.DONE_CHECK_PRE_TASK_STATUS){
			dismissProgress();
			/**
			 * taskType	0不可转发;1提前转发;2正常转发
			 * forwardLimit	是否达到转发次数上限：0未;1已达到
			 * advanceTool	提前转发道具0无;1有
 			 * advanceUseTip	提前转发道具使用提示
			 * advanceBuyTip	提前转发道具购买提示
			 * addOneTool	+1道具0无;1有
			 * addOneUseTip	+1道具使用提示
			 * addOneBuyTip	＋1道具购买提示
			 */
			if(statusData.taskType == 0){
				String des = fromPre ? btnBuySend.getTag().toString() + "后,使用火眼金睛可提前转发该任务!": "该任务暂不支持转发!";
				toast(des);
			}else if(statusData.taskType == 1){
				//是否有提前道具
				if(statusData.advanceTool == 1){//使用提示
					showTipDialog(1, 0, statusData.advanceUseTip, "使用", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(statusData.forwardLimit == 0){//未达转发上限
								showPopup();
							}else{
								checkSendCount(statusData);
							}
						}
					});
				}else{//购买提示
						showTipDialog(1, statusData.advanceToolExp, statusData.advanceBuyTip, "购买", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//购买提前道具
								userService.buyTool(UserData.getUserData().loginCode, 4, statusData.advanceToolExp, null, null);
								showProgress();
							}
						});
						}
			}else if(statusData.taskType == 2){
					checkSendCount(statusData);
			}


		}else if(msg.what == BusinessDataListener.ERROR_CHECK_PRE_TASK_STATUS){
			dismissProgress();
			toast(msg.obj.toString());
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
			statusType = StatusType.Commit;
			int type = ((Bundle)msg.obj).getInt("type");

			dismissProgress();
			if(!isFree()){
				if(BusinessStatic.getInstance().disasterFlag){
					toast("复制成功!转发成功!明日查看收益!");
					copy();
				}else
					toast("转发成功!明日查看收益!");
			}

			taskData.isSend = true;
			refreshView();
			taskService.getTaskDetail(taskData, UserData.getUserData().loginCode);
			showProgress();
			//用完信息改变,且计算转发次数,(闪购,联盟不计算次数)
			if( taskData.channelIds.size() == 0 && taskData.flagLimitCount == 1 && calCount && taskData.type != 1000300 && taskData.type != 1001000){
				shareCompleteToSave();
				UserData.getUserData().completeTaskCount += 1;
				UserData.getUserData().sendCount += 1;
				MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE);
			}
			taskData.channelIds.add(type + "");
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
//		else if(msg.what == BusinessDataListener.DONE_FAV){
//			dismissProgress();
//			toast("收藏成功!");
//			taskData.isFav = true;
//			btnFav.setBackgroundResource(R.drawable.fav_on);
//			//用完信息改变
//			UserData.getUserData().favCount += 1;
//			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE);
//		}else if(msg.what == BusinessDataListener.DONE_CANCEL_FAV){
//			dismissProgress();
//			toast("取消收藏成功!");
//			taskData.isFav = false;
//			btnFav.setBackgroundResource(R.drawable.fav_off);
//			//用完信息改变
//			UserData.getUserData().favCount -= 1;
//			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE);
//		}
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
						userService.buyTool(UserData.getUserData().loginCode, 3, statusData.advanceToolExp, null, null);
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
		if(!layProgress.isShowing())
			showProgress();
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_USER_LOGIN, MyBroadcastReceiver.ACTION_SHARE_TO_WEIXIN_SUCCESS,MyBroadcastReceiver.ACTION_SHARE_TO_QZONE_SUCCESS, MyBroadcastReceiver.ACTION_SHARE_TO_SINA_SUCCESS, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE, MyBroadcastReceiver.ACTION_WX_NOT_BACK);
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
		super.onResume();
	}


	private void initView() {
		layTop = (LinearLayout) findViewById(R.id.layTop);
		layWeb = (FrameLayout) findViewById(R.id.layWeb);
		btnBuySend = (TextView) findViewById(R.id.btnBuySend);
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

		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
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
		layout.findViewById(R.id.layWeiXin).setOnClickListener(this);
		layout.findViewById(R.id.layQQ).setOnClickListener(this);
		layout.findViewById(R.id.layXinLang).setOnClickListener(this);
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
    	imgWeiXin.setBackgroundResource(taskData.channelIds.contains(ShareUtil.CHANNEL_WEIXIN + "") ? R.drawable.share_ico_weixin_off : R.drawable.share_ico_weixin);
    	imgSina.setBackgroundResource(taskData.channelIds.contains(ShareUtil.CHANNEL_SINA + "") ? R.drawable.share_ico_sina_off : R.drawable.share_ico_sina);
    	imgQzone.setBackgroundResource(taskData.channelIds.contains(ShareUtil.CHANNEL_QZONE + "") ? R.drawable.share_ico_qzone_off : R.drawable.share_ico_qzone);

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
			CustomDialog.showChooiceDialg(this, null, "您的转发任务接交并未成功,离开后将无法得到任务收益!确定离开吗?", "再次提交", "离开", null,
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
		String des = taskData.sendstatus == 1 || (taskData.sendstatus == 0 && fromPre) ? "火眼金睛" : "立即转发";
		btnShare.setBackgroundResource(taskData.sendstatus == 1 || (taskData.sendstatus == 0 && fromPre) ? R.drawable.shape_yellow_sel : R.drawable.btn_red_sel);

		btnBuySend.setText(des);
		btnShare.setText(des);
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
								btnBuySend.setTag(timeDes);
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


		//转发按钮显示与否
		if(taskData.flagShowSend == 1){
			btnShare.setVisibility(View.VISIBLE);

			//闪购时，转发在右上角
			if(taskData.type == 1000300){
				//showUserGuide(R.drawable.user_guide_mall_send);
				 btnShare.setVisibility(View.GONE);
				 btnBuySend.setVisibility(View.VISIBLE);
				 if(taskData.channelIds.size() == 3){
					 //btnBuySend.setVisibility(View.GONE);
						btnBuySend.setText("已转发");
						btnBuySend.setClickable(false);
					}
					//更新pop上状态
					if(popupWindow != null && popupWindow.isShowing())
						updatePopView();
				 return;
			}
			UserData userData = UserData.getUserData();
			if(!userData.isLogin)
				return;

			calCount = true;
			btnShare.setClickable(true);
//			if(userData.completeTaskCount >= userData.totalTaskCount && taskData.channelIds.size() == 0){
//				btnShare.setBackgroundResource(R.drawable.shape_yellow_sel);
//				//if(!fromPre)
//				btnShare.setText(taskData.sendstatus == 1 || (taskData.sendstatus == 0 && fromPre) ? "火眼金睛" : "再来一发");
//				//不计算转发次数的，显示立即转发
//				if(taskData.type == 1000300 || taskData.type == 1001000){
//					btnShare.setText("立即转发");
//					btnShare.setBackgroundResource(R.drawable.btn_red_sel);
//				}
////				if(taskData.sendstatus == 1)
////					btnShare.setText("再来一发");
//				//btnShare.setClickable(false);
//			}
			if(Double.parseDouble(taskData.lastScore) == 0){
				btnShare.setBackgroundResource(R.drawable.btn_gray);
				btnShare.setText("抢光了");
				btnShare.setClickable(false);
			}
			//无偿转发，任务为抢光了，且未进行过转发
			if(isFree()){
				btnShare.setBackgroundResource(R.drawable.btn_green_sel);
				calCount = false;
				btnShare.setText("无偿转发");
				btnShare.setClickable(true);
			}
			if(taskData.channelIds.size() == 3){
				btnShare.setBackgroundResource(R.drawable.btn_gray);
				btnShare.setText("已转发");
				btnShare.setClickable(false);
			}
			//更新pop上状态
			if(popupWindow != null && popupWindow.isShowing())
				updatePopView();
		}else{
			 btnBuySend.setVisibility(View.GONE);
			btnShare.setVisibility(View.GONE);
		}
		//预告过来，已上线的/未登录 不显示按钮
		if(fromPre && taskData.sendstatus == 2 ){
			btnBuySend.setVisibility(View.GONE);
			btnShare.setVisibility(View.GONE);
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
		case R.id.btnBuySend:


			if(!UserData.getUserData().isLogin){
				Intent intentlogin = new Intent(TaskDetailActivity.this, LoginActivity.class);
				startActivity(intentlogin);
			}else{
				if( !UserData.getUserData().ignoreJudgeEmulator && BusinessStatic.getInstance().ISEMULATOR){
					toast("模拟器不支持该操作!");
					return;
				}
				//帐号切换进行验证
				String preUserName = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_PRE_USERNAME);
				if(!TextUtils.isEmpty(preUserName) && !preUserName.equals(UserData.getUserData().userName)){
					View view2 = LayoutInflater.from(this).inflate(R.layout.verify, null);
					final VerifyView mVerifyView = (VerifyView) view2.findViewById(R.id.txt);
					final EditText edt = (EditText) view2.findViewById(R.id.edt);
					final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mDialog = CustomDialog.showChooiceDialg(this, null, null, "确定", "换一个", view2,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									//验证是否通过
									try {
										int vaule = Integer.valueOf(edt.getText().toString().trim());
										if(vaule == mVerifyView.getResult()){
											SPUtil.saveStringToSpByName(TaskDetailActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_PRE_USERNAME, UserData.getUserData().userName);
											mDialog.setDialogDismissAfterClick();
											share();
											imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
										}else{
											toast("验证错误!请重试");
										}
									} catch (Exception e) {
										toast("验证错误!请重试");
									}
								}
							} ,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									mVerifyView.refresh();
								}
							});
					mDialog.setDialogShowAfterClick();


					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
						}
					}, 10);

				}else{
					share();
				}





			}

			//test
//			userService.commitSend(taskData.id,UserData.getUserData().loginCode, 1);
//			showProgress();

			break;
//		case R.id.btnAttention:
//			if(TextUtils.isEmpty(taskData.openId)){
//				T.show(this, "暂无微信公众号!");
//				return;
//			}
//			Util.WxAttention(this, taskData.openId);
//			break;

		default:
			break;
		}
//		//test
//		throw new NullPointerException();
//		//test end

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
	//先判断该任务是否有被转发过，已转发过，则不进行时间间隔判断
	//无偿转发不计算时间,闪购不计算,联盟
	if(taskData.channelIds.size() == 0 && !isFree() && taskData.type != 1000300 && taskData.type != 1001000){
		//命名格式 userName_lastId
		String idName = UserData.getUserData().userName + "_lastId";
		String timeName = UserData.getUserData().userName + "_lastTime";

		int id = SPUtil.getIntFromSpByName(this, Constant.SP_NAME_NORMAL, idName);
		/**
		 * 每个任务转发之间必须大于10分钟
		 */
		if(taskData.id != id && id != 0){
			//命名格式 userName_lastTime

			long time = SPUtil.getLongToSpByName(this, Constant.SP_NAME_NORMAL, timeName);
			int seconds = (int) ((System.currentTimeMillis() - time)/1000);
			if(seconds < BusinessStatic.getInstance().TASK_TIME_LAG){
				String msg = String.format("需要间隔%s才能转发下一个，请再等待%s", TimeUtil.getTimeDes(BusinessStatic.getInstance().TASK_TIME_LAG), TimeUtil.getTimeDes(BusinessStatic.getInstance().TASK_TIME_LAG - seconds));
				toast(msg);
//				String[] des = getResources().getStringArray(R.array.task_time_des);
//				int minute = (BusinessStatic.TASK_TIME_LAG - seconds) / 60;
//				minute = minute == 0 ? 1 : minute;
//				int index = (int) (Math.random()*des.length);
//				toast(String.format(des[index] ,minute ));
				return;
			}

		}
	}
	if(taskData.type == 1001000 || (taskData.type == 1000300 && Double.parseDouble(taskData.lastScore) < 1)){//闪购栏目不做限制
		showPopup();
	}else{
		//查询是否使用道具
		statusData = new PreTaskStatusData();
		taskService.checkPreTaskStatus(UserData.getUserData().loginCode, taskData.id, statusData);
		showProgress();
	}

	//showPopup();
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

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layWeiXin:
			//微信灾难
			if(BusinessStatic.getInstance().disasterFlag){
				Intent intentWeb = new Intent(this, WebViewActivity.class);
				intentWeb.putExtra("url", BusinessStatic.getInstance().disasterUrl);
				intentWeb.putExtra("title", "转发朋友圈");
				intentWeb.putExtra("disasterFlag", true);
				intentWeb.putExtra("isSend", taskData.isSend);
				startActivityForResult(intentWeb, 0);
				return;
			}
			if(!BusinessStatic.getInstance().CHANNEL_LIST.contains(ShareUtil.CHANNEL_WEIXIN + "")){
				toast("Sorry!朋友圈暂不支持转发!");
				return;
			}
			if(taskData.channelIds.contains(ShareUtil.CHANNEL_WEIXIN + "")){
				toast("朋友圈已转发!");
				return;
			}
			showProgress();
			String fullPath = Constant.IMAGE_PATH_TASK + File.separator + taskData.smallImgUrl.substring(taskData.smallImgUrl.lastIndexOf("/") + 1);
			Platform platform1 = new WechatMoments(this);
			wx(this,  taskData.taskName, fullPath, taskData.content + getResources().getString(R.string.channel_weixin), platform1);
			ShareUtil.share2WeiXin(this, taskData.taskName, fullPath, taskData.content + getResources().getString(R.string.channel_weixin));
			break;
		case R.id.layQQ:
			if(!BusinessStatic.getInstance().CHANNEL_LIST.contains(ShareUtil.CHANNEL_QZONE + "")){
				toast("Sorry!QQ空间暂不支持转发!");
				return;
			}
			if(taskData.channelIds.contains(ShareUtil.CHANNEL_QZONE + "")){
				toast("QQ空间已转发!");
				return;
			}
			showProgress();
			ShareUtil.share2Qzone(this, taskData.taskName, taskData.smallImgUrl, taskData.content + getResources().getString(R.string.channel_qzone));
			break;
		case R.id.layXinLang:
			if(!BusinessStatic.getInstance().CHANNEL_LIST.contains(ShareUtil.CHANNEL_SINA + "")){
				toast("Sorry!新浪微博暂不支持转发!");
				return;
			}
			if(taskData.channelIds.contains(ShareUtil.CHANNEL_SINA + "")){
				toast("新浪微博已转发!");
				return;
			}
			showProgress();
			String fullPath2 = Constant.IMAGE_PATH_TASK + File.separator + taskData.smallImgUrl.substring(taskData.smallImgUrl.lastIndexOf("/") + 1);
			ShareUtil.share2Sina(this, taskData.taskName, fullPath2, taskData.content + getResources().getString(R.string.channel_sina));
			break;

		default:
			break;
		}


	}
    protected void wx(final Context context , String Title ,String imgUrl, final String shareUrl,Platform platform ){
        Platform.ShareParams sp = new Platform.ShareParams();
        sp.setShareType(Platform.SHARE_WEBPAGE);
        sp.setTitle(Title);
        sp.setText(Title);
        sp.setUrl(shareUrl);
        //sp.setImageUrl(imgUrl);
        sp.setImagePath(imgUrl);
        // platform = new Wechat(context);
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                String msg = "";
                if (platform.getName().equals(Wechat.NAME)) {
                    ToastUtil.show(context, "微信分享成功");
                } else if (platform.getName().equals(WechatMoments.NAME)) {
                    ToastUtil.show(context, "微信朋友圈分享成功");
					MyBroadcastReceiver.sendBroadcast( context , MyBroadcastReceiver.ACTION_SHARE_TO_WEIXIN_SUCCESS);

				}
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                if (platform.getName().equals(Wechat.NAME)) {
                    ToastUtil.show(context, "微信分享失败");
                } else if (platform.getName().equals(WechatMoments.NAME)) {
                    ToastUtil.show(context, "微信朋友圈分享失败");
                }
            }

            @Override
            public void onCancel(Platform platform, int i) {
                if (platform.getName().equals(Wechat.NAME)) {
                    ToastUtil.show(context, "取消微信分享");
                } else if (platform.getName().equals(WechatMoments.NAME)) {
                    ToastUtil.show(context, "取消微信朋友圈分享");
                }
            }
        });
        platform.share(sp);

    }


    public void shareCompleteToSave(){
		//test save
		//命名格式 userName_lastId
		String idName = UserData.getUserData().userName + "_lastId";
		String timeName = UserData.getUserData().userName + "_lastTime";
		SPUtil.saveIntToSpByName(this, Constant.SP_NAME_NORMAL, idName, taskData.id);
		SPUtil.saveLongToSpByName(this, Constant.SP_NAME_NORMAL, timeName, System.currentTimeMillis());
	}
}
