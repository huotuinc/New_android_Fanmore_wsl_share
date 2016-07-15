package cy.com.morefan;

import java.io.InputStream;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.ToastUtil;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

public class ShareActivity extends BaseActivity{
	private String imgUrl = "http://taskapi.fhsilk.com/resource/app/104X104.png";
	private String imgPath;
	private String shareDes;
	private String shareUrl;
	protected void onCreate(android.os.Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.share);
		//InputStream in  = this.getResources().openRawResource(R.drawable.icon);
		imgPath = Constant.IMAGE_PATH_STORE + "/share_ico.png";
		//ImageUtil.saveInputStreanToFile(in, imgPath);
		shareDes = getIntent().getExtras().getString("shareDes");
		shareUrl = getIntent().getExtras().getString("shareUrl");
		if(TextUtils.isEmpty(shareDes) || TextUtils.isEmpty(shareUrl))
			finish();
	};
	@Override
	protected void onResume() {
		dismissProgress();
		super.onResume();
	}

	public void onClick(View v){
		switch (v.getId()) {
		case R.id.layQQ:
			showProgress();
			//ShareUtil.share2QQ(this, shareDes , imgUrl, shareUrl);
			break;
		case R.id.layQzone:
			showProgress();
			//ShareUtil.share2Qzone(this, shareDes , imgUrl, shareUrl);
			break;

		case R.id.layWX:
			showProgress();
			Platform platform1 = new WechatMoments(this);
			wx(this, shareDes, imgUrl, shareUrl, platform1);
			break;
		case R.id.layWechat:
			showProgress();
			Platform platform = new Wechat(this);
			wx(this, shareDes, imgUrl, shareUrl,platform);
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
		sp.setImageUrl(imgUrl);
		//sp.setImagePath(imgUrl);
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

}
