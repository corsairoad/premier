package valet.intan.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.domain.Disclaimer;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 2/27/2017.
 */

public class DisclaimerDao implements ProcessRequest {

    private Context context;
    private ValetDbHelper dbHelper;
    private static DisclaimerDao disclaimerDao;
    public static final String FLAG_CHECKIN_DISCLAIMER = "1";
    public static final String FLAG_CHECKOUT_DISCLAIMER = "2";

    private DisclaimerDao(Context context) {
        this.context = context;
        this.dbHelper = ValetDbHelper.getInstance(context.getApplicationContext());
    }

    public static DisclaimerDao getInstance(Context context) {
        if (disclaimerDao == null) {
            disclaimerDao = new DisclaimerDao(context);
        }
        return disclaimerDao;
    }

    @Override
    public void process(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
        Call<Disclaimer> call = apiEndpoint.getDisclaimer(token);
        call.enqueue(new Callback<Disclaimer>() {
            @Override
            public void onResponse(Call<Disclaimer> call, Response<Disclaimer> response) {
                if (response != null && response.body() != null) {
                    insertToDb(response.body());
                }
            }

            @Override
            public void onFailure(Call<Disclaimer> call, Throwable t) {

            }
        });
    }

    private void insertToDb(Disclaimer disclaimer) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Disclaimer.Data> dataList = disclaimer.getDataList();
        Gson gson = new Gson();
        db.execSQL("DELETE FROM " + Disclaimer.Table.TABLE_NAME);
        for (Disclaimer.Data data : dataList) {
            ContentValues cv = new ContentValues();
            cv.put(Disclaimer.Table.COL_ID_DISCLAIMER, data.getAttrib().getId());
            cv.put(Disclaimer.Table.COL_JSON_DATA, gson.toJson(data));

            db.insert(Disclaimer.Table.TABLE_NAME, null, cv);
        }
    }

    public Disclaimer.Data getDisclaimer(String flag) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {flag};
        Cursor c = db.rawQuery("SELECT * FROM " + Disclaimer.Table.TABLE_NAME + " WHERE " + Disclaimer.Table.COL_ID_DISCLAIMER + "=?", args);
        Disclaimer.Data disclaimer = null;
        if (c.moveToFirst()) {
            Gson gson = new Gson();
            disclaimer = gson.fromJson(c.getString(c.getColumnIndex(Disclaimer.Table.COL_JSON_DATA)), Disclaimer.Data.class);
        }
        return disclaimer;
    }
}
