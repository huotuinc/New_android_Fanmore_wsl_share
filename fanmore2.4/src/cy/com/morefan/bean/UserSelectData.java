package cy.com.morefan.bean;

import java.io.Serializable;

public class UserSelectData implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public UserSelectData(String name, String id){
		this.name = name;
		this.id = id;
	}
	public String name;
	public String id;
}
