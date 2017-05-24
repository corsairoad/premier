package valet.digikom.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.ClosingActivity;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.ClosingData;

/**
 * Created by DIGIKOM-EX11 on 5/23/2017.
 */

public class DownloadClosingDataService extends IntentService {
    private static final String TAG = DownloadClosingDataService.class.getSimpleName();
    public static final String ACTION = "premier.valet.download.closing.data";

    public DownloadClosingDataService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ACTION.equals(intent.getAction())) {

        }
    }

    private void downloadData(final String flag, final int currentPage, final int DATA_PER_PAGE) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<ClosingData> call = apiEndpoint.getClosingData(currentPage, DATA_PER_PAGE);
                if (flag != null) {
                    switch (flag) {
                        case ClosingActivity.DOWNLOAD_PER_SHIFT:
                            call = apiEndpoint.getClosingDataShift(currentPage, DATA_PER_PAGE);
                            break;
                        case ClosingActivity.DOWNLOAD_PER_SITE:
                            call = apiEndpoint.getClosingDataSite(currentPage, DATA_PER_PAGE);
                            break;
                        default:
                            break;
                    }
                }

                call.enqueue(new Callback<ClosingData>() {
                    @Override
                    public void onResponse(Call<ClosingData> call, Response<ClosingData> response) {
                        if (response != null && response.body() != null) {
                            //calculateTotalPage(response.body());
                            //updateListClosing(response.body().getDataList());
                        }else {
                            Toast.makeText(DownloadClosingDataService.this, "Can not download closing data. Please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ClosingData> call, Throwable t) {
                        //Toast.makeText(ClosingActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        if (t.getMessage().contains(" ECONNRESET")){
                            Log.d(TAG, t.getMessage());
                            //downloadData(ClosingActivity.DOWNLOAD_PER_LOBBY);
                        } else {
                        }

                    }
                });
            }
        }, DownloadClosingDataService.this);
    }
}
