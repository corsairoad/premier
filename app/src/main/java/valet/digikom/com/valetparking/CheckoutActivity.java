package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.FineFeeDao;
import valet.digikom.com.valetparking.dao.FinishCheckoutDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;
import valet.digikom.com.valetparking.domain.FineFee;
import valet.digikom.com.valetparking.domain.FinishCheckOut;
import valet.digikom.com.valetparking.domain.PrintCheckout;
import valet.digikom.com.valetparking.util.MakeCurrencyString;

public class CheckoutActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    TextView txtPlatNo;
    TextView txtLokasiParkir;
    TextView txtNoTransaksi;
    TextView txtDropPoint;
    TextView txtCheckinTime;
    TextView txtFee;
    TextView txtRunner;
    TextView txtTotalPay;
    CheckBox cbFineFe;
    CheckBox cbOvernight;
    TextView txtFineFee;
    TextView txtOvernight;
    Button btnCheckout;

    FineFeeDao fineFeeDao;
    FineFee.Fine mLostTicketFine;
    FineFee.Fine mOvernightFine;
    int fee = 0;
    int lostTicketFine = 0;
    int overNightFine = 0;
    int total = 0;
    int idValetHeader;

    private EntryCheckinResponse entryCheckinResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtPlatNo = (TextView) findViewById(R.id.text_plat_no);
        txtLokasiParkir = (TextView) findViewById(R.id.text_lokasi_parkir);
        txtNoTransaksi = (TextView) findViewById(R.id.text_no_transaksi);
        txtDropPoint = (TextView) findViewById(R.id.text_drop_point);
        txtCheckinTime = (TextView) findViewById(R.id.text_chekin_time);
        txtFee = (TextView) findViewById(R.id.text_fee);
        txtRunner = (TextView) findViewById(R.id.text_runner);
        txtTotalPay = (TextView) findViewById(R.id.text_total_pay);
        txtOvernight = (TextView) findViewById(R.id.text_overnight);
        txtFineFee = (TextView) findViewById(R.id.text_lost_ticket);
        cbFineFe = (CheckBox) findViewById(R.id.cb_fine_fee);
        cbOvernight = (CheckBox) findViewById(R.id.cb_stay_overnight);
        btnCheckout = (Button) findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(this);
        cbFineFe.setOnCheckedChangeListener(this);
        cbOvernight.setOnCheckedChangeListener(this);

        fineFeeDao = FineFeeDao.getInstance(this);

        if (getIntent() != null) {
            idValetHeader = getIntent().getIntExtra(EntryCheckoutCont.KEY_ENTRY_CHECKOUT,0);
            if (idValetHeader > 0) {
                new LoadEntryTask().execute(idValetHeader);
                new FetchCheckoutTask().execute(idValetHeader);
                new LoadFineTask().execute();
            }
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == cbFineFe) {
            if (mLostTicketFine != null) {
                if (!compoundButton.isChecked()) {
                    lostTicketFine = 0;
                }else {
                    lostTicketFine = mLostTicketFine.getAttrib().getFee();
                }
                txtFineFee.setText(MakeCurrencyString.fromInt(lostTicketFine));
            }
        }else if(compoundButton == cbOvernight) {
            if (mOvernightFine != null) {
                if (!compoundButton.isChecked()) {
                    overNightFine = 0;
                }else {
                    overNightFine = mOvernightFine.getAttrib().getFee();
                }
               txtOvernight.setText(MakeCurrencyString.fromInt(overNightFine));
            }
        }
        calculateTotal();

    }

    // submit checkout
    @Override
    public void onClick(View view) {
        showConfirmDialog();
    }

    private class FetchCheckoutTask extends AsyncTask<Integer, Void, EntryCheckoutCont.EntryChekout> {

        @Override
        protected EntryCheckoutCont.EntryChekout doInBackground(Integer... integers) {
            return CheckoutDao.getInstance(CheckoutActivity.this, null, null).getEntryCheckoutById(integers[0]);
        }

        @Override
        protected void onPostExecute(EntryCheckoutCont.EntryChekout entryChekout) {
            if (entryChekout != null) {
                init(entryChekout);
            }
        }
    }

    private class LoadEntryTask extends AsyncTask<Integer, Void, EntryCheckinResponse> {

        @Override
        protected EntryCheckinResponse doInBackground(Integer... integers) {
            return EntryDao.getInstance(CheckoutActivity.this).getEntryByIdResponse(integers[0]);
        }

        @Override
        protected void onPostExecute(EntryCheckinResponse response) {
            super.onPostExecute(response);
            initEntry(response);
        }
    }

    private class LoadFineTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mLostTicketFine = fineFeeDao.getLostTickeFine();
            mOvernightFine = fineFeeDao.getOvernightFine();
             return null;
        }
    }

    private void initEntry(EntryCheckinResponse response) {
        if (response != null) {
            this.entryCheckinResponse = response;
            EntryCheckinResponse.Attribute attrib = response.getData().getAttribute();
            fee = attrib.getFee();
            txtDropPoint.setText(attrib.getDropPoint());
            txtFee.setText(MakeCurrencyString.fromInt(fee));
            txtCheckinTime.setText(attrib.getCheckinTime());
            txtOvernight.setText(MakeCurrencyString.fromInt(overNightFine));
            txtFineFee.setText(MakeCurrencyString.fromInt(lostTicketFine));
            calculateTotal();
        } else {
            Toast.makeText(this,"Transaction already checked out.", Toast.LENGTH_SHORT).show();
            goToMain();
        }

    }

    private void init(EntryCheckoutCont.EntryChekout entryChekout) {
        if (entryChekout != null) {
            EntryCheckoutCont.EntryChekout.Attrib attrib = entryChekout.getAttrib();
            String platNo = attrib.getPlatNo();
            String lokasiParkir = attrib.getAreaParkir() + " " + attrib.getBlokParkir() + " " + attrib.getSektorParkir();

            txtPlatNo.setText(platNo);
            txtLokasiParkir.setText(lokasiParkir);
            txtNoTransaksi.setText(attrib.getIdTransaction());
            txtRunner.setText(attrib.getRunnerCheckout());
        }
    }

    private void calculateTotal() {
        total = fee + lostTicketFine + overNightFine;
        txtTotalPay.setText(MakeCurrencyString.fromInt(total));
    }

    private void showConfirmDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Process Checkout")
                .setContentText("Checkout transaction?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        submitCheckout();
                        sweetAlertDialog.setTitleText("success!")
                                .setContentText("Checkout success")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                })
                .showCancelButton(true)
                .show();
    }

    private void submitCheckout() {
        FinishCheckOut.Builder builder = new FinishCheckOut.Builder();

        if (cbOvernight.isChecked()) {
            builder.setOvernightFee(mOvernightFine);
        }
        if (cbFineFe.isChecked()) {
            builder.setLostTicketFee(mLostTicketFine);
        }

        FinishCheckoutDao finishCheckoutDao = FinishCheckoutDao.getInstance(this);
        finishCheckoutDao.setFinishCheckOut(builder.build());
        finishCheckoutDao.setId(idValetHeader);
        TokenDao.getToken(finishCheckoutDao, this);

        new PrintCheckoutTask().execute();

        goToMain();
        //Gson gson = new Gson();
        //String finishCheckOut = gson.toJson(builder.build());
        //Log.d("json checkout", finishCheckOut);
    }

    private void goToMain() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        finish();
    }

    private class PrintCheckoutTask extends  AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String noTrans = entryCheckinResponse.getData().getAttribute().getIdTransaksi();
            String noPol = entryCheckinResponse.getData().getAttribute().getPlatNo();
            String jenis = entryCheckinResponse.getData().getAttribute().getCar();
            String totalBayar = MakeCurrencyString.fromInt(total);
            String feex = MakeCurrencyString.fromInt(fee);
            String lostTicket = MakeCurrencyString.fromInt(lostTicketFine);
            String overnight = MakeCurrencyString.fromInt(overNightFine);
            String warna = entryCheckinResponse.getData().getAttribute().getColor();

            PrintCheckout printCheckout = new PrintCheckout(CheckoutActivity.this,noTrans,noPol,jenis,totalBayar,warna, feex, lostTicket, overnight);
            printCheckout.print();

            return null;
        }
    }
}
