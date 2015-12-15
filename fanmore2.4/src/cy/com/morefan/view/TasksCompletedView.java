package cy.com.morefan.view;

import cy.com.morefan.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;



public class TasksCompletedView extends View {

	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	// 画字体的画笔
	private Paint mTextPaint;
	// 画字体的画笔
	private Paint mTextDesPaint;
	// 圆形颜色
	private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 字的长度
	private float mTxtWidth;
	// 字的高度
	private float mTxtHeight;
	// 字的长度
	private float mTxtDesWidth;
		// 字的高度
	private float mTxtDesHeight;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	private String des;

	public TasksCompletedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		initAttrs(context, attrs);
		initVariable();
		//getBackground().setAlpha(0);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, 100);
		mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 20);
		mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0xff323a45);
		mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0xFF14b9d6);
		measure(240, 240);
		
		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	private void initVariable() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.FILL);
		
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth);
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setARGB(255, 255, 255, 255);
		mTextPaint.setTextSize(mRadius / 2);
		
		
		mTextDesPaint = new Paint();
		mTextDesPaint.setAntiAlias(true);
		mTextDesPaint.setStyle(Paint.Style.FILL);
		mTextDesPaint.setARGB(255, 255, 255, 255);
		mTextDesPaint.setTextSize(mRadius / 7);
		
		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
		
		FontMetrics fmDes = mTextDesPaint.getFontMetrics();
		mTxtDesHeight = (int) Math.ceil(fmDes.descent - fmDes.ascent);
		
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;
		
		canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);
		
		if (mProgress > 0 ) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint); //
//			canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);
			String txt = mProgress + "%";
			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
			canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);
		}
		if(!TextUtils.isEmpty(des)){
			mTxtDesWidth = mTextDesPaint.measureText(des, 0, des.length());
			canvas.drawText(des, mXCenter - mTxtDesWidth / 2, mYCenter + mTxtDesHeight / 4 + mTxtDesHeight*2, mTextDesPaint);
		}
	}
	
	public void setProgress(int progress, String des) {
		System.out.println(">>>>>"+des);
		mProgress = progress;
		this.des = des;
//		invalidate();
		postInvalidate();
	}

}
