package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.adapter.SpinnerBankAdapter;
import valet.digikom.com.valetparking.adapter.SpinnerMembershipAdapter;
import valet.digikom.com.valetparking.adapter.SpinnerPaymentAdapter;
import valet.digikom.com.valetparking.dao.BankDao;
import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.FineFeeDao;
import valet.digikom.com.valetparking.dao.FinishCheckoutDao;
import valet.digikom.com.valetparking.dao.PaymentDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.Bank;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;
import valet.digikom.com.valetparking.domain.FineFee;
import valet.digikom.com.valetparking.domain.FinishCheckOut;
import valet.digikom.com.valetparking.domain.MembershipResponse;
import valet.digikom.com.valetparking.domain.PaymentMethod;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.MakeCurrencyString;
import valet.digikom.com.valetparking.util.PrefManager;

public class CheckoutActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG_MEMBERSHIP = "mem";
    private static final String TAG_PAYMENT = "pymnt";

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
    Spinner spPaymentS;
    Spinner spBank;
    EditText inputPaymentToken;
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
    int remoteValetHeader;

    List<MembershipResponse.Data> listMemberShip = new ArrayList<>();
    List<PaymentMethod.Data> listPayment = new ArrayList<>();
    List<Bank.Data> bankList = new ArrayList<>();
    PaymentMethod.Data paymentData;
    Bank.Data bankData;

    SpinnerMembershipAdapter membershipAdapter;
    SpinnerPaymentAdapter spinnerPaymentAdapter;
    SpinnerBankAdapter spinnerBankAdapter;
    MembershipResponse.Data dataMembership;
    EntryCheckinResponse entryCheckinResponse;

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
        inputPaymentToken = (EditText) findViewById(R.id.input_payment_token);
        cbMembership = (CheckBox) findViewById(R.id.cb_membership);
        spMembership = (Spinner) findViewById(R.id.spinner_memebership);
        spMembership.setTag(TAG_MEMBERSHIP);
        spPaymentS = (Spinner) findViewById(R.id.sp_payment_methods);
        spPaymentS.setTag(TAG_PAYMENT);
        spBank = (Spinner) findViewById(R.id.sp_bank);
        imgCar = (CircleImageView) findViewById(R.id.img_car);

        if (getIntent() != null) {
            idValetHeader = getIntent().getIntExtra(EntryCheckoutCont.KEY_ENTRY_CHECKOUT,0);
            setRemoteValetHeader(idValetHeader);
            new LoadEntryTask().execute(idValetHeader);
            new FetchCheckoutTask().execute(idValetHeader);
            new LoadFineTask().execute();
            new LoadPaymentTask().execute();
            new LoadBanksTask().execute();
        }

        spMembership.setOnItemSelectedListener(this);
        spPaymentS.setOnItemSelectedListener(this);
        spBank.setOnItemSelectedListener(this);

        btnCheckout.setOnClickListener(this);
        cbFineFe.setOnCheckedChangeListener(this);
        cbOvernight.setOnCheckedChangeListener(this);
        cbVoucher.setOnCheckedChangeListener(this);
        cbMembership.setOnCheckedChangeListener(this);

        fineFeeDao = FineFeeDao.getInstance(this);

        membershipAdapter = new SpinnerMembershipAdapter(this, R.layout.laoyut_spinner_membership,R.id.text_membership, listMemberShip);
        membershipAdapter.setDropDownViewResource(R.layout.text_item_membership);
        spMembership.setAdapter(membershipAdapter);
        //setupMembersip();

        spinnerPaymentAdapter = new SpinnerPaymentAdapter(this, listPayment);
        spPaymentS.setAdapter(spinnerPaymentAdapter);

        spinnerBankAdapter = new SpinnerBankAdapter(this, bankList);
        spBank.setAdapter(spinnerBankAdapter);
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
        Spinner spinner  = (Spinner) adapterView;
        if (spinner.getId() == R.id.spinner_memebership) {
            if (cbMembership.isChecked()) {
                dataMembership = membershipAdapter.getItem(i);
                feeMembership = Integer.valueOf(dataMembership.getAttr().getPrice());
                calculateTotal();
            }
        }else if(spinner.getId() == R.id.sp_payment_methods) {
            PaymentMethod.Data data = (PaymentMethod.Data) spinnerPaymentAdapter.getItem(i);
            inputPaymentToken.setText("");
            paymentData = data;

            if (data.getAttr().getPaymentId() == 4) {
                spBank.setVisibility(View.VISIBLE);
            }else {
                spBank.setVisibility(View.GONE);
                bankData = null;
            }

            if (TextUtils.isEmpty(data.getAttr().getPaymentFieldPost())) {
                inputPaymentToken.setVisibility(View.GONE);
            }else {
                inputPaymentToken.setVisibility(View.VISIBLE);
                inputPaymentToken.setHint(data.getAttr().getPaymentName() + " No");
            }
        }else if(spinner.getId() == R.id.sp_bank) {
            bankData = (Bank.Data) spinnerBankAdapter.getItem(i);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setRemoteValetHeader(int fakeId) {
        EntryDao entryDao = EntryDao.getInstance(this);
        remoteValetHeader = entryDao.getRemoteVthdIdByFakeId(fakeId);
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

    private class LoadPaymentTask extends AsyncTask<Void, Void, List<PaymentMethod.Data>> {

        @Override
        protected List<PaymentMethod.Data> doInBackground(Void... voids) {
            return PaymentDao.getInstance(CheckoutActivity.this).fetchPaymentMethods();
        }

        @Override
        protected void onPostExecute(List<PaymentMethod.Data> datas) {
            super.onPostExecute(datas);
            if (!datas.isEmpty()) {
                listPayment.clear();
                listPayment.addAll(datas);
                spinnerPaymentAdapter.notifyDataSetChanged();
            }
        }
    }

    private class LoadBanksTask extends AsyncTask<Void, Void, List<Bank.Data>> {

        @Override
        protected List<Bank.Data> doInBackground(Void... voids) {
            return BankDao.getInstance(CheckoutActivity.this).fetchBanks();
        }

        @Override
        protected void onPostExecute(List<Bank.Data> datas) {
            super.onPostExecute(datas);
            bankList.clear();
            bankList.addAll(datas);
            spinnerBankAdapter.notifyDataSetChanged();
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
        String totalPay;
        if (total < 0) {
            totalPay = "Rp. 0.00";
        }else {
            totalPay = MakeCurrencyString.fromInt(total);
        }
        txtTotalPay.setText(totalPay);
    }

    private void showConfirmDialog() {

        if (inputPaymentToken.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(inputPaymentToken.getText().toString())) {
                Toast.makeText(this, "Please fill " + inputPaymentToken.getHint(), Toast.LENGTH_SHORT).show();
            }
        }

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Process Checkout")
                .setContentText("Checkout transaction?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();

                        if (ApiClient.isNetworkAvailable(CheckoutActivity.this)) {
                            Toast.makeText(CheckoutActivity.this, "Closing... please wait", Toast.LENGTH_SHORT).show();
                            submitCheckout();
                        }else {
                            Toast.makeText(CheckoutActivity.this, "No Internet connection.", Toast.LENGTH_SHORT).show();
                        }
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

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    private void submitCheckout() {
        FinishCheckOut.Builder builder = new FinishCheckOut.Builder();
        builder.setCheckoutTime(getCurrentDate());
        builder.setAppId(PrefManager.getInstance(this).getAppId());
        builder.setDeviceId(PrefManager.getInstance(this).getDeviceId());

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

        String token = inputPaymentToken.getText().toString();
        if (!TextUtils.isEmpty(token)) {
            builder.setCardNo(token);
        }

        if (bankData != null) {
            builder.setBankData(bankData);
        }

        builder.setPaymentData(paymentData);

        FinishCheckoutDao finishCheckoutDao = FinishCheckoutDao.getInstance(this);
        finishCheckoutDao.setFinishCheckOut(builder.build());
        finishCheckoutDao.setId(remoteValetHeader);
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
        finishCheckoutDao.setPaymentData(paymentData);
        finishCheckoutDao.setBankData(bankData);

        TokenDao.getToken(finishCheckoutDao, this);

        Gson gson = new Gson();
        String finishCheckOut = gson.toJson(builder.build());
        Log.d("json checkout", finishCheckOut);

        //goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra("refresh", 1);
        intent.setAction(Main2Activity.ACTION_DOWNLOAD_CHECKIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
