package cy.com.morefan;

import java.util.ArrayList;
import java.util.List;

import cy.com.morefan.AuthCodeSendActivity.AuthType;
import cy.com.morefan.ToCrashAuthActivity.CrashAuthType;
import cy.com.morefan.adapter.ViewPagerAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.SecurityUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ForgetLoginPwdActivity extends BaseActivity implements Callback, BroadcastListener, OnClickListener{
	private UserService userService;
	private Handler mHandler = new Handler(this);
	private MyBroadcastReceiver myBroadcastReceiver;
	private ViewPager mViewPager;
	private EditText edtPhone;
	private EditText edtCode;
	private EditText edtPwd;
	private EditText edtRePwd;
	private TextView btnGet;
//	private TextView txtSms;
//	private TextView txtManual;
	private Drawable[] tabDrawables;
	private List<TextView> tabs;
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_USER_RESET){
			dismissProgress();
			toast("密码重置成功!");
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_USER_LOGOUT);
			finish();
		}else if(msg.what == BusinessDataListener.DONE_GET_CODE){
			dismissProgress();
			toast("验证码已发送,请注意查收短信!");
		}else if(msg.what == BusinessDataListener.ERROR_USER_RESET || msg.what == BusinessDataListener.ERROR_GET_CODE){
			dismissProgress();
			toast(msg.obj.toString());
			if(msg.what == BusinessDataListener.ERROR_GET_CODE){
				recLen = 1;
				handler.postAtTime(runnable, 0);
			}
		}
		return false;
	}
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
			setContentView(R.layout.user_forget_login);



			int raduis = DensityUtil.dip2px(this, 8);
			 GradientDrawable gdLeft = new GradientDrawable();
			 //左上，右上，右下，左下
			 gdLeft.setCornerRadii(new float[]{raduis,raduis,0,0,0,0,raduis,raduis});
			 gdLeft.setColor(Color.WHITE);

//			 GradientDrawable gdMid= new GradientDrawable();
//			 gdMid.setColor(Color.WHITE);

			 GradientDrawable gdRight = new GradientDrawable();
