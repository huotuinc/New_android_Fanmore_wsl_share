package cy.com.morefan;

import java.util.ArrayList;
import java.util.List;

import cy.com.morefan.adapter.ListAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.FeedbackData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.view.LoadHistoryListView;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.LoadHistoryListView.OnLoadHistoryListener;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 *列表数据
 *反馈列表，积分兑换历史/经验明细,闪购列表, 徒弟列表,  钱包明细
 * @author cy
 *
 */
public class DataListActivity extends BaseActivity implements BusinessDataListener,Callback, OnLoadHistoryListener, BroadcastListener, OnRefreshOrLoadListener{
	public final static String ACTVITY_TYPE = "activity_type";
	public enum ActivityType{
		FeedbackList, MallList, MonenyChange, ExpHistory, PrenticeList, WalletHistory
	}

	private ActivityType curActivityType;
	private ListView listView;
	private ListAdapter adapter;
	private List<BaseData> datas;
	private String pageTag;//分页标识
	private UserService userService;
	private ImageView layEmpty;
	private MyBroadcastReceiver myBroadcastReceiver;

	private int orderType;//(0、拜师时间1、总贡献值) 默认0
	private Handler mHandler = new Handler(this);

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case BusinessDataListener.DONE_GET_MONEY_CHANGE_LIST:
		case BusinessDataListener.DONE_GET_MALL_LIST:
		case BusinessDataListener.DONE_FEEDBACK_LIST:
		case BusinessDataListener.DONE_GET_PRENTICE_LIST:
		case BusinessDataListener.DONE_GET_WALLET_HISTORY:
			dismissProgress();
			if(null != msg.obj){
				BaseData[] results = (BaseData[]) msg.obj;
				int length = results.length;
				for (int i = 0; i < length; i++) {
					BaseData item = results[i];
					if(!datas.contains(item)){
						if(item instanceof FeedbackData)
							datas.add(0,item);
						else
							datas.add(item);
					}

					if(i== length -1)
						pageTag = item.getPageTag();
				}
				adapter.setDatas(datas);
			}
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);

			if(listView instanceof PullDownUpListView){
				PullDownUpListView pullDownUpListView = (PullDownUpListView) listView;
				pullDownUpListView.onFinishLoad();
				pullDownUpListView.onFinishRefresh();
			}else if(listView instanceof LoadHistoryListView){
				LoadHistoryListView loadListView = (LoadHistoryListView) listView;
				loadListView.onFinishLoadHistory();
			}

			break;
		case BusinessDataListener.ERROR_GET_MALL_LIST:
		case BusinessDataListener.ERROR_GET_MONEY_CHANGE_LIST:
		case BusinessDataListener.ERROR_GET_PRENTICE_LIST:
		case BusinessDataListener.ERROR_GET_WALLET_HISTORY:
			dismissProgress();
			toast(msg.obj.toString());
			((PullDownUpListView) listView).onFinishLoad();
			((PullDownUpListView) listView).onFinishRefresh();
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			//mListView.setVisibility(datas.size() == 0 ? View.GONE : View.VISIBLE);
			break;
		case BusinessDataListener.ERROR_FEEDBACK_LIST:
			dismissProgress();
			toast(msg.obj.toString());
			break;

		default:
			break;
		}
		return false;




	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		curActivityType = (ActivityType) getIntent().getExtras().getSerializable(ACTVITY_TYPE);
		switch (curActivityType) {
		case WalletHistory:
			setContentView(R.layout.user_money_change);
			((TextView) findViewById(R.id.txtTitle)).setText("钱包明细");
			break;
		case FeedbackList:
			setContentView(R.layout.feedbacklist);
			break;
		case MallList:
			setContentView(R.layout.mall_list);
			break;
		case MonenyChange:
//		case ExpHistory:
//			setContentView(R.layout.user_money_change);
//			((TextView) findViewById(R.id.txtTitle)).setText(curActivityType == ActivityType.ExpHistory ? "经验明细" : "积分兑换");
//			break;
		case PrenticeList:
			setContentView(R.layout.prentice_list);
			break;

		default:
			break;
		}
		initView();
		userService = new UserService(this);
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);

	}
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.listView);
		layEmpty = (ImageView) findViewById(R.id.layEmpty);

		if(listView instanceof LoadHistoryListView){
			LoadHistoryListView loadListView = (LoadHistoryListView) listView;
			loadListView.setOnLoadHistoryListener(this);
		}else if(listView instanceof PullDownUpListView){
			PullDownUpListView pullDownUpListView = (PullDownUpListView) listView;
			pullDownUpListView.setOnRefreshOrLoadListener(this);
		}
		datas = new ArrayList<BaseData>();
		adapter = new ListAdapter(this, datas);
		listView.setAdapter(adapter);

	}

	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btnHelp:
			Intent intentRule = new Intent(this, WebViewActivity.class);
			intentRule.putExtra("url", BusinessStatic.getInstance().URL_RULE + "#shandianmimi");
			intentRule.putExtra("title", "规则说明");
			startActivity(intentRule);
			break;
		case R.id.btnCommit:
			Intent intent = new Intent(this, FeedBackActivity.class);
			startActivity(intent);
			break;
		case R.id.txtTime:
			setTab(v.getId());
			orderType = 0;
			initData();
			break;
		case R.id.txtContri:
			setTab(v.getId());
			orderType = 1;
			initData();
			break;


		default:
			break;
		}
	}


	@SuppressWarnings("deprecation")
	private void setTab(int id) {
		TextView txtTime = (TextView) findViewById(R.id.txtTime);
		TextView txtContri = (TextView) findViewById(R.id.txtContri);
		TextView txtSelect = id == R.id.txtTime ? txtTime : txtContri;
		TextView txtNormal = id == R.id.txtTime ? txtContri : txtTime;

		txtSelect.setTextColor(Color.WHITE);
		txtSelect.setBackgroundResource(R.drawable.screen_select);

		txtNormal.setTextColor(getResources().getColor(R.color.gray));
		txtNormal.setBackgroundDrawable(null);
	}

	public void initData(){
		datas.clear();
		adapter.setDatas(datas);
		pageTag = null;
		getDataFromSer();
	}
	public void getDataFromSer(){
		pageTag = TextUtils.isEmpty(pageTag) ? "0" : pageTag;
		switch (curActivityType) {
		case WalletHistory:
			userService.getWalletList(UserData.getUserData().loginCode,pageTag, Constant.PAGESIZE);
			showProgress();
			break;
		case FeedbackList:
			userService.FeedbackList(UserData.getUserData().loginCode, Constant.PAGESIZE, pageTag);
			showProgress();
			break;
		case MallList:
			userService.getMallList(UserData.getUserData().loginCode, pageTag, Constant.PAGESIZE);
			showProgress();
			break;
		case MonenyChange:
			userService.userCashHistory(UserData.getUserData().loginCode, pageTag, Constant.PAGESIZE);
			showProgress();
			break;
//		case ExpHistory:
//			userService.getExpHistory(UserData.getUserData().loginCode, pageTag, Constant.PAGESIZE);
//			showProgress();
//			break;
		case PrenticeList:
			userService.getPrenticeList(UserData.getUserData().loginCode, orderType, pageTag, Constant.PAGESIZE);
			showProgress();
			break;

		default:
			break;
		}
	}
	@Override
	public void onLoadHistory() {
		getDataFromSer();

	}

	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		mHandler.obtainMessage(type, des).sendToTarget();
		super.onDataFailed(type, des, extra);
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			final Bundle extra) {
		mHandler.obtainMessage(type, datas).sendToTarget();
		super.onDataFinish(type, des, datas, extra);

		if(type == BusinessDataListener.DONE_GET_MALL_LIST && extra != null){
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					((TextView) findViewById(R.id.txtCount)).setText("闪购" + extra.getString("count") + "单");
					((TextView) findViewById(R.id.txtTemp)).setText(extra.getString("tempScore"));
					((TextView) findViewById(R.id.txtReal)).setText(extra.getString("realScore"));

				}
			});
		}else if(type == BusinessDataListener.DONE_GET_PRENTICE_LIST && extra != null){
				final int prenticeCount = extra.getInt("prenticeCount");
				SPUtil.saveIntToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_PRENTICE_COUNT, prenticeCount);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						((TextView) findViewById(R.id.txtCount)).setText(String.format("已有:%d人", prenticeCount));
					}
				});
		}
	}

	@Override
	protected void onDestroy() {
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}

	}

	@Override
	public void onRefresh() {
		initData();

	}

	@Override
	public void onLoad() {
		getDataFromSer();

	}

}
