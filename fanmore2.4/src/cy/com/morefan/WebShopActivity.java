package cy.com.morefan;



import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cy.com.morefan.bean.ShareModel;
import cy.com.morefan.constant.BusinessStatic;

import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.listener.PoponDismissListener;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SystemTools;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.UrlFilterUtils;
import cy.com.morefan.util.WindowUtils;
import cy.com.morefan.view.SharePopupWindow;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;




@SuppressLint("SetJavaScriptEnabled")
public class WebShopActivity extends BaseActivity implements Handler.Callback  {

    private WebView webView;
    private WebView underwebView;
    private ProgressBar bar;
    private TextView txtTitle;
    private Button btnRight;
    private SharePopupWindow share;
    public MainApplication application;
    //handler对象
    public Handler mHandler;
    //windows类
    WindowManager wManager;
    //private MyBroadcastReceiver myBroadcastReceiver;
    //private String imgPath;
   // private String imgUrl = "http://task.fanmore.cn/images/28def407415841a7ada5a0b0377895e7_104X104.jpg";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler( this );
        wManager = this.getWindowManager();
        L.i("webview onCreateView");

        application = (MainApplication)getApplication();

        setContentView(R.layout.activity_web_shop);
        //imgPath = Constant.IMAGE_PATH_STORE + "/share_ico.png";

       //myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

        //url = "http://www.baidu.com";

        webView = (WebView) findViewById(R.id.webView);
        underwebView=(WebView)findViewById(R.id.underwebView);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        underwebView.getSettings().setJavaScriptEnabled(true);
        underwebView.getSettings().setDomStorageEnabled(true);

        underwebView.getSettings().setUseWideViewPort(true);
        underwebView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setDomStorageEnabled(true);
       // CyButton btnShare = (CyButton) findViewById(R.id.btnShare);

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
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("url");
        String underurl= BusinessStatic.getInstance().URL_WEBSITE +"/bottom.aspx?customerid="+ application.readMerchantId();
        underwebView.loadUrl(underurl);
        underwebView.setWebViewClient(
                new WebViewClient() {

                    //重写此方法，浏览器内部跳转
                    public boolean shouldOverrideUrlLoading(
                            WebView view, String
                            url
                    ) {
                        webView.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {

                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                    }
                }
        );
        L.i("url:" + url);
        //String title = getIntent().getExtras().getString("title");
//        boolean isShare = getIntent().getExtras().getBoolean("isShare");
//        boolean disasterFlag = getIntent().getExtras().getBoolean("disasterFlag");
//        isSend = getIntent().getExtras().getBoolean("isSend");
//        btnShare.setVisibility(disasterFlag ? View.VISIBLE : View.GONE);
        //if(isShare)
//         findViewById(R.id.layAll).setVisibility(View.VISIBLE);
        //txtTitle.setText(title);
        //txtTitle.setVisibility(View.GONE);
        //首页鉴权
        //AuthParamUtils paramUtils = new AuthParamUtils ( application, System.currentTimeMillis (), application.obtainMerchantUrl ( ), WebShopActivity.this );

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
                txtTitle.setText(title);
            }
        });

        webView.setWebViewClient(
                new WebViewClient() {

                    //重写此方法，浏览器内部跳转
                    public boolean shouldOverrideUrlLoading(
                            WebView view, String
                            url
                    ) {
                        UrlFilterUtils filter = new UrlFilterUtils(
                                WebShopActivity.this,
                                WebShopActivity.this,
                                txtTitle, mHandler,
                                application,
                                wManager
                        );
                        return filter.shouldOverrideUrlBySFriend(webView, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        //页面加载完成后,读取菜单项
                        super.onPageFinished(view, url);
                        String title = view.getTitle();
                        String temp = url;
                        if(url.startsWith("http://")){
                            int start = url.indexOf("http://");
                            temp = url.substring( start+7 );
                        }
                        if (temp.equals(title)) {
                            txtTitle.setText("");
                        }else {
                            txtTitle.setText(title);
                        }


                    }

                    @Override
                    public void onReceivedError(
                            WebView view, int errorCode, String description,
                            String failingUrl
                    )
                    {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }

                }
        );




    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (webView.canGoBack()){
                webView.goBack();
            }else {
                finish();
            }

            return true;
        }else if(keyCode == KeyEvent.KEYCODE_MENU){
        }
        return super.onKeyDown(keyCode, event);
    }
    public void onResume() {
        super.onResume();
        dismissProgress();
    };
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.btnBack:
                if (webView.canGoBack()){
                    webView.goBack();
                }else {
                    finish();
                    FragManager fragManager= new FragManager(this,R.id.layContent);
                    fragManager.setCurrentFrag(FragManager.FragType.Task);
                }

                break;

            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        //myBroadcastReceiver.unregisterReceiver();
        super.onDestroy();
    }

    public boolean handleMessage(Message msg) {

        switch ( msg.what )
        {
            //加载页面

            case Constant.FRESHEN_PAGE_MESSAGE_TAG:
            {
                //刷新界面
                String url = msg.obj.toString ();
                webView.loadUrl(url);
            }
            break;




            case Constant.SWITCH_USER_NOTIFY:
            {
                SwitchUserModel.SwitchUser user = ( SwitchUserModel.SwitchUser ) msg.obj;
                //更新userId
                application.writeMemberId ( String.valueOf ( user.getUserid ( ) ) );
                //更新昵称
                application.writeUserName ( user.getWxNickName () );
                application.writeUserIcon ( user.getWxHeadImg ( ) );

                application.writeMemberLevel(user.getLevelName());


            }
            break;
            case Constant.LOAD_SWITCH_USER_OVER:
            {
                progress.dismissProgress();
            }
            break;
            case Constant.PAY_NET:
            {
                PayModel payModel = ( PayModel ) msg.obj;
                //调用JS
                webView.loadUrl("javascript:utils.Go2Payment(" + payModel.getCustomId() + "," + payModel.getTradeNo() + "," + payModel.getPaymentType() + ", "
                        + "false);\n");
            }
            break;
            default:
                break;
        }
        return false;
    }

}
