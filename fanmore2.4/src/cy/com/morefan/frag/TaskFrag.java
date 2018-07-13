package cy.com.morefan.frag;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cy.com.morefan.BaseActivity;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.R;
import cy.com.morefan.TaskDetailActivity;
import cy.com.morefan.adapter.TaskAdapter;
import cy.com.morefan.adapter.TaskAdapter.TaskAdapterType;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.ScoreService;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.ShareUtil;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.EmptyView;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
import cy.com.morefan.view.SwipeView;
import cy.com.morefan.view.SwitcherView;

import android.R.integer;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import cy.com.morefan.view.loopview.LoopView;
import cy.com.morefan.view.loopview.OnItemSelectedListener;

public class TaskFrag extends BaseFragment
		implements BusinessDataListener, OnRefreshOrLoadListener,
		Callback, OnClickListener, BroadcastListener,PullDownUpListView.OnScrollCallBackListener{

	public enum TabType{
		mr,jljf,zfrs,syjf
	}
	private TabType tabType;
	private static TaskFrag frag;
	private View mRootView;
	private int pageIndex;//客户端现有发布时间最早任务id（做分页用）
	private TaskService taskService;
	private PullDownUpListView listView;
	private TaskAdapter adapter;
	private List<TaskData> datas;
	private PopupWindow popupWindow;
	private ImageView imgTag;
	private TextView txtTitle;
	private ImageView layEmpty;
	private View layMast;
	private MyBroadcastReceiver myBroadcastReceiver;
	private int taskType;
	private int taskStaus=1;
	private int userId=0;
	private ImageView ivTaskDataConer;
	private boolean dataShow=false;
	private LinearLayout task_date_select;
	private LoopView loopView;
	private TextView task_date_value;
	private SwitcherView switcherView;
	private List<String> dateList =new ArrayList<>();
	private TaskData[] notices;
	private LinearLayout task_notice_container;
	private UserService userService;
	private ScoreService scoreService;
	private LinearLayout task_notice_container2;
	private View headerNoticeView;

	public void setTaskStatus(int taskStatus){
		this.taskStaus = taskStatus;
	}
	public void setUserid(int userId){
		this.userId = userId;
	}

	public enum TaskType{
		mr,jljf,zfrs,syjf
	}
	private TaskType currentTaskType;
	public static TaskFrag newInstance(){
		if(frag == null)
			frag = new TaskFrag();
		return frag;
	}
	public PullDownUpListView getListView(){
		return this.listView;
	}
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == BusinessDataListener.DONE_GET_INFO_LIST) {
			dismissProgress();
			TaskData[] results = (TaskData[]) msg.obj;
			int length = results.length;
			if (length > 0) {
				if (results[0].pageIndex == 1) {
					datas.clear();
					if (getActivity() != null)
						((HomeActivity) getActivity()).setScores();
				}
			}


			for (int i = 0; i < length; i++) {
				if (!datas.contains(results[i]))
					datas.add(results[i]);
				pageIndex = results[i].pageIndex;
			}
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);

