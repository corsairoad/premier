package valet.intan.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.dao.FinishCheckoutDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.domain.CheckoutData;
import valet.intan.com.valetparking.domain.FinishCheckOut;
import valet.intan.com.valetparking.domain.FinishCheckoutResponse;

/**
 * Created by DIGIKOM-EX4 on 3/22/2017.
 */

public class PostCheckoutService extends IntentService {

    private static final String TAG = PostCheckoutService.class.getSimpleName();
    public static final String ACTION = "premier.valet.post.checkout.data";
    private LoggingUtils loggingUtils;

    private FinishCheckoutDao checkoutDao;
    private Call<FinishCheckoutResponse> call;

    public PostCheckoutService() {
        super(TAG);
        checkoutDao = FinishCheckoutDao.getInstance(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loggingUtils = LoggingUtils.getInstance(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ACTION.equalsIgnoreCase(intent.getAction())) {
            Log.d(TAG, "Post Checkout service called");
            loggingUtils.logPostCheckoutServiceCalled();

            List<CheckoutData> checkoutDataList = checkoutDao.getCheckoutData();
            if (!checkoutDataList.isEmpty()) {
                loggingUtils.logTotalCheckoutDataToPost(checkoutDataList.size());

                for (CheckoutData data : checkoutDataList) {
                    post(data);
                }
            }else {
                Log.d(TAG, "data checkout empty");
                loggingUtils.logPostCheckoutEmpty();
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            //call.cancel();
        }
    }

    private void post(CheckoutData data) {
        final int remoteVthdId = data.getRemoteVthdId();
        final FinishCheckOut finishCheckOut = toObject(data.getJsonData());
        final String noTiket = data.getNoTiket();
        if (finishCheckOut != null) {
            Log.d(TAG, "posting data checkout");
            loggingUtils.logPostCheckoutData(data);

            TokenDao.getToken(new ProcessRequest() {
                @Override
                public void process(String token) {
                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                    call = apiEndpoint.submitCheckout(remoteVthdId,finishCheckOut, token);
                    call.enqueue(new Callback<FinishCheckoutResponse>() {
                        @Override
                        public void onResponse(Call<FinishCheckoutResponse> call, Response<FinishCheckoutResponse> response) {
                            if (response != null && response.body() != null) {
                                Log.d(TAG, "Checkout from bg service success. VthdId: " + remoteVthdId + ", Tiket:" + noTiket);
                                loggingUtils.logPostCheckoutSucceed(noTiket);
                                //checkoutDao.deleteDatabyRemoteId(remoteVthdId);
                                int statusUpdate = checkoutDao.updateStatus(remoteVthdId, FinishCheckoutDao.STATUS_SYNCED);
                                if (statusUpdate > 0) {
                                    Log.d(TAG, "Checkout data synced. VthdId: " + remoteVthdId + ", Tiket:" + noTiket);
                                }else {
                                    Log.d(TAG, "Sync Checkout data failed. VthdId: " + remoteVthdId + ", Tiket:" + noTiket);
                                }
                            }else {
                                Log.d(TAG, "Checkout from bg service failed. VthdId: " + remoteVthdId + ", Tiket:" + noTiket);
                                loggingUtils.logPostCheckoutFailed(noTiket, remoteVthdId, response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<FinishCheckoutResponse> call, Throwable t) {
                            Log.d(TAG, "Checkout from bg service error. VthdId: " + remoteVthdId);
                            loggingUtils.logPostCheckoutError(noTiket, t.getMessage());
                        }
                    });

                }
            }, PostCheckoutService.this);
        }

    }

    private FinishCheckOut toObject(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json,FinishCheckOut.class);
    }

}
