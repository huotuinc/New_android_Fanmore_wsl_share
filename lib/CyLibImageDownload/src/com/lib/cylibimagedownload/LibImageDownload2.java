package com.lib.cylibimagedownload;

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
import java.util.concurrent.ConcurrentHashMap;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.webkit.WebIconDatabase.IconListener;
import android.widget.ImageView;

public class LibImageDownload2 {
	private ArrayList<DownData> DOWN_LOAD_ARRAY = new ArrayList<DownData>();
	//图片缓存
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
	private ConcurrentHashMap<String, BitmapHolder> mImageCache = new ConcurrentHashMap<String, BitmapHolder>();
	//下载对象缓存，下载完成后移除
	private ConcurrentHashMap<ImageView, String> mCache = new ConcurrentHashMap<ImageView, String>();
	public class DownData{
		public DownData(ImageView img, String url, String path){
			this.img = img;
			this.url = url;
			this.path = path;
		}
		ImageView img;
		String url;
		String path;
	}
	
	public interface OnLoaderListener{
		void onIconLoadFinished(ImageView img, Bitmap bitmap);
		void onIconLoadProgress(ImageView img, int progress);
	}
	private OnLoaderListener listener;
	public LibImageDownload2(OnLoaderListener listener) {
		this.listener = listener;
	}
	
	private LoaderThread mLoaderThread;
	public void loadImage(ImageView img, String url,String path){
		boolean isload = loadCachedIcon(img, url, path);
		//是否已在下载队列
		String tempUrl = mCache.get(img);
		if(url.equals(tempUrl)){
			
		}else{//新任务
			DownData downData = new DownData(img, url, path);
			DOWN_LOAD_ARRAY.add(downData);
			
			
				
				if (mLoaderThread == null) {
					mLoaderThread = new LoaderThread();
					mLoaderThread.start();
				}

				mLoaderThread.requestLoading();
		}
		
	}
	
	private boolean loadCachedIcon(ImageView view, String url, String path) {

        BitmapHolder holder = mImageCache.get(url);
        
        if (holder == null) {
            holder = new BitmapHolder();

            mImageCache.put(url, holder);
        } else {
        	if(view == null && holder.bitmapRef != null)
        		return true;
        	
        	
            if (holder.isNull()) {
                return false;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if(view != null)
            if (holder.setImageView(view)) {
                return true;
            }
        }

        return false;
    
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
	
	
	private static final String LOADER_THREAD_NAME = "ImageLoaderThread";
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
		public void requestLoading() {
			if (mLoaderThreadHandler == null) {
				mLoaderThreadHandler = new Handler(getLooper(), this);
			}
			mLoaderThreadHandler.sendEmptyMessage(0);
		}

		/**
		 * Receives the above message, loads photos and then sends a message to
		 * the main thread to process them.
		 */
		public boolean handleMessage(Message msg) {
			while (true) {
				if(DOWN_LOAD_ARRAY.size() == 0)
					break;
				final DownData downData = DOWN_LOAD_ARRAY.get(0);
				//先从内存获取
				if(mBitmapCache.get(downData.url) != null){
					Bitmap tempBitmap = mBitmapCache.get(downData.url).get();
					if(tempBitmap != null){
						listener.onIconLoadFinished(downData.img, tempBitmap);
						DOWN_LOAD_ARRAY.remove(downData);
						mCache.remove(downData.img);
						continue;
					}
				}
					
					 File file = new File(downData.path);
						if(!file.exists())
						  file.mkdirs();
						String fileName = downData.url.substring(downData.url.lastIndexOf("/") + 1);
						String imgPath = downData.path + "/" + fileName;
						File imageFile = new File(imgPath);
						final Bitmap result ;
						if(imageFile.exists()){//本地有存在了
							result = readImage(downData);
						}else{
							result = downImage(downData);
						}	
						BitmapHolder holder = mImageCache.get(downData.url);
						 holder.setImage(result);
						 mImageCache.put(downData.url, holder);
						 mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								loadCachedIcon(downData.img, downData.url, downData.path);
								
							}
						});
						
								
						listener.onIconLoadFinished(downData.img, result);
						DOWN_LOAD_ARRAY.remove(downData);
						mCache.remove(downData.img);
					
			}
			return true;
		}


	}
	private Handler mHandler = new Handler();
	
	private ImageLoadHttpService service;
	private Bitmap readImage(DownData fileData) {
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
	public Bitmap downImage(DownData fileData){
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
				while((step = update_bis.read(buffer)) != -1){
					sum += step;
					progress = ( 100 * sum)/contentLen;
					listener.onIconLoadProgress(fileData.img, progress);
					update_bos.write(buffer,0,step);
					update_bos.flush();
				}
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
	
	
	private class BitmapHolder {
        SoftReference<Bitmap> bitmapRef;

        public boolean setImageView(ImageView v) {
            if (bitmapRef.get() == null)
                return false;
            v.setImageBitmap(bitmapRef.get());
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

