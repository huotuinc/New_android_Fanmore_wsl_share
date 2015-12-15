package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.R;
import cy.com.morefan.TaskDetailActivity;
import cy.com.morefan.adapter.FragAdapter;
import cy.com.morefan.adapter.TaskAdapter;
import cy.com.morefan.adapter.TaskAdapter.TaskAdapterType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragManager.FragType;
import cy.com.morefan.frag.RankItemFrag.TabType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.Shake;
import cy.com.morefan.util.Shake.ShakeListener;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.EmptyView;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class TaskNewFrag extends BaseFragment implements ShakeListener {
	private static TaskNewFrag frag;
	private View mRootView;
	private int taskType;

	private List<TextView> tabs;
	private ViewPager mViewPager;
	private Drawable[] tabDrawables;

	private LinearLayout layMiddle;
	private TextView txtTitle;
	private ImageView imgTag;
	private TaskFrag taskFrag;
	private TaskFrag mallFrag;
	private Shake mShake;
	private UserService mUserService;
	public static TaskNewFrag newInstance(){
		if(frag == null)
			frag = new TaskNewFrag();
		return frag;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//frag.setRetainInstance(true);
		//mUserService = new UserService(this);
		mShake = new Shake(getActivity(), this);

		int raduis = DensityUtil.dip2px(getActivity(), 8);
		 GradientDrawable gdLeft = new GradientDrawable();
		 //左上，右上，右下，左下
		 gdLeft.setCornerRadii(new float[]{raduis,raduis,0,0,0,0,raduis,raduis});
		 gdLeft.setColor(Color.WHITE);

//		 GradientDrawable gdMid= new GradientDrawable();
//		 gdMid.setColor(Color.WHITE);

		 GradientDrawable gdRight = new GradientDrawable();
//		 //左上，右上，右下，左下
		 gdRight.setCornerRadii(new float[]{0,0,raduis,raduis,raduis,raduis,0,0});
		 gdRight.setColor(Color.WHITE);
		 tabDrawables = new Drawable[]{gdLeft, gdRight};
		// setTab(tabs.get(0).getId());
	}
	public void initData(){
		shakeStart();
		taskFrag.initData();
		mallFrag.initData();
	}


	@Override
	public void onResume() {
		shakeStart();
		super.onResume();
	}
	@Override
	public void onReshow() {
		//initData();
		shakeStart();

	}
	private void shakeStart(){
		if(UserData.getUserData().isLogin && !UserData.getUserData().dayCheckIn && getActivity() != null && ((HomeActivity)getActivity()).getCurrentFragType() == FragType.Task)
			mShake.start();
	}
	@Override
	public void onFragPasue() {
		mShake.stop();
	}
	@Override
	public void onPause() {
		mShake.stop();
		super.onPause();
	}
	@Override
	public void onDetach() {
		mShake.stop();
		super.onDetach();
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_task_new, container, false);
		init();
		return mRootView;
	}
	public void init(){
		layMiddle = (LinearLayout) getActivity().findViewById(R.id.layMiddle);
		imgTag = (ImageView) getActivity().findViewById(R.id.imgTag);
		txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);

		tabs = new ArrayList<TextView>();
		TextView txtTask = (TextView) getActivity().findViewById(R.id.txtTask);
		//TextView txtMall = (TextView) getActivity().findViewById(R.id.txtMall);
		tabs.add(txtTask);
		//tabs.add(txtMall);
		mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
		taskFrag = new TaskFrag();
		Bundle extraPerday = new Bundle();
		extraPerday.putSerializable("tabType", TaskFrag.TabType.Task);
		taskFrag.setArguments(extraPerday);

		mallFrag = new TaskFrag();
		Bundle extraPretice = new Bundle();
		extraPretice.putSerializable("tabType", TaskFrag.TabType.Mall);
		mallFrag.setArguments(extraPretice);

		List<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(taskFrag);
		fragments.add(mallFrag);
		final FragAdapter adapter = new FragAdapter(getActivity().getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(adapter);
		setTab(tabs.get(mViewPager.getCurrentItem()).getId());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int positon) {
				if(getActivity() != null)
					((HomeActivity)getActivity()).setDragable(positon == 0);
				setTab(tabs.get(positon).getId());
				setTitle(positon);

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
	private void setTab(int id){
		for(int i = 0, length = tabs.size(); i < length; i++){
			TextView item = tabs.get(i);
			if(id == item.getId()){
				//item.setBackgroundDrawable(tabDrawables[i]);
				//item.setBackgroundResource(tabBgs[i]);
				item.setTextColor(getResources().getColor(R.color.white));
			}else{
				item.setBackgroundDrawable(null);
				item.setTextColor(getResources().getColor(R.color.white));
			}
		}

	}

	private void setTitle(int positon) {
		if(positon == 0){
			txtTitle.setText(taskFrag.getTitleText());
			layMiddle.setClickable(true);
			imgTag.setVisibility(View.VISIBLE);
		}else{
			txtTitle.setText(getActivity().getResources().getString(R.string.app_title_name));
			layMiddle.setClickable(false);
			imgTag.setVisibility(View.GONE);
		}

	}


	public int getCurrentTab(){
		return mViewPager.getCurrentItem();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRight:
			//提现
			if(getActivity() != null)
				((HomeActivity) getActivity()).toCrash();
			break;
		case R.id.listView:
			if(getActivity() != null && ((HomeActivity)getActivity()).isOpened())
				((HomeActivity)getActivity()).closeMenu();
			break;
		case R.id.txtTask:
			if(mViewPager.getCurrentItem() == 0)
				taskFrag.onClickTitleMiddle();
			mViewPager.setCurrentItem(0);
			//setTab(v.getId());
			setTitle(0);
			break;
//		case R.id.txtMall:
//			mViewPager.setCurrentItem(1);
//			setTab(v.getId());
//			setTitle(1);
//			break;

		default:
			break;
		}

	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	private String currentTitle;
	public String getTitleText() {
		if(TextUtils.isEmpty(currentTitle))
			currentTitle = getActivity().getResources().getString(R.string.app_title_name);
		return currentTitle;

	}
	@Override
	public void onShake() {
		if(null != getActivity())
			((HomeActivity)getActivity()).checkIn();
	}

	private void showProgress(){
		if(null != getActivity())
			((HomeActivity)getActivity()).showProgress();
	}
	private void dismissProgress(){
		if(null != getActivity())
			((HomeActivity)getActivity()).dismissProgress();
	}
	private void toast(String msg){
		if(null != getActivity())
			((HomeActivity)getActivity()).toast(msg);
	}



}
