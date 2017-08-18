package valet.intan.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.epson.eposprint.EposException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.domain.AdditionalItems;
import valet.intan.com.valetparking.domain.EntryCheckinResponse;
import valet.intan.com.valetparking.domain.PostReprintCheckin;
import valet.intan.com.valetparking.domain.PostReprintCheckinResponse;
import valet.intan.com.valetparking.domain.ReprintCheckin;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.BitmapToString;
import valet.intan.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 4/5/2017.
 */

public class ReprintDao{

    Context context;
    ValetDbHelper dbHelper;
    static ReprintDao reprintDao;
    Gson gson;

    public static final int STATUS_PRINT_SUCCEED = 1;
    public static final int STATUS_PRINT_FAILED = 0;
    public static final int STATUS_PRINT_ERROR = -1;

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
        if (noTiket == null) {
            return STATUS_PRINT_FAILED;
        }

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
                return STATUS_PRINT_SUCCEED;
            }catch (EposException e) {
                e.printStackTrace();
                //ShowMsg.showResult(e.getPrinterStatus(),"error reprint ticket", context.getApplicationContext());
                reprintCheckin.closePrinter();
                return STATUS_PRINT_ERROR;
            }

        }

        c.close();

        return STATUS_PRINT_FAILED; // THIS IS THE CORRECT USAGE
        //return STATUS_PRINT_SUCCEED; // NOT THIS. THIS IS FOR TRIAL PURPOSE.
    }

    public void postReprintData(final String noTiket, final String platNo, final String appTime) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                PostReprintCheckin postReprintCheckin = new PostReprintCheckin.Builder()
                        .setLicensePlate(platNo)
                        .setTicketNo(noTiket)
                        .setAppsTime(appTime)
                        .build();

                String json = new Gson().toJson(postReprintCheckin);
                Log.d("REPRINT", "DATA: " + json);

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                Call<PostReprintCheckinResponse> call = apiEndpoint.postReprint(postReprintCheckin, token);
                call.enqueue(new Callback<PostReprintCheckinResponse>() {
                    @Override
                    public void onResponse(Call<PostReprintCheckinResponse> call, Response<PostReprintCheckinResponse> response) {
                        if (response != null && response.body() != null) {
                            Log.d("REPRINT", "post reprint succeed");
                        }
                    }

                    @Override
                    public void onFailure(Call<PostReprintCheckinResponse> call, Throwable t) {
                        Log.d("REPRINT", " post reprint failed", t);
                    }
                });
            }
        },context);
    }

    public void removeReprintData(String noTiket) {
        if (noTiket == null) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Table.TABLE_NAME, Table.COL_NO_TIKET + " =? ", new String[]{noTiket.trim()});
    }

    private List<AdditionalItems> stringToList(String string) {
        if (string == null) {
            return null;
        }
        Type type = new TypeToken<List<AdditionalItems>>(){}.getType();
        List<AdditionalItems> items = gson.fromJson(string,type);
        return items;
    }

    public void clearAllDataReprint() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(Table.TABLE_NAME, null, new String[]{});
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
