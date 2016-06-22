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
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.guide.BitmapData;
import cy.com.morefan.util.RandomColor;
import cy.com.morefan.util.ViewHolderUtil;
import cy.com.morefan.view.ImageLoad;

/**
 * Created by 47483 on 2016/3/25.
 */
public class GroupPersonDataAdapter  extends BaseAdapter {
    private Context mContext;
    private List<GroupPersonData> datas;
    //private SyncImageLoaderHelper syncImageLoaderHelper;


    public GroupPersonDataAdapter(Context context , List<GroupPersonData>  datas){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.department_data_item , null);
        }
        TextView txtName = ViewHolderUtil.get(convertView, R.id.company_data_item_name);
        ImageView imgIcon =  ViewHolderUtil.get(convertView, R.id.company_data_item_icon);
        TextView txtCount = ViewHolderUtil.get(convertView,R.id.company_data_item_label1);

        GroupPersonData data = datas.get(position);

        ImageLoad.loadLogo(data.getLogo(), imgIcon, mContext);
        txtName.setText(data.getName());
        txtCount.setText("转发"+String.valueOf(data.getTotalTurnCount())+"/"+"浏览"+String.valueOf(data.getTotalBrowseCount())+"/"+"伙伴"+String.valueOf(data.getPrenticeCount()));


        //syncImageLoaderHelper.loadImage(position, imgIcon , null, data.getIcon() , Constant.IMAGE_PATH_TASK);
        RandomColor randomColor = new RandomColor();
        int colorid =  randomColor.randomColor( data.getName().hashCode() , RandomColor.SaturationType.RANDOM , RandomColor.Luminosity.RANDOM );
        imgIcon.setBackgroundColor( colorid );

        return convertView;
    }
}
