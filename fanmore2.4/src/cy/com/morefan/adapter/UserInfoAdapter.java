package cy.com.morefan.adapter;

import java.util.ArrayList;
import java.util.List;


import cy.com.morefan.R;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.util.ViewHolderUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfoAdapter extends BaseAdapter{
	private Context mContext;
	private List<UserSelectData>  datas;
	private boolean[] tags;
	public UserInfoAdapter(Context mContext, List<UserSelectData> datas){
		this.mContext = mContext;
		this.datas = datas;
		tags = new boolean[datas.size()];

	}

	@Override
	public int getCount() {
		return datas.size();
	}
	public List<UserSelectData> getSelectData(){
		List<UserSelectData> result = new ArrayList<UserSelectData>();
		int length = tags.length;
		for(int i = 0; i < length; i++){
			if(tags[i])
				result.add(datas.get(i));

		}
		return result;
	}
	public void setSelect(int position){
		tags[position] = !tags[position];
		notifyDataSetChanged();

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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.userinfo_item, null);
		}
		TextView txtName	= ViewHolderUtil.get(convertView, R.id.txtName);
		ImageView imgTag	= ViewHolderUtil.get(convertView, R.id.imgTag);;

		imgTag.setVisibility(tags[position] ? View.VISIBLE : View.GONE);
		txtName.setText(datas.get(position).name);
		return convertView;
	}


}
