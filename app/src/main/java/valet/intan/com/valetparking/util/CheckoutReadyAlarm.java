package valet.intan.com.valetparking.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import valet.intan.com.valetparking.service.ReadyCheckoutService;

/**
 * Created by DIGIKOM-EX4 on 3/2/2017.
 */

public class CheckoutReadyAlarm {

    private Context context;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private static CheckoutReadyAlarm checkoutReadyAlarm;

    private CheckoutReadyAlarm(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReadyCheckoutService.class);
        pendingIntent = PendingIntent.getService(context, 88, intent, 0);
    }

    public static CheckoutReadyAlarm getInstance(Context context) {
        if (checkoutReadyAlarm == null) {
            checkoutReadyAlarm = new CheckoutReadyAlarm(context);
        }
        return checkoutReadyAlarm;
    }

    public void startAlarm() {
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),6 *10 * 1000, pendingIntent);
    }

    public void cancelAlarm() {
        alarmManager.cancel(pendingIntent);
    }
}
