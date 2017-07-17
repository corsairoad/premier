package valet.digikom.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.fragments.CalledCarFragment;
import valet.digikom.com.valetparking.fragments.ParkedCarFragment;

/**
 * Created by DIGIKOM-EX4 on 3/2/2017.
 */

public class ReadyCheckoutService extends IntentService {

    private static final String TAG = ReadyCheckoutService.class.getSimpleName();

    public ReadyCheckoutService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.d(TAG, "ALARM STARTED");
            CheckoutDao checkoutDao = CheckoutDao.getInstance(this, CalledCarFragment.getInstance(), ParkedCarFragment.getInstance());
            TokenDao.getToken(checkoutDao, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
