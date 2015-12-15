package cy.com.morefan;

import java.util.ArrayList;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.adapter.AwardAdapter;
import cy.com.morefan.bean.AllScoreData;
import cy.com.morefan.bean.AwardData;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.constant.Constant.FromType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.L;
import cy.com.morefan.view.TrendView;
import cy.com.morefan.view.TrendView.OnLoadMoreListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class AllScoreActivity extends BaseActivity implements BroadcastListener, OnLoadMoreListener, Callback, OnItemClickListener {
	private TrendView trendView;
	private UserService userService;
	private ArrayList<AllScoreData> datas;
	private ArrayList<AwardData> awardDatas;
	private AwardAdapter adapter;
	private String pageDate;
	private double maxScore;
	private double minScore;
	private int totalCount;
	private ListView listView;
	private ImageView layEmpty;
	 private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);

	@Override
		public boolean handleMessage(android.os.Message msg) {
			if(msg.what == BusinessDataListener.DONE_GET_ALLSCORE_TREND){
				dismissProgress();
				if(TextUtils.isEmpty(pageDate))
					awardDatas.clear();
				AwardData[]  results = (AwardData[]) msg.obj;
				for(int i = 0, length = results.length; i < length; i++){
					awardDatas.add(results[i]);
					if(i == length -1)
						pageDate = results[i].date;
				}

//				if(TextUtils.isEmpty(pageDate))
//					datas.clear();
//				AllScoreData[]  results = (AllScoreData[]) msg.obj;
//				for(int i = 0, length = results.length; i < length; i++){
//					datas.add(results[i]);
//					if(i == length -1)
//						pageDate = results[i].date;
//				}
				adapter.setDatas(awardDatas);
				trendView.setTrendData(datas, minScore, maxScore);
				trendView.setOnFinishLoadMore();
				refreshView();

				//adapter.notifyDataSetChanged();
			}else if(msg.what == BusinessDataListener.ERROR_GET_ALLSCORE_TREND){
				trendView.setOnFinishLoadMore();
				refreshView();
				dismissProgress();
				toast(msg.obj.toString());
			}
				return false;
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.allscore);
		pageDate = "";

		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(this);
		trendView = (TrendView) findViewById(R.id.trendView);
		layEmpty = (ImageView) findViewById(R.id.layEmpty);

		userService = new UserService(this);


		awardDatas = new ArrayList<AwardData>();
		datas = new ArrayList<AllScoreData>();
		SyncImageLoaderHelper mImageLoader =new SyncImageLoaderHelper(this);
		adapter = new AwardAdapter(mImageLoader, this, awardDatas);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();




		trendView.setListView(listView, adapter);
		//trendView.setTrendData(datas, -20, 50);
		trendView.setOnLoadMoreListener(this);
		initData();
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);



	}
	public void refreshView(){
		trendView.setVisibility(datas.size() == 0 ? View.GONE : View.VISIBLE);
		listView.setVisibility(datas.size() == 0 ? View.GONE : View.VISIBLE);
		layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
	}
	public void initData(){
		datas.clear();
		adapter.setDatas(awardDatas);
		//trendView.setTrendData(datas, 0, 0);
		pageDate = "";
		getDataFromSer();
	}
	public void getDataFromSer(){
		userService.getAllScoreTrendList(datas, UserData.getUserData().loginCode, Constant.PAGESIZE, pageDate);
		showProgress();
	}


	@Override
	public void onLoadMore() {
		//T.show(this, "加载更多");
		if(datas.size() < totalCount && !TextUtils.isEmpty(pageDate)){
			getDataFromSer();
		}
	}
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btnRefresh:
			initData();
			break;

		default:
			break;
		}
	}

	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		mHandler.obtainMessage(type, des).sendToTarget();
		super.onDataFailed(type, des, extra);
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
		if(type == DONE_GET_ALLSCORE_TREND && extra != null){
			maxScore = Double.parseDouble(extra.getString("maxScore"));
			minScore = Double.parseDouble(extra.getString("minScore"));
			final String totalScore = extra.getString("totalScore");
			totalCount = extra.getInt("totalCount");
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					((TextView)findViewById(R.id.txtTotal)).setText(totalScore);
				}
			});

		}
		mHandler.obtainMessage(type, datas).sendToTarget();
		super.onDataFinish(type, des, datas, extra);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		L.i(">>>>>>onItemClick");
		//intentDetail.putExtra("taskData", datas.get(position - 1));
		AwardData awardData = awardDatas.get(arg2);
		if(awardData.type == 2){
			Intent intentDetail = new Intent( this,AwardDetailActivity.class);
			intentDetail.putExtra(Constant.TYPE_FROM, FromType.TotalScore);
			intentDetail.putExtra(AwardDetailActivity.TASK_ID, awardData.id);
			intentDetail.putExtra(AwardDetailActivity.DATE, awardData.date);
			startActivity(intentDetail);
		}

//		Intent intent = new Intent(this, TaskActivity.class);
//     	intent.putExtra(Constant.TYPE_FROM, FromType.TotalScore);
//     	intent.putExtra(AwardDetailActivity.DATE, awardDatas.get(arg2).date);
//     	startActivity(intent);

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myBroadcastReceiver.unregisterReceiver();
	}

	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}

	}

}
