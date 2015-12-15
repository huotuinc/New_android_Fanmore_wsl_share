package cy.com.morefan.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
@SuppressWarnings("deprecation")
public class SelectorUtil  {






	/**
	 * 传入改变亮度前的bitmap，返回改变亮度后的bitmap
	 *
	 * @param srcBitmap
	 * @return
	 */
	private static Drawable changeBrightnessBitmap(Bitmap srcBitmap) {
		Bitmap bmp = Bitmap.createBitmap(srcBitmap.getWidth(),
				srcBitmap.getHeight(), Config.ARGB_8888);
		int brightness = 60 - 127;
		ColorMatrix cMatrix = new ColorMatrix();
		cMatrix.set(new float[] {
				1, 0, 0, 0, brightness,
				0, 1, 0, 0,brightness,// 改变亮度
				0, 0, 1, 0, brightness,
				0, 0, 0, 1, 0 });
		Paint paint = new Paint();
		paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
		Canvas canvas = new Canvas(bmp);
		// 在Canvas上绘制一个Bitmap
		canvas.drawBitmap(srcBitmap, 0, 0, paint);
		return new BitmapDrawable(bmp);
	}

	public static StateListDrawable getCilckSelector(Drawable drawable){
		StateListDrawable bg = new StateListDrawable();
		Drawable normal = drawable;
		Drawable pressed = changeBrightnessBitmap(((BitmapDrawable) drawable)
				.getBitmap());
		;
		// View.PRESSED_ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled }, pressed);
		// View.ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		// View.EMPTY_STATE_SET
		bg.addState(new int[] {}, normal);
		return bg;

	}
}
