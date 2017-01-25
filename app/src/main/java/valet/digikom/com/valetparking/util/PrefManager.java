package valet.digikom.com.valetparking.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by DIGIKOM-EX4 on 1/20/2017.
 */

public class PrefManager {

    public static final String KEY_DEFAULT_DROPPOINT = "drop_pint";
    public static final String KEY_PRINTER_ADDRESS = "mac_addres";

    private Context context;
    private static PrefManager prefManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private  final String PREF_NAME = PrefManager.class.getSimpleName();


    private PrefManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static PrefManager getInstance(Context context) {
        if (prefManager == null) {
            prefManager = new PrefManager(context);
        }
        return prefManager;
    }

    public void setDefaultDropPoint(int idMasterDropPoint) {
        editor.putInt(KEY_DEFAULT_DROPPOINT, idMasterDropPoint);
        editor.commit();
    }

    public int getIdDefaultDropPoint() {
        int id = sharedPreferences.getInt(KEY_DEFAULT_DROPPOINT, -1);
        return id;
    }

    public void setPrinterMacAddress(String macAddress) {
        editor.putString(KEY_PRINTER_ADDRESS,macAddress);
        editor.commit();
    }

    public String getPrinterMacAddress() {
        return sharedPreferences.getString(KEY_PRINTER_ADDRESS,null);
    }
}
