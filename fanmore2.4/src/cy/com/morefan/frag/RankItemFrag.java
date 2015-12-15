package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.List;

import com.sina.weibo.sdk.openapi.models.User;

import cy.com.morefan.AwardDetailActivity;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.R;
import cy.com.morefan.RankActivity;
import cy.com.morefan.adapter.MyPartInAdapter;
import cy.com.morefan.adapter.RankAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.PartInItemData;
import cy.com.morefan.bean.RankData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.constant.Constant.FromType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.L;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class RankItemFrag extends Fragment implements OnRefreshOrLoadListener, BusinessDataListener, Callback{
	public enum TabType{
		Perday, Total, Pretience
	}
	private View mRootView;
	private TabType tabType;
	private UserService userService;
	private List<RankData> datas;
	private RankAdapter adapter;
	private ImageView layEmpty;
	private PullDownUpListView listView;
	private TextView txtDes;
	private String rankDes;
	private int myRank;
	private int type;

	private Handler mHandler = new Handler(this);

	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_GET_RANK){
			dismissProgress();
			txtDes.setVisibility(TextUtils.isEmpty(rankDes) ? View.GONE : View.VISIBLE);
			txtDes.setText(rankDes);
			RankData[] results = (RankData[]) msg.obj;
			int length = results.length;
			for (int i = 0; i < length; i++) {
				datas.add(results[i]);
			}
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			listView.onFinishLoad();
			listView.onFinishRefresh();
			adapter.setMyRank(myRank);
			//adapter.notifyDataSetChanged();

		}else if(msg.what == BusinessDataListener.ERROR_GET_MY_PARTIN_DETAIL){
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			dismissProgress();
			toast(msg.obj.toString());
			listView.onFinishLoad();
			listView.onFinishRefresh();
		}
		return false;
	}




	public RankItemFrag(){

	}

	@Override
	public void setArguments(Bundle args) {
		if(args != null){
			tabType = (TabType) args.getSerializable(Constant.TYPE_FROM);
		}
		super.setArguments(args);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		userService = new UserService(this);
		//tabType = TabType.Perday;
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.rank_tab, null);
		listView = (PullDownUpListView) mRootView.findViewById(R.id.listView);
		txtDes = (TextView) mRootView.findViewById(R.id.txtDes);
		layEmpty = (ImageView) mRootView.findViewById(R.id.layEmpty);
		listView.setOnRefreshOrLoadListener(this);
		listView.setPullUpToLoadEnable(false);

		if(datas == null || adapter == null){
			datas = new ArrayList<RankData>();
			adapter = new RankAdapter(getActivity(), datas);
			initData();
		}else{
			refreshView();
		}
		listView.setAdapter(adapter);







		return mRootView;
	}

	private void refreshView() {
		adapter.setDatas(datas);
		layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
		txtDes.setVisibility(TextUtils.isEmpty(rankDes) ? View.GONE : View.VISIBLE);
		txtDes.setText(rankDes);

	}

	private void showProgress(){
		if(getActivity() != null)
			((RankActivity)getActivity()).showProgress();
	}
	private void dismissProgress(){
		if(getActivity() != null)
			((RankActivity)getActivity()).dismissProgress();
	}
	private void toast(String msg){
		if(getActivity() != null)
			((RankActivity)getActivity()).toast(msg);
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,Bundle extra) {
		if( null != getActivity())
			((RankActivity)getActivity()).onDataFinish(type, des, datas, extra);
		if(extra != null){
			rankDes = extra.getString("des");
			myRank = extra.getInt("myRank");
		}
		mHandler.obtainMessage(type, datas).sendToTarget();
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		if( null != getActivity())
			((RankActivity)getActivity()).onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}


	public void initData(){
		if(datas != null)
			datas.clear();
		getDataFromSer();
	}
	public void getDataFromSer(){
		int type = 1;
		switch (tabType) {
		case Perday:
			type = 1;
			break;
		case Total:
			type = 2;
			break;
		case Pretience:
			type = 3;
			break;
		default:
			type = 1;
			break;
		}
		L.i(">>tabtype:" + tabType +" type:" + type);
		userService.getRank(UserData.getUserData().loginCode, type);
		showProgress();
	}
	@Override
	public void onRefresh() {
		initData();

	}
	@Override
	public void onLoad() {

	}

}
