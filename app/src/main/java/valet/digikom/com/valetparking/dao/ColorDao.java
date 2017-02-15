package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.ColorMaster;
import valet.digikom.com.valetparking.domain.ColorMasterResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/10/2017.
 */

public class ColorDao implements ProcessRequest {

    private ValetDbHelper dbHelper;
    private static ColorDao colorDao;

    private ColorDao(ValetDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public static ColorDao getInstance(ValetDbHelper dbHelper) {
        if (colorDao == null) {
            colorDao = new ColorDao(dbHelper);
        }
        return colorDao;

    }

    private void downloadColorMaster(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<ColorMasterResponse> call = apiEndpoint.getColors(100);
        call.enqueue(new Callback<ColorMasterResponse>() {
            @Override
            public void onResponse(Call<ColorMasterResponse> call, Response<ColorMasterResponse> response) {
                if (response != null && response.body() != null) {
                    List<ColorMaster> colorMasters = response.body().getColorMasterList();
                    insertColors(colorMasters);
                }
            }

            @Override
            public void onFailure(Call<ColorMasterResponse> call, Throwable t) {

                Toast.makeText(dbHelper.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.d("Color Master", t.getMessage());
            }
        });
    }

    private void insertColors(List<ColorMaster> colorMasters) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + ColorMaster.Table.TABLE_NAME);
        for (ColorMaster color : colorMasters) {
            ContentValues cv = new ContentValues();
            cv.put(ColorMaster.Table.COL_ID, color.getId());
            cv.put(ColorMaster.Table.COL_COLOR_ID, color.getAttrib().getId_color());
            cv.put(ColorMaster.Table.COL_COLOR_NAME, color.getAttrib().getColorName());
            cv.put(ColorMaster.Table.COL_COLOR_HEX, color.getAttrib().getColorHex());

            db.insert(ColorMaster.Table.TABLE_NAME, null, cv);
        }
    }

    public List<ColorMaster> fetchColors() {
        List<ColorMaster> colorMasterList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String qry = "SELECT * FROM " + ColorMaster.Table.TABLE_NAME + " ORDER BY " + ColorMaster.Table.COL_COLOR_NAME;
        Cursor cursor = db.rawQuery(qry, new String[]{});
        if (cursor.moveToFirst()) {
            do {
                ColorMaster colorMaster = new ColorMaster();
                colorMaster.setId(cursor.getInt(cursor.getColumnIndex(ColorMaster.Table.COL_ID)));
                ColorMaster.Attrib attrib = new ColorMaster.Attrib();
                attrib.setId_color(cursor.getInt(cursor.getColumnIndex(ColorMaster.Table.COL_COLOR_ID)));
                attrib.setColorName(cursor.getString(cursor.getColumnIndex(ColorMaster.Table.COL_COLOR_NAME)));
                attrib.setColorHex(cursor.getString(cursor.getColumnIndex(ColorMaster.Table.COL_COLOR_HEX)));

                colorMaster.setAttrib(attrib);
                colorMasterList.add(colorMaster);
            }while (cursor.moveToNext());
        }

        cursor.close();

        return colorMasterList;
    }

    @Override
    public void process(String token) {
        downloadColorMaster(token);
    }
}
