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

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lib.cylibimagedownload.ImageLoader.IconLoadFinishListener;


public class ImageDownloadHelper implements IconLoadFinishListener {
    private ImageLoader mIconLoader;
	

    public ImageDownloadHelper() {
        mIconLoader = new ImageLoader(this);
    }



    public void setImage(ImageView imgView, String url, String filePath) {
    	if(TextUtils.isEmpty(url))
    		return;
    	 boolean set = false;
         set = mIconLoader.loadIcon(imgView, url, filePath);
         if (imgView != null && !set){
        	 //imgView.setBackgroundResource(R.drawable.ic_launcher);
         }
        	 
    }
    public class WidgetData{
    	public WidgetData(ImageView img, ProgressBar bar){
    		this.img = img;
    		this.bar = bar;
    	}
    	ImageView img;
    	ProgressBar bar;
    	boolean isLoaded;
    }
    
    /**
     * 添加通过progressbar增加加载效果
     * @param bar
     * @param imgView
     * @param url
     * @param filePath
     */
    public void setImage(ProgressBar bar, ImageView imgView, String url, String filePath) {}


	@Override
	public void onIconLoadFinished(ImageView img, Bitmap bitmap) {
		
	}


	@Override
	public void onIconLoadProgress(ImageView img, int progress) {
		
	}


}
