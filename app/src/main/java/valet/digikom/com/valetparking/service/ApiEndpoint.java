package valet.digikom.com.valetparking.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import valet.digikom.com.valetparking.domain.AddCarCallBody;
import valet.digikom.com.valetparking.domain.AddCarCallResponse;
import valet.digikom.com.valetparking.domain.AdditionalItemsResponse;
import valet.digikom.com.valetparking.domain.AuthResponse;
import valet.digikom.com.valetparking.domain.Bank;
import valet.digikom.com.valetparking.domain.CancelBody;
import valet.digikom.com.valetparking.domain.CancelResponse;
import valet.digikom.com.valetparking.domain.CarMasterResponse;
import valet.digikom.com.valetparking.domain.ChangePassword;
import valet.digikom.com.valetparking.domain.ChangePasswordResponse;
import valet.digikom.com.valetparking.domain.CheckinList;
import valet.digikom.com.valetparking.domain.ClosingBody;
import valet.digikom.com.valetparking.domain.ClosingData;
import valet.digikom.com.valetparking.domain.ClosingResponse;
import valet.digikom.com.valetparking.domain.ColorMasterResponse;
import valet.digikom.com.valetparking.domain.DefectResponse;
import valet.digikom.com.valetparking.domain.Disclaimer;
import valet.digikom.com.valetparking.domain.DropPointMasterResponse;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;
import valet.digikom.com.valetparking.domain.FineFee;
import valet.digikom.com.valetparking.domain.FinishCheckOut;
import valet.digikom.com.valetparking.domain.FinishCheckoutResponse;
import valet.digikom.com.valetparking.domain.GetReprintCheckinResponse;
import valet.digikom.com.valetparking.domain.MembershipResponse;
import valet.digikom.com.valetparking.domain.PatchMeBody;
import valet.digikom.com.valetparking.domain.PatchMeResponse;
import valet.digikom.com.valetparking.domain.PaymentMethod;
import valet.digikom.com.valetparking.domain.PostReprintCheckin;
import valet.digikom.com.valetparking.domain.PostReprintCheckinResponse;
import valet.digikom.com.valetparking.domain.TokenResponse;
import valet.digikom.com.valetparking.domain.ValetTypeJson;

/**
 * Created by dev on 1/7/17.
 */

public interface ApiEndpoint {

    @GET("defect_detail")
    Call<DefectResponse> getDefects(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("authenticate")
    Call<TokenResponse> getToken(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("authenticate")
    Call<AuthResponse> login(@Field("email") String email, @Field("password") String password);

    @GET("additional_item_site_detail")
    Call<AdditionalItemsResponse> getItems(@Query("page[size]") int pageSize, @Header("Authorization") String token);

    @GET("car_brand_master")
    Call<CarMasterResponse> getCars(@Query("page[size]") int pageSize, @Header("Authorization") String token);

    @GET("color_master")
    Call<ColorMasterResponse> getColors(@Query("page[size]") int pageSize, @Header("Authorization") String token);

    @GET("droppoint_floor_master")
    Call<DropPointMasterResponse> getDropPoints(@Header("Authorization") String token);

    @POST("ad_entry_checkin")
    Call<EntryCheckinResponse> postCheckin(@Body EntryCheckinContainer checkinContainer, @Header("Authorization") String token);

    @GET("ad_entry_checkin")
    Call<CheckinList> getCheckinList(@Query("page[size]") int pageSize, @Header("Authorization") String token);

    @GET("ad_entry_checkin_lobby")
    Call<CheckinList> getCurrentCheckinList(@Query("page[size]") int pageSize, @Header("Authorization") String token);

    @PUT("ad_car_call/{id}")
    Call<AddCarCallResponse> postCallCar(@Path("id") int id, @Body AddCarCallBody addCarCallBody, @Header("Authorization") String token);

    @GET("kg_entry_checkout")
    Call<EntryCheckoutCont> getCheckouts(@Query("page[size]")int pageSize, @Header("Authorization") String token);

    @GET("finefee_site_detail")
    Call<FineFee> getFineFees(@Header("Authorization") String token);

    @PUT("ad_checkout_finish_fine/{id}")
    Call<FinishCheckoutResponse> submitCheckout(@Path("id") int id, @Body FinishCheckOut finishCheckOut, @Header("Authorization") String token);

    @GET("valetfee_site_detail")
    Call<ValetTypeJson> getValetType(@Header("Authorization") String token);

    @GET("disclaimer_master")
    Call<Disclaimer> getDisclaimer(@Header("Authorization") String token);

    @GET("discount_site_detail")
    Call<MembershipResponse> getMemberships(@Header("Authorization") String token);

    //GET CLOSING DATA
    @GET("ad_checkout_finish_fine")
    Call<ClosingData> getClosingData(@Query("page[number]") int pageNumber,@Query("page[size]")int pageSize, @Header("Authorization") String token);

    //GET CLOSING DATA PER LOBBY
    @GET("ad_print_lobby")
    Call<ClosingData> getClosingDataLobby(@Query("page[size]")int pageSize, @Header("Authorization") String token);

    //GET CLOSING DATA PER SHIFT
    @GET("ad_print_shift")
    Call<ClosingData> getClosingDataShift(@Query("page[number]")int pageNumber, @Query("page[size]")int pageSize, @Header("Authorization") String token);

    //GET CLOSING DATA PER SITE
    @GET("ad_print_site")
    Call<ClosingData> getClosingDataSite(@Query("page[number]")int pageNumber, @Query("page[size]")int pageSize, @Header("Authorization") String token);

    // CLOSING EOD
    @POST("report_administrative")
    Call<ClosingResponse> close(@Body ClosingBody closingBody, @Header("Authorization") String token);

    // CHANGE SITE
    @PUT("me/set_role")
    Call<PatchMeResponse> patchMe(@Body PatchMeBody patchMeBody, @Header("Authorization") String token);

    // CANCEL TICKET
    @PUT("ad_valet_cancelation/{id}")
    Call<CancelResponse> cancelTicket(@Path("id") int id, @Body CancelBody cancelBody, @Header("Authorization") String token);

    // CHANGE PASSWORD
    @PUT("me/change_password")
    Call<ChangePasswordResponse> changePassWord(@Body ChangePassword changePassword, @Header("Authorization") String token);

    // GET PAYMENT METHODS
    @GET("payment_methods")
    Call<PaymentMethod> getPaymentMethods(@Header("Authorization") String token);

    // GET BANKS FOR PAYMENT METHOD REF
    @GET("bank_master")
    Call<Bank> getBanks(@Header("Authorization") String token);

    @GET
    Call<EntryCheckinResponse> getVthdTransactionItem(@Url String url, @Header("Authorization") String token);

    @POST("reprint_checkin")
    Call<PostReprintCheckinResponse> postReprint(@Body PostReprintCheckin reprintCheckin, @Header("Authorization") String token);

    @GET("reprint_checkin")
    Call<GetReprintCheckinResponse> getReprintData(@Query("page[number]") int pageNumber, @Query("page[size]") int pageSize,
                                                   @Query("filter") String filter, @Header("Authorization") String token);

    //@GET("reprint_checkin")
    //Call<GetReprintCheckinResponse> getReprintData(@Query("filter") String filter);


}
