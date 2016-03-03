package cy.com.morefan.bean;

import java.io.Serializable;

import android.os.Bundle;



public class UserData implements BaseData, Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public String mallUserId;

	public static void restore(Bundle in){
		userData = (UserData) in.getSerializable("userData");

	}
	public static void save(Bundle out){
		out.putSerializable("userData", userData);
	}

	public boolean completeInfo;
	/**
	 * 昨日积分收益
	 */
	public String yesScore;
	/**
	 * 总积分
	 */
	public String totalScore;
	/**
	 * 可用积分
	 */
	public String score;
	/**
	 * 钱包余额
	 */
	public String wallet;
	/**
	 * 锁定积分
	 */
	public String lockScore;
	/**
	 * 当日是否已签到,1已签到，0未
	 */
	public boolean dayCheckIn;
	/**
	 * 连续签到天数
	 */
	public int checkInDays;
	/**
	 * 剩余总经验
	 */
	public int exp;
	/**
	 * 头像url
	 */
	public String picUrl;
	/**
	 * 不进行模拟器判断
	 */
	public boolean ignoreJudgeEmulator;
	/**
	 * 每日任务上限
	 */
	public int totalTaskCount;
	/**
	 * 已完成任务量
	 */
	public int completeTaskCount;
	/**
	 * 主键id
	 */
	public int id;
	public String userName;
	public String UserNickName;
	public String RealName;
	public String pwd;
	public String shareDes;
	public String shareContent;

	/**
	 * 登录口令
	 */
	public String loginCode = "";
	/**
	 * 提现密码
	 */
	public String toCrashPwd;
	/**
	 * 手机号
	 */
	public String phone;
	/**
	 * 支付宝账号
	 */
	public String payAccount;
	/**
	 * 是否登录
	 */
	public boolean isLogin;
	/**
	 * 注册时间
	 */
	public String regTime;
	/**
	 * 转发量
	 */
	public int sendCount;
	/**
	 * 收藏量
	 */
	public int favCount;

	/**
	 * 兑换量
	 */
	public String crashCount;
	/**
	 * 福利量
	 */
	public int welfareCount;
	//public int sendTaskId;
	/**
	 * 是否有新反馈消息
	 */
	public boolean hasNewFeedback;
	/**
	 * 今日浏览量
	 */
	public int todayScanCount;

	/**
	 * 是否有火眼金眼
	 */
	public boolean hasPreTool;
	/**
	 * 积分汇率
	 */
	public double scorerate;


	private static UserData userData;
	public static UserData getUserData(){
			if(userData == null)
				userData = new UserData();
			return userData;
	}
	public static void clear(){
		userData = null;
	}
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
