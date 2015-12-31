package cy.com.morefan;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.AliPayUtil;
import cy.com.morefan.util.SubUrlFilterUtils;
import cy.com.morefan.util.SystemTools;
import cy.com.morefan.util.ToastUtil;

/**
 * 单张展示web页面
 */
public
class WebActivity extends BaseActivity implements Handler.Callback, MyBroadcastReceiver.BroadcastListener {

    //获取资源文件对象
    private
    Resources resources;
    private
    Handler mHandler;
    //application
    private
    MainApplication application;
    //web视图
    private
    WebView viewPage;
    private String url;
    //private SharePopupWindow share;
    private MyBroadcastReceiver myBroadcastReceiver;

    //tilte组件
    @Bind(R.id.newtitleLayout)
    RelativeLayout newtitleLayout;
    //标题栏左侧图标
    @Bind(R.id.titleLeftImage)
    ImageView titleLeftImage;
    //标题栏标题文字
    @Bind(R.id.titleText)
    TextView titleText;
    //标题栏右侧图标
    @Bind(R.id.titleRightImage)
    ImageView titleRightImage;
    @Bind(R.id.viewPage)
    PullToRefreshWebView refreshWebView;
    public static ValueCallback< Uri > mUploadMessage;
    public static final int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected
    void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        application = ( MainApplication ) this.getApplication ( );
        resources = this.getResources ( );
        this.setContentView(R.layout.new_load_page);
        ButterKnife.bind(this);
        //setImmerseLayout(newtitleLayout);
        mHandler = new Handler( this );
       // share = new SharePopupWindow ( WebActivity.this, WebActivity.this, application );
        myBroadcastReceiver = new MyBroadcastReceiver(WebActivity.this,this, MyBroadcastReceiver.ACTION_PAY_SUCCESS);
        Bundle bundle = this.getIntent ( ).getExtras ( );
        url = bundle.getString ( Constant.INTENT_URL );
        initView();
    }


    protected
    void initView ( ) {

        Drawable drawable = resources.getDrawable(R.color.theme_blue);
        //设置title背景
        SystemTools.loadBackground(newtitleLayout, drawable);
        //设置左侧图标
//        Drawable leftDraw = resources.getDrawable ( R.drawable.main_title_left_back );
//        SystemTools.loadBackground(titleLeftImage, leftDraw);
//        //设置右侧图标
//        Drawable rightDraw = resources.getDrawable ( R.drawable.home_title_right_share );
//        SystemTools.loadBackground(titleRightImage, rightDraw);
        viewPage = refreshWebView.getRefreshableView();
        refreshWebView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<WebView>() {
            @Override
            public void onRefresh(PullToRefreshBase<WebView> pullToRefreshBase) {
                viewPage.reload();
            }
        });
        loadPage();
    }

    private void loadPage()
    {
        viewPage.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        viewPage.setVerticalScrollBarEnabled(false);
        viewPage.setClickable(true);
        viewPage.getSettings().setUseWideViewPort(true);
        //是否需要避免页面放大缩小操作

        viewPage.getSettings().setSupportZoom(true);
        viewPage.getSettings().setBuiltInZoomControls(true);
        viewPage.getSettings().setJavaScriptEnabled(true);
        viewPage.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        viewPage.getSettings().setSaveFormData(true);
        viewPage.getSettings().setAllowFileAccess(true);
        viewPage.getSettings().setLoadWithOverviewMode(false);
        viewPage.getSettings().setSavePassword(true);
        viewPage.getSettings().setLoadsImagesAutomatically(true);
        viewPage.loadUrl(url);

        viewPage.setWebViewClient(
                new WebViewClient() {

                    //重写此方法，浏览器内部跳转
                    public boolean shouldOverrideUrlLoading(
                            WebView view, String
                            url
                    ) {
                        SubUrlFilterUtils filter = new SubUrlFilterUtils(WebActivity.this,
                                WebActivity.this,
                                titleText, mHandler,
                                application);
                        return filter.shouldOverrideUrlBySFriend(viewPage, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        titleText.setText(view.getTitle());
                    }


                    @Override
                    public void onReceivedError(
                            WebView view, int errorCode, String description,
                            String failingUrl
                    ) {
                        super.onReceivedError(view, errorCode, description, failingUrl);

                    }

                }


        );

        viewPage.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titleText.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (100 == newProgress) {
                    refreshWebView.onRefreshComplete();
                }
                super.onProgressChanged(view, newProgress);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                WebActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                openFileChooser(uploadMsg);
            }

            //For Android 4.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

                openFileChooser(uploadMsg);

            }
        });

    }

    @OnClick(R.id.titleLeftImage)
    void doBack()
    {
        if(viewPage.canGoBack ())
        {
            viewPage.goBack ( );
        }
        else
        {
            //关闭界面
            finish();
        }
    }

 //   @OnClick(R.id.titleRightImage)
