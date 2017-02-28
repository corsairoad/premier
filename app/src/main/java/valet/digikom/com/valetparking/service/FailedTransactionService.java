package valet.digikom.com.valetparking.service;

import android.app.IntentService;
import android.content.Intent;
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
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.EntryCheckinContainer;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.util.CheckinCheckoutAlarm;

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

            if (containers.isEmpty()) {
                cancelAlarm();
                return;
            }

            for (EntryCheckinContainer container : containers) {
                postCheckin(container);
            }
        }

        Log.d(TAG, "START");
    }

    private void cancelAlarm() {
        CheckinCheckoutAlarm checkinCheckoutAlarm = CheckinCheckoutAlarm.getInstance(this);
        checkinCheckoutAlarm.cancelAlarm();
    }

    private void postCheckin(final EntryCheckinContainer entryCheckinContainer) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<EntryCheckinResponse> call = apiEndpoint.postCheckin(entryCheckinContainer);
                call.enqueue(new Callback<EntryCheckinResponse>() {
                    @Override
                    public void onResponse(Call<EntryCheckinResponse> call, Response<EntryCheckinResponse> response) {
                        if (response != null && response.body() != null) {
                            EntryCheckinContainerDao containerDao = EntryCheckinContainerDao.getInstance(FailedTransactionService.this);
                            containerDao.deleteById(entryCheckinContainer.getEntryCheckin().getId());
                            Log.d(TAG,"post failed transaction success");
                        }else {
                            //debugJsonCheckin(entryCheckinContainer);
                            Log.d(TAG,"post failed transaction fail");
                        }
                    }

                    @Override
                    public void onFailure(Call<EntryCheckinResponse> call, Throwable t) {

                    }
                });
            }
        }, this);
    }

    private void debugJsonCheckin(EntryCheckinContainer entryCheckinContainer) {
        Gson gson = new Gson();
        String jsonEntryCheckin = gson.toJson(entryCheckinContainer);
        exportToFile(jsonEntryCheckin);
        Log.d("JSON CHECKIN", jsonEntryCheckin);

    }

    private void exportToFile(String json) {
        try {
            File myFile = new File("/sdcard/checkincontainer.txt");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
            myOutWriter.append(json);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(this,"Done writing SD 'mysdfile.txt'", Toast.LENGTH_SHORT).show();

        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
