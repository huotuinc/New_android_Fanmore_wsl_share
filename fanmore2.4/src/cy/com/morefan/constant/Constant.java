package cy.com.morefan.constant;

import java.io.File;

import android.os.Environment;

import cy.com.morefan.BuildConfig;

public class Constant {
	public final static String TYPE_FROM = "TYPE_FROM";
	/**
	 * 入口枚举
	 * 总积分列表，我的参与，昨日收益，今日浏览
	 * @author edushi
	 *
	 */
	public enum FromType{
		TotalScore, MyPartIn, YesAward, TodayScan, PreTask
	}
	/**
	 * 测试url
	 */
	public static final String IP_URL = "http://taskapidev.fhsilk.com";
	//public static final String IP_URL = "http://192.168.0.150:8095";
	/**
	 * 开发url
	 */
//	public static final String IP_URL = "http://192.168.1.210:8014";
//	public static final String IP_URL = "http://api.fanmore.cn";
//	public static final String IP_URL ="http://192.168.1.6:8050";
//	public static final String IP_URL ="http://192.168.3.2:8050";

	/**
	 * 正式url
	 */
//	public static final String IP_URL ="http://taskapi.fhsilk.com";
	public static final String APP_SECRET = "2a5577a6792d46eb984580e5591467f7";
	public static final String SMS_SECRET = "0ad8abe244331aacf89c9231848c9f49";
	//测试
//	public static final String IP_URL = "http://wslapi.fancat.cn"; //"http://api.guo.fancat.cn:8084";
//	public static final String APP_SECRET = "1165a8d240b29af3f418b8d10599d0dc";
//	public static final String SMS_SECRET = "483686ad1fe2bd8a02bbdca24e109953a4a96c";

	public static final String OPERATION = "HuoTu2013AD";
	public static final String QD = "huotu";
	//public static final String QD = "dm";

	//使用mainfest中的version_name
	public static final int CAROUSE_URL = 0x00000041;
	public static final String APP_VERSION (){
		//"1.0.9";
		return BuildConfig.VERSION_NAME;
	}
	//标准时间
	public final static String TIME_FORMAT   = "yyyy-MM-dd HH:mm:ss";
	//标准时间01
	public static final String DATE_FORMAT   = "yyyy-MM-dd";
	//商家支付宝编号
	public static final String MERCHANT_ALIPAY_ID = "merchant_alipay_id";
	//商户支付宝KEY信息
	public static final String ALIPAY_KEY         = "alipay_key";
	//支付宝商家编号
	public static final String ALIPAY_MERCHANT_ID = "alipay_merchant_id";

	/**
	 * 预告web跳转标识
	 */
	public static final String PRE_JUMP_FLAG = "http://www.google.cn/";
	public static final String WX_OPEN_IP = "gh_df02dcbd8fc5";
	public static final String AuthCodeType = "AuthCodeType";
	public static final int PAGESIZE = 10;
	public static final int BACK_TIME_DIS = 30 * 60;//后台返回需刷新数据的时间间隔(单位s)

	public final static String SP_NAME_LOADING = "MFSP_LOADING";//sp内容会被不定期清理
	public final static String SP_NAME_NORMAL = "MFSP_NORMAL";//sp内容不会被清理
	public final static String SP_NAME_SINA_TOKEN = "MFSP_SINA_TOKEN";//sp内容不会被清理
	public final static String SP_NAME_BACK_TIME = "MFSP_BACK_TIME";//切换至后台的时间点
	public final static String SP_NAME_PUSH_SWITCH = "MFSP_PUSH_SWITCH";//推送设置
	public final static String SP_NAME_LAT = "MFSP_LAT";
	public final static String SP_NAME_ALARM = "MFSP_ALARM";
	public final static String SP_NAME_DATE = "MFSP_DATE";
	public final static String SP_NAME_LNG = "MFSP_LNG";
	public final static String SP_NAME_LOCATION_COUNT = "MFSP_LOCATION_COUNT";
	public final static String SP_NAME_PRENTICE_COUNT = "MFSP_PRENTICE_COUNT ";//sp内容不会被清理
	public final static String SP_NAME_CITY_CODE = "MFSP_CITYCODE";
	public final static String SP_NAME_UnionId = "MFSP_UnionId";
	public final static String SP_NAME_BuserId="MFSP_BuserId";
	public final static String SP_NAME_USERNAME = "MFSP_USERNAME";
	public final static String SP_NAME_PRE_USERNAME = "MFSP_PRE_USERNAME";
	public final static String SP_NAME_USERPWD = "MFSP_USERPWD";
	public final static String SP_NAME_TOKEN = "MFSP_TOKEN";//sp内容不会被清理
	public final static String SP_NAME_TOKEN_VALUE = "MFSP_TOKEN_VALUE";//sp内容不会被清理
	public final static String SP_NAME_NOT_SHOW_USER_GUIDE = "MFSP_NOTSHOWUSERGUIDE";

