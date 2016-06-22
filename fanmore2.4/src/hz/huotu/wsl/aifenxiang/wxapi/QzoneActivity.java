package hz.huotu.wsl.aifenxiang.wxapi;

import android.os.Bundle;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.listener.MyBroadcastReceiver;

public class QzoneActivity extends BaseActivity{
	/**
	 * QZONE appid
	 */
	 public static String APP_ID="101066268";
	private Tencent tencent;
	public enum Qtype{
		QQ, Qzone
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			tencent = Tencent.createInstance(APP_ID, this);
			String des = bundle.getString("des");
			String imgUrl = bundle.getString("imgUrl");
			String contentUrl = bundle.getString("contentUrl");
			Qtype type = (Qtype) bundle.getSerializable("type");
			share(des, imgUrl, contentUrl, type);
		}
	}
	private void share(String des, String imgUrl, String contentUrl, final Qtype type){
		/**
    	 * Tencent.SHARE_TO_QQ_KEY_TYPE	选填	Int	SHARE_TO_QZONE_TYPE_IMAGE_TEXT（图文）
 		   Tencent.SHARE_TO_QQ_TITLE	必填	Int	分享的标题，最多200个字符
		   Tencent.SHARE_TO_QQ_SUMMARY	选填	String	分享的摘要，最多600字符
		   Tencent.SHARE_TO_QQ_TARGET_URL	必填	String	需要跳转的链接，URL字符串
		   Tencent.SHARE_TO_QQ_IMAGE_URL	选填	String	分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）。

    	 */
		final Bundle params = new Bundle();
         params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
         params.putString(QzoneShare.SHARE_TO_QQ_TITLE, des);
         //params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, taskData.content);
         params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, contentUrl);
         ArrayList<String> imageUrls = new ArrayList<String>();
         imageUrls.add(imgUrl);
         //params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL, taskData.smallImgUrl);
         params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
         /**
          * 用异步方式启动分享
          * @param params
          */
         new Thread(new Runnable() {
             @Override
             public void run() {
            	 if(type == Qtype.Qzone){
            		 tencent.shareToQzone(QzoneActivity.this, params, qqListener);
            	 }else{
            		 tencent.shareToQQ(QzoneActivity.this, params, qqListener);
            	 }

             }
         }).start();

	}
	IUiListener qqListener = new IUiListener() {
		@Override
		public void onCancel() {
			toast("取消分享");
			finish();
		}

		@Override
		public void onError(UiError e) {
			toast("onError: " + e.errorMessage);
			finish();
		}

		@Override
		public void onComplete(Object response) {
			// 正常情况下返回码为0
			toast("分享成功");
			 MyBroadcastReceiver.sendBroadcast(QzoneActivity.this, MyBroadcastReceiver.ACTION_SHARE_TO_QZONE_SUCCESS);
			 finish();
//			userService.commitSend(taskData.id,UserData.getUserData().loginCode, ShareUtil.CHANNEL_QZONE);
//			showProgress();
		}
	};


}
