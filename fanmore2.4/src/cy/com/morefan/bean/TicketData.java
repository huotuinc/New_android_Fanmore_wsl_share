package cy.com.morefan.bean;

public class TicketData implements BaseData{
	public int id;
	/**
	 * 奖票剩余量
	 */
	public int residueCount;
	/**
	 * 奖票面值
	 */
	public int value;
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
