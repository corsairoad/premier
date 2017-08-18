package valet.intan.com.valetparking.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import valet.intan.com.valetparking.R;

/**
 * Created by fadlymunandar on 8/18/17.
 */

public class SyncCustomDialog extends AppCompatDialog implements DialogInterface {

    private MaterialProgressBar progressBar;
    private TextView textProgress;

    public SyncCustomDialog(Context context) {
        super(context);
    }

    public SyncCustomDialog(Context context, int theme) {
        super(context, theme);
    }

    protected SyncCustomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sync_custom_dialog);

        progressBar = (MaterialProgressBar) findViewById(R.id.progressbar_sync);
        textProgress = (TextView) findViewById(R.id.txt_sync_progress);
    }

    public void setMessage(String message) {
        textProgress.setText(message);
    }

    public void setErrorMessage(String message) {
        progressBar.setVisibility(View.GONE);
        textProgress.setText(message);
    }
}
