package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.Main2Activity;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.FinishCheckOut;
import valet.digikom.com.valetparking.domain.FinishCheckoutResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class FinishCheckoutDao implements ProcessRequest {

    private Context context;
    private ValetDbHelper dbHelper;
    private static FinishCheckoutDao finishCheckoutDao;
    private  int id;
    private  FinishCheckOut finishCheckOut;

    private FinishCheckoutDao(Context context) {
        this.context = context;
        dbHelper = ValetDbHelper.getInstance(context.getApplicationContext());
    }

    public static FinishCheckoutDao getInstance(Context c) {
        if (finishCheckoutDao == null) {
            finishCheckoutDao = new FinishCheckoutDao(c);
        }
        return finishCheckoutDao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FinishCheckOut getFinishCheckOut() {
        return finishCheckOut;
    }

    public void setFinishCheckOut(FinishCheckOut finishCheckOut) {
        this.finishCheckOut = finishCheckOut;
    }

    @Override
    public void process(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<FinishCheckoutResponse> call = apiEndpoint.submitCheckout(getId(),getFinishCheckOut());
        call.enqueue(new Callback<FinishCheckoutResponse>() {
            @Override
            public void onResponse(Call<FinishCheckoutResponse> call, Response<FinishCheckoutResponse> response) {
                if (response != null && response.body() != null) {
                    int id = response.body().getData().getAttrib().getId();
                    setCheckoutCar(id);
                    Toast.makeText(context,"Checkout success", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FinishCheckoutResponse> call, Throwable t) {

            }
        });
    }


    private void setCheckoutCar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinResponse.Table.COL_IS_CHECKOUT, 1);
        db.update(EntryCheckinResponse.Table.TABLE_NAME,cv, EntryCheckinResponse.Table.COL_RESPONSE_ID + " =?", args);
    }
}
