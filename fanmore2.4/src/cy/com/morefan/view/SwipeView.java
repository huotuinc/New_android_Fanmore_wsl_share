package cy.com.morefan.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

public class SwipeView extends ViewGroup {
	private static final long RETURN_TO_ORIGINAL_POSITION_TIMEOUT = 100;
	 private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
	 private static final int REFRESH_TRIGGER_DISTANCE = 120;
	 private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;

	 private View mTarget; //the content that gets pulled down
	 private int mCurrentTargetOffsetTop;
	 private int mOriginalOffsetTop;
	 private MotionEvent mDownEvent;
		private float mPrevY;
		 private int mMediumAnimationDuration;
		private boolean mRefreshing = false;
		private float mDistanceToTriggerSync = -1;
		 private final DecelerateInterpolator mDecelerateInterpolator;
		// Target is returning to its start offset because it was cancelled or a
		// refresh was triggered.
		private boolean mReturningToStart;
		private int mTouchSlop;
		 private int mFrom;
		 private OnRefreshListener mListener;



	public SwipeView(Context context) {
		this(context, null,0);
	}

	public SwipeView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SwipeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
		mMediumAnimationDuration = getResources().getInteger(android.R.integer.config_mediumAnimTime);

	}
	 @Override
	 public void onAttachedToWindow() {
	     super.onAttachedToWindow();
	     removeCallbacks(mCancel);
	     removeCallbacks(mReturnToStartPosition);
	 }

	 @Override
	 public void onDetachedFromWindow() {
	     super.onDetachedFromWindow();
	     removeCallbacks(mReturnToStartPosition);
	     removeCallbacks(mCancel);
	 }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		 final int width =  getMeasuredWidth();
		 final int height = getMeasuredHeight();
		 if (getChildCount() == 0) {
		     return;
		 }
		 final View child = getChildAt(0);
		 final int childLeft = getPaddingLeft();
		 final int childTop = mCurrentTargetOffsetTop + getPaddingTop();
		 final int childWidth = width - getPaddingLeft() - getPaddingRight();
		 final int childHeight = height - getPaddingTop() - getPaddingBottom();
		 child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
	}

	@Override
	    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	        if (getChildCount() > 1 && !isInEditMode()) {
	            throw new IllegalStateException("SwipeRefreshLayout can host only one direct child");
	        }
	        if (getChildCount() > 0) {
	            getChildAt(0).measure(
	                    MeasureSpec.makeMeasureSpec(
	                            getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
	                            MeasureSpec.EXACTLY),
	                    MeasureSpec.makeMeasureSpec(
	                            getMeasuredHeight() - getPaddingTop() - getPaddingBottom(),
	                            MeasureSpec.EXACTLY));
	        }
	    }

	public boolean canChildScrollUp() {

		        if (android.os.Build.VERSION.SDK_INT < 14) {
		            if (mTarget instanceof AbsListView) {
		                final AbsListView absListView = (AbsListView) mTarget;
		                return absListView.getChildCount() > 0
		                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
		                                .getTop() < absListView.getPaddingTop());
		            } else {
		                return mTarget.getScrollY() > 0;
		            }
		        } else {

		            return ViewCompat.canScrollVertically(mTarget, -1);
		        }
		    }

	/**
     * 检查是否可以上拉,对于版本14以下的暂不支持
     * @return
     */
    public boolean canChildScrollDown() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
               //仿照canChildScrollUp中的这部分，判断添加<14的代码
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                                .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, 1);
        }
    }



	private void ensureTarget() {
		        // Don't bother getting the parent height if the parent hasn't been laid out yet.
		        if (mTarget == null) {
		            if (getChildCount() > 1 && !isInEditMode()) {
		                throw new IllegalStateException(
		                        "SwipeRefreshLayout can host only one direct child");
		            }
		            mTarget = getChildAt(0);
		            mOriginalOffsetTop = mTarget.getTop() + getPaddingTop();
		        }
		        if (mDistanceToTriggerSync == -1) {
		            if (getParent() != null && ((View)getParent()).getHeight() > 0) {
		                final DisplayMetrics metrics = getResources().getDisplayMetrics();
		                mDistanceToTriggerSync = (int) Math.min( ((View) getParent()) .getHeight() * MAX_SWIPE_DISTANCE_FACTOR,
		                                REFRESH_TRIGGER_DISTANCE * metrics.density);
		            }
		        }
		    }




	@Override
	    public boolean onInterceptTouchEvent(MotionEvent ev) {
	        ensureTarget();
	        boolean handled = false;
	        if (mReturningToStart && ev.getAction() == MotionEvent.ACTION_DOWN) {
	            mReturningToStart = false;
	        }
	        System.out.println("*******onInterceptTouchEvent:" + ev.getAction());
	        if (isEnabled() && !mReturningToStart && (!canChildScrollUp() || !canChildScrollDown())) {
	            handled = onTouchEvent(ev);
	        }
	        System.out.println("*******onInterceptTouchEvent:" + handled);
	        return !handled ? super.onInterceptTouchEvent(ev) : handled;
	    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		 System.out.println(">>>>>onTouchEvent:" + event.getAction());
		final int action = event.getAction();
		boolean handled = false;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mDownEvent = MotionEvent.obtain(event);
			mPrevY = mDownEvent.getY();

			handled = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mDownEvent != null && !mReturningToStart) {
				final float eventY = event.getY();
				float yDiff = eventY - mDownEvent.getY();
				System.out.println(">>>scrollUp:" + ViewCompat.canScrollVertically(mTarget, -1) + ",scrollDown:" +ViewCompat.canScrollVertically(mTarget, 1));
				System.out.println(">>>>dy:" + yDiff + ",slop:" + mTouchSlop);
				if ((yDiff > mTouchSlop && !canChildScrollUp()) || (yDiff < -mTouchSlop && !canChildScrollDown())) {
					// User velocity passed min velocity; trigger a refresh
//					if (yDiff > mDistanceToTriggerSync) {
//						// User movement passed distance; trigger a refresh
//						startRefresh();
//						handled = true;
//						break;
//					}
					//else {
						// Just track the user's movement
//						setTriggerPercentage(mAccelerateInterpolator
//								.getInterpolation(yDiff
//										/ mDistanceToTriggerSync));
						float offsetTop = yDiff;
						if (mPrevY > eventY) {
							offsetTop = yDiff - mTouchSlop;
						}
						updateContentOffsetTop((int) (offsetTop/3));
						if (mPrevY > eventY && (mTarget.getTop() < mTouchSlop)) {
							// If the user puts the view back at the top, we
							// don't need to. This shouldn't be considered
							// cancelling the gesture as the user can restart
							// from the top.
							removeCallbacks(mCancel);
						} else {
							//updatePositionTimeout();
						}
						mPrevY = event.getY();
						handled = true;
				//	}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			updatePositionTimeout();
//			if (yDiff > mDistanceToTriggerSync) {
//				// User movement passed distance; trigger a refresh
//				startRefresh();
//				handled = true;
//				break;
//			}
			//updateContentOffsetTop(0);
			if (mDownEvent != null) {
				mDownEvent.recycle();
				mDownEvent = null;
			}
			break;
		}
		System.out.println(">>>>>onTouchEvent:" + super.onTouchEvent(event));
		return handled;
	}
	 private void startRefresh() {
		 removeCallbacks(mCancel);
		 mReturnToStartPosition.run();
		 setRefreshing(true);
		 if(null != mListener)
			 mListener.onRefresh();

	}
	 public void setOnRefreshListener(OnRefreshListener listener) {
		         mListener = listener;
		     }

	 public void setRefreshing(boolean refreshing) {
		       if (mRefreshing != refreshing) {
		           ensureTarget();
		           mRefreshing = refreshing;
		       }
		   }

	private void updatePositionTimeout() {
		        removeCallbacks(mCancel);
		         postDelayed(mCancel, RETURN_TO_ORIGINAL_POSITION_TIMEOUT);
		    }
	 private void updateContentOffsetTop(int targetTop) {
		        final int currentTop = mTarget.getTop();
//		        if (targetTop > mDistanceToTriggerSync) {
//		            targetTop = (int) mDistanceToTriggerSync;
//		        } else
//		        	if (targetTop < 0) {
//		            targetTop = 0;
//		        }
		        setTargetOffsetTopAndBottom(targetTop - currentTop);
		    }

	private final Animation mAnimateToStartPosition = new Animation() {
		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop = 0;
			if (mFrom != mOriginalOffsetTop) {
				targetTop = (mFrom + (int) ((mOriginalOffsetTop - mFrom) * interpolatedTime));
			}
			final int currentTop = mTarget.getTop();
			int offset = targetTop - currentTop;

//			if (offset + currentTop < 0) {
//				offset = 0 - currentTop;
//			}
			setTargetOffsetTopAndBottom(offset);
		}
	};


	 private final Runnable mReturnToStartPosition = new Runnable() {

       @Override
       public void run() {
           mReturningToStart = true;
           animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(),
                   mReturnToStartPositionListener);
       }

   };

	// Cancel the refresh gesture and animate everything back to its original state.
	private final Runnable mCancel = new Runnable() {

	    @Override
	    public void run() {
	        mReturningToStart = true;
	        // Timeout fired since the user last moved their finger; animate the
	        // trigger to 0 and put the target back at its original position
	        animateOffsetToStartPosition(mCurrentTargetOffsetTop + getPaddingTop(), mReturnToStartPositionListener);
	    }

	};
	 private void animateOffsetToStartPosition(int from, AnimationListener listener) {
		        mFrom = from;
		       mAnimateToStartPosition.reset();
		       mAnimateToStartPosition.setDuration(mMediumAnimationDuration);
		         mAnimateToStartPosition.setAnimationListener(listener);
		       mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
		       mTarget.startAnimation(mAnimateToStartPosition);
		    }

	private void setTargetOffsetTopAndBottom(int offset) {
		mTarget.offsetTopAndBottom(offset);
		mCurrentTargetOffsetTop = mTarget.getTop();
	}


	 private final AnimationListener mReturnToStartPositionListener = new BaseAnimationListener() {
       @Override
       public void onAnimationEnd(Animation animation) {
           // Once the target content has returned to its start position, reset
           // the target offset to 0
           mCurrentTargetOffsetTop = 0;
       }
   };


   public interface OnRefreshListener {
	          public void onRefresh();
	     }

	 private class BaseAnimationListener implements AnimationListener {
       @Override
       public void onAnimationStart(Animation animation) {
       }

       @Override
       public void onAnimationEnd(Animation animation) {
       }

       @Override
       public void onAnimationRepeat(Animation animation) {
       }
   }
}
