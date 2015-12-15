package cy.com.morefan.bean;

public class PreTaskStatusData implements BaseData{
	/**
	 * taskType	0不可转发;1提前转发;2正常转发
	 * forwardLimit	是否达到转发次数上限：0未;1已达到
	 * advanceTool	提前转发道具0无;1有
	 * advanceToolExp 提前道具exp价格
	 * advanceUseTip	提前转发道具使用提示
	 * advanceBuyTip	提前转发道具购买提示
	 * addOneTool	+1道具0无;1有
	 * addOneToolExp ＋1道具exp价格
	 * addOneUseTip	+1道具使用提示
	 * addOneBuyTip	＋1道具购买提示
	 */
	public int taskType;
	public int forwardLimit;
	public int advanceTool;
	public int advanceToolExp;
	public String advanceUseTip;
	public String advanceBuyTip;
	public int addOneTool;
	public int addOneToolExp;
	public String addOneUseTip;
	public String addOneBuyTip;
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}
}
