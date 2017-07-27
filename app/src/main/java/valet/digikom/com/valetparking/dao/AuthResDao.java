package valet.digikom.com.valetparking.dao;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.Main2Activity;
import valet.digikom.com.valetparking.domain.AuthResponse;
import valet.digikom.com.valetparking.domain.CancelBody;
import valet.digikom.com.valetparking.domain.CancelResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 1/29/2017.
 */

public class AuthResDao {

    private Context context;
    private static AuthResDao authResDao;
    private PrefManager prefManager;
    private OnAuthListener authListener;

    private AuthResDao(Context context) {
        this.context = context;
        this.prefManager = PrefManager.getInstance(context);
    }

    public static AuthResDao getInstance(Context context) {
        if (authResDao == null) {
            authResDao = new AuthResDao(context.getApplicationContext());
        }
        return authResDao;
    }

    private void saveAuthRes(AuthResponse authResponse) {
        prefManager.saveAuthResponse(authResponse);
    }

    private void savePwx(String pwx) {
        prefManager.savePassword(pwx);
    }

    private void saveToken(String token) {
        prefManager.saveToken(token);
    }

    public String getUserName() {
        return prefManager.getUserName();
    }

    public String getUserEmail() {
        return prefManager.getUserEmail();
    }

    public String getPwx() {
        return prefManager.getPassWord();
    }

    public synchronized boolean login(String email, final String password, final OnAuthListener onAuthListener) {
        this.authListener = onAuthListener;
        final boolean[] isOke = {false};
        ApiEndpoint apiEndpoint = ApiClient.getClient().create(ApiEndpoint.class);
        Call<AuthResponse> call = apiEndpoint.login(email, password);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response != null && response.body() != null) {
                    AuthResponse.Data.Role role = response.body().getData().getRole();
                    String token = response.body().getMeta().getToken();
                    if (role.getRoleId() != 20) {
                        onAuthListener.loginFailed();
                        return;
                    }
                    saveAuthRes(response.body());
                    savePwx(password);
                    saveToken(token);
                    isOke[0] = true;
                    onAuthListener.loginSuccess();
                }else {
                    Toast.makeText(context,"Login Failed, Username or password incorrect.", Toast.LENGTH_SHORT).show();
                    onAuthListener.loginFailed();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(context,"Login Failed, Connection problem.", Toast.LENGTH_SHORT).show();
                isOke[0] = false;
                onAuthListener.loginFailed();
            }

        });
        return isOke[0];
    }

    public void loginSpvForCancelTicket(String email, String password, final String cancelInfo, final int id) {
        final boolean[] isOke = {false};
        ApiEndpoint apiEndpoint = ApiClient.getClient().create(ApiEndpoint.class);
        Call<AuthResponse> call = apiEndpoint.login(email, password);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response != null && response.body() != null) {
                    AuthResponse.Data.Role role = response.body().getData().getRole();
                    AuthResponse.Meta meta = response.body().getMeta();
                    String token = meta.getToken();
                    if (role.getUserLevel().toLowerCase().contains("supervisor")) {
                        CancelBody cancelBody = new CancelBody.Builder()
                                .setCancelInfo(cancelInfo)
                                .build();
                        ApiEndpoint endpoint = ApiClient.createService(ApiEndpoint.class, null);
                        Call<CancelResponse> call1 = endpoint.cancelTicket(id,cancelBody, token);
                        call1.enqueue(new Callback<CancelResponse>() {
                            @Override
                            public void onResponse(Call<CancelResponse> call, Response<CancelResponse> response) {
                                if (response != null && response.body() != null) {
                                    //delete data di database berdasarkan id;
                                    Toast.makeText(context, "Cancel Ticket success", Toast.LENGTH_SHORT).show();
                                    EntryDao entryDao = EntryDao.getInstance(context);
                                    entryDao.removeEntryById(id);
                                    Intent intent = new Intent(context, Main2Activity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            }

                            @Override
                            public void onFailure(Call<CancelResponse> call, Throwable t) {

                            }
                        });

                    }else {
                        Toast.makeText(context,"Cancel ticket failed. Account is not authorized.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context,"Cancel ticket failed. Account is not authorized.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(context,"Login Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public interface OnAuthListener {
        void loginFailed();
        void loginSuccess();
    }

}
