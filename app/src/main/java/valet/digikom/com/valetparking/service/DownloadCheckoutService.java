package valet.digikom.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.ClosingData;
import valet.digikom.com.valetparking.domain.FinishCheckoutResponse;
import valet.digikom.com.valetparking.fragments.ParkedCarFragment;

/**
 * Created by fadlymunandar on 7/31/17.
 */

public class DownloadCheckoutService extends IntentService {


    private static final String TAG_NAME = DownloadCheckoutService.class.getSimpleName();
    public static final String ACTION = "premier.valet.download.checkout.data.from.server";

    public DownloadCheckoutService() {
        super(TAG_NAME);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && ACTION.equalsIgnoreCase(intent.getAction())) {
            downloadCheckoutData();
        }

    }

    private void downloadCheckoutData() {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                Call<ClosingData> call = apiEndpoint.getCheckoutDataFromServer(200,createFilter(), token);
                call.enqueue(new Callback<ClosingData>() {
                    @Override
                    public void onResponse(Call<ClosingData> call, Response<ClosingData> response) {
                        if (response != null && response.body() != null) {
                            List<ClosingData.Data> checkoutData  = response.body().getDataList();

                            EntryDao.getInstance(DownloadCheckoutService.this).setCheckoutCar(checkoutData);

                            reloadParkedCarsList();
                        }
                    }

                    @Override
                    public void onFailure(Call<ClosingData> call, Throwable t) {

                    }
                });
            }
        }, this);
    }

    private String createFilter() {
            TimeZone tz = TimeZone.getTimeZone("GMT+7");
            Calendar cal1 = Calendar.getInstance(tz);
            cal1.set(Calendar.HOUR_OF_DAY, 8);
            cal1.set(Calendar.MINUTE, 0);
            cal1.set(Calendar.SECOND, 0);

            Calendar cal2 = Calendar.getInstance(tz);

            Date date1 = cal1.getTime();
            Date date2 = cal2.getTime();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()); // Quoted "Z" to indicate UTC, no timezone offset
            df.setTimeZone(tz);

            String dateFrom = df.format(date1);
            String dateTo = df.format(date2);

            return String.format("and(ge(vthdManCOTime,%s),le(vthdManCOTime,%s))", dateFrom, dateTo);
            //return "and(ge(vthdManCOTime,2017-06-08T00:00:00Z),le(vthdManCOTime,2017-06-08T23:59:59Z))";
    }

    private void reloadParkedCarsList() {
        Intent RTReturn = new Intent(ParkedCarFragment.RECEIVE_CURRENT_LOBBY_DATA);
        LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn);
    }
}
