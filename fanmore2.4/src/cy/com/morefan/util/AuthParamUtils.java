package cy.com.morefan.util;

import android.content.Context;
import android.text.TextUtils;
import cy.com.morefan.MainApplication;
import cy.com.morefan.bean.AccountModel;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 授权参数构建类
 */
public
class AuthParamUtils {

    private
    MainApplication application;

    private String url;

    private long timestamp;
    private
    Context context;

    public
    AuthParamUtils(MainApplication application, long timestamp, String url, Context context)
    {
        this.application = application;
        this.timestamp = timestamp;
        this.url = url;
        this.context = context;
    }

    public String obtainUrl()
    {
        StringBuilder builder = new StringBuilder (  );
        try {
            Map< String, String > paramMap = new HashMap< String, String > ( );

                paramMap.put("redirecturl",   BusinessStatic.getInstance().URL_WEBSITE);
                paramMap.put ( "userid", application.readUserId() );
                paramMap.put ( "customerid", application.readMerchantId ( ) );
                paramMap.put  ("moblie", UserData.getUserData().phone);
                //添加额外固定参数
                //1、timestamp
                paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ), "UTF-8" ) );

                //生成sigin
                paramMap.put ( "sign", getMapSign ( paramMap ) );

                builder.append ( url+"/OAuth2/WSLAuthorize.aspx" );
                builder.append("?redirecturl="+paramMap.get("redirecturl"));
                builder.append ( "&customerid="+application.readMerchantId ( ) );
                builder.append ( "&userid="+application.readUserId ( ) );
                builder.append  ("&moblie="+paramMap.get ( "moblie" ));
                builder.append ( "&timestamp=" + paramMap.get ( "timestamp" ) );
                builder.append ( "&sign="+paramMap.get ( "sign" ) );
