package valet.digikom.com.valetparking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.dao.EntryCheckinContainerDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.domain.EntryCheckin;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.PrintCheckin;
import valet.digikom.com.valetparking.domain.PrintReceiptChekin;
import valet.digikom.com.valetparking.domain.ValetTypeJson;
import valet.digikom.com.valetparking.fragments.DefectFragment;
import valet.digikom.com.valetparking.fragments.ReviewFragment;
import valet.digikom.com.valetparking.fragments.SignDialogFragment;
import valet.digikom.com.valetparking.fragments.StepOneFragmet;
import valet.digikom.com.valetparking.fragments.StepThreeFragment;
import valet.digikom.com.valetparking.fragments.StepTwoFragment;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.CheckinCheckoutAlarm;
import valet.digikom.com.valetparking.util.PrefManager;

public class AddCarActivity extends FragmentActivity implements StepOneFragmet.OnRegsitrationValid, StepTwoFragment.OnDefectSelectedListener,
                StepThreeFragment.OnStuffSelectedListener, DefectFragment.OnDefectDrawingListener, View.OnClickListener, StepOneFragmet.OnValetTypeSelectedListener, SignDialogFragment.OnDialogSignListener {

    public static final String KEY_DIALOG_SIGN = "sign";
    Button btnSubmit;
    Button btnCancel;
    StepOneFragmet fragmentRegFirst;
    DefectFragment fragmentDefect;
    StepThreeFragment fragmentStuff;
    ReviewFragment fragmentReview;
    LinearLayout layoutGrey;
    ProgressBar progressBar;
    EntryDao entryDao;
    EntryCheckinContainerDao entryCheckinContainerDao;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        layoutGrey = (LinearLayout) findViewById(R.id.layout_grey);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSubmit = (Button) findViewById(R.id.btn_registration);
        btnCancel = (Button) findViewById(R.id.btn_cancel_reg);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentRegFirst = (StepOneFragmet) fragmentManager.findFragmentById(R.id.step_one_fragment);
        fragmentDefect = (DefectFragment) fragmentManager.findFragmentById(R.id.defect_fragment);
        fragmentStuff = (StepThreeFragment) fragmentManager.findFragmentById(R.id.stuff_fragment);
        fragmentReview = (ReviewFragment) fragmentManager.findFragmentById(R.id.review_fragment);

        entryDao = EntryDao.getInstance(this);
        entryCheckinContainerDao = EntryCheckinContainerDao.getInstance(this);
    }

    private void submitCheckin(final EntryCheckinContainer checkinContainer, final EntryCheckin.Builder builder,final SweetAlertDialog sweetAlertDialog) {


        if (!ApiClient.isNetworkAvailable(this)) {
            processFailedCheckin(builder, checkinContainer);
            progressBar.setVisibility(View.GONE);
            goToMain();
        } else {
            TokenDao.getToken(new ProcessRequest() {
                @Override
                public void process(String token) {
                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                    Call<EntryCheckinResponse> call = apiEndpoint.postCheckin(checkinContainer);
                    call.enqueue(new Callback<EntryCheckinResponse>() {
                        @Override
                        public void onResponse(Call<EntryCheckinResponse> call, Response<EntryCheckinResponse> response) {
                            progressBar.setVisibility(View.GONE);
                            if (response != null && response.body() != null) {
                                EntryCheckinResponse res = response.body();
                                //entryDao.insertEntryResponse(res, EntryCheckinResponse.FLAG_UPLOAD_SUCCESS);

                                print(res);
                            } else {
                                Log.d("Post entry", "post entry checkin failed.");
                                processFailedCheckin(builder, checkinContainer);
                                //Toast.makeText(AddCarActivity.this,"Post Entry Checkin failed", Toast.LENGTH_SHORT).show();
                                /*
                                sweetAlertDialog.setTitleText("Failed!")
                                        .setContentText("Registration Failed. Please contact your support.")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        */
                            }

                            goToMain();
                        }
                        @Override
                        public void onFailure(Call<EntryCheckinResponse> call, Throwable t) {
                            //Toast.makeText(AddCarActivity.this,"Post Entry Checkin failed", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            processFailedCheckin(builder, checkinContainer);

                            goToMain();
                        }
                    });
                }
            }, this);
        }


        /*
      new Thread(new Runnable() {
          @Override
          public void run() {
              ValetDbHelper valetDbHelper = new ValetDbHelper(AddCarActivity.this);
              CheckinDao checkinDao = CheckinDao.newInstance(valetDbHelper, AddCarActivity.this);
              checkinDao.addCheckIn(checkin,bmp);
          }
      }).run();
      */
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void setCheckin(String dropPoint, String platNo, String carType, String merk, String email, String warna) {
        fragmentReview.setCheckin(dropPoint, platNo, carType, merk, email, warna);
    }

    @Override
    public void onDefectSelected(String defect, DefectMaster defectMaster) {
        fragmentReview.selectDefect(defect, defectMaster);
    }

    @Override
    public void onDefectUnselected(String defect, DefectMaster defectMaster) {
        fragmentReview.unSelectDefect(defect, defectMaster);
    }

    @Override
    public void onStuffSelected(String stuff, AdditionalItems items) {
        fragmentReview.onSelectSuff(stuff, items);
    }

    @Override
    public void onStuffUnselected(String stuff, AdditionalItems items) {
        fragmentReview.onUnselectStuff(stuff, items);
    }


    @Override
    public void onDefectDrawing(List<DefectMaster> defectMasters) {
        fragmentReview.setDefectMasterList(defectMasters);
    }

    @Override
    public void setImageDefect(Bitmap bitmap) {
        fragmentReview.setImageDefect(bitmap);
    }

    private void showConfirmDialog(final ReviewFragment reviewFragment) {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Registration")
                .setContentText("Submit Registration?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        fragmentRegFirst.setCheckIn();
                        sweetAlertDialog.dismissWithAnimation();
                        progressBar.setVisibility(View.VISIBLE);
                        submitCheckin(fragmentReview.getEntryCheckinContainer(), fragmentReview.getBuilder(),sweetAlertDialog);
                        //submitCheckin(signBmp, fragmentReview.getCheckin(), fragmentReview.getEntryCheckinContainer());

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

    @Override
    public void onClick(View view) {
        if (view == btnSubmit) {
            if (fragmentRegFirst.isFormValid()){
                showSignDialog();
            }
        }else {
            startActivity(new Intent(this, Main2Activity.class));
            finish();
        }
    }

    @Override
    public void onValetTypeSelected(ValetTypeJson.Data data) {
        fragmentReview.setValetType(data);
    }

    private void showSignDialog() {
        SignDialogFragment sdf = new SignDialogFragment();
        sdf.show(getSupportFragmentManager(), KEY_DIALOG_SIGN);
    }

    @Override
    public void setBitMapSign(Bitmap bitMapSign) {
        fragmentReview.setSignBitmap(bitMapSign);
        //showConfirmDialog(fragmentReview);
        fragmentRegFirst.setCheckIn();
        progressBar.setVisibility(View.VISIBLE);
        submitCheckin(fragmentReview.getEntryCheckinContainer(), fragmentReview.getBuilder(),null);

    }

    private void printCheckin(final EntryCheckinResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                print(response);
            }
        });
    }

    private void print(EntryCheckinResponse response) {
        //PrintCheckin printCheckin = new PrintCheckin(AddCarActivity.this, response,fragmentReview.getBitmapDefect(), fragmentReview.getSignatureBmp(), fragmentReview.getItemsList());
        //printCheckin.print();
        PrintReceiptChekin printReceiptChekin = new PrintReceiptChekin(this,response, fragmentReview.getBitmapDefect(), fragmentReview.getSignatureBmp(), fragmentReview.getItemsList());
        printReceiptChekin.buildPrintData();
    }

    private void disableActivity() {
        // disable activity from user input until submit process done
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        layoutGrey.setVisibility(View.VISIBLE);
    }

    private void enableActivity() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        layoutGrey.setVisibility(View.GONE);
    }

    private void processFailedCheckin(EntryCheckin.Builder builder, EntryCheckinContainer checkinContainer) {
        EntryCheckinResponse entryCheckinResponse = new EntryCheckinResponse();
        EntryCheckinResponse.Data data = new EntryCheckinResponse.Data();
        EntryCheckinResponse.Attribute attr = new EntryCheckinResponse.Attribute();
        attr.setValetType(builder.getValetType().getAttrib().getValetTypeName());
        attr.setAreaParkir(" ");
        attr.setAreaParkirStatus(" ");
        attr.setBlokParkir(" ");
        attr.setBlokParkirStatus(" ");
        attr.setSektorParkir(" ");
        attr.setCar(builder.getCarMaster().getAttrib().getCarName());
        attr.setLogoMobil(builder.getCarMaster().getAttrib().getLogo());
        attr.setCheckinTime(builder.getCheckInTime());
        attr.setDropPoint(builder.getDropPointMaster().getAttrib().getDropName());
        attr.setId(builder.getVthdId());
        attr.setFee(builder.getValetType().getAttrib().getPrice());
        attr.setPlatNo(builder.getPlatNo());
        attr.setIdTransaksi(builder.generateTicketNo());
        attr.setSiteName(PrefManager.getInstance(this).getSiteName());

        data.setAttribute(attr);
        data.setId(String.valueOf(attr.getId())); // vthdid

        data.setType("ad_entry_checkin");
        entryCheckinResponse.setData(data);

        checkinContainer.getEntryCheckin().setId(data.getId());
        entryDao.insertEntryResponse(entryCheckinResponse, EntryCheckinResponse.FLAG_UPLOAD_PENDING);
        long a = entryCheckinContainerDao.insert(checkinContainer);

        if (a>=0) {
            Toast.makeText(this, "insert container sukes", Toast.LENGTH_SHORT).show();
        }

        print(entryCheckinResponse);

        CheckinCheckoutAlarm checkinCheckoutAlarm = CheckinCheckoutAlarm.getInstance(this);
        checkinCheckoutAlarm.startAlarm();
    }

    private void goToMain() {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.setAction(Main2Activity.ACTION_DOWNLOAD_CHECKIN);
        startActivity(intent);
        finish();
    }
}
