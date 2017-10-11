package valet.intan.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.dao.EntryCheckinContainerDao;
import valet.intan.com.valetparking.dao.EntryDao;
import valet.intan.com.valetparking.dao.FinishCheckoutDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.domain.CheckinList;
import valet.intan.com.valetparking.domain.EntryCheckinContainer;
import valet.intan.com.valetparking.domain.EntryCheckinResponse;
import valet.intan.com.valetparking.fragments.ParkedCarFragment;

/**
 * Created by DIGIKOM-EX4 on 3/21/2017.
 */

public class DownloadCurrentLobbyService extends IntentService {

    private static final String TAG = DownloadCurrentLobbyService.class.getSimpleName();
    public static final String ACTION_DOWNLOAD = "premier.valet.download.current.lobby";

    public DownloadCurrentLobbyService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Service Download data current lobby called");
        if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            LoggingUtils.getInstance(this).logServiceAddCheckinFromOtherDeviceCalled();

            TokenDao.getToken(new ProcessRequest() {
                @Override
                public void process(String token) {
                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                    Call<CheckinList> call = apiEndpoint.getCurrentCheckinList(200, token);
                    call.enqueue(new Callback<CheckinList>() {
                        @Override
                        public void onResponse(Call<CheckinList> call, Response<CheckinList> response) {
                            if (response != null && response.body() != null) {
                                List<EntryCheckinResponse.Data> downloadedCheckinList = response.body().getCheckinResponseList();

                                EntryDao.getInstance(DownloadCurrentLobbyService.this)
                                        .insertListCheckin(downloadedCheckinList);

                                //Update fakeVthdId to remoteVthdId in post checkout table that remains fakeVthdId
                                FinishCheckoutDao.getInstance(DownloadCurrentLobbyService.this)
                                        .updateCheckoutVthdId(downloadedCheckinList);

                                reloadParkedCarsList();

                                // delete checkin data to prevent double checkin.
                                removeCheckinDataToPost(downloadedCheckinList);
                            } else  {
                                // 401 = token expired. then force logout
                                int httpResponseCode = response.code();
                                if (httpResponseCode == 401) {
                                    RefreshTokenService.startActionRefreshToken(DownloadCurrentLobbyService.this);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CheckinList> call, Throwable t) {

                        }
                    });
                }
            }, this);

            startDownloadCheckoutService();
        }
    }

    private void removeCheckinDataToPost(List<EntryCheckinResponse.Data> downloadedCheckinList) {
        EntryCheckinContainerDao checkinDao = EntryCheckinContainerDao.getInstance(this);
        for (EntryCheckinResponse.Data data : downloadedCheckinList) {
            String noTiket = data.getAttribute().getNoTiket();
            checkinDao.deleteCheckinDataByTicketNo(noTiket);
        }
    }

    private void startDownloadCheckoutService() {
        Intent intent = new Intent(this, DownloadCheckoutService.class);
        intent.setAction(DownloadCheckoutService.ACTION);
        startService(intent);
    }

    private void reloadParkedCarsList() {
        Intent RTReturn = new Intent(ParkedCarFragment.RECEIVE_CURRENT_LOBBY_DATA);
        LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn);
    }
}
