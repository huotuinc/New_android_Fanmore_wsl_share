package cy.com.morefan;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cy.com.morefan.adapter.HomeViewPagerAdapter;
import cy.com.morefan.bean.AdlistModel;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.VolleyUtil;

public class AdActivity extends BaseActivity {


    @BindView(R.id.skipText)
    TextView skipText;
    @BindView(R.id.homeViewPager)
    ViewPager homeViewPager;

    @BindView(R.id.dot)
    LinearLayout dot;
    List<AdlistModel> adDataList = null;
    int recLen = BusinessStatic.getInstance().adTime;
    int itemtime=0;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0: {
                if (homeViewPager.getCurrentItem()+1==adDataList.size()||adDataList.size()==1)
                {
                    mHandler.sendEmptyMessageDelayed(0, adDataList.get(homeViewPager.getCurrentItem()).getItemShowTime()*1000);
                    handler.removeCallbacks(runnable);
                    if(!TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME)) &&
                            !TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD))) {

                        ActivityUtils.getInstance().skipActivity(AdActivity.this, HomeActivity.class);
                    } else {
                        ActivityUtils.getInstance().skipActivity(AdActivity.this, MoblieLoginActivity.class);
                    }
                }else {
                    homeViewPager.setCurrentItem(homeViewPager.getCurrentItem() + 1);
                    mHandler.sendEmptyMessageDelayed(0, adDataList.get(homeViewPager.getCurrentItem()).getItemShowTime()*1000);
                }

            }
            break;
                case Constant.CAROUSE_URL:
                    handler.removeCallbacks(runnable);
                    mHandler.removeMessages(0);
                    Bundle bundle=new Bundle();
                 bundle.putString("url" ,  adDataList.get(homeViewPager.getCurrentItem()).getItemImgDescLink() );
                    if(!TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME)) &&
                            !TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD))) {

                        ActivityUtils.getInstance().skipActivity(AdActivity.this,HomeActivity.class,bundle);
                    } else {
                        ActivityUtils.getInstance().skipActivity(AdActivity.this, MoblieLoginActivity.class,bundle);
                    }

                default:
                    break;
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        unbinder= ButterKnife.bind(this);
        adDataList = (List<AdlistModel>) getIntent().getSerializableExtra("data");
        itemtime =adDataList.get(homeViewPager.getCurrentItem()).getItemShowTime();
        skipText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeMessages(0);
                handler.removeCallbacks(runnable);
              if(!TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME)) &&
                        !TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD))) {

                    ActivityUtils.getInstance().skipActivity(AdActivity.this, HomeActivity.class);
                } else {
                    ActivityUtils.getInstance().skipActivity(AdActivity.this, MoblieLoginActivity.class);
                }
            }
        });
        asyncGetScrollImage();
        handler.postDelayed(runnable, 0);
    }



    protected void asyncGetScrollImage(){
                //读取轮播图片实体
//                Iterator<AdlistModel> iterator = AdlistModel.findAll(AdlistModel.class);
//                while (iterator.hasNext()) {
//                    adDataList.add(iterator.next());
//                }
                initSwitchImg();
    }
    private void initSwitchImg() {

        if(adDataList==null || adDataList.size()<1 ) return;

        initDots();
        //通过适配器引入图片
        homeViewPager.setAdapter(new HomeViewPagerAdapter(adDataList, this, this.mHandler));
        homeViewPager.setCurrentItem(0);
        initListener();
        //更新文本内容
        updateTextAndDot();
        mHandler.sendEmptyMessageDelayed(0, adDataList.get(homeViewPager.getCurrentItem()).getItemShowTime()*1000);
    }

    /**
     * 初始化监听器
     */
    @SuppressWarnings("deprecation")
    private void initListener() {
        homeViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                updateTextAndDot();

            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // TODO Auto-generated method stub

            }
        });
    }
    private void updateTextAndDot() {
        int currentPage = homeViewPager.getCurrentItem();

        //改变dot
        for (int i = 0; i < dot.getChildCount(); i++) {
            dot.getChildAt(i).setEnabled(i == currentPage);
        }

    }
    private void initDots() {
        for (int i = 0; i < adDataList.size(); i++) {
            View view = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            if (i != 0) {
                params.leftMargin = 5;
            }

            view.setLayoutParams(params);
            view.setBackgroundResource(R.drawable.selecter_dot);
            dot.addView(view);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ButterKnife.unbind(this);
        VolleyUtil.cancelAllRequest();
        handler.removeCallbacks(runnable);
        if (null != mHandler) {
            mHandler.removeMessages(200);
            mHandler.removeMessages(0);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen--;
            if(recLen > 0){
                skipText.setText(String.format("%d秒后跳过", recLen));
                handler.postDelayed(this, 1000);
            }else if(recLen == 0){
                if(!TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME)) &&
                        !TextUtils.isEmpty(SPUtil.getStringToSpByName(AdActivity.this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD))) {

                    ActivityUtils.getInstance().skipActivity(AdActivity.this, HomeActivity.class);
                } else {
                    ActivityUtils.getInstance().skipActivity(AdActivity.this, MoblieLoginActivity.class);
                }
            }

        }
    };

}
