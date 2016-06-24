package cy.com.morefan.frag;

import com.lib.cylibimagedownload.ImageUtil;
import com.umeng.message.PushAgent;

import cy.com.morefan.AppUpdateActivity;
import cy.com.morefan.FeedBackActivity;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.DataListActivity;
import cy.com.morefan.DataListActivity.ActivityType;
import cy.com.morefan.PushMsgActivity;
import cy.com.morefan.R;
import cy.com.morefan.UserGuideActivity;
import cy.com.morefan.AppUpdateActivity.UpdateType;
import cy.com.morefan.UserRegActivity;
import cy.com.morefan.WebViewActivity;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.service.TaskService;
import cy.com.morefan.service.ThreadPoolManager;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CustomDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MoreFrag extends BaseFragment implements OnClickListener, Callback, BusinessDataListener{
	private static MoreFrag frag;
	private View mRootView;
	private TextView txtCacheSize;
	private TaskService taskService;
	private TextView txtPushStatus;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(msg.what == BusinessDataListener.DONE_CHECK_UP){
			dismissProgress();

			/**
			 * 1版本管理
			 * 0，无更新
			 * 1.增量更新
			 * 2.整包更新
			 * 3.强制增量更新
			 * 4.强制整包更新
			 */
			Bundle bundle = (Bundle) msg.obj;
			int updateType = bundle.getInt("updateType");
			String updateMd5 = bundle.getString("updateMd5");
			String updateUrl = bundle.getString("updateUrl");
			String updateTips = bundle.getString("updateTips");
			//是否完善个人信息
			switch (updateType) {
			case 0://无更新
				toast("当前为最新版本!");
				break;
			case 1://1.增量更新
				choiceToUpdate(UpdateType.DiffUpdate, updateMd5, updateUrl, updateTips);
				break;
			case 2://2.整包更新
				choiceToUpdate(UpdateType.FullUpate, updateMd5, updateUrl, updateTips);
				break;
			case 3://3.强制增量更新
				choiceToUpdate(UpdateType.DiffUpdate, updateMd5, updateUrl, updateTips);
				break;
			case 4://4.强制整包更新
				choiceToUpdate(UpdateType.FullUpate, updateMd5, updateUrl, updateTips);
				break;

			default:
				break;
			}


		}else if(msg.what == BusinessDataListener.ERROR_CHECK_UP){
			toast(msg.obj.toString());
			dismissProgress();
		}
		return false;
	}


	private void choiceToUpdate(final UpdateType type, final String md5, final String url, final String tips) {
		//CustomDialog.setOnKeyBackListener(getActivity());
			CustomDialog.showChooiceDialg(getActivity(), "温馨提示", "发现新版本，马上更新?\n" + tips, "马上更新", "跳过该版本", null,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(getActivity(), AppUpdateActivity.class);
							// intent.putExtra("isForce", isForce);
							intent.putExtra("type", type);
							intent.putExtra("md5", md5);
							intent.putExtra("url", url);
							intent.putExtra("tips", tips);
							startActivity(intent);
						}
					}, null);

	}
	public static MoreFrag newInstance(){
		if(frag == null)
			frag = new MoreFrag();
		return frag;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		taskService = new TaskService(this);


	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.tab_more, container, false);
		txtCacheSize = (TextView) mRootView.findViewById(R.id.txtCacheSize);
		mRootView.findViewById(R.id.layClear).setOnClickListener(this);
		mRootView.findViewById(R.id.layUpdate).setOnClickListener(this);
		
		mRootView.findViewById(R.id.layAbout).setOnClickListener(this);
		
		mRootView.findViewById(R.id.layAbout).setOnClickListener(this);
		mRootView.findViewById(R.id.layClearSina).setOnClickListener(this);
		mRootView.findViewById(R.id.laypsw).setOnClickListener(this);

	
		((TextView)mRootView.findViewById(R.id.txtVersion)).setText(Constant.APP_VERSION);
		refreshCacheSize();
		

		return mRootView;
	}
	

	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onReshow() {
		refreshCacheSize();
	}
	public void refreshCacheSize(){
		long imgStore = ImageUtil.getFileSize(Constant.IMAGE_PATH_STORE);
		long imgTask = ImageUtil.getFileSize(Constant.IMAGE_PATH_TASK);
		if(getActivity() != null)
			txtCacheSize.setText(Formatter.formatFileSize(getActivity(), imgStore + imgTask));
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		System.out.println("frag1:" + isVisibleToUser);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		
		case R.id.laypsw:
			Bundle bundle=new Bundle();
			bundle.putString("title","修改密码");
			bundle.putInt("isUpdate",1);
			ActivityUtils.getInstance().showActivity(getActivity(),UserRegActivity.class,bundle);
			break;

		case R.id.layClear:
			ThreadPoolManager.getInstance().addTask(new Runnable() {
				@Override
				public void run() {
					ImageUtil.deleteFileByPath(Constant.IMAGE_PATH_STORE);
					ImageUtil.deleteFileByPath(Constant.IMAGE_PATH_TASK);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							refreshCacheSize();
						}
					});
				}});

			break;

		case R.id.layUpdate:
			taskService.checkUpdate();
			showProgress();
			break;
	
		case R.id.layAbout:
			Intent intentAbout = new Intent(getActivity(), WebViewActivity.class);
			intentAbout.putExtra("url", BusinessStatic.getInstance().URL_ABOUTUS);
			intentAbout.putExtra("title", "关于我们");
			startActivity(intentAbout);
			break;
	

		default:
			break;
		}

	}

	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		if( null != getActivity())
			((HomeActivity)getActivity()).onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type, extra).sendToTarget();

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
//	@Override
//	public void onClickTitleMiddle() {
//		// TODO Auto-generated method stub
//
//	}


	@Override
	public void onFragPasue() {
		// TODO Auto-generated method stub

	}
}
