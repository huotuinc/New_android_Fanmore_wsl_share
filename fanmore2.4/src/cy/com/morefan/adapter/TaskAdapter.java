package cy.com.morefan.adapter;

import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.R;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.TaskFrag.TabType;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.ViewHolderUtil;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TaskAdapter extends BaseAdapter{
	private Context mContext;
	private List<TaskData> datas;
	SyncImageLoaderHelper mImageLoader;
	private TabType tabType;
	private boolean isMall;//是否为闪利栏目
	/**
	 * 普通，我的参与
	 * @author edushi
	 *
	 */
	public enum TaskAdapterType{
		Normal,Mine
	}
	private TaskAdapterType adapterType;

	public TaskAdapter(SyncImageLoaderHelper mImageLoader, Context mContext, List<TaskData> datas, TaskAdapterType adapterType){
		this.mContext = mContext;
		this.datas = datas;
		this.mImageLoader = mImageLoader;
		this.adapterType = adapterType;

	}
	public void setIsMall(boolean isMall){
		this.isMall = isMall;
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.tab_task_item, null);
		}
		TextView txtShopDes			= ViewHolderUtil.get(convertView, R.id.txtShopDes);
		ImageView imgTask 			= ViewHolderUtil.get(convertView, R.id.imgTask);
		ProgressBar bar				= ViewHolderUtil.get(convertView, R.id.load_pb);
		TextView txtTaskName		= ViewHolderUtil.get(convertView, R.id.txtTaskName);
		TextView txtStoreName		= ViewHolderUtil.get(convertView, R.id.txtStoreName);
		TextView txtSendCount		= ViewHolderUtil.get(convertView, R.id.txtSendCount);
		TextView txtDate			= ViewHolderUtil.get(convertView, R.id.txtDate);
		ImageView imgTagWeiXin		= ViewHolderUtil.get(convertView, R.id.imgTagWeiXin);
		ImageView imgTagSina		= ViewHolderUtil.get(convertView, R.id.imgTagSina);
		ImageView imgTagQzone		= ViewHolderUtil.get(convertView, R.id.imgTagQzone);
//		TextView txtSend			= ViewHolderUtil.get(convertView, R.id.txtSend);
//		TextView txtScan			= ViewHolderUtil.get(convertView, R.id.txtScan);
//		TextView txtLink			= ViewHolderUtil.get(convertView, R.id.txtLink);
		TextView txtTimeDis			= ViewHolderUtil.get(convertView, R.id.txtTimeDis);
		TextView txtTotalScore		= ViewHolderUtil.get(convertView, R.id.txtTotalScore);
		TextView txtTotalScoreDes	= ViewHolderUtil.get(convertView, R.id.txtTotalScoreDes);
		TextView txtLastScore		= ViewHolderUtil.get(convertView, R.id.txtLastScore);
		LinearLayout layMyIncome	= ViewHolderUtil.get(convertView, R.id.layMyIncome);
		//LinearLayout layExtra		= ViewHolderUtil.get(convertView, R.id.layExtra);
		TextView txtMySend			= ViewHolderUtil.get(convertView, R.id.txtMySend);
		TextView txtMyScan			= ViewHolderUtil.get(convertView, R.id.txtMyScan);
		//TextView txtMyLink			= ViewHolderUtil.get(convertView, R.id.txtMyLink);
//		TextView tagType			= ViewHolderUtil.get(convertView, R.id.tagType);//活动标识，活动，新手，公告
		ImageView imgStatusTag		= ViewHolderUtil.get(convertView, R.id.imgStatusTag);//首页状态
		ImageView imgTagTop			= ViewHolderUtil.get(convertView, R.id.imgTagTop);
		//LinearLayout layScan		= ViewHolderUtil.get(convertView, R.id.layScan);
		//TextView txtTotalCount		= ViewHolderUtil.get(convertView, R.id.txtTotalCount);
		LinearLayout layScore		= ViewHolderUtil.get(convertView, R.id.layScore);
		LinearLayout layChannel		= ViewHolderUtil.get(convertView, R.id.layChannel);
		LinearLayout laySendCount	= ViewHolderUtil.get(convertView, R.id.laySendCount);
		ImageView imgLine			= ViewHolderUtil.get(convertView, R.id.imgLine);
		ImageView imgLine2			= ViewHolderUtil.get(convertView, R.id.imgLine2);
		ImageView imgRight			= ViewHolderUtil.get(convertView, R.id.imgRight);
		LinearLayout layAlpha		= ViewHolderUtil.get(convertView, R.id.layAlpha);
		//LinearLayout layShopDes		= ViewHolderUtil.get(convertView, R.id.layShopDes);
		ImageView imgLineShop 		= ViewHolderUtil.get(convertView, R.id.imgLineShop);
		LinearLayout layLast		= ViewHolderUtil.get(convertView, R.id.layLast);
		TextView txtLastScoreDes	= ViewHolderUtil.get(convertView, R.id.txtLastScoreDes);




        	 TaskData data = datas.get(position);
        	 //日期分组
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
        	imgTagTop.setVisibility(data.isTop ? View.VISIBLE : View.GONE);
        	//设置联盟任务状态
