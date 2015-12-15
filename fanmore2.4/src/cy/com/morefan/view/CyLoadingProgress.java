package cy.com.morefan.view;

import cy.com.morefan.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class CyLoadingProgress extends LinearLayout{
	private View layout;
	public CyLoadingProgress(Context context) {
		super(context);
		init(context);
	}

	public CyLoadingProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressLint("NewApi")
	public CyLoadingProgress(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context) {
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		//setLayoutParams(layoutParams);
		layout = LayoutInflater.from(context).inflate(R.layout.loading_layout, null);
		Animation anim = AnimationUtils.loadAnimation(context,R.anim.progressbar_custom);
		Animation anim2 = AnimationUtils.loadAnimation(context,R.anim.progressbar_custom_middle);
		Animation anim3 = AnimationUtils.loadAnimation(context,R.anim.progressbar_custom_small);

		layout.findViewById(R.id.textView1).startAnimation(anim);
		layout.findViewById(R.id.textView2).startAnimation(anim2);
		layout.findViewById(R.id.textView3).startAnimation(anim3);
		addView(layout, layoutParams);

	}
	public boolean isShowing(){
		return layout.getParent() != null;
	}
	public void dismiss(){
		removeAllViews();
	}


}
