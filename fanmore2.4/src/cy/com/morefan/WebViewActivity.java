package cy.com.morefan;


import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cy.com.morefan.R;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.TempPushMsgData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.view.CyButton;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends BaseActivity implements BroadcastListener{

	private WebView webView;
	private ProgressBar bar;
	private TextView txtTitle;
	private MyBroadcastReceiver myBroadcastReceiver;
	private String imgPath;
	private String imgUrl = "http://task.fanmore.cn/images/28def407415841a7ada5a0b0377895e7_104X104.jpg";
	private boolean isSend;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		L.i("webview onCreateView");
		setContentView(R.layout.web_view);
		imgPath = Constant.IMAGE_PATH_STORE + "/share_ico.png";

		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

		//url = "http://www.baidu.com";

		webView = (WebView) findViewById(R.id.webView);
		bar = (ProgressBar) findViewById(R.id.progressBar);
		txtTitle = (TextView) findViewById(R.id.txtTitle);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);

		 webView.getSettings().setUseWideViewPort(true);
		 webView.getSettings().setLoadWithOverviewMode(true);
		 CyButton btnShare = (CyButton) findViewById(R.id.btnShare);

//		 Message msg = new Message();
//		 //the message to be dispatched with the result of the request. The message data contains three keys.
//		 //"url" returns the anchor's href attribute.
//		 //"title" returns the anchor's text. "src" returns the image's src attribute.
//		 Bundle data = new Bundle();
//		 data.putString("url", "shoutumimi");
//		 msg.setData(data);
//		 webView.requestFocusNodeHref(msg);
//		DisplayMetrics metrics = new DisplayMetrics();
//	     getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//	     int mDensity = metrics.densityDpi;
//
//	     if (mDensity == 120) {
//	    	 webView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
//	          }else if (mDensity == 160) {
//	        	  webView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
//	          }else if (mDensity == 240) {
//	        	  webView.getSettings().setDefaultZoom(ZoomDensity.FAR);
//	          }

		String url = getIntent().getExtras().getString("url");
		L.i("url:" + url);
		String title = getIntent().getExtras().getString("title");
		boolean isShare = getIntent().getExtras().getBoolean("isShare");
		boolean disasterFlag = getIntent().getExtras().getBoolean("disasterFlag");
		isSend = getIntent().getExtras().getBoolean("isSend");
		btnShare.setVisibility(disasterFlag ? View.VISIBLE : View.GONE);
		if(isShare)
			findViewById(R.id.layAll).setVisibility(View.VISIBLE);
		txtTitle.setText(title);
		webView.loadUrl(url);
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
					webView.setVisibility(View.VISIBLE);
					if(bar.getVisibility() == View.GONE){
						bar.setVisibility(View.VISIBLE);
					}
					bar.setProgress(progress);
					if(progress == 100){
						bar.setVisibility(View.GONE);
					}
			}
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {

				if(Constant.PRE_JUMP_FLAG.startsWith(url)){
					Intent intentDetail = new Intent(WebViewActivity.this, TaskDetailActivity.class);
					intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					TaskData taskData = new TaskData();
					taskData.id = TempPushMsgData.getIns().taskId;
					intentDetail.putExtra("taskData", taskData);
					intentDetail.putExtra("refreshList", true);
					startActivity(intentDetail);
					finish();
					return;
				}
				L.i(">>>>>>>>>start:" + url);
				super.onPageStarted(view, url, favicon);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
				//CommonUtil.toast(BusinessWebviewActivity.this,"Oh no!" + description);
			}
		});




	}
	public void onResume() {
		super.onResume();
		dismissProgress();
	};
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btnShare:
			if (isSend)
				toast("复制成功!");
			setResult(1);
			finish();
			break;
		case R.id.layWeiXin:
			showProgress();
			Platform platform1 = new WechatMoments();
			wx(this,  UserData.getUserData().shareDes, imgPath, UserData.getUserData().shareContent, platform1);
			//ShareUtil.share2WeiXin(this, UserData.getUserData().shareDes, imgPath, UserData.getUserData().shareContent);
			break;
		case R.id.layQQ:
			//只能分享网络图片
			showProgress();
			//ShareUtil.share2Qzone( this, UserData.getUserData().shareDes, imgUrl, UserData.getUserData().shareContent);
			break;
		case R.id.layXinLang:
			showProgress();
			//ShareUtil.share2Sina(this, UserData.getUserData().shareDes, imgPath, UserData.getUserData().shareContent);
			break;

		default:
			break;
		}

	}
	protected void wx(final Context context , String Title ,String imgUrl,String shareUrl,Platform platform ){
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

	@Override
	protected void onDestroy() {
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}

	}
}
