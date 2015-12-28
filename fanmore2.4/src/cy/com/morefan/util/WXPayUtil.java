package cy.com.morefan.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cy.com.morefan.MainApplication;
import cy.com.morefan.constant.Constant;

/**
 * Created by Administrator on 2015/9/19.
 */
public
class WXPayUtil {

    private static final String TAG = WXPayUtil.class.getName ( );

    private IWXAPI api;
    public
    MainApplication application;

    private static String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token";

    private static String genPrePayUrl = "https://api.weixin.qq.com/pay/genprepay";

    public static String NotifyUrl = "http://121.14.73.81:8080/agent/wxpay/payNotifyUrl.jsp";

    /**
     * 微信公众平台商户模块和商户约定的密钥
     *
     * 注意：不能hardcode在客户端，建议genPackage这个过程由服务器端完成
     */
    public static String PARTNER_KEY = "18076bf2a8bf9479f2cddeec13fd2ec0";

    /**
     * 微信开放平台和商户约定的支付密钥
     *
     * 注意：不能hardcode在客户端，建议genSign这个过程由服务器端完成
     */
    public static final String APP_KEY =
            "NzfP6pfeljyHeY08LO9p8YAKZCGLz8akO4lCGdXZOGnVsJqfo8jeuYB7C0GoFJGEKZMDVGKWYnbbJj3pCpvJzd4iY7bVglaNz54XAD26tiCr5DZGLjZFoRxbqe8i3HT5"; //"L8LrMqqeGRxST5reouB0K66CaYAWpqhAVsq7ggKkxHCOastWksvuX1uvmvQclxaHoYd3ElNBrNO2DHnnzgfVG9Qs473M3DTOZug5er46FhuGofumV8H2FVR9qkjSlC5K"; // wxd930ea5d5a258f4f
    // 对应的支付密钥

    private Handler handler = null;

    private Context context = null;

    private String nonceStr;

    private String body = "";

    private String fee = "";

    private long timeStamp;

    private static String AccessToken =
            "e_X1nINQ9X936ezlNs9VSI0jbfKxyCrnz67ZC1xK04fwLHEWR_kNNtMB6FqU-1"
            + "-hkbzgHgHIqlUVMYRGX9gysRsHLCaItHtaGn8eBX1WeFk";

    private static Long AccessTokenDate = SystemTools
            .getLongTime ( "2015-01-01 00:00" );

    public static final int SDK_WX_ACCESSTOKEN_FAIL = 8001;


    public static final int SDK_WX_PAY_SUCCESS = 8002;

    public static final int SDK_WX_PAY_FAIL = 8003;

    public WXPayUtil(Context context, Handler handler, MainApplication application) {
        this.context = context;
        this.handler = handler;
        this.application = application;
    }

