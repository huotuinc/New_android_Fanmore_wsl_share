package cy.com.morefan.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.StoreListData;
import cy.com.morefan.util.RandomColor;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.ImageLoad;

/**
 * Created by 47483 on 2016/5/13.
 */
public class SelectionAdapter extends BaseAdapter {
    private Context mContext;
    private List<StoreListData> datas;
    public SelectionAdapter(Context mContext, List<StoreListData> datas){
        this.mContext = mContext;
        this.datas = datas;

    }
    public void setDatas(List<StoreListData> datas) {
        this.datas = datas;
        this.notifyDataSetChanged();
    }
        @Override
    public int getCount() {
            return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.selection_item, null);
        }
        ImageView itemImage= ViewHolderUtil.get(convertView, R.id.itemImage);
        TextView itemText=ViewHolderUtil.get(convertView, R.id.itemText);
        StoreListData storeListData = datas.get(position);
        if(TextUtils.isEmpty(storeListData.getLogo())){
            itemImage.setImageResource(R.drawable.user_icon);
        }else{
            ImageLoad.loadLogo(storeListData.getLogo(), itemImage, mContext);
        }
        itemText.setText(storeListData.getUserNickName());
        return convertView;
    }
}
