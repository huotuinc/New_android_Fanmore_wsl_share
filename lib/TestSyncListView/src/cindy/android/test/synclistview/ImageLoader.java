package cindy.android.test.synclistview;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;



public class ImageLoader implements Callback{
	public abstract static interface IconLoadFinishListener {
		void onIconLoadFinished(ImageView img, Bitmap bitmap);
		void onIconLoadProgress(ImageView img, int progress);
	}
	private final int PER_MAX_LOAD_TIME = 3;//单张图最大下载次数

	private static final String LOADER_THREAD_NAME = "FileIconLoader";
	/**
	 * Type of message sent by the UI thread to itself to indicate that some
	 * photos need to be loaded.
	 */
	private static final int MESSAGE_REQUEST_LOADING = 1;
	private static final int MESSAGE_REQUEST_LOADING_ONLY = 4;
	/**
	 * Type of message sent by the loader thread to indicate that some photos
	 * have been loaded.
	 */
	private static final int MESSAGE_ICON_LOADED = 2;
	private static final int MESSAGE_ICON_PROGRESS = 3;
	/**
	 * A soft cache for image thumbnails. the key is file path
	 */
	private final static ConcurrentHashMap<String, BitmapHolder> mImageCache = new ConcurrentHashMap<String, BitmapHolder>();
	private ArrayList<FileData> downDatas = new ArrayList<ImageLoader.FileData>();
	/**
	 * A map from ImageView to the corresponding photo ID. Please note that this
	 * photo ID may change before the photo loading request is started.
	 */
	private final ConcurrentHashMap<ImageView, FileData> mPendingRequests = new ConcurrentHashMap<ImageView, FileData>();
	/**
	 * Handler for messages sent to the UI thread.
	 */
	private final Handler mMainThreadHandler = new Handler(this);
	/**
	 * Thread responsible for loading photos from the database. Created upon the
	 * first request.
	 */
	private LoaderThread mLoaderThread;

	//private final Context mContext;

	private IconLoadFinishListener iconLoadListener;
	/**
	 * Flag indicating if the image loading is paused.
	 */
	private boolean mPaused;
	/**
	 * A gate to make sure we only send one instance of MESSAGE_PHOTOS_NEEDED at
	 * a time.
	 */
	private boolean mLoadingRequested;

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_REQUEST_LOADING:
		case MESSAGE_REQUEST_LOADING_ONLY:
			mLoadingRequested = false;
			if (!mPaused) {
				if (mLoaderThread == null) {
					mLoaderThread = new LoaderThread();
					mLoaderThread.start();
				}

				mLoaderThread.requestLoading(msg.what);
			}
			return true;

		case MESSAGE_ICON_LOADED:
			if (!mPaused) {
				processLoadedIcons(msg.obj.toString());
			}
			return true;

