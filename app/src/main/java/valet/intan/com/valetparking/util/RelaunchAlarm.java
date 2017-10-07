package valet.intan.com.valetparking.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import valet.intan.com.valetparking.Main2Activity;
import valet.intan.com.valetparking.WelcomeActivity;

/**
 * Created by fadlymunandar on 10/5/17.
 */

public class RelaunchAlarm {

    private Context context;
    private PendingIntent pendingIntent;
    private PendingIntent piWelcomeActivity;
    private AlarmManager alarmWelcomeActivity;
    private AlarmManager alarmManager;
    private static RelaunchAlarm relaunchAlarm;

    private RelaunchAlarm(Context context) {
        this.context = context;
        Intent intent = new Intent(context, Main2Activity.class);
        Intent intent1 = new Intent(context, WelcomeActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 552,intent,0);
        piWelcomeActivity = PendingIntent.getActivity(context, 551, intent1, 0);

        alarmWelcomeActivity = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static RelaunchAlarm getInstance(Context context) {
        if (relaunchAlarm == null) {
            relaunchAlarm = new RelaunchAlarm(context);
        }
        return relaunchAlarm;
    }

    public void launchApp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 4);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public void launchAppToWelcomeActivity() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + 4);
        alarmWelcomeActivity.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), piWelcomeActivity);
    }

}
