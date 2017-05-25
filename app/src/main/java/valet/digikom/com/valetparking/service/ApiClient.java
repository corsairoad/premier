package valet.digikom.com.valetparking.service;

import android.content.Context;
import android.net.ConnectivityManager;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import valet.digikom.com.valetparking.dao.BankDao;
import valet.digikom.com.valetparking.dao.CarDao;
import valet.digikom.com.valetparking.dao.ColorDao;
import valet.digikom.com.valetparking.dao.DefectDao;
import valet.digikom.com.valetparking.dao.DisclaimerDao;
import valet.digikom.com.valetparking.dao.DropDao;
import valet.digikom.com.valetparking.dao.FineFeeDao;
import valet.digikom.com.valetparking.dao.ItemsDao;
import valet.digikom.com.valetparking.dao.PaymentDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.dao.ValetTypeDao;
import valet.digikom.com.valetparking.util.ValetDbHelper;

/**
 * Created by dev on 1/7/17.
 */

public class ApiClient {
    //private static final String BASE_URL = "http://premier.intelligence.id/v1/";
    private static final String BASE_URL = "http://dev.premier.intelligence.id/v1/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false);

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    public static  Retrofit getClient() {
       return builder.build();
    }

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static <S> S createService(Class<S> serviceClass, final String authToken) {
        if (authToken != null) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    return onOnIntercept(chain, authToken);
                }
            });
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.interceptors().add(logging);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    private static Response onOnIntercept(Interceptor.Chain chain, String authToken) throws IOException{
        try {
            Request original = chain.request();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + authToken)
                    .method(original.method(), original.body());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return chain.proceed(chain.request());
    }

    public static void downloadData(Context context)  {
        ValetDbHelper dbHelper = ValetDbHelper.getInstance(context);
        TokenDao.getToken(DefectDao.getInstance(dbHelper), context);
        TokenDao.getToken(ItemsDao.getInstance(dbHelper), context);
        TokenDao.getToken(CarDao.getInstance(dbHelper), context);
        TokenDao.getToken(ColorDao.getInstance(dbHelper), context);
        TokenDao.getToken(DropDao.getInstance(dbHelper), context);
        TokenDao.getToken(FineFeeDao.getInstance(context), context);
        TokenDao.getToken(ValetTypeDao.getInstance(context),context);
        TokenDao.getToken(PaymentDao.getInstance(context), context);
        TokenDao.getToken(BankDao.getInstance(context), context);
        TokenDao.getToken(DisclaimerDao.getInstance(context), context);
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
