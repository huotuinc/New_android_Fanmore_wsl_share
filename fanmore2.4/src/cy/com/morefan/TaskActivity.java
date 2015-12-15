package cy.com.morefan;

import java.util.ArrayList;
import java.util.List;

import cy.com.morefan.adapter.AwardAdapter;
import cy.com.morefan.adapter.PreTaskAdapter;
import cy.com.morefan.adapter.TaskAdapter;
import cy.com.morefan.adapter.TaskAdapter.TaskAdapterType;
import cy.com.morefan.adapter.TaskTodayAdapter;
import cy.com.morefan.bean.AwardData;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.constant.Constant.FromType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.TimeUtil;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 有分页：我的参与，任务预览
 * 无分页：昨日收益，总积分收益列表,今日浏览
 * @author cy
 *
 */
public class TaskActivity extends BaseActivity implements BusinessDataListener, Callback, OnRefreshOrLoadListener, BroadcastListener{
	private FromType mFromType;
	private UserService userService;
	private PullDownUpListView listView;
	private BaseAdapter adapter;
	private List<TaskData> taskDatas;
	private List<AwardData> awardDatas;
	private int tagId;//分页用
	private ImageView layEmpty;
	private TextView txtTitle;
	private String date;//指定日期
	private MyBroadcastReceiver myBroadcastReceiver;

	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg){
		switch (msg.what) {
		case BusinessDataListener.DONE_GET_MY_PARTIN_LIST:
		case BusinessDataListener.DONE_GET_TODAY_SCAN:
			dismissProgress();
			if (tagId == 0)
				taskDatas.clear();
			TaskData[] results = (TaskData[]) msg.obj;
			int length = results.length;
			for (int i = 0; i < length; i++) {
				if(!taskDatas.contains(results[i]))
					taskDatas.add(results[i]);
				if(i == length -1)
					tagId = results[i].partInAutoId;
			}
			//listView.setPullUpToLoadEnable(length >= Constant.PAGESIZE);
			layEmpty.setVisibility(taskDatas.size() == 0 ? View.VISIBLE : View.GONE);
			adapter.notifyDataSetChanged();
			listView.onFinishLoad();
			listView.onFinishRefresh();

			break;
		case BusinessDataListener.DONE_GET_AWARD_LIST:
			awardDatas.clear();

			dismissProgress();
			AwardData[] results2 = (AwardData[]) msg.obj;
			int length2 = results2.length;
			for (int i = 0; i < length2; i++) {
				awardDatas.add(results2[i]);
			}
			//listView.setPullUpToLoadEnable(length >= Constant.PAGESIZE);
			layEmpty.setVisibility(awardDatas.size() == 0 ? View.VISIBLE : View.GONE);
			adapter.notifyDataSetChanged();
			listView.onFinishLoad();
			listView.onFinishRefresh();
			break;
		case BusinessDataListener.ERROR_GET_MY_PARTIN_LIST:
		case BusinessDataListener.ERROR_GET_TODAY_SCAN:
		case BusinessDataListener.ERROR_GET_AWARD_LIST:

			if((mFromType == FromType.MyPartIn || mFromType == FromType.TodayScan) && taskDatas != null)
				layEmpty.setVisibility(taskDatas.size() == 0 ? View.VISIBLE : View.GONE);
			if((mFromType == FromType.TotalScore || mFromType == FromType.YesAward) && awardDatas != null)
				layEmpty.setVisibility(awardDatas.size() == 0 ? View.VISIBLE : View.GONE);
			dismissProgress();
			toast(msg.obj.toString());
			listView.onFinishLoad();
			listView.onFinishRefresh();

			break;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.user_partin);
		initView();

		mFromType = (FromType) getIntent().getExtras().getSerializable(Constant.TYPE_FROM);
		date = getIntent().getExtras().getString(AwardDetailActivity.DATE);

		switch (mFromType) {
		case PreTask:
			taskDatas = new ArrayList<TaskData>();
			adapter = new PreTaskAdapter(listView.getImageLoader(), this, taskDatas);
			txtTitle.setText("今日预告");
			break;
		case TodayScan:
			taskDatas = new ArrayList<TaskData>();
			adapter = new TaskTodayAdapter(listView.getImageLoader(), this, taskDatas);
			txtTitle.setText("今日浏览");
			listView.setPullUpToLoadEnable(false);
			break;
		case MyPartIn:
			taskDatas = new ArrayList<TaskData>();
			adapter = new TaskAdapter(listView.getImageLoader(), this, taskDatas, TaskAdapterType.Mine);
			txtTitle.setText("我的参与");
			break;
		case YesAward:
			awardDatas = new ArrayList<AwardData>();
			adapter = new AwardAdapter(listView.getImageLoader(), this, awardDatas);
			((AwardAdapter) adapter).setChangeDes(true);
			txtTitle.setText("昨日收益");
			listView.setPullUpToLoadEnable(false);
			break;
		case TotalScore:
			awardDatas = new ArrayList<AwardData>();
			adapter = new AwardAdapter(listView.getImageLoader(), this, awardDatas);
			txtTitle.setText(TimeUtil.FormatterTimeByMonthAndDay2(date));
			listView.setPullUpToLoadEnable(false);
			break;

		default:
			break;
		}

		listView.setAdapter(adapter);


		userService = new UserService(this);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE, MyBroadcastReceiver.ACTION_ALARM_UP);

	}

	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}
	private void initView() {
		txtTitle = (TextView) findViewById(R.id.txtTitle);
		listView = (PullDownUpListView) findViewById(R.id.listView);
		layEmpty = (ImageView) findViewById(R.id.layEmpty);
		listView.setOnRefreshOrLoadListener(this);
		listView.setOnItemClickListener(itemListener);

	}

	private void initData(){
		tagId = 0;
		listView.setSelection(0);
		getDataFromSer();
	}
	private void reLoadData(){
		getDataFromSer();
	}
	public void getDataFromSer(){
		//int type = currentType == TaskAdapterType.Yes ? 1 : 0;
		switch (mFromType) {
		case PreTask:
			userService.getPreTaskList(this, UserData.getUserData().loginCode, Constant.PAGESIZE, tagId);
			break;
		case TodayScan:
			userService.getTodayScanList(UserData.getUserData().loginCode);
			break;
		case YesAward:
			userService.getDayAward(UserData.getUserData().loginCode, 1, "");
			break;
		case TotalScore:
			userService.getDayAward(UserData.getUserData().loginCode, 2, date);
			break;
		case MyPartIn:
			userService.getMyPartIn(UserData.getUserData().loginCode,0, Constant.PAGESIZE, tagId);
			break;
		}
		//userService.getMyPartIn(UserData.getUserData().loginCode,0, Constant.PAGESIZE, tagId);
		showProgress();
	}

	private OnItemClickListener itemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			if(mFromType == FromType.TodayScan )//今日浏览无详情
				return;
			int taskId = 0;
			if(mFromType == FromType.TotalScore || mFromType == FromType.YesAward){//活动收益无详情
				if(awardDatas.get(position - 1).type == 1)
					return;
				taskId = awardDatas.get(position - 1).id;
			}else
				taskId = taskDatas.get(position - 1).id;
			if(mFromType == FromType.MyPartIn && !taskDatas.get(position - 1).isAccount){
				toast("结算后查看!");
				return;
			}
			if(mFromType == FromType.PreTask){
				Intent intentTaskDetail = new Intent(TaskActivity.this, TaskDetailActivity.class);
				TaskData taskData = new TaskData();
				taskData.id = taskId;
				intentTaskDetail.putExtra("taskData", taskData);
				intentTaskDetail.putExtra("fromPre", true);
				intentTaskDetail.putExtra("refreshList", true);
				intentTaskDetail.putExtra("advTime", taskDatas.get(position-1).advTime);
				startActivity(intentTaskDetail);
				return;

			}
			Intent intentDetail = new Intent(TaskActivity.this,AwardDetailActivity.class);
			//intentDetail.putExtra("taskData", datas.get(position - 1));
			intentDetail.putExtra(Constant.TYPE_FROM, mFromType);
			intentDetail.putExtra(AwardDetailActivity.TASK_ID, taskId);
			intentDetail.putExtra(AwardDetailActivity.DATE, date);
			startActivity(intentDetail);

		}
	};
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type, datas).sendToTarget();

	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();

	}

	@Override
	public void onRefresh() {
		initData();

	}
	@Override
	public void onLoad() {
		reLoadData();

	}
	@Override
	protected void onDestroy() {
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}

	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		L.i(">>>>>onFinishReceiver1");
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();
		}else if(type == ReceiverType.AlarmUp && mFromType == FromType.PreTask){
			if(null != msg){
				Bundle extra = (Bundle) msg;
				int id = extra.getInt("id");
				String ids = SPUtil.getStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_ALARM);
				//save to sp
				String[] idsA = ids.split(",");
				if(null != idsA){
					StringBuilder sb = new StringBuilder();
					for(int i = 0, length = idsA.length; i < length; i++){
						if(!idsA[i].equals(id+"")){
							sb.append("," + idsA[i]);
						}
					}
					SPUtil.saveStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_ALARM, sb.toString());
					L.i(">>>>>send broadcast:" + sb.toString());
				}
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					adapter.notifyDataSetChanged();
				}
			});
		}

	}

}
