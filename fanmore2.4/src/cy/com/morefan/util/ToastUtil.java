package cy.com.morefan.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class ToastUtil {
	private static Toast mToast;
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
