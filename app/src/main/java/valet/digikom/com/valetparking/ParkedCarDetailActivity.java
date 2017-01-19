package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.GridHolder;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.List;

import valet.digikom.com.valetparking.adapter.DropPointAdapter;
import valet.digikom.com.valetparking.dao.CallDao;
import valet.digikom.com.valetparking.dao.DropDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.AddCarCall;
import valet.digikom.com.valetparking.domain.AddCarCallBody;
import valet.digikom.com.valetparking.domain.DropPointMaster;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.fragments.ReviewFragment;
import valet.digikom.com.valetparking.util.ValetDbHelper;

public class ParkedCarDetailActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fab;
    EntryCheckinResponse entry;
    EntryDao entryDao;
    TextView txtPlatNo;
    TextView txtLokasiParkir;
    TextView txtNoTrans;
    TextView txtDropPoint;
    TextView txtCheckin;
    TextView txtFee;
    TextView txtRunner;
    EditText inputDropTo;
    DropPointMaster dropPointMaster;
    ImageButton btnDropPoint;
    ValetDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parked_car_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtPlatNo = (TextView) findViewById(R.id.text_plat_no);
        txtLokasiParkir = (TextView) findViewById(R.id.text_lokasi_parkir);
        txtNoTrans = (TextView) findViewById(R.id.text_no_transaksi);
        txtDropPoint = (TextView) findViewById(R.id.text_drop_point);
        txtCheckin = (TextView) findViewById(R.id.text_chekin_time);
        txtFee = (TextView) findViewById(R.id.text_fee);
        txtRunner = (TextView) findViewById(R.id.text_runner);
        inputDropTo = (EditText) findViewById(R.id.input_drop_point);
        btnDropPoint = (ImageButton) findViewById(R.id.btn_drop);

        btnDropPoint.setOnClickListener(this);
        dbHelper = new ValetDbHelper(this);
        new FetchDropPointTask().execute();
        entryDao = EntryDao.getInstance(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddCarCallBody addCarCallBody = new AddCarCallBody();
                AddCarCall addCarCall = new AddCarCall();
                AddCarCall.Attribute attribute = new AddCarCall.Attribute();
                attribute.setIdDropTo(String.valueOf(dropPointMaster.getAttrib().getDropId()));
                attribute.setIdRunner("1");

                addCarCall.setAttribute(attribute);

                addCarCallBody.setAddCarCall(addCarCall);
                int id = entry.getData().getAttribute().getId();
                CallDao callDao = CallDao.getInstance(id,addCarCallBody, ParkedCarDetailActivity.this);
                Gson gson = new Gson();
                String json = gson.toJson(addCarCallBody);
                TokenDao.getToken(callDao);
                startActivity(new Intent(ParkedCarDetailActivity.this, Main2Activity.class));
                finish();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            int id = getIntent().getIntExtra(EntryCheckinResponse.ID_ENTRY_CHECKIN, 0);
            new LoadEntryTask().execute(id);
        }
    }

    private void init() {
        if (entry != null) {
            EntryCheckinResponse.Attribute attr = entry.getData().getAttribute();
            String platNo = attr.getPlatNo() + " - " + attr.getCar() + " (" + attr.getColor() + ")";
            String lokasiParkir = attr.getAreaParkir() + " " + attr.getBlokParkir() + " " + attr.getSektorParkir();

            txtPlatNo.setText(platNo);
            txtLokasiParkir.setText(lokasiParkir);
            txtNoTrans.setText(attr.getIdTransaksi());
            txtDropPoint.setText(attr.getDropPoint());
            txtCheckin.setText(attr.getCheckinTime());
            txtRunner.setText(attr.getNamaRunner());
            txtFee.setText("Rp. 80.000");
        }
    }

    @Override
    public void onClick(View view) {

    }

    private class LoadEntryTask extends AsyncTask<Integer, Void, EntryCheckinResponse> {

        @Override
        protected EntryCheckinResponse doInBackground(Integer... integers) {
            return entryDao.getEntryByIdResponse(integers[0]);
        }

        @Override
        protected void onPostExecute(EntryCheckinResponse response) {
            super.onPostExecute(response);
            entry = response;
            init();
        }
    }

    private class FetchDropPointTask extends AsyncTask<Void, Void, List<DropPointMaster>> {

        @Override
        protected List<DropPointMaster> doInBackground(Void... voids) {
            return DropDao.getInstance(dbHelper).fetchAllDropPoints();
        }

        @Override
        protected void onPostExecute(List<DropPointMaster> dropPointMasters) {
            if (!dropPointMasters.isEmpty()){
                final DropPointAdapter adapter = new DropPointAdapter(ParkedCarDetailActivity.this, dropPointMasters);
                btnDropPoint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogPlus dialogPlus  = DialogPlus.newDialog(ParkedCarDetailActivity.this)
                                .setContentHolder(new GridHolder(2))
                                .setAdapter(adapter)
                                .setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                        DropPointMaster dropPoint = (DropPointMaster) item;
                                        inputDropTo.setText(dropPoint.getAttrib().getDropName());
                                        dropPointMaster = dropPoint;
                                        dialog.dismiss();
                                    }
                                })
                                .setGravity(Gravity.CENTER)
                                .setExpanded(false)
                                .create();
                        dialogPlus.show();
                    }
                });
            }
        }
    }

}
