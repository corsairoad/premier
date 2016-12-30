package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.SlideInUnderneathAnimation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int DELAY_TIME = 2000;
    LinearLayout linearLayout;
    EditText inputEmail;
    EditText inputPassword;
    FirebaseAuth auth;
    Button btnLogin;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        linearLayout = (LinearLayout) findViewById(R.id.container_input);
        auth = FirebaseAuth.getInstance();
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        //startMainActivity();
    }

    protected void startMainActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //startActivity(new Intent(SplashActivity.this, MainActivity.class));
                //finish();
                linearLayout.setVisibility(View.VISIBLE);
                new SlideInUnderneathAnimation(linearLayout).setDirection(Animation.DIRECTION_DOWN).animate();
            }
        }, DELAY_TIME);
    }

    @Override
    public void onClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(SplashActivity.this, R.string.login_empty,Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            Toast.makeText(SplashActivity.this, R.string.login_error,Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(SplashActivity.this, Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}