		case MESSAGE_ICON_PROGRESS:
			updateProgress( msg.obj.toString(), msg.arg1);
			break;
		}
		return false;
	}


	/**
	 * Constructor.
	 *
	 * @param context
	 *            content context
	 */
	public ImageLoader(Context mContext, IconLoadFinishListener l) {
		this.mContext = mContext;
		iconLoadListener = l;
	}

	private void updateProgress(String url, int progress) {
		String fileName = url.substring(url.lastIndexOf("/") + 1);
		System.out.println(fileName+">>>>" + progress);
		Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
		BitmapHolder holder = mImageCache.get(url);
		if(holder == null)
			return;
		holder.progress = progress;
		while (iterator.hasNext()) {
			ImageView img = iterator.next();
			FileData data = mPendingRequests.get(img);
			if(data != null && url.equals(data.url) && data.bar != null)
				data.bar.setProgress(progress);
		}

	}

	/**
	 * Goes over pending loading requests and displays loaded photos. If some of
	 * the photos still haven't been loaded, sends another request for image
	 * loading.
	 */
	private void processLoadedIcons(String url) {
		Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();

		while (iterator.hasNext()) {
			ImageView img = iterator.next();
			FileData data = mPendingRequests.get(img);
//			if(data.loadTime == PER_MAX_LOAD_TIME){
//				iterator.remove();
//				continue;
//			}
			if(url.equals(data.url)){
				//FileData fileData = mPendingRequests.get(view);
				boolean loaded = loadCachedIcon(img, data.bar, data.url, data.path);
				String fileName = data.url.substring(data.url.lastIndexOf("/") + 1);
				//System.out.println(fileName+">>>>" + progress);
				System.out.println(fileName + ":"+loaded);
				if (loaded) {
					if(data.bar != null)
						data.bar.setVisibility(View.GONE);
					iterator.remove();

					iconLoadListener.onIconLoadFinished(img, null);
				}else{
					data.loadTime ++;
					if(data.loadTime == 3){
						iterator.remove();
					}
				}
			}

		}

//		if (!mPendingRequests.isEmpty()) {
//			Iterator<ImageView> iterator2 = mPendingRequests.keySet().iterator();
//			while (iterator2.hasNext()){
//				ImageView img2 = iterator2.next();
//				FileData data = mPendingRequests.get(img2);
//				data.loadTime ++;
//			}
//
//			requestLoading();
//		}
	}
	class FileData{
		public FileData(String path, String url){
			this.path = path;
			this.url = url;
		}
		public ProgressBar bar;
		public ImageView img;
		public int loadTime;
		public String url;
		public String path;
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return url + "," + path;
		}
	}

	/**
	 * Load photo into the supplied image view. If the photo is already cached,
	 * it is displayed immediately. Otherwise a request is sent to load the
	 * photo from the database.
	 *
	 * @param id, database id
	 */
	private FileData downOnlyData;
	public boolean loadIcon(ImageView img, ProgressBar bar, String url, String filePath) {
		boolean loaded = false;
		if(img == null){
			downOnlyData = new FileData(filePath, url);
			requestLoadingOnly();
		}else{
			//先从内存中获取
			loaded = loadCachedIcon(img, bar, url, filePath);
			System.out.println(mImageCache.size() + "loaded:" + loaded);
			if (loaded) {
				//iconLoadListener.onIconLoadFinished(url, null);
				downDatas.remove(mPendingRequests.get(img));
				mPendingRequests.remove(img);
			} else {
				FileData f = mPendingRequests.get(img);
				if(f == null){
					f = new FileData(filePath, url);
					f.img = img;
					f.bar = bar;
					mPendingRequests.put(img, f);
					downDatas.add(f);
					if (!mPaused) {
						// Send a request to start loading photos
						requestLoading();
					}
				}
		}



		}
		return loaded;
	}

