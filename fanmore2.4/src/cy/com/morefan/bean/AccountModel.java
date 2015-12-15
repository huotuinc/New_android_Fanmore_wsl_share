package cy.com.morefan.bean;

import java.io.Serializable;

/**
 * 绑定微信账户信息
 */
public
class AccountModel implements Serializable {

    //微信用户ID
    private String accountId;
    //微信用户名称
    private String accountName;
    //微信用户头像
    private String accountIcon;
    //用户Token
    private String accountToken;
    //用户unionid
    private String accountUnionId;
    //用户性别
    private int sex;
    //用户昵称
    private String nickname;
    //openId
    private String openid;

    public
    int getSex ( ) {
        return sex;
    }

    public
    void setSex ( int sex ) {
        this.sex = sex;
    }

    public
    String getNickname ( ) {
        return nickname;
    }

    public
    void setNickname ( String nickname ) {
        this.nickname = nickname;
    }

    public
    String getOpenid ( ) {
        return openid;
    }

    public
    void setOpenid ( String openid ) {
        this.openid = openid;
    }

    public
    String getCity ( ) {
        return city;
    }

    public
    void setCity ( String city ) {
        this.city = city;
    }

    public
    String getCountry ( ) {
        return country;
    }

    public
    void setCountry ( String country ) {
        this.country = country;
    }

    public
    String getProvince ( ) {
        return province;
    }

    public
    void setProvince ( String province ) {
        this.province = province;
    }

    //城市
    private String city;
    //国家
    private String country;
    //省
    private String province;

    public
    String getAccountUnionId ( ) {
        return accountUnionId;
    }

    public
    void setAccountUnionId ( String accountUnionId ) {
        this.accountUnionId = accountUnionId;
    }

    public
    String getAccountToken ( ) {
        return accountToken;
    }

    public
    void setAccountToken ( String accountToken ) {
        this.accountToken = accountToken;
    }

    public
    String getAccountId ( ) {
        return accountId;
    }

    public
    void setAccountId ( String accountId ) {
        this.accountId = accountId;
    }

    public
    String getAccountName ( ) {
        return accountName;
    }

    public
    void setAccountName ( String accountName ) {
        this.accountName = accountName;
    }

    public
    String getAccountIcon ( ) {
        return accountIcon;
    }

    public
    void setAccountIcon ( String accountIcon ) {
        this.accountIcon = accountIcon;
    }
}
