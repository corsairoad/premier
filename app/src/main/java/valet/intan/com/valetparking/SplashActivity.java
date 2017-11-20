package valet.intan.com.valetparking;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.UUID;
import valet.intan.com.valetparking.dao.AuthResDao;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.LoggingUtils;
import valet.intan.com.valetparking.util.MyLifecycleHandler;
import valet.intan.com.valetparking.util.PrefManager;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, AuthResDao.OnAuthListener, View.OnLongClickListener {

    public static final String KEY_EXTRA_FORCE_LOGOUT = "com.valet.force.logout";

    private LinearLayout linearLayout;
    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView txtVersion;
    private TextView txtForceLogout;
    private ImageView logoSplash;

    private String email;
    private String password;

    private LoggingUtils loggingUtils;
    private static boolean RELAUNCH = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        PrefManager prefManager = PrefManager.getInstance(this);
        prefManager.setRelaunch(true);

        if (prefManager.getAuthResponse() != null) {
            goToMain();
            return;
        }

        loggingUtils = LoggingUtils.getInstance(this);

        linearLayout = (LinearLayout) findViewById(R.id.container_input);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        txtVersion = (TextView) findViewById(R.id.text_app_version);
        txtForceLogout = (TextView) findViewById(R.id.text_notif);
        logoSplash = (ImageView) findViewById(R.id.logo_splash);
        logoSplash.setOnLongClickListener(this);

        setTextVersion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleIntent(getIntent());
        requestPermissionForWriteToDisk();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyLifecycleHandler.relaunchAppIfNotVisible(this);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            boolean isForcedLogout = intent.getBooleanExtra(KEY_EXTRA_FORCE_LOGOUT, false);
            if (isForcedLogout) {
                txtForceLogout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setTextVersion() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            txtVersion.setText("Versi " + packageInfo.versionName);
            txtVersion.setOnLongClickListener(this);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void requestPermissionForDevId() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)  != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
        }else {
            if (saveDeviceAndAppId()){
                proceessToLogin();
            }
        }
    }

    private void requestPermissionForWriteToDisk() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2:{
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (saveDeviceAndAppId()) {
                        proceessToLogin();
                    }
                }
                break;
            }

        }
    }

    private boolean saveDeviceAndAppId() {
        TelephonyManager telephonyManager = (android.telephony.TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        String appId = UUID.randomUUID().toString();

        if (TextUtils.isEmpty(deviceId)) {
            showErrorDialog("IMEI Problem", "Unable to get device IMEI, please check your internet connection");
            progressBar.setVisibility(View.GONE);
            return false;
        }

        PrefManager.getInstance(this).saveDeviceAndAppId(deviceId, appId);
        return true;
    }


    @Override
    public void onClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SplashActivity.this, R.string.login_empty,Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                requestPermissionForDevId();

            }
        }).run();

    }

    @Override
    public void loginFailed(int messageCode, String messg) {
        btnLogin.setEnabled(true);
        String title = "Login failed";
        String message = "";
        switch (messageCode) {
            case AuthResDao.HTTP_STATUS_LOGIN_FORBIDDEN:
                message = messg;
                break;
            case AuthResDao.HTTP_STATUS_LOGIN_INVALID:
                message = "Username or password incorrect";
                break;
            case AuthResDao.HTTP_STATUS_LOGIN_ERR_CONN:
                message = "Connection problem";
                break;
            case AuthResDao.HTTP_STATUS_LOGIN_ERR_ROLE:
                message = "Unauthorized user";
                break;
            case AuthResDao.HTTP_STATUS_LOGIN_ERR_RESPONSE:
                message = "Error response occurred";
                break;
        }

        loggingUtils.logLoginError(email, message);

        progressBar.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_error_outline)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        inputEmail.setText("");
                        inputPassword.setText("");
                        dialog.dismiss();
                    }
                });
        builder.show();

    }

    @Override
    public void loginSuccess() {
        btnLogin.setEnabled(true);
        PrefManager prefManager = PrefManager.getInstance(this);
        loggingUtils.logLoginSucced(email);
        loggingUtils.logToken(prefManager.getToken());
        loggingUtils.logExpiredDate(prefManager.getExpiredToken());

        ApiClient.downloadData(this);
        goToMain();
    }

    private void goToMain() {

        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showErrorDialog(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setIcon(R.drawable.ic_error_outline);
        builder.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void proceessToLogin() {
        AuthResDao authResDao = AuthResDao.getInstance(SplashActivity.this);
        authResDao.login(email, password, SplashActivity.this);
        loggingUtils.logLogin(email);
    }

    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        if (id == logoSplash.getId() || id == txtVersion.getId()){
            PrefManager.getInstance(this).setRelaunch(false);
            finishAffinity();
        }
        return false;
    }
}
