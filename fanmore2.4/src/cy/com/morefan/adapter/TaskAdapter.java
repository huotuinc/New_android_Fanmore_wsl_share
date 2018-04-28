package cy.com.morefan.adapter;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cy.com.morefan.MainApplication;
import cy.com.morefan.MoblieLoginActivity;
import cy.com.morefan.R;
import cy.com.morefan.TaskDetailActivity;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.TaskFrag.TabType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.ScoreService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.TipDialog;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.stat.common.User;

public class TaskAdapter extends BaseAdapter{
	private Context mContext;
	private List<TaskData> datas;
	SyncImageLoaderHelper mImageLoader;
	private TabType tabType;
	private boolean isMall;//是否为闪利栏目
	private ScoreService scoreService;
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
		this.scoreService = new ScoreService(new BusinessDataListener() {
			@Override
			public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {

			}

			@Override
			public void onDataFailed(int type, String des, Bundle extra) {

			}

			@Override
			public void onDataFail(int type, String des, Bundle extra) {

			}
		});
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
		TextView txtStore           = ViewHolderUtil.get(convertView,R.id.txtStore);
		TextView browseCount        = ViewHolderUtil.get(convertView,R.id.browseCount);
		//TextView sendCount        = ViewHolderUtil.get(convertView,R.id.sendCount);
		ImageView task_item_share   = ViewHolderUtil.get(convertView , R.id.task_item_share);

        	 final TaskData data = datas.get(position);
        	 //日期分组
        	 if(position < 1) {
				 //txtDate.setVisibility(View.VISIBLE);
			 }else {
        		 if(data.dayCount == datas.get(position -1).dayCount){
        			 //txtDate.setVisibility(View.GONE);
        		 }else{
        			 //txtDate.setVisibility(View.VISIBLE);
        		 }
        	 }
				if (data.isSend){
					imgStatusTag.setVisibility(View.VISIBLE);
				}else {
					imgStatusTag.setVisibility(View.GONE);
				}
        	imgTask.setBackgroundResource(R.drawable.picreviewre_fresh_bg);
        	//mImageLoader.loadImage(position, imgTask, bar, data.taskSmallImgUrl , Constant.IMAGE_PATH_TASK);
        	imgTagTop.setVisibility(View.GONE);
        	//设置联盟任务状态
//        	//test
//        	if(data.type == 1001000)
//        		data.status = 9;
//        	//test end
        	//imgTagTop.setBackgroundResource(data.status == 8 ? R.drawable.tag_over : R.drawable.flag_top);
        	imgTagTop.setVisibility(View.GONE);

        	imgTagWeiXin.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_WEIXIN + "") ? R.drawable.share_ico_weixin : R.drawable.share_ico_weixin);
        	imgTagSina.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_SINA + "") ? R.drawable.share_wechat : R.drawable.share_wechat);
        	imgTagQzone.setBackgroundResource(data.channelIds.contains(ShareUtil.CHANNEL_QZONE + "") ? R.drawable.share_ico_qzone : R.drawable.share_ico_qzone);
			if(data.browseCount>=100000) {
				browseCount.setText("10万+");
			}else {
				browseCount.setText(String.valueOf(data.browseCount));
			}
			//sendCount.setText(String.valueOf(data.sendCount));
        	//layScan.setVisibility(adapterType == TaskAdapterType.Normal ? View.GONE : View.VISIBLE);
        	//bar.setProgress(position *10);
        	txtTaskName.setText(data.taskName);
			txtStore.setText("由["+data.store+"]提供");
//			txtSend .setText(data.awardSend);
//			txtScan.setText(data.awardScan);
//			txtLink .setText(data.awardLink);
			txtTimeDis.setText(data.dayDisDes);
			txtSendCount .setText(String.valueOf(data.sendCount) + "人已转发" );
			//txtDate.setText(data.creatTime.split(" ")[0]);
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
			imgRight.setVisibility(adapterType == TaskAdapterType.Mine && !data.isAccount ? View.GONE : View.GONE);
			layAlpha.setVisibility(adapterType == TaskAdapterType.Mine && !data.isAccount ? View.VISIBLE : View.GONE);
			imgLine.setVisibility((adapterType == TaskAdapterType.Normal || (adapterType == TaskAdapterType.Mine && !data.isAccount)) ? View.GONE : View.VISIBLE);
			//layMyYesIncome.setVisibility(adapterType == TaskAdapterType.Yes ? View.VISIBLE : View.GONE);

			//layScore.setVisibility(data.flagShowSend == 0 ? View.GONE : View.VISIBLE);
			//总积分<=1时，隐藏积分描述
			//layScore.setVisibility(Double.parseDouble(data.totalScore) <= 1 ? View.VISIBLE : View.VISIBLE);
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
			//返利栏目没有状态;剩余积分<=0时，隐藏积分描述
