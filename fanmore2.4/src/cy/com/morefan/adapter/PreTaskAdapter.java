package cy.com.morefan.adapter;

import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.R;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.CyButton;
import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PreTaskAdapter extends BaseAdapter{
	private Activity mActivity;
	private List<TaskData> datas;
	private SparseBooleanArray alarmStatus;
	SyncImageLoaderHelper mImageLoader;
	public enum TaskAdapterType{
		Normal,Mine
	}

	public PreTaskAdapter(SyncImageLoaderHelper mImageLoader, Activity mActivity, List<TaskData> datas){
		this.mActivity = mActivity;
		this.datas = datas;
		this.mImageLoader = mImageLoader;
		this.alarmStatus = new SparseBooleanArray();
		for(TaskData item : datas)
			alarmStatus.put(item.id, false);

	}
	@Override
	public void notifyDataSetChanged() {
		for(TaskData item : datas)
			alarmStatus.put(item.id, false);
		String ids = SPUtil.getStringToSpByName(mActivity, Constant.SP_NAME_NORMAL, Constant.SP_NAME_ALARM);
		L.i(">>>>>>>adapter notify:" + ids);
		if(!TextUtils.isEmpty(ids)){
			String[] idsA = ids.split(",");
			if(null != idsA){
				for(int i = 0, length = idsA.length; i < length; i++){
					if(!TextUtils.isEmpty(idsA[i]) ){
						alarmStatus.put(Integer.valueOf(idsA[i]), true);
					}
				}
			}
		}
		super.notifyDataSetChanged();

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
			convertView = LayoutInflater.from(mActivity).inflate(R.layout.tool_pre_item, null);
		}
		TextView txtShopDes			= ViewHolderUtil.get(convertView, R.id.txtShopDes);
		ImageView imgTask 			= ViewHolderUtil.get(convertView, R.id.imgTask);
		ProgressBar bar				= ViewHolderUtil.get(convertView, R.id.load_pb);
		TextView txtTaskName		= ViewHolderUtil.get(convertView, R.id.txtTaskName);
		TextView txtStoreName		= ViewHolderUtil.get(convertView, R.id.txtStoreName);

		TextView txtTotalScore		= ViewHolderUtil.get(convertView, R.id.txtTotalScore);
		TextView txtLastScore		= ViewHolderUtil.get(convertView, R.id.txtLastScore);

		TextView txtOrderTime		= ViewHolderUtil.get(convertView, R.id.txtOrderTime);
		TextView txtPreTime			= ViewHolderUtil.get(convertView, R.id.txtPreTime);

//		TextView tagType			= ViewHolderUtil.get(convertView, R.id.tagType);//活动标识，活动，新手，公告
		ImageView imgTagTop			= ViewHolderUtil.get(convertView, R.id.imgTagTop);
		LinearLayout layShopDes		= ViewHolderUtil.get(convertView, R.id.layShopDes);
		LinearLayout layScore		= ViewHolderUtil.get(convertView, R.id.layScore);
		ImageView imgOnline			= ViewHolderUtil.get(convertView, R.id.imgOnline);
		final TextView btnAlarm		= ViewHolderUtil.get(convertView, R.id.imgAlarm);
		TextView txtLastScoreDes   	= ViewHolderUtil.get(convertView, R.id.txtLastScoreDes);




        	final TaskData data = datas.get(position);

        	imgTask.setBackgroundResource(R.drawable.picreviewre_fresh_bg);
        	mImageLoader.loadImage(position, imgTask, bar, data.smallImgUrl, Constant.IMAGE_PATH_TASK);
        	imgTagTop.setVisibility(data.isTop ? View.GONE : View.GONE);

        	//bar.setProgress(position *10);
        	txtTaskName.setText(data.taskName);
			txtStoreName .setText(data.store);

			if(data.type == 1001000){
				txtTotalScore.setText("无上限");
				txtLastScoreDes.setText("浏览奖励:");
				txtLastScore.setText(data.awardScan);


//				layLast.setVisibility(View.INVISIBLE);
//				txtTotalScoreDes.setText("浏览奖励:");
//				txtTotalScore.setText(data.awardScan);
			}else{
				txtTotalScore.setText(data.totalScore);
				txtLastScoreDes.setText("剩余积分:");
				txtLastScore.setText(data.lastScore);


//				layLast.setVisibility(View.VISIBLE);
//				txtTotalScoreDes.setText("总积分:");
//
//				txtLastScore.setText(data.lastScore);
			}
//			txtTotalScore.setText(data.totalScore +"");
//			txtLastScore.setText(data.lastScore + "");
			txtOrderTime.setText("正式上线时间" + data.startTime);
			//txtPreTime.setText("您可以在" + data.advTime + "后,提前转发");
			txtPreTime.setText("拥有火眼金睛 可以提前转发");
			//总积分<=1时，隐藏积分描述
			layScore.setVisibility(Double.parseDouble(data.totalScore) <= 1 ? View.GONE : View.GONE);

			layShopDes.setVisibility(data.type == 1000300 && !TextUtils.isEmpty(data.rebate) ? View.GONE : View.GONE);
			txtShopDes.setText(data.rebate);

			//活动标识
//			tagType.setVisibility(data.type == 999999 ? View.GONE : View.VISIBLE);
//			tagType.setText(getTypeName(data.type));

			//状态标记设置
			//已上线、已转发不显示设置闹钟
			imgOnline.setVisibility(data.online == 2 ? View.GONE : View.GONE);


		return convertView;
	}
	private CharSequence getTypeName(int type) {
		//1000200新手任务,90100求包养,1000100公告
		switch (type) {
		case 1000200://新手
			return "新手";
		case 1000100://公告
			return "公告";
		case 1001000://联盟
			return "联盟";
		case 90100:
			return "求包养";
		case 1000300:
			return "闪购";

		default:
			return "活动";
		}
	}
	private void setStatus(ImageView imgStatusTag, TaskData data) {
		imgStatusTag.setVisibility(View.GONE);
			if(data.isSend){//是否已转发
				imgStatusTag.setVisibility(View.GONE);
			}else if(Double.parseDouble(data.lastScore) <= 0){//积分是否用完
				imgStatusTag.setVisibility(View.VISIBLE);
			}
		//不可转发，不显示状态
		if(data.flagShowSend == 0 ){
			imgStatusTag.setVisibility(View.GONE);
		}


	}







}
