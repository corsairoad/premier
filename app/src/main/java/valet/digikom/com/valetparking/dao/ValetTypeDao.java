package valet.digikom.com.valetparking.dao;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.ValetTypeJson;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;

/**
 * Created by DIGIKOM-EX4 on 1/27/2017.
 */

public class ValetTypeDao implements ProcessRequest {

    private Context context;
    private static ValetTypeDao valetTypeDao;

    private ValetTypeDao(Context context) {
        this.context = context.getApplicationContext();
    }

    public ValetTypeDao getInstance(Context c) {
        if (valetTypeDao == null) {
            valetTypeDao = new ValetTypeDao(c);
        }
        return valetTypeDao;
    }

    @Override
    public void process(String token) {

    }
}