//			if(tabType == TabType.Mall){
//				imgStatusTag.setVisibility(View.GONE);
//				if(Double.parseDouble(data.lastScore) <= 0)
//					layScore.setVisibility(View.GONE);
//
//			}
			imgLine2.setVisibility(layScore.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);


		 ImageView task_item_favarte =ViewHolderUtil.get(convertView , R.id.task_item_favarte);
		 task_item_favarte.setImageResource( data.isFav ? R.drawable.favorite_selected : R.drawable.favorite );
		 task_item_favarte.setOnClickListener(new View.OnClickListener() {
			 @Override
			 public void onClick(View v) {
				favoriate(data);
			 }
		 });

		SimpleDraweeView imgTaskPic = ViewHolderUtil.get(convertView , R.id.imgTaskPic);
		imgTaskPic.setImageURI( data.taskMainImgUrl );
		setAutoAmi(imgTaskPic ,data.taskMainImgUrl );
		SimpleDraweeView taskSmallImg = ViewHolderUtil.get(convertView , R.id.taskSmallImg);
		taskSmallImg.setImageURI(data.taskSmallImgUrl);

		task_item_share.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(data);
			}
		});

		return convertView;
	}

	protected void setAutoAmi(SimpleDraweeView simpleDraweeView , String url ){
		DraweeController draweeController = Fresco
				.newDraweeControllerBuilder()
				.setAutoPlayAnimations(true)
				//.setTapToRetryEnabled(true)
				.setUri( url )
				.setOldController(simpleDraweeView.getController())
				//.setControllerListener( listener )

				.build();

		simpleDraweeView.setController( draweeController);

		float rradie = DensityUtil.dip2px(mContext, 5);
        RoundingParams roundingParams=RoundingParams.fromCornersRadius(rradie);

        //roundingParams.setCornersRadii( rradie , rradie , rradie , rradie );

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(mContext.getResources());
        GenericDraweeHierarchy hierarchy = builder
                //.setFadeDuration(300)
                //.setPlaceholderImage(new MyCustomDrawable())
                //.setBackgrounds(backgroundList)
                //.setOverlays(overlaysList)
                .setRoundingParams(roundingParams)
                .setFailureImage(R.drawable.gray_corner_bg)
                .setPlaceholderImage(R.drawable.gray_corner_bg)
                .setDesiredAspectRatio(2)
                .build();
        simpleDraweeView.setHierarchy(hierarchy);

	}


	private void setStatus(ImageView imgStatusTag, TaskData data) {
		imgStatusTag.setVisibility(View.GONE);


	}
	public void setTabType(TabType tabType) {
		this.tabType = tabType;
	}

	protected void favoriate(TaskData taskData){
		//if(taskData.isFav)return;
		scoreService.collection(mContext , UserData.getUserData().loginCode , taskData.taskId );
		taskData.isFav=!taskData.isFav;
		this.notifyDataSetChanged();
	}

	protected void share( TaskData taskData ){
		if(!UserData.getUserData().isLogin){
			Intent intentlogin = new Intent(mContext , MoblieLoginActivity.class);
			((Activity)mContext).startActivity(intentlogin);
		}else{
			if( !UserData.getUserData().ignoreJudgeEmulator && BusinessStatic.getInstance().ISEMULATOR){
				ToastUtil.show( mContext , "模拟器不支持该操作!");
				return;
			}
			if(BusinessStatic.getInstance().disasterFlag==1){
				copy(taskData.content);
			}else {
				//share();
				popAskInfo(taskData);
			}

		}
	}

	private void copy(String content) {
		// 得到剪贴板管理器
		android.content.ClipboardManager cmb = (android.content.ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clipData = ClipData.newPlainText( content , content );
		cmb.setPrimaryClip(clipData);
		ToastUtil.show( mContext , "复制链接 转发到朋友圈/好友");

	}

	/***
	 *  分享前先弹出提示框
	 */
	private void popAskInfo(final TaskData taskData){
		boolean showPop = MainApplication.single.readShareTipDialog();
		if(!showPop){
			share(taskData);
			return;
		}


		String content="转发给好友需点击返回App才能计入转发。\r\n已转发文章再次分享不计入转发。";

		TipDialog.show(mContext , "提示", content, "知道了", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				shareInfo(taskData);
			}
		}, new CompoundButton.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				MainApplication.single.writeShareTipDialog(!b);
			}
		});
	}


	private void shareInfo(TaskData taskData){

		String imgUrl = taskData.smallImgUrl;
		String shareDes =taskData.taskName;
		String shareUrl =taskData.content;
		String fullPath1 = Constant.IMAGE_PATH_TASK + File.separator + taskData.smallImgUrl.substring(taskData.smallImgUrl.lastIndexOf("/") + 1);
		OnekeyShare oks = new OnekeyShare();

		//关闭sso授权
		oks.disableSSOWhenAuthorize();


		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(shareDes);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(shareUrl);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(shareDes);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImageUrl(imgUrl);//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(shareUrl);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite( mContext.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(shareUrl);

		// 启动分享GUIfem
		oks.show(mContext );
	}


}
