package cy.com.morefan.util;




import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import hz.huotu.wsl.aifenxiang.wxapi.WXEntryActivity;

public class ShareUtil {
	//渠道类型微信：1；新浪微博：2;qq空间：3
	public static final int CHANNEL_WEIXIN = 1;
	public static final int CHANNEL_SINA = 2;
	public static final int CHANNEL_QZONE = 3;

//	public enum Channel{
//		WeiXin, Sina, Qzone
//	}
	/**
	 * 分享到朋友圈
	 * @param des
	 * @param imgPath
	 * @param content
	 */
	public static void share2WeiXin(Activity activity, String des, String imgPath, String content){
		Intent intentShare = new Intent(activity, WXEntryActivity.class);
		intentShare.putExtra("isShare", true);
		intentShare.putExtra("name", des);
		intentShare.putExtra("path", imgPath);
		intentShare.putExtra("url", content);
		//intentShare.putExtra("type", WXEntryActivity.WXtype.Moments);
		activity.startActivityForResult(intentShare, 0);
		L.i("shareUrl:" + content);
	}
	/**
	 * 分享到会话
	 * @param des
	 * @param imgPath
	 * @param content
	 */
	public static void share2WeChat(Activity activity, String des, String imgPath, String content){
		Intent intentShare = new Intent(activity, WXEntryActivity.class);
		intentShare.putExtra("isShare", true);
		intentShare.putExtra("name", des);
		intentShare.putExtra("path", imgPath);
		intentShare.putExtra("url", content);
		//intentShare.putExtra("type", WXEntryActivity.WXtype.Conversation);
		activity.startActivityForResult(intentShare, 0);
	}
	/**
	 *
	 * @param context
	 * @param des
	 * @param imgPath
	 * @param content
	 */


}