	public final static int REQUEST_CODE_CLIENT_DOWNLOAD = 8000;
	public final static int RESULT_CODE_CLIENT_DOWNLOAD_FAILED = -8000;
	public final static int RESULT_CODE_CANCEL_FAV = 8001;
	public final static int RESULT_CODE_TASK_STATUS_CHANGE = 8002;

	public static final String BASE_IMAGE_PATH = Environment.getExternalStorageDirectory().toString() + File.separator + ".mfimage";
	public static final String IMAGE_PATH_TASK = BASE_IMAGE_PATH + File.separator + "task";
	public static final String IMAGE_PATH_STORE = BASE_IMAGE_PATH + File.separator + "store";
	public static final String IMAGE_PATH_LOADING = BASE_IMAGE_PATH + File.separator + ".anim_loading";
	public static final String PATH_PKG_TEMP = BASE_IMAGE_PATH + File.separator + "pkgtemp";
	//微信登录:用户存在
		public static final int MSG_USERID_FOUND    = 1;
		//微信登录：用户不存在
		public static final int MSG_USERID_NO_FOUND = 0;
		public static final int MSG_LOGIN           = 2;
		public static final int MSG_AUTH_CANCEL     = 3;
		public static final int MSG_AUTH_ERROR      = 4;
		public static final int MSG_AUTH_COMPLETE   = 5;
		//鉴权失效
		public static final int LOGIN_AUTH_ERROR = 2131;
		public static final int INIT_MENU_ERROR = 6361;
		public static final String MERCHANT_INFO = "merchant_info";
		//网络请求前缀
		public static final String PREFIX            = "prefix";
		/**
		 * 操作平台码
		 */
		public static final String OPERATION_CODE = "BUYER_ANDROID_2015DC";

		public static final String APPKEY = "b73ca64567fb49ee963477263283a1bf";
		/**
		 * *******************preference参数设置
		 */
	
		//会员信息
		public static final String MEMBER_INFO              = "member_info";
		//会员等级
		public static final String MEMBER_level             = "member_level";
		//会员名称
		public static final String MEMBER_NAME              = "member_name";
		//会员ID
		public static final String MEMBER_ID                = "member_id";
		//会员token
		public static final String MEMBER_UNIONID           = "member_unionid";
		//会员token
		public static final String MEMBER_TOKEN             = "member_token";
		//会员icon
		public static final String MEMBER_ICON              = "member_icon";
		//商户ID
		public static final String MERCHANT_INFO_ID         = "merchant_id";
		//商户支付宝key信息
		public static final String MERCHANT_INFO_ALIPAY_KEY = "merchant_alipay_key";
		//商户微信支付KEY信息
		public static final String MERCHANT_INFO_WEIXIN_KEY = "merchant_weixin_key";
		//商户菜单
		public static final String MERCHANT_INFO_MENUS      = "main_menus";
		//商户分类菜单
		public static final String MERCHANT_INFO_CATAGORY   = "catagory_menus";

		/**
		 * ************************定位信息设置
		 */
		public static final String LOCATION_INFO = "location_info";
		public static final String ADDRESS       = "address";
		public static final String LATITUDE      = "latitude";
		public static final String LONGITUDE     = "Longitude";
		public static final String CITY          = "city";

		/**
		 * 底部Tab菜单
		 */
		public static final String TAB_1 = "TAB_1";
		public static final String TAB_2 = "TAB_2";
		public static final String TAB_3 = "TAB_3";
		public static final String TAB_4 = "TAB_4";
		public static final String TAB_5 = "TAB_5";
		public static final String TAB_6 = "TAB_6";

		//http请求参数
		//获取具体页面的商品类别
		public static final String HTTP_OBTAIN_CATATORY = "/goods/obtainCatagory?";
		//获取商品信息
		public static final String HTTP_OBTAIN_GOODS    = "/goods/obtainGoods?";
		//new view
		public static final String WEB_TAG_NEWFRAME     = "__newframe";
		//上传图片
		public static final String WEB_TAG_COMMITIMG    = "partnermall520://pickimage";
		//登出
		public static final String WEB_TAG_LOGOUT       = "partnermall520://logout";
		//信息保护
		public static final String WEB_TAG_INFO         = "partnermall520://togglepb";
		//关闭当前页
		public static final String WEB_TAG_FINISH       = "partnermall520://closepage";
		//share
		public static final String WEB_TAG_SHARE        = "partnermall520://shareweb";
		//弹出框
		public static final String WEB_TAG_ALERT        = "partnermall520://alert";
		//支付
		public static final String WEB_TAG_PAYMENT      = "partnermall520://payment";
		//用户信息修改
		public static final String WEB_TAG_USERINFO     = "partnermall520://userinfo?";
		//联系客服
		public static final String WEB_CONTACT          = "mqqwpa://im/chat";

