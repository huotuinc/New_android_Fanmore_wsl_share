package cy.com.morefan.bean;

import java.io.Serializable;
import java.util.List;

import android.R.integer;


public class TaskData implements BaseData, Serializable {
	/**
	 * 总积分
	 */
	public String totalScore;
	/**
	 * 剩余积分
	 */
	public String lastScore;

	/**
	 * 是否显示转发按钮（0不显示，1显示）
	 */
	public int flagShowSend;
	/**
	 * 是否限制转发次数（0不限制，1限制）
	 */
	public int flagLimitCount;
	/**
	 * 任务详情界面内是否有任务介绍（0无（只有WEBVIEW），1有）
	 */
	public int flagHaveIntro;
	/**
	 * 详情预览url
	 */
	public String taskPreview;
	/**
	 * 当日浏览量
	 */
	public int dayScanCount;
	/**
	 * 总浏览量
	 */
	public int totalScanCount;
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	public int id;
	/**
	 * 任务名称
	 */
	public String taskName;


	/**
	 * 任务小图url
	 */
	public String smallImgUrl;
	/**
	 * 任务大图url
	 */
	public String largeImgUrl;
	/**
	 * 是否为置顶任务
	 */
	public boolean isTop;
	/**
	 * 已转发渠道
	 */
	public List<String> channelIds;




	/**
	 * 与今天的时间差
	 */
	public int dayCount;
	/**
	 * 与当前时间相差几天几时几分
	 */
	public String dayDisDes;
	/**
	 * 任务描述
	 */
	//public String des;
	/**
	 * 奖励描述
	 */
	public String awardSend;
	public String awardScan;
	public String awardLink;
	/**
	 * 发布商家图url
	 */
	public String storeImgUrl;
	/**
	 * 商家id
	 */
	public String storeId;
	/**
	 * 发布商家
	 */
	public String store;
	/**
	 * 发布时间
	 */
	public String startDate;
	public String endDate;
	public String creatTime;
	public String startTime;
	/**
	 * 提前时间
	 */
	public String advTime;
	/**
	 * 服务器当前时间
	 */
	public String curTime;
	/**
	 * 转发量
	 */
	public int sendCount;
	/**
	 * 微信公众号原始id
	 */
	//public String openId;
	/**
	 * 任务类型，活动/文本
	 */
	public int type;
	/**
	 * 任务状态
	 * 9 联盟任务已下架
	 */
	public int status;

	/**
	 * 0不可转发;1提前转发;2正常转发
	 */
	public int sendstatus;

	//detail
	/**
	 * 任务内容
	 */
	public String content;


	/**
	 * 转发对象集合
	 */
	public List<SendData> sendDatas;


	public int position;

	//用户登录后拥有的属性
	/**
	 * 是否已收藏
	 */
	public boolean isFav;
	/**
	 * 是否已转发
	 */
	public boolean isSend;
	/**
	 * 是否已结算
	 */
	public boolean isAccount;
	public String myAwardSend;
	public String myAwardScan;
	public String myAwardLink;
	public String myYesAwardSend;
	public String myYesAwardScan;
	public String myYesAwardLink;
	public String myExtra;
	/**
	 * 我的参与表中自增id，分页用
	 */
	public int partInAutoId;

	/**
	 * 0 不可提前转发 1 可提前转发 2 已上线
	 */
	public int online;
	/**
	 * 闪购描述
	 */
	public String extraDes;
	/**
	 * 对闪购任务，返利描述。
	 */
	public String rebate;
	public String alarmTime;
	public String alarmTimePre;
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof TaskData) {
			TaskData data = (TaskData) o;
			return this.id == data.id;
		}
		return super.equals(o);
	}

}
