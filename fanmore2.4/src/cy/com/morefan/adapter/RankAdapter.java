package cy.com.morefan.adapter;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.RankData;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.ViewHolderUtil;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.rank_item, null);
		}
		ImageView imgIndex = ViewHolderUtil.get(convertView, R.id.imgIndex);
		TextView txtDes = ViewHolderUtil.get(convertView, R.id.txtDes);
		TextView txtName1 = ViewHolderUtil.get(convertView, R.id.txtName1);
		TextView txtName2 = ViewHolderUtil.get(convertView, R.id.txtName2);
		LinearLayout lay = ViewHolderUtil.get(convertView, R.id.lay);

		RankData data = datas.get(position);
		imgIndex.setImageBitmap(getIndexImage(position + 1));
		lay.setBackgroundResource(getBgRes(position));
		txtDes.setText(data.rank);
		txtName1.setText(data.name1);
		txtName2.setText(data.name2);
		txtName2.setVisibility(TextUtils.isEmpty(data.name2) ? View.GONE : View.VISIBLE);


		return convertView;
	}
	private int getBgRes(int position) {

		if(0 == position){//第0项
			return (myIndex- 1) == position ? R.drawable.rank_item_top1_bg_my : R.drawable.rank_item_top1_bg;
		}else if(0 == position%2){//偶数项
			return (myIndex- 1) == position ? R.drawable.rank_item_even_bg_my : R.drawable.rank_item_even_bg;
		}else{
			return (myIndex- 1) == position ? R.drawable.rank_item_odd_my : R.drawable.rank_item_odd_bg;
		}
	}
	private Bitmap getIndexImage(int index){
		Bitmap bitmapTen = null;
		int ten = index/10;
		if(0 != ten )
			bitmapTen = getBitmap(ten);
		Bitmap bitmap = getBitmap(index%10);
		return bitmapTen == null ? bitmap : add2Bitmap(bitmapTen, bitmap);
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
