package valet.digikom.com.valetparking.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.CheckoutActivity;
import valet.digikom.com.valetparking.Main2Activity;
import valet.digikom.com.valetparking.domain.Bank;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.FinishCheckOut;
import valet.digikom.com.valetparking.domain.FinishCheckoutResponse;
import valet.digikom.com.valetparking.domain.MembershipResponse;
import valet.digikom.com.valetparking.domain.PaymentMethod;
import valet.digikom.com.valetparking.domain.PrintCheckout;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by DIGIKOM-EX4 on 1/25/2017.
 */

public class FinishCheckoutDao implements ProcessRequest {

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
    private CheckoutActivity checkoutActivity;
    private PaymentMethod.Data paymentData;
    private Bank.Data bankData;

    private FinishCheckoutDao(Context context) {
        this.context = context;
        dbHelper = ValetDbHelper.getInstance(context.getApplicationContext());
        checkoutActivity = (CheckoutActivity) context;
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
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
        Call<FinishCheckoutResponse> call = apiEndpoint.submitCheckout(getId(),getFinishCheckOut());
        call.enqueue(new Callback<FinishCheckoutResponse>() {
            @Override
            public void onResponse(Call<FinishCheckoutResponse> call, Response<FinishCheckoutResponse> response) {
                if (response != null && response.body() != null) {
                    int id = response.body().getData().getAttrib().getId();
                    checkedOutTime = response.body().getData().getAttrib().getUpdatedAt();
                    setCheckoutCar(id);
                    Toast.makeText(context,"Checkout success", Toast.LENGTH_SHORT).show();
                    //new PrintCheckoutTask().execute();
                    print();
                }else {
                    Toast.makeText(context,"Checkout failed. Voucher number or member id invalid.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FinishCheckoutResponse> call, Throwable t) {
                Toast.makeText(context,"Checkout failed. Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void print() {
        PrintCheckout printCheckout = new PrintCheckout(context,getTotalBayar(), getEntryCheckinResponse(),getFinishCheckOut(),
                getOverNightFine(), getLostTicketFine(),getNomorVoucher(),getDataMembership(), getIdMembership(), checkedOutTime, getPaymentData(), getBankData());
        printCheckout.print();
        goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(context, Main2Activity.class);
        context.startActivity(intent);
        checkoutActivity.finish();

    }

    private void setCheckoutCar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = new String[] {String.valueOf(id)};
        ContentValues cv = new ContentValues();
        cv.put(EntryCheckinResponse.Table.COL_IS_CHECKOUT, 1);
        db.update(EntryCheckinResponse.Table.TABLE_NAME,cv, EntryCheckinResponse.Table.COL_RESPONSE_ID + " =?", args);
    }

    private class PrintCheckoutTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            print();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            goToMain();
        }
    }
}
