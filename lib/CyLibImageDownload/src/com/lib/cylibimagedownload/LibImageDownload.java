package com.lib.cylibimagedownload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.webkit.WebIconDatabase.IconListener;

public class LibImageDownload extends Handler{
	private static HandlerThread thread;
	private static LibImageDownload handler;
	private ArrayList<ImageData> DOWN_LOAD_ARRAY = new ArrayList<ImageData>();
	private DownLoadImageListener downLoadImageListener;
	private ImageLoadHttpService service;

	public LibImageDownload(Looper looper) {
		super(looper);
		service = new ImageLoadHttpService();
	}
	public void setImageListener(DownLoadImageListener downLoadImageListener){
		this.downLoadImageListener = downLoadImageListener;
	}
	public static void sendImageDownLoadRequest(ImageData data, DownLoadImageListener downLoadImageListener){
		ArrayList<ImageData> datas = new ArrayList<ImageData>();
		datas.add(data);
		sendImageDownLoadRequest(datas, downLoadImageListener);
		
	}
	public static void sendImageDownLoadRequest(List<ImageData> datas, DownLoadImageListener downLoadImageListener){
		if(thread == null){
			thread = new HandlerThread("image_download_service");
			thread.start();
		}
		if(handler == null){
			handler = new LibImageDownload(thread.getLooper());
		}
		for(int i = 0; i < datas.size(); i ++){
			if(datas.get(i).getUrl() != null){
				handler.DOWN_LOAD_ARRAY.add(datas.get(i));
			}
		}
		
		
		if(handler.DOWN_LOAD_ARRAY.size() > 0){//有数据，才去发起下载message
			handler.setImageListener(downLoadImageListener);
			handler.removeMessages(datas.get(0).getType());
			handler.sendEmptyMessage(datas.get(0).getType());
		}
	}
	
	
	
	@Override
	public void handleMessage(Message msg) {
			getImage(msg.what);
		super.handleMessage(msg);
	}
	private void getImage(int type) {
		int repeatTime = 3;
		while(true){
			if(DOWN_LOAD_ARRAY.size() == 0){
				break;
			}
			boolean result = false;
			ImageData data = DOWN_LOAD_ARRAY.get(0);
			File file = new File(data.getPath());
			if(!file.exists())
			  file.mkdirs();
			
			String url = data.getUrl();
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			String imgPath = data.getPath()+ "/" + fileName;
			File imageFile = new File(imgPath);
			if(imageFile.exists()){//本地有存在了
				result = readImage(data);
			}else{
				result = downImage(data);
			}
			if(result || repeatTime == 0){
				downLoadImageListener.onSingleImageFinish(data);
				repeatTime =3;
				DOWN_LOAD_ARRAY.remove(data);
			}else{//重复下3次
				repeatTime --;
			}
		}
		downLoadImageListener.onImageAllFinish();
		
	}
	private boolean downImage(ImageData data) {
		HttpURLConnection hc = service.download(data.getUrl());
		if(hc != null){
			InputStream update_is = null;
			BufferedInputStream update_bis = null;
			FileOutputStream update_os = null;
			BufferedOutputStream update_bos = null;
			byte[] buffer = null;
			try{
				if(hc.getResponseCode() != 200){ 
					return false;
				}
				int contentLen = hc.getContentLength();
				if(contentLen == 0){
					return false;
				}
				update_is = hc.getInputStream();
				
				update_bis = new BufferedInputStream(update_is, 2048);
				
				String url = data.getUrl();
				String fileName = url.substring(url.lastIndexOf("/") + 1);
				String imagePath = data.getPath()+ "/" + fileName;
				File imageFile = new File(imagePath);
				
				update_os = new FileOutputStream(imageFile,false);
				update_bos = new BufferedOutputStream(update_os, 1024);
				
				buffer = new byte[2048];
				int step = 0;
				while((step = update_bis.read(buffer)) != -1){
					update_bos.write(buffer,0,step);
					update_bos.flush();
				}
				//一张图片搞定
				if(downLoadImageListener != null){
					Bitmap image = ImageUtil.readBitmapByPath(imagePath);
//					
					if(image == null){//图是坏的
						imageFile.delete();
						System.out.println("down load error file*********" + imagePath);
						return false;
					}
					data.setImage(image);
           			downLoadImageListener.onSingleImageFinish(data);
           			image = null;
					
				}
			}catch(IOException e){
				e.printStackTrace();
				return false;
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
			return false;
		}
		return true;
	}
	

	private boolean readImage(ImageData data) {
		try {
			String url = data.getUrl();
			String fileName = url.substring(url.lastIndexOf("/") + 1);
			String imagePath = data.getPath()+ "/" + fileName;
			File imgFile = new File(imagePath);
			if(downLoadImageListener != null && imgFile.exists()){
				Bitmap image = ImageUtil.readBitmapByPath(imagePath);
				
			
				if(image == null){//图片是坏的
					imgFile.delete();
					return false;
				}
				data.setImage(image);
				downLoadImageListener.onSingleImageFinish(data);
				image = null;
				}
			
			
		} catch (Exception e) {
			return false;
		}
		return true;
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

