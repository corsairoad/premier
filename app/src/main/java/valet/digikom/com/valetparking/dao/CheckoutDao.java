package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/23/2017.
 */

public class CheckoutDao implements ProcessRequest {

    private Context context;
    private ValetDbHelper dbHelper;
    private static CheckoutDao checkoutDao;

    private CheckoutDao(Context context) {
        this.context = context;
        this.dbHelper = new ValetDbHelper(context);
    }

    public static CheckoutDao getInstance(Context context) {
        if (checkoutDao == null) {
            checkoutDao = new CheckoutDao(context);
        }
        return checkoutDao;
    }

    private void downloadCheckouts(String token) {
        ApiEndpoint endpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<EntryCheckoutCont> call = endpoint.getCheckouts();
        call.enqueue(new Callback<EntryCheckoutCont>() {
            @Override
            public void onResponse(Call<EntryCheckoutCont> call, Response<EntryCheckoutCont> response) {
                if (response != null && response.body() != null) {
                    EntryCheckoutCont checkoutCont = response.body();
                    insertEntryCheckout(checkoutCont);
                }
            }

            @Override
            public void onFailure(Call<EntryCheckoutCont> call, Throwable t) {

            }
        });
    }

    private void insertEntryCheckout(EntryCheckoutCont checkoutCont) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + EntryCheckoutCont.Table.TABLE_NAME);
        Gson gson = new Gson();
        List<EntryCheckoutCont.EntryChekout> entryChekoutList = checkoutCont.getChekoutList();

        for (EntryCheckoutCont.EntryChekout e : entryChekoutList) {
            String jsonCheckout = gson.toJson(e);
            ContentValues cv = new ContentValues();
            cv.put(EntryCheckoutCont.Table.COL_RESPONSE_ID, String.valueOf(e.getAttrib().getId()));
            cv.put(EntryCheckoutCont.Table.COL_JSON_ENTRY_CHECKOUT, jsonCheckout);

            db.insert(EntryCheckoutCont.Table.TABLE_NAME,null, cv);
        }

    }

    @Override
    public void process(String token) {
        downloadCheckouts(token);
    }
}
