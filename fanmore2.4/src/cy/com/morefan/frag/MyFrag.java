package cy.com.morefan.frag;

import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cy.com.morefan.AllScoreActivity;
import cy.com.morefan.BuildConfig;
import cy.com.morefan.HomeActivity;
import cy.com.morefan.MainApplication;
import cy.com.morefan.MoblieLoginActivity;
import cy.com.morefan.R;
import cy.com.morefan.WebShopActivity;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserBaseInfoList;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.frag.FragManager.FragType;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.ActivityUtils;
import cy.com.morefan.util.AuthParamUtils;
import cy.com.morefan.util.Base64;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CropperView;
import cy.com.morefan.view.CropperView.OnCropperBackListener;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.ElasticScrollView;
import cy.com.morefan.view.ElasticScrollView.OnRefreshListener;
import cy.com.morefan.view.HeadView;
import cy.com.morefan.view.ElasticScrollView.ScrollType;
import cy.com.morefan.view.ImageLoad;
import cy.com.morefan.view.PhotoSelectView;
import cy.com.morefan.view.PhotoSelectView.OnPhotoSelectBackListener;
import cy.com.morefan.view.UserInfoView;
import cy.com.morefan.view.UserInfoView.OnUserInfoBackListener;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyFrag extends BaseFragment implements OnUserInfoBackListener,OnClickListener, BusinessDataListener, BroadcastListener, Callback,OnPhotoSelectBackListener,OnCropperBackListener {
	private static MyFrag frag;
	private View mRootView;
	private UserService userService;
	private UserData userData;
	private MyBroadcastReceiver myBroadcastReceiver;
	private HeadView head;
	private ElasticScrollView scrollView;
	private Handler mHandler = new Handler(this);
	public MainApplication application;
	public TextView txtSex;
	public TextView txtName;
	public TextView txtmylevel;
	public TextView txtTurnAmount;
	public TextView txtBrowseAmount;
	public TextView txtHistoryBrowseAmount;
	public TextView txthuoban;
	public TextView txtTime;
	public ImageView img;
	private UserInfoView userInfoView;
	private PhotoSelectView pop;
	private CropperView cropperView;


	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
			case BusinessDataListener.DONE_COMMIT_PHOTO:
				dismissProgress();
				img.setImageBitmap(cropBitmap);
				toast("头像上传成功!");
				//commitText();
				break;
			case BusinessDataListener.ERROR_COMMIT_PHOTO:
				dismissProgress();
				CustomDialog.showChooiceDialg(getActivity(), "头像上传失败", "是否重新上传?", "重传", "取消", null,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								commitPhoto();
							}
						}, null);
				break;
			case BusinessDataListener.DONE_USER_INFO:
				//checkDialog();
				dismissProgress();
				Bundle extra = (Bundle) msg.obj;
				updateView(extra);
				UserBaseInfoList baseInfoList = (UserBaseInfoList) extra.getSerializable("list");
				System.out.println(baseInfoList.toString());
				break;
			case BusinessDataListener.DONE_MODIFY_USER_INFO:
				toast("修改成功");
				Bundle extra2 = (Bundle) msg.obj;
				modify2UpdateView(extra2);
				if (((UserSelectData)extra2.getSerializable("data")).id.equals("0")) {
					UserData.getUserData().RealName = ((UserSelectData) extra2.getSerializable("data")).name;
				}
				MyBroadcastReceiver.sendBroadcast(getActivity(), MyBroadcastReceiver.ACTION_REFRESH_USEDATA);
				dismissProgress();
				break;
			case BusinessDataListener.ERROR_USER_INFO:
			case BusinessDataListener.ERROR_MODIFY_USER_INFO:
				dismissProgress();
				toast(msg.obj.toString());

				if (msg.what == BusinessDataListener.ERROR_USER_INFO)
					getActivity().finish();
				break;

			default:
				break;
		}
		return false;
	}
	private void modify2UpdateView(Bundle extra) {
		UserInfoView.Type type = (UserInfoView.Type) extra.getSerializable("type");
		UserSelectData data = (UserSelectData) extra.getSerializable("data");
		switch (type) {
			case Name:
				txtName.setText(data.name);
				break;
			case Sex:
				txtSex.setTag(data.id);
				txtSex.setText( data.id.equals("1") ? "男" : "女");
				break;

			default:
				break;
		}

	}
	private void updateView(Bundle extra) {
		txtName.setText(extra.getString("name"));
		int sex = extra.getInt("sex");//1男2女
		txtSex.setTag(sex);
		txtSex.setText( sex == 1 ? "男" : (sex == 2 ? "女" :""));
		txtTime.setText(UserData.getUserData().regTime);

	}
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
		userInfoView = new UserInfoView(getActivity());
		userInfoView.setOnUserInfoBackListener(this);


		userService = new UserService(this);
		userService.getUserBaseInfo(UserData.getUserData().loginCode);
		showProgress();

		application = (MainApplication) this.getActivity().getApplication ();
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
		mRootView.findViewById(R.id.layImg).setOnClickListener(this);
		mRootView.findViewById(R.id.laySex).setOnClickListener(this);
		txtSex= (TextView) mRootView.findViewById(R.id.txtSex);
		txtName= (TextView) mRootView.findViewById(R.id.txtName);
		txtBrowseAmount= (TextView) mRootView.findViewById(R.id.txtBrowseAmount);
		txtTurnAmount= (TextView) mRootView.findViewById(R.id.txtTurnAmount);
		txtmylevel= (TextView) mRootView.findViewById(R.id.txtmylevel);
		txtTime= (TextView) mRootView.findViewById(R.id.txtTime);
		txthuoban= (TextView) mRootView.findViewById(R.id.txthuoban);
		img =(ImageView)mRootView.findViewById(R.id.img);

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
		userInfoView = new UserInfoView(getActivity());
		userInfoView.setOnUserInfoBackListener(this);


		userService = new UserService(this);
		userService.getUserBaseInfo(UserData.getUserData().loginCode);
		showProgress();

		if(TextUtils.isEmpty(UserData.getUserData().picUrl)){
			img.setImageResource(R.drawable.user_icon);
		}else{
			//helper.loadImage(0, img, null, UserData.getUserData().picUrl, Constant.IMAGE_PATH_STORE);
			ImageLoad.loadLogo(UserData.getUserData().picUrl,img,getActivity());
		}
		txtmylevel.setText(UserData.getUserData().levelName);
		txtTime.setText(UserData.getUserData().regTime);
		txtBrowseAmount.setText(UserData.getUserData().TotalBrowseAmount);
		txtTurnAmount.setText(UserData.getUserData().TotalTurnAmount);
		txthuoban.setText(UserData.getUserData().PrenticeAmount);
		myBroadcastReceiver = new MyBroadcastReceiver(getActivity(), this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	public void refresh(){
		userService.MobileLogin(getActivity(), UserData.getUserData().pwd,UserData.getUserData().userName);

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layImg:
			if(null == pop)
				pop = new PhotoSelectView(getActivity(), this);
			pop.show();

			break;
			case R.id.laySex:

				List<UserSelectData> sexDatas = new ArrayList<UserSelectData>();
				//1男2女
				sexDatas.add(new UserSelectData("男","1"));
				sexDatas.add(new UserSelectData("女", "2"));
				userInfoView.show(UserInfoView.Type.Sex, sexDatas, txtSex.getTag().toString());
				break;
			case R.id.layName:
				userInfoView.show(UserInfoView.Type.Name, null, txtName.getText().toString());
				break;
		case R.id.btnLogOut:
			CustomDialog.showChooiceDialg(getActivity(), null, "确定要注销吗？", "注销", "取消", null, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					logout();
					Intent intentlogin	 = new Intent(getActivity(),MoblieLoginActivity.class);
					startActivity(intentlogin);
					getActivity().finish();

				}
			}, null);
			break;
		case R.id.layHistoryBrowseAmount:
			Intent intent = new Intent( getActivity() , AllScoreActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}
	public void logout(){
		SPUtil.saveStringToSpByName(getActivity(), Constant.SP_NAME_NORMAL, Constant.SP_NAME_PRE_USERNAME, UserData.getUserData().userName);
		UserData.clear();
		SPUtil.saveStringToSpByName(getActivity(), Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId, "");
		SPUtil.saveStringToSpByName(getActivity(), Constant.SP_NAME_NORMAL, Constant.SP_NAME_UnionId, "");
		//清除微信授权信息
		ShareSDK.getPlatform(Wechat.NAME).removeAccount();



		SPUtil.saveStringToSpByName(getActivity(), Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, "");
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		if(type == BusinessDataListener.DONE_USER_INFO){
			mHandler.obtainMessage(type, extra).sendToTarget();
		}else if(type == BusinessDataListener.DONE_MODIFY_USER_INFO){
			mHandler.obtainMessage(type, extra).sendToTarget();
		}else if(type == BusinessDataListener.DONE_COMMIT_PHOTO){
			mHandler.obtainMessage(type).sendToTarget();
		}
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
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
	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.UserMainDataUpdate){
			getActivity().finish();
		}else if(type == ReceiverType.Logout){
			logout();
		}


	}

	@Override
	public void onFragPasue() {
		// TODO Auto-generated method stub

	}
	private void commitPhoto(){
		userService.commitPhoto(UserData.getUserData().loginCode, Base64.encode(Util.bitmap2Bytes(cropBitmap)) , 0 );
		showProgress();
	}
	private void commit(UserInfoView.Type type, UserSelectData data){
		String name   = type == UserInfoView.Type.Name   ? data.name : txtName.getText().toString();
		String sex 	  = type == UserInfoView.Type.Sex 	? data.id : txtSex.getTag().toString();
		if( txtName .getText().toString().equals(name)
				&&txtSex.getTag().toString().equals(sex)
				)//无修改，不提交
			return;

		userService.modifyUserInfo(type, data,UserData.getUserData().loginCode, name, sex, "","", "","","");
		showProgress();
	}
	@Override
	public void onUserInfoBack(UserInfoView.Type type, UserSelectData data) {
		if(data == null)
			return;
		commit(type, data);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == 0) {// camera back
			Bitmap bitmap = Util.readBitmapByPath(imgPath);
			if (bitmap == null) {
				ToastUtil.show(getActivity(), "未获取到图片!");
				return;
			}
			if (null == cropperView)
				cropperView = new CropperView(getActivity(), this , 0 );
			cropperView.cropper(bitmap);
		} else if (requestCode == 1) {// file back
			if (data != null) {
				Bitmap bitmap = null;
				Uri uri = data.getData();
				// url是content开头的格式
				if (uri.toString().startsWith("content://")) {
					String path = null;
					String[] pojo = { MediaStore.Images.Media.DATA };
					Cursor cursor = getActivity().getContentResolver().query(uri, pojo, null,
							null, null);
					// managedQuery(uri, pojo, null, null, null);

					if (cursor != null) {
						// ContentResolver cr = this.getContentResolver();
						int colunm_index = cursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						cursor.moveToFirst();
						path = cursor.getString(colunm_index);

						bitmap = Util.readBitmapByPath(path);
					}

					if (bitmap == null) {
						ToastUtil.show(getActivity(),
								"未获取到图片!");
						return;
					}
				} else if (uri.toString().startsWith("file:///")) {
					String path = uri.toString().substring(8,
							uri.toString().length());
					bitmap = Util.readBitmapByPath(path);
					if (bitmap == null) {
						ToastUtil.show(getActivity(),
								"未获取到图片!");
						return;
					}

				}
				if (null == cropperView)
					cropperView = new CropperView(getActivity(), this , 0 );
				cropperView.cropper(bitmap);
			}

		}

	}

	private Bitmap cropBitmap;
	@Override
	public void OnCropperBack(Bitmap bitmap , int busType) {
		if(null == bitmap)
			return;
		cropBitmap = bitmap;
		commitPhoto();

	}

	@Override
	public void onPhotoSelectBack(PhotoSelectView.SelectType type) {
		if(null == type)
			return;
		getPhotoByType(type);

	}
	private void getPhotoByType(PhotoSelectView.SelectType type){
		switch (type) {
			case Camera:
				getPhotoByCamera();
				break;
			case File:
				getPhotoByFile();
				break;

			default:
				break;
		}
	}
	private String imgPath;
	public void getPhotoByCamera(){
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
			Log.v("TestFile","SD card is not avaiable/writeable right now.");
			return;
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss", Locale.CHINA);
		String imageName = "fm" + sdf.format(date) + ".jpg";
		imgPath = Environment.getExternalStorageDirectory()+ "/"+ imageName;
		File out = new File(imgPath);
		Uri uri;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", out);
		}else{
			uri = Uri.fromFile(out);
		}

		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("fileName", imageName);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 0);
	}
	public void getPhotoByFile(){
		Intent intent2 = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent2, 1);
	}
}
