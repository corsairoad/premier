package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
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

    private static DefectDao defectDao;
    private ValetDbHelper dbHelper;

    private DefectDao(ValetDbHelper dbHelper) {
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

                DefectMaster.Defect defect = new DefectMaster.Defect();
                defect.setId(cursor.getInt(cursor.getColumnIndex(DefectMaster.Table.COL_ID)));
                defect.setDefectName(cursor.getString(cursor.getColumnIndex(DefectMaster.Table.COL_NAME)));
                defect.setDefectDesc(cursor.getString(cursor.getColumnIndex(DefectMaster.Table.COL_DESC)));
                defect.setFileName(cursor.getString(cursor.getColumnIndex(DefectMaster.Table.COL_IMAGE_NAME)));
                defect.setFilePath(cursor.getString(cursor.getColumnIndex(DefectMaster.Table.COL_IMAGE_PATH)));

                attr.setDefect(defect);
                attr.setImgHeight(cursor.getFloat(cursor.getColumnIndex(DefectMaster.Table.COL_IMAGE_HEIGHT)));
                attr.setImgWidth(cursor.getFloat(cursor.getColumnIndex(DefectMaster.Table.COL_IMAGE_WIDTH)));
                attr.setxAxis(cursor.getFloat(cursor.getColumnIndex(DefectMaster.Table.COL_X_AXIS)));
                attr.setyAxis(cursor.getFloat(cursor.getColumnIndex(DefectMaster.Table.COL_Y_AXIS)));

                defectMaster.setAttributes(attr);

                defectMasters.add(defectMaster);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return defectMasters;
    }

    private void insertDefects(List<DefectMaster> defectMasterList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DefectMaster.Table.TABLE_NAME);

        for (DefectMaster defect: defectMasterList) {
            DefectMaster.Defect d = defect.getAttributes().getDefect();
            DefectMaster.DefectAttributes attributes = defect.getAttributes();

            ContentValues cv = new ContentValues();
            cv.put(DefectMaster.Table.COL_ID, d.getId());
            cv.put(DefectMaster.Table.COL_NAME, d.getDefectName());
            cv.put(DefectMaster.Table.COL_DESC, d.getDefectDesc());
            cv.put(DefectMaster.Table.COL_IMAGE_NAME, d.getFileName());
            cv.put(DefectMaster.Table.COL_IMAGE_PATH, d.getFilePath());
            cv.put(DefectMaster.Table.COL_IMAGE_HEIGHT, attributes.getImgHeight());
            cv.put(DefectMaster.Table.COL_IMAGE_WIDTH, attributes.getImgWidth());
            cv.put(DefectMaster.Table.COL_X_AXIS, attributes.getxAxis());
            cv.put(DefectMaster.Table.COL_Y_AXIS, attributes.getyAxis());

            db.insert(DefectMaster.Table.TABLE_NAME, null, cv);
        }
    }

    @Override
    public void process(String token) {
        downloadDefects(token);
    }
}
