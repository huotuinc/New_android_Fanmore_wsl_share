package cy.com.morefan.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import cy.com.morefan.R;
import cy.com.morefan.util.DensityUtil;

public class SexView {
    private Dialog dialog;
    private Context mContext;
    private View mainView;
    public enum SelectType{
        man, woman
    }
    public interface OnSexSelectBackListener{
        void onSexSelectBack( SelectType type);
    }
    private OnSexSelectBackListener listener;
    public void setOnSexSelectBackListener(OnSexSelectBackListener listener){
        this.listener = listener;
    }

    public SexView(Context context ,OnSexSelectBackListener listener) {
        this.mContext = context;

        initView(context);
        this.listener= listener;
    }

    private void initView(final Context context) {
        if(dialog == null){
            mainView = LayoutInflater.from(context).inflate(R.layout.layout_sex , null);
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
                        listener.onSexSelectBack(null);
                    dialog.dismiss();
                }
                return false;
            }
        });
        mainView.findViewById(R.id.tvMan).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onSexSelectBack(SelectType.man);
                dialog.dismiss();
            }
        });
        mainView.findViewById(R.id.tvWoman).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onSexSelectBack(SelectType.woman);
                dialog.dismiss();
            }
        });
        mainView.findViewById(R.id.tvCancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener != null)
                    listener.onSexSelectBack(null);
                dialog.dismiss();
            }
        });

        mainView.findViewById(R.id.tvMan).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN || event.getAction()==MotionEvent.ACTION_MOVE){
                    mainView.findViewById(R.id.line).setBackgroundResource(R.color.bg2);
                }else{
                    mainView.findViewById(R.id.line).setBackgroundResource(R.color.dialog_press_bg);
                }
                return false;
            }
        });
        mainView.findViewById(R.id.tvWoman).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE){
                    mainView.findViewById(R.id.line).setBackgroundResource(R.color.bg2);
                }else{
                    mainView.findViewById(R.id.line).setBackgroundResource(R.color.dialog_press_bg);
                }
                return false;
            }
        });

    }

    public void show(){
        dialog.show();
    }

}
