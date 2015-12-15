package cy.com.morefan.wxapi;

import java.text.SimpleDateFormat;

import cindy.android.test.synclistview.ImageUtil;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.sina.weibo.sdk.openapi.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.utils.Utility;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.CustomDialog.OnkeyBackListener;

public class SinaShareActivity extends BaseActivity implements OnkeyBackListener {
	//公司
	private final String APP_KEY = "1994677353";

	private IWeiboShareAPI mWeiboShareAPI;
	 /** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    /**
     * WeiboSDKDemo 程序的 APP_SECRET。
     * 请注意：请务必妥善保管好自己的 APP_SECRET，不要直接暴露在程序中，此处仅作为一个DEMO来演示。
     */
   // private static final String WEIBO_DEMO_APP_SECRET = "4ecff164368e17c8a417ef49a6e755ad";
    /** 通过 code 获取 Token 的 URL */
    private static final String OAUTH2_ACCESS_TOKEN_URL = "https://open.weibo.cn/oauth2/access_token";
    /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, APP_KEY);

		// 获取 Token 成功
		String response = SPUtil.getStringToSpByName(SinaShareActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_SINA_TOKEN);
		if(TextUtils.isEmpty(response)){
			auth();
		}else{
			mAccessToken = Oauth2AccessToken.parseAccessToken(response);
			if (mAccessToken.isSessionValid()) {

					share();

			}else{
				auth();
			}
		}

//		 Bundle bundle = getIntent().getExtras();
//			if (bundle != null) {
//				String store = bundle.getString("store");
//				String des = bundle.getString("des");
//				String path = bundle.getString("path");
//				String url = bundle.getString("url");
//				 if(mWeiboShareAPI.isWeiboAppInstalled()){
//					 mWeiboShareAPI.registerApp(); // 将应用注册到微博客户端
//					 if (savedInstanceState != null) {
//				            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
//				        }
//					 share(store, des, path, url);
//				 }else{
//
//					 mWeiboAuth.authorize(new AuthListener(), WeiboAuth.OBTAIN_AUTH_CODE);
//					 // 创建微博实例
//				   //  mWeiboAuth = new WeiboAuth(this, APP_KEY, REDIRECT_URL, "");
//					// mWeiboAuth.anthorize(new AuthListener());
//				 }
//
//			}
	}
	private void auth(){
		 mWeiboAuth = new WeiboAuth(this, APP_KEY, REDIRECT_URL, SCOPE);
		 mWeiboAuth.authorize(new AuthListener(), WeiboAuth.OBTAIN_AUTH_CODE);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		//mWeiboShareAPI.handleWeiboResponse(intent, this); //当前应用唤起微博分享后,返回当前应用
	}


	  /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 用于获取微博信息流等操作的API */
    private StatusesAPI mStatusesAPI;
	 /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
			String code = values.getString("code");
			fetchTokenAsync(code, BusinessStatic.getInstance().SINA_KEY_SECRET);
        }

        @Override
        public void onCancel() {
        	toast("取消认证");
        	finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	toast("Auth exception : " + e.getMessage());
        	finish();
        }
    }


    /**
     * 异步获取 Token。
     *
     * @param authCode  授权 Code，该 Code 是一次性的，只能被获取一次 Token
     * @param appSecret 应用程序的 APP_SECRET，请务必妥善保管好自己的 APP_SECRET，
     *                  不要直接暴露在程序中，此处仅作为一个DEMO来演示。
     */
    public void fetchTokenAsync(String authCode, String appSecret) {
        WeiboParameters requestParams = new WeiboParameters();
        requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_ID,     APP_KEY);
        requestParams.put(WBConstants.AUTH_PARAMS_CLIENT_SECRET, appSecret);
        requestParams.put(WBConstants.AUTH_PARAMS_GRANT_TYPE,    "authorization_code");
        requestParams.put(WBConstants.AUTH_PARAMS_CODE,          authCode);
        requestParams.put(WBConstants.AUTH_PARAMS_REDIRECT_URL,  REDIRECT_URL);
        // 异步请求，获取 Token
        AsyncWeiboRunner.requestAsync(OAUTH2_ACCESS_TOKEN_URL, requestParams, "POST",mListener);
    }
    @SuppressWarnings("deprecation")
	private void share(){
    	Bundle bundle = getIntent().getExtras();
		if (bundle == null)
			return;
			final String des = bundle.getString("des");
			String path = bundle.getString("path");
			final String url = bundle.getString("url");
			View layout = LayoutInflater.from(this).inflate(R.layout.custom_dialog_content_sina, null);
			TextView txtDes = (TextView) layout.findViewById(R.id.txtDes);
			ImageView img = (ImageView) layout.findViewById(R.id.img);
			final EditText edtExtra = (EditText) layout.findViewById(R.id.edtExtra);
			final Bitmap bitmap = ImageUtil.readBitmapByPath(path);

			txtDes.setText(des);
			img.setBackgroundDrawable(new BitmapDrawable(bitmap));

			CustomDialog.setOnKeyBackListener(this);
			CustomDialog.showChooiceDialg(this, null , null, "发送", "返回", layout,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							 mStatusesAPI = new StatusesAPI(mAccessToken); //创建微博分享接口实例
							 String content = String.format("【%s】%s%s", des, edtExtra.getText().toString().trim(), url);
							 mStatusesAPI.upload(content, bitmap, null, null, mListener);
						}
					}, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							toast("取消发送");
							finish();

						}
					});

    }
    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                if(response.startsWith("{\"access_token\"")){
                    // 获取 Token 成功
                    Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(response);
                    if (token != null && token.isSessionValid()) {
                        mAccessToken = token;
                        SPUtil.saveStringToSpByName(SinaShareActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_SINA_TOKEN, response);
                        share();
                    } else {
                       toast("Failed to receive access token");
                    }
                }else if (response.startsWith("{\"created_at\"")) {
                    // 调用 Status#parse 解析字符串成微博对象
                    //Status status = Status.parse(response);
                    toast("分享成功");
                    MyBroadcastReceiver.sendBroadcast(SinaShareActivity.this, MyBroadcastReceiver.ACTION_SHARE_TO_SINA_SUCCESS);
//                  //send broadcast
                    finish();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	toast("分享失败,请重试!");
//            ErrorInfo info = ErrorInfo.parse(e.getMessage());
//            Toast.makeText(SinaShareActivity.this, info.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    		return true;
		return super.onKeyDown(keyCode, event);
		};

	@Override
	public void onKeyBack() {
		toast("取消分享");
		finish();

	}

