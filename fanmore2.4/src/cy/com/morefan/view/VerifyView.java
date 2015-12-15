package cy.com.morefan.view;

import java.util.Random;

import cy.com.morefan.util.DensityUtil;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VerifyView extends View{
	private Typeface iconfont ;
	private int result;
	private Random mRandom;
	private Paint mPaint;
	private int width = 300;
	private int height = 100;
	public VerifyView(Context context) {
		super(context);
		init(context);
	}

	public VerifyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public VerifyView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	private void init(Context context) {
		width = DensityUtil.dip2px(context, 250);
		height = DensityUtil.dip2px(context, 83);
		mRandom = new Random();
		mPaint = new Paint();
		iconfont = Typeface.createFromAsset(context.getAssets(), "Tiza.ttf");
		refresh();
	}
	public void refresh(){
		int max1 = random(1, 10) > 5 ? 10 : 100;
		int max2 = max1 == 10 ? 100 : 10;
		final int random1 = random(1, max1);
		final int random2 = random(1, max2);
		result = random1 + random2;
		setValue(random1, random2);
	}
	public int getResult(){
		return result;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		System.out.println(">>>touch:" + event.getAction());
		switch (event.getAction()) {
		case  MotionEvent.ACTION_UP:
			System.out.println(">>>up");
			refresh();
			break;
		}
		return true;
	}
	private void setValue(int a, int b){
		res = new String[4];
		res[0] = String.valueOf(a);
		res[1] = "+";
		res[2] = String.valueOf(b);
		res[3] = "=";
		int aCount = a/10 > 0 ? 2 : 1;
		int bCount = b/10 > 0 ? 2 : 1;
		codeCount = aCount + bCount + 2;
		invalidate();
	}
	private String[] res;
	private int codeCount;
	private int lineCount = 100;
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int red = 0, green = 0, blue = 0;
		int perWidth = 0,fontHeight=0,codeY=0;
		perWidth = width / (codeCount +2);//每个字符的宽度
	        fontHeight = height - 2;//字体的高度
	        codeY = height - 14;




		 for (int i = 0; i < lineCount; i++) {
	            int xs = mRandom.nextInt(width);
	            int ys = mRandom.nextInt(height);
	            int xe = xs+mRandom.nextInt(width/8);
	            int ye = ys+mRandom.nextInt(height/8);
	            red = mRandom.nextInt(255);
	            green = mRandom.nextInt(255);
	            blue = mRandom.nextInt(255);
	            //g.setColor(new Color(red, green, blue));
	            mPaint.setARGB(255, red, green, blue);
	           // g.drawLine(xs, ys, xe, ye);
	            canvas.drawLine(xs, ys, xe, ye, mPaint);
	        }

		 mPaint.setTypeface(iconfont);
		 mPaint.setTextSize(fontHeight-20);
		 int count = 0;
		 for (int i = 0; i < res.length; i++) {
	           // String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
	            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
	            red = mRandom.nextInt(255);
	            green = mRandom.nextInt(255);
	            blue = mRandom.nextInt(255);
	            mPaint.setARGB(255, red, green, blue);


	            int rotate = mRandom.nextInt() > 5 ? mRandom.nextInt(60) : -mRandom.nextInt(60);
	            rotate = i == 1 ? mRandom.nextInt(10) : rotate;
	            canvas.save();
	            canvas.rotate(rotate, (float) ((count + 1 + 1.0*res[i].length()/2) * perWidth), height/2);
	           // canvas.rotate(i * 10);
	            canvas.drawText( res[i], (count + 1) * perWidth, codeY, mPaint);
	            canvas.restore();
	            count += res[i].length();
//	            g.drawString(strRand, (i + 1) * x, codeY);
//	            // 将产生的四个随机数组合在一起。
//	            randomCode.append(strRand);
	        }
	}
	/**
	 * 返回指定范围的随机数(m-n之间)的公式[1] ：Math.random()*(n-m)+m；
	 */
	public static int random(int m, int n){
		return (int) (Math.random()*(n-m)+m);
	}


}
