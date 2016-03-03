package cy.com.morefan.bean;


public class PrenticeData implements BaseData{
	public int id;
	public String pageTag;
	public String name;
	/**
	 * 上次贡献时间/拜师时间
	 */
	public String invitationCode;
	public String prenticeDes;
	public String prenticeShareDes;
	public String prenticeShareUrl;
	public int 	prenticeAmount;
	public String time;
	public String lastContri;
	public String totalContri;
	public String yesterdayBrowseAmount;
	public String historyTotalBrowseAmount;
	public String yesterdayTurnAmount;
	public String historyTotalTurnAmount;


	@Override
	public boolean equals(Object o) {
		if (o instanceof PrenticeData) {
			PrenticeData data = (PrenticeData) o;
			return this.id == data.id;

		}
		return super.equals(o);
	}
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return this.pageTag;
	}

}
