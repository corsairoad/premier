package valet.intan.com.valetparking.dao;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.Main2Activity;
import valet.intan.com.valetparking.domain.AuthResponse;
import valet.intan.com.valetparking.domain.CancelBody;
import valet.intan.com.valetparking.domain.CancelResponse;
import valet.intan.com.valetparking.domain.LoginError403;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 1/29/2017.
 */

public class AuthResDao {

    private Context context;
    private static AuthResDao authResDao;
    private PrefManager prefManager;
    private OnAuthListener authListener;

    public static final int HTTP_STATUS_LOGIN_SUKSES = 200;
    public static final int HTTP_STATUS_LOGIN_FORBIDDEN = 403;
    public static final int HTTP_STATUS_LOGIN_INVALID = 401;
    public static final int HTTP_STATUS_LOGIN_ERR_CONN = 77;
    public static final int HTTP_STATUS_LOGIN_ERR_ROLE = 80;
    public static final int HTTP_STATUS_LOGIN_ERR_RESPONSE = 90;
    public static final int HTTP_STATUS_LOGOUT_SUKSES = 200;
    public static final int HTTP_STATUS_LOGOUT_INVALID = 401;

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

    public synchronized boolean login(final String email, final String password, final OnAuthListener onAuthListener) {
        this.authListener = onAuthListener;
        ApiEndpoint apiEndpoint = ApiClient.getClient().create(ApiEndpoint.class);
        Call<JsonElement> call = apiEndpoint.login(email, password);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonElement jsonElemet = response.body();

                int responseCode = response.code();
                Gson gson = new Gson();
                switch (responseCode) {
                    case HTTP_STATUS_LOGIN_SUKSES:
                        AuthResponse authResponse = gson.fromJson(jsonElemet, AuthResponse.class);
                        proceedToSucceed(authResponse,password);
                        break;
                    case HTTP_STATUS_LOGIN_FORBIDDEN:
                        try {
                            String res = response.errorBody().string();
                            LoginError403 loginErresponse = gson.fromJson(res, LoginError403.class);
                            proceedToFailed(HTTP_STATUS_LOGIN_FORBIDDEN, loginErresponse.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    case HTTP_STATUS_LOGIN_INVALID:
                        proceedToFailed(HTTP_STATUS_LOGIN_INVALID, null);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                Toast.makeText(context,"Login Failed, Connection problem.", Toast.LENGTH_SHORT).show();
                proceedToFailed(HTTP_STATUS_LOGIN_ERR_CONN, null);
            }

        });
        return true;
    }

    private void proceedToFailed(int messageCode, String message) {
        this.authListener.loginFailed(messageCode, message);
    }

    private void proceedToSucceed(AuthResponse response, String password) {
        if (response != null ) {
            AuthResponse.Data.Role role = response.getData().getRole();
            String token = response.getMeta().getToken();
            if (role.getRoleId() != 20) {
                proceedToFailed(HTTP_STATUS_LOGIN_ERR_ROLE, null);
                return;
            }
            saveAuthRes(response);
            savePwx(password);
            saveToken(token);
            this.authListener.loginSuccess();
        }else {
            proceedToFailed(HTTP_STATUS_LOGIN_ERR_RESPONSE, null);
        }
    }

    public void loginSpvForCancelTicket(String email, String password, final String cancelInfo, final int id) {
        final boolean[] isOke = {false};
        ApiEndpoint apiEndpoint = ApiClient.getClient().create(ApiEndpoint.class);
        Call<AuthResponse> call = apiEndpoint.loginSpv(email, password);
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
        void loginFailed(int messageCode, String message);
        void loginSuccess();
    }

}
