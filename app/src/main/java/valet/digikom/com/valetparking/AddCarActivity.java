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
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.PrintCheckin;
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

public class AddCarActivity extends FragmentActivity implements StepOneFragmet.OnRegsitrationValid, StepTwoFragment.OnDefectSelectedListener,
                StepThreeFragment.OnStuffSelectedListener, DefectFragment.OnDefectDrawingListener, View.OnClickListener, StepOneFragmet.OnValetTypeSelectedListener, SignDialogFragment.OnDialogSignListener {

    public static final String KEY_DIALOG_SIGN = "sign";
    Button btnSubmit;
    Button btnCancel;
    StepOneFragmet fragmentRegFirst;
    DefectFragment fragmentDefect;
    StepThreeFragment fragmentStuff;
    ReviewFragment fragmentReview;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        btnSubmit = (Button) findViewById(R.id.btn_registration);
        btnCancel = (Button) findViewById(R.id.btn_cancel_reg);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentRegFirst = (StepOneFragmet) fragmentManager.findFragmentById(R.id.step_one_fragment);
        fragmentDefect = (DefectFragment) fragmentManager.findFragmentById(R.id.defect_fragment);
        fragmentStuff = (StepThreeFragment) fragmentManager.findFragmentById(R.id.stuff_fragment);
        fragmentReview = (ReviewFragment) fragmentManager.findFragmentById(R.id.review_fragment);
    }

    private void submitCheckin(final Bitmap bmp, final Checkin checkin, final EntryCheckinContainer checkinContainer) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<EntryCheckinResponse> call = apiEndpoint.postCheckin(checkinContainer);
                call.enqueue(new Callback<EntryCheckinResponse>() {
                    @Override
                    public void onResponse(Call<EntryCheckinResponse> call, Response<EntryCheckinResponse> response) {
                        if (response != null && response.body() != null) {
                            EntryCheckinResponse res = response.body();
                            EntryDao entryDao = EntryDao.getInstance(AddCarActivity.this);
                            entryDao.insertEntryResponse(res, EntryCheckinResponse.FLAG_UPLOAD_SUCCESS);

                            //new PrintCheckinTask().execute(res);

                            startActivity(new Intent(AddCarActivity.this, Main2Activity.class));
                            finish();

                            printCheckin(res);
                            //Log.d("Post checkin success: ", res.getData().getType());
                        }else {
                            Log.d("Post entry", "post entry checkin failed.");
                        }
                    }

                    @Override
                    public void onFailure(Call<EntryCheckinResponse> call, Throwable t) {
                        //Log.d("Post checkin error: ", t.getMessage());
                    }
                });
            }
        });

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
                        submitCheckin(fragmentReview.getSignatureBmp(), fragmentReview.getCheckin(), fragmentReview.getEntryCheckinContainer());
                        //submitCheckin(signBmp, fragmentReview.getCheckin(), fragmentReview.getEntryCheckinContainer());
                        sweetAlertDialog.setTitleText("success!")
                                .setContentText("Registration success")
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
        showConfirmDialog(fragmentReview);
    }

    private void printCheckin(final EntryCheckinResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PrintCheckin printCheckin = new PrintCheckin(AddCarActivity.this, response,fragmentReview.getBitmapDefect(), fragmentReview.getSignatureBmp(), fragmentReview.getItemsList());
                printCheckin.print();
            }
        });
    }
}
