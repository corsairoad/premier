package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.adapter.ListValetTypeAdapter;
import valet.digikom.com.valetparking.domain.ValetTypeJson;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/27/2017.
 */

public class ValetTypeDao implements ProcessRequest {

    private Context context;
    private static ValetTypeDao valetTypeDao;
    private static ValetDbHelper dbHelper;
    private Gson gson;

    private ValetTypeDao(Context context) {
        this.context = context.getApplicationContext();
        dbHelper = ValetDbHelper.getInstance(context.getApplicationContext());
        gson = new Gson();

    }

    public static ValetTypeDao getInstance(Context c) {
        if (valetTypeDao == null) {
            valetTypeDao = new ValetTypeDao(c);
        }
        return valetTypeDao;
    }

    @Override
    public void process(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<ValetTypeJson> call = apiEndpoint.getValetType();
        call.enqueue(new Callback<ValetTypeJson>() {
            @Override
            public void onResponse(Call<ValetTypeJson> call, Response<ValetTypeJson> response) {
                if (response != null && response.body() != null) {
                    insertData(response.body().getListData());
                }
            }

            @Override
            public void onFailure(Call<ValetTypeJson> call, Throwable t) {

            }
        });
    }

    private void insertData(List<ValetTypeJson.Data> data) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + ValetTypeJson.Table.TABLE_NAME);

        for (ValetTypeJson.Data d : data) {
            String id = String.valueOf(d.getAttrib().getId());
            String jsonData = gson.toJson(d);
            ContentValues cv = new ContentValues();
            cv.put(ValetTypeJson.Table.COL_JSON_DATA, jsonData);
            cv.put(ValetTypeJson.Table.COL_ID_DATA, id);

            db.insert(ValetTypeJson.Table.TABLE_NAME,null, cv);
        }
    }

    public List<ValetTypeJson.Data> getListData() {
        List<ValetTypeJson.Data> listData = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ValetTypeJson.Table.TABLE_NAME,new String[]{});
        if (c.moveToFirst()) {
            do {
                String sData = c.getString(c.getColumnIndex(ValetTypeJson.Table.COL_JSON_DATA));
                ValetTypeJson.Data data = gson.fromJson(sData, ValetTypeJson.Data.class);
                if (data != null) {
                    listData.add(data);
                }
            }while (c.moveToNext());
        }
        c.close();
        return listData;
    }
}
