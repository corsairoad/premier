package valet.intan.com.valetparking.domain;

import android.content.Context;

import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/31/2017.
 */

public class MembershipDao implements ProcessRequest {

    private Context context;
    private static MembershipDao mDao;
    private ValetDbHelper dbHelper;

    private MembershipDao(Context context) {
        this.context = context.getApplicationContext();
        dbHelper = ValetDbHelper.getInstance(context.getApplicationContext());
    }

    private static MembershipDao getInstance(Context context) {
        if (mDao == null) {
            mDao = new MembershipDao(context);
        }
        return mDao;
    }

    @Override
    public void process(String token) {

    }
}
