package cindy.android.test.synclistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class NetImageListView extends ListView implements OnScrollListener{
	private SyncImageLoaderHelper syncImageLoader;

	
	public NetImageListView(Context context) {
		super(context);
		init(context);
	}

	public NetImageListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public NetImageListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context mContext){
		syncImageLoader = new SyncImageLoaderHelper(mContext);
	}

	public SyncImageLoaderHelper getImageLoader() {
		return syncImageLoader;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				DebugUtil.debug("SCROLL_STATE_FLING");
				syncImageLoader.lock();
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				DebugUtil.debug("SCROLL_STATE_IDLE");
				loadImage();
				//loadImage();
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				syncImageLoader.lock();
				break;

			default:
				break;
		}
	}
	
	public void loadImage(){
		int start = getFirstVisiblePosition();
		int end = getLastVisiblePosition();
		if(end >= getCount()){
			end = getCount() -1;
		}
		syncImageLoader.setLoadLimit(start, end);
		syncImageLoader.unlock();
	}
}
