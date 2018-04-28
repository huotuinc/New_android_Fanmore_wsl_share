package cy.com.morefan.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cy.com.morefan.R;
import cy.com.morefan.util.DensityUtil;

public class OkCancelView {
    private Dialog dialog;
    private Context mContext;
    private View mainView;
    public enum OkCancel{
        ok, cancel
    }
    public interface OnOkCancelListener{
        void onOkCancel( OkCancel type);
    }
    private OnOkCancelListener listener;


    public OkCancelView(Context context , OnOkCancelListener listener) {
        this.mContext = context;

        initView(context);
        this.listener= listener;
    }

    private void initView(final Context context) {
        if(dialog == null){
            mainView = LayoutInflater.from(context).inflate(R.layout.layout_okcancel , null);
            dialog = new Dialog(context, R.style.PopDialog);
            dialog.setContentView(mainView);
            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
            window.setWindowAnimations(R.style.AnimationPop);  //添加动画

            //设置视图充满屏幕宽度
            WindowManager.LayoutParams lp = window.getAttributes();
            int[] size  = DensityUtil.getSize(mContext);
            lp.width = size[0]; //设置宽度
            // lp.height = (int) (size[1]*0.8);
            window.setAttributes(lp);
        }


        mainView.setFocusableInTouchMode(true);
        mainView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK ){
                    if(listener != null)
                        listener.onOkCancel(OkCancel.cancel);
                    dialog.dismiss();
                }
                return false;
            }
        });
        mainView.findViewById(R.id.tvOk).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onOkCancel(OkCancel.ok);
                dialog.dismiss();
            }
        });
        mainView.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onOkCancel( OkCancel.cancel);
                dialog.dismiss();
            }
        });


    }

    public void show(){
        dialog.show();
    }

}
