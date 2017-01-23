package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.AddCarCallBody;
import valet.digikom.com.valetparking.domain.AddCarCallResponse;
import valet.digikom.com.valetparking.domain.EntryCheckin;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/19/2017.
 */

public class CallDao implements ProcessRequest {

    private int id;
    private AddCarCallBody addCarCallBody;
    private static CallDao callDao;
    private Context context;
    private ValetDbHelper dbHelper;

    private CallDao(int id, AddCarCallBody addCarCallBody, Context context) {
        this.id = id;
        this.addCarCallBody = addCarCallBody;
        this.context = context;
        dbHelper = new ValetDbHelper(context);
    }

    public static CallDao getInstance(Context context) {
        return getInstance(0,null,context);
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
            }
        });
    }

    public void setCalledCarById(int id, int isCalled) {
        ValetDbHelper dbHelper = new ValetDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};

        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinResponse.Table.COL_IS_CALLED, isCalled);
        db.update(EntryCheckinResponse.Table.TABLE_NAME,cv, EntryCheckinResponse.Table.COL_RESPONSE_ID + "=?", args);

        db.close();
    }

    @Override
    public void process(String token) {
        callCar(token);
    }

    public List<EntryCheckinResponse> fetchAllCalledCars () {
        List<EntryCheckinResponse> responseList = new ArrayList<>();
        Gson gson = new Gson();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {"1"};
        Cursor c = db.query(EntryCheckinResponse.Table.TABLE_NAME,null, EntryCheckinResponse.Table.COL_IS_CALLED + "=?", args,null,null,EntryCheckinResponse.Table.COL_RESPONSE_ID + " DESC");

        if (c.moveToFirst()) {
            do {
                String jsonResponse = c.getString(c.getColumnIndex(EntryCheckinResponse.Table.COL_JSON_RESPONSE));
                EntryCheckinResponse response = gson.fromJson(jsonResponse, EntryCheckinResponse.class);
                if (response != null) {
                    responseList.add(response);
                }
            }while (c.moveToNext());
        }

        return responseList;
    }
}
