package valet.digikom.com.valetparking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.fragments.CalledCarFragment;
import valet.digikom.com.valetparking.fragments.ParkedCarFragment;

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
