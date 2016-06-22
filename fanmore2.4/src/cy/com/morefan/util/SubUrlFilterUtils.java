package cy.com.morefan.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;

import cy.com.morefan.MainApplication;
import cy.com.morefan.MoblieLoginActivity;
import cy.com.morefan.PayModel;
import cy.com.morefan.WebActivity;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;

/**
 * 过滤UI类
 */
public
class SubUrlFilterUtils {

    private
    Context context;
    private
    Activity aty;
    TextView titleView;
    private Handler mHandler;
    private
    MainApplication application;
    public
    WindowProgress payProgress;
    public WindowManager wManager;

    public SubUrlFilterUtils(
            Activity aty, Context context, TextView titleView, Handler mHandler,
            MainApplication application
    ) {
        this.context = context;
        this.titleView = titleView;
        this.mHandler = mHandler;
        this.application = application;
        this.aty = aty;
        wManager = aty.getWindowManager ( );
        payProgress = new WindowProgress(aty);
    }

    /**
     * webview拦截url作相应的处理
     * @param view
     * @param url
     * @return
     */
    public
    boolean shouldOverrideUrlBySFriend ( WebView view, String url ) {
        if ( url.contains ( Constant.WEB_TAG_NEWFRAME ) ) {

            String urlStr = url.substring ( 0, url.indexOf ( Constant.WEB_TAG_NEWFRAME ) );
            Bundle bundle = new Bundle( );
            bundle.putString ( Constant.INTENT_URL, urlStr );
            ActivityUtils.getInstance ( ).showActivity ( aty, WebActivity.class, bundle );
            return true;
        }
        else if ( url.contains ( Constant.WEB_TAG_USERINFO ) ) {
            //修改用户信息
            //判断修改信息的类型
            String type = url.substring(url.indexOf("=", 1)+1, url.indexOf("&", 1));
            if(Constant.MODIFY_PSW.equals ( type ))
            {
                //弹出修改密码框
            }
            return true;
        }else if(url.contains(Constant.WEB_TAG_LOGOUT)){
            //处理登出操作
            //鉴权失效
            //清除登录信息
            application.logout ();
            //跳转到登录界面
            ActivityUtils.getInstance ().skipActivity ( aty, MoblieLoginActivity.class );
        }else if(url.contains(Constant.WEB_TAG_INFO)){
            //处理信息保护
            return true;
        }else if(url.contains(Constant.WEB_TAG_FINISH)){
            if(view.canGoBack())
                view.goBack();

        } else if(url.contains ( Constant.WEB_PAY ) )
        {
            //支付进度
            payProgress.showProgress();

            //支付模块
            //获取信息
            //截取问号后面的
            //订单号
            String tradeNo = null;
            String customerID = null;
            String paymentType = null;
            PayModel payModel = new PayModel ();
            url = url.substring ( url.indexOf ( ".aspx?" )+6, url.length () );
            String[] str = url.split ( "&" );
            for(String map : str)
            {
                String[] values = map.split ( "=" );
                if(2 == values.length)
                {
                    if("trade_no".equals ( values[0] ))
                    {
                        tradeNo = values[1];
                        payModel.setTradeNo ( tradeNo );
                    }
                    else if("customerID".equals ( values[0] ))
                    {
                        customerID = values[1];
                        payModel.setCustomId ( customerID );
                    }
                    else if("paymentType".equals ( values[0] ))
                    {
                        paymentType = values[1];
                        payModel.setPaymentType ( paymentType );
                    }
                }
                else
                {
                    L.i ( "支付参数出错." );
                }
            }
            //获取用户等级
            StringBuilder builder = new StringBuilder(  );
            builder.append ( Constant.IP_URL + "/Api.ashx?req=OrderInfo"+ CONSTANT_URL() );

           try {
               JSONObject jsonUrl = new JSONObject();
               jsonUrl.put("orderNo", tradeNo);
               builder.append(URLEncoder.encode(jsonUrl.toString(), "UTF-8"));
           }catch (UnsupportedEncodingException e) {
               e.printStackTrace();
           }catch (Exception e) {
              // listener.onDataFailed(BusinessDataListener.ERROR_TO_RECHANGE, ERROR_DATA, null);
               e.printStackTrace();
           }

            AuthParamUtils param = new AuthParamUtils ( application, System.currentTimeMillis(), builder.toString (), context );
            String orderUrl = param.obtainUrlOrder ( );
            HttpUtil.getInstance ( ).doVolleyPay ( aty, context, mHandler, application, orderUrl, payModel, payProgress, titleView, wManager );
            return true;

        }
        else if(url.contains ( Constant.AUTH_FAILURE ))
        {
            //鉴权失效
            //清除登录信息
            application.logout ();
            //跳转到登录界面
            ActivityUtils.getInstance ().skipActivity ( aty, MoblieLoginActivity.class );
        }
        else
        {
            //跳转界面
            view.loadUrl ( url );
            return false;
        }
        return false;
    }
    protected String CONSTANT_URL() {
        //operation=HuoTu2013AD/HuoTu2013IP&version=appVersion&imei=*****
        //BusinessStatic.IMEI = BusinessStatic.IMEI + 2;
        String url = "&operation=" + Constant.OPERATION + "&qd=" + Constant.QD +"&version="
                + Constant.APP_VERSION + "&citycode=" + BusinessStatic.getInstance().CITY_CODE
                + "&imei=" + BusinessStatic.getInstance().IMEI + "&lat=" + BusinessStatic.getInstance().USER_LAT + "&lng=" + BusinessStatic.getInstance().USER_LNG
                + "&sign="+ getSign() + "&p=";
        return url;
    }
    private String getSign(){
        JSONObject sign = new JSONObject();
        try {
            sign.put("serial", System.currentTimeMillis());
            sign.put("imei6", BusinessStatic.getInstance().IMEI.substring(BusinessStatic.getInstance().IMEI.length() - 6));
            sign.put("key", "caonimei");
            Key publicKey = RSAUtil.getPublicKeyByModuleAndExponent(RSAUtil.module, RSAUtil.exponentString);
            String result = RSAUtil.encrypt(sign.toString(), publicKey);
            return URLEncoder.encode(result, "utf-8");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
