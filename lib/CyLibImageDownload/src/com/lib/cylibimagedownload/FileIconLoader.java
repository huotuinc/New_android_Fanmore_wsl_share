/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

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
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.lib.cylibimagedownload.ImageLoader.FileData;
import com.lib.cylibimagedownload.ImageLoader.ImageLoadHttpService;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Handler.Callback;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Asynchronously loads file icons and thumbnail, mostly single-threaded.
 */
public class FileIconLoader implements Callback {

    private static final String LOADER_THREAD_NAME = "FileIconLoader";

    /**
     * Type of message sent by the UI thread to itself to indicate that some
     * photos need to be loaded.
     */
    private static final int MESSAGE_REQUEST_LOADING = 1;

    /**
     * Type of message sent by the loader thread to indicate that some photos
     * have been loaded.
     */
    private static final int MESSAGE_ICON_LOADED = 2;


    private static class BitmapHolder{
    	 public static final int NEEDED = 0;

         public static final int LOADING = 1;

         public static final int LOADED = 2;

         int state;
        SoftReference<Bitmap> bitmapRef;

        public boolean setImageView(ImageView v) {
            if (bitmapRef.get() == null)
                return false;
            //v.setVisibility(View.VISIBLE);
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


    /**
     * A soft cache for image thumbnails. the key is file path
     */
    private final static ConcurrentHashMap<String, BitmapHolder> mImageCache = new ConcurrentHashMap<String, BitmapHolder>();

    /**
     * A map from ImageView to the corresponding photo ID. Please note that this
     * photo ID may change before the photo loading request is started.
     */
    private final ConcurrentHashMap<ImageView, FileId> mPendingRequests = new ConcurrentHashMap<ImageView, FileId>();

    /**
     * Handler for messages sent to the UI thread.
     */
    private final Handler mMainThreadHandler = new Handler(this);

    /**
     * Thread responsible for loading photos from the database. Created upon the
     * first request.
     */
    private LoaderThread mLoaderThread;

    /**
     * A gate to make sure we only send one instance of MESSAGE_PHOTOS_NEEDED at
     * a time.
     */
    private boolean mLoadingRequested;

    /**
     * Flag indicating if the image loading is paused.
     */
    private boolean mPaused;


    private IconLoadFinishListener iconLoadListener;

    /**
     * Constructor.
     *
     * @param context content context
     */
    public FileIconLoader(IconLoadFinishListener l) {
        iconLoadListener = l;
    }

    public static class FileId {
    	public int progress;
        public String mPath;

        public String mUrl;
        public ProgressBar bar;


        public FileId(String url, String path) {
            mPath = path;
            mUrl = url;
           
        }
    }

    public abstract static interface IconLoadFinishListener {
        void onIconLoadFinished(ImageView view);
    }

    /**
     * Load photo into the supplied image view. If the photo is already cached,
     * it is displayed immediately. Otherwise a request is sent to load the
     * photo from the database.
     *
     * @param id, database id
     */
    public boolean loadIcon(ImageView view,ProgressBar bar, String url, String path) {
        boolean loaded = loadCachedIcon(view, url, path);
        if (loaded) {
        	view.setVisibility(View.VISIBLE);
        	bar.setVisibility(View.GONE);
            mPendingRequests.remove(view);
        } else {
            FileId p = new FileId(url, path);
            p.bar = bar;
            mPendingRequests.put(view, p);
            if (!mPaused) {
                // Send a request to start loading photos
                requestLoading();
            }
        }
        return loaded;
    }

    public void cancelRequest(ImageView view) {
        mPendingRequests.remove(view);
    }

    /**
     * Checks if the photo is present in cache. If so, sets the photo on the
     * view, otherwise sets the state of the photo to
     * {@link BitmapHolder#NEEDED}
     */
    private boolean loadCachedIcon(ImageView view, String url, String path) {
        BitmapHolder holder = mImageCache.get(url);
        
        if (holder == null) {
            holder = new BitmapHolder();
            if (holder == null)
                return false;

            mImageCache.put(url, holder);
        } else if (holder.state == BitmapHolder.LOADED) {
            if (holder.isNull()) {
                return true;
            }

            // failing to set imageview means that the soft reference was
            // released by the GC, we need to reload the photo.
            if (holder.setImageView(view)) {
                return true;
            }
        }

        holder.state = BitmapHolder.NEEDED;
        return false;
    }


    /**
     * Stops loading images, kills the image loader thread and clears all
     * caches.
     */
    public void stop() {
        pause();

        if (mLoaderThread != null) {
            mLoaderThread.quit();
            mLoaderThread = null;
        }

        clear();
    }

    public void clear() {
        mPendingRequests.clear();
        mImageCache.clear();
    }

    /**
     * Temporarily stops loading
     */
    public void pause() {
        mPaused = true;
    }

    /**
     * Resumes loading
     */
    public void resume() {
        mPaused = false;
        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
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

    /**
     * Processes requests on the main thread.
     */
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_REQUEST_LOADING: 
                mLoadingRequested = false;
                if (!mPaused) {
                    if (mLoaderThread == null) {
                        mLoaderThread = new LoaderThread();
                        mLoaderThread.start();
                    }

                    mLoaderThread.requestLoading();
                }
                return true;

            case MESSAGE_ICON_LOADED: 
                if (!mPaused) {
                    processLoadedIcons();
                }
                return true;
        }
        return false;
    }

