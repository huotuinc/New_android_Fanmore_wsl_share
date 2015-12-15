//package cy.com.morefan;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import cy.com.morefan.adapter.PrenticeDetailAdapter;
//import cy.com.morefan.bean.BaseData;
//import cy.com.morefan.bean.PrenticeContriData;
//import cy.com.morefan.bean.UserData;
//import cy.com.morefan.constant.Constant;
//import cy.com.morefan.listener.BusinessDataListener;
//import cy.com.morefan.service.UserService;
//import cy.com.morefan.view.PullDownUpListView;
//import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Handler.Callback;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//public class PrenticDetailActivity extends BaseActivity implements Callback, OnRefreshOrLoadListener{
//	private PullDownUpListView mListView;
//	private TextView txtName;
//	private TextView txtTime;
//	private TextView txtContri;
//	private UserService mService;
//	private List<PrenticeContriData> datas;
//	private int pageTag;
//	private ImageView layEmpty;
//	private PrenticeDetailAdapter adapter;
//	private int id;
//	private Handler mHandler = new Handler(this);
//	@Override
//	public boolean handleMessage(Message msg) {
//		switch (msg.what) {
//		case BusinessDataListener.DONE_GET_PRENTICE_DETAIL:
//			dismissProgress();
//			PrenticeContriData[] results = (PrenticeContriData[]) msg.obj;
//			int length = results.length;
//			if(length != 0){
//				datas.addAll(Arrays.asList(results));
//				pageTag = results[length - 1].id;
//				adapter.notifyDataSetChanged();
//			}
//			mListView.onFinishLoad();
//			mListView.onFinishRefresh();
//
//			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
//			mListView.setVisibility(datas.size() == 0 ? View.GONE : View.VISIBLE);
//			break;
//		case BusinessDataListener.ERROR_GET_PRENTICE_DETAIL:
//			dismissProgress();
//			toast(msg.obj.toString());
//			mListView.onFinishLoad();
//			mListView.onFinishRefresh();
//
//			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
//			mListView.setVisibility(datas.size() == 0 ? View.GONE : View.VISIBLE);
//			break;
//
//		default:
//			break;
//		}
//		return false;
//	}
//	protected void onCreate(android.os.Bundle arg0) {
//		super.onCreate(arg0);
//		setContentView(R.layout.prentice_detail);
//		mListView = (PullDownUpListView) findViewById(R.id.listView);
//		txtName = (TextView) findViewById(R.id.txtName);
//		txtTime = (TextView) findViewById(R.id.txtTime);
//		txtContri = (TextView) findViewById(R.id.txtContri);
//		layEmpty = (ImageView) findViewById(R.id.layEmpty);
//		mListView.setOnRefreshOrLoadListener(this);
//
//
//		mService = new UserService(this);
//		id = getIntent().getExtras().getInt("id");
//		datas = new ArrayList<PrenticeContriData>();
//		adapter = new PrenticeDetailAdapter(this, datas);
//		mListView.setAdapter(adapter);
//		initData();
//	}
//	public void initData(){
//		datas.clear();
//		adapter.notifyDataSetChanged();
//		pageTag = 0;
//		getDataFromSer();
//	}
//	private void getDataFromSer() {
//		//mService.getPrenticeDetail(UserData.getUserData().loginCode, id, pageTag, Constant.PAGESIZE);
//		showProgress();
//
//	}
//	@Override
//	public void onDataFailed(int type, String des, Bundle extra) {
//		super.onDataFailed(type, des, extra);
//		mHandler.obtainMessage(type, des).sendToTarget();
//	}
//	@Override
//	public void onDataFinish(int type, String des, BaseData[] datas, final Bundle extra) {
//		super.onDataFinish(type, des, datas, extra);
//		if(type == BusinessDataListener.DONE_GET_PRENTICE_DETAIL && extra != null){
//			runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					txtName.setText(extra.getString("name"));
//					txtTime.setText(extra.getString("time"));
//					txtContri.setText(String.valueOf(extra.getInt("contri")));
//				}
//			});
//
//		}
//		mHandler.obtainMessage(type, datas).sendToTarget();
//	}
//	@Override
//	public void onRefresh() {
//		initData();
//
//	}
//	@Override
//	public void onLoad() {
//		getDataFromSer();
//
//	};
//
//}
