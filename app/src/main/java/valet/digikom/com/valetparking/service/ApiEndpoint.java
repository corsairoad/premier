package valet.digikom.com.valetparking.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import valet.digikom.com.valetparking.domain.AdditionalItemsResponse;
import valet.digikom.com.valetparking.domain.CarMasterResponse;
import valet.digikom.com.valetparking.domain.ColorMasterResponse;
import valet.digikom.com.valetparking.domain.DefectResponse;
import valet.digikom.com.valetparking.domain.DropPointMasterResponse;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
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

    @GET("additional_item_site_detail")
    Call<AdditionalItemsResponse> getItems();

    @GET("car_master")
    Call<CarMasterResponse> getCars();

    @GET("color_master")
    Call<ColorMasterResponse> getColors();

    @GET("droppoint_floor_master")
    Call<DropPointMasterResponse> getDropPoints();

    @POST("ad_entry_checkin")
    Call<EntryCheckinResponse> postCheckin(@Body EntryCheckinContainer checkinContainer);
}
