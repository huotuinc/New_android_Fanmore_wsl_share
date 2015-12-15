package cy.com.morefan.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class SPUtil {
	public static void saveLongToSpByName(Context context,String spName, String name,Long content){
		SharedPreferences sp = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
		Editor editor = sp.edit();
        editor.putLong(name, content);
        editor.commit();
	}
	public static Long getLongToSpByName(Context context,String spName, String name){
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		return sp.getLong(name, 0);
	}
	public static void saveStringToSpByName(Context context,String spName, String name,String content){
		SharedPreferences sp = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
		Editor editor = sp.edit();
        editor.putString(name, content);
        editor.commit();
	}
	public static String getStringToSpByName(Context context,String spName, String name){
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		return sp.getString(name, "");
	}


	public static void saveBooleanToSpByName(Context context,String spName, String name,boolean content){
		SharedPreferences sp = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
		Editor editor = sp.edit();
        editor.putBoolean(name, content);
        editor.commit();
	}
	public static void saveIntToSpByName(Context context,String spName, String name,int content){
		SharedPreferences sp = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
		Editor editor = sp.edit();
        editor.putInt(name, content);
        editor.commit();
	}
	public static boolean getBooleanFromSpByName(Context context,String spName, String name, boolean defaultVaule){
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		return sp.getBoolean(name, defaultVaule);
	}
	public static int getIntFromSpByName(Context context,String spName, String name){
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		return sp.getInt(name, 0);
	}

	public static int[] getDateFromSpByName(Context context,String spName, String name){
		int[] in = new int[2];
		SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		String time = sp.getString(name, null);
		if(TextUtils.isEmpty(time))
			return null;
		in[0] = Integer.valueOf(time.split(",")[0]);
		in[1] = Integer.valueOf(time.split(",")[1]);
		return in;
	}
	public static void clearSpByName(Context context, String spName){
		SharedPreferences sp = context.getSharedPreferences(spName,Context.MODE_PRIVATE);
		Editor editor = sp.edit();
        editor.clear();
        editor.commit();
	}
}
