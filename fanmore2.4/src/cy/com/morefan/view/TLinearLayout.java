package cy.com.morefan.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class TLinearLayout extends LinearLayout {
    private float touchX = 0;
    private int mTouchSlop = 0;

    public TLinearLayout(Context context) {
        super(context);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public TLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

    }

    public TLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d("TLinearLayout" , ev.toString() );
        boolean isScroll = true;
        switch (ev.getAction() ){
            case MotionEvent.ACTION_DOWN:
                isScroll=false;
                touchX = ev.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs( ev.getX() - touchX ) >= mTouchSlop){
                    isScroll=true;
                }else{
                    isScroll=false;
                }

                break;
            case MotionEvent.ACTION_UP:
                isScroll=false;
                break;
        }

        return isScroll;
        //return super.onInterceptTouchEvent(ev);
    }
}
