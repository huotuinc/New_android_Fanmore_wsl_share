package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.DataListActivity;
import cy.com.morefan.R;
import cy.com.morefan.ShareActivity;
import cy.com.morefan.DataListActivity.ActivityType;
import cy.com.morefan.adapter.PrenticeAdapter;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.PrenticeData;
import cy.com.morefan.bean.PrenticeTopData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.UserService;
import cy.com.morefan.view.PullDownUpListView;
import cy.com.morefan.view.PullDownUpListView.OnRefreshOrLoadListener;

@SuppressWarnings("deprecation")
public class PrenticeFrag extends BaseFragment implements OnClickListener, BusinessDataListener, OnRefreshOrLoadListener, Callback{
	private static PrenticeFrag frag;
	private View mRootView;
	private SyncImageLoaderHelper helper;
	private PullDownUpListView mListView;
	//private TextView  txtPrenticeCount;
	private UserService mService;
	private String pageTag;
	private ImageView img;
	private RelativeLayout apprentice;
	
	private PrenticeTopData topData;
	private List<PrenticeData> datas;
	private PrenticeAdapter adapter;
	private ImageView layEmpty;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case BusinessDataListener.DONE_GET_PRENTICE:
			dismissProgress();
			PrenticeData[] results = (PrenticeData[]) msg.obj;
			int length = results.length;
			if(length != 0){
				datas.addAll(Arrays.asList(results));
				pageTag = results[length - 1].pageTag;
				adapter.notifyDataSetChanged();
			}
			adapter.setDatas(topData, datas);
			mListView.onFinishLoad();
			mListView.onFinishRefresh();

			//refreshCountView();

			layEmpty.setVisibility(adapter.getCount() == 0 ? View.VISIBLE : View.GONE);
			mListView.setVisibility(adapter.getCount() == 0 ? View.GONE : View.VISIBLE);

			break;
		case BusinessDataListener.ERROR_GET_PRENTICE:
			dismissProgress();
			toast(msg.obj.toString());
			mListView.onFinishLoad();
			mListView.onFinishRefresh();
			layEmpty.setVisibility(datas.size() == 0 ? View.VISIBLE : View.GONE);
			mListView.setVisibility(datas.size() == 0 ? View.GONE : View.VISIBLE);
			break;
		case 0x11111111:
		{
			Intent intentList = new Intent(getActivity(), DataListActivity.class);
			intentList.putExtra(DataListActivity.ACTVITY_TYPE, ActivityType.PrenticeList);
			startActivity(intentList);
		}
		break;
			case 0x111111:
				ClipboardManager cmb = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				cmb.setText(topData.invitationCode);
				toast("邀请码复制成功!");
				break;
		default:
			break;
		}
		return false;
	}



	public static PrenticeFrag newInstance(){
		if(frag == null)
			frag = new PrenticeFrag();
		return frag;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mService = new UserService(this);
		topData = new PrenticeTopData();
		datas = new ArrayList<PrenticeData>();
		adapter = new PrenticeAdapter(getActivity(), topData, datas, mHandler);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_prentice, container, false);
		mListView = (PullDownUpListView) mRootView.findViewById(R.id.listView);
		//txtPrenticeCount = (TextView) getActivity().findViewById(R.id.txtPrenticeCount);
		layEmpty = (ImageView) mRootView.findViewById(R.id.layEmpty);
		/*img = (ImageView) mRootView.findViewById(R.id.imgPhoto);
		apprentice=(RelativeLayout)mRootView.findViewById(R.id.apprentice);
		apprentice.setOnClickListener(this);*/
		
		mListView.setAdapter(adapter);
		mListView.setOnRefreshOrLoadListener(this);
//		mListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				//head,top不能点击
//				if(position > 1){
//					Intent intentDetail = new Intent(getActivity(), PrenticDetailActivity.class);
//					intentDetail.putExtra("id", datas.get(position - 2).id);
//					startActivity(intentDetail);
//				}
//
//			}
//		});
		initData();
		return mRootView;
	}
	public void onResume() {
		super.onResume();
		//if(((HomeActivity)getActivity()).getCurrentFragType() == FragType.Prentice)
			//refreshCountView();
			//initData();

	};
	@Override
	public void onReshow() {
		initData();
	}
