package cy.com.morefan.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

public class TCardView extends CardView {

    private float touchX = 0;
    private int mTouchSlop = 0;


    public TCardView(@NonNull Context context) {
        super(context);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public TCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public TCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return super.onInterceptTouchEvent(ev);
        Log.d("TCardView" , ev.toString() );
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
    }
}
