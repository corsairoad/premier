package valet.digikom.com.valetparking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

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
                prefManager.setIdSite(mRoleOption.getSiteId());
                patch(dropPointMaster.getAttrib().getDropId());
                startActivity(new Intent(WelcomeActivity.this, Main2Activity.class));
                finish();
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
                }else {
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
                prefManager.setDefaultDropPoint(dropPointMaster.getAttrib().getDropId());
                updateBtnSave();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void patch(final int idLobby) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                PatchMeBody patchMeBody = new PatchMeBody();
                PatchMeBody.Data data = new PatchMeBody.Data();
                PatchMeBody.Data.RoleOpt role = new PatchMeBody.Data.RoleOpt();
                role.setUserRoleId(mRoleOption.getUserRoleId());
                role.setLobbyId(idLobby);
                data.setRoleOpt(role);
                patchMeBody.setData(data);

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<PatchMeResponse> call = apiEndpoint.patchMe(patchMeBody);
                call.enqueue(new Callback<PatchMeResponse>() {
                    @Override
                    public void onResponse(Call<PatchMeResponse> call, Response<PatchMeResponse> response) {
                        if (response != null && response.body() != null) {
                            //download lobby
                            TokenDao.getToken(new ProcessRequest() {
                                @Override
                                public void process(String token) {
                                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                                    Call<DropPointMasterResponse> call = apiEndpoint.getDropPoints();
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
                        }else {

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
