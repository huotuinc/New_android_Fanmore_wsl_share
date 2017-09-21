package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.HomeActivity;

import cy.com.morefan.R;
import cy.com.morefan.SearchActivity;
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
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.DensityUtil;

import android.content.res.Resources;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

public class TaskNewFrag extends BaseFragment  {
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
    private ImageView iv_bottom_line;

    private Resources resources;

    private TextView mr;

//    private TextView jljf;
//
//    private TextView zfrs;
//
//    private TextView syjf;

    private TextView sx;

    private int taskStaus=1;

    private TextView doing;

    private TextView done;
    //private Shake mShake;
    private UserService mUserService;
    Bundle mrextra = new Bundle();
    Bundle jljfextra = new Bundle();
    Bundle zfrsextra = new Bundle();
    Bundle syjfextra = new Bundle();
     FragAdapter adapter;
    int userid=0;

    public static TaskNewFrag newInstance() {
        if (frag == null)
            frag = new TaskNewFrag();
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //frag.setRetainInstance(true);
        //mUserService = new UserService(this);
        //mShake = new Shake(getActivity(), this);

        int raduis = DensityUtil.dip2px(getActivity(), 8);
        GradientDrawable gdLeft = new GradientDrawable();
        //左上，右上，右下，左下
        gdLeft.setCornerRadii(new float[]{raduis, raduis, 0, 0, 0, 0, raduis, raduis});
        gdLeft.setColor(Color.WHITE);

//		 GradientDrawable gdMid= new GradientDrawable();
//		 gdMid.setColor(Color.WHITE);

        GradientDrawable gdRight = new GradientDrawable();
//		 //左上，右上，右下，左下
        gdRight.setCornerRadii(new float[]{0, 0, raduis, raduis, raduis, raduis, 0, 0});
        gdRight.setColor(Color.WHITE);
        tabDrawables = new Drawable[]{gdLeft, gdRight};
        // setTab(tabs.get(0).getId());
    }

    public void initData() {
        //shakeStart();
        taskFrag.initData();

    }


    @Override
    public void onResume() {
        //shakeStart();
        super.onResume();
    }

    @Override
    public void onReshow() {
        //initData();
        //shakeStart();

    }

    //	private void shakeStart(){
//		if(UserData.getUserData().isLogin && !UserData.getUserData().dayCheckIn && getActivity() != null && ((HomeActivity)getActivity()).getCurrentFragType() == FragType.Task)
//			mShake.start();
//	}
    @Override
    public void onFragPasue() {
        //mShake.stop();
    }

    @Override
    public void onPause() {
        //mShake.stop();
        super.onPause();
    }

