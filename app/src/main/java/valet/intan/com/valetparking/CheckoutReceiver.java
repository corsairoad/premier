package valet.intan.com.valetparking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import valet.intan.com.valetparking.dao.CheckoutDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.fragments.CalledCarFragment;
import valet.intan.com.valetparking.fragments.ParkedCarFragment;

/**
 * Created by DIGIKOM-EX4 on 1/24/2017.
 */

public class CheckoutReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            CheckoutDao checkoutDao = CheckoutDao.getInstance(context, CalledCarFragment.getInstance(), ParkedCarFragment.getInstance());
            TokenDao.getToken(checkoutDao, context);
        }catch (Exception e){
            e.printStackTrace();
        }

        //Toast.makeText(context, "Receiver called",Toast.LENGTH_SHORT).show();
    }
}
