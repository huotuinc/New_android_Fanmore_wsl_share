package com.yhao.floatwindow;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;


/**
 * Created by jinxiangdong on 2017/12/24.
 */

public class BaseDialog implements
        DialogInterface.OnKeyListener
        ,DialogInterface.OnDismissListener{

    Dialog dialog;
    Context context;
    protected int screenWidthPixels;
    protected int screenHeightPixels;
    private FrameLayout contentLayout;
    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    public BaseDialog(Context context ) {

        this.context = context;
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidthPixels = metrics.widthPixels;
        screenHeightPixels = metrics.heightPixels;

        initDialog();

    }


    private void initDialog() {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.layout_operate_dialog , null);
//        view.setFocusable(true);
//        view.setFocusableInTouchMode(true);
//        this.contentLayout = view;
        contentLayout = new FrameLayout(context);
        contentLayout.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        contentLayout.setFocusable(true);
        contentLayout.setFocusableInTouchMode(true);
        dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(true);//触摸屏幕取消窗体
        dialog.setCancelable(true);//按返回键取消窗体
        dialog.setOnKeyListener(this);
        dialog.setOnDismissListener(this);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //AndroidRuntimeException: requestFeature() must be called before adding content
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setContentView(contentLayout);
        }

        setSize(screenWidthPixels, WRAP_CONTENT);


//        RecyclerView recyclerView=view.findViewById(R.id.operate_dialog_list);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        operateAdapter=new OperateDialog.OperateAdapter(list);
//        operateAdapter.setOnItemClickListener(this);
//        recyclerView.setAdapter(operateAdapter);
    }


    public void show(){
        dialog.show();
    }

    protected void addContentView(View view){
        this.contentLayout.addView( view );
    }


    /**
     * 设置弹窗的宽和高
     *
     * @param width  宽
     * @param height 高
     */
    public void setSize(int width, int height) {
        if (width == MATCH_PARENT) {
            //360奇酷等手机对话框MATCH_PARENT时两边还会有边距，故强制填充屏幕宽
            width = screenWidthPixels;
        }
        if (width == 0 && height == 0) {
            width = screenWidthPixels;
            height = WRAP_CONTENT;
        } else if (width == 0) {
            width = screenWidthPixels;
        } else if (height == 0) {
            height = WRAP_CONTENT;
        }
        //LogUtils.verbose(this, String.format("will set popup width/height to: %s/%s", width, height));
        ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, height);
        } else {
            params.width = width;
            params.height = height;
        }
        contentLayout.setLayoutParams(params);
    }



    @Override
    public void onDismiss(DialogInterface dialog) {
        dismiss();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPress();
        }
        return false;
    }

    public boolean onBackPress() {
        dismiss();
        return false;
    }

    public void dismiss() {
        dismissImmediately();
    }

    protected final void dismissImmediately() {
        dialog.dismiss();
        //Log.d(this, "popup dismiss");
    }
}
