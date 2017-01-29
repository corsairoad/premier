package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import valet.digikom.com.valetparking.dao.AuthResDao;
import valet.digikom.com.valetparking.dao.CarDao;
import valet.digikom.com.valetparking.dao.ColorDao;
import valet.digikom.com.valetparking.dao.DefectDao;
import valet.digikom.com.valetparking.dao.DropDao;
import valet.digikom.com.valetparking.dao.FineFeeDao;
import valet.digikom.com.valetparking.dao.ItemsDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.util.PrefManager;
import valet.digikom.com.valetparking.util.ValetDbHelper;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, AuthResDao.OnAuthListener {

    LinearLayout linearLayout;
    EditText inputEmail;
    EditText inputPassword;
    Button btnLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (PrefManager.getInstance(this).getAuthResponse() != null) {
            goToMain();
            return;
        }

        linearLayout = (LinearLayout) findViewById(R.id.container_input);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }

    private void downloadData() {
        ValetDbHelper dbHelper = ValetDbHelper.getInstance(this);
        TokenDao.getToken(DefectDao.getInstance(dbHelper), this);
        TokenDao.getToken(ItemsDao.getInstance(dbHelper), this);
        TokenDao.getToken(CarDao.getInstance(dbHelper), this);
        TokenDao.getToken(ColorDao.getInstance(dbHelper), this);
        TokenDao.getToken(DropDao.getInstance(dbHelper), this);
        TokenDao.getToken(FineFeeDao.getInstance(this), this);

    }

    @Override
    public void onClick(View view) {
        progressBar.setVisibility(View.VISIBLE);

        final String email = inputEmail.getText().toString().trim();
        final String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SplashActivity.this, R.string.login_empty,Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadData();

                AuthResDao authResDao = AuthResDao.getInstance(SplashActivity.this);
                authResDao.login(email, password, SplashActivity.this);
            }
        }).run();

    }

    @Override
    public void loginFailed() {
        progressBar.setVisibility(View.GONE);
        inputEmail.setText("");
        inputPassword.setText("");
    }

    @Override
    public void loginSuccess() {
        goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(this, Main2Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
