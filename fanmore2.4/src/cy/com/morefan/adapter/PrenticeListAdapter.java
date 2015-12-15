package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.PrenticeData;
import cy.com.morefan.util.ViewHolderUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PrenticeListAdapter extends BaseAdapter {
	public Context mContext;
	private List<PrenticeData> datas;
	public PrenticeListAdapter(Context mContext, List<PrenticeData> datas){
		this.mContext = mContext;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.prentice_list_item, null);
		}
		TextView txtIndex		= ViewHolderUtil.get(convertView, R.id.txtIndex);
		TextView txtName		= ViewHolderUtil.get(convertView, R.id.txtName);
		TextView txtTime		= ViewHolderUtil.get(convertView, R.id.txtTime);
		TextView txtLastContri	= ViewHolderUtil.get(convertView, R.id.txtLastContri);
		TextView txtTotalContri	= ViewHolderUtil.get(convertView, R.id.txtTotalContri);


		PrenticeData data = datas.get(position);
		txtIndex.setText((position + 1) + "");
		txtName.setText(data.name);
		txtTime.setText(String.format("%s成为了你的徒弟", data.time));
		txtLastContri.setText(data.lastContri + "积分");
		txtTotalContri.setText(data.totalContri + "积分");
		return convertView;
	}






}
