package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.util.ValetDbHelper;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by DIGIKOM-EX4 on 12/20/2016.
 */

public class CheckinDao {

    private ValetDbHelper valetDbHelper;
    private static CheckinDao checkinDao;
    private Context context;

    private CheckinDao(ValetDbHelper valetDbHelper, Context context) {
        this.valetDbHelper = valetDbHelper;
        this.context = context;
    }

    public static CheckinDao getInstance(ValetDbHelper dbHelper, Context context) {
        if (checkinDao == null) {
            checkinDao = new CheckinDao(dbHelper, context);
        }
        return checkinDao;
    }

    public static CheckinDao newInstance(ValetDbHelper dbHelper, Context context) {
        return new CheckinDao(dbHelper, context);
    }

    public void addCheckIn(Checkin checkin, Bitmap bmp) {
        //String imgUrl = saveToInternalStorage(bmp, checkin.getSignatureName().trim());
        String imgUrl = saveImage(bmp, checkin.getSignatureName());
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
        values.put(Checkin.Table.COL_IMAGE_URL, imgUrl + File.separator + checkin.getSignatureName());

        db.insert(Checkin.Table.TABLE_NAME,null,values);

        Toast.makeText(context,"Submit successfull", Toast.LENGTH_SHORT).show();
        Log.d("image Signature URL", imgUrl + File.separator + checkin.getSignatureName());
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

    private String saveToInternalStorage(Bitmap bitmapImage, String fileName){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_WORLD_READABLE);
        String d = directory.getAbsolutePath();
        // Create imageDir
        File mypath=new File(directory,fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public String saveImage(Bitmap bmp, String fileName) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/signature_images");
        myDir.mkdirs();
        File file = new File(myDir, fileName);
        Log.i(TAG, "" + file);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  myDir.getAbsolutePath();
    }
}
