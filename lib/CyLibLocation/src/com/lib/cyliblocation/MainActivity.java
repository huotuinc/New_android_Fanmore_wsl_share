package com.lib.cyliblocation;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity implements BDLocationListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LibLocation.startLocation(this, this);
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		System.out.println("onReceiveLocation");
		if (location == null)
			return ;
		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation){
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
		} 
		LibLocation.stopLocation();
 
		System.out.println(sb.toString());
	
		
	}

	@Override
	public void onReceivePoi(BDLocation arg0) {
		// TODO Auto-generated method stub
		
	}


}
