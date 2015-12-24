package cy.com.morefan.util;

import java.util.List;

public
class AuthMallModel {

    private int code;
    private String msg;
    private AuthMall data;

    public
    int getCode ( ) {
        return code;
    }

    public
    void setCode ( int code ) {
        this.code = code;
    }

    public String getMsg ( ) {
        return msg;
    }

    public
    void setMsg ( String msg ) {
        this.msg = msg;
    }

    public
    AuthMall getData ( ) {
        return data;
    }

    public
    void setData ( AuthMall data ) {
        this.data = data;
    }

    public class AuthMall
    {
        private int userid;
        private String levelName;
        private String nickName;
        private String headImgUrl;
        private List<MenuModel> home_menus;
        private int bindUserCount;
        private int userType;

        public int getBindUserCount() {
            return bindUserCount;
        }

        public void setBindUserCount(int bindUserCount) {
            this.bindUserCount = bindUserCount;
        }

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public List< MenuModel > getHome_menus ( ) {
            return home_menus;
        }

        public
        void setHome_menus ( List< MenuModel > home_menus ) {
            this.home_menus = home_menus;
        }

        public String getNickName ( ) {
            return nickName;
        }

        public
        void setNickName ( String nickName ) {
            this.nickName = nickName;
        }

        public String getHeadImgUrl ( ) {
            return headImgUrl;
        }

        public
        void setHeadImgUrl ( String headImgUrl ) {
            this.headImgUrl = headImgUrl;
        }

        public String getLevelName ( ) {
            return levelName;
        }

        public
        void setLevelName ( String levelName ) {
            this.levelName = levelName;
        }

        public
        int getUserid ( ) {
            return userid;
        }

        public
        void setUserid ( int userid ) {
            this.userid = userid;
        }
    }

    public class MenuModel
    {
        private int menu_group;
        private String menu_name;
        private String menu_url;
        private String menu_icon;
        private String menu_icon_url;

        public
        int getMenu_group ( ) {
            return menu_group;
        }

        public
        void setMenu_group ( int menu_group ) {
            this.menu_group = menu_group;
        }

        public String getMenu_name ( ) {
            return menu_name;
        }

        public
        void setMenu_name ( String menu_name ) {
            this.menu_name = menu_name;
        }

        public String getMenu_url ( ) {
            return menu_url;
        }

        public
        void setMenu_url ( String menu_url ) {
            this.menu_url = menu_url;
        }

        public String getMenu_icon ( ) {
            return menu_icon;
        }

        public
        void setMenu_icon ( String menu_icon ) {
            this.menu_icon = menu_icon;
        }

        public String getMenu_icon_url() {
            return menu_icon_url;
        }

        public void setMenu_icon_url(String menu_icon_url) {
            this.menu_icon_url = menu_icon_url;
        }
    }
}
