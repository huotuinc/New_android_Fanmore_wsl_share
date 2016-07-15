package cy.com.morefan;

import java.util.ArrayList;
import java.util.List;

import cy.com.morefan.adapter.FragAdapter;
import cy.com.morefan.adapter.RankAdapter;
import cy.com.morefan.adapter.ViewPagerAdapter;
import cy.com.morefan.bean.RankData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragManager;
import cy.com.morefan.frag.RankItemFrag;
import cy.com.morefan.frag.RankItemFrag.TabType;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.view.PullDownUpListView;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

public class RankActivity extends BaseActivity implements BroadcastListener{
//	private TextView txtPerday;
//	private TextView txtTotalScore;
//	private TextView txtPretice;
	private List<TextView> tabs;
	private int[] tabBgs;
	//private int[] tabUnBgs;
	private ViewPager mViewPager;
	private MyBroadcastReceiver myBroadcastReceiver;
	private Drawable[] tabDrawables;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.rank);
		init();

		int raduis = DensityUtil.dip2px(this, 10);
		 GradientDrawable gdLeft = new GradientDrawable();
		 //左上，右上，右下，左下
		 gdLeft.setCornerRadii(new float[]{raduis,raduis,0,0,0,0,raduis,raduis});
		 gdLeft.setColor(Color.WHITE);

		 GradientDrawable gdMid= new GradientDrawable();
		 gdMid.setColor(Color.WHITE);

		 GradientDrawable gdRight = new GradientDrawable();
//		 //左上，右上，右下，左下
		 gdRight.setCornerRadii(new float[]{0,0,raduis,raduis,raduis,raduis,0,0});
		 gdRight.setColor(Color.WHITE);
		 tabDrawables = new Drawable[]{gdLeft, gdMid, gdRight};


//





		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

	}
	private void init() {
		tabs = new ArrayList<TextView>();
		tabBgs = new int[]{R.drawable.rank_tab_left, R.drawable.rank_tab_mid, R.drawable.rank_tab_right};
		//tabUnBgs = new int[]{R.drawable.rank_tab_left_un, R.drawable.rank_tab_mid_un, R.drawable.rank_tab_right_un};
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		RankItemFrag fragPerday = new RankItemFrag();
		Bundle extraPerday = new Bundle();
		extraPerday.putSerializable(Constant.TYPE_FROM, TabType.Perday);
		fragPerday.setArguments(extraPerday);

//		RankItemFrag fragTotal = new RankItemFrag();
//		Bundle extraTotal = new Bundle();
//		extraTotal.putSerializable(Constant.TYPE_FROM, TabType.Total);
//		fragTotal.setArguments(extraTotal);
//
//		RankItemFrag fragPretice = new RankItemFrag();
//		Bundle extraPretice = new Bundle();
//		extraPretice.putSerializable(Constant.TYPE_FROM, TabType.Pretience);
//		fragPretice.setArguments(extraPretice);

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(fragPerday);
//		fragments.add(fragTotal);
//		fragments.add(fragPretice);
		final FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(adapter);
//		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
//			@Override
//			public void onPageSelected(int positon) {
//				setTab(tabs.get(positon).getId());
//
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		});

	}
//	public void onClick(View v){
//		switch (v.getId()) {
//		case R.id.txtPerday:
//			mViewPager.setCurrentItem(0);
//			setTab(v.getId());
//			break;
//		case R.id.txtTotalScore:
//			mViewPager.setCurrentItem(1);
//			setTab(v.getId());
//			break;
//		case R.id.txtPrentice:
//			mViewPager.setCurrentItem(2);
//			setTab(v.getId());
//			break;
//
//		default:
//			break;
//		}
//
//	}
	private void setTab(int id){
		for(int i = 0, length = tabs.size(); i < length; i++){
			TextView item = tabs.get(i);
			if(id == item.getId()){
				item.setBackgroundDrawable(tabDrawables[i]);
				//item.setBackgroundResource(tabBgs[i]);
				item.setTextColor(getResources().getColor(R.color.theme_blue));
			}else{
				item.setBackgroundDrawable(null);
				item.setTextColor(Color.WHITE);
			}
		}

	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnBack:
				finish();
				FragManager fragManager= new FragManager(this,R.id.layContent);
				fragManager.setCurrentFrag(FragManager.FragType.Task);
				break;
			default:
				break;
		}
	}
	@Override
		protected void onDestroy() {
			if(myBroadcastReceiver != null)
				myBroadcastReceiver.unregisterReceiver();
			super.onDestroy();
		}

}
