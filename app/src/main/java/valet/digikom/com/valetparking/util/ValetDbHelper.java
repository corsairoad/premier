package valet.digikom.com.valetparking.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import valet.digikom.com.valetparking.domain.Checkin;

/**
 * Created by DIGIKOM-EX4 on 12/20/2016.
 */

public class ValetDbHelper extends SQLiteOpenHelper {


    private Context context;
    private static final String DB_NAME  = "valetdb";
    private static final int DB_VERSION = 1;


    public ValetDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Checkin.Table.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Checkin.Table.TABLE_NAME);
        onCreate(db);
    }
}