//			//热门，推荐没有分页
//			if(currentTaskType == TaskType.Recommend || currentTaskType == TaskType.Hot){
//				listView.setPullUpToLoadEnable(false);
//			}else {
//				//listView.setPullUpToLoadEnable(length >= Constant.PAGESIZE);
//			}


			adapter.notifyDataSetChanged();
			listView.onFinishLoad();
			listView.onFinishRefresh();

		} else if (msg.what == BusinessDataListener.ERROR_GET_INFO_LIST) {
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			dismissProgress();
			toast(msg.obj.toString());
			listView.onFinishLoad();
			listView.onFinishRefresh();
		} else if (msg.what == BusinessDataListener.DONE_GET_NOTICE_LIST) {
			notices = (TaskData[]) msg.obj;
			initNotice(notices);
		} else if (msg.what == BusinessDataListener.ERROR_GET_NOTICE_LIST) {
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			dismissProgress();
			toast(msg.obj.toString());
			listView.onFinishLoad();
			listView.onFinishRefresh();
		} else if (msg.what == BusinessDataListener.DONE_COMMIT_SEND) {
			toast("分享成功");
			dismissProgress();
			String mallUserId = SPUtil.getStringToSpByName( getContext(), Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId);
			if (TextUtils.isEmpty(mallUserId )) return false;

			scoreService.getScoreInfo(getContext() , UserData.getUserData().loginCode , Integer.valueOf( mallUserId ));

			//refreshView();
		} else if (msg.what == BusinessDataListener.ERROR_COMMIT_SEND
				|| msg.what == BusinessDataListener.ERROR_USER_LOGIN) {
			dismissProgress();
			toast(msg.obj.toString());
//			if(msg.what == BusinessDataListener.ERROR_COMMIT_SEND)
//				reCommit(msg.obj.toString());
		} else if (msg.what == BusinessDataListener.ERROR_RE_COMMIT_SEND) {
			dismissProgress();
			//reCommit(msg.obj.toString());
			toast("转发失败");
		} else if (msg.what == BusinessDataListener.DONE_SCORE) {//刷新积分
			String appScore = ((Bundle)msg.obj).getString("appScore","0");
			UserData.getUserData().score =appScore;
			MyBroadcastReceiver.sendBroadcast(getContext() , MyBroadcastReceiver.ACTION_REFRESH_USEDATA);
		} else if (msg.what == BusinessDataListener.ERROR_SCORE) {
			//ToastUtil.show(this, msg.obj.toString());
		}
		return false;
	}

		@Override
		public void setArguments(Bundle args) {
			if(args != null){
				tabType = (TabType) args.getSerializable("tabType");
			}
			super.setArguments(args);
		}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initPopupWindow();
		pageIndex = 0;
		taskService = new TaskService(this);
		currentTaskType = TaskType.mr;
		myBroadcastReceiver = new MyBroadcastReceiver(getActivity(), this,
				MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE,
				MyBroadcastReceiver.ACTION_REFRESH_TASK_LIST
				//,
				//MyBroadcastReceiver.ACTION_SHARE_TO_WEIXIN_SUCCESS,
				//MyBroadcastReceiver.ACTION_SHARE_TO_QZONE_SUCCESS,
				//MyBroadcastReceiver.ACTION_SHARE_TO_SINA_SUCCESS,
				//MyBroadcastReceiver.ACTION_WX_NOT_BACK
				);

		userService = new UserService(this);
		scoreService = new ScoreService(this);
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("tabType", tabType);
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		if(savedInstanceState != null)
			tabType = (TabType) savedInstanceState.getSerializable("tabType");
		initData();
		super.onViewStateRestored(savedInstanceState);
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_task, container, false);
		listView = (PullDownUpListView) mRootView.findViewById(R.id.listView);
		listView.setOnRefreshOrLoadListener(this);
		listView.setOnItemClickListener(itemListener);
		//listView.setOnScrollListener(this);
		//listView.setPullUpToLoadEnable(false);
		listView.setOnScrollCallBackListener(this);

		imgTag = (ImageView) getActivity().findViewById(R.id.imgTag);
		txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
		layEmpty = (ImageView) mRootView.findViewById(R.id.layEmpty);
		// 奖励设置
