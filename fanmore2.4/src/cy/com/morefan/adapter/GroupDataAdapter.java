package cy.com.morefan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;

import cy.com.morefan.R;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.TaskFrag;
import cy.com.morefan.util.RandomColor;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.ImageLoad;

/**
 * Created by Administrator on 2016/3/9.
 */
public class GroupDataAdapter extends BaseAdapter {
    private Context mContext;
    private List<GroupData> datas;
    private List<GroupPersonData> groupPersonDatas;
    //private SyncImageLoaderHelper syncImageLoaderHelper;


    public GroupDataAdapter(Context context , List<GroupData>  datas,List<GroupPersonData> groupPersonDatas){
        this.mContext = context;
        this.datas = datas;
        this.groupPersonDatas = groupPersonDatas;
        //this.syncImageLoaderHelper = new SyncImageLoaderHelper(context);
    }
    @Override
    public int getCount() {
        return datas.size()+groupPersonDatas.size();
    }

    @Override
    public Object getItem(int position) {
        if (position>datas.size()){
            return groupPersonDatas.get(position-datas.size());
        }else {
            return datas.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position<datas.size()) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_data_item, null);
            TextView txtName = ViewHolderUtil.get(convertView, R.id.group_data_item_name);
            ImageView imgIcon = ViewHolderUtil.get(convertView, R.id.group_data_item_icon);
            TextView txtCount = ViewHolderUtil.get(convertView, R.id.group_data_item_count);
            TextView txttb = ViewHolderUtil.get(convertView, R.id.txttb);
            GroupData data = datas.get(position);
            txtName.setText(data.getName());
            if (data.getTotalTurnCount()>=100000){

                txtCount.setText("总转发量10万+");
            }else {
                txtCount.setText("总转发量" + data.getTotalTurnCount());
            }
            //txttb.setText("总转发" + data.getTotalTurnCount() + "/" + "总浏览" + data.getTotalBrowseCount());

        }
        else if (position>=datas.size()){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.department_data_item, null);
            TextView txtName = ViewHolderUtil.get(convertView, R.id.company_data_item_name);
            ImageView imgIcon = ViewHolderUtil.get(convertView, R.id.company_data_item_icon);
            TextView txtdata = ViewHolderUtil.get(convertView, R.id.company_data_item_label1);
                GroupPersonData data = groupPersonDatas.get(position-datas.size());
                txtName.setText(data.getName());
            txtdata.setText("转发"+data.getTotalTurnCount()+"/浏览"+data.getTotalBrowseCount()+"/伙伴"+data.getPrenticeCount());
                if (data.getLogo().isEmpty()){
                    imgIcon.setImageResource(R.drawable.prentice_photo);
                }else {
                    ImageLoad.loadLogo(data.getLogo(), imgIcon, mContext);
                }
        }
        //syncImageLoaderHelper.loadImage(position, imgIcon , null, data.getIcon() , Constant.IMAGE_PATH_TASK);

        return convertView;

    }


}
