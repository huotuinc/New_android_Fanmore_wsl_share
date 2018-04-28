package cy.com.morefan.listener;

import android.os.Bundle;
import cy.com.morefan.bean.BaseData;

public interface BusinessDataListener {
	public void onDataFinish(int type, String des, BaseData[] datas,
							 Bundle extra);

	public void onDataFailed(int type, String des, Bundle extra);

	public void onDataFail(int type, String des, Bundle extra);

	public static final int DONE_EXP_UP = 5999;//获得经验
	public static final int DONE_GET_TASK_LIST = 6000;// 获取任务列表
	public static final int DONE_GET_TASK_DETAIL = 6001;// 获取任务详情
	public static final int DONE_GET_CODE = 6002;// 获取验证码
	public static final int DONE_VERIFY_MOBILE = 8004;// 获取手机号码是否注册
	public static final int ERROR_VERIFY_MOBILE = -8004;
	public static final int DONE_USER_LOGIN = 6003;// 获取验证码
	public static final int DONE_USER_REG = 6004;// 获取验证码
	public static final int DONE_INIT = 6005;
	public static final int DONE_GET_HISTORY_SCORE = 6006;
	public static final int DONE_GET_MY_SEND_LIST = 6007;// 我的转发
	public static final int DONE_GET_MONEY_CHANGE_LIST = 6008;// 积分提现历史
	public static final int DONE_CHECK_UP = 6009;
	public static final int DONE_GET_RESEND_LIST = 6010;
	public static final int DONE_COMMIT_SEND = 6011;// 转发提交
	public static final int DONE_FAV = 6012;
	public static final int DONE_BIND_PAYACCOUNT = 6013;// 绑定支付宝账号
	public static final int DONE_COMMIT_TOCRASHPWD = 6014;// 提交提现密码
	public static final int DONE_GET_MY_FAV_LIST = 6015;// 我的收藏
	public static final int DONE_GET_MY_PARTIN_LIST = 6016;// 我的参与
	public static final int DONE_BIND_PHONE = 6017;
	public static final int DONE_UNBIND_PHONE = 6018;
	public static final int DONE_MODIFY_PWD = 6019;
	public static final int DONE_TO_RECHANGE = 6020;
	public static final int DONE_TO_GETUSERLIST = 7000;
	public static final int ERROR_TO_GETUSERLIST = -7000;
	public static final int DONE_CANCEL_FAV = 6021;
	public static final int DONE_USER_INFO = 6023;
	public static final int DONE_MODIFY_USER_INFO = 6024;
	public static final int DONE_GET_MY_FAV_DETAIL = 6025;// 我的收藏
	public static final int DONE_GET_MY_PARTIN_DETAIL = 6026;// 我的参与
	public static final int DONE_FEEDBACK = 6027;//
	public static final int DONE_USER_RESET = 6028;// 获取验证码
	public static final int DONE_FEEDBACK_LIST = 6029;//
	public static final int DONE_GET_SCANCOUNT = 6030;//
	public static final int DONE_GET_ALLSCORE_TREND = 6031;//
	public static final int DONE_GET_TODAY_SCAN = 6032;//
	public static final int DONE_GET_AWARD_LIST = 6033;//
	public static final int DONE_GET_PRENTICE = 6034;//
	public static final int DONE_GET_PRENTICE_LIST = 6035;//
	public static final int DONE_GET_PRENTICE_DETAIL = 6036;//
	public static final int DONE_GET_MALL_LIST = 6037;//
	public static final int DONE_GET_MALL_DETAIL = 6038;//
	public static final int DONE_GET_RANK = 6039;//
	public static final int DONE_GET_PUSH_MSG = 6040;//
	public static final int DONE_CHECK_TASK_STATUS = 6041;//
	public static final int DONE_CHECK_IN = 6042;//
	public static final int DONE_GET_TOOL_LIST = 6043;//
	public static final int DONE_BUY_TOOL = 6044;//
	public static final int DONE_SHAKE_PRENTICE = 6045;//
	public static final int DONE_USE_TOOL = 6046;//
	public static final int DONE_COMMIT_PHOTO = 6048;//
	public static final int DONE_GET_TICKET = 6049;//
	public static final int DONE_CHECK_PRE_TASK_STATUS = 6050;//
	public static final int DONE_CHECK_NOTICE_TASK_STATUS = 6051;
	public static final int DONE_GET_DUIBA_URL = 6052;
	public static final int DONE_GET_WALLET = 6053;
	public static final int DONE_GET_WALLET_HISTORY = 6054;

	public static final int DONE_GET_GROUP_DATA = 7001;
	public static final int DONE_GET_GROUP_PERSON = 7002;