//	  private void share(String store, String des, String path, String url) {
//		Bitmap bitmap = ImageUtil.readBitmapByPath(path);
//		TextObject textObject = new TextObject();
//		textObject.text = des;
//		ImageObject imageObject = new ImageObject();
//		imageObject.setImageObject(bitmap);
//		WebpageObject mediaObject = new WebpageObject();
//		mediaObject.identify = Utility.generateGUID();
//		mediaObject.title = des;
//		mediaObject.description = store;
//		// 设置 Bitmap 类型的图片到视频对象里
//		mediaObject.setThumbImage(bitmap);
//		mediaObject.actionUrl = url;
//
//		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();// 初始化微博的分享消息
//		weiboMessage.textObject = textObject;
//		weiboMessage.imageObject = imageObject;
//		weiboMessage.mediaObject = mediaObject;
//		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
//		request.transaction = String.valueOf(System.currentTimeMillis());
//		request.multiMessage = weiboMessage;
//		mWeiboShareAPI.sendRequest(request); // 发送请求消息到微博,唤起微博分享界面
//
//	}




//	@Override
//	public void onResponse(BaseResponse baseResp) {// 接收微客户端博请求的数据。
//
//        switch (baseResp.errCode) {
//        case WBConstants.ErrorCode.ERR_OK:
//            Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
//            MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_SHARE_TO_SINA_SUCCESS);
//            //send broadcast
//            finish();
//            break;
//        case WBConstants.ErrorCode.ERR_CANCEL:
//            Toast.makeText(this,"取消分享", Toast.LENGTH_LONG).show();
//            finish();
//            break;
//        case WBConstants.ErrorCode.ERR_FAIL:
//            Toast.makeText(this,
//                    "分享失败" + "Error Message: " + baseResp.errMsg,
//                    Toast.LENGTH_LONG).show();
//            finish();
//            break;
//        }
//
//	}

}
