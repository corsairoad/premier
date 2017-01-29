package valet.digikom.com.valetparking.dao;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.Main2Activity;
import valet.digikom.com.valetparking.domain.AuthResponse;
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
                    saveAuthRes(response.body());
                    savePwx(password);
                    isOke[0] = true;
                    onAuthListener.loginSuccess();
                }else {
                    Toast.makeText(context,"Login Failed", Toast.LENGTH_SHORT).show();
                    onAuthListener.loginFailed();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(context,"Login Failed", Toast.LENGTH_SHORT).show();
                isOke[0] = false;
                onAuthListener.loginFailed();
            }

        });
        return isOke[0];
    }

    public interface OnAuthListener {
        void loginFailed();
        void loginSuccess();
    }

}
