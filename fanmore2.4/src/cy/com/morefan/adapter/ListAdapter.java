package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.MallDetailActivity;
//import cy.com.morefan.PrenticDetailActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.BuyData;
import cy.com.morefan.bean.FeedbackData;
import cy.com.morefan.bean.HistoryData;
import cy.com.morefan.bean.PartnerData;
import cy.com.morefan.bean.PrenticeData;
import cy.com.morefan.bean.WalletData;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.ImageLoad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter{
	private Context mContext;
	private List<BaseData> datas;
	public ListAdapter(Context mContext, List<BaseData> datas){
		this.mContext = mContext;
		this.datas = datas;

	}
	public void setDatas(List<BaseData> datas2) {
		this.datas = datas2;
		this.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas.size();
	}

	@Override
	public BaseData getItem(int position) {
		// TODO Auto-generated method stub
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BaseData item = getItem(position);
		convertView = getConvertView(position, convertView, item);







		return convertView;
	}
	private View getConvertView(int position, View convertView, BaseData item) {
		if(item instanceof FeedbackData){
			FeedbackData data = (FeedbackData) item;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.feedbacklist_item, null);
			}
			LinearLayout chatFrom		= ViewHolderUtil.get(convertView, R.id.viewstubChatFrom);
			TextView txtBackContent		= ViewHolderUtil.get(convertView, R.id.txtBackContent);
			TextView txtBackTime		= ViewHolderUtil.get(convertView, R.id.txtBackTime);
			TextView txtCommitContent	= ViewHolderUtil.get(convertView, R.id.txtContent);
			TextView txtCommitTime		= ViewHolderUtil.get(convertView, R.id.txtTime);
			TextView txtStatus			= ViewHolderUtil.get(convertView, R.id.txtStatus);

			//0处理中1已处理
			chatFrom.setVisibility(data.status == 1 ? View.VISIBLE : View.GONE);
			txtBackContent.setText(data.backContent);
			txtBackTime.setText(data.backTime);
			txtCommitContent.setText(data.commitContent);
			txtCommitTime.setText(data.commitTime);
			txtStatus.setText(data.statusName);
		}else
		if(item instanceof BuyData){
			final BuyData data = (BuyData) item;
			if(convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.tab_mall_item, null);
			}
			TextView txtOrderNo		= ViewHolderUtil.get(convertView, R.id.txtOrderNo);
			TextView txtGoodsName	= ViewHolderUtil.get(convertView, R.id.txtGoodsName);
			TextView txtName		= ViewHolderUtil.get(convertView, R.id.txtName);
			TextView txtTime		= ViewHolderUtil.get(convertView, R.id.txtTime);
			TextView txtTemp		= ViewHolderUtil.get(convertView, R.id.txtTemp);
			TextView txtReal		= ViewHolderUtil.get(convertView, R.id.txtReal);
			TextView txtTimeCount	= ViewHolderUtil.get(convertView, R.id.txtTimeCount);

			txtOrderNo.setText(data.orderNo);
			txtGoodsName.setText(data.goodsName);
			txtName.setText(data.name);
			txtTime.setText(data.time);
			txtTemp.setText(data.tempScore + "");
			txtReal.setText(data.realScore + "");
			//待转正时显示
			//txtTimeCount.setVisibility(data.status == 0 ? View.VISIBLE :View.GONE);
			txtTimeCount.setText(data.timeCount);

			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intentDetail = new Intent(mContext, MallDetailActivity.class);
					intentDetail.putExtra("orderNo", data.orderNo);
					mContext.startActivity(intentDetail);
				}
			});
		}else
			if(item instanceof HistoryData){
				HistoryData data = (HistoryData) item;
				if(convertView == null){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.user_money_item, null);
				}
				TextView txtIndex		= ViewHolderUtil.get(convertView, R.id.txtIndex);
				TextView txtDes			= ViewHolderUtil.get(convertView, R.id.txtDes);
				TextView txtTime		= ViewHolderUtil.get(convertView, R.id.txtTime);
				TextView txtStatus		= ViewHolderUtil.get(convertView, R.id.txtStatus);
				TextView txtExtra		= ViewHolderUtil.get(convertView, R.id.txtExtra);
				LinearLayout layEx		= ViewHolderUtil.get(convertView, R.id.layEx);

				txtIndex.setText( position + 1 + "");
				txtDes.setText(data.action);
				txtStatus.setText(data.status);
				txtTime.setText(data.time);
				txtExtra.setText(data.extra);
				layEx.setVisibility(TextUtils.isEmpty(data.extra) ? View.GONE : View.VISIBLE);
			}else if(item instanceof WalletData){
				WalletData data = (WalletData) item;
				if(convertView == null){
					convertView = LayoutInflater.from(mContext).inflate(R.layout.user_money_item, null);
				}
				TextView txtIndex		= ViewHolderUtil.get(convertView, R.id.txtIndex);
				TextView txtDes			= ViewHolderUtil.get(convertView, R.id.txtDes);
				TextView txtTime		= ViewHolderUtil.get(convertView, R.id.txtTime);
				TextView txtStatus		= ViewHolderUtil.get(convertView, R.id.txtStatus);

				txtIndex.setText( position + 1 + "");
				txtDes.setText(data.actionDes);
				String flag = data.type == 1 ? "+" : "";
				txtStatus.setText(flag + data.amount);
				txtTime.setText(data.time);
			}
			else if(item instanceof PartnerData){
					final PartnerData data = (PartnerData) item;
					if(convertView == null){
						convertView = LayoutInflater.from(mContext).inflate(R.layout.prentice_list_item, null);
					}
					TextView txtIndex		= ViewHolderUtil.get(convertView, R.id.txtIndex);
					TextView txtName		= ViewHolderUtil.get(convertView, R.id.txtName);
					TextView txtTime		= ViewHolderUtil.get(convertView, R.id.txtTime);
					TextView txtLastContri	= ViewHolderUtil.get(convertView, R.id.txtLastContri);
					TextView txtTotalContri	= ViewHolderUtil.get(convertView, R.id.txtTotalContri);
					TextView yesterdayBrowseAmount = ViewHolderUtil.get(convertView, R.id.yesterdayBrowseAmount);
					TextView historyTotalBrowseAmount = ViewHolderUtil.get(convertView, R.id.historyTotalBrowseAmount);
					ImageView imgPhoto      = ViewHolderUtil.get(convertView,R.id.imgPhoto);

					txtIndex.setText((position + 1) + "");
					txtName.setText(data.userName);
				if (TextUtils.isEmpty(data.headFace)) {
					imgPhoto.setImageResource(R.drawable.user_icon);
				}else {
					ImageLoad.loadLogo(data.headFace,imgPhoto,mContext);
				}
					yesterdayBrowseAmount.setText(data.yesterdayBrowseAmount.substring(0,data.yesterdayBrowseAmount.indexOf("."))+"/"+data.yesterdayTurnAmount.substring(0,data.yesterdayTurnAmount.indexOf("."))+"次");
					historyTotalBrowseAmount.setText(data.historyTotalBrowseAmount.substring(0,data.historyTotalBrowseAmount.indexOf("."))+"/"+data.historyTotalTurnAmount.substring(0,data.historyTotalTurnAmount.indexOf("."))+"次");
				//imgPhoto
//					convertView.setOnClickListener(new View.OnClickListener() {
//						@Override
////						public void onClick(View v) {
////							Intent intentDetail = new Intent(mContext, PrenticDetailActivity.class);
////							intentDetail.putExtra("id", data.id);
////							mContext.startActivity(intentDetail);
////						}
//					});

				}

		return convertView;
	}


}
