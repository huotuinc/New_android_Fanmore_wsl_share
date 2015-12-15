package cy.com.morefan.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtil {
	public  static String getNetworkType(Context context){
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isAvailable() && info.isConnected())
			switch (info.getType()) {
			case ConnectivityManager.TYPE_WIFI:
				return "WIFI";
			case ConnectivityManager.TYPE_MOBILE:
				//电信CDMA,联通WCDMA,移动TD-SCDMA
				switch (info.getSubtype()) {
				case TelephonyManager.NETWORK_TYPE_CDMA:// 2G 电信 Code Division Multiple Access 码分多址
				case TelephonyManager.NETWORK_TYPE_1xRTT://2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
					return "电信2G";
				case TelephonyManager.NETWORK_TYPE_EVDO_0://  3G (EVDO 全程 CDMA2000 1xEV-DO)
				case TelephonyManager.NETWORK_TYPE_EVDO_A:// EVDO 版本A （电信3g）
				case TelephonyManager.NETWORK_TYPE_EVDO_B:// EVDO 版本B（电信3g）
				case TelephonyManager.NETWORK_TYPE_EHRPD:// 3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
					return "电信3G";
				case TelephonyManager.NETWORK_TYPE_EDGE:// EDGE（移动2g）
					return "移动2G";
				case TelephonyManager.NETWORK_TYPE_GPRS:// GPRS （联通2g）
					return "联通2G";
				case TelephonyManager.NETWORK_TYPE_HSDPA:// 3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
				case TelephonyManager.NETWORK_TYPE_UMTS:// UMTS（联通3g）
				case TelephonyManager.NETWORK_TYPE_HSPA:// HSPA
				case TelephonyManager.NETWORK_TYPE_HSPAP:// HSPA+
				case TelephonyManager.NETWORK_TYPE_HSUPA:// HSUPA
					return "联通3G";
				case TelephonyManager.NETWORK_TYPE_LTE:// LTE(3g到4g的一个过渡，称为准4g)
					return "4G";
				case TelephonyManager.NETWORK_TYPE_IDEN:// iDen
					return "2G_iDen";

				default:
					return "UNKNOWN";
				}

			default:
				return "未知";
			}
			return "无网络";
	}
}
