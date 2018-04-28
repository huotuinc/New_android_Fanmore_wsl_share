package cy.com.morefan;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.UserBaseInfoList;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import cy.com.morefan.util.Base64;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.ToastUtil;
import cy.com.morefan.util.Util;
import cy.com.morefan.view.CropperView;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.CropperView.OnCropperBackListener;
import cy.com.morefan.view.ImageLoad;
import cy.com.morefan.view.PhotoSelectView;
import cy.com.morefan.view.PhotoSelectView.OnPhotoSelectBackListener;
import cy.com.morefan.view.PhotoSelectView.SelectType;
import cy.com.morefan.view.PopWheelView;
import cy.com.morefan.view.PopWheelView.OnDateBackListener;
import cy.com.morefan.view.SexView;
import cy.com.morefan.view.UserInfoView;
import cy.com.morefan.view.UserInfoView.OnUserInfoBackListener;
import cy.com.morefan.view.UserInfoView.Type;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.w3c.dom.Text;

public class MyBaseInfoActivity extends BaseActivity
		implements OnUserInfoBackListener, Callback, OnDateBackListener,
		BroadcastListener, OnPhotoSelectBackListener, OnCropperBackListener , SexView.OnSexSelectBackListener {
	private String[] YEAR;
	private List<UserSelectData> indutryList;
	private List<UserSelectData> favoriteList;
	private List<UserSelectData> incomeList;
	private UserService userService;
	private TextView txtName;
	private TextView txtSex;
	private TextView txtAge;
	private TextView txtJob;
	private TextView txtIncome;
	private TextView txtFav;
	private TextView txtArea;
	private TextView txtTime;
	private TextView txtNickName;
    private TextView txtSign;
    private TextView txtScore;
    private TextView txtRead;
    private TextView txtTransfor;
    private TextView txtPartner;
	private LinearLayout layAll;
	private UserInfoView userInfoView;
	private SexView sexView;
	private PopWheelView popWheelView;
	private ImageView img;
	private PhotoSelectView pop;
	private CropperView cropperView;
	private Button btnLogOut;
	private SimpleDraweeView ivDoor;

	private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case BusinessDataListener.DONE_COMMIT_PHOTO:
			dismissProgress();

			int type = 0;
			if(msg.obj!=null && ((Bundle)msg.obj).containsKey("type")){
				type= ((Bundle)msg.obj).getInt("type",0);
			}

			if( type==0) {
				img.setImageBitmap(cropBitmap);
			}else{
				ivDoor.setImageURI(UserData.getUserData().photoWall);
			}
			toast("头像上传成功!");
			//commitText();
			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_REFRESH_USEDATA);

			break;
		case BusinessDataListener.ERROR_COMMIT_PHOTO:
			dismissProgress();
			ToastUtil.show(this , "上传图片失败，请重新上传");
