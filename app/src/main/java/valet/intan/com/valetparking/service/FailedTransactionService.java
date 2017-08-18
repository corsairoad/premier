package valet.intan.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.dao.EntryCheckinContainerDao;
import valet.intan.com.valetparking.dao.EntryDao;
import valet.intan.com.valetparking.dao.FinishCheckoutDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.domain.EntryCheckinContainer;
import valet.intan.com.valetparking.domain.EntryCheckinResponse;
import valet.intan.com.valetparking.fragments.ParkedCarFragment;
import valet.intan.com.valetparking.util.CheckinCheckoutAlarm;
import valet.intan.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 2/28/2017.
 */

public class FailedTransactionService extends IntentService {

    public static final String TAG = FailedTransactionService.class.getSimpleName();

    Call<EntryCheckinResponse> call;

    public FailedTransactionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "post checkin service called");
        if (ApiClient.isNetworkAvailable(this)){

            if (PrefManager.getInstance(this).isLoggingOut()) {
                Log.d(TAG, "post checkin checkout service canceled because app is logging out");
                stopSelf();
                return;
            }

            EntryCheckinContainerDao containerDao = EntryCheckinContainerDao.getInstance(this);
            List<EntryCheckinContainer> containers = containerDao.fetchAll();

            if (!containers.isEmpty()) {
                for (EntryCheckinContainer container : containers) {
                    postCheckin(container);
                }
            }

            //startDownloadCurrentLobbyService();
            startPostCheckoutService();

            Log.d(TAG, "STARTED");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(call != null) {
            //call.cancel();
        }
    }

    private void startDownloadCurrentLobbyService() {
        // different process service
        if (PrefManager.getInstance(this).getLobbyType() == 0) {
            Intent intent = new Intent(this, DownloadCurrentLobbyService.class);
            intent.setAction(DownloadCurrentLobbyService.ACTION_DOWNLOAD);
            startService(intent);
            //downloadCurrentLobbyDataService();
        }
    }

    private void startPostCheckoutService() {
        Intent intent = new Intent(this, PostCheckoutService.class);
        intent.setAction(PostCheckoutService.ACTION);
        startService(intent);
    }

    private void downloadCurrentLobbyDataService() {
        Intent intent = new Intent(this, DownloadCurrentLobbyService.class);
        intent.setAction(DownloadCurrentLobbyService.ACTION_DOWNLOAD);
        startService(intent);
    }

    private void cancelAlarm() {
        CheckinCheckoutAlarm checkinCheckoutAlarm = CheckinCheckoutAlarm.getInstance(this);
        checkinCheckoutAlarm.cancelAlarm();
        Log.d(TAG, "CANCELED");
    }

    private void postCheckin(final EntryCheckinContainer entryCheckinContainer) throws NumberFormatException {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {

                // for debugging
                convertToJson(entryCheckinContainer);

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                call = apiEndpoint.postCheckin(entryCheckinContainer, token);
                call.enqueue(new Callback<EntryCheckinResponse>() {
                    @Override
                    public void onResponse(Call<EntryCheckinResponse> call, Response<EntryCheckinResponse> response) {
                        if (response != null && response.body() != null) {
                            //int fakeVthdId = entryCheckinContainer.getEntryCheckin().getAttrib().getLastTicketCounter(); // fake vthd id diambil dari last ticket counter
                            String noTiket = response.body().getData().getAttribute().getNoTiket().trim().replace(" ", "");
                            int remoteVthdId = response.body().getData().getAttribute().getId();
                            String tiketSeq = response.body().getData().getAttribute().getIdTransaksi();
                            try{
                                int fakeVthdId = Integer.parseInt(entryCheckinContainer.getEntryCheckin().getId());
                                int lastTicketCounter = response.body().getData().getAttribute().getLastTicketCounter();
                                Log.d(TAG, "TICKET C0UNTER "+ lastTicketCounter);

                                PrefManager.getInstance(FailedTransactionService.this).saveLastTicketCounter(lastTicketCounter);

                                // update vthd id and transaction id (not ticket number)
                                int updateSuccess = EntryDao.getInstance(FailedTransactionService.this)
                                        .updateRemoteAndTicketSequenceId(String.valueOf(fakeVthdId), remoteVthdId, tiketSeq);


                                // update vthd id in checkout data if exist
                                int updateIdDataCheckout = FinishCheckoutDao.getInstance(FailedTransactionService.this)
                                        .updateCheckoutVthdId(noTiket, remoteVthdId);


                                // remove checkin data from db if succeed
                                /*EntryCheckinContainerDao containerDao = EntryCheckinContainerDao.getInstance(FailedTransactionService.this);
                                int deleteSuccess = containerDao.deleteById(String.valueOf(fakeVthdId));
                                Log.d(TAG,"remove failed-checkin " + deleteSuccess);
                                Log.d(TAG, "No. TICKET: " +  response.body().getData().getAttribute().getIdTransaksi());
                                */

                                EntryCheckinContainerDao.getInstance(FailedTransactionService.this)
                                        .deleteCheckinDataByTicketNo(noTiket);

                                // reload checkin list
                                reloadCheckinList();

                                //startDownloadCurrentLobbyService();

                                Log.d("Post Checkin", noTiket + " successfully posted");

                            }catch (Exception e) {
                                e.printStackTrace();
                                // remove checkin data from db
                                Log.d("Post Checkin from error", noTiket + " successfully posted");
                                EntryCheckinContainerDao.getInstance(FailedTransactionService.this)
                                        .deleteCheckinDataByTicketNo(noTiket);

                                // update synced checkin item in checkin list
                                EntryDao.getInstance(FailedTransactionService.this)
                                        .updateRemoteAndTicketSecByTicketNo(noTiket, remoteVthdId, tiketSeq);

                                reloadCheckinList();

                                startDownloadCurrentLobbyService();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EntryCheckinResponse> call, Throwable t) {

                    }
                });
            }
        }, this);
    }

    private void convertToJson(EntryCheckinContainer entryCheckinContainer){
        Gson gson = new Gson();
        String json = gson.toJson(entryCheckinContainer);
        Log.d("Post Checkin json", json);
    }

    private void reloadCheckinList() {
        Intent RTReturn = new Intent(ParkedCarFragment.RECEIVE_CURRENT_LOBBY_DATA);
        LocalBroadcastManager.getInstance(FailedTransactionService.this).sendBroadcast(RTReturn);
    }
}
