package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import cy.com.morefan.service.TaskService;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
import cy.com.morefan.view.EmptyView;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
import android.R.integer;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
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

public class TaskFrag extends BaseFragment implements BusinessDataListener, OnRefreshOrLoadListener, Callback, OnClickListener, BroadcastListener{

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
	private MyBroadcastReceiver myBroadcastReceiver;
	private int taskType;
	private int taskStaus=1;
	private int userId=0;

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
	public boolean handleMessage(Message msg){
		if (msg.what == BusinessDataListener.DONE_GET_TASK_LIST) {
			dismissProgress();
			TaskData[] results = (TaskData[]) msg.obj;
			int length = results.length;
			if (length>0) {
				if (results[0].pageIndex == 1) {
					datas.clear();
					if (getActivity() != null)
						((HomeActivity) getActivity()).setScores();
				}
			}



			for (int i = 0; i < length; i++) {
				if(!datas.contains(results[i]))
					datas.add(results[i]);
				pageIndex=results[i].pageIndex;
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

		}else if(msg.what == BusinessDataListener.ERROR_GET_TASK_LIST){
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			dismissProgress();
			toast(msg.obj.toString());
			listView.onFinishLoad();
			listView.onFinishRefresh();
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
		myBroadcastReceiver = new MyBroadcastReceiver(getActivity(), this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE, MyBroadcastReceiver.ACTION_REFRESH_TASK_LIST);
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
		imgTag = (ImageView) getActivity().findViewById(R.id.imgTag);
		txtTitle = (TextView) getActivity().findViewById(R.id.txtTitle);
		layEmpty = (ImageView) mRootView.findViewById(R.id.layEmpty);
		// 奖励设置
//		((TextView) mRootView.findViewById(R.id.txtSend)).setText(String.format("转发奖励￥%.1f/个", BusinessStatic.getInstance().AWARD_SEND/10.0));  ;
//		((TextView) mRootView.findViewById(R.id.txtScan)).setText(String.format("好友浏览￥%.1f/次", BusinessStatic.getInstance().AWARD_SCAN/10.0));;
//		((TextView) mRootView.findViewById(R.id.txtLink)).setText(String.format("￥%.1f/次", BusinessStatic.getInstance().AWARD_LINK/10.0));;



		datas = new ArrayList<TaskData>();
		adapter = new TaskAdapter(listView.getImageLoader(), getActivity(), datas, TaskAdapterType.Normal);
		adapter.setTabType(tabType);
		listView.setAdapter(adapter);
			initData();
		return mRootView;
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
			if(position < 1 || datas.size() == 0)
				return;
			Intent intentDetail = new Intent(getActivity(),TaskDetailActivity.class);
			TaskData taskData = datas.get(position - 1);
			taskData.position = position - 1;
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
//		if(layGroup.getChildCount() == 0){
//			Iterator<String> groups = BusinessStatic.getInstance().GROUPS.keySet().iterator();
//			int i = 0;
//			while(groups.hasNext()){
//				final String name = groups.next();
//				final int type = BusinessStatic.getInstance().GROUPS.get(name);
//				if(null == getActivity())
//					break;
//				View view = getActivity().getLayoutInflater().inflate(R.layout.pop_dis_view_item, null);
//				view.setId(i);
//				i ++;
//				TextView txtName = (TextView) view.findViewById(R.id.txtName);
//				txtName.setText(name);
//				layGroup.addView(view);
//				setPopBg(0);
//
//
//				view.setOnClickListener(new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						if(type == 0)
//							currentTitle = "粉猫";
//						else
//							currentTitle = name;
//
//						setPopBg(v.getId());
//
//						txtTitle.setText(currentTitle);
//						//currentTaskType = TaskType.Scoreful;
//						taskType = type;
//						initData();
//						popupWindow.dismiss();
//
//					}
//				});
//
//			}
//		}
		//layGroup.removeAllViews();


		datas.clear();
		adapter.notifyDataSetChanged();
		pageIndex = 0;
		L.i("initData:" + datas.size());
		getDataFromSer();
	}
	private void setPopBg(int id) {
		for(int i = 0, length = layGroup.getChildCount(); i < length; i++){
			View item = layGroup.getChildAt(i);
			item.setBackgroundColor(item.getId() == id ? 0xff999999 : Color.WHITE);
		}

	}

	public void Refresh(TaskData taskData){
		taskData.sendCount=taskData.sendCount+1;
		taskData.isSend=true;
		adapter.notifyDataSetChanged();

	}
	public void reLoadData(){
		getDataFromSer();
	}
	public void getDataFromSer(){
		adapter.notifyDataSetChanged();
	int taskType = 0;
//		//0全部;1文章（任务）;2活动;3推荐;4热门;5有分
//		switch (tabType) {
//		case mr:
//			taskType = 0;
//			break;
//		case syjf:
//			taskType = 1;
//			break;
//		case jljf:
//			taskType = 2;
//			break;
//		case zfrs:
//			taskType = 3;
//			break;
//		default:
//			break;
//		}

		taskService.getTaskList("",UserData.getUserData().loginCode,1,pageIndex+1, taskType,userId,taskStaus);

		showProgress();
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		if( null != getActivity())
			((HomeActivity)getActivity()).onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type, datas).sendToTarget();

	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		if( null != getActivity())
			((HomeActivity)getActivity()).onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}

	@Override
	public void onDataFail(int type, String des, Bundle extra) {

	}

	@Override
	public void onRefresh() {
		initData();

	}
	@Override
	public void onLoad() {
		reLoadData();

	}
	private void toast(String msg){
		if(getActivity() != null)
			((BaseActivity)getActivity()).toast(msg);

	}
	private void showProgress(){
		if(getActivity() != null)
			((BaseActivity)getActivity()).showProgress();
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
	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate || type == ReceiverType.RefreshTaskList){
			L.i("onFinishReceiver");
			initData();

		}

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


}
