package cy.com.morefan.bean;

import java.io.Serializable;

/**
 * Created by 47483 on 2016/3/25.
 */
public class GroupPersonData implements BaseData ,Serializable {
    /**
     * 头像
     */
    private String  logo;

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrenticeCount() {
        return prenticeCount;
    }

    public void setPrenticeCount(int prenticeCount) {
        this.prenticeCount = prenticeCount;
    }

    public int getTotalBrowseCount() {
        return totalBrowseCount;
    }

    public void setTotalBrowseCount(int totalBrowseCount) {
        this.totalBrowseCount = totalBrowseCount;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getTotalTurnCount() {
        return totalTurnCount;
    }

    public void setTotalTurnCount(int totalTurnCount) {
        this.totalTurnCount = totalTurnCount;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    /**
     * 	用户姓名

     */
    private String name;
    /**
     *徒弟数量
     */
    private int  prenticeCount;
    /**
     * 浏览数量
     */
    private int  totalBrowseCount;
    /**
     * 积分
     */
    private int totalScore;
    /**
     * 转发数量
     */
    private int  totalTurnCount;
    /**
     * 用户ID
     */
    private int  userid;

    /**
     * 列表页数
     * @return
     */
    public int pageIndex;
    @Override
    public String getPageTag() {
        return null;
    }
}
