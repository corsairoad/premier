package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.epson.eposprint.EposException;
import com.google.gson.Gson;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import valet.digikom.com.valetparking.AddCarActivity;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.ReprintCheckin;
import valet.digikom.com.valetparking.domain.ShowMsg;
import valet.digikom.com.valetparking.fragments.ParkedCarFragment;
import valet.digikom.com.valetparking.util.BitmapToString;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 4/5/2017.
 */

public class ReprintDao {
    Context context;
    ValetDbHelper dbHelper;
    static ReprintDao reprintDao;
    Gson gson;

    private ReprintDao(Context context) {
        this.context = context;
        dbHelper = ValetDbHelper.getInstance(context);
        gson = new Gson();
    }

    public static ReprintDao getInstance(Context context) {
        if (reprintDao == null) {
            reprintDao = new ReprintDao(context.getApplicationContext());
        }
        return reprintDao;
    }

    public void saveReprintData(EntryCheckinResponse entryCheckin, String noTiket, Bitmap bmpDefects, Bitmap bmpSignature, List<AdditionalItems> items) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String stringEntryCheckin = gson.toJson(entryCheckin);
        String defects = BitmapToString.create(bmpDefects);
        String signature = BitmapToString.create(bmpSignature);
        String stringListItem = listToString(items);

        ContentValues cv = new ContentValues();
        cv.put(Table.COL_ENTRY_CHECKIN, stringEntryCheckin);
        cv.put(Table.COL_DEFECTS, defects);
        cv.put(Table.COL_NO_TIKET, noTiket);
        cv.put(Table.COL_SIGNATURE, signature);
        cv.put(Table.COL_LIST_STUFF, stringListItem);

        db.insert(Table.TABLE_NAME,null,cv);
    }

    private String listToString(List<AdditionalItems> items) {
        if (items != null && !items.isEmpty()) {
            String listStuffString = gson.toJson(items);
            return listStuffString;
        }
        return null;
    }

    public int rePrint(String noTiket) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "SELECT * FROM " + Table.TABLE_NAME + " WHERE " + Table.COL_NO_TIKET + " = ?;";
        String[] args = new String[] {noTiket};
        Cursor c = db.rawQuery(sql,args);

        if (c.moveToFirst()) {
            String sEntry = c.getString(c.getColumnIndex(Table.COL_ENTRY_CHECKIN));
            String sDefects = c.getString(c.getColumnIndex(Table.COL_DEFECTS));
            String sSignature = c.getString(c.getColumnIndex(Table.COL_SIGNATURE));
            String sStuffs = c.getString(c.getColumnIndex(Table.COL_LIST_STUFF));

            EntryCheckinResponse entry = gson.fromJson(sEntry, EntryCheckinResponse.class);
            Bitmap bmpDefects = BitmapToString.reverse(sDefects);
            Bitmap bmpSignature = BitmapToString.reverse(sSignature);
            List<AdditionalItems> stuffs = stringToList(sStuffs);

            ReprintCheckin reprintCheckin = new ReprintCheckin(context,entry,bmpDefects,bmpSignature, stuffs);
            try {
                reprintCheckin.buildPrintData();
                return 1;
            }catch (EposException e) {
                e.printStackTrace();
                //ShowMsg.showResult(e.getPrinterStatus(),"error reprint ticket", context.getApplicationContext());
                reprintCheckin.closePrinter();
                return -1;
            }

        }
        c.close();
        return 0;
    }

    public void removeReprintData(String noTiket) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Table.TABLE_NAME, Table.COL_NO_TIKET + " =? ", new String[]{noTiket});
    }

    private List<AdditionalItems> stringToList(String string) {
        if (string == null) {
            return null;
        }
        Type type = new TypeToken<List<AdditionalItems>>(){}.getType();
        List<AdditionalItems> items = gson.fromJson(string,type);
        return items;
    }

    public static class Table {
        public static final String TABLE_NAME = "reprint_data";
        public static final String COL_ID = "_id";
        public static final String COL_NO_TIKET = "no_tiket";
        public static final String COL_ENTRY_CHECKIN = "col_entry_checkin";
        public static final String COL_DEFECTS = "defects";
        public static final String COL_SIGNATURE = "signature";
        public static final String COL_LIST_STUFF= "list_stuff";

        public static final String CREATE = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NO_TIKET + " TEXT, " +
                COL_ENTRY_CHECKIN + " TEXT, " +
                COL_DEFECTS + " TEXT, " +
                COL_SIGNATURE + " TEXT, " +
                COL_LIST_STUFF + " TEXT);";
    }
}
