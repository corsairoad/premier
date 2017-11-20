package valet.intan.com.valetparking.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import valet.intan.com.valetparking.domain.AuthResponse;

/**
 * Created by DIGIKOM-EX4 on 1/20/2017.
 */

public class PrefManager {

    public static final String KEY_DEFAULT_DROPPOINT = "drop_pint";
    public static final String KEY_DEFAULT_DROPPOINT_NAME = "drop_pint_name";
    public static final String KEY_SITE_NAME = "site";
    public static final String KEY_PRINTER_ADDRESS = "mac_addres";
    public static final String KEY_ID_SITE = "csmsid";
    public static final String KEY_DEVICE_ID = "device_id";
    public static final String KEY_APP_ID = "app_id";
    public static final String KEY_REMOTE_DEVICE_ID = "rem_dev_id";
    public static final String KEY_LAST_COUNTER_TICKET = "last_counter_ticket";
    public static final String KEY_LAST_PRINTED_TICKET_COUNTER = "last_printed_ticket_counter";
    public static final String KEY_LOBBY_TYPE = "key_lobby_type";
    public static final String KEY_TOKEN = "key_token";
    public static final String KEY_LOGGINGOUT = "key_logging_out";
    public static final String KEY_USER_ROLE_ID = "user.role.id";
    public static final String KEY_EXPIRED_TOKEN = "expired.token";
    public static final String KEY_DATE_LAST_LOGIN = "last.login";
    public static final String KEY_TOKEN_BACKUP = "token_backup";
    public static final String KEY_RELAUNCH_APP = "relaunch";

    private Context context;
    private static PrefManager prefManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private PrefManager(Context context) {
        this.context = context.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    public static PrefManager getInstance(Context context) {
        if (prefManager == null) {
            prefManager = new PrefManager(context);
        }
        return prefManager;
    }

    public void setSiteName(String name) {
        editor.putString(KEY_SITE_NAME, name);
        editor.commit();
    }

    public void saveLobbyType(int lobbyType) {
        editor.putInt(KEY_LOBBY_TYPE, lobbyType);
        editor.commit();
    }

    public int getLobbyType() {
        return sharedPreferences.getInt(KEY_LOBBY_TYPE, 0);
    }

    public String getSiteName() {
        return sharedPreferences.getString(KEY_SITE_NAME, "");
    }

    public void setDefaultDropPoint(int idMasterDropPoint) {
        editor.putString(KEY_DEFAULT_DROPPOINT, String.valueOf(idMasterDropPoint));
        editor.commit();
    }

    public String getIdDefaultDropPoint() {
        String id = sharedPreferences.getString(KEY_DEFAULT_DROPPOINT, "0");
        return id;
    }

    public void resetDefaultDropPoint(){
        editor.putString(KEY_DEFAULT_DROPPOINT, null);
        editor.putString(KEY_DEFAULT_DROPPOINT_NAME, null);
        editor.commit();
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, "Bearer " + token);
        editor.commit();
    }

    public void setRelaunch(boolean relaunch) {
        editor.putBoolean(KEY_RELAUNCH_APP, relaunch);
        editor.commit();
    }

