package cy.com.morefan.view.exlist;


import cy.com.morefan.view.PullDownUpListView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MyBaseListView extends PullDownUpListView{

	
	
	
	private int ID_EXPEND_VIEW;
	private int ID_EXPEND_BUTTON;
	private ListView listView;
	private MyAdapter adapter;
	public MyBaseListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param expendViewId 展开部分
	 * @param expendButtonId 默认显示部分
	 */
	public void setOnExpendLister(int expendViewId,int expendButtonId){
		this.listView = this;
		this.ID_EXPEND_VIEW = expendViewId;
		this.ID_EXPEND_BUTTON = expendButtonId;
	}
	public MyBaseListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public MyBaseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Collapses the currently open view.
	 *
	 * @return true if a view was collapsed, false if there was no open view.
	 */
	public boolean collapse() {
		if(adapter!=null) {
			return adapter.collapseLastOpen();
		}
		return false;
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
	@Override
	public void setAdapter(ListAdapter adapter) {
		if(ID_EXPEND_BUTTON == 0 || ID_EXPEND_VIEW == 0)
			throw new IllegalArgumentException("button id or expend view id is zero!");
		this.adapter = new MyAdapter(listView,adapter,ID_EXPEND_VIEW,ID_EXPEND_BUTTON);
		super.setAdapter(this.adapter);

	
	}
	

	



}
