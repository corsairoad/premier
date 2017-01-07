package valet.digikom.com.valetparking.service;

import retrofit2.Call;
import retrofit2.http.GET;
import valet.digikom.com.valetparking.domain.DefectResponse;

/**
 * Created by dev on 1/7/17.
 */

public interface ApiEndpoint {

    @GET("defect_master")
    Call<DefectResponse> getDefects();

}