//		((TextView) mRootView.findViewById(R.id.txtSend)).setText(String.format("转发奖励￥%.1f/个", BusinessStatic.getInstance().AWARD_SEND/10.0));  ;
//		((TextView) mRootView.findViewById(R.id.txtScan)).setText(String.format("好友浏览￥%.1f/次", BusinessStatic.getInstance().AWARD_SCAN/10.0));;
//		((TextView) mRootView.findViewById(R.id.txtLink)).setText(String.format("￥%.1f/次", BusinessStatic.getInstance().AWARD_LINK/10.0));;


		ivTaskDataConer = mRootView.findViewById(R.id.task_date_corner);
		LinearLayout task_date_top = mRootView.findViewById(R.id.task_date_top);
		task_date_top.setOnClickListener(this);
		task_date_select = mRootView.findViewById(R.id.task_date_select);
		task_date_select.setVisibility(dataShow?View.VISIBLE:View.GONE);
		loopView = mRootView.findViewById(R.id.loopView);
		task_date_value= mRootView.findViewById(R.id.task_date_value);
		layMast = mRootView.findViewById(R.id.layMast);
		layMast.setOnClickListener(this);

		task_notice_container=mRootView.findViewById(R.id.task_notice_container);

		initDateData();


		datas = new ArrayList<TaskData>();
		adapter = new TaskAdapter(listView.getImageLoader(), getActivity(), datas, TaskAdapterType.Normal);
		adapter.setTabType(tabType);
		listView.setAdapter(adapter);

		initNoticeView();

		initData();
		return mRootView;
	}

	private void initNoticeView(){
		headerNoticeView = LayoutInflater.from(getContext()).inflate(R.layout.layout_notice,null);
		listView.addHeaderView(headerNoticeView);

		task_notice_container2= headerNoticeView.findViewById(R.id.task_notice_container2);

		switcherView = headerNoticeView.findViewById(R.id.switcherView);
		switcherView.setOnClickListener(this);

		taskService.getNoticeList(UserData.getUserData().loginCode );
	}

	@Override
	public void onReshow() {
		//initData();

	}

	private LinearLayout layGroup;
	@SuppressWarnings("deprecation")
	private void initPopupWindow() {
		if(null == getActivity())
			return;
		LayoutInflater mInflater = LayoutInflater.from(getActivity());
		View layout = mInflater.inflate(R.layout.pop_dis_view, null);
		layGroup = (LinearLayout) layout.findViewById(R.id.layGroup);
		//layout.findViewById(R.id.layScore).setOnClickListener(this);
//		layout.findViewById(R.id.layNew).setOnClickListener(this);
//		layout.findViewById(R.id.layActive).setOnClickListener(this);
	//	layout.findViewById(R.id.layAll).setOnClickListener(this);
		popupWindow = new PopupWindow(layout, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.setFocusable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//设置PopupWindow显示和隐藏时的动画
    	popupWindow.setAnimationStyle(R.style.AnimationPop);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				startImgTagAnimation(popupWindow.isShowing());
			}
		});
	}

	public void startImgTagAnimation(boolean isShowing) {
		RotateAnimation rotate = null;
	if(isShowing){//to open menu
		rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	}else{
		rotate = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
	}
	rotate.setDuration(500);
	rotate.setFillAfter(true);
	imgTag.startAnimation(rotate);
}

	 //显示菜单
	    private void showPopup(){

	    	if(popupWindow == null)
				initPopupWindow();

	    	//setPopBg(0);
	           //设置位置
	    	//popupWindow.showAsDropDown(getActivity().findViewById(R.id.layMiddle),-DensityUtil.dip2px(getActivity(), 22),-DensityUtil.dip2px(getActivity(), 12));


	    	if(null == getActivity())
	    		return;
	    	popupWindow.showAtLocation(mRootView, Gravity.CENTER_HORIZONTAL | Gravity.TOP,20,DensityUtil.dip2px(getActivity(), 60)); //设置在屏幕中的显示位置
	    	startImgTagAnimation(popupWindow.isShowing());
	    }
	private OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//head不能点击

			int prefix = listView.getHeaderViewsCount();

			if(position < prefix || datas.size() == 0)
				return;
			Intent intentDetail = new Intent(getActivity(),TaskDetailActivity.class);

			int idx = position - prefix;
			if( idx <0 || idx >= datas.size() ) return;

			TaskData taskData = datas.get( idx );
			taskData.position = idx ;
			intentDetail.putExtra("taskData", taskData);
			startActivityForResult(intentDetail, 0);

		}
	};



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == Constant.RESULT_CODE_TASK_STATUS_CHANGE){
			if(data == null)
				return;

			TaskData taskData = (TaskData) data.getExtras().getSerializable("taskData");
			if(taskData != null){
				int postion = -1;
				for(TaskData item : datas){
					if(item.id == taskData.id){
						postion = item.position;
						break;
					}
				}
				if(-1 != postion){
					datas.set(postion, taskData);
					adapter.notifyDataSetChanged();
				}

			}


		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	public void initData(){
		/**
		 * 设置栏目数据
		 */
		datas.clear();
		adapter.notifyDataSetChanged();
		pageIndex = 0;
		L.i("initData:" + datas.size());

		String current =dateList.get(0); // dateList.get(dateList.size()-1);
		task_date_value.setText(current);
		loopView.setCurrentPosition(0);

		getDataFromSer();


	}
	private void setPopBg(int id) {
		for(int i = 0, length = layGroup.getChildCount(); i < length; i++){
			View item = layGroup.getChildAt(i);
			item.setBackgroundColor(item.getId() == id ? 0xff999999 : Color.WHITE);
		}

	}


	public void reLoadData(){
		getDataFromSer();
	}
	public void getDataFromSer(){
		adapter.notifyDataSetChanged();
		int taskType = 0;
		//taskService.getTaskList("",UserData.getUserData().loginCode,1,pageIndex+1, taskType,userId,taskStaus);

		String date = task_date_value.getText().toString();
		date = date.split(" ")[0];

		taskService.getInfoList( UserData.getUserData().loginCode , date  );


		showProgress();
	}


	private void getDataFromSer(String date){
		//adapter.notifyDataSetChanged();
		//int taskType = 0;
		//taskService.getTaskList("",UserData.getUserData().loginCode,1,pageIndex+1, taskType,userId,taskStaus);

		//String date = task_date_value.getText().toString();
		date = date.split(" ")[0];

		taskService.getInfoList( UserData.getUserData().loginCode , date  );

		showProgress();
	}


	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		if( null != getActivity())
			((HomeActivity)getActivity()).onDataFinish(type, des, datas, extra);

		if(type == BusinessDataListener.DONE_SCORE){
			mHandler.obtainMessage(type , extra).sendToTarget();
		}else {
			mHandler.obtainMessage(type, datas).sendToTarget();
		}

	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		if( null != getActivity())
			((HomeActivity)getActivity()).onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}

	@Override
	public void onDataFail(int type, String des, Bundle extra) {
		mHandler.obtainMessage(type, des).sendToTarget();
	}

	@Override
	public void onRefresh() {
		initData();
		taskService.getNoticeList(UserData.getUserData().loginCode );
	}
	@Override
	public void onLoad() {
		String lastDateStr = datas.get( datas.size()-1 ).taskDate;

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			Date lastDate = sf.parse(lastDateStr); //new Date( TimeUtil.getLongTime(lastDateStr));

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastDate);
			calendar.add(Calendar.DAY_OF_MONTH, -1);

