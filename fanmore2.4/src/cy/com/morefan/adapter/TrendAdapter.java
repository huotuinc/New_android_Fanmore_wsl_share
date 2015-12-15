package cy.com.morefan.adapter;

import android.widget.BaseAdapter;

public abstract class TrendAdapter extends BaseAdapter{
	public abstract void addCount(int count);
	public abstract void setFirstVisibleItem(int position);
	public abstract String getDate(int position);
}
