package cy.com.morefan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import cy.com.morefan.R;
import cy.com.morefan.bean.WeekTaskData;

/**
 * Created by 47483 on 2016/6/20.
 */

public class WeekTaskAdapter extends BaseAdapter {

    private Context mContext;
    private List<WeekTaskData> datas;
    public WeekTaskAdapter(Context mContext, List<WeekTaskData> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }
    public void setDatas(List<WeekTaskData> datas2) {
        this.datas = datas2;
        this.notifyDataSetChanged();

    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.week_task_item, null);
            convertView.setTag(new ViewHolder(convertView));

        }
        WeekTaskData data = datas.get(position);
        ViewHolder viewHolder= (ViewHolder) convertView.getTag();
        viewHolder.title.setText(data.title);

        if (data.myFinishCount*100/data.target>=100){
            viewHolder.finishCount.setText("已完成");
            viewHolder.target.setVisibility(View.GONE);
        }else {
            viewHolder.target.setVisibility(View.VISIBLE);
            viewHolder.finishCount.setText(String.valueOf(data.myFinishCount));
            viewHolder.target.setText("/"+String.valueOf(data.target));
        }
        viewHolder.progress.setProgress((data.myFinishCount*100/data.target));
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.finishCount) TextView finishCount;
        @BindView(R.id.target) TextView target;
        @BindView(R.id.Progress)
        ProgressBar progress;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

