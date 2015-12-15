package cy.com.morefan.bean;

import java.io.Serializable;


/**
 * 转发类
 */
public class SendData implements BaseData,Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public String phone;
	public String time;
	public String des;
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}
}
