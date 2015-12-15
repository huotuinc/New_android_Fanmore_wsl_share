package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.PrenticeContriData;
import cy.com.morefan.util.ViewHolderUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PrenticeDetailAdapter extends BaseAdapter {
	public Context mContext;
	private List<PrenticeContriData> datas;
	public PrenticeDetailAdapter(Context mContext, List<PrenticeContriData> datas){
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.prentice_detail_item, null);
		}

		TextView txtTime	= ViewHolderUtil.get(convertView, R.id.txtTime);
		TextView txtContri	= ViewHolderUtil.get(convertView, R.id.txtContri);

		PrenticeContriData data = datas.get(position);
		txtTime.setText(data.time);
		txtContri.setText(data.contri + "积分");
		return convertView;
	}
	static class ViewHolder{}

}
