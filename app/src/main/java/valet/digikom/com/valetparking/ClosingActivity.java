package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.adapter.ListClosingAdapter;
import valet.digikom.com.valetparking.dao.ClosingDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.ClosingData;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.PrintClosingParam;
import valet.digikom.com.valetparking.domain.PrintReceiptClosing;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.PrefManager;

public class ClosingActivity extends AppCompatActivity implements View.OnClickListener, ListClosingAdapter.OnClosingItemClickListener{

    private static final String TAG = ClosingActivity.class.getSimpleName();
    private static final int OPEN = 1;
    private static final int CLOSING = 2;

    EditText inputDateFrom;
    EditText inputDateUntil;
    EditText inputRemark;
    Button btnSetFrom;
    Button btnSetUntil;
    List<ClosingData.Data> closingData = new ArrayList<>();
    ListClosingAdapter closingAdapter;
    RecyclerView listClosingView;
    TextView textRegular;
    TextView textExclusive;
    TextView textTotal;
    MaterialProgressBar progressBar;
    Button btnClosing;

    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    int mSecond;
    int reg;
    int exc;
    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing);

        progressBar = (MaterialProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        textRegular = (TextView) findViewById(R.id.text_regular);
        textExclusive = (TextView) findViewById(R.id.text_exclusive);
        textTotal = (TextView) findViewById(R.id.text_total);
        btnSetFrom = (Button) findViewById(R.id.btn_set_from);
        btnSetUntil = (Button) findViewById(R.id.btn_set_until);
        inputDateFrom = (EditText) findViewById(R.id.input_date_from);
        inputDateUntil = (EditText) findViewById(R.id.input_date_until);
        inputRemark = (EditText) findViewById(R.id.input_remark);
        btnClosing = (Button) findViewById(R.id.btn_closing);
        btnClosing.setOnClickListener(this);
        listClosingView = (RecyclerView) findViewById(R.id.list_closing_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listClosingView.setLayoutManager(layoutManager);
        closingAdapter = new ListClosingAdapter(closingData, this);
        listClosingView.setAdapter(closingAdapter);

        btnSetFrom.setOnClickListener(this);
        btnSetUntil.setOnClickListener(this);

        inputDateFrom.setText(getCurrentDate(OPEN));
        inputDateUntil.setText(getCurrentDate(CLOSING));

        new DownloadClosingDataTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private synchronized void close() {
        PrefManager prefManager = PrefManager.getInstance(this);
        String lobbyName = prefManager.getDefaultDropPointName();
        String siteName = prefManager.getSiteName();
        ClosingDao closingDao = ClosingDao.getInstance(this);
        String readInfo = inputRemark.getText().toString();
        String startDate = inputDateFrom.getText().toString();
        String endDate = inputDateUntil.getText().toString();
        closingDao.close(readInfo,startDate,endDate);

        /*
        ------------ PRINTER CARA LAMA
        PrintClosing printClosing = new PrintClosing(this, closingData, lokasi, siteName, startDate,endDate,"Admin", reg,exc,total);
        printClosing.print();
        */

        // CREATE CLOSING PARAMETER
        PrintClosingParam closingParam = new PrintClosingParam.Builder()
                .setClosingData(closingData)
                .setSiteName(siteName)
                .setLobbyName(lobbyName)
                .setDateFrom(startDate)
                .setDateTo(endDate)
                .setAdminName("Admin")
                .setNumRegular(reg)
                .setNumExClusive(exc)
                .setNumTotal(total)
                .build();

        // BUILD AND PRINT CLOSING DATA
        PrintReceiptClosing printReceiptClosing = new PrintReceiptClosing(this, closingParam);
        printReceiptClosing.buildPrintData();
    }

    @Override
    public void onClick(final View viewx) {
        if (viewx == btnClosing) {
            if (closingData.isEmpty()) {
                Toast.makeText(this, "Closing data empty. Closing aborted.", Toast.LENGTH_SHORT).show();
                return;
            }
            new SweetAlertDialog(this)
                    .setTitleText("Closing")
                    .setContentText("Closing transaction now?")
                    .setConfirmText("Yes")
                    .setCancelText("No")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            close();
                            new DownloadClosingDataTask().execute();
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    })
                    .showCancelButton(true)
                    .show();
        }else {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog tp = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    mSecond = second;
                    if (viewx == btnSetFrom) {
                        inputDateFrom.setText(getDateString(mYear, mMonth, mDay, mHour, mMinute, mSecond));
                    }else {
                        inputDateUntil.setText(getDateString(mYear, mMonth, mDay, mHour, mMinute, mSecond));
                    }
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            tp.show(getFragmentManager(), TAG);

            DatePickerDialog dp = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                                   @Override
                                                                   public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                                                       mYear = year;
                                                                       mMonth = monthOfYear;
                                                                       mDay = dayOfMonth;
                                                                   }
                                                               },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dp.show(getFragmentManager(), TAG);

        }

    }

    private String getDateString(int year, int month, int day, int hour, int minute, int second) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return sdf.format(calendar.getTime());
    }

    private String getCurrentDate(int flag) {
        Calendar calendar = Calendar.getInstance();
        if (flag ==  OPEN) {
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }else  {
            calendar.set(Calendar.HOUR_OF_DAY, 22);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }
        return getDateString(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
    }

    @Override
    public void OnClosingItemClick(int vthdId) {
        Intent intent = new Intent(this, ParkedCarDetailActivity.class);
        intent.putExtra(EntryCheckinResponse.ID_ENTRY_CHECKIN, vthdId);
        intent.setAction("DETAIL_FOR_CLOSING_ITEM");
        startActivity(intent);
    }

    private class DownloadClosingDataTask extends AsyncTask<Void, Void, List<ClosingData.Data>> {

        @Override
        protected List<ClosingData.Data> doInBackground(Void... voids) {
            TokenDao.getToken(new ProcessRequest() {
                @Override
                public void process(String token) {
                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                    Call<ClosingData> call = apiEndpoint.getClosingData(900);
                    call.enqueue(new Callback<ClosingData>() {
                        @Override
                        public void onResponse(Call<ClosingData> call, Response<ClosingData> response) {
                            if (response != null && response.body() != null) {
                                updateListClosing(response.body().getDataList());
                            }else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(ClosingActivity.this, "Can not download closing data. Please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ClosingData> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ClosingActivity.this, "Can not download closing data. Please try again later", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }, ClosingActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(List<ClosingData.Data> datas) {
            super.onPostExecute(datas);
        }
    }

    private void updateListClosing(final List<ClosingData.Data> datas) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                countValetType(datas);
                closingData.clear();
                closingData.addAll(datas);
                closingAdapter.notifyDataSetChanged();
            }
        });
    }

    private void countValetType (List<ClosingData.Data> datas) {
        int regular = 0;
        int exclusive = 0;

        for (ClosingData.Data data : datas) {
            ClosingData.Data.Attr attr = data.getAttributes();
            String valetTypeName = attr.getValetTypeName().toLowerCase().trim();
            if (valetTypeName.equals("regular")) {
                regular +=1;
            }else {
                exclusive +=1;
            }
        }

        reg = regular;
        exc = exclusive;
        total = datas.size();

        textRegular.setText(String.valueOf(regular));
        textExclusive.setText(String.valueOf(exclusive));
        textTotal.setText(String.valueOf(datas.size()));
    }

}
