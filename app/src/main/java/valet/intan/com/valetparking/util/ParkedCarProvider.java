package valet.intan.com.valetparking.util;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import valet.intan.com.valetparking.dao.EntryDao;
import valet.intan.com.valetparking.domain.EntryCheckinResponse;

/**
 * Created by DIGIKOM-EX4 on 1/30/2017.
 */

public class ParkedCarProvider extends ContentProvider {
    private static final String AUTHORITY = "com.valet.premiere";
    private ValetDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = ValetDbHelper.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            // the entered text can be found in selectionArgs[0]
            // return a cursor with appropriate data
        String sqry = uri.getLastPathSegment();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        Map<String, String> projectionMap = new HashMap<>();
        projectionMap.put(EntryCheckinResponse.Table.COL_ID, EntryCheckinResponse.Table.COL_ID);
        projectionMap.put(EntryCheckinResponse.Table.COL_PLAT_NO, EntryCheckinResponse.Table.COL_PLAT_NO + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        projectionMap.put(EntryCheckinResponse.Table.COL_RESPONSE_ID, EntryCheckinResponse.Table.COL_RESPONSE_ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);

        builder.setProjectionMap(projectionMap);
        builder.appendWhere(EntryCheckinResponse.Table.COL_IS_CHECKOUT + "= 0 AND " +SearchManager.SUGGEST_COLUMN_TEXT_1 + " LIKE '%" + sqry + "%'");
        builder.setTables(EntryCheckinResponse.Table.TABLE_NAME);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);

        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private Cursor getAllParkedcars() {
        EntryDao entryDao = EntryDao.getInstance(getContext());
        return entryDao.getAllParkedCars();
    }

    private Cursor getParkedCarsByPlatNo(String platNo) {
        EntryDao entryDao = EntryDao.getInstance(getContext());
        return entryDao.getParkedCarsByPlatNo(platNo);
    }
}
