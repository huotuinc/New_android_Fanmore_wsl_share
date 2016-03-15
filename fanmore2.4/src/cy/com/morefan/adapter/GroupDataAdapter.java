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

import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.R;
import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.TaskFrag;
import cy.com.morefan.util.RandomColor;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.ViewHolderUtil;

/**
 * Created by Administrator on 2016/3/9.
 */
public class GroupDataAdapter extends BaseAdapter {
    private Context mContext;
    private List<GroupData> datas;
    //private SyncImageLoaderHelper syncImageLoaderHelper;


    public GroupDataAdapter(Context context , List<GroupData>  datas){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.group_data_item , null);
        }
        TextView txtName = ViewHolderUtil.get(convertView, R.id.group_data_item_name);
        ImageView imgIcon =  ViewHolderUtil.get(convertView, R.id.group_data_item_icon);
        TextView txtCount = ViewHolderUtil.get(convertView,R.id.group_data_item_count);

        GroupData data = datas.get(position);

        txtName.setText(data.getName());
        txtCount.setText(String.valueOf(data.getPersonCount()));

        //syncImageLoaderHelper.loadImage(position, imgIcon , null, data.getIcon() , Constant.IMAGE_PATH_TASK);
        RandomColor randomColor = new RandomColor();
        int colorid =  randomColor.randomColor( data.getName().hashCode() , RandomColor.SaturationType.RANDOM , RandomColor.Luminosity.RANDOM );
        imgIcon.setBackgroundColor( colorid );

        return convertView;
    }
}
