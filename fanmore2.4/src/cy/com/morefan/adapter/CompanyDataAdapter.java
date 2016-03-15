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
import cy.com.morefan.util.RandomColor;
import cy.com.morefan.util.ViewHolderUtil;

/**
 * Created by Administrator on 2016/3/9.
 */
public class CompanyDataAdapter extends BaseAdapter {
    private Context mContext;
    private List<GroupData> datas;
    //private SyncImageLoaderHelper syncImageLoaderHelper;


    public CompanyDataAdapter(Context context, List<GroupData> datas){
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.company_data_item , null);
        }
        TextView txtName = ViewHolderUtil.get(convertView, R.id.company_data_item_name);
        ImageView imgIcon =  ViewHolderUtil.get(convertView, R.id.company_data_item_icon);
        TextView txtzfCount = ViewHolderUtil.get(convertView,R.id.company_data_item_count);
        TextView txtlrcount=ViewHolderUtil.get(convertView, R.id.company_data_item_count2);
        TextView txtpersoncount = ViewHolderUtil.get(convertView,R.id.company_data_item_personcount);
        GroupData data = datas.get(position);

        txtName.setText(data.getName());
        int arvcount = data.getPersonCount() ==0 ? 0: data.getTotalTurnCount()/ data.getPersonCount();
        txtzfCount.setText( String.valueOf( data.getTotalTurnCount() ) +"/"+ String.valueOf(arvcount));
        txtpersoncount.setText(String.valueOf(data.getPersonCount()));
        int arvcount2 =data.getPersonCount() ==0 ? 0: data.getTotalBrowseCount()/data.getPersonCount();
        txtlrcount.setText( String.valueOf( data.getTotalBrowseCount() ) +"/"+ String.valueOf( arvcount2));

        //syncImageLoaderHelper.loadImage(position, imgIcon , null, data.getIcon() , Constant.IMAGE_PATH_TASK);
        RandomColor randomColor = new RandomColor();
        int colorid =  randomColor.randomColor( data.getName().hashCode() , RandomColor.SaturationType.RANDOM , RandomColor.Luminosity.RANDOM );
        imgIcon.setBackgroundColor( colorid );

        return convertView;
    }
}
