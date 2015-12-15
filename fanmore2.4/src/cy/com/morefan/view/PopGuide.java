package cy.com.morefan.view;

import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.SPUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class PopGuide implements Callback{
	private Activity mActivity;
	private WindowManager mWindowManager;
	private LinearLayout layAll;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == 1){
			dismissProgress();
			return true;
		}
		return false;
	}

	public PopGuide(Activity activity) {
		this.mActivity = activity;
	}

	public void show(Context context, int resId) {
		//已展示过，记录sp
		SPUtil.saveBooleanToSpByName(context, Constant.SP_NAME_NORMAL, resId+"", true);
				//10s后自动关闭
				mHandler.sendEmptyMessageDelayed(1, 10000);
		if(mActivity.isFinishing())
			return;
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
		}
		if(layAll == null){
			layAll = new LinearLayout(mActivity);
		}
		layAll.setBackgroundResource(resId);

		if (layAll.getParent() == null) {
			WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.TYPE_APPLICATION,
					WindowManager.LayoutParams.	FLAG_ALT_FOCUSABLE_IM,
					PixelFormat.TRANSLUCENT);
			layAll.removeAllViews();

			layAll.setGravity(Gravity.CENTER);
			layAll.setFocusable(true);
			layAll.setFocusableInTouchMode(true);
			layAll.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissProgress();
				}
			});
			layAll.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK){
						dismissProgress();
						if(keyBack != null)
							keyBack.onkeyBack();
						return true;
					}
					return false;
				}
			});

			mWindowManager.addView(layAll, lp);
		}
	}
	public interface progressOnKeyBack{
		void onkeyBack();
	}
	public progressOnKeyBack keyBack;
	public void setProgressOnkeyBack(progressOnKeyBack keyBack){
		this.keyBack = keyBack;
	}


	public void dismissProgress() {
		if (layAll != null && layAll.getParent() != null) {
			mWindowManager.removeView(layAll);
		}
	}










}
