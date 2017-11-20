package valet.intan.com.valetparking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.adapter.DropPointAdapter;
import valet.intan.com.valetparking.adapter.SpinnerSiteAdapter;
import valet.intan.com.valetparking.dao.DropDao;
import valet.intan.com.valetparking.dao.EntryDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.domain.AuthResponse;
import valet.intan.com.valetparking.domain.DropPointMaster;
import valet.intan.com.valetparking.domain.DropPointMasterResponse;
import valet.intan.com.valetparking.domain.PatchMeBody;
import valet.intan.com.valetparking.domain.PatchMeResponse;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.LoggingUtils;
import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.MyLifecycleHandler;
import valet.intan.com.valetparking.util.PrefManager;
import valet.intan.com.valetparking.util.RefreshTokenAlarm;
import valet.intan.com.valetparking.util.ValetDbHelper;

public class WelcomeActivity extends AppCompatActivity {

    Spinner spSites;
    Spinner spLobbies;
    Button btnSave;
    List<AuthResponse.Data.RoleOption> roleOptions = new ArrayList<>();
    List<DropPointMaster> dropPointList = new ArrayList<>();
    PrefManager prefManager;
    SpinnerSiteAdapter spinnerSiteAdapter;
    DropPointAdapter dropPointAdapter;
    DropPointMaster dropPointMaster;
    DropDao dropDao;
    ValetDbHelper valetDbHelper;
    AuthResponse.Data.RoleOption mRoleOption;

    private LoggingUtils loggingUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        PrefManager.getInstance(this).setRelaunch(true);

        loggingUtils = LoggingUtils.getInstance(this);

