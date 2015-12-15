package cy.com.morefan.util;

public class TimeRunner {

	public interface OnTimeRunnerListener{
		void onTimeRunnerBack(String timeDes);
		void onTimeRunnerFinish();
	}

	private OnTimeRunnerListener listener;
	private long totalTime;

	private Thread timeThread;
	private long useTime = 0;// 运动时间
	private long startTimer = 0;// 开始时间
	private boolean flag;

	public TimeRunner(long time, OnTimeRunnerListener listener){
		this.totalTime = time;
		this.listener = listener;
		flag = false;
	}
	public void stop(){
		flag = false;
		timeThread.interrupt();
	}
	public void start() {
		flag = true;
		timeThread = new Thread() {// 子线程用于监听当前步数的变化

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				startTimer = System.currentTimeMillis();
				while (flag) {
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						if (startTimer != System.currentTimeMillis()) {
							useTime = totalTime - (System.currentTimeMillis() - startTimer);
							if(useTime <= 0){
								listener.onTimeRunnerFinish();
								flag = false;
							}else
								listener.onTimeRunnerBack(getFormatTime(useTime));
					}
				}
			}
		};
		timeThread.start();

	}

	public static String getFormatTime(long time) {
		// long millisecond = time % 1000;
		time = time / 1000;
		long second = time % 60;
		long minute = (time % 3600) / 60;
		long hour = time / 3600;

		// 毫秒秒显示两位
		// String strMillisecond = "" + (millisecond / 10);
		// 秒显示两位
		String strSecond = ("00" + second)
				.substring(("00" + second).length() - 2);
		// 分显示两位
		String strMinute = ("00" + minute)
				.substring(("00" + minute).length() - 2);
		// 时显示两位
		String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

		return strHour + ":" + strMinute + ":" + strSecond;
		// + strMillisecond;
	}
}
