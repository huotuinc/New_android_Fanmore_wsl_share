package com.lib.cylibimagedownload;


public interface DownLoadImageListener {
	void onSingleImageFinish(ImageData data);
	void onImageAllFinish();
}
