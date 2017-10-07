package valet.intan.com.valetparking.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import valet.intan.com.valetparking.Main2Activity;
import valet.intan.com.valetparking.dao.EntryDao;
import valet.intan.com.valetparking.service.LoggingUtils;

/**
 * Created by fadlymunandar on 8/23/17.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompleteReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startRefreshTokenAlarm(context);
            removeAllCheckinList(context);
            removeLogFiles(context);
            launchApp(context);
        }
    }

    private void launchApp(Context context) {
        Intent inten = new Intent(context, Main2Activity.class);
        inten.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(inten);
    }

    private void startRefreshTokenAlarm(Context context) {
        RefreshTokenAlarm refreshTokenAlarm = RefreshTokenAlarm.getInstance(context);
        refreshTokenAlarm.startAlarmIn5Days();
        refreshTokenAlarm.startRepeatAlarm();
        Log.d(TAG, "onReceive: refresh token alarm resetted");
    }

    private void removeAllCheckinList(Context context) {
        EntryDao.getInstance(context).removeAllCheckinList();
    }

    private void removeLogFiles(Context context) {
        LoggingUtils.getInstance(context).removeLogFile();
    }
}
