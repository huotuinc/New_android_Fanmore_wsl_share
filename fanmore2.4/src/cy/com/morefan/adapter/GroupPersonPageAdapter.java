package cy.com.morefan.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cy.com.morefan.bean.GroupData;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.supervision.DepartmentActivity;
import cy.com.morefan.supervision.MasterActivity;

/**
 * Created by 47483 on 2016/3/25.
 */
public class GroupPersonPageAdapter extends PagerAdapter implements AdapterView.OnItemClickListener {
    String[] titles={"默认","转发","浏览","徒弟","积分"};
    List<PullToRefreshListView> listviews;
    View layEmpty;
    List<GroupPersonData> datas;
    //CompanyDataAdapter adapter;
    Context mContext;

    public GroupPersonPageAdapter( Context context, View layEmpty) {
        super();
        this.mContext=context;
        this.layEmpty = layEmpty;

        listviews=new ArrayList<PullToRefreshListView>();
        listviews.add(new PullToRefreshListView(mContext));
        listviews.add(new PullToRefreshListView(mContext));
        listviews.add(new PullToRefreshListView(mContext));
        listviews.add(new PullToRefreshListView(mContext));
        listviews.add(new PullToRefreshListView(mContext));
        listviews.get(0).setMode(PullToRefreshBase.Mode.DISABLED);
        listviews.get(1).setMode(PullToRefreshBase.Mode.DISABLED);
        listviews.get(2).setMode(PullToRefreshBase.Mode.DISABLED);
        listviews.get(3).setMode(PullToRefreshBase.Mode.DISABLED);
        listviews.get(4).setMode(PullToRefreshBase.Mode.DISABLED);
    }

    public void setDatas(List<GroupPersonData> datas){
        this.datas= datas;
        //默认
        GroupPersonDataAdapter dataAdapter0 = new GroupPersonDataAdapter( mContext , datas);
        listviews.get(0).setAdapter(dataAdapter0);
        listviews.get(0).setOnItemClickListener(this);
        //转发
        List<GroupPersonData> datas1 = new ArrayList<GroupPersonData>();
        datas1.addAll(datas);
        GroupPersonDataAdapter dataAdapter1 = new GroupPersonDataAdapter(mContext, datas1);
        listviews.get(1).setAdapter(dataAdapter1);
        listviews.get(1).setOnItemClickListener(this);
        //浏览
        List<GroupPersonData> datas2 = new ArrayList<GroupPersonData>();
        datas2.addAll(datas);
        GroupPersonDataAdapter dataAdapter2 = new GroupPersonDataAdapter(mContext, datas2);
        listviews.get(2).setAdapter(dataAdapter2);
        listviews.get(2).setOnItemClickListener(this);
        //徒弟
        List<GroupPersonData> datas3 = new ArrayList<GroupPersonData>();
        datas3.addAll(datas);
        GroupPersonDataAdapter dataAdapter3 = new GroupPersonDataAdapter(mContext, datas3);
        listviews.get(3).setAdapter(dataAdapter3);
        listviews.get(3).setOnItemClickListener(this);
        //积分
        List<GroupPersonData> datas4 = new ArrayList<GroupPersonData>();
        datas4.addAll(datas);
        GroupPersonDataAdapter dataAdapter4 = new GroupPersonDataAdapter(mContext, datas4);
        listviews.get(4).setAdapter(dataAdapter4);
        listviews.get(4).setOnItemClickListener(this);
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GroupPersonData data = datas.get(position-1);
        Intent intent = new Intent(mContext, MasterActivity.class);
        intent.putExtra("data", data);
        mContext.startActivity(intent);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        //return super.getPageTitle(position);
        return titles[position];
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //return super.instantiateItem(container, position);
        container.addView(listviews.get(position));
        return listviews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView( listviews.get(position) );
    }


}
