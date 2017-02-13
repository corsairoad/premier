package valet.digikom.com.valetparking;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClosingActivity extends AppCompatActivity implements View.OnClickListener{

    EditText inputDateFrom;
    EditText inputDateUntil;
    Button btnSetFrom;
    Button btnSetUntil;
    private static final String TAG = ClosingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing);

        btnSetFrom = (Button) findViewById(R.id.btn_set_from);
        btnSetUntil = (Button) findViewById(R.id.btn_set_until);
        inputDateFrom = (EditText) findViewById(R.id.input_date_from);
        inputDateUntil = (EditText) findViewById(R.id.input_date_until);

        btnSetFrom.setOnClickListener(this);
        btnSetUntil.setOnClickListener(this);

        inputDateFrom.setText(getCurrentDate());
        inputDateUntil.setText(getCurrentDate());
    }

    @Override
    public void onClick(final View viewx) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dp = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                               @Override
                                                               public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                                                   if (viewx == btnSetFrom) {
                                                                        inputDateFrom.setText(getDateString(year, monthOfYear,dayOfMonth));
                                                                   }else {
                                                                       inputDateUntil.setText(getDateString(year, monthOfYear,dayOfMonth));
                                                                   }
                                                               }
                                                           },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dp.show(getFragmentManager(), TAG);
    }

    private String getDateString(int year, int month, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        return sdf.format(calendar.getTime());
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return getDateString(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }


}
