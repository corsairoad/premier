package valet.digikom.com.valetparking.service;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import valet.digikom.com.valetparking.domain.DefectResponse;
import valet.digikom.com.valetparking.domain.TokenResponse;

/**
 * Created by dev on 1/7/17.
 */

public interface ApiEndpoint {

    @GET("defect_master")
    Call<DefectResponse> getDefects();

    @FormUrlEncoded
    @POST("authenticate")
    Call<TokenResponse> getToken(@Field("email") String email, @Field("password") String password);

}