//	private void refreshCountView() {
//		int prenticeCount = topData.prenticeCount - SPUtil.getIntFromSpByName(getActivity(), Constant.SP_NAME_NORMAL, Constant.SP_NAME_PRENTICE_COUNT);
//		if(prenticeCount > 0){
//			txtPrenticeCount.setText(String.valueOf(prenticeCount));
//			txtPrenticeCount.setVisibility(View.VISIBLE);
//		}else{
//			txtPrenticeCount.setVisibility(View.GONE);
//		}
//
//	}
	public void initData(){
		datas.clear();
		adapter.setDatas(topData, datas);
		pageTag = "0";
		getDataFromSer();
		
	}
	public void getDataFromSer(){
		mService.getPrentice(UserData.getUserData().loginCode, pageTag, Constant.PAGESIZE);
		showProgress();
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txtRight:
			Intent intentShare = new Intent(getActivity(), ShareActivity.class);
			intentShare.putExtra("shareDes", topData.prenticeShareDes);
			intentShare.putExtra("shareUrl", topData.prenticeShareUrl);
			startActivity(intentShare);
			break;
		case R.id.apprentice:
//			Intent intentList = new Intent(getActivity(), PrenticeListActivity.class);
//			startActivity(intentList);
			Intent intentList = new Intent(getActivity(), DataListActivity.class);
			intentList.putExtra(DataListActivity.ACTVITY_TYPE, ActivityType.PrenticeList);
			startActivity(intentList);
			break;
		case R.id.btnCopy:
			// 得到剪贴板管理器
			ClipboardManager cmb = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(topData.invitationCode);
			toast("邀请码复制成功!");
			break;
//		case R.id.btnShare:
//			Intent intentShare = new Intent(getActivity(), ShareActivity.class);
//			intentShare.putExtra("shareDes", topData.prenticeShareDes);
//			intentShare.putExtra("shareUrl", topData.prenticeShareUrl);
//			startActivity(intentShare);
//			break;
		case R.id.layRuleFrag:
//			Intent intentRule = new Intent(getActivity(), WebViewActivity.class);
//			intentRule.putExtra("url", BusinessStatic.getInstance().URL_RULE + "#shoutumimi");
//			intentRule.putExtra("title", "规则说明");
//			startActivity(intentRule);

			break;

		default:
			break;
		}
	}
	public void onHiddenChanged(boolean hidden) {
	};


    private void showProgress(){
    	if(getActivity() != null)
    		((HomeActivity)getActivity()).showProgress();
    }
    private void dismissProgress(){
    	if(getActivity() != null)
    		((HomeActivity)getActivity()).dismissProgress();
    }
    private void toast(String msg){
    	if(getActivity() != null)
    		((HomeActivity)getActivity()).toast(msg);
    }
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		if( null != getActivity())
			((HomeActivity)getActivity()).onDataFinish(type, des, datas, extra);
		if(type == BusinessDataListener.DONE_GET_PRENTICE && extra != null){
			topData.invitationCode = extra.getString("invitationCode");
			topData.prenticeDes = extra.getString("prenticeDes");
			topData.prenticeShareDes = extra.getString("prenticeShareDes");
			topData.prenticeShareUrl = extra.getString("prenticeShareUrl");
			topData.prenticeAmount = extra.getInt("prenticeAmount");
			topData.lastContri = extra.getString("lastContri");
			topData.totalContri = extra.getString("totalContri");
			topData.yesterdayBrowseAmount =extra.getString("yesterdayBrowseAmount");
			topData.historyTotalBrowseAmount=extra.getString("historyTotalBrowseAmount");
			topData.yesterdayTurnAmount=extra.getString("yesterdayTurnAmount");
			topData.historyTotalTurnAmount=extra.getString("historyTotalTurnAmount");
		}
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
		getDataFromSer();

	}



	@Override
	public void onFragPasue() {
		// TODO Auto-generated method stub

	}


}