//			String week = TimeUtil.getWeek(calendar);
//			String dateweek = lastDateStr + " " + week;
//			task_date_value.setText(dateweek);
//			if (!dateList.contains(dateweek)) {
//				dateList.add(dateweek);
//			}

			String selDate = sf.format( calendar.getTime());

			getDataFromSer( selDate );
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	private void toast(String msg){
		if(getActivity() != null)
			((BaseActivity)getActivity()).toast(msg);

	}
	private void showProgress(){
//		if(getActivity() != null)
//			((BaseActivity)getActivity()).showProgress();
	}
	private void dismissProgress(){
		if(getActivity() != null)
			((BaseActivity)getActivity()).dismissProgress();
	}
//	@Override
//	public void onClickTitleLeft() {
//		if(getActivity() != null)
//			((HomeActivity)getActivity()).openOrCloseMenu();
//
//	}
//	@Override
//	public void onClickTitleRight() {
//		//initData();
//		//提现
//		if(getActivity() != null)
//			((HomeActivity) getActivity()).toCrash();
//	}
//	@Override
	public void onClickTitleMiddle() {
		showPopup();

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.listView:
			if(getActivity() != null && ((HomeActivity)getActivity()).isOpened())
				((HomeActivity)getActivity()).closeMenu();
			break;
		case R.id.task_date_top:
		case R.id.layMast:
			dataShow=!dataShow;
			ivTaskDataConer.setImageResource( dataShow ? R.drawable.corner2:R.drawable.corner );
			task_date_select.setVisibility(dataShow?View.VISIBLE:View.GONE);
			task_notice_container.setVisibility(!dataShow?View.VISIBLE:View.GONE);
			layMast.setVisibility(dataShow?View.VISIBLE:View.GONE);
			break;
		case R.id.switcherView:
			//ToastUtil.show( getContext() ,switcherView.getCurrentItem() );
			Intent intentDetail = new Intent(getActivity(),TaskDetailActivity.class);
			TaskData taskData = null;
			for(int i=0;i<notices.length;i++){
				if(notices[i].taskName.equals( switcherView.getCurrentItem().toString() )){
					taskData = notices[i];
				}
			}

			intentDetail.putExtra("taskData", taskData);
			startActivityForResult(intentDetail, 0);

			break;
//		case R.id.layHot:
//			currentTitle = "热门";
//			txtTitle.setText("热门");
//			currentTaskType = TaskType.Hot;
//			initData();
//			popupWindow.dismiss();
//			break;
//		case R.id.layNew:
//			currentTitle = "推荐";
//			txtTitle.setText("推荐");
//			currentTaskType = TaskType.Recommend;
//			initData();
//			popupWindow.dismiss();
//			break;
//		case R.id.layActive:
//			currentTitle = "活动";
//			txtTitle.setText("活动");
//			currentTaskType = TaskType.Active;
//			initData();
//			popupWindow.dismiss();
//			break;
//		case R.id.layScore:
//			currentTitle = "有分";
//			txtTitle.setText(currentTitle);
//			currentTaskType = TaskType.Scoreful;
//			initData();
//			popupWindow.dismiss();
//			break;
//		case R.id.layAll:
//			currentTitle = getActivity().getResources().getString(R.string.app_title_name);
//			txtTitle.setText(currentTitle);
//			currentTaskType = TaskType.All;
//			initData();
//			popupWindow.dismiss();
//			break;

		default:
			break;
		}

	}
	@Override
	public void onDestroy() {
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();

		switcherView.destroySwitcher();

	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate || type == ReceiverType.RefreshTaskList){
			L.i("onFinishReceiver");
			initData();

		}else if(type == ReceiverType.ShareToWeixinSuccess){
			int channleType = ShareUtil.CHANNEL_WEIXIN;
			int taskid = ((Bundle)msg).getInt("taskId",0);
			commit( taskid , channleType );
		}else if(type == ReceiverType.ShareToSinaSuccess){
			int channleType = ShareUtil.CHANNEL_SINA;
			int taskid = ((Bundle)msg).getInt("taskId",0);
			commit(taskid , channleType );
//			userService.commitSend(taskData.id,UserData.getUserData().loginCode, ShareUtil.CHANNEL_SINA);
//			showProgress();
		}else if(type == ReceiverType.ShareToQzoneSuccess){
			int channleType = ShareUtil.CHANNEL_QZONE;
			int taskid = ((Bundle)msg).getInt("taskId",0);
			commit(taskid , channleType );
//			userService.commitSend(taskData.id,UserData.getUserData().loginCode, ShareUtil.CHANNEL_QZONE);
//			showProgress();
		}

	}

	private void commit(int taskid , int channleType ) {
		//TaskDetailActivity.StatusType statusType = TaskDetailActivity.StatusType.Share;
		userService.commitSend(taskid,UserData.getUserData().loginCode, channleType);
		showProgress();

	}

	private void reCommit(String msg ,final int taskid , final int channelType){
		CustomDialog.showChooiceDialg(this.getActivity() , "提交失败", msg, "重试", "取消", null,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						commit( taskid , channelType );
					}
				}, null);
	}


	private String currentTitle;
	public String getTitleText() {
		if(null != getActivity() && TextUtils.isEmpty(currentTitle))
			currentTitle = getActivity().getResources().getString(R.string.app_title_name);
		return currentTitle;

	}
	@Override
	public void onFragPasue() {
		// TODO Auto-generated method stub

	}

	private void initDateData(){
		Calendar calendar=Calendar.getInstance();
		Date currentDate= calendar.getTime();
		dateList.clear();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		for(int i=0;i>=-30;i--){
			calendar.setTime(currentDate);
			calendar.add(Calendar.DAY_OF_MONTH , i);
			String fdate = simpleDateFormat.format(calendar.getTime()) +"  "+ TimeUtil.getWeek(calendar);
			dateList.add( fdate );
		}


		loopView.setItems(dateList);
		loopView.setListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(int index) {
				if(index>= dateList.size()|| index<0) return;
				task_date_value.setText( dateList.get(index) );
				datas.clear();
				getDataFromSer();
			}
		});

		int curentP = 0; // dateList.size()-1;
		loopView.setCurrentPosition( curentP );
		task_date_value.setText( dateList.get(curentP) );
		//String mon = dateList.get(curentP).split(" ")[0];

		//taskService.getInfoList(UserData.getUserData().loginCode , mon );