//			CustomDialog.showChooiceDialg(this, "头像上传失败", "是否重新上传?", "重传", "取消", null,
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							commitPhoto();
//						}
//					}, null);
			break;
		case BusinessDataListener.DONE_USER_INFO:
			//checkDialog();
			dismissProgress();
			Bundle extra = (Bundle) msg.obj;
			updateView(extra);
			UserBaseInfoList baseInfoList = (UserBaseInfoList) extra.getSerializable("list");
			System.out.println(baseInfoList.toString());
			indutryList = baseInfoList.industryList;
			favoriteList = baseInfoList.favoriteList;
			incomeList = baseInfoList.incomeList;
			break;
		case BusinessDataListener.DONE_MODIFY_USER_INFO:
			toast("修改成功");
			Bundle extra2 = (Bundle) msg.obj;
			modify2UpdateView(extra2);



			dismissProgress();
			break;
		case BusinessDataListener.ERROR_USER_INFO:
		case BusinessDataListener.ERROR_MODIFY_USER_INFO:
			dismissProgress();
			toast(msg.obj.toString());

			if(msg.what == BusinessDataListener.ERROR_USER_INFO)
				finish();
			break;

		default:
			break;
		}
		return false;
	}

	private void modify2UpdateView(Bundle extra) {
		Type type = (Type) extra.getSerializable("type");
		UserSelectData data = (UserSelectData) extra.getSerializable("data");
		switch (type) {
		case Name:
			txtName.setText(data.name);
			txtNickName.setText(data.name);

			UserData.getUserData().RealName = data.name;

			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_REFRESH_USEDATA);

			break;
		case Sex:
			txtSex.setTag(data.id);
			txtSex.setText( Integer.parseInt(data.id) ==1?"男":"女" );
			//txtSex.setBackgroundResource( Integer.parseInt(data.id) == 1 ? R.drawable.sex_male : R.drawable.sex_female);
			break;
		case Age:
			txtAge.setText(data.name);
			break;
		case Job:
			txtJob.setTag(data.id);
			txtJob.setText(data.name);
			break;
		case Fav:
			txtFav.setTag(data.id);
			txtFav.setText(data.name);
			break;
		case Income:
			txtIncome.setTag(data.id);
			txtIncome.setText(data.name);
			break;
		case Sign:
			txtSign.setText(data.name);

			UserData.getUserData().sign = data.name;

			MyBroadcastReceiver.sendBroadcast(this, MyBroadcastReceiver.ACTION_REFRESH_USEDATA);

			break;
		default:
			break;
		}

	}

	private void updateView(Bundle extra) {
		txtName.setText(extra.getString("name"));
		txtNickName.setText(extra.getString("name"));
		int sex = extra.getInt("sex");//1男2女
		txtSex.setTag(sex);
		txtSex.setText( sex==1 ?"男":"女");
		//txtSex.setBackgroundResource( sex == 1 ? R.drawable.sex_male : (sex == 2 ? R.drawable.sex_female : 0));
		txtAge.setText(extra.getString("birth"));
		txtJob.setText(extra.getString("industry"));
		txtJob.setTag(extra.getString("industryId"));
		txtFav.setText(extra.getString("favorite"));
		txtFav.setTag(extra.getString("favoriteId"));
		txtIncome.setText(extra.getString("income"));
		txtIncome.setTag(extra.getString("incomeId"));
		txtArea.setText(extra.getString("area"));
		txtTime.setText(UserData.getUserData().regTime);
		txtSign.setText(extra.getString("signDesc"));

		txtRead.setText(UserData.getUserData().TotalBrowseAmount);
		txtTransfor.setText( UserData.getUserData().TotalTurnAmount );
		txtScore.setText(UserData.getUserData().score);
		txtPartner.setText(UserData.getUserData().PrenticeAmount);
	}

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
		setContentView(R.layout.user_baseinfo);

		initView();


		userInfoView = new UserInfoView(this);
		userInfoView.setOnUserInfoBackListener(this);

		sexView = new SexView(this , this);


		userService = new UserService(this);
		userService.getUserBaseInfo(UserData.getUserData().loginCode);
		showProgress();

		SyncImageLoaderHelper helper = new SyncImageLoaderHelper(this);
		if(TextUtils.isEmpty(UserData.getUserData().picUrl)){
			img.setImageResource(R.drawable.user_icon);
		}else{
			//helper.loadImage(0, img, null, UserData.getUserData().picUrl, Constant.IMAGE_PATH_STORE);
			ImageLoad.loadLogo(UserData.getUserData().picUrl,img,this);
		}

		if(TextUtils.isEmpty(UserData.getUserData().photoWall)){

		}else{
			ivDoor.setImageURI(UserData.getUserData().photoWall);
		}


		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);
	}
	private void checkDialog() {
		if(!UserData.getUserData().completeInfo)
			CustomDialog.showChooiceDialg(this, null, "完善基本信息,可获得经验奖励!", "朕知道了", null, null, null, null);

	}

	private void initView() {
		btnLogOut=(Button) findViewById(R.id.btnLogOut);
		img = (ImageView) findViewById(R.id.img);
		txtName = (TextView) findViewById(R.id.txtName);
		txtSex = (TextView) findViewById(R.id.txtSex);
		txtAge = (TextView) findViewById(R.id.txtAge);
		txtJob = (TextView) findViewById(R.id.txtJob);
		txtIncome = (TextView) findViewById(R.id.txtIncome);
		txtFav = (TextView) findViewById(R.id.txtFav);
		txtArea = (TextView) findViewById(R.id.txtArea);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtNickName = (TextView)findViewById(R.id.txtNickName);
		txtSign = (TextView)findViewById(R.id.txtSign);
		ivDoor= (SimpleDraweeView)findViewById(R.id.ivDoor);
		layAll = (LinearLayout) findViewById(R.id.layAll);
		txtScore = (TextView)findViewById(R.id.txtScore);
		txtTransfor=(TextView)findViewById(R.id.txtTransfor);
		txtRead=(TextView)findViewById(R.id.txtRead);
		txtPartner=(TextView)findViewById(R.id.txtPartner);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogOut:
			CustomDialog.showChooiceDialg(this, null, "确定要注销吗？", "注销", "取消", null, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					logout();
					Intent intentlogin	 = new Intent(MyBaseInfoActivity.this,MoblieLoginActivity.class);
					startActivity(intentlogin);
					finish();

				}
			}, null);
			break;
		case R.id.layImg:
			if(null == pop)
				pop = new PhotoSelectView(this, this);
			pop.setHide();
			pop.show();

			break;
		case R.id.laySex:

			//List<UserSelectData> sexDatas = new ArrayList<UserSelectData>();
			//1男2女
			//sexDatas.add(new UserSelectData("男","1"));
			//sexDatas.add(new UserSelectData("女", "2"));
			//userInfoView.show(Type.Sex, sexDatas, txtSex.getTag().toString());
			//mainZoomOut(layAll);
			sexView.show();
			break;
		case R.id.layName:
			userInfoView.show(Type.Name, null, txtName.getText().toString());
			break;
		case R.id.layJob:
			userInfoView.show(Type.Job, indutryList, txtJob.getTag().toString());
			//mainZoomOut(layAll);
			break;
		case R.id.layFav:
			userInfoView.show(Type.Fav, favoriteList, txtFav.getTag().toString());
			//mainZoomOut(layAll);
			break;
		case R.id.layIncome:
			userInfoView.show(Type.Income, incomeList, txtIncome.getTag().toString());
			//mainZoomOut(layAll);
			break;
		case R.id.layAge:
			if(YEAR == null)
				initYears();
			if(popWheelView == null){
				popWheelView = new PopWheelView(this);
				popWheelView.setOnDateBackListener(this);
			}

			//mainZoomOut(layAll);
			popWheelView.show(txtAge.getText().toString().trim());
			//userInfoView.show(Type.Age, YEAR);
			break;
        case R.id.layNickName://我的昵称
            Intent intent = new Intent(this , SignActivity.class);
            intent.putExtra("title", "昵称");
            intent.putExtra("content" , txtNickName.getText().toString());
            startActivityForResult(intent,4001);
            break;
        case R.id.laySign://我的个签
            Intent intentSign = new Intent(this , SignActivity.class);
            intentSign.putExtra("content" , txtSign.getText().toString());
            intentSign.putExtra("title","我的个签");
            startActivityForResult(intentSign , 4000);
            break;
		case R.id.layDoor://照片墙
			if(null == pop)
				pop = new PhotoSelectView(this, this);

			pop.setShow();
			pop.show();
				break;
        case R.id.btnBack:
            this.finish();
            break;
		default:
			break;
		}
	}

	public void logout(){
		SPUtil.saveStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_PRE_USERNAME, UserData.getUserData().userName);
		UserData.clear();
		SPUtil.saveStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_BuserId, "");
		SPUtil.saveStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_UnionId, "");
		//清除微信授权信息
		ShareSDK.getPlatform(Wechat.NAME).removeAccount();



		SPUtil.saveStringToSpByName(this, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERPWD, "");
	}

	private void initYears() {
		YEAR = new String[60];
		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR) - 10;
		for(int i = 0; i < 60; i++){
			YEAR[i] = curYear - i + "";

		}

	}

	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		if(type == BusinessDataListener.DONE_USER_INFO){
			mHandler.obtainMessage(type, extra).sendToTarget();
		}else if(type == BusinessDataListener.DONE_MODIFY_USER_INFO){
			mHandler.obtainMessage(type, extra).sendToTarget();
		}else if(type == BusinessDataListener.DONE_COMMIT_PHOTO){
			mHandler.obtainMessage(type, extra ).sendToTarget();
		}
		super.onDataFinish(type, des, datas, extra);
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		mHandler.obtainMessage(type, des).sendToTarget();
		super.onDataFailed(type, des, extra);
	}



	private void commitPhoto( Bitmap bitmap , int busType ){
		userService.commitPhoto(UserData.getUserData().loginCode, Base64.encode(Util.bitmap2Bytes(bitmap)) , busType );
		showProgress();
	}

	private void commit(Type type, UserSelectData data){
		String name   = type == Type.Name   ? data.name : txtNickName.getText().toString();
		String age    = type == Type.Age    ? data.name : txtAge.getText().toString();
		String sex 	  = type == Type.Sex 	? data.id : txtSex.getTag().toString();
		String job	  = type == Type.Job 	? data.id : txtJob.getTag().toString();
		String income = type == Type.Income ? data.id : txtIncome.getTag().toString();
		String fav 	  = type == Type.Fav	? data.id : txtFav.getTag().toString();
		String sign   = type == Type.Sign   ? data.name : txtSign.getText().toString();
		if( txtNickName .getText().toString().equals(name)
		&& txtAge.getText().toString().equals(age)
		&&txtSex.getTag().toString().equals(sex)
		&& txtJob.getTag().toString().equals(job)
		&& txtIncome.getTag().toString().equals(income)
		&& txtFav.getTag().toString().equals(fav)
		&& txtSign.getText().toString().equals(sign))//无修改，不提交
			return;

		userService.modifyUserInfo(type, data,UserData.getUserData().loginCode, name, sex, age,job, fav,income , sign );
		showProgress();
	}
	@Override
	public void onUserInfoBack(Type type, UserSelectData data) {
		if(data == null)
			return;
		commit(type, data);
	}

	@Override
	public void onDateBack(String date) {
		if(!TextUtils.isEmpty(date)){
			//txtAge.setText(date);
			UserSelectData data = new UserSelectData(date, "");
			commit(Type.Age, data);
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



	private String imgPath;
	public void getPhotoByCamera( int reqcode ){
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
		Uri uri ;
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(this , BuildConfig.APPLICATION_ID + ".provider", out);
		}else {
			uri = Uri.fromFile(out);
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("fileName", imageName);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, reqcode);
	}


	public void getPhotoByFile(){
		Intent intent2 = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent2, 1);
	}

	public void getPhoneByFile2(){
		Intent intent2 = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent2, 3);
	}

	@Override
	public void onPhotoSelectBack(SelectType type) {
		if(null == type)
			return;
		getPhotoByType(type);

	}
	private void getPhotoByType(SelectType type){
		switch (type) {
		case Camera:
			getPhotoByCamera(0);
			break;
		case File:
			getPhotoByFile();
			break;
		case DoorFile:
			getPhoneByFile2();
			break;
		case DoorCamera:
			getPhotoByCamera(4);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		if (requestCode == 0) {// camera back
			Bitmap bitmap = Util.readBitmapByPath(imgPath);
			if (bitmap == null) {
				ToastUtil.show(MyBaseInfoActivity.this, "未获取到图片!");
				return;
			}
			if (null == cropperView)
				cropperView = new CropperView(this, this,0);
			cropperView.setBusType(0);
			cropperView.cropper(bitmap);
		} else if (requestCode == 1) {// file back
			if (data != null) {
				Bitmap bitmap = null;
				Uri uri = data.getData();
				// url是content开头的格式
				if (uri.toString().startsWith("content://")) {
					String path = null;
					String[] pojo = { MediaStore.Images.Media.DATA };
					Cursor cursor = getContentResolver().query(uri, pojo, null,
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
						ToastUtil.show(MyBaseInfoActivity.this,
								"未获取到图片!");
						return;
					}
				} else if (uri.toString().startsWith("file:///")) {
					String path = uri.toString().substring(8,
							uri.toString().length());
					bitmap = Util.readBitmapByPath(path);
					if (bitmap == null) {
						ToastUtil.show(MyBaseInfoActivity.this,
								"未获取到图片!");
						return;
					}

				}
				if (null == cropperView)
					cropperView = new CropperView(this, this, 0);
				cropperView.setBusType(0);
				cropperView.cropper(bitmap);
			}

		}else if(requestCode==3){//选择照片墙图片
			selectPic(data);
		}else if(requestCode==4){//
			Bitmap bitmap = Util.readBitmapByPath(imgPath);
			if (bitmap == null) {
				ToastUtil.show(MyBaseInfoActivity.this, "未获取到图片!");
				return;
			}
			if (null == cropperView)
				cropperView = new CropperView(this, this , 1);
			cropperView.setBusType(1);
			cropperView.cropper(bitmap);
		}
		else if(requestCode == 4000){//个签
		    //txtSign.setText( data.getStringExtra("content") );
			UserSelectData bean = new UserSelectData(data.getStringExtra("content"), data.getStringExtra("content"));
			commit( Type.Sign , bean);

        }else if(requestCode==4001){//昵称
		    //txtNickName.setText( data.getStringExtra("content") );
			UserSelectData bean = new UserSelectData(data.getStringExtra("content"),data.getStringExtra("content"));
			commit(Type.Name , bean);
        }

	}

	private void selectPic(Intent data){
		if (data != null) {
			Bitmap bitmap = null;
			Uri uri = data.getData();
			// url是content开头的格式
			if (uri.toString().startsWith("content://")) {
				String path = null;
				String[] pojo = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(uri, pojo, null,
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
					ToastUtil.show(MyBaseInfoActivity.this,
							"未获取到图片!");
					return;
				}
			} else if (uri.toString().startsWith("file:///")) {
				String path = uri.toString().substring(8,
						uri.toString().length());
				bitmap = Util.readBitmapByPath(path);
				if (bitmap == null) {
					ToastUtil.show(MyBaseInfoActivity.this,
							"未获取到图片!");
					return;
				}

			}
			if (null == cropperView)
				cropperView = new CropperView(this, this,1);
			cropperView.setBusType(1);
			cropperView.cropper(bitmap);
		}
	}


	private Bitmap cropBitmap;
	@Override
	public void OnCropperBack(Bitmap bitmap , int busType ) {
		if(null == bitmap)
			return;
		cropBitmap = bitmap;
		commitPhoto( cropBitmap , busType);

	}


	@Override
	public void onSexSelectBack(SexView.SelectType type) {
		if(null == type)
			return;

		UserSelectData bean = new UserSelectData(type== SexView.SelectType.man? "男":"女", type== SexView.SelectType.man?"1":"2");

		commit( Type.Sex , bean);

	}
}
