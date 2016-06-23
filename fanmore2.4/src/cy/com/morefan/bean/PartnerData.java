package cy.com.morefan.bean;

/**
 * Created by 47483 on 2016/6/23.
 */

public class PartnerData implements BaseData {


    public String userName;
    public String headFace;
    public String time;
    public String userId;
    public String yesterdayBrowseAmount;
    public String historyTotalBrowseAmount;
    public String yesterdayTurnAmount;
    public String historyTotalTurnAmount;
    public int pageIndex;
    @Override
    public String getPageTag() {

        return null;
    }
}
