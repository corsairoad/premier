package valet.digikom.com.valetparking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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

public class ClosingActivity extends AppCompatActivity implements View.OnClickListener, ListClosingAdapter.OnClosingItemClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = ClosingActivity.class.getSimpleName();
    private static final int OPEN = 1;
    private static final int CLOSING = 2;

    private static final String DOWNLOAD_PER_LOBBY = "lobby";
    private static final String DOWNLOAD_PER_SHIFT = "shift";
    private static final String DOWNLOAD_PER_SITE = "site";

    public static final int PRINT_CLOSING = 0;
    public static final int PRINT_SUMMARY = 1;
    public static final int PRINT_DETAILS = 2;

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
    Button btnPrintDetails;
    Button btnPrintSummary;
    LinearLayout layoutPrintSummary;
    Spinner spReport;
    Toolbar toolbar;

    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    int mSecond;
    int reg;
    int exc;
    int total;

    String startDate = "";
    String endDate = "";
    String selectedReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        btnPrintSummary = (Button) findViewById(R.id.btn_print_summary);
        btnPrintDetails = (Button) findViewById(R.id.btn_print_detail);
        layoutPrintSummary = (LinearLayout) findViewById(R.id.layout_print_summary);
        spReport = (Spinner) findViewById(R.id.spinner_report);
        listClosingView = (RecyclerView) findViewById(R.id.list_closing_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listClosingView.setLayoutManager(layoutManager);
        closingAdapter = new ListClosingAdapter(closingData, this);
        listClosingView.setAdapter(closingAdapter);

        ArrayAdapter<CharSequence> spReportAdapter = ArrayAdapter.createFromResource(this, R.array.array_report_type,  R.layout.text_item_spinner_report);
        spReportAdapter.setDropDownViewResource(R.layout.text_dropdown_item_spinner_report);
        spReport.setAdapter(spReportAdapter);
        spReport.setOnItemSelectedListener(this);

        btnSetFrom.setOnClickListener(this);
        btnSetUntil.setOnClickListener(this);
        btnClosing.setOnClickListener(this);
        btnPrintSummary.setOnClickListener(this);
        btnPrintDetails.setOnClickListener(this);

        inputDateFrom.setText(getCurrentDate(OPEN));
        inputDateUntil.setText(getCurrentDate(CLOSING));

        handleIntent();

        downloadData(DOWNLOAD_PER_LOBBY);

        startDate = inputDateFrom.getText().toString();
        endDate = inputDateUntil.getText().toString();
    }

    private void handleIntent() {
        if (getIntent() != null) {
            if (Main2Activity.ACTION_REPORT.equals(getIntent().getAction())) {
                getSupportActionBar().setTitle(getString(R.string.report)); // set title
                showPrintButtonOnly();
            }
        }
    }

    private void showPrintButtonOnly() {
        btnClosing.setVisibility(View.GONE);
        layoutPrintSummary.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
    }

    private void downloadData(String flagDownload) {
        closingData.clear();
        closingAdapter.notifyDataSetChanged();
        new DownloadClosingDataTask().execute(flagDownload);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private synchronized void close(String flagHeader, int flagPrint) {
        ClosingDao closingDao = ClosingDao.getInstance(this);
        String readInfo = inputRemark.getText().toString();
        closingDao.close(readInfo,startDate,endDate);
        print(flagHeader, flagPrint);
        
        /*
        ------------ PRINT CARA LAMA
        PrintClosing printClosing = new PrintClosing(this, closingData, lokasi, siteName, startDate,endDate,"Admin", reg,exc,total);
        printClosing.print();
        */

    }

    private void print(String flagHeader, int flagPrint) {
        PrefManager prefManager = PrefManager.getInstance(this);
        String lobbyName = prefManager.getDefaultDropPointName();
        String siteName = prefManager.getSiteName();

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
        PrintReceiptClosing printReceiptClosing = new PrintReceiptClosing(this, closingParam,flagHeader, flagPrint);
        printReceiptClosing.buildPrintData();
    }

    @Override
    public void onClick(final View viewx) {

        if (closingData.isEmpty()) {
            Toast.makeText(this, "Data empty", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = viewx.getId();
        Toast.makeText(this, "Printing.. please wait", Toast.LENGTH_SHORT).show();
        switch (id) {
            case R.id.btn_closing:
                new SweetAlertDialog(this)
                        .setTitleText("Closing")
                        .setContentText("Closing transaction now?")
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                close(null, PRINT_CLOSING);
                                new DownloadClosingDataTask().execute(DOWNLOAD_PER_LOBBY);
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
                break;
            case R.id.btn_print_summary:
                print(selectedReport,PRINT_SUMMARY);
                break;
            case R.id.btn_print_detail:
                print(selectedReport, PRINT_DETAILS);
                break;
            default:
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
                break;

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
    public void OnClosingItemClick(int vthdId) { // vthdid changed to position on adapter
        //openDetailClosing(vthdId);
        progressBar.setVisibility(View.VISIBLE);
        showDialogDetail(vthdId);
    }

    private void openDetailClosing(int vthdId) { // vthdid changed to position on adapter
        Intent intent = new Intent(this, ParkedCarDetailActivity.class);
        intent.putExtra(EntryCheckinResponse.ID_ENTRY_CHECKIN, vthdId);
        intent.setAction("DETAIL_FOR_CLOSING_ITEM");
        startActivity(intent);
    }

    private void showDialogDetail(int vthdId) { // vthdid changed to position on adapter
        final View view = getLayoutInflater().inflate(R.layout.layout_closing_detail_dialog, null);
        final ClosingData.Data data = closingData.get(vthdId);
        if (data != null) {
            TokenDao.getToken(new ProcessRequest() {
                @Override
                public void process(String token) {
                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                    Call<EntryCheckinResponse> call = apiEndpoint.getVthdTransactionItem(data.getLinks().getSelf().getHref());
                    call.enqueue(new Callback<EntryCheckinResponse>() {
                        @Override
                        public void onResponse(Call<EntryCheckinResponse> call, Response<EntryCheckinResponse> response) {
                            if (response != null && response.body()!=null) {
                                initClosingData(view, data, response.body().getData());
                                MaterialDialog materialDialog = new MaterialDialog.Builder(ClosingActivity.this)
                                        .customView(view, true)
                                        .title("Car Detail")
                                        .positiveText("Oke")
                                        .build();
                                materialDialog.show();
                            }else {
                                Toast.makeText(ClosingActivity.this, "Cannot retrieve data. Please check your internet connection", Toast.LENGTH_SHORT).show();
                            }

                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<EntryCheckinResponse> call, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ClosingActivity.this, "Cannot retrieve data. Please check your internet connection", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, this);


        }

    }

    private void initClosingData(View view, ClosingData.Data data, EntryCheckinResponse.Data vthdData) {
        TextView txtLicensePlate = (TextView) view.findViewById(R.id.text_plat_no);
        TextView txtCarType = (TextView) view.findViewById(R.id.text_car_type);
        TextView txtColor = (TextView) view.findViewById(R.id.text_color);
        TextView txtValetType = (TextView) view.findViewById(R.id.text_valet_type);
        TextView txtDropPoint = (TextView) view.findViewById(R.id.text_drop_point);
        TextView txtCheckin = (TextView) view.findViewById(R.id.text_checkin_time);
        TextView txtCheckout = (TextView) view.findViewById(R.id.text_checkout_time);
        TextView txtTicketNo = (TextView) view.findViewById(R.id.text_ticket_no);
        TextView txtIdTransaksi = (TextView) view.findViewById(R.id.text_id_transaksi);

        txtLicensePlate.setText(data.getAttributes().getPlatNo());
        txtCarType.setText(vthdData.getAttribute().getCar());
        txtColor.setText(vthdData.getAttribute().getColor());
        txtDropPoint.setText(vthdData.getAttribute().getDropPoint());
        txtValetType.setText(data.getAttributes().getValetTypeName());
        txtCheckin.setText(data.getAttributes().getCheckIn());
        txtCheckout.setText(data.getAttributes().getCheckout());
        txtTicketNo.setText(data.getAttributes().getNoTiket());
        txtIdTransaksi.setText(data.getAttributes().getTransactionId());
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedReport = (String) adapterView.getSelectedItem();
        switch (i) {
            case 0:
                downloadData(DOWNLOAD_PER_LOBBY);
                break;
            case 1:
                downloadData(DOWNLOAD_PER_SITE);
                break;
            case 2:
                downloadData(DOWNLOAD_PER_SHIFT);
                break;
            default:break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class DownloadClosingDataTask extends AsyncTask<String, Void, List<ClosingData.Data>> {

        @Override
        protected List<ClosingData.Data> doInBackground(final String... strings) {
            TokenDao.getToken(new ProcessRequest() {
                @Override
                public void process(String token) {
                    String flag = strings[0];
                    ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                    Call<ClosingData> call = apiEndpoint.getClosingData(900);
                    if (flag != null) {
                        switch (flag) {
                            case DOWNLOAD_PER_SHIFT:
                                call = apiEndpoint.getClosingDataShift(900);
                                break;
                            case DOWNLOAD_PER_SITE:
                                call = apiEndpoint.getClosingDataSite(900);
                                break;
                            default:
                                break;
                        }
                    }

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
