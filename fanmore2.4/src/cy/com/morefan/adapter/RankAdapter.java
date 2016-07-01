package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.RankData;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.ImageLoad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RankAdapter extends BaseAdapter{
	private Context mContext;
	private List<RankData> datas;
	private int[] indexRes;
	private int widthMargin;
	private int myIndex = 1;
	public RankAdapter(Context mContext, List<RankData> datas){
		this.mContext = mContext;
		this.datas = datas;
		indexRes = new int[]{R.drawable.index_0,
							 R.drawable.index_1,
							 R.drawable.index_2,
							 R.drawable.index_3,
							 R.drawable.index_4,
							 R.drawable.index_5,
							 R.drawable.index_6,
							 R.drawable.index_7,
							 R.drawable.index_8,
							 R.drawable.index_9};
		widthMargin = DensityUtil.dip2px(mContext, 3);
	}
	public void setDatas(List<RankData> datas2) {
		this.datas = datas2;
		this.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
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
	public int getViewTypeCount() {
		//return super.getViewTypeCount();
		return 2;

	}

	@Override
	public int getItemViewType(int position) {
		//return super.getItemViewType(position);
		return datas.get(position).type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {



		int type= getItemViewType(position);
		if(type==0){
			return  getView0( convertView,position);

		}else {
			return getView1(convertView,position);
		}










		//return convertView;
	}


	protected View getView0(View convertView, int position){

		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.rank_item, null);
		}


		TextView imgIndex = ViewHolderUtil.get(convertView, R.id.imgIndex);
		ImageView imgRankuser = ViewHolderUtil.get(convertView,R.id.imgRankuser);
		TextView txtDes = ViewHolderUtil.get(convertView, R.id.txtDes);
		TextView txtName1 = ViewHolderUtil.get(convertView, R.id.txtName1);

		LinearLayout lay = ViewHolderUtil.get(convertView, R.id.lay);

		RankData data = datas.get(position);
		if (TextUtils.isEmpty(data.logo)){
			imgRankuser.setImageResource(R.drawable.user_icon);
		}else {
			ImageLoad.loadLogo(data.logo, imgRankuser, mContext);
		}
		imgIndex.setBackgroundResource(getIndexImage(data.rankValue ));
		lay.setBackgroundResource(getBgRes(position));
		txtDes.setText(String.valueOf((data.value))+"次");
		txtName1.setText(data.name);

		return convertView;
	}

	protected View getView1(View convertView, int position){

		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.rank_item_2, null);
		}
		RankData data = datas.get(position);
		ImageView myRanklogo =  ViewHolderUtil.get(convertView,R.id.myRanklogo);
		TextView myRankname =  ViewHolderUtil.get(convertView,R.id.myRankname);
		TextView myRankDes =  ViewHolderUtil.get(convertView,R.id.myRankDes);
		TextView myRankvalue =  ViewHolderUtil.get(convertView,R.id.myRankvalue);
		myRankDes.setText(data.myRankDes);
		myRankname.setText("我");
		myRankvalue.setText(data.myRankvalue);
		if (TextUtils.isEmpty(data.logo)){
			myRanklogo.setImageResource(R.drawable.user_icon);
		}else {
			ImageLoad.loadLogo(data.logo, myRanklogo, mContext);
		}
		return convertView;
	}


	private int getBgRes(int position) {

		if(0 == position){//第0项
			return  R.color.gray_list_bg ;
		}
		if (1== position){
			return  R.color.gray_list_bg;
		}if (2==position){
			return R.color.gray_list_bg;
		}if (3==position){
			return R.color.gray_list_bg;
		}else {
			return R.color.white;
		}
	}
	private int getIndexImage(int position){

			return indexRes[(position)%10];


	}
	private Bitmap getBitmap(int index){
		return BitmapFactory.decodeResource(mContext.getResources(), indexRes[index]);
	}
	/**
	 * 将两张位图拼接成一张(横向拼接)
	 *
	 * @param first
	 * @param second
	 * @return
	 */
	private Bitmap add2Bitmap(Bitmap first, Bitmap second) {

                int width =first.getWidth() + second.getWidth() + widthMargin;
                int height = Math.max(first.getHeight(), second.getHeight());
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(first, 0, 0, null);
		canvas.drawBitmap(second, first.getWidth() + widthMargin, 0, null);
		return result;
	}
	public void setMyRank(int myRank) {
		this.myIndex = myRank;
		L.i(">>>>>>myRank:" + myRank);
		notifyDataSetChanged();

	}


}
