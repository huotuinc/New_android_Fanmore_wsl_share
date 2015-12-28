package cy.com.morefan;

import java.util.List;

/**
 * 切换用户模型
 */
public
class SwitchUserModel {

    private String msg;
    private int code;
    private
    List<SwitchUser> data;

    public String getMsg ( ) {
        return msg;
    }

    public
    void setMsg ( String msg ) {
        this.msg = msg;
    }

    public
    int getCode ( ) {
        return code;
    }

    public
    void setCode ( int code ) {
        this.code = code;
    }

    public List< SwitchUser > getData ( ) {
        return data;
    }

    public
    void setData ( List< SwitchUser > data ) {
        this.data = data;
    }

    public class SwitchUser
    {
        private int coerceloseefficacy;
        private int belongOne;
        private int IsDelete;
        private int levelID;
        private String wxUnionId;
        private int userid;
        private String password;
        private int userType;
        private String wxNickName;
        private String wxHeadImg;
        private String username;
        private String wxOpenId;
        private int customerid;
        private String levelName;

        public
        int getCoerceloseefficacy ( ) {
            return coerceloseefficacy;
        }

        public
        void setCoerceloseefficacy ( int coerceloseefficacy ) {
            this.coerceloseefficacy = coerceloseefficacy;
        }

        public
        int getBelongOne ( ) {
            return belongOne;
        }

        public
        void setBelongOne ( int belongOne ) {
            this.belongOne = belongOne;
        }

        public
        int getIsDelete ( ) {
            return IsDelete;
        }

        public
        void setIsDelete ( int isDelete ) {
            IsDelete = isDelete;
        }

        public
        int getLevelID ( ) {
            return levelID;
        }

        public
        void setLevelID ( int levelID ) {
            this.levelID = levelID;
        }

        public String getWxUnionId ( ) {
            return wxUnionId;
        }

        public
        void setWxUnionId ( String wxUnionId ) {
            this.wxUnionId = wxUnionId;
        }

        public
        int getUserid ( ) {
            return userid;
        }

        public
        void setUserid ( int userid ) {
            this.userid = userid;
        }

        public String getPassword ( ) {
            return password;
        }

        public
        void setPassword ( String password ) {
            this.password = password;
        }

        public
        int getUserType ( ) {
            return userType;
        }

        public
        void setUserType ( int userType ) {
            this.userType = userType;
        }

        public String getWxNickName ( ) {
            return wxNickName;
        }

        public
        void setWxNickName ( String wxNickName ) {
            this.wxNickName = wxNickName;
        }

        public String getWxHeadImg ( ) {
            return wxHeadImg;
        }

        public
        void setWxHeadImg ( String wxHeadImg ) {
            this.wxHeadImg = wxHeadImg;
        }

        public String getUsername ( ) {
            return username;
        }

        public
        void setUsername ( String username ) {
            this.username = username;
        }

        public String getWxOpenId ( ) {
            return wxOpenId;
        }

        public
        void setWxOpenId ( String wxOpenId ) {
            this.wxOpenId = wxOpenId;
        }

        public
        int getCustomerid ( ) {
            return customerid;
        }

        public
        void setCustomerid ( int customerid ) {
            this.customerid = customerid;
        }

        public String getLevelName ( ) {
            return levelName;
        }

        public
        void setLevelName ( String levelName ) {
            this.levelName = levelName;
        }
    }
}
