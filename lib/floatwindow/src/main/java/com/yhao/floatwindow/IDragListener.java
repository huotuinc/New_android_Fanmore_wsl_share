package com.yhao.floatwindow;

import android.view.MotionEvent;

public interface IDragListener {
    void startDrag(MotionEvent event);
    void draging(MotionEvent event);
    void endDrag(MotionEvent event);
}
