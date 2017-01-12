package cy.com.morefan.listener;

import cy.com.morefan.HomeActivity;
import cy.com.morefan.LoadingActivity;
import cy.com.morefan.PushMsgHandleActivity;
import cy.com.morefan.R;
import cy.com.morefan.TaskDetailActivity;
import cy.com.morefan.TestActivity;
import cy.com.morefan.bean.TaskData;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SPUtil;
import cy.com.morefan.util.Util;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class AlamrReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String title = "未知";
		int id = 0;
		if(null != intent.getExtras()){
			title = intent.getExtras().getString("title");
			id = intent.getExtras().getInt("id");
			System.out.println(title);
		}
		//清除本地闹钟
		if(0 != id){
			Bundle extra = new Bundle();
			extra.putInt("id", id);
			MyBroadcastReceiver.sendBroadcast(context, MyBroadcastReceiver.ACTION_ALARM_UP, extra);
		}
		 Notification n = new Notification();
         //n.icon = R.drawable.icon;
         //n.tickerText = title;
         //n.when = System.currentTimeMillis();
         //设置声音
         //n.defaults = Notification.DEFAULT_SOUND;
         //设置 如果通知被点击 则通知自动取消
         //n.flags = Notification.FLAG_AUTO_CANCEL;
         //n.flags=Notification.FLAG_ONGOING_EVENT;
       //app是否在运行
         Intent appIntent = new Intent(context, PushMsgHandleActivity.class);
         appIntent.putExtra("alarmId", id);
         appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);



         PendingIntent pi = PendingIntent.getActivity(context, id, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		//n.setLatestEventInfo(context, title, "小粉提醒", pi);




		Notification.Builder builder = new Notification.Builder(context)
				.setAutoCancel(true)
				.setContentTitle(title)
				.setContentText(title)
				.setContentIntent(pi)
				.setSmallIcon(R.drawable.icon)
				.setWhen(System.currentTimeMillis())
				.setDefaults( Notification.DEFAULT_SOUND)
				.setOngoing(true);
		n=builder.getNotification();



		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE); // get system service
         nm.notify(id, n);
	}

}
