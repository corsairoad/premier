package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.adapter.DropPointAdapter;
import valet.digikom.com.valetparking.adapter.SpinnerSiteAdapter;
import valet.digikom.com.valetparking.dao.DropDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.AuthResponse;
import valet.digikom.com.valetparking.domain.DropPointMaster;
import valet.digikom.com.valetparking.domain.DropPointMasterResponse;
import valet.digikom.com.valetparking.domain.PatchMeBody;
import valet.digikom.com.valetparking.domain.PatchMeResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.PrefManager;
import valet.digikom.com.valetparking.util.ValetDbHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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
                    prefManager.setIdSite(mRoleOption.getSiteId());
                    prefManager.setSiteName(mRoleOption.getSiteName());
                    prefManager.setDefaultDropPoint(dropPointMaster.getAttrib().getDropId());
                    prefManager.setDefaultDropPointName(dropPointMaster.getAttrib().getDropName());
                    patch(dropPointMaster.getAttrib().getDropId());
                    goToMain();
                }else {
                    Toast.makeText(WelcomeActivity.this,"Oops, you are not connected to internet. Please try again later", Toast.LENGTH_SHORT).show();
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
                updateBtnSave();
                /*
                if (dropPointMaster != null) {
                    prefManager.setDefaultDropPoint(dropPointMaster.getAttrib().getDropId());
                    prefManager.setDefaultDropPointName(dropPointMaster.getAttrib().getDropName());
                    updateBtnSave();
                }
                */
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(Main2Activity.ACTION_DOWNLOAD_CHECKIN);
        startActivity(intent);
        finish();
    }

    private void patch(final int idLobby){
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

                            Log.d("Last_ticket", ""+lastTicketCounter);

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
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<DropPointMasterResponse> call, Throwable t) {
                                            Toast.makeText(WelcomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            //Log.d("DropPointMaster error: ", t.getMessage());
                                        }
                                    });
                                }
                            }, WelcomeActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<PatchMeResponse> call, Throwable t) {
                        Toast.makeText(WelcomeActivity.this, "error", Toast.LENGTH_SHORT).show();
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


}