    @Override
    public void onDetach() {
        //mShake.stop();
        super.onDetach();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.tab_task_new, container, false);
        init();
        initWidth();
        return mRootView;
    }

    public void init() {
        layMiddle = (LinearLayout) getActivity().findViewById(R.id.layMiddle);
        imgTag = (ImageView) getActivity().findViewById(R.id.imgTag);
        txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
        doing=(TextView)mRootView.findViewById(R.id.doing);
        done=(TextView)mRootView.findViewById(R.id.done);
        //tabs = new ArrayList<TextView>();
        mr = (TextView) mRootView.findViewById(R.id.mr);
//        jljf = (TextView) mRootView.findViewById(R.id.jljf);
//        zfrs = (TextView) mRootView.findViewById(R.id.zfrs);
//        syjf = (TextView) mRootView.findViewById(R.id.syjf);
        sx =(TextView) mRootView.findViewById(R.id.sx);
        iv_bottom_line = (ImageView) mRootView.findViewById(R.id.iv_bottom_line);
        mr.setOnClickListener(new MyClickListener(0));
        //jljf.setOnClickListener(new MyClickListener(3));
        //zfrs.setOnClickListener(new MyClickListener(1));
        //syjf.setOnClickListener(new MyClickListener(1));
        sx.setOnClickListener(new MyClickListener(1));
        TextView txtTask = (TextView) getActivity().findViewById(R.id.txtTask);
        //TextView txtMall = (TextView) getActivity().findViewById(R.id.txtMall);
//		tabs.add(txtTask);
        //tabs.add(txtMall);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        taskFrag = new TaskFrag();

        mrextra.putSerializable("tabType", TaskFrag.TabType.mr);

//        jljfextra.putSerializable("tabType", TaskFrag.TabType.jljf);
//
//        zfrsextra.putSerializable("tabType", TaskFrag.TabType.zfrs);
//
//        syjfextra.putSerializable("tabType", TaskFrag.TabType.syjf);
//		mallFrag = new TaskFrag();
//		Bundle extraPretice = new Bundle();
//		extraPretice.putSerializable("tabType", TaskFrag.TabType.Mall);
//		mallFrag.setArguments(extraPretice);
        Fragment mr = new TaskFrag();
//        mr.setArguments(mrextra);
//        Fragment jljf = new TaskFrag();
//        jljf.setArguments(jljfextra);
//        Fragment zfrs = new TaskFrag();
//        zfrs.setArguments(zfrsextra);
//        Fragment syjf = new TaskFrag();
//        syjf.setArguments(syjfextra);
        Fragment sx   = new SelectionFrag();

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(mr);
        //fragments.add(jljf);
        //fragments.add(zfrs);
        //fragments.add(syjf);
        fragments.add(sx);
        //fragments.add(mallFrag);
        adapter = new FragAdapter(this.getChildFragmentManager() , fragments);
        mViewPager.setAdapter(adapter);

        //setTab(tabs.get(mViewPager.getCurrentItem()).getId());
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new MyViewPagerChangedListener());

    }

    private int first = 0;
    private int second = 0;
    private int third = 0;
    private int fourth = 0;

    private void initWidth() {
        int lineWidth = iv_bottom_line.getLayoutParams().width;
        Log.d("lineWidth ", lineWidth + "");
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        resources = getResources();

        first = width / 2;
        second = first * 2;
        third = first * 3;
        fourth = first* 4;

    }

    private int currPosition = 0;

    class MyViewPagerChangedListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {

//				if(getActivity() != null)
//					((HomeActivity)getActivity()).setDragable(arg0 == 0);
//				setTab(tabs.get(0).getId());
//				setTitle(0);
            TranslateAnimation ta = null;


            switch (arg0) {

                case 0:

                    if (currPosition == 1) {
                        ta = new TranslateAnimation(first, 0, 0, 0);
                    }
//                    if (currPosition == 3) {
//                        ta = new TranslateAnimation(third, 0, 0, 0);
//                    }
//                    if (currPosition == 4) {
//                        ta = new TranslateAnimation(fourth, 0, 0, 0);
//                    }

                    ((TaskFrag) adapter.getItem( mViewPager.getCurrentItem())).setTaskStatus(taskStaus);
                    break;

//                case 1:
//
//                    if (currPosition == 0) {
//                        ta = new TranslateAnimation(0, first, 0, 0);
//                    }
//                    if (currPosition == 2) {
//                        ta = new TranslateAnimation(second, first, 0, 0);
//                    }
////                    if (currPosition == 3) {
////                        ta = new TranslateAnimation(third, first, 0, 0);
////                    }
////                    if (currPosition == 4) {
////                        ta = new TranslateAnimation(fourth, first, 0, 0);
////                    }
//
//                    ((TaskFrag) adapter.getItem( mViewPager.getCurrentItem())).setTaskStatus(taskStaus);
//                    ((TaskFrag)adapter.getItem(mViewPager.getCurrentItem())).setUserid(userid);
//                    break;

                case 1:
                    if (currPosition == 0) {
                        ta = new TranslateAnimation(0, first, 0, 0);
                    }
//                    if (currPosition == 1) {
//                        ta = new TranslateAnimation(first, second, 0, 0);
//                    }
//                    if (currPosition == 3) {
//                        ta = new TranslateAnimation(third, second, 0, 0);
//                    }
//                    if (currPosition == 4) {
//                        ta = new TranslateAnimation(fourth, second, 0, 0);
//                    }
                    ((SelectionFrag) adapter.getItem( mViewPager.getCurrentItem())).initData();
//                    ((TaskFrag) adapter.getItem( mViewPager.getCurrentItem())).setTaskStatus(taskStaus);
//                    ((TaskFrag)adapter.getItem(mViewPager.getCurrentItem())).setUserid(userid);
                    break;

//                case 3:
//                    if (currPosition == 0) {
//                        ta = new TranslateAnimation(0, third, 0, 0);
//                    }
//                    if (currPosition == 1) {
//                        ta = new TranslateAnimation(first, third, 0, 0);
//                    }
//                    if (currPosition == 2) {
//                        ta = new TranslateAnimation(second, third, 0, 0);
//                    }
//                    if (currPosition == 4) {
//                        ta = new TranslateAnimation(fourth, third, 0, 0);
//                    }
//
//                    ((TaskFrag) adapter.getItem( mViewPager.getCurrentItem())).setTaskStatus(taskStaus);
//                    ((TaskFrag)adapter.getItem(mViewPager.getCurrentItem())).setUserid(userid);
//                    break;
//                case 4:
//                    if (currPosition == 0) {
//                        ta = new TranslateAnimation(0, fourth, 0, 0);
//                    }
//                    if (currPosition == 1) {
//                        ta = new TranslateAnimation(first, fourth, 0, 0);
//                    }
//                    if (currPosition == 2) {
//                        ta = new TranslateAnimation(second, fourth, 0, 0);
//                    }
//                    if (currPosition == 3) {
//                        ta = new TranslateAnimation(third, fourth, 0, 0);
//                    }
//                    ((SelectionFrag) adapter.getItem( mViewPager.getCurrentItem())).initData();
//                    break;


            }

            currPosition = arg0;

            if(ta!=null) {
                ta.setDuration(300);
                ta.setFillAfter(true);
                iv_bottom_line.startAnimation(ta);
            }

        }


        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    }

    public void setTab(int id,int userid) {
//        for (int i = 0, length = tabs.size(); i < length; i++) {
//            TextView item = tabs.get(i);
//            if (id == item.getId()) {
//                //item.setBackgroundDrawable(tabDrawables[i]);
//                //item.setBackgroundResource(tabBgs[i]);
//                item.setTextColor(getResources().getColor(R.color.white));
//            } else {
//                item.setBackgroundDrawable(null);
//                item.setTextColor(getResources().getColor(R.color.white));
//            }
//        }

        mViewPager.setCurrentItem(id);

        this.userid = userid;

    }

    class MyClickListener implements OnClickListener {

        private int index = 0;

        public MyClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mViewPager.setCurrentItem(index);


        }

    }

    private void setTitle(int positon) {
        if (positon == 0) {
            txtTitle.setText(taskFrag.getTitleText());
            layMiddle.setClickable(true);
            imgTag.setVisibility(View.VISIBLE);
        } else {
            txtTitle.setText(getActivity().getResources().getString(R.string.app_title_name));
            layMiddle.setClickable(false);
            imgTag.setVisibility(View.GONE);
        }

    }


    public int getCurrentTab() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRight:
                //搜索
                Bundle bundle =new Bundle();
                bundle.putInt("type",0);
                ActivityUtils.getInstance().showActivity(getActivity(), SearchActivity.class,bundle);
                break;
            case R.id.listView:
                if (getActivity() != null && ((HomeActivity) getActivity()).isOpened())
                    ((HomeActivity) getActivity()).closeMenu();
                break;
            case R.id.txtTask:
                if (mViewPager.getCurrentItem() == 0)
                    //taskFrag.onClickTitleMiddle();
                    mViewPager.setCurrentItem(0);
                //setTab(v.getId());
                setTitle(0);
                break;
