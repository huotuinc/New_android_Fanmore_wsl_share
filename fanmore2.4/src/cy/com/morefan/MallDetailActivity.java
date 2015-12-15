package cy.com.morefan;

import cy.com.morefan.bean.BaseData;
import cy.com.morefan.bean.BuyData;
import cy.com.morefan.bean.BuyItemData;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.listener.BusinessDataListener;
import cy.com.morefan.listener.MyBroadcastReceiver;
import cy.com.morefan.listener.MyBroadcastReceiver.BroadcastListener;
import cy.com.morefan.listener.MyBroadcastReceiver.ReceiverType;
import cy.com.morefan.service.UserService;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MallDetailActivity extends BaseActivity implements BroadcastListener, Callback{
	private LinearLayout layList;
	private BuyData mBuyData;
	private UserService mUserService;
	private TextView txtGoodsName;
	private TextView txtOrderNo;
	private TextView txtTime;
	private TextView txtName;
	private TextView txtTemp;
	private TextView txtReal;
	private TextView txtDayCount;
	private TextView txtStatus;
	private MyBroadcastReceiver myBroadcastReceiver;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case BusinessDataListener.DONE_GET_MALL_DETAIL:
			dismissProgress();
			updateView();
			break;
		case BusinessDataListener.ERROR_GET_MALL_DETAIL:
			dismissProgress();
			toast(msg.obj.toString());
			finish();
			break;


		default:
			break;
		}
		return false;
	}


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.mall_detail);
		layList = (LinearLayout) findViewById(R.id.layList);
		txtGoodsName = (TextView) findViewById(R.id.txtGoodsName);
		txtOrderNo = (TextView) findViewById(R.id.txtOrderNo);
		txtTime = (TextView) findViewById(R.id.txtTime);
		txtName = (TextView) findViewById(R.id.txtName);
		txtTemp = (TextView) findViewById(R.id.txtTemp);
		txtReal = (TextView) findViewById(R.id.txtReal);
		txtDayCount = (TextView) findViewById(R.id.txtDayCount);
		txtStatus = (TextView) findViewById(R.id.txtStatus);


		mBuyData = new BuyData();
		mUserService = new UserService(this);

		String orderNo = getIntent().getExtras().getString("orderNo");
		mBuyData.orderNo = orderNo;

		mUserService.getMallDetail(mBuyData, UserData.getUserData().loginCode);
		showProgress();
		myBroadcastReceiver = new MyBroadcastReceiver(this, this, MyBroadcastReceiver.ACTION_BACKGROUD_BACK_TO_UPDATE);



	}
	private void updateView() {
		txtGoodsName.setText(mBuyData.goodsName);
		txtOrderNo.setText(mBuyData.orderNo);

		txtTime.setText(mBuyData.time);
		txtName.setText(mBuyData.name);
		txtTemp.setText(String.valueOf(mBuyData.tempScore));
		txtReal.setText(String.valueOf(mBuyData.realScore ));
//		String time = null;
//		if(mBuyData.status == 0)
//			time = mBuyData.timeCount;
//		else if(mBuyData.status == 1)
//			time = "已到账";
//		else
//			time = "积分扣除";
		txtDayCount.setText(mBuyData.timeCount);
		//1、积分临时 2、积分正式 3、积分扣除
		txtStatus.setText(mBuyData.statusDes);
		//txtStatus.setText(getStatusDes(mBuyData.status));
		setList();

	}
//	private String getStatusDes(int status){
//		switch (status) {
//		case 0:
//
//			return "积分临时";
//		case 1:
//			return "积分到账";
//		case -1:
//		case -2:
//			return "积分扣除";
//
//		default:
//			return "未知";
//		}
//	}

	public void setList(){

		for(int i = 0, length = mBuyData.listDatas.size(); i < length; i ++){
			View view = LayoutInflater.from(this).inflate(R.layout.mall_detail_item, null);
			ImageView imgLine = (ImageView) view.findViewById(R.id.imgLine);
			ImageView imgTag = (ImageView) view.findViewById(R.id.imgTag);
			TextView txtStatus = (TextView) view.findViewById(R.id.txtStatus);
			TextView txtScore = (TextView) view.findViewById(R.id.txtScore);
			TextView txtTime = (TextView) view.findViewById(R.id.txtTime);
			imgLine.setVisibility(i == 0 ? View.INVISIBLE : View.VISIBLE);
			imgTag.setBackgroundResource(i == length - 1 ? R.drawable.kuaidi_blue : R.drawable.kuaidi_gray);
			txtStatus.setTextColor(i == length - 1  ? Color.BLACK : Color.GRAY);
			txtScore.setTextColor(i == length - 1  ? Color.BLACK : Color.GRAY);
			txtTime.setTextColor(i == length - 1  ? Color.BLACK : Color.GRAY);
			BuyItemData item = mBuyData.listDatas.get(i);
			txtStatus.setText(item.action);
			txtScore.setText(item.score + "分");
			txtTime.setText(item.time);

			layList.addView(view);

		}
	}
	@Override
	public void onDataFailed(int type, String des, Bundle extra) {
		super.onDataFailed(type, des, extra);
		mHandler.obtainMessage(type, des).sendToTarget();
	}
	@Override
	public void onDataFinish(int type, String des, BaseData[] datas,
			Bundle extra) {
		super.onDataFinish(type, des, datas, extra);
		mHandler.obtainMessage(type).sendToTarget();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		myBroadcastReceiver.unregisterReceiver();
	}

	@Override
	public void onFinishReceiver(ReceiverType type, Object msg) {
		if(type == ReceiverType.BackgroundBackToUpdate){
			finish();

		}

	}

}
