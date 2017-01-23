package valet.digikom.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by DIGIKOM-EX4 on 1/23/2017.
 */

public class EntryCheckoutService extends IntentService {

    private static final String TAG_SERVICE = EntryCheckoutService.class.getSimpleName();

    public EntryCheckoutService() {
        super(TAG_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("Entry Checkout", "Entry checkout alarm called");
    }
}
