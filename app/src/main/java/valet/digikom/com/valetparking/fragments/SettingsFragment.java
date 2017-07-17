package valet.digikom.com.valetparking.fragments;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import valet.digikom.com.valetparking.R;

/**
 * Created by DIGIKOM-EX4 on 12/13/2016.
 */

public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_premier);
    }
}
