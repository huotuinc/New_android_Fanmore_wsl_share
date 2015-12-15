package cy.com.morefan.view;


import cindy.android.test.synclistview.NetImageListView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class LoadHistoryListView extends NetImageListView implements OnScrollListener {

	private final static int LOADING = 1;//加载中
	private final static int LOAD_DONE = 2;//加载完成




	private int state;//操作状态




	public interface OnLoadHistoryListener{
		void onLoadHistory();
	}
	private OnLoadHistoryListener listener;
	public LoadHistoryListView(Context context) {
		super(context);
		init();
	}



	public LoadHistoryListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public LoadHistoryListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	private void init() {
		setOnScrollListener(this);

	}

	/**
	 * 设置监听
	 * @param listener
	 */
	public void setOnLoadHistoryListener(OnLoadHistoryListener listener){
		this.listener = listener;
	}
	/**
	 * 用于加载完成
	 */
	public void onFinishLoadHistory(){
		state = LOAD_DONE;
	}

	private void loadHistory(){
		state = LOADING;
		listener.onLoadHistory();
	}

	private boolean forOnce = true;
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem + visibleItemCount == totalItemCount && firstVisibleItem != 0) {
			forOnce = true;
		} else if(firstVisibleItem == 0){
			if( forOnce && state != LOADING && totalItemCount > visibleItemCount){
				forOnce = false;
				loadHistory();
			}
		}else{
			forOnce = true;
		}

	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}




}
