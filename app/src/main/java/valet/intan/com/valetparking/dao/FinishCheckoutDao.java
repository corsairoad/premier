package valet.intan.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.eposprint.EposException;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.CheckoutActivity;
import valet.intan.com.valetparking.Main2Activity;
import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.domain.Bank;
import valet.intan.com.valetparking.domain.CheckoutData;
import valet.intan.com.valetparking.domain.EntryCheckinResponse;
import valet.intan.com.valetparking.domain.FinishCheckOut;
import valet.intan.com.valetparking.domain.FinishCheckoutResponse;
import valet.intan.com.valetparking.domain.MembershipResponse;
import valet.intan.com.valetparking.domain.PaymentMethod;
import valet.intan.com.valetparking.domain.PrintCheckoutParam;
import valet.intan.com.valetparking.domain.PrintReceipt;
import valet.intan.com.valetparking.domain.PrintReceiptCheckout;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class FinishCheckoutDao implements ProcessRequest {

    public static final String PRINT_CHECOUT_SUCCEED = "oke";
    public static final String PRINT_CHECKOUT_FAILED = "failed";

    private Context context;
    private ValetDbHelper dbHelper;
    private static FinishCheckoutDao finishCheckoutDao;
    private int id;
    private FinishCheckOut finishCheckOut;
    private EntryCheckinResponse entryCheckinResponse;
    private String totalBayar;
    private int overNightFine;
    private int lostTicketFine;
    private String nomorVoucher;
    private String idMembership;
    private MembershipResponse.Data dataMembership;
    private String checkedOutTime;
    private PaymentMethod.Data paymentData;
    private Bank.Data bankData;

    public static final int STATUS_SYNCED = 1;
    public static final int STATUS_PENDING = 0;
    public static final int FAKE_UPDATED_TO_REMOTE_ID = 1;

    private FinishCheckoutDao(Context context) {
        this.context = context;
        dbHelper = ValetDbHelper.getInstance(context);
    }

    public static FinishCheckoutDao getInstance(Context c) {
        if (finishCheckoutDao == null) {
            finishCheckoutDao = new FinishCheckoutDao(c);
        }
        return finishCheckoutDao;
    }

    public int getOverNightFine() {
        return overNightFine;
    }

    public String getIdMembership() {
        return idMembership;
    }

    public Bank.Data getBankData() {
        return bankData;
    }

    public String getCheckedOutTime() {
        return checkedOutTime;
    }

    public void setBankData(Bank.Data bankData) {
        this.bankData = bankData;
    }

    public void setIdMembership(String idMembership) {
        this.idMembership = idMembership;
    }

    public void setOverNightFine(int overNightFine) {
        this.overNightFine = overNightFine;
    }

    public int getLostTicketFine() {
        return lostTicketFine;
    }

    public void setLostTicketFine(int lostTicketFine) {
        this.lostTicketFine = lostTicketFine;
    }

    public String getNomorVoucher() {
        return nomorVoucher;
    }

    public void setNomorVoucher(String nomorVoucher) {
        this.nomorVoucher = nomorVoucher;
    }

    public MembershipResponse.Data getDataMembership() {
        return dataMembership;
    }

    public void setDataMembership(MembershipResponse.Data dataMembership) {
        this.dataMembership = dataMembership;
    }

    public String getTotalBayar() {
        return totalBayar;
    }

    public void setTotalBayar(String totalBayar) {
        this.totalBayar = totalBayar;
    }

    public void setCheckedOutTime(String checkedOutTime) {
        this.checkedOutTime = checkedOutTime;
    }

    public EntryCheckinResponse getEntryCheckinResponse() {
        return entryCheckinResponse;
    }

    public void setEntryCheckinResponse(EntryCheckinResponse entryCheckinResponse) {
        this.entryCheckinResponse = entryCheckinResponse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PaymentMethod.Data getPaymentData() {
        return paymentData;
    }

    public void setPaymentData(PaymentMethod.Data paymentData) {
        this.paymentData = paymentData;
    }

    public FinishCheckOut getFinishCheckOut() {
        return finishCheckOut;
    }

    public void setFinishCheckOut(FinishCheckOut finishCheckOut) {
        this.finishCheckOut = finishCheckOut;
    }

    @Override
    public void process(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
        Call<FinishCheckoutResponse> call = apiEndpoint.submitCheckout(getId(),getFinishCheckOut(), token);
        call.enqueue(new Callback<FinishCheckoutResponse>() {
            @Override
            public void onResponse(Call<FinishCheckoutResponse> call, Response<FinishCheckoutResponse> response) {
                if (response != null && response.body() != null) {
                    int id = response.body().getData().getAttrib().getId();
                    checkedOutTime = response.body().getData().getAttrib().getUpdatedAt();
                    setCheckoutCar(id);
                    Toast.makeText(context,"Checkout success", Toast.LENGTH_SHORT).show();
                    //new PrintCheckoutTask().execute();
                    print(id);
                }else {
                    Toast.makeText(context,"Checkout failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FinishCheckoutResponse> call, Throwable t) {
                Toast.makeText(context,"Checkout failed. Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String print(final int remoteId) {
        try {
            // create print checkout param
            String ticketSeq = EntryDao.getInstance(context).getTicketSequence(remoteId);
            PrintCheckoutParam.Builder paramBuilder = new PrintCheckoutParam.Builder()
                    .setTotalBayar(getTotalBayar())
                    .setEntryCheckinResponse(getEntryCheckinResponse())
                    .setTotalCheckin(ticketSeq)
                    .setFinishCheckout(getFinishCheckOut())
                    .setOvernigthFine(getOverNightFine())
                    .setLostTicketFine(getLostTicketFine())
                    .setNovoucher(getNomorVoucher())
                    .setDataMembership(getDataMembership())
                    .setIdMembership(getIdMembership())
                    .setCheckoutTime(getCheckedOutTime())
                    .setPaymentData(getPaymentData())
                    .setBankData(getBankData());

            // print data
            PrintReceiptCheckout printReceiptCheckout = new PrintReceiptCheckout(context,paramBuilder.build());
            printReceiptCheckout.buildPrintData();
            ReprintDao.getInstance(context).removeReprintData(paramBuilder.build().getEntryCheckinResponse().getData().getAttribute().getNoTiket());
            return PRINT_CHECOUT_SUCCEED;
        } catch (final EposException e) {
            e.printStackTrace();
            sendBroadcastErrorPrint(e.getErrorStatus());
        }
        return PRINT_CHECKOUT_FAILED;
        //goToMain();
        /*
        ---------- cara print lama -------------------
        ----------------------------------------------
        PrintCheckout printCheckout = new PrintCheckout(context,getTotalBayar(), getEntryCheckinResponse(),getFinishCheckOut(),
                getOverNightFine(), getLostTicketFine(),getNomorVoucher(),getDataMembership(), getIdMembership(),
                checkedOutTime, getPaymentData(), getBankData());
        printCheckout.print();
      */
    }

    private void goToMain() {
        Intent intent = new Intent(context, Main2Activity.class);
        context.startActivity(intent);
        //checkoutActivity.finish();
    }

    public void setCheckoutCar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinResponse.Table.COL_IS_CHECKOUT, 1);
        int udpatSukes = db.update(EntryCheckinResponse.Table.TABLE_NAME,cv, EntryCheckinResponse.Table.COL_RESPONSE_ID + " = ?", args);
        Log.d("Update to checkout", "" + udpatSukes);

    }

    public long saveDataCheckout(int remoteVthdId, FinishCheckOut finishCheckOut, String noTiket) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String json = toJson(finishCheckOut);
        ContentValues cv = new ContentValues();

        cv.put(FinishCheckOut.Table.COL_JSON_DATA, json);
        cv.put(FinishCheckOut.Table.COL_DATA_ID, remoteVthdId);
        cv.put(FinishCheckOut.Table.COL_NO_TIKET, noTiket);

        return db.insert(FinishCheckOut.Table.TABLE_NAME, null, cv);
    }

    public List<CheckoutData> getCheckoutData() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<CheckoutData> checkoutDataList = new ArrayList<>();
        String whereClause = FinishCheckOut.Table.COL_STATUS + " = ? ";
        String[] args = new String[]{String.valueOf(STATUS_PENDING)};

        //Cursor c = db.rawQuery("SELECT * FROM " + FinishCheckOut.Table.TABLE_NAME, new String[] {});
        Cursor c = db.query(FinishCheckOut.Table.TABLE_NAME,null,whereClause,args,null,null,null);

        if (c.moveToFirst()) {
            do{
                CheckoutData checkoutData = new CheckoutData();
                checkoutData.setRemoteVthdId(c.getInt(c.getColumnIndex(FinishCheckOut.Table.COL_DATA_ID)));
                checkoutData.setJsonData(c.getString(c.getColumnIndex(FinishCheckOut.Table.COL_JSON_DATA)));
                checkoutData.setNoTiket(c.getString(c.getColumnIndex(FinishCheckOut.Table.COL_NO_TIKET)));

                checkoutDataList.add(checkoutData);
            } while (c.moveToNext());
        }
        c.close();
        return checkoutDataList;
    }


    public int updateCheckoutVthdId(String noTiket, int remoteVthdId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = FinishCheckOut.Table.COL_NO_TIKET + " = ?";
        String[] args = new String[] {noTiket};

        ContentValues cv = new ContentValues();
        cv.put(FinishCheckOut.Table.COL_DATA_ID, remoteVthdId);
        cv.put(FinishCheckOut.Table.COL_ID_STILL_FAKE, FAKE_UPDATED_TO_REMOTE_ID);

        return db.update(FinishCheckOut.Table.TABLE_NAME,cv,whereClause,args);
    }

    // Invoked in DownloadCurrentLobbyService
    public void updateCheckoutVthdId(List<EntryCheckinResponse.Data> downloadedCheckinList) {
        if(downloadedCheckinList != null &&!downloadedCheckinList.isEmpty()) {
            for (EntryCheckinResponse.Data e : downloadedCheckinList) {
                String noTiket = e.getAttribute().getNoTiket().trim();
                int remoteVthdId = e.getAttribute().getId();
                if (isCheckoutPending(noTiket)) {
                    int row = updateCheckoutVthdId(noTiket, remoteVthdId);
                    if (row > 0) {
                        updateColIsIdStillFake(noTiket, FAKE_UPDATED_TO_REMOTE_ID);
                    }

                }
            }
        }
    }

    public int deleteDatabyRemoteId(int remoteVthdId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClaue = FinishCheckOut.Table.COL_DATA_ID + " = ?";
        String[] args = new String[] {String.valueOf(remoteVthdId)};

        return db.delete(FinishCheckOut.Table.TABLE_NAME, whereClaue,args);
    }

    public int updateStatus(int dataId, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = FinishCheckOut.Table.COL_DATA_ID + " = ?";
        String[] args = new String[] {String.valueOf(dataId)};

        ContentValues cv = new ContentValues();
        cv.put(FinishCheckOut.Table.COL_STATUS, status);

        return db.update(FinishCheckOut.Table.TABLE_NAME,cv, whereClause, args);
    }

    public int removeAllSyncedCheckout() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = FinishCheckOut.Table.COL_STATUS + " = ?";
        String[] args = new String[] {String.valueOf(STATUS_SYNCED)};

        return db.delete(FinishCheckOut.Table.TABLE_NAME, where, args);
    }

    public boolean isAlreadyCheckout(String noTiket) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = FinishCheckOut.Table.COL_NO_TIKET + " = ?";

        Cursor c = db.query(FinishCheckOut.Table.TABLE_NAME,null,whereClause,new String[]{noTiket},null,null,null);
        return c.moveToFirst();
    }

    public boolean isCheckoutPending(String noTiket) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = FinishCheckOut.Table.COL_NO_TIKET + " = ? AND " +
                FinishCheckOut.Table.COL_STATUS + " = " + STATUS_PENDING +
                " AND " + FinishCheckOut.Table.COL_ID_STILL_FAKE + " = 0";

        Cursor c = db.query(FinishCheckOut.Table.TABLE_NAME, null, whereClause, new String[]{noTiket},null,null,null);

        return c.moveToFirst();

    }

    private void updateColIsIdStillFake (String noTicket, int value) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String where = FinishCheckOut.Table.COL_NO_TIKET + " = ?";

        ContentValues values = new ContentValues();
        values.put(FinishCheckOut.Table.COL_ID_STILL_FAKE, value);

        db.update(FinishCheckOut.Table.TABLE_NAME, values, where , new String[]{noTicket});

    }

    private String toJson(FinishCheckOut dataCheckout) {
        Gson gson = new Gson();
        String finishCheckOut = gson.toJson(dataCheckout);
        Log.d("json checkout", finishCheckOut);
        return finishCheckOut;
    }




    private void sendBroadcastErrorPrint(int statusPrint) {
        Intent intent = new Intent();
        intent.putExtra(CheckoutActivity.EXTRA_STATUS_PRINT, statusPrint);
        intent.setAction(CheckoutActivity.ACTION_ERROR_PRINT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
