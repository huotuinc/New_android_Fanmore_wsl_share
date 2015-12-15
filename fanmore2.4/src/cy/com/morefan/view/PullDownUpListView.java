package cy.com.morefan.view;


import cindy.android.test.synclistview.NetImageListView;
import cy.com.morefan.R;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.L;
import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class PullDownUpListView extends NetImageListView implements OnScrollListener {

	private static final String TAG = "PullDownUpListView";
	public static final int STATUS_NORMAL = 0;//listview位置状态量
	public static final int STATUS_HEAD = 1;
	public static final int STATUS_FOOT = 2;
	//下拉刷新四个状态
	private final static int PULL_To_REFRESH = 3;//下拉刷新
	private final static int RELEASE_To_REFRESH = 4;//松开刷新
	private final static int REFRESHING = 5;//刷新中
	private final static int REFRESH_DONE = 6;//刷新完成
	//上拉加载四个状态
	private final static int PULL_TO_LOAD = 7;//上拉加载
	private final static int RELEASE_TO_LOAD = 8;//松开加载
	private final static int LOADING = 9;//加载中
	private final static int LOAD_DONE = 10;//加载完成


	private int currentStatus = STATUS_HEAD;


	private int state;//操作状态
	private boolean isBack;//松开刷新 》 下拉刷新，松开加载 》 上拉加载 状态改变的标识
	private boolean isRecored;// 用于保证startY的值在一个完整的touch事件中只被记录一次

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;
	//head
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;
	private LinearLayout headView;
	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;
	private int headContentHeight;
	// foot
	private LinearLayout footView;
	private TextView footTipsTextview;
	private ProgressBar footProgressBar;

	// 是否支持上拉下拉标识
	private boolean pullDownToRefresh;
	private boolean pullUpToLoad;
	private int totalCount;


	public interface OnRefreshOrLoadListener{
		void onRefresh();
		void onLoad();
	}
	private OnRefreshOrLoadListener listener;
	public PullDownUpListView(Context context) {
		super(context);
		initView(context);
	}

	public PullDownUpListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	public PullDownUpListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 设置监听
	 * @param listener
	 */
	public void setOnRefreshOrLoadListener(OnRefreshOrLoadListener listener){
		this.listener = listener;
	}
	/**
	 * 设置是否支持下拉刷新
	 * @param enable
	 */
	public void setPullDownToRefreshEnable(boolean enable){
		this.pullDownToRefresh = enable;
	}
	/**
	 * 设置是否支持上拉加载
	 * @param enable
	 */
	public void setPullUpToLoadEnable(boolean enable){
		this.pullUpToLoad = enable;
			footView.setVisibility(enable ? View.VISIBLE :View.GONE);
	}
	/**
	 * 用于刷新完成后更新head view
	 */
	public void onFinishRefresh(){
		state = REFRESH_DONE;
		changeHeaderViewByState();
	}
	/**
	 * 用于加载完成后更新foot view
	 */
	public void onFinishLoad(){
		state = LOAD_DONE;
		changeFootViewByVisible(View.GONE);
	}



	private void initView(Context context) {

		//head view
		headView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.head, null);
		arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView.findViewById(R.id.head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();


		addHeaderView(headView, null, false);

		//foot view
		footView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.foot, null);
		footProgressBar = (ProgressBar) footView.findViewById(R.id.foot_progressBar);
		footTipsTextview = (TextView) footView.findViewById(R.id.foot_tipsTextView);

		addFooterView(footView);
		footView.setVisibility(View.GONE);
		//设置footview在listview中不可点击
		footView.setClickable(true);
		footView.requestFocus();
		footView.requestFocusFromTouch();



		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);


		setOnScrollListener(this);
		state = REFRESH_DONE;
		//默认支持上拉加载，下拉刷新
		pullDownToRefresh = true;
		pullUpToLoad = true;

	}
	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
		private void measureView(View child) {
			ViewGroup.LayoutParams p = child.getLayoutParams();
			if (p == null)
				p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

			int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
			int lpHeight = p.height;
			int childHeightSpec;
			if (lpHeight > 0) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		}
	private int startY;
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.v(TAG, "ACTION_DOWN");
				if (currentStatus == STATUS_HEAD || currentStatus == STATUS_FOOT && !isRecored) {
					startY = (int) ev.getY();
				}
				break;
			case MotionEvent.ACTION_UP:
				setAcitionUp();
				break;
			case MotionEvent.ACTION_CANCEL:
				setAcitionUp();
				break;
			case MotionEvent.ACTION_HOVER_EXIT:
				break;
			case MotionEvent.ACTION_OUTSIDE:
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				int dy = tempY - startY;
				if(!isRecored){
					// done状态下
					if (state == REFRESH_DONE || state == LOAD_DONE) {
						if (pullDownToRefresh && dy > 0 && currentStatus == STATUS_HEAD) {//下拉刷新
							isRecored = true;
							startY = tempY;
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}else if (pullUpToLoad && dy < 0 && currentStatus == STATUS_FOOT) {//上拉加载
//							isRecored = true;
//							startY = tempY;
//							state = PULL_TO_LOAD;
//							changeFootViewByState();
						}
					}
				}
				if(dy == 0 || !isRecored)
					break;
				//避免临界点的跳越
				dy = tempY - startY;
				if(pullDownToRefresh && currentStatus == STATUS_HEAD)//下拉刷新
					pullDownToRefresh(dy);
				else if(pullUpToLoad && currentStatus == STATUS_FOOT)
					//pullUpToLoading(dy);
				break;
			}
		return super.onTouchEvent(ev);
	}

	private void setAcitionUp() {
		if (state != REFRESHING && state != LOADING) {
			switch (state) {
			case PULL_To_REFRESH:
				state = REFRESH_DONE;
				changeHeaderViewByState();
				break;
			case RELEASE_To_REFRESH:
				state = REFRESHING;
				changeHeaderViewByState();
				//执行刷新操作
				if(listener != null)
					listener.onRefresh();
				break;
			}
		}
		isRecored = false;
		isBack = false;
	}






	private void pullDownToRefresh(int dy) {
		if (state != REFRESHING && isRecored ) {
			// 松开刷新
			if (state == RELEASE_To_REFRESH) {
				//setSelection(0), 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
				setSelection(0);
				// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
				if ((dy / RATIO < headContentHeight)&& dy > 0) {
					state = PULL_To_REFRESH;
					changeHeaderViewByState();
				}else if (dy < 0) {// 一下子推到顶了
					state = REFRESH_DONE;
					changeHeaderViewByState();
				}
			}
			// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
			if (state == PULL_To_REFRESH) {
				setSelection(0);
				// 下拉刷新 > 松开刷新
				if ((dy / RATIO) >= headContentHeight) {
					state = RELEASE_To_REFRESH;
					isBack = true;
					changeHeaderViewByState();
				}else if (dy < 0) {// 下拉刷新 > 取消
					state = REFRESH_DONE;
					changeHeaderViewByState();
				}
			}

			//更新padding
			if(state == PULL_To_REFRESH || state == RELEASE_To_REFRESH)
			headView.setPadding(0, -1 * headContentHeight + dy / RATIO, 0, 0);
		}
	}



		/**
		 * 更新headView
		 */
		private void changeHeaderViewByState() {

			switch (state) {
			case RELEASE_To_REFRESH:
				arrowImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(animation);
				tipsTextview.setText("松开刷新");
				//Log.v(TAG, "当前状态，松开刷新");
				break;
			case PULL_To_REFRESH:
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.VISIBLE);
				// 是由RELEASE_To_REFRESH状态转变来的
				if (isBack) {
					isBack = false;
					arrowImageView.clearAnimation();
					arrowImageView.startAnimation(reverseAnimation);

					tipsTextview.setText("下拉刷新");
				} else {
					tipsTextview.setText("下拉刷新");
				}
				//Log.v(TAG, "当前状态，下拉刷新");
				break;
			case REFRESHING:
				footView.setVisibility(View.GONE);
				headView.setPadding(0, 0, 0, 0);
				progressBar.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.GONE);
				tipsTextview.setText("正在刷新...");
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				//Log.v(TAG, "当前状态,正在刷新...");
				break;
			case REFRESH_DONE:
				headView.setPadding(0, -1 * headContentHeight, 0, 0);
				progressBar.setVisibility(View.GONE);
				arrowImageView.clearAnimation();
				//arrowImageView.setImageResource(R.drawable.arrow);
				tipsTextview.setText("下拉刷新");
				lastUpdatedTextView.setVisibility(View.VISIBLE);

				//Log.v(TAG, "当前状态，refresh_done");
				break;
			}
		}

	/**
	 * 更新footView
	 */
		private void changeFootViewByVisible(int visible){
			L.i(">>>>>>>>changeFootViewByVisible");
			if(state == LOAD_DONE ){
				footView.setVisibility(View.GONE);
			}
//			if(totalCount < Constant.PAGESIZE){
//				state = LOAD_DONE;
//				footView.setVisibility(View.GONE);
//				return;
//
//			}
			if(!pullUpToLoad && totalCount > 2){
				state = LOAD_DONE;
				footView.setVisibility(View.VISIBLE);
				footProgressBar.setVisibility(View.GONE);
				footTipsTextview.setText("没有更多内容了");
				return;
			}

			//执行加载操作
			if(state == RELEASE_TO_LOAD ){
				footView.setVisibility(View.VISIBLE);
				footProgressBar.setVisibility(visible);
				footTipsTextview.setText(View.VISIBLE == visible ? "努力加载更多内容..." : "没有更多内容了");
				state = LOADING;
				if(listener != null)
					listener.onLoad();
			}


		}

	private boolean forOnce = true;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		totalCount = totalItemCount;
		if (firstVisibleItem + visibleItemCount == totalItemCount && firstVisibleItem != 0) {
			currentStatus = STATUS_FOOT;
			if( forOnce && state != LOADING){
				forOnce = false;
				state = RELEASE_TO_LOAD;
				changeFootViewByVisible(View.VISIBLE);
			}

		} else if(firstVisibleItem == 0){
			currentStatus = STATUS_HEAD;
		}else{
			forOnce = true;
			currentStatus = STATUS_NORMAL;
		}

	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}




}
