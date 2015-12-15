package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.FeedbackData;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.CyButton;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToolAdapter extends BaseAdapter{
	public interface OnItemClickListener{
		void onItemClick(int position);
	}
	private Context mContext;
	public ToolAdapter(Context mContext){
		this.mContext = mContext;

	}
	public void setDatas(List<FeedbackData> datas2) {
		this.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	private OnItemClickListener listener;
	public void setOnItemClickListener(OnItemClickListener listener){
		this.listener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.tab_tool_item, null);
		}
		convertView.setBackgroundColor(position%2==0 ? 0xffeaeaea : Color.WHITE);

		if(null != listener){
			ViewHolderUtil.get(convertView, R.id.btn).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listener.onItemClick(position);
				}
			});
		}
		return convertView;
	}


}
