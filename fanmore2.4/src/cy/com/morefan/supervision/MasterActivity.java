package cy.com.morefan.supervision;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.FragAdapter;
import cy.com.morefan.bean.GroupPersonData;
import cy.com.morefan.frag.MyTaskFrag;
import cy.com.morefan.frag.PartnerFrag;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.view.CircleImageView;
import cy.com.morefan.view.ImageLoad;


public class MasterActivity extends BaseActivity implements View.OnClickListener, MyBroadcastReceiver.BroadcastListener {
    @BindView(R.id.btnBack)
    public Button btnBack;
    @BindView(R.id.imguser)
    public CircleImageView imguser;
    TextView txt_partner;
    @BindView(R.id.txtTitle)
    TextView txtTitle;
    @BindView(R.id.rw)
    TextView rw;
    @BindView(R.id.hb)
    TextView hb;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.browseCount) TextView browseCount;
    @BindView(R.id.TurnAmount) TextView turnAmount;
    @BindView(R.id.prenticeAmount) TextView prenticeAmount;
    @BindView(R.id.rw_bottom_line)
            ImageView rw_bottom_line;
    @BindView(R.id.hb_bottom_line)
            ImageView hb_bottom_line;
    GroupPersonData groupData;
    private FragAdapter adapter;
    private MyBroadcastReceiver myBroadcastReceiver;
    int userid =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        unbinder = ButterKnife.bind(this);
        hb.setOnClickListener(this);
        rw.setOnClickListener(this);
        imguser.setBorderColor(getResources().getColor(R.color.white));
        imguser.setBorderWidth((int)getResources().getDimension(R.dimen.head_width));

        if( getIntent().hasExtra("data") ) {
            groupData = (GroupPersonData)getIntent().getSerializableExtra("data");
            String title = groupData.getName();
            txtTitle.setText(title);
            if(TextUtils.isEmpty(groupData.getLogo())){
                imguser.setImageResource(R.drawable.user_icon);
            }else{
                ImageLoad.loadLogo(groupData.getLogo(), imguser, this);
            }
            userid = groupData.getUserid();
            if (groupData.getTotalBrowseCount()>=10000){
                double browsecount1 =(double)groupData.getTotalBrowseCount()/10000;
                BigDecimal  b   =   new   BigDecimal(browsecount1);
                double   browsecount2   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
                browseCount.setText(String.valueOf(browsecount2) + "万次");
            }else {
                browseCount.setText(String.valueOf(groupData.getTotalBrowseCount()) + "次");
            }
            if (groupData.getTotalTurnCount()>=10000){
                double browsecount1 =(double)groupData.getTotalTurnCount()/10000;
                BigDecimal  b   =   new   BigDecimal(browsecount1);
                double   browsecount2   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
                turnAmount.setText(String.valueOf(browsecount2) + "万次");
            }else {
                turnAmount.setText(String.valueOf(groupData.getTotalTurnCount()) + "次");
            }
            if (groupData.getPrenticeCount()>=10000){
                double browsecount1 =(double)groupData.getPrenticeCount()/10000;
                BigDecimal  b   =   new   BigDecimal(browsecount1);
                double   browsecount2   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
                prenticeAmount.setText(String.valueOf(browsecount2) + "万人");
            }else {
                prenticeAmount.setText(String.valueOf(groupData.getPrenticeCount()) + "人");
            }
        }


        Fragment rw = new MyTaskFrag();
        Fragment hb = new PartnerFrag();
        Bundle bd=new Bundle();
        bd.putInt("userid",userid);

        hb.setArguments(bd);
        rw.setArguments(bd);

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(rw);
        fragments.add(hb);

        adapter = new FragAdapter(this.getSupportFragmentManager() , fragments);
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        hb_bottom_line.setBackgroundColor(getResources().getColor(R.color.white));
                        rw_bottom_line.setBackgroundColor(getResources().getColor(R.color.theme_back));
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        hb_bottom_line.setBackgroundColor(getResources().getColor(R.color.theme_back));
                        rw_bottom_line.setBackgroundColor(getResources().getColor(R.color.white));
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

    }


    public void onClick(View v ){
        if( v.getId()==R.id.btnBack){
            this.finish();
        }else if(v.getId()==R.id.btnQuery){

        }else if (v.getId()==R.id.hb){
            viewPager.setCurrentItem(1);
            hb_bottom_line.setBackgroundColor(getResources().getColor(R.color.theme_back));
            rw_bottom_line.setBackgroundColor(getResources().getColor(R.color.white));
        }else if (v.getId()==R.id.rw){
            viewPager.setCurrentItem(0);
            hb_bottom_line.setBackgroundColor(getResources().getColor(R.color.white));
            rw_bottom_line.setBackgroundColor(getResources().getColor(R.color.theme_back));
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        myBroadcastReceiver.unregisterReceiver();
    }

    @Override
    public void onFinishReceiver(MyBroadcastReceiver.ReceiverType type, Object msg) {
        if (type == MyBroadcastReceiver.ReceiverType.BackgroundBackToUpdate) {
            finish();

        }
    }
}
