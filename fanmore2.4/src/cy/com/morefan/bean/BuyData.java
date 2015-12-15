package cy.com.morefan.bean;

import java.util.List;

public class BuyData implements BaseData{
	public String orderNo;
	public int orderID;
	public String goodsName;
	public String time;
	public String name;
	/**
	 * 临时积分
	 */
	public String tempScore;
	/**
	 * 实际积分
	 */
	public String realScore;
	public String timeCount;
	public String orderTime;
	/**
	 * 0、待转正
		1、已转正
		-1、待转正状态下被作废
		-2、已转正状态下被作废
	 */
	public int status;
	public String statusDes;
	public List<BuyItemData> listDatas;

	@Override
	public boolean equals(Object o) {
		if(o instanceof BuyData){
			BuyData data = (BuyData) o;
			return this.orderID == data.orderID;

		}
		return super.equals(o);
	}

	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