        valetDbHelper = ValetDbHelper.getInstance(this);
        dropDao = DropDao.getInstance(valetDbHelper);
        prefManager = PrefManager.getInstance(this);
        spLobbies = (Spinner) findViewById(R.id.spinner_lobby);
        spSites = (Spinner) findViewById(R.id.spinner_site);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ApiClient.isNetworkAvailable(WelcomeActivity.this)) {
                    btnSave.setEnabled(false);
                    btnSave.setText("Saving... please wait");
                    // patch(dropPointMaster.getAttrib().getDropId());
                    loggingUtils.logSetSiteAndLobby(prefManager.getUserName(), mRoleOption.getSiteName(), dropPointMaster.getAttrib().getDropName());
                    save(dropPointMaster.getAttrib().getDropId());

                    // //checkIfLoginfromDifferentLobby(dropPointMaster.getAttrib().getDropId(), prefManager.getIdDefaultDropPoint());
                    //removeAllCheckinList();

                    //prefManager.setIdSite(mRoleOption.getSiteId());
                    //prefManager.setSiteName(mRoleOption.getSiteName());
                    //prefManager.setDefaultDropPoint(dropPointMaster.getAttrib().getDropId());
                    //prefManager.setDefaultDropPointName(dropPointMaster.getAttrib().getDropName());
                    //prefManager.setUserRoleId(mRoleOption.getUserRoleId());

                    //setLastLoginDate();
                    //setRefreshTokenAlarm();

                    //goToMain();
                } else {
                    loggingUtils.logSetSiteAndLobbyError("No internet connecntion");
                    showErrorDialog("Internet Problem", "Unable to submit site & lobby data due to connection problem.");
                }

            }
        });

        AuthResponse authResponse = prefManager.getAuthResponse();
        if (authResponse != null) {
            roleOptions = authResponse.getData().getRoleOptions();
        }

        spinnerSiteAdapter = new SpinnerSiteAdapter(this, roleOptions);
        spSites.setAdapter(spinnerSiteAdapter);
        spSites.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AuthResponse.Data.RoleOption roleOption = (AuthResponse.Data.RoleOption) spinnerSiteAdapter.getItem(i);
                if (roleOption != null) {
                    mRoleOption = roleOption;
                    patch(mRoleOption.getIdLobby());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dropPointAdapter = new DropPointAdapter(this, dropPointList);
        spLobbies.setAdapter(dropPointAdapter);
        spLobbies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dropPointMaster = (DropPointMaster) dropPointAdapter.getItem(i);
                //updateBtnSave();

                if (dropPointMaster != null) {
                    //prefManager.setDefaultDropPoint(dropPointMaster.getAttrib().getDropId());
                    //prefManager.setDefaultDropPointName(dropPointMaster.getAttrib().getDropName());
                    updateBtnSave();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLifecycleHandler.relaunchToWelcomeActivity(this);
    }

    private void setLastLoginDate() {
        prefManager.setLastLoginDateToCurrentDate();
    }

    private void setRefreshTokenAlarm() {
        RefreshTokenAlarm refreshTokenAlarm = RefreshTokenAlarm.getInstance(this);
        refreshTokenAlarm.startRepeatAlarm();
        refreshTokenAlarm.startAlarmIn5Days();
    }

    // delete checkin list in local db if current lobby id different from last logged in lobby id
    // so the data from different lobby won't show up in parked cars fragment
    private void checkIfLoginfromDifferentLobby(int newLobby, String previousLobby) {
        if (previousLobby == null) {
            return;
        }

        if (!String.valueOf(newLobby).equalsIgnoreCase(previousLobby)) {
            EntryDao.getInstance(this).removeAllCheckinList();
        }
    }

    private void removeAllCheckinList() {
        EntryDao.getInstance(this).removeAllCheckinList();
    }

    private void goToMain() {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Main2Activity.ACTION_DOWNLOAD_CHECKINS);
        startActivity(intent);
        finish();
    }

    private void save(final int idLobby) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                PatchMeBody patchMeBody = new PatchMeBody();
                PatchMeBody.Data data = new PatchMeBody.Data();
                PatchMeBody.Data.RoleOpt role = new PatchMeBody.Data.RoleOpt();
                role.setUserRoleId(mRoleOption.getUserRoleId());
                role.setLobbyId(idLobby);
                role.setDeviceId(prefManager.getDeviceId());
                data.setRoleOpt(role);
                patchMeBody.setData(data);

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class);
                Call<PatchMeResponse> call = apiEndpoint.patchMe(patchMeBody, token);
                call.enqueue(new Callback<PatchMeResponse>() {
                    @Override
                    public void onResponse(Call<PatchMeResponse> call, Response<PatchMeResponse> response) {
                        //checkIfLoginfromDifferentLobby(dropPointMaster.getAttrib().getDropId(), prefManager.getIdDefaultDropPoint());
                        if (response != null && response.body() != null) {
                            removeAllCheckinList();

                            String remoteDeviceId = response.body().getData().getRemoteDeviceId();
                            int lastTicketCounter = response.body().getData().getLastCounterTicket();
                            prefManager.saveRemoteDeviceId(remoteDeviceId);
                            prefManager.saveLastTicketCounter(lastTicketCounter);

                            manageTicketCounter(dropPointMaster.getAttrib().getDropId(), lastTicketCounter);

                            prefManager.setIdSite(mRoleOption.getSiteId());
                            prefManager.setSiteName(mRoleOption.getSiteName());
                            prefManager.setDefaultDropPoint(dropPointMaster.getAttrib().getDropId());
                            prefManager.setDefaultDropPointName(dropPointMaster.getAttrib().getDropName());
                            prefManager.setUserRoleId(mRoleOption.getUserRoleId());

                            setLastLoginDate();
                            setRefreshTokenAlarm();

                            loggingUtils.logSetSiteAndLobbySucceed(mRoleOption.getSiteName(), dropPointMaster.getAttrib().getDropName(),remoteDeviceId, lastTicketCounter);

                            goToMain();
                        }else {
                            loggingUtils.logSetSiteAndLobbyError(response.message());
                            btnSave.setEnabled(true);
                            btnSave.setText("SAVE");
                            showErrorDialog("Request Problem", "Response code: " + response.code() + "\n" + "Message: " + response.message());
                        }

                    }

                    @Override
                    public void onFailure(Call<PatchMeResponse> call, Throwable t) {
                        loggingUtils.logSetSiteAndLobbyError(t.getCause().getMessage());
                        btnSave.setEnabled(true);
                        btnSave.setText("SAVE");
                        Toast.makeText(WelcomeActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
                        showErrorDialog("Request Failure", "Message: " + t.getCause().getMessage());

                    }
                });
            }
        }, this);
    }

    private void manageTicketCounter(int idLobby, int counterFromServer) {
        int lastPrintedTicket = prefManager.getLastPrintedTicket();
        int lastLobbyId = Integer.valueOf(prefManager.getIdDefaultDropPoint());

        if (counterFromServer > lastPrintedTicket) {
            prefManager.saveLastPrintedTicketCounter(counterFromServer);
        } else if (idLobby != lastLobbyId) {
            prefManager.saveLastPrintedTicketCounter(counterFromServer);
        }
    }

    private void patch(final int idLobby){

        if (!ApiClient.isNetworkAvailable(this)) {
            showErrorDialog("Internet Problem", "Unable to download lobbies due to internet connection problem.");
            if (dropPointAdapter != null && !dropPointList.isEmpty()) {
                dropPointList.clear();
                dropPointAdapter.notifyDataSetChanged();
            }
        }

        btnSave.setVisibility(View.GONE);

        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                PatchMeBody patchMeBody = new PatchMeBody();
                PatchMeBody.Data data = new PatchMeBody.Data();
                PatchMeBody.Data.RoleOpt role = new PatchMeBody.Data.RoleOpt();
                role.setUserRoleId(mRoleOption.getUserRoleId());
                role.setLobbyId(idLobby);
                role.setDeviceId(prefManager.getDeviceId());
                data.setRoleOpt(role);
                patchMeBody.setData(data);

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                Call<PatchMeResponse> call = apiEndpoint.patchMe(patchMeBody, token);
                call.enqueue(new Callback<PatchMeResponse>() {
                    @Override
                    public void onResponse(Call<PatchMeResponse> call, Response<PatchMeResponse> response) {
                        if (response != null && response.body() != null) {
                            String remoteDeviceId = response.body().getData().getRemoteDeviceId();
                            int lastTicketCounter = response.body().getData().getLastCounterTicket();
                            prefManager.saveRemoteDeviceId(remoteDeviceId);
                            prefManager.saveLastTicketCounter(lastTicketCounter);


                            //download lobby
                            TokenDao.getToken(new ProcessRequest() {
                                @Override
                                public void process(String token) {
                                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                                    Call<DropPointMasterResponse> call = apiEndpoint.getDropPoints(token);
                                    call.enqueue(new Callback<DropPointMasterResponse>() {
                                        @Override
                                        public void onResponse(Call<DropPointMasterResponse> call, Response<DropPointMasterResponse> response) {
                                            if (response != null && response.body() != null) {
                                                dropPointList = response.body().getDropPointList();
                                                dropDao.insertDropPoints(dropPointList);
                                                updateSpinnerLobby();
                                                btnSave.setVisibility(View.VISIBLE);
                                            }else {
                                                btnSave.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DropPointMasterResponse> call, Throwable t) {
                                            Toast.makeText(WelcomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            btnSave.setVisibility(View.VISIBLE);
                                            //Log.d("DropPointMaster error: ", t.getMessage());
                                        }
                                    });
                                }
                            }, WelcomeActivity.this);
                        }else {
                            btnSave.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<PatchMeResponse> call, Throwable t) {
                        Toast.makeText(WelcomeActivity.this, "error", Toast.LENGTH_SHORT).show();
                        btnSave.setVisibility(View.VISIBLE);
                    }
                });
            }
        }, this);
    }

    private void updateSpinnerLobby() {
        dropPointList.clear();
        dropPointList.addAll(dropDao.fetchAllDropPoints());
        dropPointAdapter = new DropPointAdapter(this, dropPointList);
        spLobbies.setAdapter(dropPointAdapter);
    }

    public void updateBtnSave() {
        if (btnSave.getVisibility() == View.INVISIBLE) {
            btnSave.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorDialog(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setIcon(R.drawable.ic_error_outline);
        builder.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


}
