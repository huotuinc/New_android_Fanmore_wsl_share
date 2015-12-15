package cy.com.morefan.bean;



public class TempPushMsgData {
	public boolean isRunning;
	public boolean fromNotify;
	/**
	 *  0=任务推送， 1=web推送
	 */
	public int type;
	/**
	 * 是否已获得任务状态
	 */
	public boolean hasStatus;
	/**
	 *0已开始
	 *1未开始
	 *2已下架
	 */
	public int status;
	public String webUrl;
	public int taskId;
	private static TempPushMsgData data;
	private TempPushMsgData(){

	}
	public static TempPushMsgData getIns(){
		if(data == null)
			data = new TempPushMsgData();
		return data;
	}
	public static void clear(){
		data.fromNotify = false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ">>type:" + type + " status:" + status + " url:" + webUrl + " taskId:" + taskId;
	}

}
