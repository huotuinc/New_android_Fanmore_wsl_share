package cy.com.morefan;

import java.util.ArrayList;
import java.util.List;
import cy.com.morefan.adapter.PushMsgAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.PushMsgData;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;

public class PushMsgActivity extends BaseActivity implements OnRefreshOrLoadListener, Callback, BroadcastListener{
	private PullDownUpListView mListView;
	private PushMsgAdapter adapter;
	private List<PushMsgData> datas;
	private UserService mUserService;
	private int pageIndex;
	private ImageView layEmpty;
	private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_GET_PUSH_MSG){
			dismissProgress();

			PushMsgData[] results = (PushMsgData[]) msg.obj;
			int length = results.length;
			for (int i = 0; i < length; i++) {
				datas.add(results[i]);
				if(i== length -1)
					pageIndex = results[i].id;
			}
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			//listView.setPullUpToLoadEnable(datas.size() >= Constant.PAGESIZE);
			mListView.onFinishLoad();
			mListView.onFinishRefresh();
			adapter.setDatas(datas);
		}else if(msg.what == BusinessDataListener.ERROR_GET_PUSH_MSG){
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			dismissProgress();
			toast(msg.obj.toString());
			mListView.onFinishLoad();
			mListView.onFinishRefresh();
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.push_msg);
		mListView = (PullDownUpListView) findViewById(R.id.listView);
		layEmpty = (ImageView) findViewById(R.id.layEmpty);
		mListView.setOnRefreshOrLoadListener(this);
		datas = new ArrayList<PushMsgData>();
		adapter = new PushMsgAdapter(this, new ArrayList<PushMsgData>());
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				PushMsgData data = datas.get(position - 1);
				int type = data.type;
				if(type == 1){//1：普通消息
					//to web
					Intent intentAbout = new Intent(PushMsgActivity.this, WebViewActivity.class);
					intentAbout.putExtra("url", data.webUrl);
					intentAbout.putExtra("title", "消息详情");
					startActivity(intentAbout);
				}else{//预告消息
					int status = data.taskStatus;
					if(status == 2){
						toast("任务已下架!");
						return;
					}else if(status == 0){
						//to task detail,得刷新任务列表
						Intent intentDetail = new Intent(PushMsgActivity.this, TaskDetailActivity.class);
						TaskData taskData = new TaskData();
						taskData.id = data.taskId;
						intentDetail.putExtra("taskData", taskData);
						intentDetail.putExtra("refreshList", true);
						startActivityForResult(intentDetail, 0);
					}else{
						//to web
						Intent intentAbout = new Intent(PushMsgActivity.this, WebViewActivity.class);
						intentAbout.putExtra("url", data.webUrl);
						intentAbout.putExtra("title", "任务预告");
						startActivity(intentAbout);
					}

				}


			}
		});

		mUserService = new UserService(this);
		initData();
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}

	private void initData() {
		datas.clear();
		getDataFromSer(0);
	}
	private void getDataFromSer(int pageIndex){
		mUserService.getPushMsg(UserData.getUserData().loginCode, pageIndex, Constant.PAGESIZE);
		showProgress();
	}

	@Override
	public void onRefresh() {
		initData();
	}

	@Override
	public void onLoad() {
		getDataFromSer(pageIndex);

	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type, datas).sendToTarget();
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}

	}
	@Override
	protected void onDestroy() {
		if(myBroadcastReceiver != null)
			myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}

}
