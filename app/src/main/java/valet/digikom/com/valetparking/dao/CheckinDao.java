package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 12/20/2016.
 */

public class CheckinDao {

    private ValetDbHelper valetDbHelper;
    private static CheckinDao checkinDao;

    private CheckinDao(ValetDbHelper valetDbHelper) {
        this.valetDbHelper = valetDbHelper;
    }

    public static CheckinDao getInstance(ValetDbHelper dbHelper) {
        if (checkinDao == null) {
            checkinDao = new CheckinDao(dbHelper);
        }
        return checkinDao;
    }

    public void addCheckIn(Checkin checkin) {
        SQLiteDatabase db = valetDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Checkin.Table.COL_TRANSACTION_ID, checkin.getTransactionId());
        values.put(Checkin.Table.COL_PLAT_NO, checkin.getPlatNo());
        values.put(Checkin.Table.COL_MERK, checkin.getMerkMobil());
        values.put(Checkin.Table.COL_WARNA, checkin.getWarnaMobil());
        values.put(Checkin.Table.COL_JENIS, checkin.getJenisMobil());
        values.put(Checkin.Table.COL_EMAIL, checkin.getEmailCustomer());
        values.put(Checkin.Table.COL_RUNNER, checkin.getRunnerName());
        values.put(Checkin.Table.COL_DROP_POINT, checkin.getDropPoint());

        db.insert(Checkin.Table.CREATE_TABLE,null,values);
        db.close();
    }

    public List<Checkin> getAllListCheckIn() throws ParseException {
        List<Checkin> checkinList = new ArrayList<>();
        SQLiteDatabase db = valetDbHelper.getReadableDatabase();
        String query = "SELECT * FROM " + Checkin.Table.TABLE_NAME + " ORDER BY " + Checkin.Table.COL_CHECKIN_TIME + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY HH:mm:ss", Locale.getDefault());

        if (cursor.moveToFirst()) {
            do {
                Checkin checkin = new Checkin();
                checkin.setTransactionId(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_TRANSACTION_ID)));
                checkin.setPlatNo(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_PLAT_NO)));
                checkin.setMerkMobil(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_MERK)));
                checkin.setWarnaMobil(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_WARNA)));
                checkin.setCheckinTime(sdf.parse(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_CHECKIN_TIME))));
                checkin.setJenisMobil(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_JENIS)));
                checkin.setEmailCustomer(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_EMAIL)));
                checkin.setRunnerName(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_RUNNER)));
                checkin.setDropPoint(cursor.getString(cursor.getColumnIndex(Checkin.Table.COL_DROP_POINT)));

                checkinList.add(checkin);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return checkinList;
    }
}
