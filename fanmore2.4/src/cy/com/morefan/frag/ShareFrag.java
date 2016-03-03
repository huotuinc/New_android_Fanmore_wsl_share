package cy.com.morefan.frag;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import cindy.android.test.synclistview.ImageUtil;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.R;
import cy.com.morefan.TaskActivity;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.ToastUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class ShareFrag extends BaseFragment implements OnClickListener{
	private static ShareFrag frag;
	private View mRootView;
	private WebView webView;
	private ProgressBar bar;
	private TextView btn;
	private Button btnShare;
	private Handler mHandler = new Handler();

	public static ShareFrag newInstance(){
		if(frag == null)
			frag = new ShareFrag();
		return frag;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//InputStream in  = this.getResources().openRawResource(R.raw.share_ico);
		imgPath = Constant.IMAGE_PATH_STORE + "/share_ico.png";
		//ImageUtil.saveInputStreanToFile(in, imgPath);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_share, container, false);
		btn = (TextView) mRootView.findViewById(R.id.btnGoBack);
		btn.setOnClickListener(this);
		btnShare = (Button) mRootView.findViewById(R.id.btnShare);
		btnShare.setOnClickListener(this);




		webView = (WebView) mRootView.findViewById(R.id.webView);
		//不使用缓存：
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		bar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
//		L.i("url:" + url);
		webView.loadUrl(UserData.getUserData().shareContent);
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
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
			}
		});

		webView.addJavascriptInterface(new Object() {

			@JavascriptInterface
            public void clickLinkOnAndroid() {

                mHandler.post(new Runnable() {

                    public void run() {
                        btn.setVisibility(View.VISIBLE);

                    }

                });

            }

        }, "android");



		return mRootView;
	}
	@Override
	public void onReshow() {
		webView.loadUrl(UserData.getUserData().shareContent);
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}
//	@Override
//	public void onClickTitleLeft() {
//		if(getActivity() != null)
//			((HomeActivity)getActivity()).openOrCloseMenu();
//
//	}
//	@Override
//	public void onClickTitleRight() {
//
//	}
//	@Override
//	public void onClickTitleMiddle() {
//
//	}
	String imgPath;
	String imgUrl = "http://task.fanmore.cn/images/28def407415841a7ada5a0b0377895e7_104X104.jpg";
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGoBack:
			btn.setVisibility(webView.canGoBack() ? View.VISIBLE : View.GONE);
			if(webView.canGoBack()){
				webView.goBack();
				btn.setVisibility(webView.canGoBack() ? View.VISIBLE : View.GONE);
			}else{
				if(getActivity() != null)
					((HomeActivity)getActivity()).toast("已是首页!");
			}

			break;
		case R.id.btnShare:
			showPopup();
			break;
		case R.id.layWeiXin:
			showProgress();
			Platform platform1 = new WechatMoments(getActivity());
			wx( getActivity(), UserData.getUserData().shareDes, imgUrl,UserData.getUserData().shareContent,platform1);
			break;
		case R.id.layQQ:
			//只能分享网络图片
			showProgress();
			ShareUtil.share2Qzone( getActivity(), UserData.getUserData().shareDes, imgUrl, UserData.getUserData().shareContent);
			break;
		case R.id.layXinLang:
			showProgress();
			Platform platform2 = new SinaWeibo(getActivity());
			sinaWeibo(getActivity(), UserData.getUserData().shareDes, imgUrl,UserData.getUserData().shareContent,platform2);
			break;


		default:
			break;
		}

	}


	private void sinaWeibo(final Context context , String Title ,String imgUrl,String shareUrl,Platform platform)
	{
		Platform.ShareParams sp = new Platform.ShareParams ( );
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setText(Title +shareUrl);
		sp.setImageUrl(imgUrl);
		Platform sinaWeibo = ShareSDK.getPlatform(context, SinaWeibo.NAME);
		sinaWeibo.setPlatformActionListener ( new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
				String msg = "";

				ToastUtil.show(context, "微博分享成功");

			}

			@Override
			public void onError(Platform platform, int i, Throwable throwable) {

				ToastUtil.show(context, "微博分享失败");

			}

			@Override
			public void onCancel(Platform platform, int i) {

				ToastUtil.show(context, "取消微博分享");

			}
		});
		//执行分享
		sinaWeibo.share ( sp );
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
	public void onHiddenChanged(boolean hidden) {
		if(hidden && popupWindow != null)
			popupWindow.dismiss();
	};
	public void onResume() {
		super.onResume();
		dismissProgress();
	};

	private PopupWindow popupWindow;
	private void initPopupWindow() {
		LayoutInflater mInflater = LayoutInflater.from(getActivity());
		View layout = mInflater.inflate(R.layout.pop_share, null);
		layout.findViewById(R.id.layWeiXin).setOnClickListener(this);
		layout.findViewById(R.id.layQQ).setOnClickListener(this);
		layout.findViewById(R.id.layXinLang).setOnClickListener(this);
		layout.findViewById(R.id.layAll).getBackground().setAlpha(220);
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

           //设置位置
    	//popupWindow.showAsDropDown(btnShare,-50,-180);
    	popupWindow.showAtLocation(btnShare, Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM,0,100); //设置在屏幕中的显示位置

    	//popupWindow.showAtLocation(mRootView, Gravity.LEFT|Gravity.TOP,20,90); //设置在屏幕中的显示位置
    }
    private void showProgress(){
    	if(getActivity() != null)
    		((HomeActivity)getActivity()).showProgress();
    }
    private void dismissProgress(){
    	if(getActivity() != null)
    		((HomeActivity)getActivity()).dismissProgress();
    }
	@Override
	public void onFragPasue() {
		// TODO Auto-generated method stub

	}

}
