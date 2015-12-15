package com.lib.cylibimagedownload;

import android.graphics.Bitmap;

public class ImageData {
	private int id;
	private Bitmap image;
	private String url;// 下载地址
	private String path;// 储存目录
	private int type;// 下载类型


	public ImageData( String url, int type, String path) {
		super();
		this.url = url;
		this.path = path;
		this.type = type;
	}
	public ImageData(int id, String url, int type, String path) {
		super();
		this.id = id;
		this.url = url;
		this.path = path;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
