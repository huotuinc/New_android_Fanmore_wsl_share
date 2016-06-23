package cy.com.morefan.view;

import cy.com.morefan.R;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.SecurityUtil;
import cy.com.morefan.util.ToastUtil;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class NinePointLineView extends View {

	Paint linePaint = new Paint();
	Paint whiteLinePaint = new Paint();
	Paint textPaint = new Paint();
	// 由于两个图片都是正方形，所以获取一个长度就行了
	Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.lock);
	int defaultBitmapRadius = defaultBitmap.getWidth() / 2;
	// 初始化被选中图片的直径、半径
	Bitmap selectedBitmap = BitmapFactory.decodeResource(getResources(),
			R.drawable.indicator_lock_area);
	int selectedBitmapDiameter = selectedBitmap.getWidth();
	int selectedBitmapRadius = selectedBitmapDiameter / 2;
	// 定义好9个点的数组
	PointInfo[] points = new PointInfo[9];
	// 相应ACTION_DOWN的那个点
	PointInfo startPoint = null;
	// 屏幕的宽高
	int width, height;
	// 当ACTION_MOVE时获取的X，Y坐标
	int moveX, moveY;
	// 是否发生ACTION_UP
	boolean isUp = false;
	// 最终生成的用户锁序列
	StringBuffer lockString = new StringBuffer();
	//操作类型，生成密码/密码验证
	public enum NiePointActionType{
		Creat, Auth, Modify
	}
	private NiePointActionType currentType;
	//待匹配密码
	private String authKey;
	//匹配次数
	private int tryTime;
	//最大匹配次数
	private final int MAX_TRY_TIME = 5;
	//新密码
	private String tmpNewKey;

	public interface OnSecretFinishListener{
		void OnSecretFinish(NiePointActionType type, String key);
		void OnSecretAuthFail();
		void onClick();
	}
	private OnSecretFinishListener listener;
	public void setOnSecretFinishListener(OnSecretFinishListener listener){
		this.listener = listener;
	}

	public NinePointLineView(Context context) {
		super(context);
		init(context);
	}


	public NinePointLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	private Context mContext;
	private int textSize;
	private int paddingBottom;
	private void init(Context context) {
		this.mContext = context;
		//this.setBackgroundColor(Color.WHITE);
		currentType = NiePointActionType.Creat;

		textSize = dip2px(context, 18);
		paddingBottom = dip2px(context, 100);
		initPaint();

		int[] size = DensityUtil.getSize(context);
		//set default value
		width = size[0];
		height = (int) (size[1] * 0.85);
		initPoints(points);

	}
	public void setActionType(NiePointActionType type){
		this.currentType = type;
		switch (type) {
		case Creat:
			msg = "划动手势，进行密码设置";

			break;
		case Auth:
			msg = "请输入密码";
			break;
		case Modify:
			msg = "请输入旧密码";
			break;

		default:
			break;
		}
	}

	/**
	 * 设置待匹配密码
	 * @param key
	 */
	public void setAuthKey(String key){
		this.authKey = key;
		//currentType = NiePointActionType.Auth;

	}
	/**
	 * 重新生成密码
	 */
	public void reCreate(){
		this.tmpNewKey = null;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//MLog.i("onMeasure");
		// 初始化屏幕大小
		System.out.println(">>>>>>onMeasure");
		int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();
		if (mWidth != 0 && mHeight != 0) {
			width = mWidth;
			height = mHeight;
			initPoints(points);
		}
		//MLog.i("width、height = " + width + "、" + height);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		//MLog.i("onLayout");
		super.onLayout(changed, left, top, right, bottom);
	}


	private String msg;
	private int startX = 0, startY = 0;
	@Override
	protected void onDraw(Canvas canvas) {

		if(!TextUtils.isEmpty(msg))
			canvas.drawText(msg, dip2px(mContext, 10), dip2px(mContext, 30), textPaint);

		if (moveX != 0 && moveY != 0 && startX != 0 && startY != 0) {
			// 绘制当前活动的线段
			drawLine(canvas, startX, startY, moveX, moveY);
		}
		//canvas.drawText("忘记密码", width - textSize * 5 , height - dip2px(mContext, 20), textPaint);

		drawNinePoint(canvas);

		//canvas.drawCircle(x, y, 1, new Paint());
		super.onDraw(canvas);
	}
	Rect pwdRect ;

	// 记住，这个DOWN和MOVE、UP是成对的，如果没从UP释放，就不会再获得DOWN；
	// 而获得DOWN时，一定要确认消费该事件，否则MOVE和UP不会被这个View的onTouchEvent接收
	private int x,y;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		x = (int) event.getX();
		y = (int) event.getY();

		if (pwdRect.contains(x, y) && getSelectedCount() == 0) {
			if(event.getAction() == MotionEvent.ACTION_UP ){
				if(listener != null)
					listener.onClick();
			}
			return true;
		}



		boolean flag = true;

		if (isUp) {// 如果已滑完，重置每个点的属性和lockString

			finishDraw();

			// 当UP后，要返回false，把事件释放给系统，否则无法获得Down事件
			flag = false;

		} else {// 没滑完，则继续绘制

			if(null != event)
				handlingEvent(event);

			// 这里要返回true，代表该View消耗此事件，否则不会收到MOVE和UP事件
			flag = true;

		}
		return flag;
	}

	private void handlingEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			moveX = (int) event.getX();
			moveY = (int) event.getY();
			//MLog.i("onMove:" + moveX + "、" + moveY);
			for (PointInfo temp : points) {
				if (temp != null && temp.isInMyPlace(moveX, moveY) && temp.isNotSelected()) {
					temp.setSelected(true);
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					if(startPoint == null)
						startPoint = temp;
					int len = lockString.length();
					if (len != 0) {
						int preId = lockString.charAt(len - 1) - 48;
						System.out.println(">>>>>preId:" + preId);
						points[preId].setNextId(temp.getId());
					}
					lockString.append(temp.getId());
					break;
				}
			}

			invalidate(0, height - width - paddingBottom, width, height);
			break;

		case MotionEvent.ACTION_DOWN:
			startPoint = null;
			int downX = (int) event.getX();
			int downY = (int) event.getY();
			//MLog.i("onDown:" + downX + "、" + downY);
			for (PointInfo temp : points) {
				if (temp != null && temp.isInMyPlace(downX, downY)) {
					temp.setSelected(true);
					startPoint = temp;
					startX = temp.getCenterX();
					startY = temp.getCenterY();
					lockString.append(temp.getId());
					break;
				}
			}
			invalidate(0, height - width - paddingBottom, width, height);
			break;

		case MotionEvent.ACTION_UP:

			//MLog.i("onUp");
			startX = startY = moveX = moveY = 0;
			isUp = true;
			checkResult();
			finishDraw();
			invalidate();
			break;
		default:
			//MLog.i("收到其他事件！！");
			break;
		}
	}

	private int getSelectedCount(){
		int count = 0;
		for(PointInfo item : points){
			if(item.selected){
				count++;
			}
		}
		return count;
	}
	private void checkResult() {
		if(getSelectedCount() < 2)
			return;
		String currentKey = SecurityUtil.MD5Encryption(lockString.toString());
		switch (currentType) {
		case Creat://设置密码
			String currentTmpKey = lockString.toString();
			if(TextUtils.isEmpty(tmpNewKey)){
				tmpNewKey = currentTmpKey;//新建密码第一步
				msg = "再绘一次手势确认密码";
			}else{
				if(tmpNewKey.endsWith(currentTmpKey)){//新建密码第二步确认
					msg = "密码设置成功";
					//成功
					if(listener != null)
						listener.OnSecretFinish(currentType,tmpNewKey);
				}else{
					msg = "两次手势图案不图!请重绘";
				}
			}
			break;
		case Modify://修改密码

//			if(tryTime == MAX_TRY_TIME ){
//				//失败
//				if(listener != null)
//					listener.OnSecretAuthFail();
//			}
			if(authKey.equals(currentKey)){//验证通过
				//新建密码第一步
				reCreate();
				setActionType(NiePointActionType.Creat);

			}else{
				//tryTime ++;
				//msg = "原手势密码错误!";//String.format("密码错误!还可以再输入%d次", 5 - tryTime);


				tryTime ++;
				if(5 - tryTime < 1){
					if(listener != null)
						listener.OnSecretAuthFail();
					//logout
					ToastUtil.show(getContext(), "提现密码输入错误!帐号被注销，请重新登录!");
					MyBroadcastReceiver.sendBroadcast(getContext(), MyBroadcastReceiver.ACTION_USER_LOGOUT);


				}
				msg = String.format("原手势密码错误!还可以再输入%d次", 5 - tryTime);

			}

			break;
		case Auth://密码验证

			if(tryTime == MAX_TRY_TIME){
				//失败
				if(listener != null)
					listener.OnSecretAuthFail();
			}

			if(authKey.equals(currentKey)){
				//通过验证
				if(listener != null)
					listener.OnSecretFinish(NiePointActionType.Auth,null);
				msg = "通过验证";
			}else{

				tryTime ++;
				if(5 - tryTime < 1){
					if(listener != null)
						listener.OnSecretAuthFail();
					//logout
					MyBroadcastReceiver.sendBroadcast(getContext(), MyBroadcastReceiver.ACTION_USER_LOGOUT);
				}
				msg = String.format("密码错误!还可以再输入%d次", 5 - tryTime);
			}


			break;

		default:
			break;
		}

	}


	private void finishDraw() {
		for (PointInfo temp : points) {
			temp.setSelected(false);
			temp.setNextId(temp.getId());
		}
		lockString.delete(0, lockString.length());
		isUp = false;
		invalidate();
	}

	private void initPoints(PointInfo[] points) {

		 FontMetrics fm = textPaint.getFontMetrics();
		 int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));
		pwdRect = new Rect( width - textSize * 5, height - textHeight*2, width, height - textHeight);

		int len = points.length;

		//图间距
		int seletedSpacing = (width - selectedBitmapDiameter * 3) / 4;

		// 被选择图的左上角坐标
		int seletedX = seletedSpacing;
		int seletedY = height - width - paddingBottom;// + seletedSpacing;

		// 没被选时图片的左上角坐标
		int defaultX = seletedX + selectedBitmapRadius - defaultBitmapRadius;
		int defaultY = seletedY + selectedBitmapRadius - defaultBitmapRadius;

		// 绘制好每个点
		for (int i = 0; i < len; i++) {
			//行首
			if (i%3 == 0) {
				seletedX = seletedSpacing;
				seletedY += selectedBitmapDiameter + seletedSpacing;

				defaultX = seletedX + selectedBitmapRadius- defaultBitmapRadius;
				defaultY += selectedBitmapDiameter + seletedSpacing;

			}
			points[i] = new PointInfo(i, defaultX, defaultY, seletedX, seletedY);

			seletedX += selectedBitmapDiameter + seletedSpacing;
			defaultX += selectedBitmapDiameter + seletedSpacing;

		}
	}

	private void initPaint() {
		initLinePaint(linePaint);
		initTextPaint(textPaint);
		initWhiteLinePaint(whiteLinePaint);
	}

	/**
	 * 初始化文本画笔
	 * @param paint
	 */
	private void initTextPaint(Paint paint) {
		paint.setColor(getResources().getColor(R.color.white));
		textPaint.setTextSize(textSize);
		textPaint.setAntiAlias(true);
		textPaint.setTypeface(Typeface.MONOSPACE);
	}

	/**
	 * 初始化黑线画笔
	 *
	 * @param paint
	 */
	private void initLinePaint(Paint paint) {
		paint.setColor(0xff8effff);
		paint.setStrokeWidth(defaultBitmap.getWidth()/5);
		paint.setAntiAlias(true);
		paint.setStrokeCap(Cap.ROUND);
	}

	/**
	 * 初始化白线画笔
	 *
	 * @param paint
	 */
	private void initWhiteLinePaint(Paint paint) {
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(defaultBitmap.getWidth()/3 - 5);
		paint.setAntiAlias(true);
		paint.setStrokeCap(Cap.ROUND);

	}

	/**
	 * 绘制已完成的部分
	 *
	 * @param canvas
	 */
	private void drawNinePoint(Canvas canvas) {

		if (startPoint != null) {
			drawEachLine(canvas, startPoint);
		}

		// 绘制每个点的图片
		for (PointInfo pointInfo : points) {
			if(pointInfo == null)
				continue;
			if (pointInfo.isSelected()) {// 绘制大圈
				canvas.drawBitmap(selectedBitmap, pointInfo.getSeletedX(),
						pointInfo.getSeletedY(), null);
			}
			// 绘制点
			canvas.drawBitmap(defaultBitmap, pointInfo.getDefaultX(),
					pointInfo.getDefaultY(), null);
		}

	}

	/**
	 * 递归绘制每两个点之间的线段
	 *
	 * @param canvas
	 * @param point
	 */
	private void drawEachLine(Canvas canvas, PointInfo point) {
		if (point.hasNextId()) {
			int n = point.getNextId();
			drawLine(canvas, point.getCenterX(), point.getCenterY(),
					points[n].getCenterX(), points[n].getCenterY());
			// 递归
			drawEachLine(canvas, points[n]);
		}
	}

	/**
	 * 先绘制黑线，再在上面绘制白线，达到黑边白线的效果
	 *
	 * @param canvas
	 * @param startX
	 * @param startY
	 * @param stopX
	 * @param stopY
	 */
	private void drawLine(Canvas canvas, float startX, float startY,
			float stopX, float stopY) {
		canvas.drawLine(startX, startY, stopX, stopY, linePaint);
		//canvas.drawLine(startX, startY, stopX, stopY, whiteLinePaint);
	}

	/**
	 * 用来表示一个点
	 *
	 * @author zkwlx
	 *
	 */
	private class PointInfo {

		// 一个点的ID
		private int id;

		// 当前点所指向的下一个点的ID，当没有时为自己ID
		private int nextId;

		// 是否被选中
		private boolean selected;

		// 默认时图片的左上角X坐标
		private int defaultX;

		// 默认时图片的左上角Y坐标
		private int defaultY;

		// 被选中时图片的左上角X坐标
		private int seletedX;

		// 被选中时图片的左上角Y坐标
		private int seletedY;

		public PointInfo(int id, int defaultX, int defaultY, int seletedX,
				int seletedY) {
			this.id = id;
			this.nextId = id;
			this.defaultX = defaultX;
			this.defaultY = defaultY;
			this.seletedX = seletedX;
			this.seletedY = seletedY;
		}

		public boolean isSelected() {
			return selected;
		}

		public boolean isNotSelected() {
			return !isSelected();
		}

		public void setSelected(boolean selected) {
			this.selected = selected;
		}

		public int getId() {
			return id;
		}

		public int getDefaultX() {
			return defaultX;
		}

		public int getDefaultY() {
			return defaultY;
		}

		public int getSeletedX() {
			return seletedX;
		}

		public int getSeletedY() {
			return seletedY;
		}

		public int getCenterX() {
			return seletedX + selectedBitmapRadius;
		}

		public int getCenterY() {
			return seletedY + selectedBitmapRadius;
		}

		public boolean hasNextId() {
			return nextId != id;
		}

		public int getNextId() {
			return nextId;
		}

		public void setNextId(int nextId) {
			this.nextId = nextId;
		}

		/**
		 * 坐标(x,y)是否在当前点的范围内
		 *
		 * @param x
		 * @param y
		 * @return
		 */
		public boolean isInMyPlace(int x, int y) {
			boolean inX = x > seletedX
					&& x < (seletedX + selectedBitmapDiameter);
			boolean inY = y > seletedY
					&& y < (seletedY + selectedBitmapDiameter);

			return (inX && inY);
		}

	}
	/**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
