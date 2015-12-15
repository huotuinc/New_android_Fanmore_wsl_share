package cindy.android.test.synclistview;

import java.io.File;
import java.util.Vector;




import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BookItemAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	private Context mContext;
	private Vector<BookModel> mModels = new Vector<BookModel>();
	private ListView mListView;
	SyncImageLoaderHelper syncImageLoader;
	String path = Environment.getExternalStorageDirectory().toString() + File.separator + ".mfimage/test";
	
	public BookItemAdapter(Context context,SyncImageLoaderHelper syncImageLoader){
		mInflater = LayoutInflater.from(context);
		//syncImageLoader = new SyncImageLoaderHelper();
		mContext = context;
		//mListView = listView;
		this.syncImageLoader = syncImageLoader;
		
	//	mListView.setOnScrollListener(onScrollListener);
	}

	
	public void addBook(String book_name,String out_book_url,String out_book_pic){
		BookModel model = new BookModel();
		model.book_name =book_name;
		model.out_book_url = out_book_url;
		model.out_book_pic = out_book_pic;
		mModels.add(model);
	}
	
	public void clean(){
		mModels.clear();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mModels.size();
	}

	@Override
	public Object getItem(int position) {
		if(position >= getCount()){
			return null;
		}
		return mModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.book_item_adapter, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.bar = (ProgressBar) convertView.findViewById(R.id.load_pb);
			holder.sItemTitle =  (TextView) convertView.findViewById(R.id.sItemTitle);
			//holder.sItemInfo =  (TextView) convertView.findViewById(R.id.sItemInfo);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		BookModel model = mModels.get(position);
		
		holder.sItemTitle.setText(model.book_name);
		//sItemInfo.setText(model.out_book_url);
		//iv.setBackgroundResource(R.drawable.rc_item_bg);
		syncImageLoader.loadImage(position, holder.img, holder.bar, model.out_book_pic, path);
		//syncImageLoader.loadImage(position,model.out_book_pic,imageLoadListener);
		return  convertView;
	}
	public class ViewHolder{
		ImageView img;
		ProgressBar bar;
		TextView sItemTitle;
		//TextView sItemInfo;
	}

//	SyncImageLoaderHelper.OnImageLoadListener imageLoadListener = new SyncImageLoaderHelper.OnImageLoadListener(){
//
//		@Override
//		public void onImageLoad(Integer t, Drawable drawable) {
//			//BookModel model = (BookModel) getItem(t);
//			View view = mListView.findViewWithTag(t);
//			if(view != null){
//				ImageView iv = (ImageView) view.findViewById(R.id.sItemIcon);
//				iv.setBackgroundDrawable(drawable);
//			}
//		}
//		@Override
//		public void onError(Integer t) {
//			BookModel model = (BookModel) getItem(t);
//			View view = mListView.findViewWithTag(model);
//			if(view != null){
//				ImageView iv = (ImageView) view.findViewById(R.id.sItemIcon);
//				iv.setBackgroundResource(R.drawable.rc_item_bg);
//			}
//		}
//		
//	};
	
//	public void loadImage(){
//		int start = mListView.getFirstVisiblePosition();
//		int end =mListView.getLastVisiblePosition();
//		if(end >= getCount()){
//			end = getCount() -1;
//		}
//		syncImageLoader.setLoadLimit(start, end);
//		syncImageLoader.unlock();
//	}
	
//	AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
//		
//		@Override
//		public void onScrollStateChanged(AbsListView view, int scrollState) {
//			switch (scrollState) {
//				case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
//					DebugUtil.debug("SCROLL_STATE_FLING");
//					syncImageLoader.lock();
//					break;
//				case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
//					DebugUtil.debug("SCROLL_STATE_IDLE");
//					loadImage();
//					//loadImage();
//					break;
//				case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
//					syncImageLoader.lock();
//					break;
//	
//				default:
//					break;
//			}
//			
//		}
//		
//		@Override
//		public void onScroll(AbsListView view, int firstVisibleItem,
//				int visibleItemCount, int totalItemCount) {
//			// TODO Auto-generated method stub
//			
//		}
//	};
}