//    void doShare()
//    {
//        String text = application.obtainMerchantName ()+"分享";
//        String imageurl = application.obtainMerchantLogo ();
//        if(!imageurl.contains ( "http://" ))
//        {
//            //加上域名
//            imageurl = application.obtainMerchantUrl () + imageurl;
//        }
//        else if( TextUtils.isEmpty(imageurl))
//        {
//            imageurl = Constants.COMMON_SHARE_LOGO;
//        }
//        String title = application.obtainMerchantName ()+"分享";
//        String url = null;
//        url = viewPage.getUrl();
//        ShareModel msgModel = new ShareModel ();
//        msgModel.setImageUrl ( imageurl);
//        msgModel.setText ( text );
//        msgModel.setTitle ( title );
//        msgModel.setUrl ( url );
//        share.initShareParams ( msgModel );
//        share.showShareWindow ( );
//        share.showAtLocation (
//                titleRightImage,
//                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0
//        );
//        share.setPlatformActionListener (
//                new PlatformActionListener( ) {
//                    @Override
//                    public
//                    void onComplete (
//                            Platform platform, int i, HashMap< String, Object > hashMap
//                    ) {
//                        Message msg = Message.obtain();
//                        msg.what = Constants.SHARE_SUCCESS;
//                        msg.obj = platform;
//                        mHandler.sendMessage ( msg );
//                    }
//
//                    @Override
//                    public
//                    void onError ( Platform platform, int i, Throwable throwable ) {
//                        Message msg = Message.obtain();
//                        msg.what = Constants.SHARE_ERROR;
//                        msg.obj = platform;
//                        mHandler.sendMessage ( msg );
//                    }
//
//                    @Override
//                    public
//                    void onCancel ( Platform platform, int i ) {
//                        Message msg = Message.obtain();
//                        msg.what = Constants.SHARE_CANCEL;
//                        msg.obj = platform;
//                        mHandler.sendMessage ( msg );
//                    }
//                }
//        );
//        share.setOnDismissListener ( new PoponDismissListener ( WebActivity.this ) );
//    }


    @Override
    public
    boolean handleMessage ( Message msg )
    {
        switch ( msg.what )
        {
            //分享
//            case Constant.SHARE_SUCCESS:
//            {
//                //分享成功
//                Platform platform = (Platform) msg.obj;
//                int action = msg.arg1;
//                if("WechatMoments".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "微信朋友圈分享成功" );
//                }
//                else if("Wechat".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "微信分享成功" );
//                }
//                else if("QZone".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "QQ空间分享成功" );
//                }
//                else if("SinaWeibo".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "sina微博分享成功" );
//                }
//
//            }
//            break;
//            case Constants.SHARE_ERROR:
//            {
//                //分享失败
//                Platform platform = (Platform) msg.obj;
//                int action = msg.arg1;
//                if("WechatMoments".equals ( platform.getName () )) {
//                    ToastUtils.showShortToast ( WebActivity.this, "微信朋友圈分享失败" );
//                }
//                else if("Wechat".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "微信分享失败" );
//                }
//                else if("QZone".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "QQ空间分享失败" );
//                }
//                else if("SinaWeibo".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "sina微博分享失败" );
//                }
//            }
//            break;
//            case Constants.SHARE_CANCEL:
//            {
//                //分享取消
//                Platform platform = (Platform) msg.obj;
//                int action = msg.arg1;
//                if("WechatMoments".equals ( platform.getName () )) {
//                    ToastUtils.showShortToast ( WebActivity.this, "微信朋友圈分享取消" );
//                }
//                else if("Wechat".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "微信分享取消" );
//                }
//                else if("QZone".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "QQ空间分享取消" );
//                }
//                else if("SinaWeibo".equals ( platform.getName () ))
//                {
//                    ToastUtils.showShortToast ( WebActivity.this, "sina微博分享取消" );
//                }
//            }
//            break;
            case AliPayUtil.SDK_PAY_FLAG: {
                PayGoodBean payGoodBean = ( PayGoodBean ) msg.obj;
                String tag = payGoodBean.getTag ( );
                String[] tags = tag.split ( ";" );
                for ( String str:tags )
                {
                    if(str.contains ( "resultStatus" ))
                    {
                        String code = str.substring ( str.indexOf ( "{" )+1, str.indexOf ( "}" ) );
                        if(!"9000".equals ( code ))
                        {
                            //支付宝支付信息提示
                            ToastUtil.show(WebActivity.this, "支付宝支付失败，code:" + code);
                        }
                    }
                }
            }
            break;
            case Constant.PAY_NET:
            {
                PayModel payModel = ( PayModel ) msg.obj;
                //调用JS
                viewPage.loadUrl ( "javascript:utils.Go2Payment("+payModel.getCustomId ()+","+ payModel.getTradeNo ()+","+ payModel.getPaymentType ()+", "
                                   + "false);\n" );
            }
            default:
                break;
        }
        return false;

    }

    @Override
    protected
    void onDestroy ( ) {
        super.onDestroy ( );
        ButterKnife.unbind(this);
        if( null != myBroadcastReceiver)
        {
            myBroadcastReceiver.unregisterReceiver();
        }
    }

    @Override
    public
    void onFinishReceiver ( MyBroadcastReceiver.ReceiverType type, Object msg ) {
        if(type == MyBroadcastReceiver.ReceiverType.wxPaySuccess)
        {
            viewPage.goBack();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }
}
