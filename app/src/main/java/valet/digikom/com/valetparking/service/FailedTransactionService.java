package valet.digikom.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.dao.EntryCheckinContainerDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.FinishCheckoutDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.fragments.ParkedCarFragment;
import valet.digikom.com.valetparking.util.CheckinCheckoutAlarm;
import valet.digikom.com.valetparking.util.PrefManager;

/**
 * Created by DIGIKOM-EX4 on 2/28/2017.
 */

public class FailedTransactionService extends IntentService {
    public static final String TAG = FailedTransactionService.class.getSimpleName();

    public FailedTransactionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (ApiClient.isNetworkAvailable(this)){
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
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<EntryCheckinResponse> call = apiEndpoint.postCheckin(entryCheckinContainer);
                call.enqueue(new Callback<EntryCheckinResponse>() {
                    @Override
                    public void onResponse(Call<EntryCheckinResponse> call, Response<EntryCheckinResponse> response) {
                        if (response != null && response.body() != null) {
                            //int fakeVthdId = entryCheckinContainer.getEntryCheckin().getAttrib().getLastTicketCounter(); // fake vthd id diambil dari last ticket counter
                            String noTiket = response.body().getData().getAttribute().getNoTiket().trim();
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

                                /*
                                // update vthd id in checkout data if exist
                                int updateIdDataCheckout = FinishCheckoutDao.getInstance(FailedTransactionService.this)
                                        .updateCheckoutVthdId(noTiket, remoteVthdId);
                                */


                                // remove checkin data from db if succeed
                                /*EntryCheckinContainerDao containerDao = EntryCheckinContainerDao.getInstance(FailedTransactionService.this);
                                int deleteSuccess = containerDao.deleteById(String.valueOf(fakeVthdId));
                                Log.d(TAG,"remove failed-checkin " + deleteSuccess);
                                Log.d(TAG, "No. TICKET: " +  response.body().getData().getAttribute().getIdTransaksi());
                                */

                                EntryCheckinContainerDao.getInstance(FailedTransactionService.this)
                                        .deleteCheckinDataByTicketNo(noTiket);

                                // reload checkin list
                                Intent RTReturn = new Intent(ParkedCarFragment.RECEIVE_CURRENT_LOBBY_DATA);
                                LocalBroadcastManager.getInstance(FailedTransactionService.this).sendBroadcast(RTReturn);

                                startDownloadCurrentLobbyService();

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

                                Intent RTReturn = new Intent(ParkedCarFragment.RECEIVE_CURRENT_LOBBY_DATA);
                                LocalBroadcastManager.getInstance(FailedTransactionService.this).sendBroadcast(RTReturn);

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
}
