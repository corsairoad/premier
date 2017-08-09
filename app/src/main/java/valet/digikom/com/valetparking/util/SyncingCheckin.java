package valet.digikom.com.valetparking.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import valet.digikom.com.valetparking.dao.EntryCheckinContainerDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.FinishCheckoutDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.fragments.ParkedCarFragment;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.FailedTransactionService;
import valet.digikom.com.valetparking.service.ProcessRequest;

/**
 * Created by fadlymunandar on 8/5/17.
 */

public class SyncingCheckin extends IntentService {

    private static final String TAG = SyncingCheckin.class.getSimpleName();
    public static final String ACTION = "premier.valet.post.checkin";
    public static final String ACTION_ERROR_RESPONSE = "premier.valet.post.error";
    public static final String EXTRA = "premier.message.sync.extra";

    private int totalcheckinToPost = 0;
    int count = 1;
    private Call<EntryCheckinResponse> call;

    public SyncingCheckin() {
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

    public void sync() {
        EntryCheckinContainerDao checkinDao = EntryCheckinContainerDao.getInstance(this);
        List<EntryCheckinContainer> checkins = checkinDao.fetchAll();

        if (checkins.isEmpty()) {
            startSyncChekoutService();
            return;
        }

        totalcheckinToPost = checkins.size();
        sendMessage(ACTION,"Posting data checkin 0/" + totalcheckinToPost);

        for (final EntryCheckinContainer checkin : checkins) {
            postCheckin(checkin,count);
            count++;
        }

    }

    private void reloadCheckinList() {
        // reload checkin list
        Intent RTReturn = new Intent(ParkedCarFragment.RECEIVE_CURRENT_LOBBY_DATA);
        LocalBroadcastManager.getInstance(SyncingCheckin.this).sendBroadcast(RTReturn);
    }

    private void postCheckin(final EntryCheckinContainer checkin, final int count) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {

                String noTiket = null;
                int remoteVthdId = 0;
                String tiketSeq = null;

                try {
                    ApiEndpoint endpoint = ApiClient.createService(ApiEndpoint.class);
                    call = endpoint.postCheckin(checkin, token);

                    Response<EntryCheckinResponse> httpResponse = call.execute();
                    EntryCheckinResponse response = httpResponse.body();

                    if (response != null) {
                        noTiket = response.getData().getAttribute().getNoTiket().trim();
                        remoteVthdId = response.getData().getAttribute().getId();
                        tiketSeq = response.getData().getAttribute().getIdTransaksi();

                        String fakeVthdString = checkin.getEntryCheckin().getId();
                        int fakeVthdId = Integer.parseInt(fakeVthdString);
                        int lastTicketCounter = response.getData().getAttribute().getLastTicketCounter();

                        Log.d(TAG, "TICKET C0UNTER "+ lastTicketCounter);
                        PrefManager.getInstance(SyncingCheckin.this).saveLastTicketCounter(lastTicketCounter);

                        // update vthd id and transaction id (not ticket number)
                        int updateSuccess = EntryDao.getInstance(SyncingCheckin.this)
                                .updateRemoteAndTicketSequenceId(String.valueOf(fakeVthdId), remoteVthdId, tiketSeq);

                        // update vthd id in checkout data if exist
                        int updateIdDataCheckout = FinishCheckoutDao.getInstance(SyncingCheckin.this)
                                .updateCheckoutVthdId(noTiket, remoteVthdId);

                        EntryCheckinContainerDao.getInstance(SyncingCheckin.this)
                                .deleteCheckinDataByTicketNo(noTiket);

                        sendMessage(ACTION,"Posting data checkin " + count + "/" + totalcheckinToPost);

                        reloadCheckinList();
                        reloadCheckinList();
                        reloadCheckinList();

                        if (count == totalcheckinToPost) {
                            startSyncChekoutService();
                        }

                    } else {
                        //int code = httpResponse.code();
                        //String message = "Login " + httpResponse.message();
                        //sendMessage(ACTION_ERROR_RESPONSE, message + " " + code);
                        //stopSelf();
                    }

                } catch (IOException e) {
                    PrefManager.getInstance(SyncingCheckin.this).setLoggingOut(false);
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    // remove checkin data from db
                    if (noTiket != null && remoteVthdId >0 && tiketSeq != null) {
                        Log.d("Post Checkin from error", noTiket + " successfully posted");


                        EntryCheckinContainerDao.getInstance(SyncingCheckin.this)
                                .deleteCheckinDataByTicketNo(noTiket);

                        // update vthd id in checkout data if exist
                        int updtateCheckoutVthdId = FinishCheckoutDao.getInstance(SyncingCheckin.this)
                                .updateCheckoutVthdId(noTiket, remoteVthdId);

                        // update synced checkin item in checkin list
                        int updateRemoteTicketSeq = EntryDao.getInstance(SyncingCheckin.this)
                                .updateRemoteAndTicketSecByTicketNo(noTiket, remoteVthdId, tiketSeq);

                        reloadCheckinList();

                        sendMessage(ACTION,"Posting data checkin " + count + "/" + totalcheckinToPost);

                        if (count == totalcheckinToPost) {
                            startSyncChekoutService();
                        }
                    }
                }
            }
        }, this);

    }

    private void sendMessage(String action,String message){
        Intent intent = new Intent(action);
        intent.putExtra(EXTRA, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void startSyncChekoutService() {
        Intent intent = new Intent(this, SyncingCheckout.class);
        intent.setAction(SyncingCheckout.ACTION);
        startService(intent);
    }
}