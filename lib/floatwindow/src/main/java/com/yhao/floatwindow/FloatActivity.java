package com.yhao.floatwindow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于在内部自动申请权限
 * https://github.com/yhaolpz
 */

public class FloatActivity extends AppCompatActivity {

    //756232212

    private static List<PermissionListener> mPermissionListenerList;
    private static PermissionListener mPermissionListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestAlertWindowPermission();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 7212);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7212) {
            if (PermissionUtil.hasPermissionOnActivityResult(this)) {
                mPermissionListener.onSuccess();
            } else {
                mPermissionListener.onFail();
            }
        }
        finish();
    }


    static  void openTipDialog(final Context context , final PermissionListener permissionListener) {
//        dialog =
//
//        dialog.setTitle("申请权限");
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//
//
//        float density = context.getResources().getDisplayMetrics().density;
//        TextView tv = new TextView(context);
//        tv.setMovementMethod(new ScrollingMovementMethod());
//        tv.setVerticalScrollBarEnabled(true);
//        tv.setTextSize(14f);
//        //tv.set = (250 * density).toInt()
//
//        dialog.setView(tv, Float.floatToIntBits (25 * density),
//                Float.floatToIntBits(15 * density),
//                Float.floatToIntBits(25 * density), 0);
//
//
//        tv.setText("请允许App使用\"显示悬浮窗\"权限！");
//        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                goRequest(context, permissionListener);
//            }
//        });

        final TipAlertDialog dialog = new TipAlertDialog(context);

        dialog.show("申请权限","请允许App使用\\\"显示悬浮窗\\\"权限！","", false ,true ,null, new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                goRequest(context, permissionListener);
            }
        });
    }

    static void goRequest(Context context ,PermissionListener permissionListener ){
        if (mPermissionListenerList == null) {
            mPermissionListenerList = new ArrayList<>();
            mPermissionListener = new PermissionListener() {
                @Override
                public void onSuccess() {
                    for (PermissionListener listener : mPermissionListenerList) {
                        listener.onSuccess();
                    }
                    mPermissionListenerList.clear();
                }

                @Override
                public void onFail() {
                    for (PermissionListener listener : mPermissionListenerList) {
                        listener.onFail();
                    }
                    mPermissionListenerList.clear();
                }
            };
            Intent intent = new Intent(context, FloatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        mPermissionListenerList.add(permissionListener);
    }

    static synchronized void request(Context context, PermissionListener permissionListener) {
        if (PermissionUtil.hasPermission(context)) {
            permissionListener.onSuccess();
            return;
        }


        if (mPermissionListenerList == null) {
            mPermissionListenerList = new ArrayList<>();
            mPermissionListener = new PermissionListener() {
                @Override
                public void onSuccess() {
                    for (PermissionListener listener : mPermissionListenerList) {
                        listener.onSuccess();
                    }
                    mPermissionListenerList.clear();
                }

                @Override
                public void onFail() {
                    for (PermissionListener listener : mPermissionListenerList) {
                        listener.onFail();
                    }
                    mPermissionListenerList.clear();
                }
            };
            Intent intent = new Intent(context, FloatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        mPermissionListenerList.add(permissionListener);
    }


}
