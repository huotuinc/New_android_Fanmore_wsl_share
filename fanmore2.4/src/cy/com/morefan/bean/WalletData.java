package cy.com.morefan.bean;

public class WalletData implements BaseData{
	public String id;
	/**
	 * 1收入;2支出
	 */
	public int type;
	public String amount;
	public String time;
	public String actionDes;

	@Override
	public boolean equals(Object o) {
		if(o instanceof WalletData){
			return this.id.equals(((WalletData)o).id);
		}
		return super.equals(o);
	}

	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return id;
	}


}