//                builder.append ( "&version=" + application.getAppVersion ( context ) );
//                builder.append ( "&operation=" + Constant.OPERATION_CODE );


            return builder.toString ();
        }
        catch ( UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch blockL
            L.e ( e.getMessage ( ) );
            return null;
        }

    }

    /**
     * 获取post参数
     * @return
     */
    public
    Map obtainParams(AccountModel account)
    {
            Map< String, Object > paramMap = new HashMap< String, Object > ( );
            paramMap.put ( "customerId", application.readMerchantId ( ) );
            paramMap.put ( "sex", String.valueOf ( account.getSex ( ) ) );
            paramMap.put ( "nickname", account.getNickname ( ) );
            paramMap.put ( "openid", account.getOpenid ( ) );
            paramMap.put ( "city", account.getCity ( ) );
            paramMap.put ( "country", account.getCountry ( ) );
            paramMap.put ( "province", account.getProvince ( ) );
            paramMap.put ( "headimgurl", account.getAccountIcon ( ) );
            paramMap.put ( "unionid", account.getAccountUnionId ( ) );
            paramMap.put ( "timestamp", String.valueOf ( timestamp ) );
            paramMap.put ( "appid", Constant.APP_ID );
            paramMap.put ( "version", application.getAppVersion ( context ) );
            paramMap.put ( "operation", Constant.OPERATION_CODE );
            paramMap.put ( "sign", getSign ( paramMap ) );
            //去掉null值
            paramMap = removeNull(paramMap);
            return paramMap ;
    }

    private Map removeNull(Map map)
    {
        Map<String, String> lowerMap = new HashMap< String, String > (  );
        Iterator lowerIt = map.entrySet ().iterator ();
        while ( lowerIt.hasNext () )
        {

            Map.Entry entry = ( Map.Entry ) lowerIt.next ();
            Object value = entry.getValue ( );
            if( null != value )
            {
                lowerMap.put ( String.valueOf ( entry.getKey () ).toLowerCase (), String.valueOf ( value ) );
            }
        }

        return lowerMap;
    }

    public String obtainUrls()
    {
        StringBuilder builder = new StringBuilder (  );
        try {
            Map< String, String > paramMap = new HashMap< String, String > ( );
                //获取url中的参数
                String params = url.substring ( url.indexOf ( "?" ) + 1, url.length ( ) );
                String[] str = params.split ( "&" );
                if ( str.length > 0 ) {
                    for ( String map : str ) {
                        //获取参数
                        String[] values = map.split ( "=" );
                        if ( 2 == values.length ) {
                            paramMap.put ( values[ 0 ], URLEncoder.encode ( values[ 1 ], "UTF-8" ) );
                        }
                        else if ( 1 == values.length ) {
                            paramMap.put ( values[ 0 ], null );
                        }
                    }
                }

                //添加额外固定参数
                paramMap.put ( "version", application.getAppVersion ( context ) );
                paramMap.put ( "operation", Constant.OPERATION_CODE );
                //1、timestamp
                paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ), "UTF-8" ) );
                //appid
                paramMap.put ( "appid", URLEncoder.encode ( Constant.APP_ID , "UTF-8" ));
                //生成sigin
                paramMap.put ( "sign", getSign ( paramMap ) );

                builder.append ( url );
                builder.append ( "&timestamp=" + paramMap.get ( "timestamp" ) );
                builder.append ( "&appid="+paramMap.get ( "appid" ) );
                builder.append ( "&sign="+paramMap.get ( "sign" ) );
                builder.append ( "&version=" + application.getAppVersion ( context ) );
                builder.append ( "&operation=" + Constant.OPERATION_CODE );

            return builder.toString ();
        }
        catch ( UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            L.e ( e.getMessage ( ) );
            return null;
        }

    }

    public String obtainUrlName()
    {
        StringBuilder builder = new StringBuilder (  );
        try {
            Map< String, String > paramMap = new HashMap< String, String > ( );
            //获取url中的参数
            String params = url.substring ( url.indexOf ( "?" ) + 1, url.length ( ) );
            String[] str = params.split ( "&" );
            if ( str.length > 0 ) {
                for ( String map : str ) {
                    //获取参数
                    String[] values = map.split ( "=" );
                    if ( 2 == values.length ) {
                        paramMap.put ( values[ 0 ], URLEncoder.encode ( values[ 1 ], "UTF-8" ) );
                    }
                    else if ( 1 == values.length ) {
                        paramMap.put ( values[ 0 ], "" );
                    }
                }
            }

            //添加额外固定参数
            paramMap.put ( "version", application.getAppVersion ( context ) );
            paramMap.put ( "operation", Constant.OPERATION_CODE );
            //1、timestamp
            paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ), "UTF-8" ) );
            //appid
            paramMap.put ( "appid", URLEncoder.encode ( Constant.APP_ID , "UTF-8" ));
            //生成sigin
            paramMap.put ( "sign", getSign ( paramMap ) );

            builder.append ( url );
            builder.append ( "&timestamp=" + paramMap.get ( "timestamp" ) );
            builder.append ( "&appid="+paramMap.get ( "appid" ) );
            builder.append ( "&sign="+paramMap.get ( "sign" ) );
            builder.append ( "&version=" + application.getAppVersion ( context ) );
            builder.append ( "&operation=" + Constant.OPERATION_CODE );

            return builder.toString ();
        }
        catch ( UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            L.e ( e.getMessage ( ) );
            return null;
        }

    }

    public String obtainUrlOrder()
    {
        StringBuilder builder = new StringBuilder (  );
        try {
            Map< String, String > paramMap = new HashMap< String, String > ( );
            //获取url中的参数
            String params = url.substring ( url.indexOf ( "?" ) + 1, url.length ( ) );
            String[] str = params.split ( "&" );
            if ( str.length > 0 ) {
                for ( String map : str ) {
                    //获取参数
                    String[] values = map.split ( "=" );
                    if ( 2 == values.length ) {
                        paramMap.put ( values[ 0 ], URLEncoder.encode ( values[ 1 ], "UTF-8" ) );
                    }
                    else if ( 1 == values.length ) {
                        paramMap.put ( values[ 0 ], null );
                    }
                }
            }

            //添加额外固定参数
            paramMap.put ( "version", application.getAppVersion ( context ) );
            paramMap.put ( "operation", Constant.OPERATION_CODE );
            //1、timestamp
            paramMap.put ( "timestamp", URLEncoder.encode ( String.valueOf ( timestamp ), "UTF-8" ) );
            //appid
            paramMap.put ( "appid", URLEncoder.encode ( Constant.APP_ID , "UTF-8" ));
            //生成sigin
            paramMap.put ( "sign", getSign ( paramMap ) );

            builder.append ( url );
            builder.append ( "&timestamp=" + paramMap.get ( "timestamp" ) );
            builder.append ( "&appid="+paramMap.get ( "appid" ) );
            builder.append ( "&sign="+paramMap.get ( "sign" ) );
            builder.append ( "&version=" + application.getAppVersion ( context ) );
            builder.append ( "&operation=" + Constant.OPERATION_CODE );

            return builder.toString ();
        }
        catch ( UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            L.e ( e.getMessage ( ) );
            return null;
        }
    }

    private String getMapSign(Map<String, String> map) throws UnsupportedEncodingException {




//        Map<String, String> resultMap = new TreeMap<String, String>();
//        for (Object key : map.keySet()) {
//            resultMap.put(key.toString(), map.get(key));
//        }
//
//        StringBuilder strB = new StringBuilder();
//        for (String key : resultMap.keySet()) {
//            if (!"sign".equals(key) && !TextUtils.isEmpty(resultMap.get(key))) {
//                strB.append("&" + key + "=" + resultMap.get(key));
//            }
//        }
//        String toSign = (strB.toString().length() > 0 ? strB.toString().substring(1) : "") + "1165a8d240b29af3f418b8d10599d0dc";


        String toSign = doSort(map);


        return EncryptUtil.getInstance().encryptMd532(toSign).toLowerCase();
    }


    private String getSign(Map map)
    {
        String values = this.doSort(map);
        L.i ( "sign", values );
        // values = URLEncoder.encode(values);
        //String signHex =DigestUtils.md5DigestAsHex(values.toString().getBytes("UTF-8")).toLowerCase();
        String signHex = EncryptUtil.getInstance().encryptMd532(values);
        L.i("signHex", signHex);
        return signHex;
    }

    /**
     *
     * @方法描述：获取sign码第二步：参数排序
     * @方法名：doSort
     * @参数：@param map
     * @参数：@return
     * @返回：String
     * @exception
     * @since
     */
    private String doSort(Map<String, String> map)
    {
        //将MAP中的key转成小写
        Map<String, String> lowerMap = new HashMap< String, String > (  );
        Iterator lowerIt = map.entrySet ().iterator ();
        while ( lowerIt.hasNext () )
        {
            Map.Entry entry = ( Map.Entry ) lowerIt.next ();
            Object value = entry.getValue ( );
            if( ! TextUtils.isEmpty ( String.valueOf ( value ) ) )
            {
                lowerMap.put ( String.valueOf ( entry.getKey () ).toLowerCase (), String.valueOf ( value ) );
            }
        }

        TreeMap<String, String> treeMap = new TreeMap< String, String > ( lowerMap );
        StringBuffer buffer = new StringBuffer();
        Iterator it = treeMap.entrySet ().iterator ();
        while(it.hasNext ())
        {
            Map.Entry entry =(Map.Entry) it.next();
            buffer.append ( entry.getKey ()+"=" );
            buffer.append ( entry.getValue ()+"&" );
        }
        String suffix = buffer.substring ( 0, buffer.length ()-1 )+  Constant.APP_SECRET;//Constant.APP_SECRET;
        return suffix;
    }

    public Map obtainParams(Map map){
        Map< String, Object > paramMap = new HashMap< String, Object > ( );
        if( map !=null ){
            paramMap.putAll(map);
        }
        paramMap.put ( "timestamp", String.valueOf ( timestamp ) );
        paramMap.put ( "appid", Constant.APP_ID );
        paramMap.put ( "version", application.getAppVersion ( context ) );
        paramMap.put ( "operation", Constant.OPERATION_CODE );
        paramMap.put ( "sign", getSign ( paramMap ) );
        //去掉null值
        paramMap = removeNull(paramMap);

        return paramMap;
    }

}
