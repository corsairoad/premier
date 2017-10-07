package valet.intan.com.valetparking;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;
import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.dao.DropDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.domain.ChangePassword;
import valet.intan.com.valetparking.domain.ChangePasswordResponse;
import valet.intan.com.valetparking.domain.DropPointMaster;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.MyLifecycleHandler;
import valet.intan.com.valetparking.util.ObjectToJson;
import valet.intan.com.valetparking.util.ValetDbHelper;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class PreferenceActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    private ListPreference prefDropPoint;

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        MyLifecycleHandler.relaunchAppIfNotVisible(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getAction() != null && intent.getAction().equals("valet.digikom.com.valetparking.PreferenceActivity")) {
            if (ApiClient.isNetworkAvailable(this)) {
                new DownloadDataTask().execute();
            }else {
                Toast.makeText(this, "Can't sync data. Please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        } else if (intent.getAction() != null && intent.getAction().equals("valet.digikom.com.valetparking.PreferenceActivity.change.password")) {
            changePwx();
        }
    }

    private void changePwx() {
        new MaterialDialog.Builder(this)
                .title("Change Password")
                .customView(R.layout.layout_change_password_dialog, false)
                .positiveText("Change")
                .positiveColor(Color.parseColor("#009688"))
                .negativeText("Cancel")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Toast.makeText(PreferenceActivity.this, "Changing password...", Toast.LENGTH_SHORT).show();
                        View view = dialog.getCustomView();
                        EditText inputOldPwx = (EditText) view.findViewById(R.id.input_old_password);
                        EditText inputNewPwx = (EditText) view.findViewById(R.id.input_new_password);
                        EditText inputRetype = (EditText) view.findViewById(R.id.input_retype_password);

                        String oldPwx = inputOldPwx.getText().toString();
                        String newPwx = inputNewPwx.getText().toString();
                        String retype = inputRetype.getText().toString();

                        final ChangePassword changePassword = new ChangePassword();
                        ChangePassword.Data data = new ChangePassword.Data();
                        ChangePassword.Data.Attr atr = new ChangePassword.Data.Attr();
                        atr.setOldPassword(oldPwx);
                        atr.setNewPassword(newPwx);
                        atr.setRetypePassword(retype);
                        data.setAttr(atr);
                        changePassword.setData(data);

                        String json = ObjectToJson.getJson(changePassword);

                        TokenDao.getToken(new ProcessRequest() {
                            @Override
                            public void process(String token) {
                                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                                Call<ChangePasswordResponse> call = apiEndpoint.changePassWord(changePassword, token);
                                call.enqueue(new Callback<ChangePasswordResponse>() {
                                    @Override
                                    public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                                        if (response != null && response.body() != null) {
                                            ChangePasswordResponse res = response.body();
                                            Toast.makeText(PreferenceActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                                        Toast.makeText(PreferenceActivity.this, "error occured while changing password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }, PreferenceActivity.this);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private class DownloadDataTask extends AsyncTask<Void, Void, Void> {
        ACProgressFlower dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
             dialog = new ACProgressFlower.Builder(PreferenceActivity.this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text("Syncing data...")
                    .textSize(14)
                    .fadeColor(Color.DKGRAY).build();
            dialog.show();
            Toast.makeText(PreferenceActivity.this, "Syncing data...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ApiClient.downloadData(PreferenceActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(PreferenceActivity.this, "Sync data completed", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        addPreferencesFromResource(R.xml.pref_premier);

        prefDropPoint = (ListPreference)findPreference(getString(R.string.pref_drop_point_key));
        //new FetchDropPointTask().execute();
    }

    private class FetchDropPointTask extends AsyncTask<Void, Void,List<DropPointMaster>> {

        @Override
        protected List<DropPointMaster> doInBackground(Void... voids) {
            DropDao dropDao = DropDao.getInstance(ValetDbHelper.getInstance(PreferenceActivity.this));
            return dropDao.fetchAllDropPoints();
        }

        @Override
        protected void onPostExecute(List<DropPointMaster> dropPointMasters) {
            super.onPostExecute(dropPointMasters);
            if (dropPointMasters != null && !dropPointMasters.isEmpty()) {
                CharSequence[] entries = new CharSequence[dropPointMasters.size()];
                CharSequence[] entryValues = new CharSequence[dropPointMasters.size()];

                int a=0;
                for (DropPointMaster drop : dropPointMasters) {
                    entries[a] = drop.getAttrib().getDropName();
                    entryValues[a] = String.valueOf(drop.getAttrib().getDropId());
                    a++;
                }

                if (entries.length > 0) {
                    prefDropPoint.setEntries(entries);
                }

                if (entryValues.length > 0) {
                    prefDropPoint.setEntryValues(entryValues);
                }
            }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }
    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), PreferenceActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), PreferenceActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), PreferenceActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
