package cy.com.morefan.bean;

import java.io.Serializable;
import java.util.List;

public class StoreData implements BaseData, Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public String imgUrl;
	public String name;
	public String taskCount;
	public String opentId;
	public String id;
	public List<TaskData> taskDatas;
	public String pageTag;//分页id
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return this.pageTag;
	}

}
