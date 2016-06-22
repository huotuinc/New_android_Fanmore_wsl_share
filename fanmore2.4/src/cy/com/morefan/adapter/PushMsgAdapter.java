package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.PushMsgData;
import cy.com.morefan.util.ViewHolderUtil;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PushMsgAdapter extends BaseAdapter{
	private Context mContext;
	private List<PushMsgData> datas;
	public PushMsgAdapter(Context mContext, List<PushMsgData> datas){
		this.mContext = mContext;
		this.datas = datas;
	}
	public void setDatas(List<PushMsgData> datas2) {
		this.datas = datas2;
		this.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.push_msg_item, null);
		}
		ImageView imgStatus = ViewHolderUtil.get(convertView, R.id.imgStatus);
		TextView txtDes = ViewHolderUtil.get(convertView, R.id.txtDes);
		TextView txtTitle = ViewHolderUtil.get(convertView, R.id.txtTitle);
		TextView txtTime = ViewHolderUtil.get(convertView, R.id.txtTime);


		PushMsgData data = datas.get(position);
		txtTitle.setText(data.title);
		txtDes.setText(data.des);
		txtTime.setText(data.time);
		imgStatus.setVisibility(data.type == 0 ? View.VISIBLE : View.GONE);
		txtTitle.setTextColor(data.type == 0 ? Color.RED : Color.BLACK);


		return convertView;
	}
	/**
	 *
	 * @param 1：未开始
               0：已开始
               2：已下架

	 * @return
	 */



}
