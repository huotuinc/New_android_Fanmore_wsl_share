package cy.com.morefan.adapter;

import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.DataListActivity;
import cy.com.morefan.R;
import cy.com.morefan.DataListActivity.ActivityType;
import cy.com.morefan.bean.PrenticeData;
import cy.com.morefan.bean.PrenticeTopData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.CircleImageView;
import cy.com.morefan.view.ImageLoad;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.SyncStateContract.Helpers;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PrenticeAdapter extends BaseAdapter{



	private int ITEM_TYPE_TOP = 1;
	private int ITEM_TYPE_NORMAL = 0;
	private Context mContext;
	private PrenticeTopData topData;
	private List<PrenticeData> datas;
	private Handler mHandler;
	private SyncImageLoaderHelper helper;
	public PrenticeAdapter(Context mContext, PrenticeTopData topData, List<PrenticeData> datas, Handler mHandler){
		this.mContext = mContext;
		this.topData = topData;
		this.datas = datas;
		this.mHandler = mHandler;
	}
	@Override
	public int getItemViewType(int position) {
		if(position == 0)
			return ITEM_TYPE_TOP;
		else
			return ITEM_TYPE_NORMAL;
	}

	public void setDatas(PrenticeTopData topData, List<PrenticeData> datas){
		this.topData = topData;
		this.datas = datas;
		notifyDataSetChanged();

	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size() + 1;
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
		if(getItemViewType(position) == ITEM_TYPE_TOP){
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.tab_prentice_top, null);
			}
			TextView txtTopCode 	= ViewHolderUtil.get(convertView, R.id.txtCode);
			TextView txtTopDes		= ViewHolderUtil.get(convertView, R.id.txtDes);
			TextView txtTopTotal	= ViewHolderUtil.get(convertView, R.id.txtTotalContri);
			TextView txtTopLast		= ViewHolderUtil.get(convertView, R.id.txtLastContri);
			TextView prenticeAmount = ViewHolderUtil.get(convertView, R.id.prenticeAmount);
			TextView BrowseAmount   = ViewHolderUtil.get(convertView, R.id.BrowseAmount);
			TextView TurnAmount     = ViewHolderUtil.get(convertView, R.id.TurnAmount);
			CircleImageView imgPhoto	= ViewHolderUtil.get(convertView, R.id.imgPhoto);
			TextView btnCopy     =ViewHolderUtil.get(convertView,R.id.btnCopy);
			LinearLayout apprentice = ViewHolderUtil.get(convertView, R.id.apprentice);
			helper = new SyncImageLoaderHelper(mContext);
			btnCopy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mHandler.sendEmptyMessage(0x111111);
				}
			});
			apprentice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mHandler.sendEmptyMessage(0x11111111);
				}
			});
			UserData userData = UserData.getUserData();
			imgPhoto.setBorderColor(mContext.getResources().getColor(R.color.white));
			imgPhoto.setBorderWidth((int)mContext.getResources().getDimension(R.dimen.head_width));
			if(TextUtils.isEmpty(userData.picUrl)){
				imgPhoto.setImageResource(R.drawable.user_icon);
			}else{
				//helper.loadImage(0, imgPhoto, null, UserData.getUserData().picUrl, Constant.IMAGE_PATH_STORE);
				ImageLoad.loadLogo(UserData.getUserData().picUrl,imgPhoto,mContext);
			}
			txtTopCode.setText(topData.invitationCode);
			txtTopDes.setText(topData.prenticeDes);
			prenticeAmount.setText( String.valueOf( topData.prenticeAmount)+"人");
			BrowseAmount.setText( topData.yesterdayBrowseAmount+"/"+ topData.historyTotalBrowseAmount+"次");
			TurnAmount.setText(topData.yesterdayTurnAmount+"/"+topData.historyTotalTurnAmount+"次");
		}else{
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.tab_prentice_item, null);
			}
			TextView txtLast	= ViewHolderUtil.get(convertView, R.id.txtLastContri);
			TextView txtTotal	= ViewHolderUtil.get(convertView, R.id.txtTotalContri);
			TextView txtName	= ViewHolderUtil.get(convertView, R.id.txtName);
			TextView txtTime	= ViewHolderUtil.get(convertView, R.id.txtTime);

			PrenticeData data = datas.get(position - 1);
			txtLast.setText(String.format("上次贡献:%s积分", data.lastContri));
			txtTotal.setText(String.format("历史贡献:%s积分", data.totalContri));
			txtName.setText(data.name);
			txtTime.setText(data.time);
		}

		return convertView;
	}



}
