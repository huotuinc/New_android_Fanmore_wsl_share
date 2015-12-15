package cy.com.morefan.view;

import com.nineoldandroids.view.ViewHelper;

import cy.com.morefan.R;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class DragLayout2 extends FrameLayout{
	private static final float RANGE = 0.7f;

	private GestureDetectorCompat gestureDetector;
	private ViewDragHelper mDragHelper;
	private View mLeftMenu;
	private MyRelativeLayout mContentView;
	private int mainLeft;
	private Context mContext;

	private int range;
	private int width;
	private int height;

	private boolean dragable;
	private View btnLeft;
	/**
	 * shadow
	 */
	private ImageView imgShadow;
	public void setDragable(boolean enable){
		this.dragable = enable;
	}
	public boolean getDragable(){
		return this.dragable;
	}
	public DragLayout2(Context context) {
		this(context, null);
	}
	public DragLayout2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	@SuppressLint("NewApi")
	public DragLayout2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		dragable = true;
		curStatus = Status.Close;
		gestureDetector = new GestureDetectorCompat(context, new YScrollDetector());
		mDragHelper = ViewDragHelper.create(this, 1.0f, callBack);

	}
	class YScrollDetector extends SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return Math.abs(distanceY) <= Math.abs(distanceX);
		}
	}

	@Override
	protected void onFinishInflate() {
		if(mLeftMenu == null)
			mLeftMenu = getChildAt(0);
		if(mContentView == null)
			mContentView = (MyRelativeLayout) getChildAt(1);
		//mContentView.setDragLayout(this);
		mLeftMenu.setVisibility(View.INVISIBLE);

		btnLeft = mContentView.findViewById(R.id.btnLeft);
		//此项目定制
		View shadowView = mLeftMenu.findViewById(R.id.shadow);
		if(null != shadowView){
			imgShadow = (ImageView) shadowView;

			int[] size = DensityUtil.getSize(mContext);
			FrameLayout.LayoutParams params = (LayoutParams) imgShadow.getLayoutParams();
			int marginLeft = (int) (0.75 * size[0]);
			params.setMargins(marginLeft, 0, -marginLeft, 0);
			params.height = (int) (size[1] * 0.88);
			imgShadow.setLayoutParams(params);
		}

		super.onFinishInflate();
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = mLeftMenu.getMeasuredWidth();
		height = mLeftMenu.getMeasuredHeight();
		range = (int) (width * RANGE);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
	super.onLayout(changed, l, t, r, b);

	mLeftMenu.layout(0, 0, width, height);
	mContentView.layout(mainLeft, 0, mainLeft + width, height);

	};

	ViewDragHelper.Callback callBack =  new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int arg1) {
			return true;//child == mContentView;
		}
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			if (changedView == mContentView) {
				mainLeft = left;
			}
			dispatchDragEvent(mainLeft);
			if (changedView == mLeftMenu) {
				mLeftMenu.layout(0, 0, width, height);
				mContentView.layout(mainLeft, 0, mainLeft + width, height);
			}
		};


		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (mainLeft + dx < 0) {
				return 0;
			} else if (mainLeft + dx > range) {
				return range;
			} else {
				return left;
			}
		}
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
				 if (xvel > 0) {
						openLeft();
					} else if (xvel < 0) {
						L.i("*******************1");
						closeLeft();
					} else if (releasedChild == mContentView && mainLeft > range * 0.3) {
						openLeft();
					} else if (releasedChild == mContentView && mainLeft > range * 0.7) {
						openLeft();
					} else {
						L.i("*******************2");
						closeLeft();
					}
		}
	};
	private boolean isDragging;
	public boolean isDragging() {
		return isDragging;
	}
	public boolean isOpened() {
		return getStatus() == Status.Open;
	}
	public interface DragListener{
		public void onOpen();
		public void onClose();
	}
	private DragListener mDragListener;
	public void setDrayListener(DragListener mDragListener){
		this.mDragListener = mDragListener;
	}
	public enum Status{
		Drag, Open, Close
	}
	private Status curStatus;
	private void dispatchDragEvent(int mainLeft) {
		mLeftMenu.setVisibility(View.VISIBLE);
		isDragging = true;

		Status lastStatus = getStatus();
		float percent = mainLeft / (float) range;
		animate(percent);

		if(curStatus == lastStatus)
			return;
		curStatus = lastStatus;
		if(mDragListener == null)
			return;
		if (lastStatus == Status.Close) {
			isDragging = false;
			mLeftMenu.setEnabled(false);
			mContentView.setEnabled(true);
			mDragListener.onClose();
		} else if (lastStatus == Status.Open) {
			isDragging = false;
			mLeftMenu.setEnabled(true);
			mContentView.setEnabled(false);
			mDragListener.onOpen();
		}


	}
	public Status getStatus(){
		Status status;
		int mainLeft = mContentView.getLeft();
		if (mainLeft == 0) {
			status = Status.Close;
		} else if (mainLeft == range) {
			status = Status.Open;
		} else {
			status = Status.Drag;
		}
		return status;
	}

	@Override
	public void computeScroll() {
		if (mDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	public void openOrClose(){
		Status status = getStatus();
		if(status == Status.Close)
			openLeft();
		else if(status == Status.Open){
			closeLeft();
		}
	}
	public void openLeft() {
		if (mDragHelper.smoothSlideViewTo(mContentView, range, 0)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	};

	public void closeLeft(){
		if (mDragHelper.smoothSlideViewTo(mContentView, 0, 0)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}


	@Override
	public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {
		if(dragable){
			final int action = MotionEventCompat.getActionMasked(ev);
			  if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			      mDragHelper.cancel();
			      return false;
			  }
			  return mDragHelper.shouldInterceptTouchEvent(ev) && gestureDetector.onTouchEvent(ev);
		}
		return super.onInterceptTouchEvent(ev);
	};
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(dragable){
			try {
				mDragHelper.processTouchEvent(ev);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			 return true;
		}
		return super.onTouchEvent(ev);


	}

	public void animate(float percent) {

		float f1 = 1 - percent * 0.3f;
		ViewHelper.setScaleX(mContentView, f1);
		ViewHelper.setScaleY(mContentView, f1);
		ViewHelper.setTranslationX(mLeftMenu, -mLeftMenu.getWidth() / 2.2f
				+ mLeftMenu.getWidth() / 2.2f * percent);
		ViewHelper.setScaleX(mLeftMenu, 0.5f + 0.5f * percent);
		ViewHelper.setScaleY(mLeftMenu, 0.5f + 0.5f * percent);
		ViewHelper.setAlpha(mLeftMenu, percent);
		if(null != btnLeft)
			ViewHelper.setAlpha(btnLeft, 1 - percent);

		int color = (Integer) evaluate(percent,
				Color.parseColor("#ff000000"), Color.parseColor("#00000000"));


		 Log.d("DragLayout", "animate :" + getBackground());
		getBackground().setColorFilter(color, Mode.SRC_OVER);
	}

	 public static Object evaluate(float fraction, Object startValue,
	            Object endValue) {
	        int startInt = (Integer) startValue;
	        int startA = (startInt >> 24) & 0xff;
	        int startR = (startInt >> 16) & 0xff;
	        int startG = (startInt >> 8) & 0xff;
	        int startB = startInt & 0xff;
	        int endInt = (Integer) endValue;
	        int endA = (endInt >> 24) & 0xff;
	        int endR = (endInt >> 16) & 0xff;
	        int endG = (endInt >> 8) & 0xff;
	        int endB = endInt & 0xff;
	        return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
	                | (int) ((startR + (int) (fraction * (endR - startR))) << 16)
	                | (int) ((startG + (int) (fraction * (endG - startG))) << 8)
	                | (int) ((startB + (int) (fraction * (endB - startB))));
	    }




}
