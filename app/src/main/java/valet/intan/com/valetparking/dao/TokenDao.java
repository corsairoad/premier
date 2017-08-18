package valet.intan.com.valetparking.dao;

import android.content.Context;
import android.util.Log;

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

    public static void getToken(final ProcessRequest request, Context context){
        final PrefManager prefManager = PrefManager.getInstance(context);
        String token = prefManager.getToken();
        if (token != null) {
            if (!token.contains(BEARER)){
                token = BEARER + token;
                prefManager.saveToken(token);
            }
            request.process(token);
        }else {
            AuthResDao authResDao = AuthResDao.getInstance(context);
            ApiEndpoint service = ApiClient.createService(ApiEndpoint.class);
            Call<TokenResponse> call = service.getToken(authResDao.getUserEmail(), authResDao.getPwx());
            call.enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response)  {
                    if ((response != null) && (response.body() != null)) {
                        Token token = response.body().getToken();
                        String v = BEARER + token.getToken();
                        prefManager.saveToken(v);
                        request.process(v);
                        Log.d("Token Auth", v);
                    }
                }

                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {

                }
            });
        }

    }

}
