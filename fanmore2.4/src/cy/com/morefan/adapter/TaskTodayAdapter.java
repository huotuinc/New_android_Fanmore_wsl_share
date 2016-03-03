package cy.com.morefan.adapter;

import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.R;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.ViewHolderUtil;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TaskTodayAdapter extends BaseAdapter{
	private Context mContext;
	private List<TaskData> datas;
	SyncImageLoaderHelper mImageLoader;

	public TaskTodayAdapter(SyncImageLoaderHelper mImageLoader, Context mContext, List<TaskData> datas){
		this.mContext = mContext;
		this.datas = datas;
		this.mImageLoader = mImageLoader;

	}
	@Override
	public int getCount() {
		return datas.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.tab_today_task_item, null);
		}
		ImageView imgTask			= ViewHolderUtil.get(convertView, R.id.imgTask);
		ProgressBar bar				= ViewHolderUtil.get(convertView, R.id.load_pb);
		TextView txtTaskName		= ViewHolderUtil.get(convertView, R.id.txtTaskName);
		TextView txtStoreName		= ViewHolderUtil.get(convertView, R.id.txtStoreName);
		TextView txtSendCount		= ViewHolderUtil.get(convertView, R.id.txtSendCount);
		TextView txtDate			= ViewHolderUtil.get(convertView, R.id.txtDate);
		TextView txtTimeDis			= ViewHolderUtil.get(convertView, R.id.txtTimeDis);
		TextView txtDayScanCount	= ViewHolderUtil.get(convertView, R.id.txtDayScanCount);
		TextView txtTotalScanCount	= ViewHolderUtil.get(convertView, R.id.txtTotalScanCount);
		ImageView imgTagWeiXin		= ViewHolderUtil.get(convertView, R.id.imgTagWeiXin);
		ImageView imgTagSina		= ViewHolderUtil.get(convertView, R.id.imgTagSina);
		ImageView imgTagQzone		= ViewHolderUtil.get(convertView, R.id.imgTagQzone);
		ImageView imgStatusTag		= ViewHolderUtil.get(convertView, R.id.imgStatusTag);//我的参与状态



        	 TaskData data = datas.get(position);
        	 if(position < datas.size() - 1){
        		 if(data.dayCount == datas.get(position + 1).dayCount){
        			 txtDate.setVisibility(View.GONE);
        		 }else{
        			 txtDate.setVisibility(View.VISIBLE);
        		 }
        	 }else{
        		 txtDate.setVisibility(View.VISIBLE);
        	 }

        	imgTask.setBackgroundResource(R.drawable.picreviewre_fresh_bg);
        	mImageLoader.loadImage(position, imgTask, bar, data.smallImgUrl, Constant.IMAGE_PATH_TASK);
        	imgTagWeiXin.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_WEIXIN + "") ? R.drawable.share_ico_weixin : R.drawable.share_ico_weixin);
        	imgTagSina.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_SINA + "") ? R.drawable.share_ico_sina : R.drawable.share_ico_sina);
        	imgTagQzone.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_QZONE + "") ? R.drawable.share_ico_qzone : R.drawable.share_ico_qzone);

        	//bar.setProgress(position *10);
        	txtTaskName.setText(data.taskName);
			txtStoreName .setText(data.store);
			txtDayScanCount.setText(data.dayScanCount + "");
			txtTotalScanCount .setText(data.totalScanCount + "");
			txtSendCount .setText(data.sendCount + "人已转发" );
			txtTimeDis.setText(data.dayDisDes);
			txtDate .setText(data.creatTime.split(" ")[0]);
			int resid = getStatusImgResId(data.status);
			imgStatusTag.setVisibility(resid == 0 ? View.GONE : View.VISIBLE);
			if(resid != 0){
				imgStatusTag.setBackgroundResource(resid);
			}



		return convertView;
	}
	private int getStatusImgResId(int status){
		//4任务终结,5任务取消,6积分用完
		if( status == 4 || status == 5 || status == 6){
			return R.drawable.task_tag_over;
		}
		return 0;
	}







}
