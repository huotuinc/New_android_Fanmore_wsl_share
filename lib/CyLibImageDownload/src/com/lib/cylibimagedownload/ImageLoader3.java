package com.lib.cylibimagedownload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lib.cylibimagedownload.FileIconLoader.FileId;
import com.lib.cylibimagedownload.FileIconLoader.ImageLoadHttpService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ImageLoader3 implements Callback {

	MemoryCache memoryCache;
	FileCache fileCache;
//	private Map<ImageView, String> imageViews = Collections
//			.synchronizedMap(new WeakHashMap<ImageView, String>());
	
	private Map<ImageView, String> imageViews = new ConcurrentHashMap<ImageView, String>();
	// 线程池
	ExecutorService executorService;

	public ImageLoader3(Context context) {
		memoryCache = new MemoryCache();
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}

	// 当进入listview时默认的图片，可换成你自己的默认图片
	final int stub_id = R.drawable.picreviewre_fresh_bg;

	// 最主要的方法
	public void DisplayImage(String url, String path, ImageView imageView, ProgressBar bar) {
		imageViews.put(imageView, url);
		// 先从内存缓存中查找

		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			// 若没有的话则开启新线程加载图片
			queuePhoto(url, path, imageView, bar);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url,String path, ImageView imageView, ProgressBar bar) {
		PhotoToLoadData p = new PhotoToLoadData(url, path, imageView, bar);
		executorService.submit(new PhotosLoader(p));
	}
	

	
	private ImageLoadHttpService service;
	private Bitmap getBitmap(PhotoToLoadData data) {
		//先从本地获取
			String fileName = data.url.substring(data.url.lastIndexOf("/") + 1);
			String imagePath = data.path + "/" + fileName;
			File imgFile = new File(imagePath);
			Bitmap image = null;
			if(imgFile.exists()){
				 image = ImageUtil.readBitmapByPath(imagePath);
				if(image != null)
					return image;
				
			}
			
			
		//从网络获取
			if(service == null)
				service = new ImageLoadHttpService();
			HttpURLConnection hc = service.download(data.url);
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
					if(TextUtils.isEmpty(data.path))
						return BitmapFactory.decodeStream(update_is);
					
					update_bis = new BufferedInputStream(update_is, 2048);
					
//					String fileName = fileData.mUrl.substring(data.url.lastIndexOf("/") + 1);
//					String imagePath = fileData.mPath+ "/" + fileName;
					 File file = new File(data.path);
						if(!file.exists())
						  file.mkdirs();
				//	File imageFile = new File(imagePath);
//					imageFile.mkdirs();
					
					
					update_os = new FileOutputStream(imgFile,false);
					update_bos = new BufferedOutputStream(update_os, 1024);
					
					buffer = new byte[2048];
					int step = 0;
					int progress = 0;
					int sum = 0;
					while((step = update_bis.read(buffer)) != -1){
						sum += step;
						progress = ( 100 * sum)/contentLen;
//						if(!imageViewReused(data))
//							data.bar.setProgress(progress);
						//iconLoadListener.onIconLoadProgress(img, progress);
//						Message msg2 = mMainThreadHandler.obtainMessage();
//						msg2.what = 3;
//						msg2.arg1 = progress;
//						msg2.obj = fileData.bar;
//						mMainThreadHandler.sendMessage(msg2);
//						fileData.progress = progress;
//						mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
						update_bos.write(buffer,0,step);
						update_bos.flush();
//						try {
//							Thread.sleep(200);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
					}
//					Message msg2 = mMainThreadHandler.obtainMessage();
//					msg2.what = 3;
//					msg2.arg1 = 100;
//					msg2.obj = fileData.bar;
//					mMainThreadHandler.sendMessage(msg2);
//					fileData.progress = 100;
//					mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
					//iconLoadListener.onIconLoadProgress(img, 100);
					//一张图片搞定
					image = ImageUtil.readBitmapByPath(imagePath);
//						
						if(image == null){//图是坏的
							imgFile.delete();
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

//	private Bitmap getBitmap(PhotoToLoadData data) {
//		File f = fileCache.getFile(data.url);
//
//		// 先从文件缓存中查找是否有
//		//Bitmap b = decodeFile(f);
//		if (b != null)
//			return b;
//
//		// 最后从指定的url中下载图片
//		try {
//			Bitmap bitmap = null;
//			URL imageUrl = new URL(data.url);
//			HttpURLConnection conn = (HttpURLConnection) imageUrl
//					.openConnection();
//			conn.setConnectTimeout(30000);
//			conn.setReadTimeout(30000);
//			conn.setInstanceFollowRedirects(true);
//			InputStream is = conn.getInputStream();
//			OutputStream os = new FileOutputStream(f);
//			CopyStream(is, os);
//			os.close();
//			bitmap = decodeFile(f);
//			return bitmap;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			return null;
//		}
//	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
//	private Bitmap decodeFile(File f) {
//		try {
//			// decode image size
//			BitmapFactory.Options o = new BitmapFactory.Options();
//			o.inJustDecodeBounds = true;
//			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
//
//			// Find the correct scale value. It should be the power of 2.
//			final int REQUIRED_SIZE = 70;
//			int width_tmp = o.outWidth, height_tmp = o.outHeight;
//			int scale = 1;
//			while (true) {
//				if (width_tmp / 2 < REQUIRED_SIZE
//						|| height_tmp / 2 < REQUIRED_SIZE)
//					break;
//				width_tmp /= 2;
//				height_tmp /= 2;
//				scale *= 2;
//			}
//
//			// decode with inSampleSize
//			BitmapFactory.Options o2 = new BitmapFactory.Options();
//			o2.inSampleSize = scale;
//			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
//		} catch (FileNotFoundException e) {
//		}
//		return null;
//	}

	// Task for the queue
	private class PhotoToLoadData {
		public String url;
		public ImageView imageView;
		public ProgressBar bar;
		public String path;

		public PhotoToLoadData(String u, String path, ImageView i, ProgressBar b) {
			url = u;
			this.path = path;
			imageView = i;
			bar = b;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoadData photoToLoadData;

		PhotosLoader(PhotoToLoadData photoToLoad) {
			this.photoToLoadData = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoadData))
				return;
			Bitmap bmp = getBitmap(photoToLoadData);
			if(bmp == null)
				return;
			memoryCache.put(photoToLoadData.url, bmp);
			if (imageViewReused(photoToLoadData))
				return;
			System.out.println(">>>>>>>>>>BitmapDisplayer:"+photoToLoadData.url);
			uiHandler.obtainMessage(0, photoToLoadData).sendToTarget();
//			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoadData, 100);
//			// 更新的操作放在UI线程中
//			Activity a = (Activity) photoToLoadData.imageView.getContext();
//			a.runOnUiThread(bd);
		}
	}
	private Handler uiHandler = new Handler(this);
	@SuppressLint("NewApi")
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == 0){
			
			PhotoToLoadData photoToLoad = (PhotoToLoadData) msg.obj;
			if (imageViewReused(photoToLoad))
				return true;
			
			Bitmap bitmap = memoryCache.get(photoToLoad.url);
			System.out.println(">>>>>>>>>>handleMessage:" + bitmap);
			if (bitmap != null){
				System.out.println(">>>>>>>>>>setImageBitmap");
				photoToLoad.imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				photoToLoad.imageView.setImageBitmap(bitmap);
			}
				
			else{
				//photoToLoad.bar.setProgress(progress);
			}
				//photoToLoad.imageView.setImageResource(stub_id);
		return true;
		}
		return false;
	}
	//class UpdateProgress

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoadData photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoadData photoToLoad;
		int progress;
		

		public BitmapDisplayer(Bitmap b, PhotoToLoadData p, int progress) {
			bitmap = b;
			photoToLoad = p;
			this.progress = progress;
		}

		@SuppressLint("NewApi")
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			System.out.println(">>>>>>>>>>Runnable");
			if (bitmap != null){
				photoToLoad.imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
				photoToLoad.imageView.setImageBitmap(bitmap);
			}
				
			else{
				photoToLoad.bar.setProgress(progress);
			}
				//photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		//memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
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
}
