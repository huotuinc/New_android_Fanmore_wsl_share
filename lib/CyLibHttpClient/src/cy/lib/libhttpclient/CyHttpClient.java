package cy.lib.libhttpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
public class CyHttpClient {
	    private static String CHARSET = HTTP.UTF_8;
	    private static HttpClient cyHttpClient;

	    private CyHttpClient() {
	    }
	    public static JSONObject xml2Json(String xml) throws JSONException{
			    return  XML.toJSONObject(xml);
	    }

	    public static synchronized HttpClient getHttpClient() {
	        if (null== cyHttpClient) {
	            HttpParams params =new BasicHttpParams();
	            // 设置一些基本参数
	            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	            HttpProtocolParams.setContentCharset(params,
	                    CHARSET);
	            HttpProtocolParams.setUseExpectContinue(params, true);
	            HttpProtocolParams
	                    .setUserAgent(
	                            params,
	                            "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
	                                    +"AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
	            // 超时设置
	/* 从连接池中取连接的超时时间 */
	            ConnManagerParams.setTimeout(params, 10000);
	            /* 连接超时 */
	            HttpConnectionParams.setConnectionTimeout(params, 10000);
	            /* 请求超时 */
	            HttpConnectionParams.setSoTimeout(params, 10000);

	            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
	            SchemeRegistry schReg =new SchemeRegistry();
	            schReg.register(new Scheme("http", PlainSocketFactory
	                    .getSocketFactory(), 80));
	            schReg.register(new Scheme("https", SSLSocketFactory
	                    .getSocketFactory(), 443));

	            // 使用线程安全的连接管理来创建HttpClient
	            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(params, schReg);
	            cyHttpClient =new DefaultHttpClient(conMgr, params);
	        }
	        return cyHttpClient;
	    }



	    public static MyJSONObject get(String url) throws Exception{
	    		   HttpGet request = new HttpGet(url);
	                // 发送请求
	                HttpClient client = getHttpClient();
	                HttpResponse response = client.execute(request);
	                if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
	                    throw new RuntimeException("请求失败");
	                }
	                HttpEntity resEntity =  response.getEntity();
	                MyJSONObject result = readData(resEntity);
	    			//检查该请求是否有经验返回
	    			return result;
	               // return (resEntity ==null) ?null : EntityUtils.toString(resEntity, CHARSET);

	    }
	    private static MyJSONObject readData(HttpEntity entity) throws Exception {
			InputStreamReader isReader = null;
			BufferedReader bufferReader = null;

			isReader = new InputStreamReader(entity.getContent());
			bufferReader = new BufferedReader(isReader, 2048);
			StringBuffer sb = new StringBuffer();

			String result = bufferReader.readLine();
			while (result != null) {
				sb.append(result);
				result = bufferReader.readLine();
			}
			try {
				return new MyJSONObject(sb.toString());
			} catch (Exception e) {
				return new MyJSONObject(xml2Json(sb.toString()).toString());
			}
			//return new MyJSONObject(sb.toString());
		}


	    /**
	     * 上传流
	     * @param pathUrl
	     * @param content
	     * @return
	     */
	    public static MyJSONObject post(String pathUrl, String content) throws Exception{
	         //String pathUrl = "http://172.20.0.206:8082/TestServelt/login.do";
	         //建立连接
	         URL url=new URL(pathUrl);
	         HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();

	         ////设置连接属性
	         httpConn.setDoOutput(true);//使用 URL 连接进行输出
	         httpConn.setDoInput(true);//使用 URL 连接进行输入
	         httpConn.setUseCaches(false);//忽略缓存
	         httpConn.setRequestMethod("POST");//设置URL请求方法
	         httpConn.setConnectTimeout(10000);
	         String requestString = content;//"客服端要以以流方式发送到服务端的数据...";

	         //设置请求属性
	        //获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
	              byte[] requestStringBytes = requestString.getBytes(CHARSET);
	              httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
	              httpConn.setRequestProperty("Content-Type", "application/octet-stream");
	              httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
	              httpConn.setRequestProperty("Charset", "UTF-8");
	              //
//	              String name=URLEncoder.encode("黄武艺","utf-8");
//	              httpConn.setRequestProperty("NAME", name);

	              //建立输出流，并写入数据
	              OutputStream outputStream = httpConn.getOutputStream();
	              outputStream.write(requestStringBytes);
	              outputStream.close();
	             //获得响应状态
	              int responseCode = httpConn.getResponseCode();
	              if(responseCode != HttpStatus.SC_OK) {
	                    throw new RuntimeException("请求失败");
	                }//连接成功

	               //当正确响应时处理数据
	               StringBuffer sb = new StringBuffer();
	                  String readLine;
	                  BufferedReader responseReader;
	                 //处理响应流，必须与服务器响应流输出的编码一致
	                  responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), CHARSET));
	                  while ((readLine = responseReader.readLine()) != null) {
	                   sb.append(readLine).append("\n");
	                  }
	                  responseReader.close();
	                  try {
	      				return new MyJSONObject(sb.toString());
	      			} catch (Exception e) {
	      				return new MyJSONObject(xml2Json(sb.toString()).toString());
	      			}
	       }

	    /**
	     *
	     * @param url
	     * @param params
	     * @return
	     * @throws JSONException
	     */
	    public static MyJSONObject post(String url, List<NameValuePair> params) throws Exception{
                UrlEncodedFormEntity entity =new UrlEncodedFormEntity(params, "utf-8");
                // 创建POST请求
                HttpPost request =new HttpPost(url);
                request.setEntity(entity);
                // 发送请求
                HttpClient client = getHttpClient();
                HttpResponse response = client.execute(request);
                if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    throw new RuntimeException("请求失败");
                }
                HttpEntity resEntity =  response.getEntity();
                return readData(resEntity);//(resEntity ==null) ?null : EntityUtils.toString(resEntity, CHARSET);


	    }

	public static MyJSONObject post(String url, NameValuePair... params) throws Exception {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
		for (NameValuePair p : params)
			formparams.add(p);
		return post(url, formparams);
	}


}
