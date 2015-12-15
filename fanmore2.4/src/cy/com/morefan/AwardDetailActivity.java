package cy.com.morefan;

import java.util.ArrayList;
import java.util.List;

import cy.com.morefan.adapter.FragAdapter;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.constant.Constant.FromType;
import cy.com.morefan.frag.PartInDetailItemFrag;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.util.L;
import cy.com.morefan.util.TimeUtil;
import android.R.integer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TextView;

/**
 * 总积分:显示指定日期、历史
 * 我的参与：历史
 * 昨日收益： 昨日，历史
 * @author cy
 *
 */
public class AwardDetailActivity extends BaseActivity implements BroadcastListener {

	public final static String TASK_ID = "TASK_ID";
	public final static String DATE = "DATE";
	public final static String PAGE_INDEX = "PAGE_INDEX";
	private TaskData taskData;
	private MyBroadcastReceiver myBroadcastReceiver;
	private ViewPager mViewPager;
	private List<TextView> tabs;
	private FromType mFromType;
	private int taskId;
	private String date;



	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.user_partin_detail);
		mFromType = (FromType) getIntent().getExtras().getSerializable(Constant.TYPE_FROM);
		taskId = getIntent().getExtras().getInt(TASK_ID);
		date = getIntent().getExtras().getString(DATE);
		L.i(">>>>>>>>fromType:" + mFromType);

		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		setViewByType();
		myBroadcastReceiver = new MyBroadcastReceiver(this, this,MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}

	private void setViewByType() {
		//累计收益，我的参与时不显示
		findViewById(R.id.layTotal).setVisibility(mFromType == FromType.MyPartIn ? View.GONE : View.VISIBLE);
		findViewById(R.id.layTabs).setVisibility(mFromType == FromType.MyPartIn ? View.GONE : View.VISIBLE);

		TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
		txtTitle.setText(getTitleByType());
		if(mFromType != FromType.MyPartIn){
			TextView txtDay = (TextView) findViewById(R.id.txtDay);
			String dayTitle = mFromType == FromType.YesAward ? "昨日收益" : TimeUtil.FormatterTimeByMonthAndDay2(date) + "收益";
			txtDay.setText(dayTitle);
			tabs = new ArrayList<TextView>();
			txtDay.setTag(0);
			tabs.add(txtDay);
			TextView txtTotal = (TextView) findViewById(R.id.txtTotal);
			txtTotal.setTag(1);
			tabs.add(txtTotal);
		}


		int pageCount = mFromType == FromType.MyPartIn ? 1 : 2;
		List<Fragment> fragments = new ArrayList<Fragment>();
		for(int i = 0; i < pageCount; i++){
			Bundle argsDay = new Bundle();
			argsDay.putInt(TASK_ID, taskId);
			argsDay.putString(DATE, date);
			argsDay.putSerializable(Constant.TYPE_FROM, mFromType);
			argsDay.putInt(PAGE_INDEX, i);
			PartInDetailItemFrag frag = new PartInDetailItemFrag();
			frag.setArguments(argsDay);
			fragments.add(frag);
		}
		FragAdapter adapter = new FragAdapter(getSupportFragmentManager(),fragments);
		mViewPager.setAdapter(adapter);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setCurrentItem(0);
		setTabs(0);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				setTabs(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});




	}

	private String getTitleByType() {
		switch (mFromType) {
		case MyPartIn:
			return "收益详情";
		case TotalScore:
			return TimeUtil.FormatterTime(date);
		case YesAward:
			return "昨日收益";

		default:
			break;
		}
		return null;
	}

	private void setTabs(int position) {
		if(tabs == null)
			return;
		for (TextView item : tabs) {
			item.setBackgroundResource(R.drawable.partin_detail_tab_off);
			item.setTextColor(Color.BLACK);
			if (Integer.valueOf(item.getTag().toString()) == position) {
				item.setTextColor(Color.WHITE);
				item.setBackgroundResource(R.drawable.partin_detail_tab_on);
			}

		}

	}


	@Override
	protected void onDestroy() {
		dismissProgress();
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}

	public void onClickButton(View v) {
		switch (v.getId()) {
		case R.id.btnBack:
			finish();
			break;
//		case R.id.btnTask:
//			Intent intentTask = new Intent(this, TaskDetailActivity.class);
//			intentTask.putExtra("taskData", taskData);
//			startActivity(intentTask);
//			break;
		case R.id.txtDay:
			mViewPager.setCurrentItem(0);
			setTabs(0);
			break;
		case R.id.txtTotal:
			mViewPager.setCurrentItem(1);
			setTabs(1);
			break;

		default:
			break;
		}
	}

	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if (type == ReceiverType.BackgroundBackToUpdate) {
			finish();
		}

	}


}
