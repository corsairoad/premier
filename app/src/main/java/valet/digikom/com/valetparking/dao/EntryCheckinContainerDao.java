package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 2/28/2017.
 */

public class EntryCheckinContainerDao {
    private Context context;
    private ValetDbHelper dbHelper;
    private static EntryCheckinContainerDao containerDao;
    private Gson gson;

    private EntryCheckinContainerDao(Context context) {
        this.context = context;
        this.dbHelper = ValetDbHelper.getInstance(context.getApplicationContext());
        gson = new Gson();
    }

    public static EntryCheckinContainerDao getInstance(Context context) {
        if (containerDao == null) {
            containerDao = new EntryCheckinContainerDao(context);
        }
        return containerDao;
    }

    public long insert(EntryCheckinContainer checkinContainer) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String json = gson.toJson(checkinContainer);
        String id = checkinContainer.getEntryCheckin().getId();

        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinContainer.Table.COL_FAKE_VTHD_ID, id);
        cv.put(EntryCheckinContainer.Table.COL_JSON_DATA, json);

        return db.insert(EntryCheckinContainer.Table.TABLE_NAME, null, cv);
    }

    public void deleteCheckinDataByTicketNo(String ticketNo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + EntryCheckinContainer.Table.TABLE_NAME, new String[]{});
        if (c.moveToFirst()) {
            do {
                String json = c.getString(c.getColumnIndex(EntryCheckinContainer.Table.COL_JSON_DATA));
                int rowId = c.getInt(c.getColumnIndex(EntryCheckinContainer.Table.COL_ID));
                EntryCheckinContainer container = gson.fromJson(json, EntryCheckinContainer.class);
                if (ticketNo.trim().equals(container.getEntryCheckin().getAttrib().getTicketNo().trim())) {
                    deleteRowByRowId(rowId);
                    return;
                }
            }while (c.moveToNext());
        }
        c.close();
    }

    private void deleteRowByRowId(int rowId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String condition = EntryCheckinContainer.Table.COL_ID + " = ?";
        String[] args = new String[]{String.valueOf(rowId)};

        db.delete(EntryCheckinContainer.Table.TABLE_NAME,condition,args);
    }

    public List<EntryCheckinContainer> fetchAll() {
        List<EntryCheckinContainer> containers = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + EntryCheckinContainer.Table.TABLE_NAME, new String[]{});
        if (c.moveToFirst()) {
            do {
                String json = c.getString(c.getColumnIndex(EntryCheckinContainer.Table.COL_JSON_DATA));
                EntryCheckinContainer container = gson.fromJson(json, EntryCheckinContainer.class);

                containers.add(container);
            }while (c.moveToNext());
        }
        c.close();
        return containers;
    }

    public int deleteById(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {id};

        return db.delete(EntryCheckinContainer.Table.TABLE_NAME,EntryCheckinContainer.Table.COL_FAKE_VTHD_ID + " = ?", args);
    }
}
