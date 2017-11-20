package valet.intan.com.valetparking.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.Main2Activity;
import valet.intan.com.valetparking.domain.ClosingBody;
import valet.intan.com.valetparking.domain.ClosingResponse;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.LoggingUtils;
import valet.intan.com.valetparking.service.ProcessRequest;

/**
 * Created by DIGIKOM-EX4 on 2/13/2017.
 */

public class ClosingDao{

    private Context context;
    private static ClosingDao closingDao;

    private ClosingDao(Context context) {
        this.context = context.getApplicationContext();
    }

    public static ClosingDao getInstance(Context context){
        if (closingDao == null) {
            closingDao = new ClosingDao(context);
        }
        return closingDao;
    }

    public void close(String readInfo, String startDate, String endDate) {
        ClosingBody.Data.Attr attr = new ClosingBody.Data.Attr();
        attr.setReadInfo(readInfo);
        attr.setReadStartDate(startDate);
        attr.setReadEndDate(endDate);

        ClosingBody.Data data = new ClosingBody.Data();
        data.setAttr(attr);

        final ClosingBody closingBody = new ClosingBody();
        closingBody.setData(data);
        Gson gson = new Gson();
        String datax = gson.toJson(closingBody);
        Log.d("json closing", datax);

        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                Call<ClosingResponse> call = apiEndpoint.close(closingBody, token);
                call.enqueue(new Callback<ClosingResponse>() {
                    @Override
                    public void onResponse(Call<ClosingResponse> call, Response<ClosingResponse> response) {
                        if (response != null && response.body() != null) {
                            Toast.makeText(context, "Closing success", Toast.LENGTH_SHORT).show();
                            EntryDao.getInstance(context).deleteUncheckedOutEntry();

                            // remove all synced checkout data
                            FinishCheckoutDao.getInstance(context).removeAllSyncedCheckout();
                            LoggingUtils.getInstance(context).logEODSucceed();
                        }else {
                            Toast.makeText(context, "Closing failed", Toast.LENGTH_SHORT).show();
                            LoggingUtils.getInstance(context).logEODFailed(response.code(), response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ClosingResponse> call, Throwable t) {
                        Toast.makeText(context, "Closing failed", Toast.LENGTH_SHORT).show();
                        LoggingUtils.getInstance(context).logEODError(t.getMessage());
                    }
                });
            }
        }, context);
    }


}
