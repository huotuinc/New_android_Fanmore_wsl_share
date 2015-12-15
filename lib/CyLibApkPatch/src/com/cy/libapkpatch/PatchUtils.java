package com.cy.libapkpatch;

import android.content.Context;

public class PatchUtils {

	static{
		System.loadLibrary("CyLibApkPatch");
	}
	/**
	 * 差分升级
	 */
	public static void diffUpdate(final Context mContext,final String patchFullPath,final String md5OrSha1){
		new Thread(){
			public void run() {
				
				
			};
		}.start();
	}
	public static native void patch(String oldfile, String newFile,String patchFile );
		
 }
