package cy.com.morefan.constant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.os.Bundle;

import cy.com.morefan.bean.AccountModel;


public class BusinessStatic implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static BusinessStatic mStatic;
	public static BusinessStatic getInstance(){
		if(mStatic == null)
			mStatic = new BusinessStatic();
		return mStatic;
	}
	public static void restore(Bundle in){
		mStatic = (BusinessStatic) in.getSerializable("BusinessStatic");

	}
	public static void save(Bundle out){
		out.putSerializable("BusinessStatic", mStatic);
	}
	public  String SMS_TAG = "(MoreFan)";
	public  boolean SMS_ENALBE = true;
	public  String CITY_CODE = "0";//杭州市
	public  int CHANGE_BOUNDARY;//提现下限

	public  String IMEI = "";
	public  int TODAY_TOTAL_SCORE;//今日任务总额
	public  int AWARD_SEND;
	public  int AWARD_SCAN;
	public  int AWARD_LINK;
	public  String URL_RULE;
	public  String URL_ABOUTUS;
	public  String URL_PUTIN;//投放指南
	public  String URL_TOOL;//投放指南
	public  String URL_MANUALSERVICE;//人工服务
	public  String URL_SERVICE;
	public  double USER_LAT;
	public  double USER_LNG;
	public  int API_LEVEL;
	public  int TASK_TIME_LAG;//任务间隔(单位s)
	public  List<String> CHANNEL_LIST = new ArrayList<String>();//支持的渠列表
	public  String SINA_KEY_SECRET;
	/**
	 * 0,非灾难;1、灾难
	 */
	public  boolean disasterFlag;
	public  String disasterUrl;
	/**
	 * 是否为模拟器
	 */
	public  boolean ISEMULATOR;
	public  String grenadeRewardInfo;
	/**
	 * 栏目数据
	 */
	public  LinkedHashMap<String, Integer> GROUPS = new LinkedHashMap<String, Integer>();
	//public  String[] checkExps;

	/**
	 * 提现类型:0积分提现;1积分到钱包
	 */
	public  int CRASH_TYPE = 1;

	public  String WEIXIN_IGNORE_VERSION;

	//微信用户信息
	public AccountModel accountModel;

}