    public
    void pay ( String body, String fee ) {
        this.body = body;
        this.fee = fee;

        api = WXAPIFactory.createWXAPI(context, Constant.WXPAY_ID);

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                GetAccessTokenResult accessTokenResult = getAccessToken();
                if (accessTokenResult.localRetCode != LocalRetCode.ERR_OK)
                {
                    String msgString = String.format("获取AccessToekn失败，错误代码%d,原因%s",
                            accessTokenResult.errCode, accessTokenResult.errMsg);
                    Message msg = handler.obtainMessage(SDK_WX_ACCESSTOKEN_FAIL, msgString);
                    handler.sendMessage(msg);
                    return;
                }
                GetPrepayIdResult prePayResult = sendPrePayReq(accessTokenResult.accessToken);
                if( prePayResult.localRetCode != LocalRetCode.ERR_OK){
                    String msgString = String.format("生成预订单失败，错误代码%d,原因%s", prePayResult.errCode, prePayResult.errMsg);
                    Message msg =handler.obtainMessage(SDK_WX_ACCESSTOKEN_FAIL, msgString);
                    handler.sendMessage(msg);
                    return;
                }

                sendPayReq(prePayResult);
            }
        };


        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }



    /**
     * 调用 支付 订单接口
     *@创建人：jinxiangdong
     *@修改时间：2015年6月16日 上午10:59:05
     *@方法描述：
     *@方法名：sendPayReq
     *@参数：@param result
     *@返回：void
     *@exception
     *@since
     */
    private void sendPayReq(GetPrepayIdResult result)
    {
        PayReq req = new PayReq();
        req.appId = Constant.WXPAY_ID;
        req.partnerId = application.readWxpayParentId ();
        req.prepayId = result.prepayId;
        req.nonceStr = nonceStr;
        req.timeStamp = String.valueOf(timeStamp);
        req.packageValue = "Sign=Wxpay";// "Sign=" + packageValue;

        List<NameValuePair > signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("appkey", APP_KEY));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genSign(signParams);
        L.i ( "调起支付的package串：" + req.packageValue );
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        boolean isPay = api.sendReq(req);
    }

    /**
     * 生成预支付订单
     *@创建人：jinxiangdong
     *@修改时间：2015年6月16日 上午10:25:13
     *@方法描述：
     *@方法名：sendPrePayReq
     *@参数：@param accessToken
     *@参数：@return
     *@返回：GetPrepayIdResult
     *@exception
     *@since
     */
    private GetPrepayIdResult sendPrePayReq(String accessToken)
    {
        String url = String.format("%s?access_token=%s", genPrePayUrl,
                accessToken);
        String entity = genProductArgs();

        L.i ( " url = " + url );
        L.i(" entity = " + entity);

        GetPrepayIdResult result = new GetPrepayIdResult();

        byte[] buf = httpPost(url, entity);
        if (buf == null || buf.length == 0)
        {
            result.localRetCode = LocalRetCode.ERR_HTTP;
            result.errCode=0;
            result.errMsg="请求失败";
            return result;
        }

        String content = new String(buf);
        L.i(" prePay content = " + content);
        result.parseFrom(content);
        return result;
    }

    /**
     * 获得 AccessToken
     * 时间段间隔90分钟
     *@创建人：jinxiangdong
     *@修改时间：2015年6月16日 上午10:18:58
     *@方法描述：
     *@方法名：getAccessToken
     *@参数：@return
     *@返回：GetAccessTokenResult
     *@exception
     *@since
     */
    private GetAccessTokenResult getAccessToken()
    {
        long currentTime = System.currentTimeMillis();
        long timespan = currentTime - AccessTokenDate;
        long second = timespan / 1000;
        GetAccessTokenResult result = new GetAccessTokenResult();

        if (AccessToken == null || AccessToken.length()<1 || second > 5400)
        {
            String url = String.format(
                    "%s?grant_type=client_credential&appid=%s&secret=%s",
                    tokenUrl, Constant.WXPAY_ID, Constant.WXPAY_SECRT);
            L.i("get access token, url = " + url);

            byte[] buf = httpGet(url);
            if (buf == null || buf.length == 0) {
                result.localRetCode = LocalRetCode.ERR_HTTP;
                result.errCode=0;
                result.errMsg="请求失败";
                return result;
            }
            String content = new String(buf);
            result.parseFrom(content);
        }else
        {
            result.accessToken= AccessToken;
            result.localRetCode= LocalRetCode.ERR_OK;
        }
        return result;
    }

    /**
     *
     *@创建人：jinxiangdong
     *@修改时间：2015年6月16日 上午10:30:59
     *@方法描述：
     *@方法名：genNonceStr
     *@参数：@return
     *@返回：String
     *@exception
     *@since
     */
    private String genNonceStr()
    {
        Random random = new Random();
        return new MD5().getMessageDigest(String.valueOf(random.nextInt(10000))
                                                .getBytes());
    }

    /**
     * 建议 traceid 字段包含用户信息及订单信息，方便后续对订单状态的查询和跟踪
     */
    private String getTraceId()
    {
        String userName = application.getUserName ();
        return userName + "_" + genTimeStamp();
    }
    /**
     * 返回当前时间的秒数
     *@创建人：jinxiangdong
     *@修改时间：2015年6月16日 上午10:29:50
     *@方法描述：
     *@方法名：genTimeStamp
     *@参数：@return
     *@返回：long
     *@exception
     *@since
     */
    private long genTimeStamp()
    {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 注意：商户系统内部的订单号,32个字符内、可包含字母,确保在商户系统唯一
     */
    private String genOutTradNo()
    {
        Random random = new Random();
        return new MD5().getMessageDigest(String.valueOf(random.nextInt(10000))
                                                .getBytes());
    }

    /**
     * 生成详细的订单数据  JSON格式 Post
     *
     *@创建人：jinxiangdong
     *@修改时间：2015年6月16日 上午10:27:14
     *@方法描述：
     *@方法名：genProductArgs
     *@参数：@return
     *@返回：String
     *@exception
     *@since
     */
    private String genProductArgs()
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("appid", Constant.WXPAY_ID);
            // traceId
            // 由开发者自定义，可用于订单的查询与跟踪，建议根据支付用户信息生成此id
            String traceId = getTraceId();
            json.put("traceid", traceId);
            //32位内的随机串，防重发
            nonceStr = genNonceStr();
            json.put("noncestr", nonceStr);

            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("bank_type", "WX"));
            packageParams.add(new BasicNameValuePair("body", body));
            packageParams.add(new BasicNameValuePair("fee_type", "1"));
            packageParams.add(new BasicNameValuePair("input_charset", "UTF-8"));
            packageParams.add(new BasicNameValuePair("notify_url", NotifyUrl));
            packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
            packageParams.add(new BasicNameValuePair("partner", application.readWxpayParentId ()));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", getIP() ));
            packageParams.add(new BasicNameValuePair("total_fee", fee));
            String packageValue = genPackage(packageParams);

            json.put("package", packageValue);
            timeStamp = genTimeStamp();
            json.put("timestamp", timeStamp);

            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", Constant.WXPAY_ID));
            signParams.add(new BasicNameValuePair("appkey", APP_KEY));
            signParams.add(new BasicNameValuePair("noncestr", nonceStr));
            signParams.add(new BasicNameValuePair("package", packageValue));
            signParams.add(new BasicNameValuePair("timestamp", String.valueOf(timeStamp)));
            signParams.add(new BasicNameValuePair("traceid", traceId));
            json.put("app_signature", genSign(signParams));

            json.put("sign_method", "sha1");
        } catch (Exception e)
        {
            L.i ( "genProductArgs fail, ex = " + e.getMessage ( ) );
            return null;
        }

        return json.toString();
    }

    private String getIP(){
        return "192.168.1.1";
    }

    private String genSign(List<NameValuePair> params)
    {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (; i < params.size() - 1; i++)
        {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append(params.get(i).getName());
        sb.append('=');
        sb.append(params.get(i).getValue());

        String sha1 = new MD5().sha1(sb.toString());
        L.i ( "sha1签名串：" + sb.toString ( ) );
        L.i ( "genSign, sha1 = " + sha1 );
        return sha1;
    }

    /**
     * 签名 订单数据包
     *@创建人：jinxiangdong
     *@修改时间：2015年6月16日 上午10:53:52
     *@方法描述：
     *@方法名：genPackage
     *@参数：@param params
     *@参数：@return
     *@返回：String
     *@exception
     *@since
     */
    private String genPackage(List<NameValuePair> params)
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++)
        {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PARTNER_KEY); // 注意：不能hardcode在客户端，建议genPackage这个过程都由服务器端完成

        // 进行md5摘要前，params内容为原始内容，未经过url encode处理
        String packageSign = new MD5().getMessageDigest(sb.toString().getBytes()).toUpperCase();
        L.i ( "package签名串：" + sb.toString ( ) );
        return URLEncodedUtils.format(params, "utf-8") + "&sign=" + packageSign;
    }

    public static byte[] httpPost(String url, String entity)
    {
        if (url == null || url.length() == 0)
        {
            L.i ( "httpPost, url is null" );
            return null;
        }

        HttpClient httpClient = getNewHttpClient();

        HttpPost httpPost = new HttpPost(url);

        try
        {
            httpPost.setEntity(new StringEntity(entity));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse resp = httpClient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            {
                L.i( "httpGet fail, status code = "
                           + resp.getStatusLine().getStatusCode());
                return null;
            }

            return EntityUtils.toByteArray(resp.getEntity());
        } catch (Exception e)
        {
            L.i( "httpPost exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] httpGet(final String url) {
        if (url == null || url.length() == 0) {
            L.i( "httpGet, url is null");
            return null;
        }

        HttpClient httpClient = getNewHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse resp = httpClient.execute(httpGet);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                L.i( "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                return null;
            }

            return EntityUtils.toByteArray(resp.getEntity());

        } catch (Exception e) {
            L.i( "httpGet exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static HttpClient getNewHttpClient()
    {
        try
        {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion (params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                                      .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e)
        {
            return new DefaultHttpClient();
        }
    }

    private static enum LocalRetCode
    {
        ERR_OK, ERR_HTTP, ERR_JSON, ERR_OTHER
    }

    private static class GetAccessTokenResult
    {

        private static final String TAG = "GetAccessTokenResult";

        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;

        public String accessToken;

        public int expiresIn;

        public int errCode;

        public String errMsg;

        public void parseFrom(String content)
        {

            if (content == null || content.length() <= 0)
            {
                L.i( "parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }

            try
            {
                JSONObject json = new JSONObject(content);
                if (json.has("access_token"))
                { // success case
                    accessToken = json.getString("access_token");
                    expiresIn = json.getInt("expires_in");
                    localRetCode = LocalRetCode.ERR_OK;
                } else
                {
                    errCode = json.getInt("errcode");
                    errMsg = json.getString("errmsg");
                    localRetCode = LocalRetCode.ERR_JSON;
                }

            } catch (Exception e)
            {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    private static class GetPrepayIdResult
    {

        private static final String TAG = "MicroMsg.SDKSample.PayActivity.GetPrepayIdResult";

        public LocalRetCode localRetCode = LocalRetCode.ERR_OTHER;

        public String prepayId;

        public int errCode;

        public String errMsg;

        public void parseFrom(String content)
        {

            if (content == null || content.length() <= 0)
            {
                L.i( "parseFrom fail, content is null");
                localRetCode = LocalRetCode.ERR_JSON;
                return;
            }

            try
            {
                JSONObject json = new JSONObject(content);
                if (json.has("prepayid"))
                { // success case
                    prepayId = json.getString("prepayid");
                    localRetCode = LocalRetCode.ERR_OK;
                } else
                {
                    localRetCode = LocalRetCode.ERR_JSON;
                }

                errCode = json.getInt("errcode");
                errMsg = json.getString("errmsg");

            } catch (Exception e)
            {
                localRetCode = LocalRetCode.ERR_JSON;
            }
        }
    }

    public class MD5
    {

        public MD5()
        {
        }

        public String getMessageDigest(byte[] buffer)
        {
            char hexDigits[] =
                    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
                      'd', 'e', 'f' };
            try
            {
                MessageDigest mdTemp = MessageDigest.getInstance("MD5");
                mdTemp.update(buffer);
                byte[] md = mdTemp.digest();
                int j = md.length;
                char str[] = new char[j * 2];
                int k = 0;
                for (int i = 0; i < j; i++)
                {
                    byte byte0 = md[i];
                    str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                    str[k++] = hexDigits[byte0 & 0xf];
                }
                return new String(str);
            } catch (Exception e)
            {
                return null;
            }
        }

        public String sha1(String str)
        {
            if (str == null || str.length() == 0)
            {
                return null;
            }

            char hexDigits[] =
                    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
                      'd', 'e', 'f' };

            try
            {
                MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
                mdTemp.update(str.getBytes());

                byte[] md = mdTemp.digest();
                int j = md.length;
                char buf[] = new char[j * 2];
                int k = 0;
                for (int i = 0; i < j; i++)
                {
                    byte byte0 = md[i];
                    buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                    buf[k++] = hexDigits[byte0 & 0xf];
                }
                return new String(buf);
            } catch (Exception e)
            {
                return null;
            }
        }
    }

    private static class SSLSocketFactoryEx extends SSLSocketFactory
    {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public
        SSLSocketFactoryEx ( KeyStore truststore )
        throws NoSuchAlgorithmException, KeyManagementException,
                KeyStoreException, UnrecoverableKeyException {
            super ( truststore );

            TrustManager tm = new X509TrustManager( ) {

                public
                X509Certificate[] getAcceptedIssuers ( ) {
                    return null;
                }

                @Override
                public
                void checkClientTrusted (
                        X509Certificate[] chain,
                        String authType
                                        )
                throws java.security.cert.CertificateException {
                }

                @Override
                public
                void checkServerTrusted (
                        X509Certificate[] chain,
                        String authType
                                        )
                throws java.security.cert.CertificateException {
                }
            };

            sslContext.init (
                    null, new TrustManager[]
                            { tm }, null
                            );
        }

        @Override
        public Socket createSocket (
                Socket socket, String host, int port,
                boolean autoClose
                            ) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory ( ).createSocket (
                    socket, host,
                    port, autoClose
                                                                );
        }

        @Override
        public Socket createSocket ( ) throws IOException {
            return sslContext.getSocketFactory ( ).createSocket ( );
        }
    }
}
