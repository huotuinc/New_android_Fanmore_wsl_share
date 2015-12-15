package cy.com.morefan.frag;

import cy.com.morefan.HomeActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class RuleFrag extends BaseFragment implements OnClickListener{
	private static RuleFrag frag;
	private View mRootView;
	private WebView webView;
	private ProgressBar bar;
	private TextView btn;
	private Handler mHandler = new Handler();
	public static RuleFrag newInstance(){
		if(frag == null)
			frag = new RuleFrag();
		return frag;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_rule, container, false);
		btn = (TextView) mRootView.findViewById(R.id.btnGoBack);
		btn.setOnClickListener(this);



		String url = BusinessStatic.getInstance().URL_RULE;

		webView = (WebView) mRootView.findViewById(R.id.webView);
		bar = (ProgressBar) mRootView.findViewById(R.id.progressBar);
		//不使用缓存：
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
//		L.i("url:" + url);
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
		webView.loadUrl(BusinessStatic.getInstance().URL_RULE);
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

		default:
			break;
		}

	}
	@Override
	public void onFragPasue() {
		// TODO Auto-generated method stub

	}

}
