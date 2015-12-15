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



import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.HashMap;

import com.lib.cylibimagedownload.FileIconLoader.IconLoadFinishListener;

public class FileIconHelper implements  IconLoadFinishListener {

    private static final String LOG_TAG = "FileIconHelper";

    private static HashMap<ImageView, ProgressBar> imageFrames = new HashMap<ImageView, ProgressBar>();

    private static HashMap<String, Integer> fileExtToIcons = new HashMap<String, Integer>();

    private FileIconLoader mIconLoader;


    public FileIconHelper() {
        mIconLoader = new FileIconLoader(this);
    }

    private static void addItem(String[] exts, int resId) {
        if (exts != null) {
            for (String ext : exts) {
                fileExtToIcons.put(ext.toLowerCase(), resId);
            }
        }
    }


    public void setIcon(int postion, String url, String path, ImageView fileImage, ProgressBar bar) {
//        String filePath = fileInfo.filePath;
//        long fileId = fileInfo.dbId;
//        String extFromFilename = Util.getExtFromFilename(filePath);
//        FileCategory fc = FileCategoryHelper.getCategoryFromPath(filePath);
//        fileImageFrame.setVisibility(View.GONE);
//        boolean set = false;
//        int id = getFileIcon(extFromFilename);
//        fileImage.setImageResource(id);
//
//        mIconLoader.cancelRequest(fileImage);
//        switch (fc) {
//            case Apk:
//                set = mIconLoader.loadIcon(fileImage, filePath, fileId, fc);
//                break;
//            case Picture:
//            case Video:
//                set = mIconLoader.loadIcon(fileImage, filePath, fileId, fc);
//                if (set)
//                    fileImageFrame.setVisibility(View.VISIBLE);
//                else {
//                    fileImage.setImageResource(fc == FileCategory.Picture ? R.drawable.file_icon_picture
//                            : R.drawable.file_icon_video);
//                    imageFrames.put(fileImage, fileImageFrame);
//                    set = true;
//                }
//                break;
//            default:
//                set = true;
//                break;
//        }
        //bar.setVisibility(View.GONE);
        //fileImage.setVisibility(View.GONE);
    	bar.setTag(postion);
        boolean set = mIconLoader.loadIcon(fileImage,bar, url, path);
        if (!set){
        	imageFrames.put(fileImage, bar);
            fileImage.setImageResource(R.drawable.picreviewre_fresh_bg);
        }
        	
    }

    @Override
    public void onIconLoadFinished(ImageView view) {
//        ProgressBar bar = imageFrames.get(view);
//        if (bar != null) {
////        	bar.setVisibility(View.GONE);
////        	view.setVisibility(View.VISIBLE);
//            imageFrames.remove(view);
//        }
    }

}
