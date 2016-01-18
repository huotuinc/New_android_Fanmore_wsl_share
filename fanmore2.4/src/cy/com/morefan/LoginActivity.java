package cy.com.morefan;

import java.util.Map;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.util.AuthParamUtils;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.view.CyLoadingProgress;
import cy.com.morefan.bean.AccountModel;
import cy.com.morefan.constant.Constant;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 登录界面
 */
public
class LoginActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {

    private
    LinearLayout loginL;
    private
    AutnLogin      login;
    //handler对象
    public Handler mHandler;
    //windows类
    //WindowManager wManager;

   
   

    public
    MainApplication application;
    public Resources res;
	private TextView btn_wxlogin;
	//private CyLoadingProgress progress;

    @Override
    protected
    void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        mHandler = new Handler ( this );
        setContentView ( R.layout.login );
        application = ( MainApplication ) this.getApplication ();
       
       
      //  wManager = this.getWindowManager ( );
//        progress = new ProgressPopupWindow ( LoginActivity.this, LoginActivity.this, wManager );
        //progress = (CyLoadingProgress) findViewById(R.id.layProgress);
//        successProgress = new ProgressPopupWindow ( LoginActivity.this, LoginActivity.this, wManager );
//    
        btn_wxlogin = ( TextView ) this.findViewById ( R.id.btn_wxlogin );
        btn_wxlogin.setOnClickListener ( this );
        loginL=(LinearLayout) findViewById(R.id.lay);
        
