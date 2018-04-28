package cy.com.morefan.view;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cy.com.morefan.R;
import cy.com.morefan.adapter.UserInfoAdapter;
import cy.com.morefan.bean.UserData;
import cy.com.morefan.bean.UserSelectData;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.util.L;
//import cy.com.morefan.view.PopCheckIn.OnPopCheckListener;
import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class UserInfoView {
	public enum Type{
		Name, Sex, Age, Job, Income, Fav,malluser,Sign
	}
	public HashMap<Type, String> titleNames = new HashMap<UserInfoView.Type, String>();
	public interface OnUserInfoBackListener{
		void onUserInfoBack(Type type, UserSelectData data );
	}
	private OnUserInfoBackListener listener;
	private View mainView;
	private TextView txtTitle;
	private ListView listView;
	private Context mContext;
	private TextView btnSure;
	private Dialog dialog;
	private LinearLayout layMain;
	private EditText edtName;

	public UserInfoView(Context context){
		this.mContext = context;
		initView(context);
	}

	public void setOnUserInfoBackListener(OnUserInfoBackListener listener){
		this.listener = listener;
	}

	private void initView(final Context context) {
		if(dialog == null){
			mainView = LayoutInflater.from(context).inflate(R.layout.pop_userinfo, null);
			dialog = new Dialog(context, R.style.PopDialog);
			dialog.setContentView(mainView);
			 Window window = dialog.getWindow();
			 window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
			 window.setWindowAnimations(R.style.AnimationPop);  //添加动画

			 //设置视图充满屏幕宽度
			 WindowManager.LayoutParams lp = window.getAttributes();
			 int[] size  = DensityUtil.getSize(mContext);
			 lp.width = size[0]; //设置宽度
			// lp.height = (int) (size[1]*0.8);
			 window.setAttributes(lp);
		}
		titleNames.put(Type.Name, "昵称");
		titleNames.put(Type.Sex, "性别");
		titleNames.put(Type.Age, "出生年份");
		titleNames.put(Type.Job, "职业");
		titleNames.put(Type.Income, "收入");
		titleNames.put(Type.Fav, "爱好");
		titleNames.put(Type.malluser,"用户列表");
		edtName = (EditText) mainView.findViewById(R.id.edtName);
		txtTitle = (TextView) mainView.findViewById(R.id.txtTitle);
		listView = (ListView) mainView.findViewById(R.id.listView);


		btnSure = (TextView) mainView.findViewById(R.id.btnSure);
		btnSure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(curType == Type.Name){
					if(listener != null){
						listener.onUserInfoBack(Type.Name, new UserSelectData(edtName.getText().toString(), "0"));
					}
				}else{
					List<UserSelectData> result = adapter.getSelectData();


					StringBuffer tag = new StringBuffer();
					StringBuffer name = new StringBuffer();
					int length = result.size();
					for (int i = 0; i < length; i++) {
						tag.append(result.get(i).id);
						name.append(result.get(i).name);
						if(i != length -1){
							tag.append(",");
							name.append(",");
						}

					}
					if(listener != null){
						listener.onUserInfoBack(Type.Fav, new UserSelectData(name.toString(), tag.toString()));
					}
				}


				dialog.dismiss();

			}
		});
		// init support
		mainView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				 if(listener != null)
					 listener.onUserInfoBack(null,null);
				 dialog.dismiss();
			}
		});

		mainView.setFocusableInTouchMode(true);
		mainView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK ){
					 if(listener != null)
						 listener.onUserInfoBack(null,null);
					 dialog.dismiss();
				}
				return false;
			}
		});
		layMain = (LinearLayout) mainView.findViewById(R.id.layMain);


	}
	private Handler handler = new Handler();
	private UserInfoAdapter adapter;
	private Type curType;
	public void show(final Type type, final List<UserSelectData> datas, String selectIds){
		curType = type;
		txtTitle.setText(titleNames.get(type));
		btnSure.setVisibility(type == Type.Fav || type == Type.Name ? View.VISIBLE : View.GONE);
		edtName.setVisibility(type == Type.Name ? View.VISIBLE : View.GONE);
		listView.setVisibility(type == Type.Name ? View.GONE : View.VISIBLE);
		dialog.show();
		if(type == Type.Name){
			edtName.requestFocus();
			edtName.requestFocusFromTouch();

			final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}, 10);
			edtName.setText(selectIds);

		}else{
			adapter = new UserInfoAdapter(mContext, datas);
			listView.setAdapter(adapter);
			if(!TextUtils.isEmpty(selectIds)){
				//boolean[] tags = new boolean[datas.size()];
				for(int i = 0, length = datas.size(); i < length; i++){
					if(selectIds.contains(datas.get(i).id))
						adapter.setSelect(i);
				}
			}
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					if(type == Type.Sex || type == Type.Job || type == Type.Income || type == Type.malluser ){
						if(listener != null){
							listener.onUserInfoBack(type, datas.get(arg2));
						}
						dialog.dismiss();
					}else if(type == Type.Fav){
						adapter.setSelect(arg2);
					}

				}
			});
		}






		//set height
		LinearLayout.LayoutParams params =  (LayoutParams) layMain.getLayoutParams();
    	//reset
    	params.height = LinearLayout.LayoutParams.WRAP_CONTENT;//ownHeight > height ? height :ownHeight;
    	layMain.setLayoutParams(params);

		ViewTreeObserver vto2 = layMain.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		    	layMain.getViewTreeObserver().removeGlobalOnLayoutListener(this);
		    	LinearLayout.LayoutParams params =  (LayoutParams) layMain.getLayoutParams();
				int ownHeight = layMain.getHeight();
				int height = (int) (DensityUtil.getSize(mContext)[1] * 0.75);
				params.height = ownHeight > height ? height :ownHeight;
				layMain.setLayoutParams(params);
		    }
		});


	}
}