//        	//test
//        	if(data.type == 1001000)
//        		data.status = 9;
//        	//test end
        	imgTagTop.setBackgroundResource(data.status == 8 ? R.drawable.tag_over : R.drawable.flag_top);
        	imgTagTop.setVisibility(data.status == 8 ? View.VISIBLE : View.GONE);

        	imgTagWeiXin.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_WEIXIN + "") ? R.drawable.share_ico_weixin : R.drawable.share_ico_weixin);
        	imgTagSina.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_SINA + "") ? R.drawable.share_wechat : R.drawable.share_wechat);
        	imgTagQzone.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_QZONE + "") ? R.drawable.share_ico_qzone : R.drawable.share_ico_qzone);


        	//layScan.setVisibility(adapterType == TaskAdapterType.Normal ? View.GONE : View.VISIBLE);
        	//bar.setProgress(position *10);
        	txtTaskName.setText(data.taskName);
			txtStoreName .setText(data.store);
//			txtSend .setText(data.awardSend);
//			txtScan.setText(data.awardScan);
//			txtLink .setText(data.awardLink);
			txtTimeDis.setText(data.dayDisDes);
			txtSendCount .setText(data.sendCount + "人已转发" );
			txtDate.setText(data.creatTime.split(" ")[0]);
			if(data.type == 1001000){
				txtTotalScore.setText("无上限");
				txtLastScoreDes.setText("浏览奖励:");
				txtLastScore.setText(data.awardScan);
			}else{
				txtTotalScore.setText(data.totalScore);
				txtLastScoreDes.setText("剩余积分:");
				txtLastScore.setText(data.lastScore);
			}
			layMyIncome.setVisibility((adapterType == TaskAdapterType.Normal || (adapterType == TaskAdapterType.Mine && !data.isAccount)) ? View.GONE : View.VISIBLE);
			imgRight.setVisibility(adapterType == TaskAdapterType.Mine && !data.isAccount ? View.GONE : View.VISIBLE);
			layAlpha.setVisibility(adapterType == TaskAdapterType.Mine && !data.isAccount ? View.VISIBLE : View.GONE);
			imgLine.setVisibility((adapterType == TaskAdapterType.Normal || (adapterType == TaskAdapterType.Mine && !data.isAccount)) ? View.GONE : View.VISIBLE);
			//layMyYesIncome.setVisibility(adapterType == TaskAdapterType.Yes ? View.VISIBLE : View.GONE);

			//layScore.setVisibility(data.flagShowSend == 0 ? View.GONE : View.VISIBLE);
			//总积分<=1时，隐藏积分描述
			layScore.setVisibility(Double.parseDouble(data.totalScore) <= 1 ? View.VISIBLE : View.VISIBLE);
			//不可转发的，隐藏积分，渠道，转发人数布局
			if(data.flagShowSend == 0)
				layScore.setVisibility( View.GONE );
			//imgLine2.setVisibility(layScore.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);

			txtShopDes.setVisibility( adapterType == TaskAdapterType.Normal && data.type == 1000300 && !TextUtils.isEmpty(data.rebate) ? View.VISIBLE : View.GONE);
			imgLineShop.setVisibility( adapterType == TaskAdapterType.Normal && data.type == 1000300 && !TextUtils.isEmpty(data.rebate) ? View.VISIBLE : View.GONE);
			txtShopDes.setText(data.rebate);

			layChannel.setVisibility(data.flagShowSend == 0 ? View.GONE : View.VISIBLE);
			laySendCount.setVisibility(data.flagShowSend == 0 || isMall ? View.GONE : View.VISIBLE);
			//laySendCount.setVisibility(isMall ? View.GONE : View.VISIBLE);
			//收益
			txtMySend.setText(data.myAwardSend);
			txtMyScan.setText(data.myAwardScan);
			//txtMyLink.setText(data.myAwardLink);
			//活动标识
//			tagType.setVisibility(data.type == 999999 ? View.GONE : View.VISIBLE);
//			tagType.setText(getTypeName(data.type));

			//状态标记设置
			setStatus(imgStatusTag, data);
			//返利栏目没有状态;剩余积分<=0时，隐藏积分描述
			if(tabType == TabType.Mall){
				imgStatusTag.setVisibility(View.GONE);
				if(Double.parseDouble(data.lastScore) <= 0)
					layScore.setVisibility(View.GONE);

			}
			imgLine2.setVisibility(layScore.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);

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
		if(adapterType == TaskAdapterType.Mine ){//我的参与
			if(data.isAccount){//已结算
				//4任务终结,5任务取消,6积分用完
				if( data.status == 4 || data.status == 5 || data.status == 6){
					imgStatusTag.setVisibility(View.VISIBLE);
					imgStatusTag.setBackgroundResource(R.drawable.task_tag_over);
				}
			}else{
				imgStatusTag.setVisibility(View.VISIBLE);
				imgStatusTag.setBackgroundResource(R.drawable.task_tag_not_account);
			}

		}else{
			if(data.isSend){//是否已转发
				imgStatusTag.setVisibility(View.VISIBLE);
				imgStatusTag.setBackgroundResource(R.drawable.task_send_tag);
			}else if(Double.parseDouble(data.lastScore) <= 0){//积分是否用完
				imgStatusTag.setVisibility(View.VISIBLE);
				imgStatusTag.setBackgroundResource(R.drawable.task_scoreover_tag);
			}
		}
		//不可转发，不显示状态
		if(data.flagShowSend == 0 && adapterType != TaskAdapterType.Mine ){
			imgStatusTag.setVisibility(View.GONE);
		}


	}
	public void setTabType(TabType tabType) {
		this.tabType = tabType;
	}







}
