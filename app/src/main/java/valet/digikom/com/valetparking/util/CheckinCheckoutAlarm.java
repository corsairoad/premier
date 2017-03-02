package valet.digikom.com.valetparking.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import valet.digikom.com.valetparking.service.FailedTransactionService;

/**
 * Created by DIGIKOM-EX4 on 2/28/2017.
 */

public class CheckinCheckoutAlarm {

    private Context context;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private static CheckinCheckoutAlarm checkinCheckoutAlarm;

    private CheckinCheckoutAlarm(Context context) {
        this.context = context;
        Intent intent = new Intent(context.getApplicationContext(), FailedTransactionService.class);
        pendingIntent = PendingIntent.getService(context,99,intent,0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static CheckinCheckoutAlarm getInstance(Context context) {
        if (checkinCheckoutAlarm == null) {
            checkinCheckoutAlarm = new CheckinCheckoutAlarm(context);
        }
        return checkinCheckoutAlarm;
    }

    public void startAlarm() {
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 6 * 10 * 1000, pendingIntent);
    }

    public void cancelAlarm() {
        alarmManager.cancel(pendingIntent);
    }
}
