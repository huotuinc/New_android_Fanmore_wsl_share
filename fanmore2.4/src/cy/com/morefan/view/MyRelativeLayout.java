package cy.com.morefan.view;

import cy.com.morefan.util.L;
import cy.com.morefan.view.DragLayout.Status;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyRelativeLayout extends LinearLayout {
    private DragLayout dl;

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
	public MyRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDragLayout(DragLayout dl) {
        this.dl = dl;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
//    	return true;
        if (dl.getStatus() != Status.Close) {
            return true;
        }
//    	if(dl.getStatus() == Status.Open || dl.isDragging())
//    		return true;
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dl.getStatus() != Status.Close) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                dl.close();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

}
