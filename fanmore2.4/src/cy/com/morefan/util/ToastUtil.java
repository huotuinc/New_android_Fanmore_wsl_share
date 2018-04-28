package cy.com.morefan.util;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cy.com.morefan.MainApplication;
import cy.com.morefan.R;

public class ToastUtil {
	private static Toast mToast;
	private static Toast mCustomToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void show(Context mContext, String text) {

        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_LONG);
        mHandler.postDelayed(r, 1500);

        mToast.show();
    }

    public static void show(String text , int gravity){
        if (mCustomToast == null){
            mCustomToast = Toast.makeText(MainApplication.single, text, Toast.LENGTH_LONG);
            mCustomToast.setGravity(gravity , 0, 0);
            View view = LayoutInflater.from(MainApplication.single).inflate(R.layout.layout_toast , null);
            mCustomToast.setView(view);
            //mCustomToast.setMargin(10,10);
        }

        ((TextView)mCustomToast.getView().findViewById(R.id.toast_text)).setText(text);
        mCustomToast.show();
    }


    public static void show(String text , int gravity , int bg , int fontColor ){
        if (mCustomToast == null){
            mCustomToast = Toast.makeText(MainApplication.single, text, Toast.LENGTH_LONG);
            mCustomToast.setGravity(gravity , 0, 0);
            View view = LayoutInflater.from(MainApplication.single).inflate(R.layout.layout_toast , null);
            mCustomToast.setView(view);
            //mCustomToast.setMargin(10,10);
        }
        mCustomToast.getView().findViewById(R.id.toast_text).setBackgroundResource(bg);
        ((TextView) mCustomToast.getView().findViewById(R.id.toast_text)).setTextColor(fontColor);
        ((TextView)mCustomToast.getView().findViewById(R.id.toast_text)).setText(text);

        mCustomToast.show();
    }



//	private static Toast mToast;
//
//	public static void show(Context ctx, String text) {
//		if (mToast == null) {
//			mToast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
//		} else {
//			mToast.setText(text);
//		}
//		mToast.show();
//	}

}
