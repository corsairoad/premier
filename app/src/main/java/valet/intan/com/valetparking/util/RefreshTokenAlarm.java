package valet.intan.com.valetparking.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import valet.intan.com.valetparking.service.RefreshTokenService;

/**
 * Created by fadlymunandar on 8/23/17.
 */

public class RefreshTokenAlarm {
    private AlarmManager alarmManager;
    private PendingIntent pi;
    private static RefreshTokenAlarm refreshTokenAlarm;
    private PrefManager prefManager;
    private static final String TAG = RefreshTokenAlarm.class.getSimpleName();

    private RefreshTokenAlarm(Context context) {
        Intent intent = new Intent(context, RefreshTokenService.class);
        intent.setAction(RefreshTokenService.ACTION_REFRESH_TOKEN);

        pi = PendingIntent.getService(context, 713, intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        prefManager = PrefManager.getInstance(context);
    }

    public static RefreshTokenAlarm getInstance(Context context) {
        if (refreshTokenAlarm == null) {
            refreshTokenAlarm = new RefreshTokenAlarm(context);
        }
        return refreshTokenAlarm;
    }

    public void startAlarmIn5Days() {
        try {

            String expDate = prefManager.getExpiredToken();

            if (expDate == null) {
                return;
            }

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date date = dateFormat.parse(expDate);

            Log.d(TAG, "expired in: " + date);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+7"));
            //calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR));
            //calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 7);
            //calendar.set(Calendar.HOUR_OF_DAY, 13);
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
            //calendar.set(Calendar.SECOND, 0);

            long calLong = calendar.getTimeInMillis();
            Log.d(TAG, "startAlarmIn5days: calLong: " + calLong);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calLong, pi);
            Log.d(TAG, "alarm set in " + calendar.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void cancelAlarm() {
        alarmManager.cancel(pi);
    }
}
