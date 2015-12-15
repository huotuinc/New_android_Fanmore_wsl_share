package cy.com.morefan.view;


import java.lang.reflect.Field;

import cy.com.morefan.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog {
	public static CustomDialog showChooiceDialg(Activity activity, String title,
			String content, String b1Text, String b2Text, View view,
			DialogInterface.OnClickListener yes,
			DialogInterface.OnClickListener no) {
		if(activity == null)
			return null;
		if(activity.isFinishing())
			return null;
		CustomDialog ad = new CustomDialog.Builder(activity).setTitle(title)
				.setContentView(view).setMessage(content)
				.setPositiveButton(b1Text, yes).setNegativeButton(b2Text, no)
				.create();
		ad.show();
		return ad;
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
	}
	/**
	 * 点击按钮后dialog继续显示
	 */
	public void setDialogShowAfterClick(){
		try {
	        //不关闭
	     Field field = getClass().getSuperclass().getDeclaredField("mShowing");
	        field.setAccessible(true);
	        field.set(this, false);
	        } catch (Exception e) {
	         e.printStackTrace();
	         }
	}
	public void setDialogDismissAfterClick(){
		try {
	        //不关闭
	     Field field = getClass().getSuperclass().getDeclaredField("mShowing");
	        field.setAccessible(true);
	        field.set(this, true);
	        } catch (Exception e) {
	         e.printStackTrace();
	         }
	}
	public interface OnkeyBackListener{
		void onKeyBack();
	}
	public static OnkeyBackListener listener;
	public static void setOnKeyBackListener(OnkeyBackListener listener){
		CustomDialog.listener = listener;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			//System.out.println("customDialog onKeyDown");
			this.setDialogDismissAfterClick();
			if(listener != null){
				listener.onKeyBack();
				dismiss();
			}

			//customDialog.dismiss();
		}

		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onDetachedFromWindow() {
		listener = null;
		super.onDetachedFromWindow();
	}

	public static class Builder{
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private LayoutInflater mInflater;
		private CustomDialog dialog;
		private DialogInterface.OnClickListener positiveButtonClickListener,negativeButtonClickListener;
		private View contentView;
		public Builder(Context context){
			this.context = context ;
			this.mInflater = LayoutInflater.from(context);
		}

		public Builder setTitle(String title){
			this.title = title;
			return this;
		}

		public Builder setMessage(String message){
			this.message = message;
			return this;
		}

		 public Builder setContentView(View v) {
	        this.contentView = v;
	        return this;
	    }


		public Builder setPositiveButton(String text,DialogInterface.OnClickListener listener){
			this.positiveButtonText = text;
			this.positiveButtonClickListener = listener;
			return this;
		}
		public Builder setNegativeButton(String text,DialogInterface.OnClickListener listener){
			this.negativeButtonText = text;
			this.negativeButtonClickListener = listener;
			return this;
		}

		public CustomDialog create(){
			dialog = new CustomDialog(context, R.style.Dialog);
			 Window window = dialog.getWindow();
			 window.setGravity(Gravity.CENTER);  //此处可以设置dialog显示的位置
			 window.setWindowAnimations(R.style.AnimationPop);  //添加动画
			View layout = mInflater.inflate(R.layout.custom_dialog, null);

			/**
			 * set the dialog title
			 */
			FrameLayout layTitle = (FrameLayout) layout.findViewById(R.id.layTitle);
			layTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
			 ((TextView) layout.findViewById(R.id.title)).setText(title);

			/**
			 * set content
			 */
			 LinearLayout layContent = (LinearLayout) layout.findViewById(R.id.content);
			 layContent.setBackgroundResource(TextUtils.isEmpty(title) ? R.drawable.dialog_middle_top_circle_bg : R.drawable.dialog_middle_bg);
            if (!TextUtils.isEmpty(message)) {
            	 // set the content message
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set add the contentView to the dialog body
            	layContent.removeAllViews();
            	layContent.addView(contentView,new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            }

            /**
             * set bottom button
             */
            layout.findViewById(R.id.bottom).setVisibility( TextUtils.isEmpty(positiveButtonText) && TextUtils.isEmpty(negativeButtonText) ? View.GONE : View.VISIBLE);
         // set the confirm button
            TextView positiveButton = (TextView) layout.findViewById(R.id.positiveButton);
            positiveButton.setBackgroundResource(TextUtils.isEmpty(negativeButtonText) ? R.drawable.dialog_single_btn_sel : R.drawable.dialog_left_btn_sel);
         // if no confirm button just set the visibility to GONE
            positiveButton.setVisibility(TextUtils.isEmpty(positiveButtonText) ? View.GONE : View.VISIBLE);
            positiveButton.setText(positiveButtonText);
            positiveButton.setOnClickListener(new ViewClickLister());

            // set the cancel button
            TextView negativeButton = (TextView) layout.findViewById(R.id.negativeButton);
            negativeButton.setBackgroundResource(TextUtils.isEmpty(positiveButtonText) ? R.drawable.dialog_single_btn_sel : R.drawable.dialog_right_btn_sel);
         // if no confirm button just set the visibility to GONE
            negativeButton.setVisibility(TextUtils.isEmpty(negativeButtonText) ? View.GONE : View.VISIBLE);
            negativeButton.setText(negativeButtonText);
            negativeButton.setOnClickListener(new ViewClickLister());
            layout.findViewById(R.id.imgLine).setVisibility(positiveButton.getVisibility() == View.GONE || negativeButton.getVisibility() == View.GONE ? View.GONE : View.VISIBLE);

            dialog.setContentView(layout);
			return dialog;
		}
		class ViewClickLister implements View.OnClickListener{

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.positiveButton:
					if(null != positiveButtonClickListener)
               		 	positiveButtonClickListener.onClick(dialog,DialogInterface.BUTTON_POSITIVE);

					break;
				case R.id.negativeButton:
					if(null != negativeButtonClickListener)
						negativeButtonClickListener.onClick(dialog,DialogInterface.BUTTON_NEGATIVE);
					break;

				}
				dialog.dismiss();
			}

		}

	}


}
