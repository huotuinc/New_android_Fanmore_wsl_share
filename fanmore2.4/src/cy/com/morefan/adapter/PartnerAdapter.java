package cy.com.morefan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.bean.PartnerData;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.ImageLoad;

/**
 * Created by 47483 on 2016/6/23.
 */

public class PartnerAdapter extends BaseAdapter {
    private Context mContext;
    private List<PartnerData> datas;
    public PartnerAdapter(Context context, List<PartnerData> datas){
        this.mContext = context;
        this.datas = datas;
        //this.syncImageLoaderHelper = new SyncImageLoaderHelper(context);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.partner_item , null);
        }
        ImageView imgPhoto = ViewHolderUtil.get(convertView, R.id.imgPhoto);
        TextView txtName =ViewHolderUtil.get(convertView,R.id.txtName);
        TextView yesterdayBrowseAmount=ViewHolderUtil.get(convertView,R.id.yesterdayBrowseAmount);
        TextView historyTotalAmount=ViewHolderUtil.get(convertView,R.id.historyTotalAmount);
        TextView time=ViewHolderUtil.get(convertView,R.id.time);
        PartnerData partnerData=datas.get(position);
        ImageLoad.loadLogo(partnerData.headFace,imgPhoto,mContext);
        txtName.setText("我的伙伴:"+partnerData.userName);
        yesterdayBrowseAmount.setText(partnerData.yesterdayBrowseAmount+"/"+partnerData.historyTotalBrowseAmount+"次");
        historyTotalAmount.setText(partnerData.yesterdayTurnAmount+"/"+partnerData.historyTotalTurnAmount+"次");
        time.setText(partnerData.time);
          return convertView;
    }
}
