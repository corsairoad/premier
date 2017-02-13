package valet.digikom.com.valetparking.dao;

import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;

/**
 * Created by DIGIKOM-EX4 on 2/13/2017.
 */

public class ClosingDao implements ProcessRequest {
    @Override
    public void process(String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
    }
}
