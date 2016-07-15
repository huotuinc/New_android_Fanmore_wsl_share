package cy.com.morefan.adapter;

import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.R;
import cy.com.morefan.bean.AwardData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ViewHolderUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AwardAdapter extends TrendAdapter{
	private Context mContext;
	private List<AwardData> datas;
	SyncImageLoaderHelper mImageLoader;
	private boolean changeDes;
	private int trendAddCount;

	public AwardAdapter(SyncImageLoaderHelper mImageLoader, Context mContext, List<AwardData> datas){
		this.mContext = mContext;
		this.datas = datas;
		this.mImageLoader = mImageLoader;

	}
	@Override
	public int getCount() {
		return datas.size() + trendAddCount;
	}
	public void setDatas(List<AwardData> datas){
		this.datas = datas;
		notifyDataSetChanged();

	}

	public void setChangeDes(boolean changeDes){
		this.changeDes = changeDes;
	}

	public void addCount(int count){
		trendAddCount = count;
		notifyDataSetChanged();
	}
	@Override
	public void setFirstVisibleItem(int position) {
		//this.firstVisibleItem = position;
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

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.allscore_list_item, null);
		}

		//共有属性
		ImageView imgTask		= ViewHolderUtil.get(convertView, R.id.imgTask);
		ProgressBar bar			= ViewHolderUtil.get(convertView, R.id.load_pb);
		TextView txtDes			= ViewHolderUtil.get(convertView, R.id.txtTaskName);
		TextView txtName		= ViewHolderUtil.get(convertView, R.id.txtStoreName);
		TextView txtStore       = ViewHolderUtil.get(convertView,R.id.txtStore);
		TextView txtDate		= ViewHolderUtil.get(convertView, R.id.txtDate);
		//LinearLayout layScan	= ViewHolderUtil.get(convertView, R.id.layScan);
		TextView txtScore		= ViewHolderUtil.get(convertView, R.id.txtScore);
		TextView txtScanCount	= ViewHolderUtil.get(convertView, R.id.txtScanCount);
		TextView txtScoreDes	= ViewHolderUtil.get(convertView, R.id.txtScoreDes);
		TextView txtScanDes		= ViewHolderUtil.get(convertView, R.id.txtScanDes);
		LinearLayout layAward	= ViewHolderUtil.get(convertView, R.id.layAward);
		LinearLayout layDate	= ViewHolderUtil.get(convertView, R.id.layDate);
		ImageView imgStatusTag	= ViewHolderUtil.get(convertView, R.id.imgStatusTag);
		LinearLayout layGroup	= ViewHolderUtil.get(convertView, R.id.layGroup);
		TextView txtGroupDate	= ViewHolderUtil.get(convertView, R.id.txtGroupDate);
		TextView txtGroupScan	= ViewHolderUtil.get(convertView, R.id.txtGroupScan);
		//TextView txtGroupScore	= ViewHolderUtil.get(convertView, R.id.txtGroupScore);
		LinearLayout layBottom	= ViewHolderUtil.get(convertView, R.id.layBottom);
		LinearLayout lay_turn_browse = ViewHolderUtil.get(convertView,R.id.lay_turn_browse);
		lay_turn_browse.setVisibility(View.GONE);

		if(changeDes){
			txtScanDes.setText("昨日浏览量:");
			txtScoreDes.setText("昨日收益:");
		}




		if(position >= datas.size()){
			convertView.setVisibility(View.INVISIBLE );
		}else{
			convertView.setVisibility(View.VISIBLE );
			AwardData data = datas.get(position);

        	imgTask.setBackgroundResource(R.drawable.picreviewre_fresh_bg);
        	mImageLoader.loadImage(position, imgTask, bar, data.imgUrl, Constant.IMAGE_PATH_TASK);

        	//bar.setProgress(position *10);
        	txtName.setText(data.titile);
			txtDes .setText(data.titile);
			txtStore.setText(data.des);
			txtDate.setText(data.dateDes);
			layAward.setVisibility(data.type == 2 ? View.VISIBLE : View.GONE);
			layDate.setVisibility(data.type == 1 ? View.VISIBLE : View.GONE);
			imgStatusTag.setVisibility(data.type == 2 ? View.GONE : View.GONE);
			txtScanCount .setText(data.scanCount + "");
			txtScore .setText(data.score + "");
			layGroup.setVisibility(View.VISIBLE);
			txtGroupDate.setText(data.date);
			txtGroupScan.setText(String.valueOf(data.dayScanCount));
			//txtGroupScore.setText(String.valueOf(data.dayScore));
			if(position - 1 >= 0 && data.date.equals(datas.get(position - 1).date))
				layGroup.setVisibility(View.GONE);

			layBottom.setVisibility(View.VISIBLE);
			int length = datas.size();
			if(position + 1 < length && data.date.equals(datas.get(position + 1).date)){
				layBottom.setVisibility(View.GONE);
			}
		}




		return convertView;
	}




	@Override
	public String getDate(int position) {
		// TODO Auto-generated method stub
		return datas.get(position).date;
	}





}