    public boolean getRelaunch() {
        return sharedPreferences.getBoolean(KEY_RELAUNCH_APP, true);
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN,null);
    }

    public void saveBackupToken(String backupToken){
        editor.putString(KEY_TOKEN_BACKUP, backupToken);
        editor.commit();
    }

    public String getBackupToken() {
        return sharedPreferences.getString(KEY_TOKEN_BACKUP, null);
    }

    public void saveRemoteDeviceId(String remoteDevId) {
        editor.putString(KEY_REMOTE_DEVICE_ID, remoteDevId);
        editor.commit();
    }

    public String getRemoteDeviceId() {
        return sharedPreferences.getString(KEY_REMOTE_DEVICE_ID, "");
    }

    public void saveLastTicketCounter(int lastCountTicket) {
        editor.putInt(KEY_LAST_COUNTER_TICKET, lastCountTicket);
        editor.commit();
    }

    public int getLastTicketCounter() {
        return sharedPreferences.getInt(KEY_LAST_COUNTER_TICKET, 0);
    }

    public void saveLastPrintedTicketCounter(int lastPrinted) {
        editor.putInt(KEY_LAST_PRINTED_TICKET_COUNTER, lastPrinted);
        editor.commit();
    }

    public int getLastPrintedTicket() {
        return sharedPreferences.getInt(KEY_LAST_PRINTED_TICKET_COUNTER, 0);
    }

    public void saveDeviceAndAppId(String deviceId, String appId) {
        editor.putString(KEY_DEVICE_ID, deviceId);
        editor.putString(KEY_APP_ID, appId);
        editor.commit();
    }

    public String getDeviceId() {
        return sharedPreferences.getString(KEY_DEVICE_ID, " ");
    }

    public String getAppId() {
        return sharedPreferences.getString(KEY_APP_ID, " ");
    }

    public void setDefaultDropPointName(String name) {
        editor.putString(KEY_DEFAULT_DROPPOINT_NAME, name);
        editor.commit();
    }

    public String getDefaultDropPointName() {
        return sharedPreferences.getString(KEY_DEFAULT_DROPPOINT_NAME, "");
    }

    public void setPrinterMacAddress(String macAddress) {
        editor.putString(KEY_PRINTER_ADDRESS,macAddress);
        editor.commit();
    }

    public String getPrinterMacAddress() {
        return sharedPreferences.getString(KEY_PRINTER_ADDRESS,null);
    }

    public void saveAuthResponse(AuthResponse authResponse) {
        if (authResponse == null) {
            editor.putString(AuthResponse.KEY, null);
        }else {
            String jsonAuthResponse = gson.toJson(authResponse);
            editor.putString(AuthResponse.KEY, jsonAuthResponse);
        }

        editor.commit();
    }

    public AuthResponse getAuthResponse() {
        String authRes = sharedPreferences.getString(AuthResponse.KEY,null);
        AuthResponse authResponse = gson.fromJson(authRes, AuthResponse.class);
        return authResponse;
    }

    public String getUserName() {
        AuthResponse authResponse = getAuthResponse();
        if (authResponse != null) {
            AuthResponse.Data.User user = authResponse.getData().getUser();
            return user.getUserName();
        }
        return null;
    }

    public String getUserEmail() {
        AuthResponse authResponse = getAuthResponse();
        if (authResponse != null) {
            AuthResponse.Data.User user = authResponse.getData().getUser();
            return user.getUserEmail();
        }
        return null;
    }

    public void savePassword(String pwd) {
        editor.putString(AuthResponse.KEY_PASSWORD, pwd).commit();
    }

    public String getPassWord() {
        return sharedPreferences.getString(AuthResponse.KEY_PASSWORD, null);
    }

    public void logoutUser() {
        editor.remove(AuthResponse.KEY);
        editor.commit();
    }

    public void setIdSite(int id) {
        editor.putInt(KEY_ID_SITE, id);
        editor.commit();
    }

    public int getIdSite() {
        return  sharedPreferences.getInt(KEY_ID_SITE, 0);
    }

    public boolean isLoggingOut(){
        return sharedPreferences.getBoolean(KEY_LOGGINGOUT, false);
    }

    public void setLoggingOut(boolean loggingOut) {
        editor.putBoolean(KEY_LOGGINGOUT, loggingOut);
        editor.commit();
    }

    public void setUserRoleId(int id){
        editor.putInt(KEY_USER_ROLE_ID, id);
        editor.commit();
    }

    public int getUserRoleId() {
        return sharedPreferences.getInt(KEY_USER_ROLE_ID,20);
    }

    //  expdate parameter in long converted to string
    public void setExpiredDateToken(String expDate) {
        editor.putString(KEY_EXPIRED_TOKEN, expDate);
        editor.commit();
    }

    public String getExpiredToken() {
        return sharedPreferences.getString(KEY_EXPIRED_TOKEN, null);
    }

    // date parameter in String : MM/dd/yy hh:mm:ss
    public void saveLastLoginDate(String date) {
        editor.putString(KEY_DATE_LAST_LOGIN, date);
        editor.commit();
    }

    public String getLastLoginDate() {
        return sharedPreferences.getString(KEY_DATE_LAST_LOGIN, null);
    }

    public void setLastLoginDateToCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date date = new Date();
        String lastLogin = dateFormat.format(date);

        saveLastLoginDate(lastLogin);
    }



}
