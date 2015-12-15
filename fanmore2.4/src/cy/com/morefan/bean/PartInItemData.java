package cy.com.morefan.bean;

import java.io.Serializable;

public class PartInItemData implements BaseData, Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public String time;
	/**
	 * yyyy-MM-dd
	 */
	public String date;
	public String des;
	public String score;
	public String pageTag;//分页
	/**
	 * 渠道类型微信：1；新浪微博：2;qq空间：3
	 */
	public int channel;
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return this.pageTag;
	}

}
