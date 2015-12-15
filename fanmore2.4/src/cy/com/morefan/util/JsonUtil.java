package cy.com.morefan.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

	public static <T> T convertToObj(JSONObject jsonObject,Class<T> cla){
		if(jsonObject==null)
			return null;

		Field[] fb  =cla.getDeclaredFields();
		T t;
		try {
		t = cla.newInstance();
		for(int j=0;j<fb.length;j++){
		    String fieldName = fb[j].getName();
		    String fieldNameU=fieldName.substring(0, 1).toUpperCase(Locale.CHINA)+fieldName.substring(1);
		    L.i(">>>>" + fieldName + "," + fieldNameU + "," + fb[j].getType());
//		    Method method=cla.getMethod("set"+fieldNameU, fb[j].getType());
//		    method.invoke(t, jsonObject.get(fieldName));
		}
		        return t;

		} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return null;
		}




}
