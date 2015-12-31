package cy.com.morefan.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/30.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private static class Holder
    {
        private static final CrashHandler instance = new CrashHandler();
    }

    private CrashHandler()
    {

    }

    public static final CrashHandler getInstance()
    {
        return Holder.instance;
    }
    //程序的Context对象
    private Context mContext;
    //
//用来存储设备信息和异常信息
    private Map< String, String > infos = new HashMap< String, String >( );


    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public
    void uncaughtException ( Thread thread, Throwable ex ) {

        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
    } catch (InterruptedException e) {
    }

    //退出程序
    android.os.Process.killProcess(android.os.Process.myPid());
    System.exit(1);
}
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtil.show(mContext, "很抱歉,程序出现异常,即将退出.");
                Looper.loop();
            }
        }.start ( );
        //收集设备参数信息
        //collectDeviceInfo ( mContext );
        return true;
    }

    /**
     * 收集设备参数信息
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
    }
}