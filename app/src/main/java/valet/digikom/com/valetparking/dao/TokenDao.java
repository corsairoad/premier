package valet.digikom.com.valetparking.dao;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.Token;
import valet.digikom.com.valetparking.domain.TokenResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;

/**
 * Created by dev on 1/8/17.
 */

public class TokenDao {

    public static void getToken(final ProcessRequest request) {

        ApiEndpoint service = ApiClient.createService(ApiEndpoint.class);
        Call<TokenResponse> call = service.getToken(Token.EMAIL, Token.PASSX);
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if ((response != null) && (response.body() != null)) {
                    Token token = response.body().getToken();
                    String v = token.getToken();
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
