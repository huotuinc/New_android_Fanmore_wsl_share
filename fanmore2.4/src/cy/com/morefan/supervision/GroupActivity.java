package cy.com.morefan.supervision;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.huibin.androidsegmentcontrol.SegmentControl;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.FragAdapter;
import cy.com.morefan.frag.ArchitectureFrag;
import cy.com.morefan.frag.GroupTaskFrag;
import cy.com.morefan.view.CyButton;

/**
 *
 */
public class GroupActivity extends BaseActivity implements SegmentControl.OnSegmentControlClickListener {
    @Bind(R.id.btnBack)
    public Button btnBack;
    @Bind(R.id.btnQuery)
    public CyButton btnQuery;
    FragAdapter adapter;
    int taskId=0;
    @Bind(R.id.segment_control)
    public SegmentControl segment_control;


    private ArrayList<Fragment> list = null;
    @Bind(R.id.myviewpager)
    public ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        segment_control.setOnSegmentControlClickListener(this);
        initViewPager();
    }
    private void initViewPager() {
        Fragment jg = ArchitectureFrag.newInstance();
        Fragment rw = GroupTaskFrag.newInstance();



        list = new ArrayList<Fragment>();

        list.add(jg);
        list.add(rw);


        mViewPager.setAdapter(new FragAdapter(getSupportFragmentManager(),list));
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyViewPagerChangedListener());


    }

    public void setFrag( ){
        mViewPager.setCurrentItem(0);

    }



    class MyViewPagerChangedListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }



        @Override
        public void onPageSelected(int arg0) {
            if (arg0==0){
                segment_control.setCurrentIndex(0);
            }
            else if (arg0==1){
                segment_control.setCurrentIndex(1);
            }
        }


    }



    public void onClick(View view){
        if(view.getId()==R.id.btnBack){
            this.finish();
        }else if(view.getId()==R.id.btnQuery){

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }




    @Override
    public void onSegmentControlClick(int index) {
        switch (index){
            case 0:
                mViewPager.setCurrentItem(0);
                return;
            case 1:
                mViewPager.setCurrentItem(1);
                return;
        }

    }
}