//	public Bitmap getCacheBitmap(String url) {
//		BitmapHolder holder = mImageCache.get(url);
//		if(holder == null)
//			return null;
//		if(holder.bitmapRef == null)
//			return null;
//		return holder.bitmapRef.get();
//	}

	/**
	 * Checks if the photo is present in cache. If so, sets the photo on the
	 * view, otherwise sets the state of the photo to
	 * {@link BitmapHolder#NEEDED}
	 */
	private boolean loadCachedIcon(ImageView view, ProgressBar bar, String url, String path) {

        BitmapHolder holder = mImageCache.get(url);

//        //避免未下载完成时，imageview利用带来的图片错位
//        if(view != null)
//        	view.setBackgroundResource(R.drawable.picreviewre_fresh_above);

        if (holder == null) {
            holder = new BitmapHolder();
            holder.status = loadStatus.NeedLoaded;
            mImageCache.put(url, holder);
        } else {
        	//避免未下载完成时，imageview利用带来的图片错位
            if(view != null)
            	view.setBackgroundResource(holder.status == loadStatus.Error ? R.drawable.bg_error : R.drawable.picreviewre_fresh_above);

        	if(bar != null){
        		if(holder.progress == 100){
            		bar.setVisibility(View.GONE);
            	}else{
            		bar.setVisibility(View.VISIBLE);
            		bar.setProgress(holder.progress);
            	}
        	}



        	if(view == null && holder.bitmapRef != null)
        		return true;


            if (holder.isNull()) {
                return false;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if(view != null)
            if ( holder.setImageView(view) && holder.status == loadStatus.Loaded ) {
                return true;
            }
        }

        if(holder.status == loadStatus.Error)
        	requestLoading();
        return false;

	}

	/**
	 * Sends a message to this thread itself to start loading images. If the
	 * current view contains multiple image views, all of those image views will
	 * get a chance to request their respective photos before any of those
	 * requests are executed. This allows us to load images in bulk.
	 */
	private void requestLoading() {
		if (!mLoadingRequested) {
			mLoadingRequested = true;
			mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING);
		}
	}
	private void requestLoadingOnly(){
		mLoadingRequested = true;
		mMainThreadHandler.sendEmptyMessage(MESSAGE_REQUEST_LOADING_ONLY);
	}

	public void cancelRequest(ImageView view) {
		mPendingRequests.remove(view);
	}

	/**
	 * The thread that performs loading of photos from the database.
	 */
	private class LoaderThread extends HandlerThread implements Callback {

		private Handler mLoaderThreadHandler;

		public LoaderThread() {
			super(LOADER_THREAD_NAME);
		}

		/**
		 * Sends a message to this thread to load requested photos.
		 */
		public void requestLoading(int type) {
			if (mLoaderThreadHandler == null) {
				mLoaderThreadHandler = new Handler(getLooper(), this);
			}
			mLoaderThreadHandler.sendEmptyMessage(type);
		}

		/**
		 * Receives the above message, loads photos and then sends a message to
		 * the main thread to process them.
		 */
		public boolean handleMessage(Message msg) {
			if(msg.what == MESSAGE_REQUEST_LOADING_ONLY){

				if(downOnlyData != null){
					File file = new File(downOnlyData.path);
					if(!file.exists())
					  file.mkdirs();
					String fileName = downOnlyData.url.substring(downOnlyData.url.lastIndexOf("/") + 1);
					String imgPath = downOnlyData.path + "/" + fileName;
					File imageFile = new File(imgPath);
					System.out.println("loading:" + imageFile.exists());
					if(!imageFile.exists()){
						 downImage(downOnlyData);
					}
				}




			}else{
				Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
				while (iterator.hasNext()) {
					ImageView img = iterator.next();
					FileData fileData = mPendingRequests.get(img);
					if(fileData != null){
						BitmapHolder holder = mImageCache.get(fileData.url);
						if(holder != null){

							 File file = new File(fileData.path);
								if(!file.exists())
								  file.mkdirs();
								String fileName = fileData.url.substring(fileData.url.lastIndexOf("/") + 1);
								String imgPath = fileData.path + "/" + fileName;
								File imageFile = new File(imgPath);
								Bitmap result = null;
								if(imageFile.exists()){//本地有存在了
									result = readImage(fileData);
								}else{
									result = downImage(fileData);
								}
								holder.progress = 100;
								if(result == null){//无图
									//img.setBackgroundResource(R.drawable.ic_launcher);
									holder.setImage(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_error));
									holder.status = loadStatus.Error;
								}else{
									holder.status = loadStatus.Loaded;
									 holder.setImage(result);
								}
								  mImageCache.put(fileData.url, holder);
								//mImageCache.put(fileData.url, holder);
								  mMainThreadHandler.obtainMessage(MESSAGE_ICON_LOADED, fileData.url).sendToTarget();
								//mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);

						}
					}
					//mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
					 //if(fileData != null && holder != null){}
				}
			}




			return true;
		}


	}
