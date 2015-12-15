//package cy.com.morefan;
//
//import com.nineoldandroids.animation.AnimatorSet;
//import com.nineoldandroids.animation.ObjectAnimator;
//
//import cy.com.morefan.bean.BaseData;
//import cy.com.morefan.bean.ShakePrenticeData;
//import cy.com.morefan.bean.UserData;
//import cy.com.morefan.listener.BusinessDataListener;
//import cy.com.morefan.service.UserService;
//import cy.com.morefan.util.DensityUtil;
//
//import cy.com.morefan.view.CustomDialog;
//import android.R.integer;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Handler.Callback;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//public class ShakePrenticeActivity extends BaseActivity implements Callback{
//	private UserService userService;
////	private Shake shake;
//	private ShakePrenticeData shakeData;
//	private ImageView img;
//	private TextView txtName;
//	private TextView txtCount;
//	private TextView txtReg;
//	private TextView txtSex;
//	private ImageView imgTop;
//	private ImageView imgBottom;
//	private RelativeLayout layResult;
//	private int ANIMATION_TIME = 1200;
//	private long curTime;
//	private boolean gettingData;
//	private boolean hasUse;//是否已收徒
//	private Handler mHandler = new Handler(this);
//	@Override
//	public boolean handleMessage(Message msg) {
//		switch (msg.what) {
//		case 1:
//			if(gettingData){
//				showProgress();
//			}else{
//				updateView();
//			}
//			break;
//		case BusinessDataListener.DONE_SHAKE_PRENTICE:
//			hasUse = false;
//			dismissProgress();
//			gettingData = false;
//			if(System.currentTimeMillis() - curTime >= ANIMATION_TIME)
//				updateView();
//
//			break;
//		case BusinessDataListener.ERROR_SHAKE_PRENTICE:
//		case BusinessDataListener.ERROR_USE_TOOL:
//			gettingData = false;
//			toast(msg.obj.toString());
//			dismissProgress();
//			break;
//		case BusinessDataListener.DONE_USE_TOOL:
//			hasUse = true;
//			dismissProgress();
//			toast("收徒成功！");
//			hideResultAnim();
//			Bundle extra = (Bundle) msg.obj;
//			//道具数量为0时，返回
//			if(null != extra && extra.getInt("count") == 0)
//				finish();
//			break;
//
//		default:
//			break;
//		}
//		return false;
//	}
//
//	@Override
//	protected void onCreate(Bundle arg0) {
//		// TODO Auto-generated method stub
//		super.onCreate(arg0);
//		setContentView(R.layout.shake_prentice);
//		userService = new UserService(this);
//		shake = new Shake(this, this);
//
//		shakeData = new ShakePrenticeData();
//		initView();
//	}
//	@Override
//	protected void onResume() {
//		shake.start();
//		super.onResume();
//	}
//	@Override
//	protected void onPause() {
//		shake.stop();
//		super.onPause();
//	}
//	@Override
//	protected void onDestroy() {
//		shake.stop();
//		super.onDestroy();
//	}
//	private void initView() {
//		imgTop = (ImageView) findViewById(R.id.imgTop);
//		imgBottom = (ImageView) findViewById(R.id.imgBottom);
//		img  = (ImageView) findViewById(R.id.img);
//		txtName = (TextView) findViewById(R.id.txtName);
//		txtCount = (TextView) findViewById(R.id.txtCount);
//		txtReg = (TextView) findViewById(R.id.txtReg);
//		layResult = (RelativeLayout) findViewById(R.id.layResult);
//		txtSex = (TextView) findViewById(R.id.txtSex);
//		layResult.setVisibility(View.GONE);
//
//	}
//
//	public void onClick(View v){
//		switch (v.getId()) {
//		case R.id.btn:
//			View contentView = getLayoutInflater().inflate(R.layout.custom_dialog_tool_buy, null);
//			ImageView img = (ImageView) contentView.findViewById(R.id.img);
//			TextView txtDes = (TextView) contentView.findViewById(R.id.txtDes);
//			contentView.findViewById(R.id.txtValue).setVisibility(View.GONE);
//
//			img.setBackgroundResource(R.drawable.tool_prentice2);
//			txtDes.setText("招收徒弟将消耗一个道具！确定收此徒弟吗？");
//			CustomDialog.showChooiceDialg(this, null, null, "收徒", "取消", contentView,
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							userService.useTool(UserData.getUserData().loginCode, 1, shakeData.id);
//							showProgress();
//						}
//					}, null);
//
//
//
//
//			break;
//
//		default:
//			break;
//		}
//	}
//	private void updateView() {
//		if(!shakeData.hasResult)
//			return;
//		showResultAnim();
//		//添加摇动成功音效
//		shake.shakeSound(R.raw.shake_match);
//		layResult.setVisibility(View.VISIBLE);
//		//img
//		txtName.setText(shakeData.name);
//		txtCount.setText("转发次数:" + shakeData.sentCount);
//		txtReg.setText("注册时间:" + shakeData.regTime);
//		txtSex.setVisibility(shakeData.sex == 0 ? View.GONE : View.VISIBLE);
//		//String sex = shakeData.sex == 1 ? "男" : (shakeData.sex == 2 ? "女" : "未知");
//		txtSex.setBackgroundResource(shakeData.sentCount == 1 ? R.drawable.sex_male : R.drawable.sex_female);
//
//	}
//	@Override
//	public void onShake() {
//		gettingData = true;
//		curTime = System.currentTimeMillis();
//		if(!hasUse)
//			hideResultAnim();
//		//添加摇动音效
//		shake.shakeSound(R.raw.shake_sound_male);
//		startAnim();
//		shakeData.clear();
//		userService.shakePrentice(UserData.getUserData().loginCode, shakeData);
//		//delay updateView
//		Message msg = mHandler.obtainMessage();
//		msg.what = 1;
//		mHandler.sendMessageDelayed(msg, ANIMATION_TIME);
////		showProgress();
//
//	}
//	private void showResultAnim(){
//		layResult.setVisibility(View.VISIBLE);
//		AnimatorSet set = new AnimatorSet();
//		set.playTogether(
////		    ObjectAnimator.ofFloat(myView, "rotationX", 0, 360),
////		    ObjectAnimator.ofFloat(myView, "rotationY", 0, 180),
////		    ObjectAnimator.ofFloat(myView, "rotation", 0, -90),
////		    ObjectAnimator.ofFloat(myView, "translationX", 0, 90),
//		    ObjectAnimator.ofFloat(layResult, "translationY", -DensityUtil.dip2px(this, 550), 0)
////		    ObjectAnimator.ofFloat(myView, "scaleX", 0.8f, 1),
////		    ObjectAnimator.ofFloat(myView, "scaleY", 0.8f, 1)
////		    ObjectAnimator.ofFloat(myView, "alpha", 1, 0.25f, 1)
//		);
//		set.setDuration(ANIMATION_TIME/2).start();
//	}
//	private void hideResultAnim(){
//		if(layResult.getVisibility() == View.GONE)
//			return;
//		AnimatorSet set = new AnimatorSet();
//		set.playTogether(
////		    ObjectAnimator.ofFloat(myView, "rotationX", 0, 360),
////		    ObjectAnimator.ofFloat(myView, "rotationY", 0, 180),
////		    ObjectAnimator.ofFloat(myView, "rotation", 0, -90),
////		    ObjectAnimator.ofFloat(myView, "translationX", 0, 90),
//		    ObjectAnimator.ofFloat(layResult, "translationY", 0, DensityUtil.dip2px(this, 300))
////		    ObjectAnimator.ofFloat(myView, "scaleX", 0.8f, 1),
////		    ObjectAnimator.ofFloat(myView, "scaleY", 0.8f, 1)
////		    ObjectAnimator.ofFloat(myView, "alpha", 1, 0.25f, 1)
//		);
//		set.setDuration(ANIMATION_TIME/2).start();
//	}
//	private void startAnim() {
//		int dis = DensityUtil.dip2px(this, 70);
//		AnimatorSet set = new AnimatorSet();
//		set.playTogether(
////		    ObjectAnimator.ofFloat(myView, "rotationX", 0, 360),
////		    ObjectAnimator.ofFloat(myView, "rotationY", 0, 180),
////		    ObjectAnimator.ofFloat(myView, "rotation", 0, -90),
////		    ObjectAnimator.ofFloat(myView, "translationX", 0, 90),
//		    ObjectAnimator.ofFloat(imgTop, "translationY", 0, -dis),
//		    ObjectAnimator.ofFloat(imgBottom, "translationY", 0, dis)
////		    ObjectAnimator.ofFloat(myView, "scaleX", 0.8f, 1),
////		    ObjectAnimator.ofFloat(myView, "scaleY", 0.8f, 1)
////		    ObjectAnimator.ofFloat(myView, "alpha", 1, 0.25f, 1)
//		);
//		set.setDuration(ANIMATION_TIME/2).start();
//
//		AnimatorSet set2 = new AnimatorSet();
//		set2.playTogether(
////		    ObjectAnimator.ofFloat(myView, "rotationX", 0, 360),
////		    ObjectAnimator.ofFloat(myView, "rotationY", 0, 180),
////		    ObjectAnimator.ofFloat(myView, "rotation", 0, -90),
////		    ObjectAnimator.ofFloat(myView, "translationX", 0, 90),
//		    ObjectAnimator.ofFloat(imgTop, "translationY", -dis, 0),
//		    ObjectAnimator.ofFloat(imgBottom, "translationY", dis, 0)
////		    ObjectAnimator.ofFloat(myView, "scaleX", 0.8f, 1),
////		    ObjectAnimator.ofFloat(myView, "scaleY", 0.8f, 1)
////		    ObjectAnimator.ofFloat(myView, "alpha", 1, 0.25f, 1)
//		);
//		set2.setStartDelay(ANIMATION_TIME/2);
//		set2.setDuration(ANIMATION_TIME/2).start();
//
//	}
//
//	@Override
//	public void onDataFinish(int type, String des, BaseData[] datas, Bundle extra) {
//		super.onDataFinish(type, des, datas, extra);
//		mHandler.obtainMessage(type, extra).sendToTarget();
//	}
//	@Override
//	public void onDataFailed(int type, String des, Bundle extra) {
//		super.onDataFailed(type, des, extra);
//		mHandler.obtainMessage(type, des).sendToTarget();
//	}
//
//}
