//package cy.com.morefan.view;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cy.com.morefan.R;
//import cy.com.morefan.bean.UserData;
//import cy.com.morefan.constant.BusinessStatic;
//import cy.com.morefan.util.DensityUtil;
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Color;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.Window;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//public class PopCheckIn{
//
//	public interface OnPopCheckListener{
//		void onPopCheck();
//	}
//	private OnPopCheckListener listener;
//	private View mainView;
//	private Context mContext;
//	private List<ImageView> lines;
//	private List<ImageView> circles;
//	private ImageView imgFlag;
//	public PopCheckIn(Context context){
//		this.mContext = context;
//		initView(context);
//	}
//	private Dialog dialog;
//	private void initView(Context context) {
//		if(dialog == null){
//			mainView = LayoutInflater.from(context).inflate(R.layout.check_in, null);
//			dialog = new Dialog(context, R.style.PopDialog);
//			dialog.setContentView(mainView);
//			 Window window = dialog.getWindow();
//			 window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
//			 window.setWindowAnimations(R.style.AnimationPop);  //添加动画
//
//			 //设置视图充满屏幕宽度
//			 WindowManager.LayoutParams lp = window.getAttributes();
//			 lp.width = (int)(DensityUtil.getSize(mContext)[0]); //设置宽度
//			 window.setAttributes(lp);
//		}
////		if( null != BusinessStatic.getInstance().checkExps && BusinessStatic.getInstance().checkExps.length == 5){
////			((TextView)mainView.findViewById(R.id.txt1)).setText(BusinessStatic.getInstance().checkExps[0]);
////			((TextView)mainView.findViewById(R.id.txt2)).setText(BusinessStatic.getInstance().checkExps[1]);
////			((TextView)mainView.findViewById(R.id.txt3)).setText(BusinessStatic.getInstance().checkExps[2]);
////			((TextView)mainView.findViewById(R.id.txt4)).setText(BusinessStatic.getInstance().checkExps[3]);
////			((TextView)mainView.findViewById(R.id.txt5)).setText(BusinessStatic.getInstance().checkExps[4]);
////			((TextView)mainView.findViewById(R.id.txtContent)).setText(String.format("连续5天签到后，每日最高可获取%s经验\n您获得的经验可在道具中心兑换道具，提高收益", BusinessStatic.getInstance().checkExps[4]));
////		}
//		imgFlag = (ImageView) mainView.findViewById(R.id.imgFlag);
//
//		ImageView l1 = (ImageView) mainView.findViewById(R.id.l1);
//		ImageView l2 = (ImageView) mainView.findViewById(R.id.l2);
//		ImageView l3 = (ImageView) mainView.findViewById(R.id.l3);
//		ImageView l4 = (ImageView) mainView.findViewById(R.id.l4);
//		ImageView l5 = (ImageView) mainView.findViewById(R.id.l5);
//		lines = new ArrayList<ImageView>();
//		lines.add(l1);
//		lines.add(l2);
//		lines.add(l3);
//		lines.add(l4);
//		lines.add(l5);
//		ImageView c1 = (ImageView) mainView.findViewById(R.id.c1);
//		ImageView c2 = (ImageView) mainView.findViewById(R.id.c2);
//		ImageView c3 = (ImageView) mainView.findViewById(R.id.c3);
//		ImageView c4 = (ImageView) mainView.findViewById(R.id.c4);
//		ImageView c5 = (ImageView) mainView.findViewById(R.id.c5);
//		circles = new ArrayList<ImageView>();
//		circles.add(c1);
//		circles.add(c2);
//		circles.add(c3);
//		circles.add(c4);
//		circles.add(c5);
//
//		mainView.findViewById(R.id.btnCheck).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(null != listener)
//					listener.onPopCheck();
//				//mUserService.CheckIn(UserData.getUserData().loginCode);
//
//			}
//		});
//		mainView.setFocusableInTouchMode(true);
//		mainView.setOnKeyListener(new View.OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if(keyCode == KeyEvent.KEYCODE_BACK ){
////					 if(listener != null)
////						 listener.onCheckInBack(0);
//					dialog.dismiss();
//				}
//				return false;
//			}
//		});
//		mainView.findViewById(R.id.imgCancel).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//
//			}
//		});
//		mainView.findViewById(R.id.layBg).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//
//			}
//		});
//		mainView.findViewById(R.id.layMain).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//			}
//		});
//
//
//	}
//	private void update(int dayCount){
//		imgFlag.setVisibility(UserData.getUserData().dayCheckIn ? View.VISIBLE : View.GONE);
//		for(int i = 0; i < 5; i++){
//			if(dayCount >= 5){
//				lines.get(i).setBackgroundColor(0xffffad03);
//				circles.get(i).setImageResource(R.drawable.shape_check_circle);
//			}else{
//				if(i < dayCount){
//					if(i == dayCount - 1)
//						lines.get(i).setBackgroundResource(R.drawable.shape_check_rectangle);
//					else
//						lines.get(i).setBackgroundColor(0xffffad03);
//					circles.get(i).setImageResource(R.drawable.shape_check_circle);
//				}else{
//					lines.get(i).setBackgroundColor(Color.WHITE);
//					circles.get(i).setImageResource(R.drawable.shape_check_circle_white);
//				}
//			}
//
//		}
//
//	}
//	public void setOnPopCheckListener(OnPopCheckListener listener){
//		this.listener = listener;
//	}
//	private int dayCount;
//	public void show(int dayCount){
//		this.dayCount = dayCount;
//		update(dayCount);
//		dialog.show();
//	}
//	public void doneCheckIn(){
//		dayCount ++;
//		update(dayCount);
//
//	}
//
//}
