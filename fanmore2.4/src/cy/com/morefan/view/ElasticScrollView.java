package cy.com.morefan.view;


import cy.com.morefan.util.L;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.FrameLayout.LayoutParams;

public class ElasticScrollView extends ScrollView {
	public interface OnRefreshListener {
		public void onRefresh(float per, ScrollType type);
	}

	/**
	 * PULL_To_REFRESH    下拉刷新
	 * RELEASE_To_REFRESH 松开刷新
	 * REFRESHING         刷新中
	 *
	 * @author edushi
	 *
	 */
	public enum ScrollType{
		DONE, RELEASE_To_REFRESH, PULL_To_REFRESH, REFRESHING
	}
	private static final String TAG = "ElasticScrollView";

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;
	private ScrollType curType;
	private OnRefreshListener refreshListener;



	private LinearLayout innerLayout;
	private View headView;
	private int headContentWidth;
	private int headContentHeight;

	private View bottomLayout;
	private int bottomHeight;

	/**
	 * 是否有弹性
	 */
	private boolean elasticable;


	private int startY;


	public ElasticScrollView(Context context) {
		super(context);
		init(context);
	}

	public ElasticScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if(getChildCount() > 0){
			innerLayout = (LinearLayout) getChildAt(0);
			innerLayout.addView(bottomLayout);
		}
	}

	public void addHeadView(View headView){
		this.headView = headView;
		measureView(headView);
		//
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.i("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);

		 innerLayout.addView(headView,0);

	}

	private void init(Context context) {
		bottomLayout = new LinearLayout(context);
		bottomLayout.setLayoutParams(new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		measureView(bottomLayout);
		bottomHeight = bottomLayout.getMeasuredHeight();
		bottomLayout.setPadding(0, 0, 0, -1 * bottomHeight);
		bottomLayout.invalidate();

		curType = ScrollType.DONE;
		elasticable = true;
	}
	public void setElasticAlbe(boolean elasticable){
		this.elasticable = elasticable;
	}


	private boolean isBottom;//是否滑动底部
	//private boolean isBottomRecord;
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt){
		isBottom = t + getHeight() >=  computeVerticalScrollRange();
	}
	@SuppressLint("NewApi")
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
			boolean clampedY) {
		if(getPaddingTop() == 0)
			super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}
	public void reset(){
		if(null != headView)
			headView.setPadding(0, -headContentHeight, 0, 0);
		bottomLayout.setPadding(0, 0, 0, -bottomHeight);
		setPadding(0, 0, 0, 0);
		invalidate();
	}
	private boolean isRecored;//是否已记录
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		L.i(">>>>>action:" + event.getAction());
		if (elasticable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_CANCEL:
				reset();
				break;
			case MotionEvent.ACTION_DOWN:
				if (!isRecored && (getScrollY() == 0 || isBottom )) {
					isRecored = true;
					startY = (int) event.getY();
					Log.i(TAG, "在down时候记录当前位置‘");
				}
				break;
			case MotionEvent.ACTION_UP:
				Log.i(TAG, "curType:" + curType + isBottom);
				if (curType != ScrollType.REFRESHING) {
					if (curType == ScrollType.DONE) {
						// 什么都不做
						setPadding(0, 0, 0, 0);
						if(null != headView)
							headView.setPadding(0, -1 * headContentHeight, 0, 0);
						bottomLayout.setPadding(0, 0, 0,-bottomHeight);
					}
					if (curType == ScrollType.PULL_To_REFRESH) {
						curType = ScrollType.DONE;
						//changeHeaderViewByState();
						if(null != headView)
							headView.setPadding(0, -1 * headContentHeight, 0, 0);
						onListenerBack(0);
						Log.i(TAG, "由下拉刷新状态，到done状态");
					}
					if (curType == ScrollType.RELEASE_To_REFRESH) {
						curType = ScrollType.REFRESHING;
						//changeHeaderViewByState();
						if(null != headView)
							headView.setPadding(0, 0, 0, 0);
						onListenerBack(0);
						Log.i(TAG, "由松开刷新状态，到done状态");
					}
				}else{
					setPadding(0, 0, 0, 0);
				}
				isRecored = false;
				if(isBottom || null == headView){
					setPadding(0, 0, 0, 0);
					bottomLayout.setPadding(0, 0, 0,-bottomHeight);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				if (!isRecored && (getScrollY() == 0 || isBottom )) {
					Log.i(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}
				int dy = tempY - startY;
				if(isBottom){
					bottomLayout.setPadding(0, 0, 0,(-dy - bottomHeight) / RATIO);
					//setPadding(0, 0, 0,(-dy) / RATIO );
				}

				//只加弹性
				if(null == headView){
					if(getScrollY() == 0)
						setPadding(0, dy / RATIO, 0, 0);
					break;
				}
				if(curType == ScrollType.REFRESHING || !isRecored || dy == 0)
					break;
				//避免临界点的跳越
				dy = tempY - startY;

				//已添加headView
					// 可以松手去刷新了
					if (curType == ScrollType.RELEASE_To_REFRESH) {

						if ((dy / RATIO < headContentHeight) && dy > 0) {
							curType = ScrollType.PULL_To_REFRESH;
							//changeHeaderViewByState();
							//onListenerBack();
							Log.i(TAG, "由松开刷新状态转变到下拉刷新状态");
						}else if (dy <= 0) {// 一下子推到顶了
							curType = ScrollType.DONE;
							//onListenerBack();
							//changeHeaderViewByState();
							Log.i(TAG, "由松开刷新状态转变到done状态");
						} else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (curType == ScrollType.PULL_To_REFRESH) {

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if (dy / RATIO >= headContentHeight) {
							curType = ScrollType.RELEASE_To_REFRESH;
							//changeHeaderViewByState();
							//onListenerBack();
							Log.i(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}else if (dy <= 0) {// 上推到顶了
							curType = ScrollType.DONE;
							//changeHeaderViewByState();
							//onListenerBack();
							Log.i(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (curType == ScrollType.DONE) {
						if (dy > 0) {
							curType = ScrollType.PULL_To_REFRESH;
//							changeHeaderViewByState();
							//onListenerBack();
						}
					}

					float per = 0;
					// 更新headView的size
					if (curType == ScrollType.PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight + dy / RATIO, 0, 0);
						per = (-1 * headContentHeight + dy / RATIO)/ (float)headContentHeight;
					}

					// 更新headView的paddingTop
					if (curType == ScrollType.RELEASE_To_REFRESH) {
						int top = dy / RATIO - headContentHeight;
						L.i(">>>>>>>>top:" + top);
						headView.setPadding(0, top, 0, 0);
						per = (dy / RATIO - headContentHeight)/(float)headContentHeight;
					}
					//L.i(">>>>per:" + per);
					onListenerBack(per);
					if (getPaddingBottom() != 0) {
						//canReturn = false;
						return true;
					}
				break;
			}
		}
		return super.onTouchEvent(event);
	}

	private void onListenerBack(float per) {
		if(null == refreshListener)
			return;
		refreshListener.onRefresh(per, curType);

	}


	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		elasticable = true;
	}


	public void onRefreshComplete() {
		curType = ScrollType.DONE;
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		setPadding(0, 0, 0, 0);
		//lastUpdatedTextView.setText("最近更新:" + new Date().toLocaleString());
		//changeHeaderViewByState();
		invalidate();
		//scrollTo(0, 0);
	}



}
