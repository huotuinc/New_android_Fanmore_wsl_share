package cy.com.morefan.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cy.com.morefan.R;
import cy.com.morefan.util.DensityUtil;
import cy.com.morefan.wheel.OnWheelChangedListener;
import cy.com.morefan.wheel.WheelView;
import cy.com.morefan.wheel.adapters.ArrayWheelAdapter;
import cy.com.morefan.wheel.adapters.NumericWheelAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class PopWheelView {
	public interface OnDateBackListener{
		void onDateBack(String date);
	}
	private OnDateBackListener listener;
	private Dialog dialog;
	private View mainView;
	private Context mContext;
	private int minYear;  //最小年份
	private int curDay;
	private String result;
	private WheelView wheelYear;
	private WheelView wheelMonth;
	private WheelView wheelDay;
	public PopWheelView(Context context){
		this.mContext = context;
		initView(context);
	}
	public void setOnDateBackListener(OnDateBackListener listener){
		this.listener = listener;
	}
	private void initView(final Context context) {

		if(dialog == null){
			mainView = LayoutInflater.from(context).inflate(R.layout.pop_wheelview, null);
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

//		if(pop == null){
//			mainView = LayoutInflater.from(context).inflate(R.layout.pop_wheelview, null);
//			pop = new PopupWindow(mainView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
//			pop.setAnimationStyle(R.style.AnimationPop);
//		}


		wheelYear = (WheelView) mainView.findViewById(R.id.wheelYear);
		wheelYear.setCyclic(true);
		wheelMonth = (WheelView) mainView.findViewById(R.id.wheelMonth);
		wheelMonth.setCyclic(true);
		wheelDay = (WheelView) mainView.findViewById(R.id.wheelDay);
		wheelDay.setCyclic(true);
	   // final TextView txtDate = (TextView) mainView.findViewById(R.id.txtDate);










    mainView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			 if(listener != null)
				 listener.onDateBack(null);
			 dialog.dismiss();

		}
	});
      mainView.findViewById(R.id.btnSure).setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			 if(listener != null){
				 listener.onDateBack(result);
			 }

			 dialog.dismiss();

		}
	});



		if(mainView.getBackground() == null)
			mainView.setBackgroundColor(Color.BLACK);
		mainView.getBackground().setAlpha(150);

		mainView.setFocusableInTouchMode(true);
		mainView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK ){
					 if(listener != null)
						 listener.onDateBack(null);
					 dialog.dismiss();
				}
				return false;
			}
		});
		mainView.findViewById(R.id.layAll).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				 if(listener != null)
					 listener.onDateBack(null);
				 dialog.dismiss();

			}
		});
		mainView.findViewById(R.id.layMain).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});


	}


	/**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, minYear + year.getCurrentItem());//
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(">>>>>>>>maxDays:"+maxDays);
        day.setViewAdapter(new DateNumericAdapter(mContext, 1, maxDays, curDay - 1));

       // int curDay2 = Math.min(maxDays, curDay);
        //day.setCurrentItem(curDay - 1, true);

//        mYear = minYear+year.getCurrentItem();
//        mMonth = month.getCurrentItem()+1;
//        mDay = curDay;
//        calendar.set(Calendar.DAY_OF_MONTH, mDay);
//        getWeek(calendar.get(Calendar.DAY_OF_WEEK));

    }

	public void show(String date){
		dialog.show();
		//pop.showAtLocation(mainView, 0, 0, 0);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 10);
		if(TextUtils.isEmpty(date)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			date = sdf.format(calendar.getTime());
		}

		minYear = calendar.get(Calendar.YEAR) - 50;
		String[] dates = date.split("-");
		int year = Integer.valueOf(dates[0]);
		int month = Integer.valueOf(dates[1]);
		curDay = Integer.valueOf(dates[2]);

		final String months[] = new String[] { "01", "02", "03", "04", "05", "06","07", "08", "09", "10", "11", "12" };
		final DateNumericAdapter yearAdapter = new DateNumericAdapter(mContext, minYear, minYear + 50, year - minYear);
		final DateArrayAdapter monthAdapter = new DateArrayAdapter(mContext, months, month - 1);

		OnWheelChangedListener dateListener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            	 updateDays(wheelYear, wheelMonth, wheelDay);
            	 result = String.format("%s-%s-%02d", minYear + wheelYear.getCurrentItem(),months[wheelMonth.getCurrentItem()],wheelDay.getCurrentItem() + 1 );
//
            }
        };

		// year
		wheelYear.setViewAdapter(yearAdapter);
		wheelYear.setCurrentItem(year - minYear);
		wheelYear.addChangingListener(dateListener);
		// month

		wheelMonth.setViewAdapter(monthAdapter);
		wheelMonth.setCurrentItem(month - 1);
		wheelMonth.addChangingListener(dateListener);
		// day

		updateDays(wheelYear, wheelMonth, wheelDay);
		wheelDay.setCurrentItem(curDay - 1);
		wheelDay.addChangingListener(dateListener);


	}

	 /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(22);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(22);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
}
