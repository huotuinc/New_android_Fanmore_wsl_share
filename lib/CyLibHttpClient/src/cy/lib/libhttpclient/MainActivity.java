package cy.lib.libhttpclient;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.example.cylibhttpclient.R;

import android.os.Bundle;
import android.app.Activity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//querySocre();
		//querySocreRecord();
		//queryAccount();
		//queryWaterBillByCode("林荷芳", "741340000");
		//queryWaterBillByIdentity("林荷芳", "1234");
//		queryWaterBillByCompany("台州市腾隆机动车部件有限公司");
String content2 = "<xml><status>1</status><msg><jf>3720</jf><mer>华联仙居城东店</mer><tim>2013-12-21 19:14:21</tim><vip_card>1111013060009105</vip_card><jf>1800</jf><mer>仙居永安汽车装潢养护中心</mer><tim>2013-11-16 10:16:18</tim><vip_card>1111013060009105</vip_card><jf>1800</jf><mer>仙居永安汽车装潢养护中心</mer><tim>2014-01-05 13:35:22</tim><vip_card>1111013060009105</vip_card></msg></xml>";
////		System.out.println(StaxonUtil.xml2json(content2));
////		XStream xStream = new XStream(new JettisonMappedXmlDriver());
////
////		xStream.alias("student", Student.class);
////	    fail(xStream.toXML(bean));
//
		JSONObject jsonObj = null;
		try {
		    jsonObj =XML.toJSONObject(content2) ;
		    System.out.println(">>>aaa:" + jsonObj.getJSONObject("xml").getJSONObject("msg").getJSONArray("jf").toString());
		} catch (JSONException e) {
		    //Log.e("JSON exception", e.getMessage());
		    e.printStackTrace();
		}


		System.out.println( jsonObj.toString());



	}










//	private void queryAccount() {
//
//		new Thread(){
//			public void run() {
//				String url ="http://211.140.182.233:7001/jf.jsp";
//				/**
//				 url?type=queryaccount&merid=APP&mob=13761036955&serial=26740cea397648c6ac2c7900057470e8&sign=f6baca4686d37befee7816dbde54aa1e
//type 是类型  queryaccount 代表查询账户
//merid 对应商户ID
//mob 是手机号
//serial 是流水号永不重复
//sign 是签名
//sign=MD5(type=query&merid=12121&mob=13761036955&serial=26740cea397648c6ac2c7900057470e8&key=key);
//
//
//
//				 */
//				long time = System.currentTimeMillis();
//				String merid = "APP";
//				String mob = "13967617575";
//				String sign = String.format("type=queryaccount&merid=%s&mob=%s&serial=%s&key=%s", merid, mob, "" + time, "idkdueHHDleuhk*&^%3073h3" );
//			    //准备数据
//			    NameValuePair param1 =new BasicNameValuePair("type", "queryaccount");
//			    NameValuePair param2 =new BasicNameValuePair("merid", merid);
//			    NameValuePair param3 =new BasicNameValuePair("mob", mob);
//			    NameValuePair param4 =new BasicNameValuePair("serial", "" + time);
//			    NameValuePair param7 =new BasicNameValuePair("sign", SecurityUtil.MD5Encryption(sign));
//			    try {
//			    	System.out.println(">>");
//			        // 使用工具类直接发出POST请求,服务器返回json数据，比如"{userid:12}"
//			        String response = CyHttpClient.post(url, param1, param2, param3, param4, param7);
////			        JSONObject root =new JSONObject(response);
//			        System.out.println(response);
//
//			        SAXParserFactory spf = SAXParserFactory.newInstance();
//				      SAXParser saxParser = spf.newSAXParser();
//				      XMLReader reader = saxParser.getXMLReader();
//				      SaxHanlder sax = new SaxHanlder();
//				      reader.setContentHandler(sax);
//				      InputSource in = new InputSource(new ByteArrayInputStream(response.getBytes()));
//				     reader.parse(in);
//			    } catch (RuntimeException e) {
//			    	System.out.println(e.toString());
//			        // 请求失败或者连接失败
//			        //Log.w(TAG, e.getMessage());
//			       // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
//			    } catch (Exception e) {
//			    	System.out.println(e.toString());
//			        // JSon解析出错
//			       // Log.w(TAG, e.getMessage());
//			    }
//			};
//		}.start();
//
//
//	}








}
