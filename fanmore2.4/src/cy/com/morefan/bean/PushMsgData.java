package cy.com.morefan.bean;

import android.R.integer;

public class PushMsgData implements BaseData{
/**
 * id
title
des
time
type
taskId
taskStatus
webUrl

 */
	public int id;
	public String title;
	public String des;
	public String time;
	/**
	 *1：普通消息
     *0：预告消息
     */
	public int type;
	public int taskId;
	/**
	 * 预告任务状态：1：未开始
       0：已开始
        2：已下架

	 */
	public int taskStatus;
	public String webUrl;
	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}
}
