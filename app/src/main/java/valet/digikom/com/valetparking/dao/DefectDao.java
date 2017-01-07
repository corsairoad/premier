package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.domain.DefectResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by dev on 1/7/17.
 */

public class DefectDao {

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

    public void downloadDefects() {
        ApiEndpoint apiEndpoint = ApiClient.getClient().create(ApiEndpoint.class);
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

    public void insertDefects(List<DefectMaster> defectMasterList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DefectMaster.Table.TABLE_NAME);

        for (DefectMaster defect: defectMasterList) {
            ContentValues cv = new ContentValues();
            cv.put(DefectMaster.Table.COL_ID, defect.getId());
            cv.put(DefectMaster.Table.COL_NAME, defect.getDefectName());
            cv.put(DefectMaster.Table.COL_DESC, defect.getDefectDesc());
            cv.put(DefectMaster.Table.COL_HREF, defect.getHref());

            db.insert(DefectMaster.Table.TABLE_NAME, null, cv);

        }
    }
}
