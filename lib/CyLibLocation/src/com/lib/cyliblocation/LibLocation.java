package com.lib.cyliblocation;

import android.content.Context;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


/**
 * 在项目manifest内添加
 * 1.服务申明：
 * <service
            android:name="com.baidu.location.f"
            android:enabled="true"
            android:process=":remote" />
 * 2.权限申明：
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <uses-permission android:name="android.permission.READ_LOGS" />          
 */
public class LibLocation {
	
	private static LocationClient mLocationClient = null;
	
	public static void startLocation(Context context, BDLocationListener listener){
		if(mLocationClient == null){
			//定位
			mLocationClient = new LocationClient(context); // 声明LocationClient类
			LocationClientOption option = new LocationClientOption();
		    option.setOpenGps(true);
		    option.setAddrType("all");//返回的定位结果包含地址信息
		    option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
		    option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		    option.disableCache(true);//禁止启用缓存定位
		    option.setPoiNumber(5);	//最多返回POI个数	
		    option.setPoiDistance(1000); //poi查询距离		
		    option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息		
		    mLocationClient.setLocOption(option);
		    mLocationClient.registerLocationListener(listener);    //注册监听函数
			
		}
		System.out.println("location start!");
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		else
			mLocationClient.requestLocation();
		
	}
	public static void stopLocation(){
		if(mLocationClient != null){
			mLocationClient.stop();
			mLocationClient = null;
		}
			
	}

}