private Context mContext;

	private ImageLoadHttpService service;
	private Bitmap readImage(FileData fileData) {
		try {
			String fileName = fileData.url.substring(fileData.url.lastIndexOf("/") + 1);
			String imagePath = fileData.path + "/" + fileName;
			File imgFile = new File(imagePath);
				Bitmap image = ImageUtil.readBitmapByPath(imagePath);

				if(image == null){//图片是坏的
					imgFile.delete();
					return null;
				}
			return image;
		} catch (Exception e) {
			return null;
		}
	}
	public Bitmap downImage(FileData fileData){
		if(service == null)
			service = new ImageLoadHttpService();
		HttpURLConnection hc = service.download(fileData.url);
		if(hc != null){
			InputStream update_is = null;
			BufferedInputStream update_bis = null;
			FileOutputStream update_os = null;
			BufferedOutputStream update_bos = null;
			byte[] buffer = null;
			try{
				if(hc.getResponseCode() != 200){
					return null;
				}
				int contentLen = hc.getContentLength();
				if(contentLen == 0){
					return null;
				}
				update_is = hc.getInputStream();
				if(TextUtils.isEmpty(fileData.path))
					return BitmapFactory.decodeStream(update_is);

				update_bis = new BufferedInputStream(update_is, 2048);

				String fileName = fileData.url.substring(fileData.url.lastIndexOf("/") + 1);
				String imagePath = fileData.path+ "/" + fileName;
				File imageFile = new File(imagePath);


				update_os = new FileOutputStream(imageFile,false);
				update_bos = new BufferedOutputStream(update_os, 1024);

				buffer = new byte[2048];
				int step = 0;
				int progress = 0;
				int sum = 0;
				System.out.println(">>>>>>startdown");
				while((step = update_bis.read(buffer)) != -1){
					sum += step;
					progress = ( 100 * sum)/contentLen;
					iconLoadListener.onIconLoadProgress(fileData.img, progress);
					update_bos.write(buffer,0,step);
					update_bos.flush();
					updateProgress(fileData.url, progress);
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					//mMainThreadHandler.obtainMessage(MESSAGE_ICON_PROGRESS, progress, 0, fileData.url).sendToTarget();//sendEmptyMessage(MESSAGE_ICON_LOADED);
				}
				updateProgress(fileData.url, 100);
				//mMainThreadHandler.obtainMessage(MESSAGE_ICON_PROGRESS, 100, 0, fileData.url).sendToTarget();
				//iconLoadListener.onIconLoadProgress(fileData.img, 100);
				//一张图片搞定
					Bitmap image = ImageUtil.readBitmapByPath(imagePath);
//
					if(image == null){//图是坏的
						imageFile.delete();
						System.out.println("down load error file*********" + imagePath);
						return null;
					}
					System.out.println("down load file********* save to local" + imagePath);

           			return image;

			}catch(IOException e){
				e.printStackTrace();
				return null;
			}finally{
				try{
					if(update_bis != null)
						update_bis.close();
					if(update_is != null)
						update_is.close();
					if(update_bos != null)
						update_bos.close();
					if(update_os != null)
						update_os.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}else{
			return null;
		}

	}

	private  ConnectType apnType = ConnectType.CMNET;
	private enum ConnectType {
		CMWAP, CMNET, WIFI, UNIWAP, Unknow
	}
	class ImageLoadHttpService {

		HttpURLConnection download(String url){
			try{
				HttpURLConnection hc = openMyConnection(url);
				return hc;
			}catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		private HttpURLConnection openMyConnection(String cUrl)throws IOException {
			HttpURLConnection hc = null;
			if (apnType == ConnectType.CMWAP || apnType == ConnectType.UNIWAP) {
				hc = requestHttpByWap(cUrl);
			} else {
				URL url = new URL(cUrl);
				hc = (HttpURLConnection) url.openConnection();
			}
			return hc;
		}
		private HttpURLConnection requestHttpByWap(String st) throws IOException {
			String urlt = st;
			String pUrl = "http://10.0.0.172";
			urlt = urlt.replace("http://", "");
			pUrl = pUrl + urlt.substring(urlt.indexOf("/"));
			urlt = urlt.substring(0, urlt.indexOf("/"));

			URL url = new URL(pUrl);
			HttpURLConnection hc = (HttpURLConnection) url.openConnection();

			hc.setRequestProperty("X-Online-Host", urlt);
			hc.setDoInput(true);

			return hc;
		}


	}

	private enum loadStatus{
		NeedLoaded, Loaded, Error
	}
	 private static class BitmapHolder {
		 loadStatus status;
		 SoftReference<Bitmap> bitmapRef;
		 int progress;
		 String path;
	        public boolean setImageView(ImageView v) {
	            if (bitmapRef.get() == null)
	                return false;
	            v.setImageBitmap(bitmapRef.get());
	            v.setBackgroundDrawable(null);
	           // v.setBackgroundDrawable(new BitmapDrawable(bitmapRef.get()));
	            return true;
	        }
	        public boolean isNull() {
	            return bitmapRef == null;
	        }
	        public void setImage(Object image) {
	            bitmapRef = image == null ? null : new SoftReference<Bitmap>((Bitmap) image);
	        }

	 }

}
