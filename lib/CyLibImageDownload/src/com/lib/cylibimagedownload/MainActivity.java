package com.lib.cylibimagedownload;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity implements DownLoadImageListener {
	public final static String PATH_MAIN = Environment.getExternalStorageDirectory().toString() + File.separator + ".LIBIMAGEDOWN";
	public final static String PATH_LOADING_IMG = PATH_MAIN + File.separator + "img" ;

	ImageView img;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		img = (ImageView) findViewById(R.id.img);
		String url = "http://i.weather.com.cn/i/product/pic/m/sevp_nsmc_wxcl_asc_e99_achn_lno_py_20131017040000000.jpg";
		ImageData imageData = new ImageData(url, 0, PATH_LOADING_IMG);
		LibImageDownload.sendImageDownLoadRequest(imageData, this);
	}

	@Override
	public void onSingleImageFinish(ImageData data) {
		System.out.println(">>>>>onSingleImageFinish");
		BitmapDrawable bd = new BitmapDrawable(data.getImage());
		img.setBackgroundDrawable(bd);
		
	}

	@Override
	public void onImageAllFinish() {
		// TODO Auto-generated method stub
		
	}


}
