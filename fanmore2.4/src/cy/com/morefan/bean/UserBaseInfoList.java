package cy.com.morefan.bean;

import java.io.Serializable;
import java.util.List;

public class UserBaseInfoList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<UserSelectData> industryList;
	public List<UserSelectData> favoriteList;
	public List<UserSelectData> incomeList;
	public List<UserSelectData> malluserlist;

}
