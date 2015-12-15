package cy.com.morefan.bean;

public class HistoryData implements BaseData {
	public String action;
	public String time;
	public String status;
	public String extra;
	public String pageTag;//id（分页用）
	@Override
	public String getPageTag() {
		return this.pageTag;
	}



//	public String money;
//	public String score;
//	public String time;
//	public int status;
//	public String des;
//	public String extras;
//	public int tagId;//提现表自增id（分页用）

}
