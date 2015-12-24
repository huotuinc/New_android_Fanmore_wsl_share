package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.List;

import cy.com.morefan.AwardDetailActivity;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.R;
import cy.com.morefan.adapter.MyPartInAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.PartInItemData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.constant.Constant.FromType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.UserService;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PartInDetailItemFrag extends Fragment implements OnRefreshOrLoadListener, BusinessDataListener, Callback{
	private View mRootView;
	private FromType mFromType;
	public String date;
	private int taskId;
	private int pageIndex;
	private String tagId;//分页用
	private UserService userService;
	private List<PartInItemData> datas;
	private MyPartInAdapter adapter;
	//private TextView txtTotal;
	private ImageView layEmpty;
	private PullDownUpListView listView;
	private TextView txtScan;
	private TextView txtLink;
	private TextView txtScore;
	private boolean showMoneyAnim = true;

	private Handler mHandler = new Handler(this);


//	public boolean handleMessage(Message msg) {
//		if(msg.what == BusinessDataListener.DONE_GET_MY_PARTIN_DETAIL){
//			if(showMoneyAnim){
//				showMoneyAnim = false;
//				if(getActivity() != null){
//					ImageView imgMoney = (ImageView) getActivity().findViewById(R.id.imgMoney);
//					((AwardDetailActivity)getActivity()).showMoneyAnim(imgMoney);
//				}
//
//
//			}
//			dismissProgress();
//			Bundle extra = (Bundle) msg.obj;
//
//
//
//			if(tagId.equals("0")){
//				datas.clear();
//				setTotalScore(extra.getString("totalScore"));
//				txtScan.setText(extra.getInt("scanCount") + "");
//				txtLink.setText(extra.getInt("linkCount") +"");
//				txtScore.setText(extra.getString("scoreCount"));
//			}
//
//			PartInItemData[] results = (PartInItemData[]) extra.getSerializable("datas");
//			int length = results.length;
//			for (int i = 0; i < length; i++) {
////				if(pageIndex == 1)
////					results[i].score = "";
//				datas.add(results[i]);
//				if(i== length -1){
//					tagId = results[i].pageTag;
//				}
//			}
//			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
//			//listView.setPullUpToLoadEnable(datas.size() >= Constant.PAGESIZE);
//			listView.onFinishLoad();
//			listView.onFinishRefresh();
//			adapter.notifyDataSetChanged();
//
//		}else if(msg.what == BusinessDataListener.ERROR_GET_MY_PARTIN_DETAIL){
//			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
//			dismissProgress();
//			toast(msg.obj.toString());
//			listView.onFinishLoad();
//			listView.onFinishRefresh();
//		}
//		return false;
//	}


	private void setTotalScore(String totalScore) {
		if(getActivity() != null)
			((TextView)getActivity().findViewById(R.id.txtTotalScore)).setText(totalScore);

	}


	public PartInDetailItemFrag(){

	}

	@Override
	public void setArguments(Bundle args) {
		if(args != null){
		mFromType = (FromType) args.getSerializable(Constant.TYPE_FROM);
		date = args.getString(AwardDetailActivity.DATE);
		taskId = args.getInt(AwardDetailActivity.TASK_ID);
		pageIndex = args.getInt(AwardDetailActivity.PAGE_INDEX);
		}
		super.setArguments(args);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		userService = new UserService(this);
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.user_partin_detail_item, null);
//		if(mRootView == null){
//			mRootView = inflater.inflate(R.layout.user_partin_detail_item, null);
//		}else{
//
//		}

		listView = (PullDownUpListView) mRootView.findViewById(R.id.listView);
		txtScan = (TextView) mRootView.findViewById(R.id.txtScan);
		txtLink = (TextView) mRootView.findViewById(R.id.txtLink);
		txtScore = (TextView) mRootView.findViewById(R.id.txtScore);
		layEmpty = (ImageView) mRootView.findViewById(R.id.layEmpty);
		listView.setOnRefreshOrLoadListener(this);


		datas = new ArrayList<PartInItemData>();
		adapter = new MyPartInAdapter(getActivity(), datas);
		listView.setAdapter(adapter);
		//userService = new UserService(this);
		initData();

		return mRootView;
	}
	private void showProgress(){
		if(getActivity() != null)
			((AwardDetailActivity)getActivity()).showProgress();
	}
	private void dismissProgress(){
		if(getActivity() != null)
			((AwardDetailActivity)getActivity()).dismissProgress();
	}
	private void toast(String msg){
		if(getActivity() != null)
			((AwardDetailActivity)getActivity()).toast(msg);
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		if( null != getActivity())
			((AwardDetailActivity)getActivity()).onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type, extra).sendToTarget();
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		if( null != getActivity())
			((AwardDetailActivity)getActivity()).onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}

	@Override
	public void onDataFail(int type, String des, Bundle extra) {

	}


	public void initData(){
		tagId = "0";
		getDataFromSer();
	}
	public void reload(){
		getDataFromSer();
	}
	public void getDataFromSer(){
		switch (mFromType) {
		case MyPartIn://任务历史收益
			userService.getMyPartInDetail(UserData.getUserData().loginCode, taskId, Constant.PAGESIZE, tagId, 3, "");
			adapter.setNeedGroup(true);
			break;
		case TotalScore://总积分
			if(pageIndex == 0){//指定日期
				userService.getMyPartInDetail(UserData.getUserData().loginCode, taskId, Constant.PAGESIZE, tagId, 2, date);
				adapter.setNeedGroup(false);
			}else{//任务历史收益
				userService.getMyPartInDetail(UserData.getUserData().loginCode, taskId, Constant.PAGESIZE, tagId, 3, "");
				adapter.setNeedGroup(true);
			}
			break;
		case YesAward:
			if(pageIndex == 0){//昨日收益
				userService.getMyPartInDetail(UserData.getUserData().loginCode, taskId, Constant.PAGESIZE, tagId, 1, "");
				adapter.setNeedGroup(false);
			}else{//任务历史收益
				userService.getMyPartInDetail(UserData.getUserData().loginCode, taskId, Constant.PAGESIZE, tagId, 3,"");
				adapter.setNeedGroup(true);
			}
			break;

		default:
			break;
		}

		showProgress();
	}
	@Override
	public void onRefresh() {
		initData();

	}
	@Override
	public void onLoad() {
		reload();

	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}
}
