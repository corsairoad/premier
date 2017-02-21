package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.Bank;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 2/21/2017.
 */

public class BankDao implements ProcessRequest {
    private Context context;
    private ValetDbHelper dbHelper;
    private static BankDao bankDao;

    private BankDao(Context context) {
        this.context = context;
        dbHelper = ValetDbHelper.getInstance(context);
    }

    public static BankDao getInstance(Context context) {
        if (bankDao == null) {
            bankDao = new BankDao(context.getApplicationContext());
        }
        return bankDao;
    }

    @Override
    public void process(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<Bank> call = apiEndpoint.getBanks();
        call.enqueue(new Callback<Bank>() {
            @Override
            public void onResponse(Call<Bank> call, Response<Bank> response) {
                if (response != null && response.body() != null) {
                    insertToDb(response.body());
                }
            }

            @Override
            public void onFailure(Call<Bank> call, Throwable t) {

            }
        });
    }

    private void insertToDb(Bank body){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + Bank.Table.TABLE_NAME);
        List<Bank.Data> dataList = body.getDataList();

        for (Bank.Data data : dataList) {
            ContentValues cv = new ContentValues();
            cv.put(Bank.Table.COL_BANK_ID, data.getAttr().getBankId());
            cv.put(Bank.Table.COL_BANK_NAME, data.getAttr().getBankName());
            cv.put(Bank.Table.COL_BANK_DESC, data.getAttr().getBankDesc());

            db.insert(Bank.Table.TABLE_NAME, null, cv);
        }
    }

    public List<Bank.Data> fetchBanks() {
        List<Bank.Data> banks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + Bank.Table.TABLE_NAME, new String[]{});

        if (c.moveToFirst()) {
            do {
                Bank.Data data = new Bank.Data();
                Bank.Data.Attr attr = new Bank.Data.Attr();
                attr.setBankId(c.getInt(c.getColumnIndex(Bank.Table.COL_BANK_ID)));
                attr.setBankName(c.getString(c.getColumnIndex(Bank.Table.COL_BANK_NAME)));
                attr.setBankDesc(c.getString(c.getColumnIndex(Bank.Table.COL_BANK_DESC)));
                data.setAttr(attr);
                data.setId(String.valueOf(attr.getBankId()));

                banks.add(data);
            }while (c.moveToNext());
        }
        return banks;
    }
}
