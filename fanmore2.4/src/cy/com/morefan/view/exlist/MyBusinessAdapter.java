package cy.com.morefan.view.exlist;

import java.util.BitSet;

import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class MyBusinessAdapter extends MyBaseAdapter{
	/**
	 * Reference to the last expanded list item.
	 * Since lists are recycled this might be null if
	 * though there is an expanded list item
	 */
	private View lastOpen = null;
	/**
	 * The position of the last expanded list item.
	 * If -1 there is no list item expanded.
	 * Otherwise it points to the position of the last expanded list item
	 */
	private int lastOpenPosition = -1;
	
	/**
	 * Default Animation duration
	 * Set animation duration with @see setAnimationDuration
	 */
	private int animationDuration = 330;
	
	/**
	 * A list of positions of all list items that are expanded.
	 * Normally only one is expanded. But a mode to expand
	 * multiple will be added soon.
	 *
	 * If an item onj position x is open, its bit is set
	 */
	private BitSet openItems = new BitSet();
	/**
	 * We remember, for each collapsable view its height.
	 * So we dont need to recalculate.
	 * The height is calculated just before the view is drawn.
	 */
	private final SparseIntArray viewHeights = new SparseIntArray(10);

	public MyBusinessAdapter(ListAdapter adapter) {
		super(adapter);
	}
	protected abstract View getExpendView(View parent);
	protected abstract View getExpendButtonView(View parent);
	protected abstract ListView getListView();
	
	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		view = wrapped.getView(position, view, viewGroup);
		enableFor(view, position);
		return view;
	}
	public void enableFor(View parent, int position) {
		View more = getExpendButtonView(parent);
		View itemToolbar = getExpendView(parent);
		if(itemToolbar != null && more != null){
			itemToolbar.measure(parent.getWidth(), parent.getHeight());
			enableFor(more, itemToolbar, position);
		}
		
	}
	private void enableFor(View button, final View target, final int position) {

		if(target == lastOpen && position!=lastOpenPosition) {
			// lastOpen is recycled, so its reference is false
			lastOpen = null;
		}
		if(position == lastOpenPosition) {
			// re reference to the last view
			// so when can animate it when collapsed
			lastOpen = target;
		}
		
		updateExpandable(target,position);
		boolean isClickable = true;
		if(button.getTag() != null){
			System.out.println(button.getTag());
			
			isClickable =  Boolean.valueOf(button.getTag().toString());//(Boolean) button.getTag();
		}
			
		if(isClickable)
			button.setOnClickListener(new MyListener(target,position));
	
		
	}
	class MyListener implements View.OnClickListener{
		private View target;
		private int position;
		public MyListener(View target, int position){
			this.target = target;
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			Animation a = target.getAnimation();

			if (a != null && a.hasStarted() && !a.hasEnded()) {
				
			}else{
				target.clearAnimation();

				int type = target.getVisibility() == View.VISIBLE
						? ExpandCollapseAnimation.COLLAPSE
						: ExpandCollapseAnimation.EXPAND;

				// remember the state
				if (type == ExpandCollapseAnimation.EXPAND) {
					openItems.set(position, true);
				} else {
					openItems.set(position, false);
				}
				// check if we need to collapse a different view
				if (type == ExpandCollapseAnimation.EXPAND) {
					if (lastOpenPosition != -1 && lastOpenPosition != position) {
						if (lastOpen != null) {
							animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE);
						}
						openItems.set(lastOpenPosition, false);
					}
					lastOpen = target;
					lastOpenPosition = position;
				} else if (lastOpenPosition == position) {
					lastOpenPosition = -1;
				}
				animateView(target, type);
			}
			Animation animation = target.getAnimation();
			if(animation != null)
				animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					if(position == getCount() - 1){
						//getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);  
						getListView().setSelection(getCount());
						
					}
				}
			});
			
			
		}
	}
	/**
	 * Performs either COLLAPSE or EXPAND animation on the target view
	 * @param target the view to animate
	 * @param type the animation type, either ExpandCollapseAnimation.COLLAPSE
	 *			 or ExpandCollapseAnimation.EXPAND
	 */
	private void animateView(final View target, final int type) {
		Animation anim = new ExpandCollapseAnimation(
				target,
				type
		);
		anim.setDuration(animationDuration);
		target.startAnimation(anim);
	}
	private void updateExpandable(View target, int position) {
		final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)target.getLayoutParams();
		int height = viewHeights.get(position, -1);
		if(height == -1) {
			viewHeights.put(position, target.getMeasuredHeight());
		} 

		if(openItems.get(position)) {
			target.setVisibility(View.VISIBLE);
			params.bottomMargin = 0;
		} else {
			target.setVisibility(View.GONE);
			params.bottomMargin = 0-viewHeights.get(position);
		}
	}
	
	/**
	 * Closes the current open item.
	 * If it is current visible it will be closed with an animation.
	 *
	 * @return true if an item was closed, false otherwise
	 */
	public boolean collapseLastOpen() {
		if(isAnyItemExpanded()) {
			// if visible animate it out
			if(lastOpen != null) {
				animateView(lastOpen, ExpandCollapseAnimation.COLLAPSE);
			}
			openItems.set(lastOpenPosition, false);
			lastOpenPosition = -1;
			return true;
		}
		return false;
	}
	public boolean isAnyItemExpanded() {
		return (lastOpenPosition != -1) ? true : false;
	}

}
