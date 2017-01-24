package valet.digikom.com.valetparking;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;

public class CheckoutActivity extends AppCompatActivity {

    TextView txtPlatNo;
    TextView txtLokasiParkir;
    TextView txtNoTransaksi;
    TextView txtDropPoint;
    TextView txtCheckinTime;
    TextView txtFee;
    TextView txtRunner;
    CheckBox cbFineFe;
    Spinner spFineFee;
    Button btnCheckout;

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
        cbFineFe = (CheckBox) findViewById(R.id.cb_fine_fee);
        spFineFee = (Spinner) findViewById(R.id.spinner_lost_ticket);
        btnCheckout = (Button) findViewById(R.id.btn_checkout);

        if (getIntent() != null) {
            int id = getIntent().getIntExtra(EntryCheckoutCont.KEY_ENTRY_CHECKOUT,0);
            if (id > 0) {
                new FetchCheckoutTask().execute(id);
                new LoadEntryTask().execute(id);
            }
        }

    }

    class FetchCheckoutTask extends AsyncTask<Integer, Void, EntryCheckoutCont.EntryChekout> {

        @Override
        protected EntryCheckoutCont.EntryChekout doInBackground(Integer... integers) {
            return CheckoutDao.getInstance(CheckoutActivity.this, null).getEntryCheckoutById(integers[0]);
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

    private void initEntry(EntryCheckinResponse response) {
        EntryCheckinResponse.Attribute attrib = response.getData().getAttribute();
        txtDropPoint.setText(attrib.getDropPoint());
        txtFee.setText(String.valueOf(attrib.getFee()));
        txtCheckinTime.setText(attrib.getCheckinTime());
    }

    private void init(EntryCheckoutCont.EntryChekout entryChekout) {
        EntryCheckoutCont.EntryChekout.Attrib attrib = entryChekout.getAttrib();
        String platNo = attrib.getPlatNo();
        String lokasiParkir = attrib.getAreaParkir() + " " + attrib.getBlokParkir() + " " + attrib.getSektorParkir();

        txtPlatNo.setText(platNo);
        txtLokasiParkir.setText(lokasiParkir);
        txtNoTransaksi.setText(attrib.getIdTransaction());
        txtRunner.setText(attrib.getRunnerCheckout());
    }

}
