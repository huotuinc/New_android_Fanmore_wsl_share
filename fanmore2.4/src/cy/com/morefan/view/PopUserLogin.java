package cy.com.morefan.view;



import cindy.android.test.synclistview.SyncImageLoaderHelper;
import cy.com.morefan.AutnLogin;
import cy.com.morefan.BaseActivity;
import cy.com.morefan.R;
import cy.com.morefan.bean.AccountModel;
import cy.com.morefan.constant.BusinessStatic;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.Util;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PopUserLogin extends BaseActivity implements OnClickListener{

	public interface OnPopLoginListener{
		void onLogin(String userName, String pwd);
		void onReg();
		void onForget();
		void onNext();
	}
	private SyncImageLoaderHelper helper;
	private OnPopLoginListener listener;
	private View mainView;
	private Context mContext;
//	private EditText edtUserName;
//	private EditText edtPwd;
	private ImageView imgPhoto;
	private TextView txtName;
	private Button btnNext;
	AccountModel accountModel= new AccountModel();
	public PopUserLogin(Context context){
		this.mContext = context;
		initView(context);
	}
	private Dialog dialog;
	private void initView(Context context) {
		if(dialog == null){
			mainView = LayoutInflater.from(context).inflate(R.layout.pop_userloginnext, null);
			dialog = new Dialog(context, R.style.PopDialog);
			dialog.setContentView(mainView);
			 Window window = dialog.getWindow();
			 window.setGravity(Gravity.TOP);  //此处可以设置dialog显示的位置
			 window.setWindowAnimations(R.style.AnimationPopTopIn);  //添加动画

			 //设置视图充满屏幕宽度
			 WindowManager.LayoutParams lp = window.getAttributes();
			 lp.width = (int)(DensityUtil.getSize(mContext)[0]); //设置宽度
			 lp.height= (int)(DensityUtil.getSize(mContext)[1]);//设置高度
			 window.setAttributes(lp);
		}
		helper = new SyncImageLoaderHelper(this);
		imgPhoto=(ImageView)mainView.findViewById(R.id.imgPhoto);
		txtName= (TextView) mainView.findViewById(R.id.txtName);
		btnNext= (Button) mainView.findViewById(R.id.btnNext);
//		edtUserName = (EditText) mainView.findViewById(R.id.edtUserName);
//		edtPwd = (EditText) mainView.findViewById(R.id.edtPwd);

		mainView.findViewById(R.id.btnNext).setOnClickListener(this);


		if(TextUtils.isEmpty(BusinessStatic.getInstance().accountModel.getAccountIcon())){
			imgPhoto.setImageResource(R.drawable.user_icon);
		}else{
			helper.loadImage(-1, imgPhoto, null,BusinessStatic.getInstance().accountModel.getAccountIcon(), Constant.BASE_IMAGE_PATH);
		}
		txtName.setText(BusinessStatic.getInstance().accountModel.getAccountName());

	}

	public void show(Context context, OnPopLoginListener listener){
		AccountModel accountModel=new AccountModel();
		this.listener = listener;
		//String userName = SPUtil.getStringToSpByName(context, Constant.SP_NAME_NORMAL, Constant.SP_NAME_USERNAME);
		//imgPhoto.setBackgroundResource(Integer.parseInt(accountModel.getAccountIcon()));
		//imgPhoto.setBackgroundResource(R.drawable.icon);
		//txtName.setText(accountModel.getAccountName());
//		if(!TextUtils.isEmpty(userName)){
//			edtPwd.requestFocus();
//			edtPwd.requestFocusFromTouch();
//		}
//		this.dayCount = dayCount;
//		update(dayCount);
		dialog.show();


	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnNext:
			next();
			break;

		default:

			break;
		}


	}
	private void next(){

		if(listener == null)
			return;
		listener.onNext();
		dialog.dismiss();

	}

}
