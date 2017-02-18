package valet.digikom.com.valetparking.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import valet.digikom.com.valetparking.domain.AuthResponse;

/**
 * Created by DIGIKOM-EX4 on 1/20/2017.
 */

public class PrefManager {

    public static final String KEY_DEFAULT_DROPPOINT = "drop_pint";
    public static final String KEY_DEFAULT_DROPPOINT_NAME = "drop_pint_name";
    public static final String KEY_SITE_NAME = "site";
    public static final String KEY_PRINTER_ADDRESS = "mac_addres";
    public static final String KEY_ID_SITE = "csmsid";

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

    public String getSiteName() {
        return sharedPreferences.getString(KEY_SITE_NAME, "");
    }

    public void setDefaultDropPoint(int idMasterDropPoint) {
        editor.putString(KEY_DEFAULT_DROPPOINT, String.valueOf(idMasterDropPoint));
        editor.commit();
    }

    public String getIdDefaultDropPoint() {
        String id = sharedPreferences.getString(KEY_DEFAULT_DROPPOINT, null);
        return id;
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
        String jsonAuthResponse = gson.toJson(authResponse);
        editor.putString(AuthResponse.KEY, jsonAuthResponse);
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


}
