package cy.com.morefan.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/9.
 */
public class GroupData implements BaseData ,Serializable {
    /**
     * 组织编号
     */
    private int id;
    /**
     * 组织级别
     */
    private int level;
    /**
     *
     */
    private String icon;
    /**
     * 组织名称
     */
    private String name;
    /**
     * 组织人数
     */
    private int personCount;
    /**
     * 总浏览数量
     */
    private int  totalBrowseCount;
    /**
     * 总转发数量
     */
    private int  totalTurnCount;

    @Override
    public String getPageTag() {
        return null;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPersonCount() {
        return personCount;
    }

    public void setPersonCount(int personCount) {
        this.personCount = personCount;
    }

    public int getTotalBrowseCount() {
        return totalBrowseCount;
    }

    public void setTotalBrowseCount(int totalBrowseCount) {
        this.totalBrowseCount = totalBrowseCount;
    }

    public int getTotalTurnCount() {
        return totalTurnCount;
    }

    public void setTotalTurnCount(int totalTurnCount) {
        this.totalTurnCount = totalTurnCount;
    }
}
