package valet.digikom.com.valetparking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.epson.eposprint.EposException;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import valet.digikom.com.valetparking.dao.EntryCheckinContainerDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.ReprintDao;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.domain.EntryCheckin;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.PrintReceiptChekin;
import valet.digikom.com.valetparking.domain.ShowMsg;
import valet.digikom.com.valetparking.domain.ValetTypeJson;
import valet.digikom.com.valetparking.fragments.DefectFragment;
import valet.digikom.com.valetparking.fragments.ReviewFragment;
import valet.digikom.com.valetparking.fragments.SignDialogFragment;
import valet.digikom.com.valetparking.fragments.StepOneFragmet;
import valet.digikom.com.valetparking.fragments.StepThreeFragment;
import valet.digikom.com.valetparking.fragments.StepTwoFragment;
import valet.digikom.com.valetparking.util.CheckinCheckoutAlarm;
import valet.digikom.com.valetparking.util.MyPrinterException;
import valet.digikom.com.valetparking.util.PrefManager;

public class AddCarActivity extends FragmentActivity implements StepOneFragmet.OnRegsitrationValid,
        StepTwoFragment.OnDefectSelectedListener, StepThreeFragment.OnStuffSelectedListener,
        DefectFragment.OnDefectDrawingListener, View.OnClickListener,
        StepOneFragmet.OnValetTypeSelectedListener, SignDialogFragment.OnDialogSignListener {

    public static final String KEY_DIALOG_SIGN = "sign";
    private Button btnSubmit;
    private Button btnCancel;
    private StepOneFragmet fragmentRegFirst;
    private DefectFragment fragmentDefect;
    private StepThreeFragment fragmentStuff;
    private ReviewFragment fragmentReview;
    private LinearLayout layoutGrey;
    private ProgressBar progressBar;
    private EntryDao entryDao;
    private EntryCheckinContainerDao entryCheckinContainerDao;
    private SignDialogFragment sdf;
    private MaterialDialog materialDialog;

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
        fragmentRegFirst = (StepOneFragmet) fragmentManager.findFragmentById(R.id.step_one_fragment); // for car details (license plate, valet type, car type, and color)
        fragmentDefect = (DefectFragment) fragmentManager.findFragmentById(R.id.defect_fragment);
        fragmentStuff = (StepThreeFragment) fragmentManager.findFragmentById(R.id.stuff_fragment);
        fragmentReview = (ReviewFragment) fragmentManager.findFragmentById(R.id.review_fragment);

        entryDao = EntryDao.getInstance(this);
        entryCheckinContainerDao = EntryCheckinContainerDao.getInstance(this);
    }

    private void submitCheckin(final EntryCheckinContainer checkinContainer, final EntryCheckin.Builder builder) {
        processFailedCheckin(builder, checkinContainer);
        //goToMain();

        /*
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
                            }
                            goToMain();
                        }
                        @Override
                        public void onFailure(Call<EntryCheckinResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            processFailedCheckin(builder, checkinContainer);

                            goToMain();
                        }
                    });
                }
            }, this);
        }
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
        int valetId = data.getAttrib().getId();
        String valetTypeName = data.getAttrib().getValetTypeName();
        fragmentReview.setValetType(data);

        fragmentRegFirst.onValetTypeChange(valetTypeName);
        fragmentStuff.onValetTypeChange(valetTypeName);
        fragmentDefect.onValetTypeChange(valetTypeName);
    }

    private void showSignDialog() {
        sdf = new SignDialogFragment();
        sdf.show(getSupportFragmentManager(), KEY_DIALOG_SIGN);
    }

    @Override
    public void setBitMapSign(Bitmap bitMapSign) {

        if (sdf != null) {
            sdf.dismiss();
        }

        showDialog(true);

        fragmentReview.setSignBitmap(bitMapSign);
        //showConfirmDialog(fragmentReview);
        fragmentRegFirst.setCheckIn();
        progressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                submitCheckin(fragmentReview.getEntryCheckinContainer(), fragmentReview.getBuilder());
                //processFailedCheckin(fragmentReview.getBuilder(),fragmentReview.getEntryCheckinContainer());
                //goToMain();
            }
        }).start();
    }

    private void showDialog(boolean show) {
        if (show) {
            materialDialog = new MaterialDialog.Builder(this)
                    .title("Please wait...")
                    .customView(R.layout.layout_scrolling_image, false)
                    .build();
            materialDialog.show();
        }else {
            if (materialDialog != null) {
                materialDialog.dismiss();
            }
        }
    }

    private void print(EntryCheckinResponse response) {
        //PrintCheckin printCheckin = new PrintCheckin(AddCarActivity.this, response,fragmentReview.getBitmapDefect(), fragmentReview.getSignatureBmp(), fragmentReview.getItemsList());
        //printCheckin.print();
        final PrintReceiptChekin printReceiptChekin = new PrintReceiptChekin(this,response, fragmentReview.getBitmapDefect(), fragmentReview.getSignatureBmp(), fragmentReview.getItemsList());
        try {
            printReceiptChekin.buildPrintData();
            //goToMain();
        } catch (final EposException e) {
            final int errStatus = e.getErrorStatus();
            e.printStackTrace();
            printReceiptChekin.closePrinter();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                    printReceiptChekin.closePrinter();
                    ShowMsg.showResult(errStatus,"Error print checkin", AddCarActivity.this);
                    Toast.makeText(AddCarActivity.this,"Print error: " + errStatus, Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    private void processFailedCheckin(EntryCheckin.Builder builder, EntryCheckinContainer checkinContainer) {
        PrefManager prefManager = PrefManager.getInstance(this);
        int lastTicketCounter = builder.getLastTicketCounter(this);
        String noTiket = builder.generateTicketNo(this, lastTicketCounter);
        int fakeVthdId = builder.generateId();

        // ------------ create checkin data and save to local db ------------------
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
        //attr.setId(lastTicketCounter);
        attr.setId(fakeVthdId);
        attr.setFee(builder.getValetType().getAttrib().getPrice());
        attr.setPlatNo(builder.getPlatNo());
        attr.setIdTransaksi(noTiket); // transaction id set for no tiket for temporary
        attr.setNoTiket(noTiket);
        attr.setSiteName(prefManager.getSiteName());
        attr.setLastTicketCounter(lastTicketCounter);
        attr.setColor(builder.getColorMaster().getAttrib().getColorName());

        data.setAttribute(attr);
        data.setId(String.valueOf(fakeVthdId)); // vthdid
        data.setType("ad_entry_checkin");
        entryCheckinResponse.setData(data);

        // -------------- create checkin json body for save to local db and upload to server --------------
        EntryCheckin entryCheckin = checkinContainer.getEntryCheckin();
        //entryCheckin.setId(String.valueOf(lastTicketCounter));
        entryCheckin.setId(String.valueOf(fakeVthdId));
        entryCheckin.getAttrib().setCheckinTime(attr.getCheckinTime());
        entryCheckin.getAttrib().setTicketNo(attr.getIdTransaksi());
        entryCheckin.getAttrib().setQrCode("------------------------");
        entryCheckin.getAttrib().setDeviceId(prefManager.getDeviceId());
        entryCheckin.getAttrib().setAppId(prefManager.getAppId());
        entryCheckin.getAttrib().setLastTicketCounter(lastTicketCounter);

        print(entryCheckinResponse);
        saveReprintData(entryCheckinResponse,noTiket.trim());

        entryDao.insertEntryResponse(entryCheckinResponse, EntryCheckinResponse.FLAG_UPLOAD_PENDING);
        entryCheckinContainerDao.insert(checkinContainer);

        goToMain();
    }

    private void saveReprintData(EntryCheckinResponse entryCheckinResponse, String noTiket) {
        Bitmap bmpSignature = fragmentReview.getSignatureBmp();
        Bitmap bmpDefects = fragmentReview.getBitmapDefect();
        List<AdditionalItems> stuffs = fragmentReview.getItemsList();

        ReprintDao reprintDao = ReprintDao.getInstance(AddCarActivity.this);
        reprintDao.saveReprintData(entryCheckinResponse, noTiket.trim(), bmpDefects, bmpSignature,stuffs);
    }

    private void startAlarmForUploadCheckin() {
        CheckinCheckoutAlarm checkinCheckoutAlarm = CheckinCheckoutAlarm.getInstance(this);
        checkinCheckoutAlarm.startAlarm();
    }

    private void goToMain() {
        Intent intent = new Intent(this, Main2Activity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(Main2Activity.ACTION_DOWNLOAD_CHECKIN);
        startActivity(intent);
        finish();
    }


    /*
        For debugging
     */
    private void debugJsonCheckin(EntryCheckinContainer entryCheckinContainer) {
        Gson gson = new Gson();
        String jsonEntryCheckin = gson.toJson(entryCheckinContainer);
        exportToFile(jsonEntryCheckin);
        Log.d("JSON CHECKIN", jsonEntryCheckin);

    }

    /*
        For debugging
     */
    private void exportToFile(String json) {
        try {
            File myFile = new File("/sdcard/mysdfile.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
            myOutWriter.append(json);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(this,"Done writing SD 'mysdfile.txt'", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
