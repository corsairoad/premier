package valet.intan.com.valetparking.dao;

import android.content.Context;
import android.util.Log;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.domain.Token;
import valet.intan.com.valetparking.domain.TokenResponse;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.PrefManager;

/**
 * Created by dev on 1/8/17.
 */

public class TokenDao {

    private static final String BEARER = "Bearer ";
    private static final String TAG = TokenDao.class.getSimpleName();

    public static void getToken(final ProcessRequest request, Context context){
        final PrefManager prefManager = PrefManager.getInstance(context);
        String token = prefManager.getToken();
        if (token != null) {
            if (!token.contains(BEARER)){
                token = BEARER + token;
                prefManager.saveToken(token);
            }
            request.process(token);
        }
    }

    public static void refreshToken(final Context context) {
        final PrefManager prefManager = PrefManager.getInstance(context);
        String token = prefManager.getToken();

        if (token == null) {
            return;
        }

        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class);
        Call<ResponseBody> call = apiEndpoint.logout(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == AuthResDao.HTTP_STATUS_LOGOUT_SUKSES) {
                    Log.d(TAG, "logout token success, reauthenticating...");
                    reAuthenticate(context);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "Logout token failed.");
            }
        });
    }

    private static void reAuthenticate(Context context) {
        Log.d(TAG, "Getting new token");
        AuthResDao authResDao = AuthResDao.getInstance(context);
        final PrefManager prefManager = PrefManager.getInstance(context);

        ApiEndpoint service = ApiClient.createService(ApiEndpoint.class);

        Call<TokenResponse> call = service.getToken(authResDao.getUserEmail(), authResDao.getPwx());
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response)  {
                if ((response != null) && (response.body() != null)) {
                    Token token = response.body().getToken();
                    String v = BEARER + token.getToken();
                    prefManager.saveToken(v);
                    Log.d(TAG, "Reauthenticate token success. new token: " + v);
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.d(TAG, "Reauthenticate failed.");
            }
        });
    }

}