	public static final int ERROR_GET_TASK_LIST = -6000;
	public static final int ERROR_GET_TASK_DETAIL = -6001;
	public static final int ERROR_GET_CODE = -6002;
	public static final int ERROR_USER_LOGIN = -6003;// 获取验证码
	public static final int ERROR_USER_REG = -6004;
	public static final int NOT_USER_REG = 56000;
	public static final int ERROR_INIT = -6005;
	public static final int ERROR_GET_HISTORY_SCORE = -6006;
	public static final int ERROR_GET_MY_SEND_LIST = -6007;
	public static final int ERROR_GET_MONEY_CHANGE_LIST = -6008;
	public static final int ERROR_CHECK_UP = -6009;
	public static final int ERROR_GET_RESEND_LIST = -6010;
	public static final int ERROR_COMMIT_SEND = -6011;
	public static final int ERROR_FAV = -6012;
	public static final int ERROR_BIND_PAYACCOUNT = -6013;// 绑定支付宝账号
	public static final int ERROR_COMMIT_TOCRASHPWD = -6014;// 提交提现密码
	public static final int ERROR_GET_MY_FAV_LIST = -6015;// 我的收藏
	public static final int ERROR_GET_MY_PARTIN_LIST = -6016;
	public static final int ERROR_BIND_PHONE = -6017;
	public static final int ERROR_UNBIND_PHONE = -6018;
	public static final int ERROR_MODIFY_PWD = -6019;
	public static final int ERROR_TO_RECHANGE = -6020;
	public static final int ERROR_CANCEL_FAV = -6021;
	public static final int ERROR_RE_COMMIT_SEND = -6022;// 需要重新提交
	public static final int ERROR_USER_INFO = -6023;
	public static final int ERROR_MODIFY_USER_INFO = -6024;
	public static final int ERROR_GET_MY_FAV_DETAIL = -6025;// 我的收藏
	public static final int ERROR_GET_MY_PARTIN_DETAIL = -6026;// 我的参与
	public static final int ERROR_FEEDBACK = -6027;//
	public static final int ERROR_USER_RESET = -6028;// 获取验证码
	public static final int ERROR_FEEDBACK_LIST = -6029;//
	public static final int ERROR_GET_SCANCOUNT = -6030;//
	public static final int ERROR_GET_ALLSCORE_TREND = -6031;//
	public static final int ERROR_GET_TODAY_SCAN = -6032;//
	public static final int ERROR_GET_AWARD_LIST = -6033;//
	public static final int ERROR_GET_PRENTICE = -6034;//
	public static final int ERROR_GET_PRENTICE_LIST = -6035;//
	public static final int ERROR_GET_PRENTICE_DETAIL = -6036;//
	public static final int ERROR_GET_MALL_LIST = -6037;//
	public static final int ERROR_GET_MALL_DETAIL = -6038;//
	public static final int ERROR_GET_RANK = -6039;//
	public static final int ERROR_GET_PUSH_MSG = -6040;//
	public static final int ERROR_CHECK_TASK_STATUS = -6041;//
	public static final int ERROR_CHECK_IN = -6042;//
	public static final int ERROR_GET_TOOL_LIST = -6043;//
	public static final int ERROR_BUY_TOOL = -6044;//
	public static final int ERROR_SHAKE_PRENTICE = -6045;//
	public static final int ERROR_USE_TOOL = -6046;//
	public static final int ERROR_ALREADY_CHECK_IN = -6047;//
	public static final int ERROR_COMMIT_PHOTO = -6048;//
	public static final int ERROR_GET_TICKET = -6049;//
	public static final int ERROR_CHECK_PRE_TASK_STATUS = -6050;//
	public static final int ERROR_CHECK_NOTICE_TASK_STATUS = -6051;//
	public static final int ERROR_GET_DUIBA_URL = -6052;
	public static final int ERROR_GET_WALLET = -6053;
	public static final int ERROR_GET_WALLET_HISTORY = -6054;
	public static final int DONE_TO_MOBLIELOGIN=6666;
	public static final int NULL_USER=8888;
	public static final int ERROR_TO_MOBLIELOGIN=-6666;

	public static final int ERROR_GET_GROUP_DATA = -7001;
	public static final int ERROR_GET_GROUP_PERSON = -7002;
	public static final int DONE_GET_STORE_LIST = 8000;
	public static final int ERROR_GET_STORE_LIST = -8000;
	public static final int DONE_CHECK_VERIFYCODE  = 8002;// 检查验证码是否有效
	public static final int ERROR_CHECK_VERIFYCODE  = -8002;// 检查验证码是否有效
	public static final int DONE_GET_WEEKTASK  = 8003;
	public static final int ERROR_GET_WEEKTASK  = -8003;
	public static final int DONE_GET_ORGANIZESUM = 9000;
	public static final int ERROR_GET_ORGANIZESUM = -9000;

	public static final int DONE_GET_FAVORITE_DATE= 8800;//获得我的收藏的日期列表
	public static final int ERROR_GET_FAVORITE_DATE = -8800;
	public static final int DONE_GET_FAVORITE_LIST= 8801;//获得我的收藏
	public static final int ERROR_GET_FAVORITE_LIST = -8801;
	public static final int DONE_GET_INFO_LIST = 8805;//获得资讯首页列表
	public static final int ERROR_GET_INFO_LIST = -8805;
	public static final int DONE_GET_NOTICE_LIST = 8810;//获得通知
	public static final int ERROR_GET_NOTICE_LIST=-8810;
	public static final int DONE_COLLECTION=8811;//收藏接口
	public static final int ERROR_COLLECTION=-8811;
	public static final int DONE_SCORE=8812;//获得积分
	public static final int ERROR_SCORE=-8812;
	public static final int DONE_RECHARGE=8813;//积分兑换
	public static final int ERROR_RECHARGE=-8813;
}

