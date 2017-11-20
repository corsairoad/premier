package valet.intan.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by fadlymunandar on 10/4/17.
 */

public class ServiceSendMail extends IntentService {

    public static final String ACTION_SEND_REPORT = "com.premier.send.log.report";
    private static final String TAG = ServiceSendMail.class.getSimpleName();


    public ServiceSendMail() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        LoggingUtils.getInstance(this).sendMail();
    }
}
