package cy.com.morefan.guideview;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Scroller;

public class MyScrollView extends ScrollView {
	// 用于滑动的类
		private Scroller mScroller;
	public MyScrollView(Context context) {
		this(context, null, 0);
	}
	public MyScrollView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public MyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		 mScroller = new Scroller(context);
		 isInEditMode();
		
		
	}
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
	//	System.out.println(">>>>>computeScroll");
		Iterator<AnimationView> iterator = animationViews.values().iterator();
		while(iterator.hasNext()){
			AnimationView item = iterator.next();
			item.check();}
		super.computeScroll();
	}
	private ConcurrentHashMap<AnimationView, AnimationView> animationViews = new ConcurrentHashMap<AnimationView, AnimationView>();
	//private AnimationView animationView;
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if(changed)
			checkAnimationView(getChildAt(0));
	}
	private void checkAnimationView(View view) {
		if(view instanceof ViewGroup){
			int childCount = ((ViewGroup) view).getChildCount();
			for(int i = 0; i < childCount; i++){
				View item = ((ViewGroup) view).getChildAt(i);
				if(item instanceof AnimationView){
					animationViews.put((AnimationView) item, (AnimationView) item);
					
				}else if(item instanceof ViewGroup){
					checkAnimationView(item);
				}
				
			}
		}
		
		
	}
	public void setAnimationView(){
		
	}
	
	
	
	
	
	

}
