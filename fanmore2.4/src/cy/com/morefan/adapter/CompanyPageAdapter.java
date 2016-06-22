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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cy.com.morefan.bean.GroupData;
import cy.com.morefan.supervision.CompanyActivity;
import cy.com.morefan.supervision.DepartmentActivity;

/**
 * Created by Administrator on 2016/3/10.
 */
public class CompanyPageAdapter extends PagerAdapter{
    String[] titles={"默认","转发","浏览"};
    List<PullToRefreshListView> listviews;
    View layEmpty;
    List<GroupData> datas;
    //CompanyDataAdapter adapter;
    Context mContext;

    public CompanyPageAdapter( Context context, View layEmpty) {
        super();
        this.mContext=context;
        this.layEmpty = layEmpty;

        listviews=new ArrayList<PullToRefreshListView>();
        listviews.add(new PullToRefreshListView(mContext));
        listviews.add(new PullToRefreshListView(mContext));
        listviews.add(new PullToRefreshListView(mContext));
        listviews.get(0).setMode(PullToRefreshBase.Mode.DISABLED);
        listviews.get(1).setMode(PullToRefreshBase.Mode.DISABLED);
        listviews.get(2).setMode(PullToRefreshBase.Mode.DISABLED);
    }

    public void setDatas(List<GroupData> datas){
        this.datas= datas;

        CompanyDataAdapter dataAdapter0 = new CompanyDataAdapter( mContext , datas);
        listviews.get(0).setAdapter(dataAdapter0);

        List<GroupData> datas1 = new ArrayList<GroupData>();
        datas1.addAll(datas);
        //Collections.copy(datas1,datas);
        Collections.sort(datas1, new ZFComparar());
        CompanyDataAdapter dataAdapter1 = new CompanyDataAdapter(mContext, datas1);
        listviews.get(1).setAdapter(dataAdapter1);

        List<GroupData> datas2 = new ArrayList<GroupData>();
        datas2.addAll(datas);
        //Collections.copy(datas2, datas);
        Collections.sort(datas2, new LRComparar());
        CompanyDataAdapter dataAdapter2 = new CompanyDataAdapter(mContext, datas2);
        listviews.get(2).setAdapter(dataAdapter2);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        //return super.getPageTitle(position);
        return titles[position];
    }

    @Override
    public int getCount() {
        return 3;
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

    class ZFComparar implements Comparator<GroupData> {
        @Override
        public int compare(GroupData lhs, GroupData rhs) {
            Integer c1 = lhs.getPersonCount() ==0 ? 0: lhs.getTotalTurnCount()/lhs.getPersonCount();
            Integer c2 = rhs.getPersonCount() ==0? 0: rhs.getTotalTurnCount()/rhs.getPersonCount();
            return c1.compareTo(c2);
        }
    }
    class LRComparar implements Comparator<GroupData> {
        @Override
        public int compare(GroupData lhs, GroupData rhs) {
            Integer c1 =lhs.getPersonCount() ==0 ? 0:  lhs.getTotalBrowseCount()/lhs.getPersonCount();
            Integer c2 =rhs.getPersonCount() ==0 ? 0:  rhs.getTotalBrowseCount()/rhs.getPersonCount();
            return c1.compareTo(c2);
        }
    }
}
