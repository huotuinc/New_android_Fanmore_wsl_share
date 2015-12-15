package cy.com.morefan.guideview;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class AnimationView extends ImageView{
	private int height;
	private Status currentStatus;
	private Context mContext;
	private enum Status{
		Large, Small
	}
	public AnimationView(Context context) {
		this(context, null, 0);
	}
	public AnimationView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public AnimationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		 isInEditMode();
		 DisplayMetrics displayMetrics = new DisplayMetrics();
		 ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		 height  =  displayMetrics.heightPixels * 2 / 3;
		 currentStatus = Status.Small;
		 zoomOut();
		
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}
	public void check(){
		int[] location = new int[2];
        getLocationOnScreen(location);
        Rect rect = new Rect();
        getGlobalVisibleRect(rect);
        int y = location[1] + (rect.bottom - rect.top)/2;
        System.out.println(">>>>>rect:" + rect.top + "," + rect.bottom);
       
//        int per = currentStatus == Status.Large ? 3 : 1;
//        int y = location[1] ;//+ per*getHeight()/2;//
       // System.out.println(">>>>>y:" + y +","+ per);
        
        if(y > height ){//缩小
        	//to small
        	if(currentStatus == Status.Large){
        		
        		zoomOut();
        	}
        }else{
        	//to large
        	if(currentStatus == Status.Small){
        		zoomIn();
        	}
        }
	}
	private void zoomIn() {
		currentStatus = Status.Large;
		
		AnimatorSet set = new AnimatorSet();
		set.playTogether(
//		    ObjectAnimator.ofFloat(myView, "rotationX", 0, 360),
//		    ObjectAnimator.ofFloat(myView, "rotationY", 0, 180),
//		    ObjectAnimator.ofFloat(myView, "rotation", 0, -90),
//		    ObjectAnimator.ofFloat(myView, "translationX", 0, 90),
//		    ObjectAnimator.ofFloat(this, "translationY", -30, 0),
		    ObjectAnimator.ofFloat(this, "scaleX", 0.2f, 1),
		    ObjectAnimator.ofFloat(this, "scaleY", 0.2f, 1)
//		    ObjectAnimator.ofFloat(myView, "alpha", 1, 0.25f, 1)
		);
		//set.setInterpolator(new OvershootInterpolator(3F));
		set.setInterpolator(AnimationUtils.loadInterpolator(mContext,
	                android.R.anim.anticipate_overshoot_interpolator));
		set.setDuration(400).start();
//		Animation animation = AnimationUtils.loadAnimation(this,R.anim.main_zoom_in);
//		view.startAnimation(animation);
		
	}
	private void zoomOut() {
		currentStatus = Status.Small;
		AnimatorSet set = new AnimatorSet();
		set.playTogether(
//		    ObjectAnimator.ofFloat(myView, "rotationX", 0, 360),
//		    ObjectAnimator.ofFloat(myView, "rotationY", 0, 180),
//		    ObjectAnimator.ofFloat(myView, "rotation", 0, -90),
//		    ObjectAnimator.ofFloat(myView, "translationX", 0, 90),
//		    ObjectAnimator.ofFloat(myView, "translationY", 0, -30),
		    ObjectAnimator.ofFloat(this, "scaleX", 1, 0.2f),
		    ObjectAnimator.ofFloat(this, "scaleY", 1, 0.2f)
//		    ObjectAnimator.ofFloat(myView, "alpha", 1, 0.25f, 1)
		);
		//set.setInterpolator(new OvershootInterpolator(3F));
		set.setInterpolator(AnimationUtils.loadInterpolator(mContext,
                android.R.anim.anticipate_overshoot_interpolator));
		set.setDuration(400).start();
		
	}
	
	
	

}
