package cy.com.morefan.util;

import android.os.Handler;
import android.widget.TextView;

public class NumberAnimUtil{
	private Handler mHandler = new Handler();
	private TextView txt;
	/**
	 * 起始数
	 */
	private int start;
	/**
	 * 结束数
	 */
	private int end;

	/**
	 * 变动数字间隔
	 */
	private int per_no;
	/**
	 * 变动时间间隔
	 */
	private int per_time;
	/**
	 * 变动总次数
	 */
	private int count = 20;
	/**
	 * 变动总时长
	 */
	private int time = 800;

	public NumberAnimUtil(TextView txt,int from, int to){
		this.txt = txt;
		this.start = from;
		this.end = to;
		this.per_time = time/count;
		this.per_no = (to - from)/ count;
		if(per_no == 0)
			per_no = 1;

	}
	public void start(){
		if(start != end)
			mHandler.postDelayed(runnable, per_time);
	}
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			start+= per_no;
			if(start < end){
				txt.setText(Util.MoneyFormat(start) + "");
				mHandler.postDelayed(runnable, per_time);
			}else{
				txt.setText(Util.MoneyFormat(end) + "");
			}

		}
	};


}
