package cy.com.morefan.view.exlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * 需实现setOnExpendLister方法
 * @author edushi
 *
 */
public class ExListView extends MyBaseListView {
	private OnActionClickListener listener;
	private int[] buttonIds = null;

	public ExListView(Context context) {
		super(context);
	}

	public ExListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public void colseLastItem(){
		super.collapse();
	}

	public void setItemActionListener(OnActionClickListener listener, int ... buttonIds) {
		this.listener = listener;
		this.buttonIds = buttonIds;
	}

	/**
	 * Interface for callback to be invoked whenever an action is clicked in
	 * the expandle area of the list item.
	 */
	public interface OnActionClickListener {
		/**
		 * Called when an action item is clicked.
		 *
		 * @param itemView the view of the list item
		 * @param clickedView the view clicked
		 * @param position the position in the listview
		 */
		public void onClick(View itemView, View clickedView, int position);
	}

	public void setAdapter(final ListAdapter adapter) {
		super.setAdapter(new MyBaseAdapter(adapter) {

			@Override
			public View getView(final int position, View view, ViewGroup viewGroup) {
				final View listView = wrapped.getView(position, view, viewGroup);
				// add the action listeners
				if(buttonIds != null && listView!=null) {




					for(int id : buttonIds) {
						View buttonView = listView.findViewById(id);
						if(buttonView != null) {
								buttonView.findViewById(id).setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View view) {
												if (listener != null) {
													listener.onClick(listView,view, position);
												}
											}
										});
						}
					}
				}
				return listView;
			}
		});
	}

}
