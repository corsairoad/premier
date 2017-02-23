package valet.digikom.com.valetparking.dao;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.domain.ClosingBody;
import valet.digikom.com.valetparking.domain.ClosingResponse;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;

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
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<ClosingResponse> call = apiEndpoint.close(closingBody);
                call.enqueue(new Callback<ClosingResponse>() {
                    @Override
                    public void onResponse(Call<ClosingResponse> call, Response<ClosingResponse> response) {
                        if (response != null && response.body() != null) {
                            Toast.makeText(context, "Closing success", Toast.LENGTH_SHORT).show();
                            EntryDao.getInstance(context).deleteUncheckedOutEntry();
                        }else {
                            Toast.makeText(context, "Closing failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ClosingResponse> call, Throwable t) {
                        Toast.makeText(context, "Closing failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, context);
    }


}
