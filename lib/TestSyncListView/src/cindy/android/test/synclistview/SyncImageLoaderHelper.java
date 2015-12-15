package cindy.android.test.synclistview;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import cindy.android.test.synclistview.ImageLoader.IconLoadFinishListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SyncImageLoaderHelper implements IconLoadFinishListener {

	private Object lock = new Object();

	private boolean mAllowLoad = true;

	private boolean firstLoad = true;

	private int mStartLoadLimit = 0;

	private int mStopLoadLimit = 0;

	final Handler handler = new Handler();

	private HashMap<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	public interface OnImageLoadListener {
		public void onImageLoad(Integer t, Drawable drawable);
		public void onError(Integer t);
	}



	public void setLoadLimit(int startLoadLimit,int stopLoadLimit){
		if(startLoadLimit > stopLoadLimit){
			return;
		}
		mStartLoadLimit = startLoadLimit;
		mStopLoadLimit = stopLoadLimit;
	}

	public void restore(){
		mAllowLoad = true;
		firstLoad = true;
	}

	public void lock(){
		mAllowLoad = false;
		firstLoad = false;
	}

	public void unlock(){
		mAllowLoad = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}



//	public SyncImageLoaderHelper(Context mContext){
//		imageLoader = new ImageLoader(mContext, this);
//	}
	private Context mContext;
	public SyncImageLoaderHelper(Context mContext) {
		this.mContext = mContext;
		imageLoader = new ImageLoader(mContext, this);
	}
	private ImageLoader imageLoader;

	//position, holder.imgTask, holder.bar, data.smallImgUrl, Constant.IMAGE_PATH_TASK
	public void loadImage(int position, ImageView img, ProgressBar bar, String url, String path){
		if(img != null)
			img.setBackgroundResource(R.drawable.picreviewre_fresh_bg);
		if(TextUtils.isEmpty(url)){
			return;
		}

//		img.setTag(position);
//		ImageData imageData = mImageCache.get(position);
//		if (imageData == null) {
//			imageData = new ImageData(img, bar, url, path);
//			mImageCache.put(position, imageData);
//
//		}

		load(position, img, bar, url, path);
	}

	/**
	 * 只下载
	 * @param url
	 * @param path
	 */
	public void loadImage(String url, String path){
		imageLoader.loadIcon(null, null, url, path);
	}

	private void load(final int position, final ImageView img, final ProgressBar bar, final String url, final String path) {
		if(!mAllowLoad){
			DebugUtil.debug("prepare to load");
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if(mAllowLoad && firstLoad){
			imageLoader.loadIcon(img, bar, url, path);
		}

		if(mAllowLoad && position <= mStopLoadLimit && position >= mStartLoadLimit){
			imageLoader.loadIcon(img, bar, url, path);
		}

//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				if(!mAllowLoad){
//					DebugUtil.debug("prepare to load");
//					synchronized (lock) {
//						try {
//							lock.wait();
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//
//				if(mAllowLoad && firstLoad){
//					imageLoader.loadIcon(data.img, data.url, data.path);
//				}
//
//				if(mAllowLoad && position <= mStopLoadLimit && position >= mStartLoadLimit){
//					imageLoader.loadIcon(data.img, data.url, data.path);
//				}
//			}
//
//		}).start();

	}
//	public void loadImage(Integer t, String imageUrl,
//			OnImageLoadListener listener) {
//		final OnImageLoadListener mListener = listener;
//		final String mImageUrl = imageUrl;
//		final Integer mt = t;
//
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				if(!mAllowLoad){
//					DebugUtil.debug("prepare to load");
//					synchronized (lock) {
//						try {
//							lock.wait();
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//
//				if(mAllowLoad && firstLoad){
//					loadImage(mImageUrl, mt, mListener);
//				}
//
//				if(mAllowLoad && mt <= mStopLoadLimit && mt >= mStartLoadLimit){
//					loadImage(mImageUrl, mt, mListener);
//				}
//			}
//
//		}).start();
//	}



	@Override
	public void onIconLoadFinished(ImageView img, Bitmap bitmap) {
//		ImageData imageData = mImageCache.get(Integer.valueOf(img.getTag().toString()));
//		imageData.bar.setVisibility(View.GONE);

	}

	@Override
	public void onIconLoadProgress(final ImageView img,final int progress) {
//		new Thread(){
//			public void run() {
//				String url = mImageCache.get(Integer.valueOf(img.getTag().toString())).url;
//				Iterator<ImageData> iterator = mImageCache.values().iterator();
//				while (iterator.hasNext()) {
//					ImageData imageData = iterator.next();
//					if(url.equals(imageData.url)){
//						imageData.progress = progress;
//						imageData.bar.setProgress(progress);
//					}
//				}
//
//
//			};
//		}.start();

	}
}
