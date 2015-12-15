package cy.com.morefan.view;


import cy.com.morefan.R;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;


public class MyView extends TextView {
	public interface OnScratchListener{
		void onScratchBack(float percent);
	}

	private int width, height;

	private Context mContext;
	private Paint mPaint;
	private Canvas tempCanvas;
	private Bitmap mBitmap;
	private float x, y, ox, oy;
	private Path mPath;
	Handler mHandler;
	MyThread mThread;

	int messageCount;
	private OnScratchListener listener;

	int[] pixels;

	int color = 0xFFD6D6D6;

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;

		// 获取控件大小值
				TypedArray a = mContext.obtainStyledAttributes(attrs,
						R.styleable.lotter,0,0);
				width = (int) (DensityUtil.getSize(context)[0] * 0.8125);//(int) a.getDimension(R.styleable.lotter_width, 10);
				L.i(">>>width:" + width);
				height = (int) a.getDimension(R.styleable.lotter_height, 10);
				a.recycle();
		init();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(width, height);
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void setOnScratchListener(OnScratchListener listener){
		this.listener = listener;
	}

	private boolean hasBacked;
	/**
	 * 再一次抽奖
	 */
	public void againLotter(String value) {
		hasBacked = false;
		messageCount = 0;
		tempCanvas.drawColor(color);
		setText(value);
		if( null == textRect){
			Rect rect= new Rect();
			getPaint().getTextBounds(value, 0, 1, rect);
			L.i(">>>>rect:" + rect);
			int textWidth = rect.width();
			int textHeight = rect.height();
			textRect = new Rect();
			textRect.left = width/2 - textWidth/2;
			textRect.top = height/2 - textHeight/2;
			textRect.right = width/2 + textWidth/2;
			textRect.bottom = height/2 + textHeight/2;
			L.i(">>>>rect:" + textRect);
		}
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		//init();
	}

	private void init() {
//		widget = this.getMeasuredWidth();//(int) a.getDimension(R.styleable.lotter_widget, 300);
//		height = this.getMeasuredHeight();
		L.i(width + "," + height);

		// 初始化路径
		mPath = new Path();

		// 初始化画笔
		mPaint = new Paint();
		mPaint.setColor(0xffaaaaaa);
		mPaint.setAlpha(0);
		mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(50);//画笔宽度



		// 初始化Bitmap并且锁定到临时画布上
		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
		tempCanvas = new Canvas();
		tempCanvas.setBitmap(mBitmap);
		againLotter("再接再厉");

		// 在字线程中创建Handler接收像素消息
		mThread = new MyThread();
		mThread.start();
	}
	private Rect textRect;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 将处理过的bitmap画上去
		canvas.drawBitmap(mBitmap, 0, 0, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			touchDown(event);
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(event);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
	}

	/**
	 *  移动的时候
	 * @param event
	 */
	private void touchMove(MotionEvent event) {
		x = event.getX();
		y = event.getY();
		// 二次贝塞尔，实现平滑曲线；oX, oY为操作点 x,y为终点
		mPath.quadTo((x + ox) / 2, (y + oy) / 2, x, y);
		tempCanvas.drawPath(mPath, mPaint);
		ox = x;
		oy = y;
		invalidate();
		computeScale();
	}
	/**
	 * 第一次按下来
	 *
	 * @param event
	 */
	private void touchDown(MotionEvent event) {
		ox = x = event.getX();
		oy = y = event.getY();
		mPath.reset();
		mPath.moveTo(ox, oy);
	}
	/**
	 * 计算百分比
	 */
	private void computeScale() {
		Message msg = mHandler.obtainMessage(0);
		msg.obj = ++messageCount;
		mHandler.sendMessage(msg);
	}

	/**
	 * 异步线程，作用是创建handler接收处理消息。
	 * @author Administrator
	 *
	 */
	class MyThread extends Thread {

		public MyThread() {
		}

		@Override
		public void run() {
			super.run();
			/*
			 * 创建 handler前先初始化Looper.
			 */
			Looper.prepare();

			mHandler = new Handler() {
				@Override
				public void dispatchMessage(Message msg) {
					super.dispatchMessage(msg);
					// 只处理最后一次的百分比
					if ((Integer) (msg.obj) != messageCount) {
						return;
					}
					// 取出像素点
					synchronized (mBitmap) {
//						if (pixels == null) {
//							pixels = new int[mBitmap.getWidth()
//									* mBitmap.getHeight()];
//						}
//						mBitmap.getPixels(pixels, 0, width, 0, 0, width,
//								height);

					if(!hasBacked){
						int count = 0;
						for(int x = textRect.left; x < textRect.right; x ++){
							for(int y = textRect.top; y < textRect.bottom; y ++){
								if(mBitmap.getPixel(x, y) == 0)
									count++;
							}
						}
						int sum2 = textRect.width() * textRect.height();
						float percent = count / (float) sum2;
						L.i(">>>>percent:" + percent);
						if( null != listener && percent > 0.6 ){
							hasBacked = true;
							listener.onScratchBack(percent);
						}
					}
					}


//					int sum = pixels.length;
//					int num = 0;
//					for (int i = 0; i < sum; i++) {
//						if (pixels[i] == 0) {
//							num++;
//						}
//					}
//					info.setScratchPercentage(num / (double) sum);
//					System.out.println("百分比:" + info.getScratchPercentage()
//							* 100);
				}
			};
			/*
			 * 启动该线程的消息队列
			 */
			Looper.loop();
		}
	}
}
