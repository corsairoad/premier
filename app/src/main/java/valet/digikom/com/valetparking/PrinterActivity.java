package valet.digikom.com.valetparking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.epson.epos2.Epos2Exception;
import com.epson.epos2.Log;
import com.epson.epos2.printer.Printer;
import com.epson.epos2.printer.PrinterStatusInfo;
import com.epson.epos2.printer.ReceiveListener;
import com.epson.eposprint.EposException;
import com.epson.epsonio.EpsonIoException;
import com.google.gson.Gson;

import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.PrintCheckin;
import valet.digikom.com.valetparking.domain.PrintReceiptTest;
import valet.digikom.com.valetparking.domain.ShowMsg;
import valet.digikom.com.valetparking.domain.SpnModelsItem;
import valet.digikom.com.valetparking.util.PrefManager;

public class PrinterActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext = null;
    private EditText mEditTarget = null;
    private Spinner mSpnSeries = null;
    private Spinner mSpnLang = null;
    private Button btnPrintCheckin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnPrintCheckin = (Button) findViewById(R.id.btn_test_checkin);
        btnPrintCheckin.setOnClickListener(this);

        mContext = this;

        int[] target = {
                R.id.btnDiscovery,
                R.id.btnSampleReceipt,
                R.id.btnSampleCoupon
        };

        for (int i = 0; i < target.length; i++) {
            Button button = (Button)findViewById(target[i]);
            button.setOnClickListener(this);
        }

        mSpnSeries = (Spinner)findViewById(R.id.spnModel);
        ArrayAdapter<SpnModelsItem> seriesAdapter = new ArrayAdapter<SpnModelsItem>(this, android.R.layout.simple_spinner_item);
        seriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t88), Printer.TM_T88));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m10), Printer.TM_M10));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_m30), Printer.TM_M30));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p20), Printer.TM_P20));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60), Printer.TM_P60));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p60ii), Printer.TM_P60II));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_p80), Printer.TM_P80));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t20), Printer.TM_T20));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t60), Printer.TM_T60));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t70), Printer.TM_T70));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t81), Printer.TM_T81));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t82), Printer.TM_T82));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t83), Printer.TM_T83));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90), Printer.TM_T90));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_t90kp), Printer.TM_T90KP));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u220), Printer.TM_U220));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_u330), Printer.TM_U330));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_l90), Printer.TM_L90));
        seriesAdapter.add(new SpnModelsItem(getString(R.string.printerseries_h6000), Printer.TM_H6000));
        mSpnSeries.setAdapter(seriesAdapter);
        mSpnSeries.setSelection(0);

        mSpnLang = (Spinner)findViewById(R.id.spnLang);
        ArrayAdapter<SpnModelsItem> langAdapter = new ArrayAdapter<SpnModelsItem>(this, android.R.layout.simple_spinner_item);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_ank), Printer.MODEL_ANK));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_japanese), Printer.MODEL_JAPANESE));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_chinese), Printer.MODEL_CHINESE));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_taiwan), Printer.MODEL_TAIWAN));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_korean), Printer.MODEL_KOREAN));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_thai), Printer.MODEL_THAI));
        langAdapter.add(new SpnModelsItem(getString(R.string.lang_southasia), Printer.MODEL_SOUTHASIA));
        mSpnLang.setAdapter(langAdapter);
        mSpnLang.setSelection(0);

        mEditTarget = (EditText)findViewById(R.id.edtTarget);
        initTarget();
    }

    private void initTarget() {
        String targetAddress = PrefManager.getInstance(this).getPrinterMacAddress();
        if (targetAddress != null) {
            mEditTarget.setText(targetAddress);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
        if (data != null && resultCode == RESULT_OK) {
            String target = data.getStringExtra(getString(R.string.title_target));
            if (target != null) {
                PrefManager.getInstance(this).setPrinterMacAddress(target);
                EditText mEdtTarget = (EditText)findViewById(R.id.edtTarget);
                mEdtTarget.setText("TCP:"+target);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()) {
            case R.id.btnDiscovery:
                intent = new Intent(this, DiscoveryActivity.class);
                startActivityForResult(intent, 0);
                break;

            case R.id.btnSampleReceipt:
                updateButtonState(false);
                if (!TextUtils.isEmpty(PrefManager.getInstance(this).getPrinterMacAddress())) {
                    printTestReceipt();
                }else {
                    Toast.makeText(this, "please discover printer first", Toast.LENGTH_SHORT).show();
                }
                if (v == btnPrintCheckin) {
                    return;
                }
                break;
            default:
                // Do nothing
                break;
        }
    }

    private void printCheckin(final EntryCheckinResponse response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PrintCheckin printCheckin = new PrintCheckin(PrinterActivity.this, response,null,null, null);
                printCheckin.print();
            }
        });

    }

    private void updateButtonState(boolean state) {
        Button btnReceipt = (Button)findViewById(R.id.btnSampleReceipt);
        Button btnCoupon = (Button)findViewById(R.id.btnSampleCoupon);
        btnReceipt.setEnabled(state);
        btnCoupon.setEnabled(state);
    }



    private void printTestReceipt() {
        PrintReceiptTest printReceiptTest = new PrintReceiptTest(PrinterActivity.this);
        printReceiptTest.buildPrintData();
        updateButtonState(true);

    }
}
