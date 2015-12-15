package cy.lib.edittext;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.R.bool;
import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;


/**
 * step 1.using setValidator() set avalidator
 * step 2.using validate to validate
 * @author cy
 *
 */
public class CyEditText extends AutoCompleteTextView implements Callback{
	public interface Validator{
		/**
		 * 错误判断条件
		 * @param input
		 * @return
		 */
		boolean validate(String input);
		/**
		 * 错误提示
		 * @return
		 */
		String validateErrorMsg();
	}
	private Validator mValidator;
	private Drawable imgCloseButton;
	private boolean isShowError;
	private Handler mHandler = new Handler(this);
	@Override
	public boolean handleMessage(Message msg) {
		if(!isFocusable())
			return true;
		CyEditText.this.setCompoundDrawables(CyEditText.this.getCompoundDrawables()[0], CyEditText.this.getCompoundDrawables()[1], null, CyEditText.this.getCompoundDrawables()[3]);
		if(msg.obj != null)
			CyEditText.this.setCompoundDrawables(CyEditText.this.getCompoundDrawables()[0], CyEditText.this.getCompoundDrawables()[1], (Drawable)msg.obj, CyEditText.this.getCompoundDrawables()[3]);
//		Drawable drawable = getCompoundDrawables()[2];
//		System.out.println(">>>>haveButton:" + (drawable != null) );
//		if(msg.what == 0 && !isShowClear){//add
//			isShowClear = true;
//			System.out.println(">>>>add" );
//
//		}else if(msg.what == 1 && drawable != null && msg.obj == null){//clear
//			isShowClear = false;
//			System.out.println(">>>>clear" );
//			CyEditText.this.setCompoundDrawables(CyEditText.this.getCompoundDrawables()[0], CyEditText.this.getCompoundDrawables()[1], null, CyEditText.this.getCompoundDrawables()[3]);
//
//		}
			return false;
	}

	public CyEditText(Context context) {
		super(context);
		init(context, null);
	}
	public CyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CyEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	public void setValidator(Validator mValidator){
		this.mValidator = mValidator;
	}
	public boolean validate(){
		if(mValidator == null)
			throw new InstantiationError("Please set a validator first, using setValidator()");
		String input = this.getText().toString();
		boolean result = mValidator.validate(input);
		if(!result)
			setError(mValidator.validateErrorMsg());
		return result;
	}

	public int getBackground(int style){
		switch (style) {
		case 0:
			return R.drawable.edittext_selector;
		case 1:
			return R.drawable.edittext_selector_blue ;
		case 2:

			return R.drawable.edittext_transparent;

		default:
			return R.drawable.edittext_selector;
		}
	}
	public void init(Context context, AttributeSet attrs){
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CyEditText);
		//0:orange, 1:blue,2:transparent
		int stye = ta.getInt(R.styleable.CyEditText_style, 0);
		ta.recycle();
		//int backgroud = ;//stye == 1 ? : ;
		setBackgroundResource(getBackground(stye));


		int padding =  (int) dip2px(context, 5);
		setPadding(padding, 0, padding, 0);
		//init delete
		imgCloseButton = getResources().getDrawable(R.drawable.edittext_delete);
		//调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		imgCloseButton.setBounds(0, 0, imgCloseButton.getMinimumWidth(), imgCloseButton.getMinimumHeight());
		// There may be initial text in the field, so we may need to display the  button
        handleClearButton();
		 //if the Close image is displayed and the user remove his finger from the button, clear it. Otherwise do nothing
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	CyEditText et = CyEditText.this;
                if (et.getCompoundDrawables()[2] == null)
                    return false;
                if (event.getAction() != MotionEvent.ACTION_UP)
                    return false;
                if (!isShowError && event.getX() > et.getWidth() - et.getPaddingRight() - imgCloseButton.getIntrinsicWidth()) {
                    et.setText("");
                    CyEditText.this.handleClearButton();
                }
                return false;
            }

        });

        //if text changes, take care of the button
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            	isShowError =false;
            	setError(null);
            	CyEditText.this.handleClearButton();
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

	}
	void handleClearButton() {
		String content = this.getText().toString();
		//设置光标
		Selection.setSelection(this.getText(), content.length());
        if (content.equals("")) {
        	//remove clear button
        	mHandler.sendEmptyMessageDelayed(1, 100);
        } else {
        	//add clear button
        	//使用handler延迟，若直接设置drawble，setError的图未消失前会挡住drawable,致drawable不显示
        	Message message = mHandler.obtainMessage();
        	message.obj = imgCloseButton;
        	message.what = 0;
        	mHandler.sendMessageDelayed(message, 100);
        }
    }

	@Override
	public void setError(CharSequence error) {
		super.setError(error);
		if(error != null){
			isShowError = true;
			requestFocus();
			requestFocusFromTouch();
			shake();
		}




	}
	private void shake(){
		AnimatorSet set = new AnimatorSet();
		set.playTogether(
		    ObjectAnimator.ofFloat(this, "translationX", 0, 10)
		);
		CycleInterpolator interpolator = new CycleInterpolator(8);
		set.setInterpolator(interpolator);
		set.setDuration(400).start();
	}
	/**
	 * 第一个参数为 单位，第二个参数为单位(第一个参数设置的单位)指定的值，返回值 都是像素
	 * @param paramContext
	 * @param paramFloat
	 * @return
	 */
	public static float dip2px(Context paramContext, float paramFloat)
	  {
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paramFloat, paramContext.getResources().getDisplayMetrics());
	  }





}
