package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.CarMaster;
import valet.digikom.com.valetparking.domain.CarMasterResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/9/2017.
 */

public class CarDao implements ProcessRequest {

    private ValetDbHelper dbHelper;
    private static CarDao carDao;

    private CarDao(ValetDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public static CarDao getInstance(ValetDbHelper dbHelper) {
        if(carDao == null) {
            carDao = new CarDao(dbHelper);
        }
        return carDao;
    }

    public List<CarMaster> fetchAllCars() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<CarMaster> carMasterList = new ArrayList<>();
        String qry = "SELECT * FROM " + CarMaster.Table.TABLE_NAME + " ORDER BY " + CarMaster.Table.COL_CAR_NAME;
        Cursor cursor = db.rawQuery(qry, new String[]{});

        try{
            if (cursor.moveToFirst()) {
                do {
                    CarMaster carMaster = new CarMaster();
                    CarMaster.Attrib attrib = new CarMaster.Attrib();
                    attrib.setId_attrib(cursor.getInt(cursor.getColumnIndex(CarMaster.Table.COL_CAR_ID)));
                    attrib.setCarName(cursor.getString(cursor.getColumnIndex(CarMaster.Table.COL_CAR_NAME)));
                    carMaster.setId(cursor.getInt(cursor.getColumnIndex(CarMaster.Table.COL_ID)));
                    carMaster.setAttrib(attrib);

                    carMasterList.add(carMaster);
                }while (cursor.moveToNext());
            }
        }catch (SQLiteException e) {
            e.printStackTrace();
        }finally {
            cursor.close();
        }

        return carMasterList;
    }

    private void downloadCarMaster(String token) {
        ApiEndpoint endpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<CarMasterResponse> call = endpoint.getCars();
        call.enqueue(new Callback<CarMasterResponse>() {
            @Override
            public void onResponse(Call<CarMasterResponse> call, Response<CarMasterResponse> response) {
                if (response != null && response.body() !=null) {
                    List<CarMaster> carMasterList = response.body().getData();
                    insertCars(carMasterList);
                }
            }

            @Override
            public void onFailure(Call<CarMasterResponse> call, Throwable t) {
                //Log.d("Download Car Master", t.getMessage());
                Toast.makeText(dbHelper.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertCars(List<CarMaster> carMasterList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + CarMaster.Table.TABLE_NAME);
        for (CarMaster carMaster : carMasterList) {
            ContentValues cv = new ContentValues();
            cv.put(CarMaster.Table.COL_ID, carMaster.getId());
            cv.put(CarMaster.Table.COL_CAR_NAME, carMaster.getAttrib().getCarName());
            cv.put(CarMaster.Table.COL_CAR_ID, carMaster.getAttrib().getId_attrib());

            db.insert(CarMaster.Table.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void process(String token) {
        downloadCarMaster(token);
    }
}
