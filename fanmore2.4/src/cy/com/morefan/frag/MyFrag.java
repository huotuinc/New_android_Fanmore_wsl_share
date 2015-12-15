package cy.com.morefan.frag;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cy.com.morefan.AuthCodeSendActivity.AuthType;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.MyBaseInfoActivity;
import cy.com.morefan.MySafeActivity;
import cy.com.morefan.R;
import cy.com.morefan.AuthCodeSendActivity;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragManager.FragType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.ElasticScrollView;
import cy.com.morefan.view.ElasticScrollView.OnRefreshListener;
import cy.com.morefan.view.HeadView;
import cy.com.morefan.view.ElasticScrollView.ScrollType;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyFrag extends BaseFragment implements OnClickListener, BusinessDataListener, BroadcastListener, Callback{
	private static MyFrag frag;
	private View mRootView;
	private TextView txtScore;
	private TextView txtTotalScore;
	private UserService userService;
	private TextView txtLeastTaskCount;
	private TextView txtToCash;
	private MyBroadcastReceiver myBroadcastReceiver;
	private ImageView img;
	private SyncImageLoaderHelper helper;
	private TextView txtName;
	private ImageView imgTag;
	private HeadView head;
	private ElasticScrollView scrollView;
	private Handler mHandler = new Handler(this);

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == BusinessDataListener.DONE_USER_LOGIN) {
			scrollView.onRefreshComplete();
			head.onRefreshComplete();
			if (getActivity() == null)
				return false;
			dismissProgress();
			initData();
		}else if(msg.what == BusinessDataListener.ERROR_USER_LOGIN){
			scrollView.onRefreshComplete();
			head.onRefreshComplete();
			dismissProgress();
			toast(msg.obj.toString());
		}
		return false;
	};
	public static MyFrag newInstance(){
		if(frag == null)
			frag = new MyFrag();
		return frag;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		L.i("MyFrag onCreate");
		super.onCreate(savedInstanceState);
		userService = new UserService(this);
		myBroadcastReceiver = new MyBroadcastReceiver(getActivity(), this, MyBroadcastReceiver.ACTION_USER_MAINDATA_UPDATE, MyBroadcastReceiver.ACTION_USER_LOGOUT);
		helper = new SyncImageLoaderHelper(getActivity());
	}
	@Override
	public void onDestroy() {
		myBroadcastReceiver.unregisterReceiver();
		super.onDestroy();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		L.i("MyFrag onCreateView");
		mRootView = inflater.inflate(R.layout.tab_my, container, false);
		mRootView.findViewById(R.id.btnLogOut).setOnClickListener(this);
		mRootView.findViewById(R.id.layBaseInfo).setOnClickListener(this);
		mRootView.findViewById(R.id.laySafe).setOnClickListener(this);
		img = (ImageView) mRootView.findViewById(R.id.imgPhoto);
		imgTag = (ImageView) mRootView.findViewById(R.id.imgTag);
		txtName = (TextView) mRootView.findViewById(R.id.txtName);
		txtToCash = (TextView) mRootView.findViewById(R.id.btnToCash);
		txtToCash.setOnClickListener(this);
		txtToCash.setText("进入商城");
		txtScore = (TextView) mRootView.findViewById(R.id.txtScore);
		txtTotalScore = (TextView) mRootView.findViewById(R.id.txtTotalScore);
		//txtLeastTaskCount = (TextView) mRootView.findViewById(R.id.txtLeastTaskCount);


		scrollView = (ElasticScrollView) mRootView.findViewById(R.id.scrollview);
		head = new HeadView(getActivity());
		scrollView.addHeadView(head.getView());
		scrollView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh(float per, ScrollType type) {
				head.onRefresh(per, type);
				if(type == ScrollType.REFRESHING)
					refresh();
			}
		});
		return mRootView;
	}
	@Override
	public void onResume() {
		if(getActivity() != null && ((HomeActivity)getActivity()).getCurrentFragType() == FragType.My)
			((HomeActivity)getActivity()).setTitleButton(FragType.My);
		refresh();
		super.onResume();
	}
	@Override
	public void onReshow() {
		if(getActivity() != null && ((HomeActivity)getActivity()).getCurrentFragType() == FragType.My)
			((HomeActivity)getActivity()).setTitleButton(FragType.My);
		refresh();
	}
	private void initData() {

	UserData userData = UserData.getUserData();
	imgTag.setVisibility(userData.completeInfo ? View.GONE : View.VISIBLE);
		txtName.setText(userData.userName);
		txtScore.setText(userData.score);
		txtTotalScore.setText(userData.totalScore);
	txtLeastTaskCount.setText(String.format("%d/%d", userData.completeTaskCount, userData.totalTaskCount));
		if(TextUtils.isEmpty(userData.picUrl)){
			img.setImageResource(R.drawable.user_icon);
		}else{
			helper.loadImage(0, img, null, UserData.getUserData().picUrl, Constant.IMAGE_PATH_STORE);
		}
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	public void refresh(){
		userService.userLogin(getActivity(), UserData.getUserData().userName, UserData.getUserData().pwd, true);
		showProgress();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRight:
			refresh();
			break;

		case R.id.layPhone:
			Intent intentPhone = new Intent(getActivity(), AuthCodeSendActivity.class);
			if(TextUtils.isEmpty(UserData.getUserData().phone)){//未绑定
				intentPhone.putExtra(Constant.AuthCodeType, AuthType.Phone);
			}else{//已绑定，先解除绑定
				intentPhone.putExtra(Constant.AuthCodeType, AuthType.UnBindPhone);
			}
			startActivity(intentPhone);
			break;
		case R.id.laySafe:
			Intent intentSafe = new Intent(getActivity(),MySafeActivity.class);
			startActivity(intentSafe);
			break;
		case R.id.layBaseInfo://用户基本信息
		case R.id.imgPhoto:
			Intent intentBaseInfo = new Intent(getActivity(),MyBaseInfoActivity.class);
			startActivity(intentBaseInfo);
			break;
		case R.id.btnToCash://提现
			if(BusinessStatic.getInstance().CRASH_TYPE == 0 && !UserData.getUserData().lockScore.equals("0")){
				toast("您的提现申请正在受理中...");
				return;
			}
			if(getActivity() != null)
				((HomeActivity) getActivity()).toCrash();


			break;
		case R.id.btnLogOut:
			CustomDialog.showChooiceDialg(getActivity(), null, "确定要注销吗？", "注销", "取消", null, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					logout();

				}
			}, null);
			break;

		default:
			break;
		}

	}
	private void logout(){
		SPUtil.saveStringToSpByName(getActivity(), Constant.SP_NAME_NORMAL, Constant.SP_NAME_PRE_USERNAME, UserData.getUserData().userName);
		UserData.clear();

		//清除微信授权信息
		ShareSDK.getPlatform(Wechat.NAME).removeAccount();


		if(getActivity() != null)
			((HomeActivity)getActivity()).userLoginOut2ShowTaskFrag();
		SPUtil.saveStringToSpByName(getActivity(), Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, "");
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		mHandler.obtainMessage(type).sendToTarget();

	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		mHandler.obtainMessage(type, des).sendToTarget();
	}

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
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.UserMainDataUpdate){
			initData();
		}else if(type == ReceiverType.Logout){
			logout();
		}


	}

	@Override
	public void onFragPasue() {
		// TODO Auto-generated method stub

	}
}
