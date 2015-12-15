package cy.com.morefan.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.text.TextUtils;
import android.text.format.Formatter;

public class TimeUtil {
	/**
	 *
	 * @param time单位秒
	 * @return
	 */
	public static String getTimeDes(int time){
		String msg = "";
		int minute = time / 60;
		int seconds = time % 60;
		if(minute == 0){
			msg = seconds + "秒";
		}else if(seconds == 0 && minute != 0 ){
			msg = minute + "分钟" ;
		}else{
			msg = minute + "分" + seconds + "秒";
		}

		return msg;
	}

	public static String getCurDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd", Locale.CHINA);
		java.util.Date date = new java.util.Date(System.currentTimeMillis());
		return sdf.format(date);
	}
	/**
	 *
	 * @param time yyy-MM-dd HH:mm
	 * @return
	 */
	public static long getLongTime(String time){
		SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm", Locale.CHINA);
		Date date;
		try {
			date = sdf.parse(time);
			Calendar c = Calendar.getInstance();
			TimeZone tz = TimeZone.getTimeZone("GMT");
			c.setTimeZone(tz);
			c.setTime(date);

			return c.getTimeInMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 *
	 * @param date yyyy-MM-dd hh-mm-ss
	 * @return yyyy年MM月dd日
	 */
	public static String FormatterTime(String date) {
		if(TextUtils.isEmpty(date))
			return "未知";
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyy-MM-dd", Locale.CHINA);
		SimpleDateFormat sdfTo = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
		try {
			return sdfTo.format(sdfFrom.parse(date));
		} catch (ParseException e) {
			return "未知";
		}

	}
	/**
	 *
	 * @param date yyyy-MM-dd hh-mm-ss
	 * @return MM月dd日
	 */
	public static String FormatterTimeByMonthAndDay(String date) {
		if(TextUtils.isEmpty(date))
			return "未知";
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyy-MM-dd HH-mm-ss", Locale.CHINA);
		SimpleDateFormat sdfTo = new SimpleDateFormat("MM月dd日", Locale.CHINA);
		try {
			return sdfTo.format(sdfFrom.parse(date));
		} catch (ParseException e) {
			return "未知";
		}

	}
	/**
	 *
	 * @param date yyyy-MM-dd
	 * @return MM月dd日
	 */
	public static String FormatterTimeByMonthAndDay2(String date) {
		if(TextUtils.isEmpty(date))
			return "未知";
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyy-MM-dd", Locale.CHINA);
		SimpleDateFormat sdfTo = new SimpleDateFormat("MM月dd日", Locale.CHINA);
		try {
			return sdfTo.format(sdfFrom.parse(date));
		} catch (ParseException e) {
			return "未知";
		}

	}
	/**
	 *
	 * @param date yyyy-MM-dd hh-mm-ss
	 * @return yyy-MM-dd
	 */
	public static String FormatterTimeToDay(String date) {
		if(TextUtils.isEmpty(date))
			return "未知";
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyy-MM-dd HH-mm-ss", Locale.CHINA);
		SimpleDateFormat sdfTo = new SimpleDateFormat("yyy-MM-dd", Locale.CHINA);
		try {
			return sdfTo.format(sdfFrom.parse(date));
		} catch (ParseException e) {
			return "未知";
		}

	}

	/**
	 *
	 * @param date yyyy-MM-dd hh-mm-ss
	 * @return long
	 */
	public static long FormatterTimeToLong(String format, String time) {
		if(TextUtils.isEmpty(time))
			return 0;
		SimpleDateFormat sdfFrom = new SimpleDateFormat(format, Locale.CHINA);
		try {
			java.util.Date date = sdfFrom.parse(time);
			return date.getTime();
		} catch (ParseException e) {
			return 0;
		}

	}

	/**
	 *
	 * @param date yyyy-MM-dd hh-mm-ss
	 * @return yyy-MM-dd hh:mm
	 */
	public static String FormatterTimeToMinute(String date) {
		if(TextUtils.isEmpty(date))
			return "未知";
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyy-MM-dd HH-mm-ss", Locale.CHINA);
		SimpleDateFormat sdfTo = new SimpleDateFormat("yyy-MM-dd HH:mm", Locale.CHINA);
		try {
			return sdfTo.format(sdfFrom.parse(date));
		} catch (ParseException e) {
			return "未知";
		}

	}
	/**
	 *
	 * @param date yyyy-MM-dd hh:mm:ss
	 * @return yyy-MM-dd hh:mm
	 */
	public static String FormatterTimeToMinute2(String date) {
		if(TextUtils.isEmpty(date))
			return "未知";
		SimpleDateFormat sdfFrom = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.CHINA);
		SimpleDateFormat sdfTo = new SimpleDateFormat("yyy-MM-dd HH:mm", Locale.CHINA);
		try {
			return sdfTo.format(sdfFrom.parse(date));
		} catch (ParseException e) {
			return "未知";
		}

	}

}
