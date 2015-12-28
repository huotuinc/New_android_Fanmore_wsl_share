package cy.com.morefan.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;


import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Locale;
import java.util.Random;

import cy.com.morefan.MainApplication;
import cy.com.morefan.PayGoodBean;

/**
 * 支付宝支付工具类
 */
public
class AliPayUtil {

    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE =
            "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMCul0XS9X/cVMkmrSeaZXnSvrs"
            + "/bK5EiZf3d3/lTwHx165wAX/UIz4AcZHbKkYKKzmZKrRsu3tLRKFuflooKSVmWxk2hmeMqRETPZ"
            + "/t8rKf8UONZIpOlOXEmJ/rYwxhnMeVhbJJxsko2so/jc+XAPLyv0tsfoI"
            + "/TsJuhaGQ569ZAgMBAAECgYAK4lHdOdtwS4vmiO7DC++rgAISJbUH6wsysGHpsZRS8cxTKDSNefg7ql6"
            +
            "/9Hdg2XYznLlS08mLX2cTD2DHyvj38KtxLEhLP7MtgjFFeTJ5Ta1UuBRERcmy0xSLh2zayiSwGTM8Bwu7UD6LUSTGwrgRR2Gg4EDpSG08J5OCThKF4QJBAPOO6WKI/sEuoRDtcIJqtv58mc4RSmit/WszkvPlZrjNFDU6TrOEnPU0zi3f8scxpPxVYROBceGj362m+02G2I0CQQDKhlq4pIM2FLNoDP4mzEUyoXIwqn6vIsAv8n49Tr9QnBjCrKt8RiibhjSEvcYqM/1eocW0j2vUkqR17rNuVVz9AkBq+Z02gzdpwEJMPg3Jqnd/pViksuF8wtbo6/kimOKaTrEOg/KnVJrf9HaOnatzpDF0B0ghGhzb329SRWJhddXNAkAkjrgVmGyu+HGiGKZP7pOXHhl0u3H+vzEd9pHfEzXpoSO/EFgsKKXv3Pvh8jexKo1T5bPAchsu1gGl4B63jeUpAkBbgUalUpZWZ4Aii+Mfts+S2E5RooZfVFqVBIsK47hjcoqLw4JJenyjFu+Skl2jOQ8+I5y1Ggeg6fpBMr2rbVkf";

    // 支付宝公钥
    public static final String RSA_PUBLIC = "";

    // 未付款交易的超时时间
    //设置未付款交易的超时时间,一旦超时，该笔交易就会自动 被关闭。
    //当用户输入支付密码、点击确认付款后（即创建支付宝交易后）开始计时。
    //取值范围：1m～ 15d，或者使用绝对时间（示例格式： 2014-06-13 16:00:00）。
    //m-分钟， h-小时，  d-天， 1c-当天（无论交易何时创建，都在0点关闭）。
    //该参数数值不接受小数点，如 1.5h，可转换为 90m。
    public static final String IT_B_PAY = "30m";

    // 服务器异步通知页面路径
    // 支付宝服务器主动通知商户网站里指定的页面http路径。
    private String notify_url   = "";
    //
    public static final int    SDK_PAY_FLAG = 7001;

    public static final int SDK_CHECK_FLAG = 7002;

    private Handler handler = null;
    private Activity context = null;

    private String out_trade_no = "";
    private int    productType  = 0;
    private long   productId    = 0;
    private
    MainApplication application;


    public AliPayUtil(Activity context, Handler handler, MainApplication application) {
        this.handler = handler;
        this.context = context;
        this.application = application;
    }

    public void pay( String subject , String body , String price ,String noticeUrl, final int productType, final long productId ){
        this.notify_url = noticeUrl;
        this.productId=productId;
        this.productType=productType;

        // 订单
        String orderInfo = getOrderInfo( subject , body, price );
        // 对订单做RSA签名
        String sign = sign(orderInfo);

        try {
            // 仅需对sign做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            L.e ( e.getMessage () );
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask( context );
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                PayGoodBean payResult=new PayGoodBean();
                payResult.setOrderNo( out_trade_no);
                payResult.setProductId(productId);
                payResult.setProductType(productType);
                payResult.setTag(result);
                msg.obj = payResult;
                handler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    public String sign(String content) {
        return new SignUtils().sign(content, RSA_PRIVATE);
    }


    /**
     * create the order info. 创建订单信息
     *
     */
    public String getOrderInfo( String subject, String body, String price ) {
        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + application.readAlipayParentId () + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + application.readAlipayAppKey () + "\"";

        // 商户网站唯一订单号
        this.out_trade_no=getOutTradeNo();
        orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notify_url + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\""+ IT_B_PAY + "\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        //orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    public String getOutTradeNo() {
//        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
//                Locale.getDefault());
//        Date date = new Date();
//        String key = format.format(date);
//
//        Random r = new Random();
//        key = key + r.nextInt();
//        key = key.substring(0, 15);
//
//        return key;

        Random random = new Random();
        int rnd = random.nextInt(100000);
        String num = String.format(Locale.getDefault(), "%5d", rnd).replace(
                " ", "0");
        String orderno = NDateUtils.formatDate(System.currentTimeMillis(),
                                              "yyyyMMddHHmmss");
        orderno += "android" + num;
        return orderno;
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    public String getSignType() {
        return "sign_type=\"RSA\"";
    }

    public class SignUtils {

        private static final String ALGORITHM = "RSA";

        private static final String SIGN_ALGORITHMS = "SHA1WithRSA";

        private static final String DEFAULT_CHARSET = "UTF-8";

        public String sign(String content, String privateKey) {
            try {
                PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                        AliPayBase64.decode(privateKey));
                KeyFactory keyf = KeyFactory.getInstance(ALGORITHM);
                PrivateKey priKey = keyf.generatePrivate(priPKCS8);

                java.security.Signature signature = java.security.Signature
                        .getInstance(SIGN_ALGORITHMS);

                signature.initSign(priKey);
                signature.update(content.getBytes(DEFAULT_CHARSET));

                byte[] signed = signature.sign();

                return AliPayBase64.encode(signed);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

    }

}
