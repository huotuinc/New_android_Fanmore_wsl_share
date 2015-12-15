package cy.com.morefan.view;

import cy.com.morefan.R;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class EmptyView extends ImageView{
	private AnimationDrawable mAnimation;
	public EmptyView(Context context) {
		super(context);
		init(context);
	}
	public EmptyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public EmptyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context) {
		//mAnimation = AnimationUtils.loadAnimation(context,R.anim.anim_empty);
		mAnimation = (AnimationDrawable)getDrawable();
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if(View.VISIBLE == visibility){
			mAnimation.start();
			//startAnimation(mAnimation);
		}else{
			mAnimation.stop();
		}
	}




}
