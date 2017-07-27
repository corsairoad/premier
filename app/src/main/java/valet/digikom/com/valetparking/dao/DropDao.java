package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.DropPointMaster;
import valet.digikom.com.valetparking.domain.DropPointMasterResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/11/2017.
 */

public class DropDao implements ProcessRequest {

    private ValetDbHelper dbHelper;
    private static DropDao dropDao;

    private DropDao(ValetDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public static DropDao getInstance(ValetDbHelper dbHelper){
        if (dropDao == null) {
            dropDao = new DropDao(dbHelper);
        }
        return dropDao;
    }

    private void downloadDropPoints(String token){
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
        Call<DropPointMasterResponse> call = apiEndpoint.getDropPoints(token);
        call.enqueue(new Callback<DropPointMasterResponse>() {
            @Override
            public void onResponse(Call<DropPointMasterResponse> call, Response<DropPointMasterResponse> response) {
                if (response != null && response.body() != null) {
                    List<DropPointMaster> dropPointList = response.body().getDropPointList();
                    insertDropPoints(dropPointList);
                }
            }

            @Override
            public void onFailure(Call<DropPointMasterResponse> call, Throwable t) {
                Toast.makeText(dbHelper.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.d("DropPointMaster error: ", t.getMessage());
            }
        });
    }

    public void insertDropPoints(List<DropPointMaster> dropPointList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DropPointMaster.Table.TABLE_NAME);
        for (DropPointMaster dp : dropPointList) {
            ContentValues cv = new ContentValues();
            cv.put(DropPointMaster.Table.COL_ID, Integer.valueOf(dp.getId()));
            cv.put(DropPointMaster.Table.COL_TYPE, dp.getType());
            cv.put(DropPointMaster.Table.COL_DROP_ID, dp.getAttrib().getDropId());
            cv.put(DropPointMaster.Table.COL_DROP_NAME, dp.getAttrib().getDropName());
            cv.put(DropPointMaster.Table.COL_DROP_DESC, dp.getAttrib().getDropDesc());
            cv.put(DropPointMaster.Table.COL_LATITUDE, dp.getAttrib().getLatitude());
            cv.put(DropPointMaster.Table.COL_LONGITUDE, dp.getAttrib().getLongitude());

            db.insert(DropPointMaster.Table.TABLE_NAME, null, cv);
        }
    }

    public List<DropPointMaster> fetchAllDropPoints() {
        List<DropPointMaster> dropPointList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + DropPointMaster.Table.TABLE_NAME, new String[]{});
        if (c.moveToFirst()) {
            do {
                DropPointMaster dropPoint = new DropPointMaster();
                DropPointMaster.Attrib attrib = new DropPointMaster.Attrib();
                attrib.setDropId(c.getInt(c.getColumnIndex(DropPointMaster.Table.COL_DROP_ID)));
                attrib.setDropName(c.getString(c.getColumnIndex(DropPointMaster.Table.COL_DROP_NAME)));
                attrib.setDropDesc(c.getString(c.getColumnIndex(DropPointMaster.Table.COL_DROP_DESC)));
                attrib.setLatitude(c.getDouble(c.getColumnIndex(DropPointMaster.Table.COL_LATITUDE)));
                attrib.setLongitude(c.getDouble(c.getColumnIndex(DropPointMaster.Table.COL_LONGITUDE)));

                int id = c.getInt(c.getColumnIndex(DropPointMaster.Table.COL_ID));

                dropPoint.setId(String.valueOf(id));
                dropPoint.setType(c.getString(c.getColumnIndex(DropPointMaster.Table.COL_TYPE)));
                dropPoint.setAttrib(attrib);

                dropPointList.add(dropPoint);
            }while (c.moveToNext());
        }

        c.close();

        return dropPointList;
    }

    public DropPointMaster getDropPointById(int idDropPoint) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = null;
        DropPointMaster dropPoint = new DropPointMaster();
        DropPointMaster.Attrib attrib = new DropPointMaster.Attrib();
        try {
            String[] args = new String[] {String.valueOf(idDropPoint)};
            c = db.query(DropPointMaster.Table.TABLE_NAME,null,DropPointMaster.Table.COL_DROP_ID + "=?",args,null,null,null);

            if (c.moveToFirst()) {
                do {
                    attrib.setDropId(c.getInt(c.getColumnIndex(DropPointMaster.Table.COL_DROP_ID)));
                    attrib.setDropName(c.getString(c.getColumnIndex(DropPointMaster.Table.COL_DROP_NAME)));
                    attrib.setDropDesc(c.getString(c.getColumnIndex(DropPointMaster.Table.COL_DROP_DESC)));
                    attrib.setLatitude(c.getDouble(c.getColumnIndex(DropPointMaster.Table.COL_LATITUDE)));
                    attrib.setLongitude(c.getDouble(c.getColumnIndex(DropPointMaster.Table.COL_LONGITUDE)));

                    int id = c.getInt(c.getColumnIndex(DropPointMaster.Table.COL_ID));

                    dropPoint.setId(String.valueOf(id));
                    dropPoint.setType(c.getString(c.getColumnIndex(DropPointMaster.Table.COL_TYPE)));
                    dropPoint.setAttrib(attrib);
                }while (c.moveToNext());
            }

        }catch (SQLiteException e) {
            e.printStackTrace();
        }finally {
            if (c != null) {
                c.close();
            }
        }

        return dropPoint;
    }

    @Override
    public void process(String token) {
        downloadDropPoints(token);
    }
}
