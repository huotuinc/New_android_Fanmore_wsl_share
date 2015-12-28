package cy.com.morefan;

import java.io.Serializable;

/**
 * 基础bean
 */
public
class BaseBean implements Serializable {

    //appversion
    private String appVersion;
    //appName
    private String appName;
    //appBuild
    private String appBuild;

    public String getAppVersion ( ) {
        return appVersion;
    }

    public
    void setAppVersion ( String appVersion ) {
        this.appVersion = appVersion;
    }

    public String getAppName ( ) {
        return appName;
    }

    public
    void setAppName ( String appName ) {
        this.appName = appName;
    }

    public String getAppBuild ( ) {
        return appBuild;
    }

    public
    void setAppBuild ( String appBuild ) {
        this.appBuild = appBuild;
    }
}
