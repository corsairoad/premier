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
import valet.digikom.com.valetparking.domain.PaymentMethod;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 2/21/2017.
 */

public class PaymentDao implements ProcessRequest {

    private Context context;
    private static PaymentDao paymentDao;
    private ValetDbHelper dbHelper;

    private PaymentDao(Context context) {
        this.context = context;
        dbHelper = ValetDbHelper.getInstance(context);
    }

    public static PaymentDao getInstance(Context context) {
        if (paymentDao == null) {
            paymentDao = new PaymentDao(context.getApplicationContext());
        }
        return paymentDao;
    }

    @Override
    public void process(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<PaymentMethod> call = apiEndpoint.getPaymentMethods();
        call.enqueue(new Callback<PaymentMethod>() {
            @Override
            public void onResponse(Call<PaymentMethod> call, Response<PaymentMethod> response) {
                if (response != null && response.body() != null) {
                    insertToDB(response.body());
                }
            }

            @Override
            public void onFailure(Call<PaymentMethod> call, Throwable t) {

            }
        });
    }

    private void insertToDB(PaymentMethod paymentMethod) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<PaymentMethod.Data> listData = paymentMethod.getDataList();
        db.execSQL("DELETE FROM " + PaymentMethod.Table.TABLE_NAME);
        for (PaymentMethod.Data data : listData) {
            ContentValues cv = new ContentValues();
            PaymentMethod.Data.Attr attr = data.getAttr();
            cv.put(PaymentMethod.Table.COL_PYMNT_ID, attr.getPaymentId());
            cv.put(PaymentMethod.Table.COL_PYMNT_NAME, attr.getPaymentName());
            cv.put(PaymentMethod.Table.COL_PYMNT_DESC, attr.getPaymentDesc());
            cv.put(PaymentMethod.Table.COL_PYMNT_FIELD_POST, attr.getPaymentFieldPost());
            cv.put(PaymentMethod.Table.COL_PYMNT_CATEGORY_ID, attr.getPaymentCategoryId());

            db.insert(PaymentMethod.Table.TABLE_NAME, null, cv);
        }
    }

    public List<PaymentMethod.Data> fetchPaymentMethods() {
        List<PaymentMethod.Data> listPayment = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + PaymentMethod.Table.TABLE_NAME + " ORDER BY " + PaymentMethod.Table.COL_PYMNT_ID, new String[]{});
        if (c.moveToFirst()) {
            do {
                PaymentMethod.Data data = new PaymentMethod.Data();
                PaymentMethod.Data.Attr attr = new PaymentMethod.Data.Attr();
                attr.setPaymentId(c.getInt(c.getColumnIndex(PaymentMethod.Table.COL_PYMNT_ID)));
                attr.setPaymentName(c.getString(c.getColumnIndex(PaymentMethod.Table.COL_PYMNT_NAME)));
                attr.setPaymentDesc(c.getString(c.getColumnIndex(PaymentMethod.Table.COL_PYMNT_DESC)));
                attr.setPaymentFieldPost(c.getString(c.getColumnIndex(PaymentMethod.Table.COL_PYMNT_FIELD_POST)));
                attr.setPaymentCategoryId(c.getInt(c.getColumnIndex(PaymentMethod.Table.COL_PYMNT_CATEGORY_ID)));
                data.setAttr(attr);
                listPayment.add(data);

            }while (c.moveToNext());
        }
        c.close();
        return listPayment;
    }
}
