package valet.intan.com.valetparking.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import valet.intan.com.valetparking.dao.EntryDao;

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
        }
    }

    private void startRefreshTokenAlarm(Context context) {
        RefreshTokenAlarm refreshTokenAlarm = RefreshTokenAlarm.getInstance(context);
        refreshTokenAlarm.startAlarmIn5Days();
        Log.d(TAG, "onReceive: refresh token alarm resetted");
    }

    private void removeAllCheckinList(Context context) {
        EntryDao.getInstance(context).removeAllCheckinList();
    }
}
