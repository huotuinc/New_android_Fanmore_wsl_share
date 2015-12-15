package cy.com.morefan.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.SPUtil;
import cy.lib.edittext.CyEditText;

/**
 * Created by Administrator on 2015/12/3.
 */
public class PopReg extends BaseActivity implements  View.OnClickListener {


    public interface OnPopRegListener{
        void onLogin(String userName, String pwd);
        void onReg(int logintype,String invitationCode,String phone,String code);
        void onForget();


    }
    private OnPopRegListener listener;
    private View mainView;
    private Context mContext;
    private CyEditText edtUserReg;
    private Button btnReg;
    private  int logintype;
    private String phone;
    private String code;

    public PopReg(Context context , int logintype , String phone , String code ){
        this.mContext = context;
        this.logintype = logintype;
        this.phone = phone;
        this.code = code;
        initView(context);
    }
    private Dialog dialog;
    private void initView(Context context) {
        if(dialog == null){
            mainView = LayoutInflater.from(context).inflate(R.layout.pop_userreg, null);
            dialog = new Dialog(context, R.style.PopDialog);
            dialog.setContentView(mainView);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.TOP);  //此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.AnimationPopTopIn);  //添加动画
            dialog.setCanceledOnTouchOutside(false);



            //设置视图充满屏幕宽度
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int)(DensityUtil.getSize(mContext)[0]); //设置宽度
            lp.height= (int)(DensityUtil.getSize(mContext)[1]);//设置高度
            window.setAttributes(lp);
        }
        edtUserReg= (CyEditText) mainView.findViewById(R.id.edtUserReg);
        edtUserReg.setText("WSL0LOVE");
        btnReg= (Button) mainView.findViewById(R.id.btnReg);

        mainView.findViewById(R.id.btnReg).setOnClickListener(this);

    }

    private String tempUserName;
    public void show(Context context, OnPopRegListener listener){
        this.listener = listener;

        edtUserReg.setText("");
        dialog.show();
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 500);
    }
    private Handler handler = new Handler();
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnReg:
                userReg();


                break;
            default:

                break;
        }


    }
    public void userReg(){

        if(listener == null)
            return;
        if (TextUtils.isEmpty(edtUserReg.getText().toString())){
            edtUserReg.setError("请输入邀请码");
        }
        else {
            //progress.showProgress();

            listener.onReg(logintype ,edtUserReg.getText().toString(),phone,code );
            //listener.onReg(2,edtUserReg.getText().toString(),usephone,usecode);


        }



        // listener.onLogin();
    }
    public void hide()
    {
        if( dialog != null){
            dialog.dismiss();
        }
    }
}


