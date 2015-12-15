package cy.com.morefan.view.exlist;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;


public class MyAdapter extends MyBusinessAdapter {
	private int ID_EXPEND_VIEW;
	private int ID_EXPEND_BUTTON;
	private ListView listView;

	public MyAdapter(ListView listView, ListAdapter adapter, int iD_EXPEND_VIEW,int iD_EXPEND_BUTTON) {
		super(adapter);
		this.ID_EXPEND_VIEW = iD_EXPEND_VIEW;
		this.ID_EXPEND_BUTTON = iD_EXPEND_BUTTON;
		this.listView = listView;
	}
	public MyAdapter(ListAdapter adapter) {
		super(adapter);
	}
	@Override
	protected ListView getListView() {
		return listView;
	}
	@Override
	protected View getExpendView(View parent) {
		return parent.findViewById(ID_EXPEND_VIEW);
	}

	@Override
	protected View getExpendButtonView(View parent) {
		return parent.findViewById(ID_EXPEND_BUTTON);
	}


}
