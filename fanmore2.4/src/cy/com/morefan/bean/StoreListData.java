package cy.com.morefan.bean;

import cy.com.morefan.BaseBean;

/**
 * Created by 47483 on 2016/5/13.
 */
public class StoreListData implements BaseData {
    private String Logo;
    private int UserId;
    private String UserName;
    /**
     * 当前页数
     */
    public int pageIndex;

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String userNickName) {
        UserNickName = userNickName;
    }

    private String UserNickName;

    @Override
    public String getPageTag() {
        return null;
    }
}
