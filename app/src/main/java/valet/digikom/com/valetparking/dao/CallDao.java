package valet.digikom.com.valetparking.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.AddCarCallBody;
import valet.digikom.com.valetparking.domain.AddCarCallResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;

/**
 * Created by DIGIKOM-EX4 on 1/19/2017.
 */

public class CallDao implements ProcessRequest {

    private int id;
    private AddCarCallBody addCarCallBody;
    private static CallDao callDao;
    private Context context;

    private CallDao(int id, AddCarCallBody addCarCallBody, Context context) {
        this.id = id;
        this.addCarCallBody = addCarCallBody;
        this.context = context;
    }

    public static CallDao getInstance(int id, AddCarCallBody addCarCallBody, Context context) {
        if (callDao == null) {
            callDao = new CallDao(id,addCarCallBody,context);
        }

        return callDao;
    }

    private void callCar(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<AddCarCallResponse> call = apiEndpoint.postCallCar(id,addCarCallBody);
        call.enqueue(new Callback<AddCarCallResponse>() {
            @Override
            public void onResponse(Call<AddCarCallResponse> call, Response<AddCarCallResponse> response) {
                if (response != null & response.body() != null) {
                    Toast.makeText(context,"Call success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddCarCallResponse> call, Throwable t) {
                //Log.d("call error:", t.getMessage());
            }
        });
    }

    @Override
    public void process(String token) {
        callCar(token);
    }
}
