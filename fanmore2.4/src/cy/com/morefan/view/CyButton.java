package cy.com.morefan.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CyButton extends TextView {

	public CyButton(Context context) {
		super(context);
		init();
	}

	public CyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public void setBackgroundResource(int resid) {
		super.setBackgroundResource(resid);
		init();
	}
	@SuppressWarnings("deprecation")
	private void init() {
		setClickable(true);
		setGravity(Gravity.CENTER);
		setBackgroundDrawable(newSelector());
	}

	/**
	 * 传入改变亮度前的bitmap，返回改变亮度后的bitmap
	 *
	 * @param srcBitmap
	 * @return
	 */
	private Drawable changeBrightnessBitmap(Bitmap srcBitmap) {
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

//	private Bitmap makeRadGrad(GradientDrawable drawable) {
//	    Canvas canvas = new Canvas();
//	    drawable.draw(canvas);
//
//	    Bitmap bitmap = Bitmap.createBitmap(30, 30, Config.ARGB_8888);
//	    Paint paint = new Paint();
//	    canvas.drawBitmap(bitmap, 0, 0, paint);
//
//	    return bitmap;
//	}

	/** 设置Selector。 */
	private StateListDrawable newSelector() {
		StateListDrawable bg = new StateListDrawable();

		Drawable normal = getBackground();
		Drawable pressed = changeBrightnessBitmap(((BitmapDrawable) getBackground()).getBitmap());
//		if(normal instanceof GradientDrawable){
//			pressed = changeBrightnessBitmap(makeRadGrad((GradientDrawable)normal));
//		}else{
//			 pressed = changeBrightnessBitmap(((BitmapDrawable) getBackground()).getBitmap());
//		}
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