    /**
     * Goes over pending loading requests and displays loaded photos. If some of
     * the photos still haven't been loaded, sends another request for image
     * loading.
     */
    private void processLoadedIcons() {
        Iterator<ImageView> iterator = mPendingRequests.keySet().iterator();
        while (iterator.hasNext()) {
            ImageView view = iterator.next();
            FileId fileId = mPendingRequests.get(view);
            System.out.println(">>>>>>>>>>>>bar:" + fileId.bar.getTag() + "," + fileId.progress);
            fileId.bar.setVisibility(View.VISIBLE);
            fileId.bar.setProgress(fileId.progress);
            if(fileId.progress == 100)
            	fileId.bar.setVisibility(View.GONE);
            boolean loaded = loadCachedIcon(view, fileId.mUrl, fileId.mPath);
            if (loaded) {
                iterator.remove();
                iconLoadListener.onIconLoadFinished(view);
            }
        }

        if (!mPendingRequests.isEmpty()) {
            requestLoading();
        }
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
            Iterator<FileId> iterator = mPendingRequests.values().iterator();
            while (iterator.hasNext()) {
                FileId id = iterator.next();
                BitmapHolder holder = mImageCache.get(id.mUrl);
                if (holder != null && holder.state == BitmapHolder.NEEDED) {
                    // Assuming atomic behavior
                    holder.state = BitmapHolder.LOADING;
                    //do loading
                    File file = new File(id.mPath);
					if(!file.exists())
					  file.mkdirs();
					String fileName = id.mUrl.substring(id.mUrl.lastIndexOf("/") + 1);
					String imgPath = id.mPath + "/" + fileName;
					File imageFile = new File(imgPath);
					Bitmap result = null;
					if(imageFile.exists()){//本地有存在了
						result = readImage(id);
					}else{
						//set bar visible
//						Message msg2 = mMainThreadHandler.obtainMessage();
//						msg2.what = 3;
//						msg2.obj = id.bar;
//						mMainThreadHandler.sendMessage(msg2);
						result = downImage(id);
					}
					  holder.setImage(result);

                    holder.state = BitmapHolder.LOADED;
                    mImageCache.put(id.mUrl, holder);
                    mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
                }
            }

            //mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
            return true;
        }



    }
    
    
    
    
    private ImageLoadHttpService service;
	private Bitmap readImage(FileId fileData) {
		try {
			String fileName = fileData.mUrl.substring(fileData.mUrl.lastIndexOf("/") + 1);
			String imagePath = fileData.mPath + "/" + fileName;
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
	public Bitmap downImage(FileId fileData){
		if(service == null)
			service = new ImageLoadHttpService();
		HttpURLConnection hc = service.download(fileData.mUrl);
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
				if(TextUtils.isEmpty(fileData.mPath))
					return BitmapFactory.decodeStream(update_is);
				
				update_bis = new BufferedInputStream(update_is, 2048);
				
				String fileName = fileData.mUrl.substring(fileData.mUrl.lastIndexOf("/") + 1);
				String imagePath = fileData.mPath+ "/" + fileName;
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
					//iconLoadListener.onIconLoadProgress(img, progress);
//					Message msg2 = mMainThreadHandler.obtainMessage();
//					msg2.what = 3;
//					msg2.arg1 = progress;
//					msg2.obj = fileData.bar;
//					mMainThreadHandler.sendMessage(msg2);
					fileData.progress = progress;
					mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
					update_bos.write(buffer,0,step);
					update_bos.flush();
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				Message msg2 = mMainThreadHandler.obtainMessage();
//				msg2.what = 3;
//				msg2.arg1 = 100;
//				msg2.obj = fileData.bar;
//				mMainThreadHandler.sendMessage(msg2);
				fileData.progress = 100;
				mMainThreadHandler.sendEmptyMessage(MESSAGE_ICON_LOADED);
				//iconLoadListener.onIconLoadProgress(img, 100);
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
	
	 
}
