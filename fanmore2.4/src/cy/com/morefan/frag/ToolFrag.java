//package cy.com.morefan.frag;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cindy.android.test.synclistview.SyncImageLoaderHelper;
//
//import com.nineoldandroids.animation.ObjectAnimator;
//
//import cy.com.morefan.HomeActivity;
//
//import cy.com.morefan.R;
//import cy.com.morefan.TaskActivity;
//import cy.com.morefan.WebViewActivity;
//import cy.com.morefan.adapter.MyToolAdapter;
//import cy.com.morefan.bean.BaseData;
//import cy.com.morefan.bean.ToolData;
//import cy.com.morefan.bean.UserData;
//import cy.com.morefan.constant.BusinessStatic;
//import cy.com.morefan.constant.Constant;
//import cy.com.morefan.constant.Constant.FromType;
//import cy.com.morefan.listener.BusinessDataListener;
//import cy.com.morefan.service.UserService;
//import cy.com.morefan.view.CustomDialog;
//import cy.com.morefan.view.ElasticScrollView;
//import cy.com.morefan.view.HeadView;
//import cy.com.morefan.view.ElasticScrollView.OnRefreshListener;
//import cy.com.morefan.view.HorizontalListView;
//import cy.com.morefan.view.ElasticScrollView.ScrollType;
//import cy.com.morefan.view.HorizontalListView.HscrollLister;
//import cy.com.morefan.view.PullDownUpListView;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Handler.Callback;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//public class ToolFrag extends BaseFragment implements OnClickListener, Callback, BusinessDataListener{
//	private static ToolFrag frag;
//	private View mRootView;
//	private UserService userService;
//	private HorizontalListView hListView;
//	private PullDownUpListView listView;
//	private List<ToolData> tools;
//	private List<ToolData> myTools;
//	private MyToolAdapter adapter;
//	private LinearLayout layTool;
//	private LinearLayout layMyTool;
//	private TextView txtExp;
//	private ImageView img;
//	private ObjectAnimator objAnim;
//	private SyncImageLoaderHelper helper;
//	private ElasticScrollView scrollView;
//	private Handler mHandler = new Handler(this);
//	@Override
//	public boolean handleMessage(Message msg) {
//		switch (msg.what) {
//		case BusinessDataListener.DONE_GET_TOOL_LIST:
//			head.onRefreshComplete();
//			scrollView.onRefreshComplete();
//			dismissProgress();
//			updateTools();
//			break;
//		case BusinessDataListener.DONE_BUY_TOOL:
//			toast("购买成功!");
//			dismissProgress();
//			updateTools();
//			break;
//		case BusinessDataListener.ERROR_GET_TOOL_LIST:
//		case BusinessDataListener.ERROR_BUY_TOOL:
//			head.onRefreshComplete();
//			scrollView.onRefreshComplete();
//			dismissProgress();
//			toast(msg.obj.toString());
//			break;
//
//		default:
//			break;
//		}
//		return false;
//	}
//
//
//	public static ToolFrag newInstance(){
//		if(frag == null)
//			frag = new ToolFrag();
//		return frag;
//	}
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		userService = new UserService(this);
//		tools = new ArrayList<ToolData>();
//		myTools = new ArrayList<ToolData>();
//		helper = new SyncImageLoaderHelper(getActivity());
//	}
//	private HeadView head;
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		mRootView = inflater.inflate(R.layout.tab_tool, container, false);
//		hListView = (HorizontalListView) mRootView.findViewById(R.id.hListView);
//		img = (ImageView) mRootView.findViewById(R.id.imgPhoto);
//		adapter = new MyToolAdapter(getActivity(), myTools);
//		hListView.setAdapter(adapter);
//		scrollView = (ElasticScrollView) mRootView.findViewById(R.id.layScroll);
//		head = new HeadView(getActivity());
////		View head = getActivity().getLayoutInflater().inflate(R.layout.tool_head, null);
////		final ImageView img1 = (ImageView) head.findViewById(R.id.img1);
//		scrollView.addHeadView(head.getView());
//		scrollView.setOnRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh(float per, ScrollType type) {
//				head.onRefresh(per, type);
//				if(type == ScrollType.REFRESHING)
//					initData();
//
//			}
//		});
//		hListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//					long arg3) {
//				ToolData item = myTools.get(position);
//				if(item.type == 1){//摇徒弟
////					Intent intentP = new Intent(getActivity(), ShakePrenticeActivity.class);
////					startActivity(intentP);
//				}else if(item.type == 3){//任务转发提限
//					CustomDialog.showChooiceDialg(getActivity(), item.name, item.desc + ";转发任务时直接使用!", "朕知道了", null, null, null, null);
//				}else if(item.type == 4){//任务提前预览
//					Intent intent = new Intent(getActivity(), TaskActivity.class);
//	  	        	intent.putExtra(Constant.TYPE_FROM, FromType.PreTask);
//	  	        	startActivity(intent);
//				}else if(item.type == 2){
////					Intent intentTicket = new Intent(getActivity(), ScratchTicketActivity.class);
////	  	        	startActivity(intentTicket);
//				}
//
//			}
//		});
//		hListView.setOnHscrollLister(new HscrollLister() {
//
//			@Override
//			public void onHScroll(boolean enable) {
//				if(null != getActivity())
//					((HomeActivity)getActivity()).setDragable(enable);
//			}
//		});
//
//		layTool = (LinearLayout) mRootView.findViewById(R.id.layTool);
//		layMyTool = (LinearLayout) mRootView.findViewById(R.id.layMyTool);
//		txtExp = (TextView) mRootView.findViewById(R.id.txtExp);
//
////		listView = (PullDownUpListView) mRootView.findViewById(R.id.listView);
////		ToolAdapter adapter = new ToolAdapter(getActivity());
////		listView.setAdapter(adapter);
////		adapter.setOnItemClickListener(new ToolAdapter.OnItemClickListener() {
////			@Override
////			public void onItemClick(int position) {
////				View contentView = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_tool_buy, null);
////				CustomDialog.showChooiceDialg(getActivity(), null, null, "兑换", "取消", contentView, null, null);
////				}
////		});
//
//		return mRootView;
//	}
//
//	public void initData() {
//		userService.getToolList(UserData.getUserData().loginCode, tools, myTools);
//		showProgress();
//	}
//	private void updateTools(){
//		if(null == getActivity())
//			return;
//
//		if(TextUtils.isEmpty(UserData.getUserData().picUrl)){
//			img.setImageResource(R.drawable.user_icon);
//		}else{
//			helper.loadImage(0, img, null, UserData.getUserData().picUrl, Constant.IMAGE_PATH_STORE);
//		}
//		//update exp
//		txtExp.setText(UserData.getUserData().isLogin ? "Exp." + UserData.getUserData().exp : "Exp.未登录");
//		//
//		adapter.setDatas(myTools);
//		layMyTool.setVisibility(myTools.size() == 0 ? View.GONE : View.VISIBLE);
//		layTool.removeAllViews();
//		for(int i = 0, length = tools.size(); i < length; i++){
//			View v = getActivity().getLayoutInflater().inflate(R.layout.tab_tool_item, null);
//			ImageView img = (ImageView) v.findViewById(R.id.img);
//			TextView txtName = (TextView) v.findViewById(R.id.txtName);
//			TextView txtValue = (TextView) v.findViewById(R.id.txtValue);
//			TextView txtDes = (TextView) v.findViewById(R.id.txtDes);
//			TextView btn = (TextView) v.findViewById(R.id.btn);
//
//			final ToolData tool = tools.get(i);
//			v.setBackgroundColor(i%2==0 ? 0xffeaeaea : Color.WHITE);
//			img.setBackgroundResource(getImgId(tool.type));
//			txtName.setText(tool.name);
//			txtValue.setText(tool.value + "经验");
//			txtDes.setText(tool.desc);
//			btn.setText(tool.totalCount == -1 ? "购买" : (tool.residueCount == 0 ? "今日售完" : (tool.residueCount + "/" + tool.totalCount)));
//			btn.setBackgroundResource(tool.totalCount != -1 && tool.residueCount == 0 ? R.drawable.shape_gray_sel : R.drawable.shape_yellow_sel);
//			btn.setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if(tool.totalCount != -1 && tool.residueCount == 0){
//						toast("今日售完!");
//						return;
//					}
//					if(tool.value > UserData.getUserData().exp){
//						toast("经验不足!");
//						return;
//					}
//					View contentView = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_tool_buy, null);
//					ImageView img = (ImageView) contentView.findViewById(R.id.img);
//					TextView txtDes = (TextView) contentView.findViewById(R.id.txtDes);
//					TextView txtValue = (TextView) contentView.findViewById(R.id.txtValue);
//
//					img.setBackgroundResource(getImgId(tool.type));
//					txtDes.setText(tool.desc);
//					txtValue.setText("消耗:" + tool.value + "经验");
//					CustomDialog.showChooiceDialg(getActivity(), null, null, "购买", "取消", contentView,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									userService.buyTool(UserData.getUserData().loginCode, tool.type, tool.value, tools, myTools);
//									showProgress();
//								}
//							}, null);
//
//				}
//			});
//			layTool.addView(v);
//
//		}
//
//	}
//	/**
//	 * 道具类型
//	 * 1 摇徒弟
//	 * 2抽奖
//	 * 3任务转发提限
//	 * 4任务提前预览
//	 */
//	public int getImgId(int type){
//		switch (type) {
//		case 1:
//			return R.drawable.tool_prentice2;
//		case 2:
//			return R.drawable.tool_shake2;
//		case 3:
//			return R.drawable.tool_sendcount2;
//		case 4:
//			return R.drawable.tool_sendtime2;
//
//		default:
//			return R.drawable.tool_prentice2;
//		}
//
//	}
//
//
//	public boolean canotMove(){
//		return 0 != hListView.getFirstVisiblePosition() && hListView.isDragging();
//	}
//
//	@Override
//	public void onResume() {
//		initData();
//		super.onResume();
//	}
//	@Override
//	public void onReshow() {
//		initData();
//	}
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
////		case R.id.layExp:
////			if(!UserData.getUserData().isLogin){
////				Intent intentlogin = new Intent(getActivity(), LoginActivity.class);
////				startActivity(intentlogin);
////				return;
////			}else{
//////				Intent intentExp = new Intent(getActivity(), HistoryMoneyChangeActivity.class);
//////				intentExp.putExtra("actionType", ActionType.Exp);
//////				startActivity(intentExp);
////				Intent intentExp = new Intent(getActivity(), DataListActivity.class);
////				intentExp.putExtra(DataListActivity.ACTVITY_TYPE, ActivityType.ExpHistory);
////				startActivity(intentExp);
////			}
////
////			break;
//		case R.id.btnRight:
//			Intent intentRule = new Intent(getActivity(), WebViewActivity.class);
//			intentRule.putExtra("url", BusinessStatic.getInstance().URL_TOOL);
//			intentRule.putExtra("title", "道具说明");
//			startActivity(intentRule);
//			break;
//
//		default:
//			break;
//		}
//
//
//	}
//
//	@Override
//	public void onDataFinish(int type, String des, BaseData[] datas,
//			Bundle extra) {
//		if( null != getActivity())
//			((HomeActivity)getActivity()).onDataFinish(type, des, datas, extra);
//		mHandler.obtainMessage(type, extra).sendToTarget();
//
//	}
//	@Override
//	public void onDataFailed(int type, String des, Bundle extra) {
//		if( null != getActivity())
//			((HomeActivity)getActivity()).onDataFailed(type, des, extra);
//		mHandler.obtainMessage(type, des).sendToTarget();
//
//	}
//
//	@Override
//	public void onDataFail(int type, String des, Bundle extra) {
//
//	}
//
//	private void showProgress(){
//		if(getActivity() != null)
//			((HomeActivity)getActivity()).showProgress();
//	}
//	private void dismissProgress(){
//		if(getActivity() != null)
//			((HomeActivity)getActivity()).dismissProgress();
//	}
//	private void toast(String msg){
//		if(getActivity() != null)
//			((HomeActivity)getActivity()).toast(msg);
//	}
////	@Override
////	public void onClickTitleMiddle() {
////		// TODO Auto-generated method stub
////
////	}
//
//
//	@Override
//	public void onFragPasue() {
//		// TODO Auto-generated method stub
//
//	}
//}
