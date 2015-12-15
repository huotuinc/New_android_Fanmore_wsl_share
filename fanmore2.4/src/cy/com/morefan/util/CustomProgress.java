package cy.com.morefan.util;

import cy.com.morefan.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

public class CustomProgress {
	private PopupWindow pop;
	private Context mContext;
	private View layout;
	public CustomProgress(Context mContext){
		this.mContext = mContext;
		
	}
	
	public void showProgress(){
		if(pop == null || layout == null){
			LayoutInflater mInflater = LayoutInflater.from(mContext);
			layout = mInflater.inflate(R.layout.loading_layout, null);
			pop = new PopupWindow(layout, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
		
			
			pop.setFocusable(true);
			layout.setClickable(true);
//			popupWindow.setBackgroundDrawable(null);
			layout.setFocusable(true);
			layout.setFocusableInTouchMode(true);
			layout.getBackground().setAlpha(50);
			layout.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK ){
						pop.dismiss();
						return true;
					}
					return false;
				}
			});
		}
		Animation anim = AnimationUtils.loadAnimation(mContext,R.anim.progressbar_custom);
		Animation anim2 = AnimationUtils.loadAnimation(mContext,R.anim.progressbar_custom_middle);
		Animation anim3 = AnimationUtils.loadAnimation(mContext,R.anim.progressbar_custom_small);

		layout.findViewById(R.id.textView1).startAnimation(anim);
		layout.findViewById(R.id.textView2).startAnimation(anim2);
		layout.findViewById(R.id.textView3).startAnimation(anim3);
		pop.showAtLocation(layout, Gravity.CENTER, 0, 0);
		
	}
	public void dismissProgress(){
		if(pop != null){
			pop.dismiss();
			
		}
	}
}
