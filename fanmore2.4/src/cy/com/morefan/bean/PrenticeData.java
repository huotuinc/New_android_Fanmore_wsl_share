package cy.com.morefan.bean;


public class PrenticeData implements BaseData{
	public int id;
	public String pageTag;
	public String name;
	/**
	 * 上次贡献时间/拜师时间
	 */
	public String time;
	public String lastContri;
	public String totalContri;
	public Double yesterdayBrowseAmount;
	public Double historyTotalBrowseAmount;
	public Double yesterdayTurnAmount;
	public Double historyTotalTurnAmount;


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
