package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.FeedbackData;
import cy.com.morefan.bean.ToolData;
import cy.com.morefan.util.ViewHolderUtil;
import android.R.integer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyToolAdapter extends BaseAdapter{
	private Context mContext;
	private List<ToolData> datas;
	public MyToolAdapter(Context mContext, List<ToolData> datas){
		this.mContext = mContext;
		this.datas = datas;

	}
	public void setDatas(List<ToolData> datas) {
		this.datas = datas;
		this.notifyDataSetChanged();

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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.tab_tool_my_item, null);
		}
		ImageView img = ViewHolderUtil.get(convertView, R.id.img);
		TextView txtCount = ViewHolderUtil.get(convertView, R.id.txtCount);
		TextView txtDes = ViewHolderUtil.get(convertView, R.id.txtDes);


		ToolData tool = datas.get(position);
		img.setBackgroundResource(getImgId(tool.type));
		txtCount.setText("×" + tool.ownCount);
		txtDes.setText(tool.name);
		return convertView;
	}
	/**
	 * 道具类型
	 * 1 摇徒弟
	 * 2抽奖
	 * 3任务转发提限
	 * 4任务提前预览
	 */
	public int getImgId(int type){
		switch (type) {
		case 1:
			return R.drawable.tool_prentice2;
		case 2:
			return R.drawable.tool_shake2;
		case 3:
			return R.drawable.tool_sendcount2;
		case 4:
			return R.drawable.tool_sendtime2;

		default:
			return R.drawable.tool_prentice2;
		}

	}


}
