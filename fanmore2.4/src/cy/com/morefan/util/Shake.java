//package cy.com.morefan.util;
//
//import java.util.List;
//
//import cy.com.morefan.R;
//import android.app.ActivityManager;
//import android.app.Service;
//import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.media.MediaPlayer;
//import android.os.Handler;
//import android.os.Vibrator;
//
//public class Shake implements SensorEventListener{
//	public interface ShakeListener{
//		void onShake();
//	}
//	// 速度阈值，当摇晃速度达到这值后产生作用
//		private static final int SPEED_SHRESHOLD = 2000;
//		// 两次检测的时间间隔
//		private static final int UPTATE_INTERVAL_TIME = 70;
//		private static final int SHAKE_INTERVAL_TIME = 2000;
//	//感应管理器
//    private SensorManager mSensorManager;
//    //震动器
//    private Vibrator vibrator;
//    private Context mContext;
//    private ShakeListener listener;
//
//
//    //活动管理器
//   // ActivityManager activityManager ;
//
//	public Shake(Context context, ShakeListener listener){
//		this.mContext = context;
//		this.listener = listener;
//		if( null == listener)
//			return;
//		//1获得硬件信息
//        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//        vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
//      //  activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//
//      }
//
//	private boolean hasStart;
//	public boolean start() {
//		if(hasStart)
//			return true;
//		// 2 判断当前手机是否带加速度感应器，如果不带，直接结束，不启动服务
//		List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
//		if (sensors != null)
//			if (sensors.size() == 0)
//				return false;
//
//		// 4注册侦听事件
//		mSensorManager.registerListener(this,
//				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//				SensorManager.SENSOR_DELAY_GAME);
//		hasStart = true;
//		return true;
//	}
//
//	public void stop(){
//		hasStart = false;
//		mSensorManager.unregisterListener(this);
//	}
//
//	@Override
//	public void onAccuracyChanged(Sensor sensor, int accuracy) {
//		// TODO Auto-generated method stub
//
//	}
//	  //3生成感应侦听事件
//	// 手机上一个位置时重力感应坐标
//	private float lastX;
//	private float lastY;
//	private float lastZ;
//	// 上次检测时间
//	private long lastUpdateTime;
//	private long lastShakeTime;
//	@Override
//	public void onSensorChanged(SensorEvent event) {
//		if(listener == null)
//			return;
//		// 现在检测时间
//				long currentUpdateTime = System.currentTimeMillis();
//				if(currentUpdateTime - lastShakeTime < SHAKE_INTERVAL_TIME)
//					return;
//				// 两次检测的时间间隔
//				long timeInterval = currentUpdateTime - lastUpdateTime;
//				// 判断是否达到了检测时间间隔
//				if (timeInterval < UPTATE_INTERVAL_TIME)
//					return;
//				// 现在的时间变成last时间
//				lastUpdateTime = currentUpdateTime;
//
//				// 获得x,y,z坐标
//				float x = event.values[0];
//				float y = event.values[1];
//				float z = event.values[2];
//
//				// 获得x,y,z的变化值
//				float deltaX = x - lastX;
//				float deltaY = y - lastY;
//				float deltaZ = z - lastZ;
//
//				// 将现在的坐标变成last坐标
//				lastX = x;
//				lastY = y;
//				lastZ = z;
//
//				double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
//						* deltaZ)
//						/ timeInterval * 10000;
//				// 达到速度阀值，发出提示
//				if (speed >= SPEED_SHRESHOLD) {
//					lastShakeTime = System.currentTimeMillis();
//					  vibrator.vibrate(500);
//					  listener.onShake();
//				}
//
////        // TODO Auto-generated method stub
////          int sensorType = event.sensor.getType();
////
////          //读取摇一摇敏感值
////          int shakeSenseValue=15;//Integer.parseInt(getResources().getString(R.string.shakeSenseValue)); ;
////          //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
////          float[] values = event.values;
////
////          if(sensorType == Sensor.TYPE_ACCELEROMETER){
////              if((Math.abs(values[0])>shakeSenseValue||Math.abs(values[1])>shakeSenseValue||Math.abs(values[2])>shakeSenseValue)){
////                  //触发事件，执行打开应用行为
////                vibrator.vibrate(500);
////                //txt.setText("shake");
////                //showMoneyAnim();
////                shake();
////                if(listener != null)
////                	listener.onShake();
////                }
////            }
//
//}
//	private Handler handler = new Handler();
//    private MediaPlayer mMediaPlayer;
//
//
//
//
//    private int deflautSound = R.raw.shake_sound_male;//R.raw.shake_match
//    public void shakeSound(int sid){
//    	if(mMediaPlayer == null){
// 			mMediaPlayer = MediaPlayer.create(mContext,sid);
// 		}else{
// 			mMediaPlayer.reset();
//				mMediaPlayer =  MediaPlayer.create(mContext,sid);
// 		}
//    	try {
//            if (mMediaPlayer != null) {
//            	mMediaPlayer.stop();
//            }
//            mMediaPlayer.prepare();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//			mMediaPlayer.start();
//    }
//
//}