        //loginText = ( TextView ) this.findViewById ( R.id.loginText );
        //initView();
    }

   

    @Override
    protected
    void onDestroy ( ) {
        super.onDestroy();
        dismissProgress();
//        successProgress.dismissView ();
    }

    @Override
    protected
    void onResume ( ) {
        super.onResume ( );
        if(null != progress)
        {
            dismissProgress();
        }

        btn_wxlogin.setClickable ( true );
    }

    @Override
    public
    void onClick ( View v ) {
        switch ( v.getId ( ) ) {
            case R.id.btn_wxlogin: {

                    //
                   showProgress();
                    ShareSDK.getPlatform(LoginActivity.this, Wechat.NAME);
                    login = new AutnLogin(LoginActivity.this, mHandler, loginL, application);
                    login.authorize(new Wechat(LoginActivity.this));

                    loginL.setClickable(false);

             
            }
            break;
            default:
                break;
        }
    }

    @Override
    public
    boolean handleMessage ( Message msg ) {

        switch ( msg.what )
        {

            //授权登录
            case Constant.MSG_AUTH_COMPLETE:
            {
                //提示授权成功
                Platform plat = ( Platform ) msg.obj;
                login.authorize ( plat );
            }
            break;
            //授权登录
            case Constant.LOGIN_AUTH_ERROR:
            {
            	btn_wxlogin.setClickable ( true );
            dismissProgress();
                startActivity(new Intent(LoginActivity.this, UserRegActivity.class));
                finish();
//                //successProgress.dismissView ();
//                //提示授权失败
//                String notice = ( String ) msg.obj;
////                noticePop = new NoticePopWindow ( LoginActivity.this, LoginActivity.this, wManager, notice);
////                noticePop.showNotice ( );
////                noticePop.showAtLocation (
////                        findViewById ( R.id.loginL ),
////                        Gravity.CENTER, 0, 0
////                                         );
           }
          break;
            case Constant.MSG_AUTH_ERROR:
            {
                loginL.setClickable ( true );
                dismissProgress();
                Throwable throwable = ( Throwable ) msg.obj;
                if("cn.sharesdk.wechat.utils.WechatClientNotExistException".equals ( throwable.toString () ))
                {
                    //手机没有安装微信客户端
                	ToastUtil.show(this, "手机没有安装微信客户端");
                    startActivity(new Intent(LoginActivity.this, UserRegActivity.class));

//                    noticePop = new NoticePopWindow ( LoginActivity.this, LoginActivity.this, wManager, "手机没有安装微信客户端");
//                    noticePop.showNotice ();
//                    noticePop.showAtLocation (
//                            findViewById ( R.id.loginL ),
//                            Gravity.CENTER, 0, 0
//                                             );
                }
                else
                {
                    loginL.setClickable ( true );
                    dismissProgress();
//                    //提示授权失败
               ToastUtil.show(this, "微信授权失败");
                    startActivity(new Intent(LoginActivity.this, UserRegActivity.class));

//                    noticePop = new NoticePopWindow ( LoginActivity.this, LoginActivity.this, wManager, "微信授权失败");
//                    noticePop.showNotice ();
//                    noticePop.showAtLocation (
//                            findViewById ( R.id.loginL ),
//                            Gravity.CENTER, 0, 0
//                                             );
                }

            }
            break;
            case Constant.MSG_AUTH_CANCEL:
            {
                loginL.setClickable ( true );
                //提示取消授权
                dismissProgress();
                ToastUtil.show(this, "微信授权被取消");
                startActivity(new Intent(LoginActivity.this, UserRegActivity.class));

//                noticePop = new NoticePopWindow ( LoginActivity.this, LoginActivity.this, wManager, "微信授权被取消");
//                noticePop.showNotice ( );
//                noticePop.showAtLocation (
//                        findViewById ( R.id.loginL ),
//                        Gravity.CENTER, 0, 0
//                                         );

            }
            break;
            case Constant.MSG_USERID_FOUND:
            {
            	ToastUtil.show(this, "已经获取用户信息");


            }
            break;
            case Constant.MSG_LOGIN:
            {
                dismissProgress();
                BusinessStatic.getInstance().accountModel  = ( AccountModel ) msg.obj;
                userService.userReg(LoginActivity.this,
                        BusinessStatic.getInstance().accountModel.getAccountName(), null,
                        BusinessStatic.getInstance().accountModel.getAccountId(), "LOGINOAUTHOR",
                        BusinessStatic.getInstance().accountModel.getAccountUnionId(), BusinessStatic.getInstance().accountModel.getOpenid(),
                        BusinessStatic.getInstance().accountModel.getAccountIcon(), BusinessStatic.getInstance().accountModel.getNickname());
                //AuthParamUtils paramUtils = new AuthParamUtils ( application, System.currentTimeMillis (), "", LoginActivity.this );
                //paramUtils.obtainParams ( BusinessStatic.getInstance().accountModel );

                //userLogin(1);


            }
            break;
            case Constant.MSG_USERID_NO_FOUND:
            {
                dismissProgress();
                //提示授权成功
                ToastUtil.show(this, "获取用户信息失败");
//                noticePop = new NoticePopWindow ( LoginActivity.this, LoginActivity.this, wManager, "获取用户信息失败");
//                noticePop.showNotice ();
//                noticePop.showAtLocation (
//                        findViewById ( R.id.loginL ),
//                        Gravity.CENTER, 0, 0
//                                         );
            }
            break;
         case Constant.INIT_MENU_ERROR:
            {
                dismissProgress();
            ToastUtil.show(this, "获取用户信息失败");
//                //提示初始化菜单失败
//                noticePop = new NoticePopWindow ( LoginActivity.this, LoginActivity.this, wManager, "初始化菜单失败");
//                noticePop.showNotice ();
//                noticePop.showAtLocation (
//                        findViewById ( R.id.loginL ),
//                        Gravity.CENTER, 0, 0
//                                         );
         }
           break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {

            MainApplication.finshApp();
        }
        return true;
        }


    @Override
    public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
        super.onDataFinish(type, des, datas, extra);

        if(type == BusinessDataListener.DONE_USER_LOGIN){
            Intent intentReg = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intentReg);
            finish();
        }
        else if( type == BusinessDataListener.DONE_USER_REG){
            //popreg.hide();
            String loginCode= SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD);
            String username= SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME);

            userService.userLogin(this, username, loginCode, true);

        }
    }

    @Override
    public void onDataFail(int type, String des, Bundle extra) {
        if(type == BusinessDataListener.NOT_USER_REG){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    userLogin(1);
                }
            });
        }
    }




}
