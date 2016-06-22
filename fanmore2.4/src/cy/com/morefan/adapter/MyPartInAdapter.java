package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.PartInItemData;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ViewHolderUtil;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyPartInAdapter extends BaseAdapter{
	private boolean needGroup;
	private Context mContext;
	private List<PartInItemData> datas;
	private Integer[] res = new Integer[]{R.drawable.share_ico_weixin, R.drawable.share_ico_sina, R.drawable.share_ico_qzone};
	public MyPartInAdapter(Context mContext, List<PartInItemData> datas){
		this.mContext = mContext;
		this.datas = datas;

	}

	@Override
	public int getCount() {
		return datas.size();
	}

	public void setNeedGroup(boolean needGroup){
		this.needGroup = needGroup;

	}
	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.user_money_item, null);
		}
		TextView txtIndex		= ViewHolderUtil.get(convertView, R.id.txtIndex);
		TextView txtDes			= ViewHolderUtil.get(convertView, R.id.txtDes);
		TextView txtTime		= ViewHolderUtil.get(convertView, R.id.txtTime);
		TextView txtDate		= ViewHolderUtil.get(convertView, R.id.txtDate);
		TextView txtStatus		= ViewHolderUtil.get(convertView, R.id.txtStatus);
		LinearLayout layFrom	= ViewHolderUtil.get(convertView, R.id.layFrom);
		ImageView imgFrom		= ViewHolderUtil.get(convertView, R.id.imgFrom);


		PartInItemData data = datas.get(position);
		// 日期分组
		if(needGroup){
			if (position < datas.size() - 1) {
				if (data.date.equals(datas.get(position + 1).date)) {
					txtDate.setVisibility(View.GONE);
				} else {
					txtDate.setVisibility(View.VISIBLE);
				}
			} else {
				txtDate.setVisibility(View.VISIBLE);
			}
		}
		txtDate.setText(data.date);


		txtIndex.setText(position + 1 + "");
		txtDes.setText(data.des);
		txtTime.setText(data.time);
		txtStatus.setText(data.score);
		L.i(">>>>>score" + position + ":" + data.score + "," + "0".equals(data.score));
		layFrom.setVisibility(data.channel == 0 ? View.GONE : View.VISIBLE);
		if(data.channel > 0 && data.channel < 4)
			imgFrom.setBackgroundResource(res[data.channel - 1]);
		return convertView;
	}


}
