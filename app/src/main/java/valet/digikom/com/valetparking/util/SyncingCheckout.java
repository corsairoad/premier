package valet.digikom.com.valetparking.util;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import valet.digikom.com.valetparking.dao.FinishCheckoutDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.CheckoutData;
import valet.digikom.com.valetparking.domain.FinishCheckOut;
import valet.digikom.com.valetparking.domain.FinishCheckoutResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;

/**
 * Created by fadlymunandar on 8/5/17.
 */

public class SyncingCheckout extends IntentService {

    private static final String TAG = SyncingCheckout.class.getSimpleName();
    public static final String ACTION = "premier.valet.post.checkout";
    public static final String EXTRA = "premier.message.sync.extra";
    public static final String ACTION_LOGOUT = "premier.valet.logout";
    public static final String ACTION_LOGOUT_ERROR_RESPONSE = "premier.error.checkout";

    private int totalCheckoutData;
    private int count = 1;
    private Call<FinishCheckoutResponse> call;

    public SyncingCheckout() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ACTION.equalsIgnoreCase(intent.getAction())) {
            sync();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
        }
    }

    private void sync() {
        FinishCheckoutDao checkoutDao = FinishCheckoutDao.getInstance(this);
        List<CheckoutData> checkoutDataList = checkoutDao.getCheckoutData();

        totalCheckoutData = checkoutDataList.size();
        String message = "Synchronizing checkout data 0/" + totalCheckoutData;

        sendMessage(ACTION, message);

        if (!checkoutDataList.isEmpty()) {

            sendMessage(ACTION, "Synchronizing checkout data " + count + "/" + totalCheckoutData);

            for (CheckoutData data : checkoutDataList) {
                postCheckout(data);
                count++;
            }

        } else {
            sendMessage(ACTION_LOGOUT, null);
        }
    }

    private void postCheckout(final CheckoutData data) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {

                try {
                    int remoteVthdId = data.getRemoteVthdId();
                    FinishCheckOut finishCheckOut = toObject(data.getJsonData());
                    String noTiket = data.getNoTiket();

                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                    call = apiEndpoint.submitCheckout(remoteVthdId,finishCheckOut, token);

                    Response<FinishCheckoutResponse> httpResponse = call.execute();
                    FinishCheckoutResponse checkoutResponse = httpResponse.body();

                    if (checkoutResponse != null) {
                        Log.d(TAG, "Checkout succeed. VthdId: " + remoteVthdId + ", Tiket:" + noTiket);

                        FinishCheckoutDao checkoutDao = FinishCheckoutDao.getInstance(SyncingCheckout.this);

                        int statusUpdate = checkoutDao.updateStatus(remoteVthdId, FinishCheckoutDao.STATUS_SYNCED);

                        if (statusUpdate > 0) {
                            Log.d(TAG, "Checkout data synced. VthdId: " + remoteVthdId + ", Tiket:" + noTiket);
                        }else {
                            Log.d(TAG, "Sync Checkout data failed. VthdId: " + remoteVthdId + ", Tiket:" + noTiket);
                        }

                        if (count == totalCheckoutData) {
                            sendMessage(ACTION_LOGOUT, null);
                        }

                        sendMessage(ACTION, "Synchronizing checkout data " + count + "/" + totalCheckoutData);
                    }else {
                        int code = httpResponse.code();
                        String message = "Logout " + httpResponse.message();
                        sendMessage(ACTION_LOGOUT_ERROR_RESPONSE, message + " " + code);
                        stopSelf();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, this);
    }

    private void sendMessage(String action, String extra) {
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA, extra);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private FinishCheckOut toObject(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json,FinishCheckOut.class);
    }
}
