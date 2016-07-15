package cy.com.morefan.bean;

import android.R.integer;

public class PrenticeTopData implements BaseData{
//	邀请码
//	收徒描述
//	分享描述
//	分享地址
//	徒弟个数
//	徒弟昨日贡献总值
//	徒弟总贡献值（int）
	public String invitationCode;
	public String prenticeDes;
	public String prenticeShareDes;
	public String prenticeShareUrl;
	public int 	prenticeAmount;
	public String lastContri;
	public String totalContri;
	// 昨日浏览量
	public String yesterdayBrowseAmount;
	// 历史总浏览量
	public String historyTotalBrowseAmount;
	/// 昨日转发量
	public String yesterdayTurnAmount;
	// 历史总转发量
	public String historyTotalTurnAmount;

	public String shareTitle;

	@Override
	public String getPageTag() {
		// TODO Auto-generated method stub
		return null;
	}

}
