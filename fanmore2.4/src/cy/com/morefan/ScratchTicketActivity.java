//package cy.com.morefan;
//
//import cy.com.morefan.bean.BaseData;
//import cy.com.morefan.bean.TicketData;
//import cy.com.morefan.bean.UserData;
//import cy.com.morefan.listener.BusinessDataListener;
//import cy.com.morefan.service.UserService;
//import cy.com.morefan.view.MyView.OnScratchListener;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Handler.Callback;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//public class ScratchTicketActivity extends BaseActivity implements Callback{
//	private TextView txtCount;
//	private TicketData ticket;
//	private TextView btnNext;
//	private UserService userService;
//	//private MyView myView;
//	private ImageView imgResult;
//	private Handler mHandler = new Handler(this);
//	@Override
//	public boolean handleMessage(Message msg) {
//		switch (msg.what) {
//		case BusinessDataListener.DONE_GET_TICKET:
//			dismissProgress();
//			String value = ticket.value == 0 ? "再接再厉" : String.valueOf(ticket.value);
//			//myView.againLotter(value);
//			updateViews(false, false);
//			break;
//		case BusinessDataListener.DONE_USE_TOOL:
//			dismissProgress();
//			ticket.residueCount --;
//			updateViews(ticket.residueCount > 0, true);
//			break;
//		case BusinessDataListener.ERROR_GET_TICKET:
//			btnNext.setVisibility(View.VISIBLE);
//			dismissProgress();
//			toast(msg.obj.toString());
//			break;
//		case BusinessDataListener.ERROR_USE_TOOL:
//			dismissProgress();
//			toast(msg.obj.toString());
//			updateViews(false, false);
//			break;
//
//		default:
//			break;
//		}
//		return false;
//	}
//	@Override
//	protected void onCreate(Bundle arg0) {
//		super.onCreate(arg0);
//		setContentView(R.layout.scratch_ticket);
//		btnNext = (TextView) findViewById(R.id.btnNext);
//		txtCount = (TextView) findViewById(R.id.txtCount);
//		myView = (MyView) findViewById(R.id.myView);
//		imgResult = (ImageView) findViewById(R.id.imgResult);
//		myView.setOnScratchListener(this);
//		ticket = new TicketData();
//		userService = new UserService(this);
//		getTicket();
//	}
//
//	private void getTicket() {
//		ticket.value = 0;
//		userService.getTicket(UserData.getUserData().loginCode, ticket);
//		showProgress();
//		updateViews(false,false);
//	}
//
//	private void updateViews(boolean next, boolean showResult) {
//		btnNext.setClickable(next);
//		btnNext.setBackgroundResource(next ? R.drawable.shape_yellow : R.drawable.shape_gray);
//
//
//		imgResult.setVisibility(showResult && ticket.value != 0 ? View.VISIBLE : View.INVISIBLE);
//		txtCount.setText(String.format("您还有%d张刮刮卡", ticket.residueCount));
//
//	}
//	public void onClick(View v){
//		switch (v.getId()) {
//		case R.id.btnNext:
//			getTicket();
//			break;
//		case R.id.btnCommit:
//			getTicket();
//			break;
//
//		default:
//			break;
//		}
//
//	}
//	@Override
//	public void onDataFinish(int type, String des, BaseData[] datas,
//			Bundle extra) {
//		mHandler.obtainMessage(type, extra).sendToTarget();
//		super.onDataFinish(type, des, datas, extra);
//	}
//	@Override
//	public void onDataFailed(int type, String des, Bundle extra) {
//		mHandler.obtainMessage(type, des).sendToTarget();
//		super.onDataFailed(type, des, extra);
//	}
//	@Override
//	public void onScratchBack(float percent) {
//			userService.useTool(UserData.getUserData().loginCode, 2, ticket.id);
//			showProgress();
//
//	}
//}
