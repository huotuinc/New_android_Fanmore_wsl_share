package cy.com.morefan.bean;

public class AwardData implements BaseData{
	public int id;
	public String titile;
	public String imgUrl;
	public String des;
	/**
	 * 此条目积分
	 */
	public String score;
	/**
	 * 当日总积分
	 */
	public String dayScore;
	/**
	 * 此条目浏览量
	 */
	public int scanCount;
	/**
	 * 当日总浏览量
	 */
	public int dayScanCount;

	/**
	 * //1活动类型收益，2任务类型收益,3闪购类型收益
	 */
	public int type;
	public String date;
	/**
	 * yyyy-MM-dd HH:mm
	 */
	public String dateDes;
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
