package cy.com.morefan;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.util.VolleyUtil;

/**
 * 单张展示web页面
 */
public
class WebHelpActivity extends BaseActivity implements  View.OnClickListener, Handler.Callback {

    public
    Resources resources;
    public MainApplication application;
    public
    WindowManager wManager;
    public
    AssetManager am;

    @Bind(R.id.webPage)
    PullToRefreshWebView webPage;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_web_help);
        ButterKnife.bind(this);
        resources = this.getResources();
        application = (MainApplication) this.getApplication();
        wManager = this.getWindowManager();
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        initWebPage();
    }



    private void initWebPage()
    {
        webPage.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<WebView>() {
            @Override
            public void onRefresh(PullToRefreshBase<WebView> pullToRefreshBase) {
                loadPage();
            }
        });
        loadPage();
    }

    private void loadPage()
    {
        WebView viewPage = webPage.getRefreshableView();
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
        viewPage.loadUrl(BusinessStatic.getInstance().guide);

        viewPage.setWebViewClient(
                new WebViewClient() {

                }


        );

        viewPage.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (null==progressBar){
                    return;
                }
                progressBar.setProgress(newProgress);
                if (100 == newProgress) {
                    webPage.onRefreshComplete();
                }
                super.onProgressChanged(view, newProgress);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VolleyUtil.cancelAllRequest();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btnBack)
    void doBack()
    {
        finish();
        FragManager fragManager= new FragManager(this,R.id.layContent);
        fragManager.setCurrentFrag(FragManager.FragType.Task);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            //关闭
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
