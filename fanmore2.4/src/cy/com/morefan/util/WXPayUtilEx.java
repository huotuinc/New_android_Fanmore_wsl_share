package cy.com.morefan.util;

import android.content.Context;
import android.os.Handler;
import android.util.Xml;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cy.com.morefan.MainApplication;
import cy.com.morefan.WXPayResult;
import cy.com.morefan.constant.Constant;

/**
 * 微信支付工具类
 */
public
class WXPayUtilEx {

    private String wxpayNotifyUrl;
    // API密钥，在商户平台设置
    public static final int    SDK_WXPAY_PAY = 9001;

    // IWXAPI api;
    IWXAPI msgApi = null;

    Context context;

    Handler handler;

    String body = "";

    String fee;

    PayReq req;

    String prepay_id;

    StringBuffer sb;

    String out_trade_no;

    int productType=0;
    long productId=0;
    private
    MainApplication application;

    private String attach;

    public WXPayUtilEx(Context context, Handler handler, String notifyurl, MainApplication application)
    {
        this.context = context;
        this.handler = handler;
        this.wxpayNotifyUrl = notifyurl;
        this.sb=new StringBuffer();
        this.req = new PayReq();
        this.application = application;
        msgApi = WXAPIFactory.createWXAPI(context, null);
        boolean isRegister = msgApi.registerApp(application.readWxpayAppId ( ));
    }

    /**
     * 必须异步调用
     *
     */
    public WXPayResult pay(String body, String fee , int productType, long productid, String attach, String orderId)
    {
        this.body = body;
        this.fee = fee;
        this.productId=productid;
        this.productType=productType;
        this.attach = attach;
        this.out_trade_no = orderId;

        Map<String, String> getPrePayXml = getPrePayId();

        if (getPrePayXml == null)
        {
            WXPayResult result = new WXPayResult();
            result.setCode(0);
            result.setMessage("请求预支付订单接口是失败");
            return result;
        }
        if (getPrePayXml.containsKey("return_code")
            && getPrePayXml.containsKey("return_msg"))
        {
            String resultCode = getPrePayXml.get("return_code");
            if (resultCode.equals("FAIL"))
            {
                WXPayResult result = new WXPayResult();
                result.setCode(0);
                result.setMessage("return_code="
                                  + getPrePayXml.get("return_code") + ",return_msg="
                                  + getPrePayXml.get("return_msg"));
                return result;
            }
        }

        sb.append("prepay_id\n" + getPrePayXml.get("prepay_id") + "\n\n");
        // show.setText(sb.toString());

        prepay_id = getPrePayXml.get("prepay_id");

        genPayReq();

        String extData = "{orderNo:"+out_trade_no+",productType:"+this.productType+",productId:"+this.productId+"}";
        req.extData = extData;

        boolean isPay = msgApi.sendReq(req);

        WXPayResult result = new WXPayResult();
        result.setCode(1);
        result.setMessage("返回1，不能代表支付成功。");
        return result;
    }

    /**
     * 生成 预订单id
     *
     * @创建人：jinxiangdong
     * @修改时间：2015年6月17日 上午11:19:49
     * @方法描述：
     * @方法名：getPrePayId
     * @参数：@return
     * @返回：Map<String,String>
     * @exception
     * @since
     */
    private Map<String, String> getPrePayId()
    {
        String url = String
                .format(Constant.WX_URL);
        String entity = genProductArgs();

        L.i( entity );

        byte[] buf = WXPayUtil.httpPost(url, entity);

        if (buf == null)
            return null;

        String content = new String(buf);
        L.i( content );
        Map<String, String> xml = decodeXml(content);

        return xml;
    }

    // 32位内的随机串，防重发
    private String genNonceStr()
    {
        Random random = new Random();
        return getMessageDigest(String.valueOf(random.nextInt(10000))
                                      .getBytes());
    }

    // 商户订单号 商户系统内部的订单号, 32个字符内, 可包含字母, 确保在商户系统唯一
    private String genOutTradNo()
    {
        Random random = new Random();
        int rnd = random.nextInt(100000);
        String num = String.format(Locale.getDefault(), "%5d", rnd).replace(
                " ", "0");

        String orderno = NDateUtils.formatDate(System.currentTimeMillis(),
                                              "yyyyMMddHHmmss");
        orderno += "android" + num;
        return orderno;

        // return getMessageDigest(String.valueOf(random.nextInt(10000))
        // .getBytes());
    }

    //
    private String getIP()
    {
        return "192.168.1.1";
    }

    /**
     * 生成签名
     */
    private String genPackageSign(List<NameValuePair> params)
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
        sb.append(application.readWxpayAppKey ( ));

        String packageSign;
        packageSign = getMessageDigest(sb.toString().getBytes()).toUpperCase(
                Locale.getDefault());
        L.i( packageSign);

        return packageSign;
    }

    private String toXml(List<NameValuePair> params)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++)
        {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        L.i( sb.toString() );
        return sb.toString();
    }

    public Map<String, String> decodeXml(String content)
    {

        try
        {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {

                String nodeName = parser.getName();
                switch (event)
                {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false)
                        {
                            // 实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e)
        {
            L.e( e.toString());
        }
        return null;

    }

    private String genProductArgs()
    {
        StringBuffer xml = new StringBuffer();

        try
        {
            String nonceStr = genNonceStr();

            //out_trade_no=genOutTradNo();

            xml.append ( "</xml>" );
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", application.readWxpayAppId ( )));
            packageParams.add(new BasicNameValuePair("attach", attach));
            packageParams.add(new BasicNameValuePair("body", body));
            packageParams.add(new BasicNameValuePair("mch_id",
                                                     application.readWxpayParentId ()));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url",
                                                     this.wxpayNotifyUrl));
            packageParams.add(new BasicNameValuePair("out_trade_no", out_trade_no ));
            packageParams.add(new BasicNameValuePair("spbill_create_ip",
                                                     getIP()));
            packageParams.add(new BasicNameValuePair("total_fee", fee));
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));

            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));

            String xmlstring = toXml(packageParams);
            xmlstring = new String(xmlstring.getBytes(), "ISO8859-1");
            return xmlstring;

        } catch (Exception e)
        {
            L.e( "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

    }

    private long genTimeStamp()
    {
        return System.currentTimeMillis() / 1000;
    }

    private String genAppSign(List<NameValuePair> params)
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
        sb.append(application.readWxpayAppKey ( ));

        this.sb.append("sign str\n" + sb.toString() + "\n\n");
        String appSign = getMessageDigest(sb.toString().getBytes())
                .toUpperCase(Locale.getDefault());
        L.e( appSign );
        return appSign;
    }

    private void genPayReq()
    {

        req.appId = application.readWxpayAppId ();// Constant.WX_APPID;
        req.partnerId = application.readWxpayParentId ();// WXPayUtil.PARTERID;
        req.prepayId = prepay_id;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());

        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);

        sb.append("sign\n" + req.sign + "\n\n");

        // show.setText(sb.toString());

        L.e( signParams.toString() );

    }

    public String getMessageDigest(byte[] buffer)
    {
        char hexDigits[] =
                { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                  'e', 'f' };
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

}