package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.FineFee;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class FineFeeDao implements ProcessRequest {

    private Context context;
    private ValetDbHelper dbHelper;
    private static FineFeeDao fineFeeDao;
    Gson gson = new Gson();


    private FineFeeDao(Context context) {
        this.context = context;
        dbHelper = ValetDbHelper.getInstance(context.getApplicationContext());
    }

    public static FineFeeDao getInstance(Context context) {
        if (fineFeeDao == null) {
            fineFeeDao = new FineFeeDao(context);
        }
        return fineFeeDao;
    }

    private void downloadFineFee(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class,token);
        Call<FineFee> call = apiEndpoint.getFineFees();
        call.enqueue(new Callback<FineFee>() {
            @Override
            public void onResponse(Call<FineFee> call, Response<FineFee> response) {
                if (response != null && response.body()!= null) {
                    insertFineFee(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<FineFee> call, Throwable t) {

            }
        });
    }

    private void insertFineFee(List<FineFee.Fine> data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + FineFee.Table.TABLE_NAME);

        for (FineFee.Fine fine : data) {
            int id = fine.getAttrib().getId();
            String jsonFine = gson.toJson(fine);

            ContentValues cv = new ContentValues();
            cv.put(FineFee.Table.COL_FINE_ID,id);
            cv.put(FineFee.Table.COL_JSON_FINE, jsonFine);
            cv.put(FineFee.Table.COL_FINE_TYPE, fine.getAttrib().getFine_type());

            db.insert(FineFee.Table.TABLE_NAME,null, cv);
        }
    }

    public FineFee.Fine getLostTickeFine() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {FineFee.LOST_TICKET};
        Cursor c = db.query(FineFee.Table.TABLE_NAME,null, FineFee.Table.COL_FINE_TYPE + "=?", args,null,null,null);

        if (c.moveToFirst()) {
            return  gson.fromJson(c.getString(c.getColumnIndex(FineFee.Table.COL_JSON_FINE)), FineFee.Fine.class);
        }

        c.close();
        return null;
    }

    public FineFee.Fine getOvernightFine() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {FineFee.STAY_OVERNIGHT};
        Cursor c = db.query(FineFee.Table.TABLE_NAME,null, FineFee.Table.COL_FINE_TYPE + "=?", args,null,null,null);

        if (c.moveToFirst()) {
            return gson.fromJson(c.getString(c.getColumnIndex(FineFee.Table.COL_JSON_FINE)), FineFee.Fine.class);
        }

        c.close();
        return null;
    }

    @Override
    public void process(String token) {
        downloadFineFee(token);
    }
}
