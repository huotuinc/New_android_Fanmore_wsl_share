package cy.com.morefan.bean;


public class ShakePrenticeData implements BaseData {
	public boolean hasResult;
	public String imgUrl;
	public int id;
	/**
	 * 0未知，1男2女
	 */
	public int sex;
	public String regTime;
	public int sentCount;
	public String name;

	public void clear(){
		this.hasResult = false;
		this.imgUrl = null;
		this.id = 0;
		this.sex = 0;
		this.regTime = null;
		this.sentCount = 0;
		this.name = null;
	}

	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
