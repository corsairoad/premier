package valet.intan.com.valetparking.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.SplashActivity;
import valet.intan.com.valetparking.dao.AuthResDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.domain.AuthResponse;
import valet.intan.com.valetparking.util.CheckinCheckoutAlarm;
import valet.intan.com.valetparking.util.DownloadCheckinAlarm;
import valet.intan.com.valetparking.util.PrefManager;
import valet.intan.com.valetparking.util.RefreshTokenAlarm;


public class RefreshTokenService extends IntentService {
    private static final String TAG = RefreshTokenService.class.getSimpleName();
    public static final String ACTION_REFRESH_TOKEN = "valet.intan.com.valetparking.service.action.refresh.token";
    public static final int RESPONSE_TOKEN_EXPIRED = 401;
    public static final int RESPONSE_INVALID_TOKEN_REQUEST = 400;
    public static final int RESPONSE_INTERNAL_SERVER_ERROR = 500;
    private LoggingUtils loggingUtils;

    public RefreshTokenService() {
        super(TAG);
    }


    public static void startActionRefreshToken(Context context) {
        Intent intent = new Intent(context, RefreshTokenService.class);
        intent.setAction(ACTION_REFRESH_TOKEN);

        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loggingUtils = LoggingUtils.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REFRESH_TOKEN.equals(action)) {
                Log.d(TAG, "Refresh token service called");
                if (ApiClient.isNetworkAvailable(RefreshTokenService.this)) {
                    handleActionRefreshToken();
                }else {
                    //resetRefreshTokenAlarm();
                    loggingUtils.logRefreshTokenError("No internet connection");
                }
            }
        }
    }

    private void handleActionRefreshToken() {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(final String token) {
                Log.d(TAG, "Connecting to server. old Token: " + token);
                loggingUtils.logOldToken(token);

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class);
                Call<AuthResponse.MetaContainer> call = apiEndpoint.refreshToken(token);
                call.enqueue(new Callback<AuthResponse.MetaContainer>() {
                    @Override
                    public void onResponse(Call<AuthResponse.MetaContainer> call, Response<AuthResponse.MetaContainer> response) {
                        if (response != null && response.body() != null) {
                            String token2 = response.body().getData().getMeta().getToken();
                            String expDate = response.body().getData().getMeta().getExpiredDate();

                            logIfEquals(token, token2);

                            if (token2 != null && expDate != null) {
                                saveToBackupToken(token2);
                                saveTokenAndExpDate(token2, expDate);
                                resetRefreshTokenAlarm();

                                Log.d(TAG, "refresh token succeed. new token: " + token2);

                                loggingUtils.logRefreshTokenSucceed(token2);

                            }else {
                                loggingUtils.logIfTokenOrExpDateNull(token2, expDate);
                            }
                        } else {
                            if (response != null) {
                                handleErrorResponseCode(response.code(), token, response);
                                loggingUtils.logRefreshTokenFailed(response.message(), response.code());
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<AuthResponse.MetaContainer> call, Throwable t) {
                        forceLogout(token);
                        Log.d(TAG, "onFailure: refresh token failure occured: " + t.getMessage());
                        loggingUtils.logRefreshTokenError(t.getMessage());
                    }
                });
            }
        }, this);
    }

    private void logIfEquals(String token, String token2) {
        if (token.equalsIgnoreCase(token2)) {
            loggingUtils.logNewTokenEqualsToTheLastOne(token,token2);
        }
    }

    private  void handleErrorResponseCode(int code, String token, Response<AuthResponse.MetaContainer> response) {
        switch (code) {
            case RESPONSE_TOKEN_EXPIRED:
                handleTokenExpired(token, response);
                break;
            case RESPONSE_INVALID_TOKEN_REQUEST:
                replaceTokenWithBackupToken();
                break;
            case RESPONSE_INTERNAL_SERVER_ERROR:
                //resetRefreshTokenAlarm();
                break;
            default:
                //resetRefreshTokenAlarm();
                break;

        }
    }

    private void replaceTokenWithBackupToken() {
        PrefManager prefManager = PrefManager.getInstance(this);
        String backupToken = prefManager.getBackupToken();
        String currentToken = prefManager.getToken();

        if (backupToken != null && !backupToken.equals(currentToken)) {
            prefManager.saveToken(backupToken);
            loggingUtils.logReplaceCurrentTokenWithBackupOne();
        }
    }

    private void saveTokenAndExpDate(String token2, String expDate){
        PrefManager prefManager = PrefManager.getInstance(this);
        prefManager.saveToken(token2);
        prefManager.setExpiredDateToken(expDate);
        prefManager.setLastLoginDateToCurrentDate();
    }

    private void resetRefreshTokenAlarm() {
        RefreshTokenAlarm refreshTokenAlarm = RefreshTokenAlarm.getInstance(this);
        refreshTokenAlarm.startAlarmIn5Days();
    }

    public void forceLogout(String token) {
        PrefManager prefManager = PrefManager.getInstance(this);
        prefManager.logoutUser();
        prefManager.setLoggingOut(false);
        prefManager.setPrinterMacAddress(null);

        logout(token);
        stopAllService();
        goToSplash();
        stopSelf();
    }

    private void goToSplash() {
        PrefManager.getInstance(this).saveAuthResponse(null);
        Intent intent = new Intent(this,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SplashActivity.KEY_EXTRA_FORCE_LOGOUT, true);
        startActivity(intent);
    }

    private void stopAllService() {
        CheckinCheckoutAlarm checkinCheckoutAlarm = CheckinCheckoutAlarm.getInstance(this);
        DownloadCheckinAlarm downloadCheckinAlarm = DownloadCheckinAlarm.getInstance(this);
        RefreshTokenAlarm refreshTokenAlarm = RefreshTokenAlarm.getInstance(this);

        checkinCheckoutAlarm.cancelAlarm();
        downloadCheckinAlarm.cancelAlarm();
        refreshTokenAlarm.cancelAlarm();
    }

    private void logout(final String token) {
        ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
        Call<ResponseBody> call = apiEndpoint.logout(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int code = response.code();
                if (code == AuthResDao.HTTP_STATUS_LOGOUT_SUKSES) {
                    Log.d(TAG, "onResponse: logging out to server succeed");
                    PrefManager prefManager = PrefManager.getInstance(RefreshTokenService.this);
                    prefManager.logoutUser();
                    prefManager.setLoggingOut(false);
                    prefManager.setPrinterMacAddress(null);
                    forceLogout(token);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void handleTokenExpired(String token, Response<AuthResponse.MetaContainer> response) {
        try {
            forceLogout(token);
            assert response != null;
            Log.d(TAG, "onResponse: refresh token failed: " + response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToBackupToken(String newToken) {
        PrefManager.getInstance(this).saveBackupToken("Bearer " + newToken);
    }

}
