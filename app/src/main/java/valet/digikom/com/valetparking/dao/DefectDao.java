package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.domain.DefectResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by dev on 1/7/17.
 */

public class DefectDao implements ProcessRequest {

    public static DefectDao defectDao;
    ValetDbHelper dbHelper;

    public DefectDao(ValetDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public static DefectDao getInstance(ValetDbHelper helper) {
        if (defectDao == null) {
            defectDao = new DefectDao(helper);
        }
        return defectDao;
    }

    private void downloadDefects(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<DefectResponse> call = apiEndpoint.getDefects();
        call.enqueue(new Callback<DefectResponse>() {
            @Override
            public void onResponse(Call<DefectResponse> call, Response<DefectResponse> response) {
                if (response != null && response.body() != null) {
                    List<DefectMaster> defectResponses = response.body().getData();
                    insertDefects(defectResponses);
                }
            }

            @Override
            public void onFailure(Call<DefectResponse> call, Throwable t) {
                Toast.makeText(dbHelper.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<DefectMaster> getAllDeffects() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DefectMaster.Table.TABLE_NAME, new String[]{});
        List<DefectMaster> defectMasters = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                DefectMaster defectMaster = new DefectMaster();
                DefectMaster.DefectAttributes attr = new DefectMaster.DefectAttributes();
                attr.setDefectName(cursor.getString(cursor.getColumnIndex(DefectMaster.Table.COL_NAME)));
                attr.setDefectDesc(cursor.getString(cursor.getColumnIndex(DefectMaster.Table.COL_DESC)));
                attr.setHref(cursor.getString(cursor.getColumnIndex(DefectMaster.Table.COL_HREF)));
                defectMaster.setId(cursor.getInt(cursor.getColumnIndex(DefectMaster.Table.COL_ID)));
                defectMaster.setAttributes(attr);

                defectMasters.add(defectMaster);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return defectMasters;
    }

    private void insertDefects(List<DefectMaster> defectMasterList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DefectMaster.Table.TABLE_NAME);

        for (DefectMaster defect: defectMasterList) {
            ContentValues cv = new ContentValues();
            cv.put(DefectMaster.Table.COL_ID, defect.getId());
            cv.put(DefectMaster.Table.COL_NAME, defect.getAttributes().getDefectName());
            cv.put(DefectMaster.Table.COL_DESC, defect.getAttributes().getDefectDesc());
            cv.put(DefectMaster.Table.COL_HREF, defect.getAttributes().getHref());

            db.insert(DefectMaster.Table.TABLE_NAME, null, cv);
        }
        db.close();
    }



    @Override
    public void process(String token) {
        downloadDefects(token);
    }
}
