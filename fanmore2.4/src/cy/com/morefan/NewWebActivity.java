package cy.com.morefan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.SPUtil;

/**
 * 单张展示web页面
 */
public
class NewWebActivity extends BaseActivity implements MyBroadcastReceiver.BroadcastListener {


    private
    MainApplication application;
    //web视图
    private
    WebView viewPage;
    private String url;
    private MyBroadcastReceiver myBroadcastReceiver;


    @Bind(R.id.viewPage)
    PullToRefreshWebView refreshWebView;
    public static ValueCallback< Uri > mUploadMessage;
    public static final int FILECHOOSER_RESULTCODE = 1;

    @Override
    protected
    void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newweb);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        application = ( MainApplication ) this.getApplication ( );

        myBroadcastReceiver = new MyBroadcastReceiver(NewWebActivity.this,this, MyBroadcastReceiver.ACTION_PAY_SUCCESS);
        Bundle bundle=getIntent().getExtras();
        url = bundle.getString("url");
        if (TextUtils.isEmpty(url)){
            if(!TextUtils.isEmpty(SPUtil.getStringToSpByName(NewWebActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME)) &&
                    !TextUtils.isEmpty(SPUtil.getStringToSpByName(NewWebActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD))) {

                ActivityUtils.getInstance().skipActivity(NewWebActivity.this, HomeActivity.class);
            } else {
                ActivityUtils.getInstance().skipActivity(NewWebActivity.this, MoblieLoginActivity.class);
            }
        }else {
            initView();
        }
    }


    protected
    void initView ( ) {
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



        viewPage.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
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
                NewWebActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
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
