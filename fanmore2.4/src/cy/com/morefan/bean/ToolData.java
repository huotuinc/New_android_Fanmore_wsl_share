package cy.com.morefan.bean;

public class ToolData implements BaseData{
	//公共属性
	/**
	 * 道具名
	 */
	public String name;
	/**
	 * 道具价值
	 */
	public int value;
	/**
	 * 道具类型
	 * 1 摇徒弟
	 * 2抽奖
	 * 3任务转发提限
	 * 4任务提前预览
	 */
	public int type;
	/**
	 * 描述
	 */
	public String desc;
	/**
	 * 剩余量
	 */
	public int residueCount;
	/**
	 * 总量
	 */
	public int totalCount;

	/**
	 * 拥有量
	 */
	public int ownCount;

	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