//			 //左上，右上，右下，左下
			 gdRight.setCornerRadii(new float[]{0,0,raduis,raduis,raduis,raduis,0,0});
			 gdRight.setColor(Color.WHITE);
			 tabDrawables = new Drawable[]{gdLeft, gdRight};


			 initView();
		userService = new UserService(this);

		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_USER_LOGIN, MyBroadcastReceiver.ACTION_SMS_RECEIVED, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);


	}
	OnEditorActionListener actionClickListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if(actionId == EditorInfo.IME_ACTION_DONE){
				userReset();
				return true;
			}
			return false;
		}

	};

	private int recLen;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
        	recLen--;
            if(recLen > 0){
            	btnGet.setText(String.format("%d秒后重新获取", recLen));
 	            handler.postDelayed(this, 1000);
            }else if(recLen == 0){
            	btnGet.setText("重新获取");
            	btnGet.setBackgroundResource(R.drawable.btn_enable);
            	btnGet.setClickable(true);
            }

        }
    };
	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		List<View> mViews = new ArrayList<View>();
		View viewSms = LayoutInflater.from(this).inflate(R.layout.user_forget_login_sms, null);
		edtPhone = (EditText) viewSms.findViewById(R.id.edtPhone);
		edtCode = (EditText) viewSms.findViewById(R.id.edtCode);
		edtPwd = (EditText) viewSms.findViewById(R.id.edtPwd);
		//edtRePwd = (EditText) viewSms.findViewById(R.id.edtRePwd);
		btnGet = (TextView) viewSms.findViewById(R.id.btnGet);
		btnGet.setOnClickListener(this);
		edtRePwd.setOnEditorActionListener(actionClickListener);

		tabs = new ArrayList<TextView>();
		TextView txtSms = (TextView) findViewById(R.id.txtSms);
		TextView txtManual = (TextView) findViewById(R.id.txtManual);
		tabs.add(txtSms);
		tabs.add(txtManual);
		//btnComplete = (TextView) findViewById(R.id.btnComplete);
		View viewManual = LayoutInflater.from(this).inflate(R.layout.tab_rule, null);
		mViews.add(viewSms);
		mViews.add(viewManual);

		final WebView mWebView = (WebView) viewManual.findViewById(R.id.webView);
		final ProgressBar bar = (ProgressBar) viewManual.findViewById(R.id.progressBar);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);

		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		 if(!TextUtils.isEmpty(BusinessStatic.getInstance().URL_MANUALSERVICE))
			 mWebView.loadUrl(BusinessStatic.getInstance().URL_MANUALSERVICE);
		 mWebView.setWebChromeClient(new WebChromeClient() {
				public void onProgressChanged(WebView view, int progress) {
					mWebView.setVisibility(View.VISIBLE);
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

		ViewPagerAdapter adapter = new ViewPagerAdapter(mViews);
		mViewPager.setAdapter(adapter);
		setTab(tabs.get(mViewPager.getCurrentItem()).getId());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int positon) {
				//btnComplete.setVisibility(arg0 == 0 ? View.VISIBLE : View.GONE);
				//updateTab(arg0);
				setTab(tabs.get(positon).getId());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
	private void setTab(int id){
		for(int i = 0, length = tabs.size(); i < length; i++){
			TextView item = tabs.get(i);
			if(id == item.getId()){
				item.setBackgroundDrawable(tabDrawables[i]);
				//item.setBackgroundResource(tabBgs[i]);
				item.setTextColor(getResources().getColor(R.color.theme_blue));
			}else{
				item.setBackgroundDrawable(null);
				item.setTextColor(Color.WHITE);
			}
		}

	}
//	private void updateTab(int index){
////		if(index == mViewPager.getCurrentItem())
////			return;
//		if(index == 0){
//			txtManual.setBackgroundColor(getResources().getColor(R.color.gray5));
//			txtManual.setTextColor(Color.WHITE);
//			txtSms.setBackgroundColor(Color.WHITE);
//			txtSms.setTextColor(getResources().getColor(R.color.gray5));
//		}else{
//			txtSms.setBackgroundColor(getResources().getColor(R.color.gray5));
//			txtSms.setTextColor(Color.WHITE);
//			txtManual.setBackgroundColor(Color.WHITE);
//			txtManual.setTextColor(getResources().getColor(R.color.gray5));
//		}
//
//	}
	@Override
	protected void onResume() {
		String phone = UserData.getUserData().phone;
 		if(TextUtils.isEmpty(phone)){
			edtPhone.setFocusable(true);
		}else{
			edtPhone.setFocusable(false);
			edtPhone.setText(phone);
		}
		super.onResume();
	}

	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type).sendToTarget();

	}

	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();

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
		}else if(type == ReceiverType.Sms){
			edtCode.setText(msg.toString());
			edtPwd.requestFocus();

		}else if(type == ReceiverType.Login){
			finish();
		}

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGet:
			String phone = edtPhone.getText().toString().trim();
			if(!Util.isPhoneNum(phone)){
				edtPhone.requestFocus();
				edtPhone.setError("手机号错误");
				return;
			}
			//test logincode,正式不需要logincode
			//String logincode = "test01^" + SecurityUtil.MD5Encryption("123456");
			//userService.getAuthCode("", phone, 4);
			showProgress();

			recLen = 90;
			handler.postDelayed(runnable, 1000);
			btnGet.setClickable(false);
			btnGet.setBackgroundResource(R.drawable.btn_disable);
			break;
		case R.id.txtSms:
			if(mViewPager.getCurrentItem() != 0)
				mViewPager.setCurrentItem(0);
			break;
		case R.id.txtManual:
			if(mViewPager.getCurrentItem() != 1)
				mViewPager.setCurrentItem(1);
			break;
		case R.id.btnComplete:
			userReset();
			break;

		default:
			break;
		}

	}

	private void userReset() {

		edtPhone.setError(null);
		edtPwd.setError(null);
		edtRePwd.setError(null);
		edtCode.setError(null);
		String userName = edtPhone.getText().toString();
		String userPwd = edtPwd.getText().toString();
		String code = edtCode.getText().toString().trim();
		String userRePwd = edtRePwd.getText().toString();
		if(!Util.isPhoneNum(userName)){
			edtPhone.requestFocus();
			edtPhone.setError("手机号错误");
			return;
		}
		if(edtCode.getVisibility() == View.VISIBLE && TextUtils.isEmpty(code)){
			edtCode.setError("验证码不能为空");
			edtCode.requestFocus();
			edtCode.requestFocusFromTouch();
			return;
		}
		if(!Util.userPwdIsLegal(userPwd).equals("success")){
			edtPwd.setError(Util.userPwdIsLegal(userPwd));
			edtPwd.requestFocus();
			edtPwd.requestFocusFromTouch();
			return;
		}

		if(!Util.userPwdIsLegal(userRePwd).equals("success")){
			edtRePwd.setError(Util.userPwdIsLegal(userRePwd));
			edtRePwd.requestFocus();
			edtRePwd.requestFocusFromTouch();
			return;
		}
		if(!userPwd.equals(userRePwd)){
			edtRePwd.setError("两次输入不同!");
			edtRePwd.requestFocus();
			edtRePwd.requestFocusFromTouch();
			return;
		}
		//userService.userReset(this, userName, SecurityUtil.MD5Encryption(userPwd), code );
		showProgress();



	}

}