		//支付
		public static final String WEB_PAY      = "/Mall/AppAlipay.aspx";
		//鉴权失效
		public static final String AUTH_FAILURE = "/UserCenter/Login.aspx";

		//网页支付
		public static final int PAY_NET = 2222;

		//是否测试环境
		public static final boolean IS_TEST = true;

		//handler code
		//1、success
		public static final int SUCCESS_CODE = 0;
		//2、error code
		public static final int ERROR_CODE   = 1;
		//3、null code
		public static final int NULL_CODE    = 2;

	

		public static final String INTENT_URL   = "INTENT_URL";
		public static final String INTENT_TITLE = "INTENT_TITLE";

		/**
		 * 修改密码
		 */
		public static final String MODIFY_PSW = "modifyPsw";

		/**
		 * 侧滑菜单加载页面
		 */
		public static final int LOAD_PAGE_MESSAGE_TAG = 4381;

		/**
		 * 关闭载入用户数据
		 */
		public static final int LOAD_SWITCH_USER_OVER = 8888;

		/**
		 * tile栏刷新页面
		 */
		public static final int FRESHEN_PAGE_MESSAGE_TAG = 4380;


		/**
		 * 切换用户
		 */
		public static final int SWITCH_USER_NOTIFY = 9988;

		/**
		 * 首页Url
		 */
		public static final String HOME_PAGE_URL = "http://www.baidu.com";

		/**
		 * 基本信息
		 */
		public static final String BASE_INFO   = "base_ifo";
		/**
		 * 当前加载的页面
		 */
		public static final String CURRENT_URL = "current_url";

		//页面的类型
		/**
		 * 设置界面
		 */
		public static final String PAGE_TYPE_SETTING = "setting";

		/**
		 * 微信支付appID
		 */
		public static final String WXPAY_ID    = "wx369cdeb338051de5";
		public static final String WXPAY_SECRT = "0dcbc62b9a4724e89097bb00a0bd0ae2";
		public static final String PAY_URL     = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

		// 商户PID
		public static final String PARTNER      = "";
		// 商户收款账号
		public static final String SELLER       = "";
		// 商户私钥，pkcs8格式
		public static final String RSA_PRIVATE  = "";
		// 支付宝公钥
		public static final String RSA_PUBLIC   = "";
		public static final int    SDK_PAY_FLAG = 1;

		public static final int SDK_CHECK_FLAG = 2;

		// API密钥，在商户平台设置(微信支付商户)
		public static final String wxpayApikey    = "0db0d4908a6ae6a09b0a7727878f0ca6";
		//微信parterKey
		public static final String wxpayParterkey = "1251040401";




	
		/**
		 * capCode
		 */
		public static final String CAP_CODE = "default";

		public final static int ANIMATION_COUNT = 1000;

		/**
		 * app系统配置
		 */
		public static final String SYS_INFO    = "sysInfo";
		public static final String FIRST_OPEN  = "firstOpen";

		/**
		 * app颜色配置
		 */
		public static final String COLOR_INFO   = "colorInfo";
		public static final String COLOR_MAIN   = "mainColor";
		public static final String COLOR_SECOND = "secondColor";

		public static final String CUSTOMER_ID = "customerid={}";
		public static final String USER_ID     = "userid={}";

		//测试
		public static final String APP_ID = "huotuacf89c9231848c9f49";
		//正式
		//public static final String APP_ID = "huotuacf89c9231848c9f49";

		//接口连接前缀
		//测试
		public static final String INTERFACE_PREFIX = "http://mallapi.huobanj.cn/";
		//正式
		//public static final String INTERFACE_PREFIX = "http://mallapi.huobanmall.com/";
		//接口连接前缀
		//public static final String INTERFACE_PREFIX = "http://192.168.1.56:8089/";

		// 平台安全码


	//微信支付
	public static final String WX_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		public static final String ALIPAY_NOTIFY = "alipay_notify";
		public static final String WEIXIN_NOTIFY = "weixin_notify";
		public static final String IS_WEB_WEIXINPAY = "is_web_weixinpay";
		public static final String IS_WEB_ALIPAY = "is_web_alipay";

		public static final String COMMON_SHARE_LOGO = "http://1804.img.pp.sohu.com.cn/images/2013/1/14/16/2/6205e011f029437o_13cfbf362e6g85.jpg";

		//数据包版本号
		public static final String DATA_INIT            = "data_init";
		//会员信息
		public static final String PACKAGE_VERSION              = "package_version";
	//微信商家编号
	public static final String WEIXIN_MERCHANT_ID = "weixin_merchant_id";
	//商户微信支付KEY信息
	public static final String WEIXIN_KEY         = "weixin_key";
	//商家微信编号
	public static final String MERCHANT_WEIXIN_ID = "merchant_weixin_id";

	public static final String IS_POP_SHARE_DIALOG="is_pop_share_dialog";

		

}
