package valet.digikom.com.valetparking.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import valet.digikom.com.valetparking.service.DownloadCurrentLobbyService;

/**
 * Created by DIGIKOM-EX4 on 4/21/2017.
 */

public class DownloadCheckinAlarm {
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private Context context;
    private static DownloadCheckinAlarm checkinAlarm;

    private DownloadCheckinAlarm(Context context) {
        Intent intent = new Intent(context, DownloadCurrentLobbyService.class);
        intent.setAction(DownloadCurrentLobbyService.ACTION_DOWNLOAD);
        pendingIntent = PendingIntent.getService(context,65,intent, 0);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static DownloadCheckinAlarm getInstance(Context context) {
        if (checkinAlarm == null) {
            checkinAlarm = new DownloadCheckinAlarm(context);
        }
        return checkinAlarm;
    }

    public void startAlarm() {
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 5 * 60 * 1000, pendingIntent);
    }

    public void cancelAlarm() {
        alarmManager.cancel(pendingIntent);
    }
}
