package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.domain.AdditionalItemsResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/9/2017.
 */

public class ItemsDao implements ProcessRequest{

    private ValetDbHelper dbHelper;
    private static ItemsDao itemsDao;

    private ItemsDao(ValetDbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public static ItemsDao getInstance (ValetDbHelper dbHelper) {
        if (itemsDao == null) {
            itemsDao = new ItemsDao(dbHelper);
        }
        return itemsDao;
    }

    private void downloadItems(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<AdditionalItemsResponse> call = apiEndpoint.getItems(100);
        call.enqueue(new Callback<AdditionalItemsResponse>() {
            @Override
            public void onResponse(Call<AdditionalItemsResponse> call, Response<AdditionalItemsResponse> response) {
                if (response != null && response.body() != null) {
                    List<AdditionalItems> itemsList = response.body().getData();
                    insertItems(itemsList);
                }
            }

            @Override
            public void onFailure(Call<AdditionalItemsResponse> call, Throwable t) {
                Toast.makeText(dbHelper.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.d("Additional Items", t.getMessage());
            }
        });

    }

    private void insertItems(List<AdditionalItems> itemsList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + AdditionalItems.Table.TABLE_NAME);
        for (AdditionalItems items : itemsList) {
            ContentValues cv = new ContentValues();
            cv.put(AdditionalItems.Table.COL_ID, items.getAttributes().getId());
            cv.put(AdditionalItems.Table.COL_ITEM_NAME, items.getAttributes().getName());

            db.insert(AdditionalItems.Table.TABLE_NAME,null, cv);
        }

    }

    @Override
    public void process(String token) {
        downloadItems(token);
    }

    public List<AdditionalItems> fetchItems() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<AdditionalItems> itemsList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + AdditionalItems.Table.TABLE_NAME,new String[]{});
        if (cursor.moveToFirst()) {
            do {
                AdditionalItems ai = new AdditionalItems();

                AdditionalItems.Attributes attr = new AdditionalItems.Attributes();

                AdditionalItems.AdditionalItemMaster aim = new AdditionalItems.AdditionalItemMaster();
                aim.setId(cursor.getInt(cursor.getColumnIndex(AdditionalItems.Table.COL_ID)));
                aim.setName(cursor.getString(cursor.getColumnIndex(AdditionalItems.Table.COL_ITEM_NAME)));

                //attr.setAdditionalItemMaster(aim);

                ai.setAttributes(aim);

                itemsList.add(ai);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return itemsList;
    }
}
