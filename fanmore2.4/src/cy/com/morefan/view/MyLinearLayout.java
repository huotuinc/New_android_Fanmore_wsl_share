package cy.com.morefan.view;

import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout{
	private int[] size;
	public MyLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		size = DensityUtil.getSize(context);
	}
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		ViewGroup.LayoutParams params = getLayoutParams();
		int ownHeight = getHeight();
		L.i(">>>>>>" + ownHeight + "," + size[1]);
	}

//	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		// TODO Auto-generated constructor stub
//	}
//	public boolean isOpened;
//	public void setIsOpend(boolean isOpened){
//		this.isOpened = isOpened;
//	}
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return isOpened ? true : super.onInterceptTouchEvent(ev);
//	}



}
