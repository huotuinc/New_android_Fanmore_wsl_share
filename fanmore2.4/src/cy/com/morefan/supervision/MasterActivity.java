package cy.com.morefan.supervision;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
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
    @Bind(R.id.btnBack)
    public Button btnBack;
    @Bind(R.id.imguser)
    public CircleImageView imguser;
    TextView txt_partner;
    @Bind(R.id.txtTitle)
    TextView txtTitle;
    @Bind(R.id.rw)
    TextView rw;
    @Bind(R.id.hb)
    TextView hb;
    @Bind(R.id.viewPager)
    ViewPager viewPager;
    @Bind(R.id.browseCount) TextView browseCount;
    @Bind(R.id.TurnAmount) TextView turnAmount;
    @Bind(R.id.prenticeAmount) TextView prenticeAmount;
    @Bind(R.id.rw_bottom_line)
            ImageView rw_bottom_line;
    @Bind(R.id.hb_bottom_line)
            ImageView hb_bottom_line;
    GroupPersonData groupData;
    private FragAdapter adapter;
    private MyBroadcastReceiver myBroadcastReceiver;
    int userid =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        ButterKnife.bind(this);
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
            browseCount.setText(String.valueOf(groupData.getTotalBrowseCount())+"次");
            turnAmount.setText(String.valueOf(groupData.getTotalTurnCount())+"次");
            prenticeAmount.setText(String.valueOf(groupData.getPrenticeCount())+"人");
        }


        Fragment rw = new MyTaskFrag();
        Fragment hb = new PartnerFrag();
        Bundle bd=new Bundle();
        bd.putInt("userid",userid);

        hb.setArguments(bd);

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
        ButterKnife.unbind(this);
        myBroadcastReceiver.unregisterReceiver();
    }

    @Override
    public void onFinishReceiver(MyBroadcastReceiver.ReceiverType type, Object msg) {
        if (type == MyBroadcastReceiver.ReceiverType.BackgroundBackToUpdate) {
            finish();

        }
    }
}
