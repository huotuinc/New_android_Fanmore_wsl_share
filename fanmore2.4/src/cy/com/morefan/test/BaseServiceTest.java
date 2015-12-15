package cy.com.morefan.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.util.RSAUtil;
import android.test.AndroidTestCase;

public class BaseServiceTest extends AndroidTestCase{
	public void testRequestHttpByWap() throws ParseException, UnsupportedEncodingException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

		Date date = sdf.parse("2014-07-03 16:40:00");
		long time = date.getTime();
		String imei = "869420010092534";
		String sign = getSign(time, imei.substring(imei.length() - 6));
		System.out.println(">>>>sign:" + sign);
		System.out.println(">>>>encode:" + URLEncoder.encode(sign , "utf-8"));
	}
	private String getSign(long time, String imei){
		JSONObject sign = new JSONObject();
		try {
			sign.put("serial", time);
			sign.put("imei6", imei);
			sign.put("key", "caonimei");
			Key publicKey = RSAUtil.getPublicKeyByModuleAndExponent(RSAUtil.module, RSAUtil.exponentString);
			String result = RSAUtil.encrypt(sign.toString(), publicKey);
			return result;
			//return URLEncoder.encode(result , "utf-8");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

}