//            case R.id.doing:
//                taskStaus = 1;
//                if (mViewPager.getCurrentItem()==4){
//                    doing.setTextColor(resources.getColor(R.color.theme_blue));
//                    doing.setBackgroundColor(resources.getColor(R.color.white));
//                    done.setTextColor(resources.getColor(R.color.black));
//                    done.setBackgroundColor(resources.getColor(R.color.gray1));
//                }
//                else {
//                    ((TaskFrag) adapter.getItem(mViewPager.getCurrentItem())).setTaskStatus(1);
//                    ((TaskFrag)adapter.getItem(mViewPager.getCurrentItem())).setUserid(userid);
//                    doing.setTextColor(resources.getColor(R.color.theme_blue));
//                    doing.setBackgroundColor(resources.getColor(R.color.white));
//                    done.setTextColor(resources.getColor(R.color.black));
//                    done.setBackgroundColor(resources.getColor(R.color.gray1));
//                    ((TaskFrag) adapter.getItem(mViewPager.getCurrentItem())).initData();
//                }
//                break;
//            case R.id.done:
//                taskStaus = 0;
//                if (mViewPager.getCurrentItem()==4){
//                    done.setTextColor(resources.getColor(R.color.theme_blue));
//                    done.setBackgroundColor(resources.getColor(R.color.white));
//                    doing.setTextColor(resources.getColor(R.color.black));
//                    doing.setBackgroundColor(resources.getColor(R.color.gray1));
//                }
//                else {
//                    ((TaskFrag) adapter.getItem(mViewPager.getCurrentItem())).setTaskStatus(0);
//                    ((TaskFrag)adapter.getItem(mViewPager.getCurrentItem())).setUserid(userid);
//                    done.setTextColor(resources.getColor(R.color.theme_blue));
//                    done.setBackgroundColor(resources.getColor(R.color.white));
//                    doing.setTextColor(resources.getColor(R.color.black));
//                    doing.setBackgroundColor(resources.getColor(R.color.gray1));
//                    ((TaskFrag) adapter.getItem(mViewPager.getCurrentItem())).initData();
//
//                }
//                break;
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
        if (TextUtils.isEmpty(currentTitle))
            currentTitle = getActivity().getResources().getString(R.string.app_title_name);
        return currentTitle;

    }
//	@Override
//	public void onShake() {
//		if(null != getActivity())
//			((HomeActivity)getActivity()).checkIn();
//	}

    private void showProgress() {
        if (null != getActivity())
            ((HomeActivity) getActivity()).showProgress();
    }

    private void dismissProgress() {
        if (null != getActivity())
            ((HomeActivity) getActivity()).dismissProgress();
    }

    private void toast(String msg) {
        if (null != getActivity())
            ((HomeActivity) getActivity()).toast(msg);
    }


}