//		task_notice_container=mRootView.findViewById(R.id.task_notice_container);
//
//        switcherView = mRootView.findViewById(R.id.switcherView);
//		switcherView.setOnClickListener(this);
//
//		taskService.getNoticeList(UserData.getUserData().loginCode );
	}

	private void initNotice(TaskData[] list ){

		//task_notice_container.setVisibility(list==null || list.length<1? View.GONE:View.VISIBLE);

		int length = list.length;
		if(length<1) {
			listView.removeHeaderView(headerNoticeView);
			//task_notice_container2.setVisibility(View.GONE);
			return;
		}

		 if( headerNoticeView.getParent() !=null){
			listView.removeHeaderView(headerNoticeView);
		 }
		 listView.addHeaderView(headerNoticeView);
		//task_notice_container2.setVisibility(View.VISIBLE);

		ArrayList<CharSequence> notices = new ArrayList<>();
		for(int i=0;i<list.length;i++){
			String text = list[i].taskName;
			String tag = list[i].tagTitle;
			if(TextUtils.isEmpty(tag)) continue;

			text = "<font color='#E99319'>"+ tag +"</font>|" + text;
			CharSequence charSequence = Html.fromHtml(text);
			notices.add( charSequence );
		}

		switcherView.setResource(notices);

		if(notices.size()<=1) {
			switcherView.setTimePeriod(4000*10000);
		}else {
			switcherView.setTimePeriod(4000);
		}

		switcherView.startRolling();
	}


		@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (this.datas == null || this.datas.size() < 1) return;


			final TaskData cur = this.datas.get(firstVisibleItem);

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					try {

						String time = cur.taskDate;
						Calendar calendar = Calendar.getInstance();
						SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
						Date b1 = sf.parse(time);
						calendar.setTime(b1);
						String w = TimeUtil.getWeek(calendar);


						String week = TimeUtil.getWeek(calendar);
						String temp = time + "  " + week;
						task_date_value.setText(temp);

					} catch (Exception ex) {
					}
				}
			});
	}

}
