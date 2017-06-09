package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import com.epson.eposprint.EposException;
import com.google.gson.Gson;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.AddCarActivity;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.GetReprintCheckinResponse;
import valet.digikom.com.valetparking.domain.PostReprintCheckin;
import valet.digikom.com.valetparking.domain.PostReprintCheckinResponse;
import valet.digikom.com.valetparking.domain.ReprintCheckin;
import valet.digikom.com.valetparking.domain.ShowMsg;
import valet.digikom.com.valetparking.domain.Token;
import valet.digikom.com.valetparking.fragments.ParkedCarFragment;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.BitmapToString;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 4/5/2017.
 */

public class ReprintDao implements ProcessRequest {

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

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<PostReprintCheckinResponse> call = apiEndpoint.postReprint(postReprintCheckin);
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

    @Override
    public void process(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<GetReprintCheckinResponse> call = apiEndpoint.getReprintData(1,10,getReprintDataFilterParam());
        //Call<GetReprintCheckinResponse> call = apiEndpoint.getReprintData(getReprintDataFilterParam());
        call.enqueue(new Callback<GetReprintCheckinResponse>() {
            @Override
            public void onResponse(Call<GetReprintCheckinResponse> call, Response<GetReprintCheckinResponse> response) {
                if (response != null && response.body() != null) {
                    Log.d("REPRINT", "DOWNLOAD REPRINT SUCCEED");
                }else {
                    Log.d("REPRINT", "DOWNLOAD REPRINT FAILED");
                }
            }

            @Override
            public void onFailure(Call<GetReprintCheckinResponse> call, Throwable t) {
                Log.d("REPRINT", "DOWNLOAD REPRINT FAILED", t);
            }
        });
    }

    private String getReprintDataFilterParam() {
        TimeZone tz = TimeZone.getTimeZone("GMT+7");
        Calendar cal1 = Calendar.getInstance(tz);
        cal1.set(Calendar.HOUR_OF_DAY, 8);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);

        Calendar cal2 = Calendar.getInstance(tz);
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 59);

        Date date1 = cal1.getTime();
        Date date2 = cal2.getTime();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);

        String dateFrom = df.format(date1);
        String dateTo = df.format(date2);

        return String.format("and(ge(created_at,%s),le(created_at,%s))", dateFrom, dateTo);
        //return "and(ge(created_at,2017-06-08T00:00:00Z),le(created_at,2017-06-08T23:59:59Z))";
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
