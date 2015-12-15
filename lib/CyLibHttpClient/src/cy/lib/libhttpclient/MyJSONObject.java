package cy.lib.libhttpclient;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class MyJSONObject extends JSONObject {
	public MyJSONObject(){
		super();
	}

	public MyJSONObject(String str) throws JSONException{
		super(str);
	}

	public MyJSONObject(JSONObject jso, String[] keys) throws JSONException{
		super(jso,keys);
	}
	@Override
	public String getString(String name){
		try{
			if(has(name)){
				return super.getString(name);
			}else{
				return "";
			}
		}catch(JSONException e){
			return "";
		}
	}

	@Override
	public int getInt(String name) {
		try{
			if(has(name)){
				return super.getInt(name);
			}else{
				return 0;
			}
		}catch(JSONException e){
			return 0;
		}
	}

	@Override
	public double getDouble(String name) {
		try{
			if(has(name)){
				return super.getDouble(name);
			}else{
				return 0;
			}
		}catch(JSONException e){
			return 0;
		}
	}

	@Override
	public long getLong(String name){
		try{
			if(has(name)){
				return super.getLong(name);
			}else{
				return 0;
			}
		}catch(JSONException e){
			return 0;
		}
	}

	@Override
	public MyJSONObject getJSONObject(String name) throws JSONException{
		if(has(name)){
			JSONObject jso = super.getJSONObject(name);
			return convertJSONObject2My(jso);
		}else{
			return null;
		}
	}

	@Override
	public JSONArray getJSONArray(String name) throws JSONException {
		if(has(name)){
			JSONArray jArray = super.getJSONArray(name);

			for(int i = 0; i < jArray.length(); i ++){
				JSONObject jo = jArray.getJSONObject(i);
				jArray.put(i, convertJSONObject2My(jo));
			}
			return jArray;
		}else{
			return null;
		}
	}

	@Override
	public Object get(String name) throws JSONException {
		if(has(name)){
			return super.get(name);
		}else{
			return null;
		}
	}

	@Override
	public boolean getBoolean(String name) throws JSONException {
		if(has(name)){
			return super.getBoolean(name);
		}else{
			return false;
		}
	}

	private MyJSONObject convertJSONObject2My(JSONObject jsonObject) throws JSONException{
		Iterator<?> it = jsonObject.keys();
		String[] keys = new String[jsonObject.length()];
		int i = 0;
		while(it.hasNext()){
			String aKey = (String)it.next();
			keys[i] = aKey;
			i ++;
		}
		MyJSONObject mjso = new MyJSONObject(jsonObject,keys);
		return mjso;
	}
}
