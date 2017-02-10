package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.adapter.SpinnerMembershipAdapter;
import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.FineFeeDao;
import valet.digikom.com.valetparking.dao.FinishCheckoutDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;
import valet.digikom.com.valetparking.domain.FineFee;
import valet.digikom.com.valetparking.domain.FinishCheckOut;
import valet.digikom.com.valetparking.domain.MembershipResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.MakeCurrencyString;

public class CheckoutActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    TextView txtPlatNo;
    TextView txtLokasiParkir;
    TextView txtNoTransaksi;
    TextView txtDropPoint;
    TextView txtCheckinTime;
    TextView txtFee;
    TextView txtRunner;
    TextView txtTotalPay;
    TextView txtValetType;
    CheckBox cbFineFe;
    CheckBox cbOvernight;
    CheckBox cbVoucher;
    CheckBox cbMembership;
    Spinner spMembership;
    EditText inputVoucher;
    EditText inputMembershipId;
    TextView txtFineFee;
    TextView txtOvernight;
    Button btnCheckout;
    CircleImageView imgCar;
    FineFeeDao fineFeeDao;
    FineFee.Fine mLostTicketFine;
    FineFee.Fine mOvernightFine;
    int fee = 0;
    int lostTicketFine = 0;
    int overNightFine = 0;
    int total = 0;
    int feeMembership = 0;
    int idValetHeader;

    SpinnerMembershipAdapter membershipAdapter;
    List<MembershipResponse.Data> listMemberShip = new ArrayList<>();
    MembershipResponse.Data dataMembership;

    EntryCheckinResponse entryCheckinResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null) {
            idValetHeader = getIntent().getIntExtra(EntryCheckoutCont.KEY_ENTRY_CHECKOUT,0);
            if (idValetHeader > 0) {
                new LoadEntryTask().execute(idValetHeader);
                new FetchCheckoutTask().execute(idValetHeader);
                new LoadFineTask().execute();
            }
        }

        txtPlatNo = (TextView) findViewById(R.id.text_plat_no);
        txtLokasiParkir = (TextView) findViewById(R.id.text_lokasi_parkir);
        txtNoTransaksi = (TextView) findViewById(R.id.text_no_transaksi);
        txtDropPoint = (TextView) findViewById(R.id.text_drop_point);
        txtCheckinTime = (TextView) findViewById(R.id.text_chekin_time);
        txtFee = (TextView) findViewById(R.id.text_fee);
        txtValetType = (TextView) findViewById(R.id.text_valet_type);
        txtRunner = (TextView) findViewById(R.id.text_runner);
        txtTotalPay = (TextView) findViewById(R.id.text_total_pay);
        txtOvernight = (TextView) findViewById(R.id.text_overnight);
        txtFineFee = (TextView) findViewById(R.id.text_lost_ticket);
        cbFineFe = (CheckBox) findViewById(R.id.cb_fine_fee);
        cbOvernight = (CheckBox) findViewById(R.id.cb_stay_overnight);
        btnCheckout = (Button) findViewById(R.id.btn_checkout);
        cbVoucher = (CheckBox) findViewById(R.id.cb_voucher);
        inputVoucher = (EditText) findViewById(R.id.input_voucher);
        inputMembershipId = (EditText) findViewById(R.id.input_membership_id);
        cbMembership = (CheckBox) findViewById(R.id.cb_membership);
        spMembership = (Spinner) findViewById(R.id.spinner_memebership);
        imgCar = (CircleImageView) findViewById(R.id.img_car);

        spMembership.setOnItemSelectedListener(this);
        btnCheckout.setOnClickListener(this);
        cbFineFe.setOnCheckedChangeListener(this);
        cbOvernight.setOnCheckedChangeListener(this);
        cbVoucher.setOnCheckedChangeListener(this);
        cbMembership.setOnCheckedChangeListener(this);

        fineFeeDao = FineFeeDao.getInstance(this);

        membershipAdapter = new SpinnerMembershipAdapter(this, R.layout.laoyut_spinner_membership,R.id.text_membership, listMemberShip);
        membershipAdapter.setDropDownViewResource(R.layout.text_item_membership);
        spMembership.setAdapter(membershipAdapter);
        setupMembersip();
    }

    private void setupMembersip() {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
               ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<MembershipResponse> call = apiEndpoint.getMemberships();
                call.enqueue(new Callback<MembershipResponse>() {
                    @Override
                    public void onResponse(Call<MembershipResponse> call, Response<MembershipResponse> response) {
                        if (response != null && response.body() != null) {
                            MembershipResponse membershipResponse = response.body();
                            List<MembershipResponse.Data> dataList = membershipResponse.getDataList();
                            listMemberShip.clear();
                            listMemberShip.addAll(dataList);
                            membershipAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<MembershipResponse> call, Throwable t) {

                    }
                });
            }
        }, this);
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
        } else if (compoundButton == cbVoucher) {
            if (!b) {
                inputVoucher.setVisibility(View.INVISIBLE);
            }else {
                inputVoucher.setVisibility(View.VISIBLE);
            }
        } else if (compoundButton == cbMembership) {
            if (!b) {
                spMembership.setVisibility(View.INVISIBLE);
                inputMembershipId.setVisibility(View.GONE);
                dataMembership = null;
                feeMembership = 0;
            }else {
                dataMembership = membershipAdapter.getItem(0);
                feeMembership = Integer.valueOf(dataMembership.getAttr().getPrice());
                spMembership.setVisibility(View.VISIBLE);
                inputMembershipId.setVisibility(View.VISIBLE);
            }
        }

        calculateTotal();

    }

    // submit checkout
    @Override
    public void onClick(View view) {
        showConfirmDialog();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (cbMembership.isChecked()) {
            dataMembership = membershipAdapter.getItem(i);
            feeMembership = Integer.valueOf(dataMembership.getAttr().getPrice());
            calculateTotal();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            String platNo = attrib.getPlatNo();
            String lokasiParkir = attrib.getAreaParkir() + " " + attrib.getBlokParkir() + " " + attrib.getSektorParkir();
            String logoMobil = response.getData().getAttribute().getLogoMobil();

            Glide.with(this)
                    .load(logoMobil)
                    .centerCrop()
                    .placeholder(R.drawable.car_icon)
                    .crossFade()
                    .into(imgCar);

            txtValetType.setText(attrib.getValetType());
            txtPlatNo.setText(platNo);
            txtLokasiParkir.setText(lokasiParkir);
            txtDropPoint.setText(attrib.getDropPoint());
            txtNoTransaksi.setText(attrib.getIdTransaksi());
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

            //txtPlatNo.setText(platNo);
            //txtLokasiParkir.setText(lokasiParkir);
            //txtNoTransaksi.setText(attrib.getIdTransaction());
            txtRunner.setText(attrib.getRunnerCheckout());
        }
    }

    private void calculateTotal() {
        total = fee + lostTicketFine + overNightFine + feeMembership;
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

        if (cbMembership.isChecked()) {
            builder.setMembership(dataMembership);
        }

        if (cbVoucher.isChecked()) {
            String noVoucher = inputVoucher.getText().toString();
            if (TextUtils.isEmpty(noVoucher)) {
                inputVoucher.setError("input voucher cant'be empty");
                return;
            }
            builder.setVoucher(inputVoucher.getText().toString());
        }

        FinishCheckoutDao finishCheckoutDao = FinishCheckoutDao.getInstance(this);
        finishCheckoutDao.setFinishCheckOut(builder.build());
        finishCheckoutDao.setId(idValetHeader);
        finishCheckoutDao.setEntryCheckinResponse(entryCheckinResponse);
        finishCheckoutDao.setTotalBayar(txtTotalPay.getText().toString());
        finishCheckoutDao.setLostTicketFine(lostTicketFine);
        finishCheckoutDao.setOverNightFine(overNightFine);
        finishCheckoutDao.setNomorVoucher(inputVoucher.getText().toString());
        if (dataMembership != null) {
            dataMembership.getAttr().setToken(inputMembershipId.getText().toString());
        }
        finishCheckoutDao.setDataMembership(dataMembership);
        finishCheckoutDao.setIdMembership(inputMembershipId.getText().toString());

        TokenDao.getToken(finishCheckoutDao, this);

        //Gson gson = new Gson();
        //String finishCheckOut = gson.toJson(builder.build());
        //Log.d("json checkout", finishCheckOut);

        //goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra("refresh", 1);
        startActivity(intent);
        finish();
    }
}
