package cy.com.morefan.bean;

public class FeedbackData implements BaseData {

	public String id;
	/**
	 * 问题内容
	 */
	public String commitContent;
	/**
	 * 反馈时间
	 */
	public String commitTime;
	/**
	 * 0处理中1已处理
	 */
	public int status;
	/**
	 * 处理的状态
	 */
	public String statusName;
	/**
	 * 处理后的内容
	 */
	public String backContent;
	/**
	 * 处理的时间
	 */
	public String backTime;
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return id;
	}

}
