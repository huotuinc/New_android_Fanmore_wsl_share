package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.AllScoreData;
import cy.com.morefan.util.ViewHolderUtil;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AllScoreAdapter extends TrendAdapter{
	private Context mContext;
	private List<AllScoreData> datas;
	private int firstVisibleItem;
	private int trendAddCount;
	public AllScoreAdapter(Context mContext, List<AllScoreData> datas){
		this.mContext = mContext;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size() + trendAddCount;
	}
	public void setDatas(List<AllScoreData> datas){
		this.datas = datas;
		notifyDataSetChanged();

	}

	public void addCount(int count){
		trendAddCount = count;
		notifyDataSetChanged();
	}
	@Override
	public void setFirstVisibleItem(int position) {
		this.firstVisibleItem = position;
		notifyDataSetChanged();
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

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.allscore_item, null);

		}
		ImageView lineTop 		= ViewHolderUtil.get(convertView, R.id.lineTop);
		ImageView lineBottom 	= ViewHolderUtil.get(convertView, R.id.lineBottom);
		TextView txtCircle 		= ViewHolderUtil.get(convertView, R.id.txtCircle);
		//RelativeLayout lay		= ViewHolderUtil.get(convertView, R.id.lay);
		TextView txtExtra		= ViewHolderUtil.get(convertView, R.id.txtExtra);
		TextView txtScanCount	= ViewHolderUtil.get(convertView, R.id.txtScanCount);
		TextView txtTime		= ViewHolderUtil.get(convertView, R.id.txtTime);
		LinearLayout layScan	= ViewHolderUtil.get(convertView, R.id.layScan);






		if(position >= datas.size()){
			convertView.setVisibility(View.INVISIBLE );
		}else{
			convertView.setVisibility(View.VISIBLE );
			AllScoreData data = datas.get(position);
			lineTop.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
			lineBottom.setVisibility(position == datas.size() -1 ? View.INVISIBLE : View.VISIBLE);

			txtCircle.setBackgroundResource(position == firstVisibleItem ? R.drawable.red_circle : R.drawable.blue_circle);
			if(position < datas.size()){
				txtCircle.setText(data.score + "");
			}
			//浏览
			int leftOrRight = position%2 == 0 ? RelativeLayout.RIGHT_OF : RelativeLayout.LEFT_OF;
			RelativeLayout.LayoutParams layoutParams = (LayoutParams) layScan.getLayoutParams();
			//api 17以下没有removeRule,所以用add -1来remove rule
			layoutParams.addRule(RelativeLayout.RIGHT_OF, -1);
			layoutParams.addRule(RelativeLayout.LEFT_OF, -1);
			layoutParams.addRule(leftOrRight, R.id.layCircle);
			layScan.setLayoutParams(layoutParams);
			//额外描述
			boolean hasExtra = !TextUtils.isEmpty(data.extra);
			txtExtra.setVisibility(hasExtra ? View.VISIBLE :View.GONE);
			if(hasExtra){
				leftOrRight = (position + 1)%2 == 0 ? RelativeLayout.RIGHT_OF : RelativeLayout.LEFT_OF;
				txtExtra.setBackgroundResource(leftOrRight == RelativeLayout.RIGHT_OF ? R.drawable.allscore_bg_right : R.drawable.allscore_bg_left);
				layoutParams = (LayoutParams) txtExtra.getLayoutParams();
				layoutParams.addRule(RelativeLayout.RIGHT_OF, -1);
				layoutParams.addRule(RelativeLayout.LEFT_OF, -1);
				layoutParams.addRule(leftOrRight, R.id.layCircle);
				txtExtra.setLayoutParams(layoutParams);
				txtExtra.setText(data.extra);
			}
			txtScanCount.setText(data.scanCount + "");
			txtTime.setText(data.date);



				if(firstVisibleItem == position){

			}

		}

		return convertView;
	}
	@Override
	public String getDate(int position) {
		// TODO Auto-generated method stub
		return null;
	}



}
