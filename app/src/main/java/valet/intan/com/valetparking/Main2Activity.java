package valet.intan.com.valetparking;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.intan.com.valetparking.adapter.ParkedCarPagerAdapter;
import valet.intan.com.valetparking.dao.AuthResDao;
import valet.intan.com.valetparking.dao.DropDao;
import valet.intan.com.valetparking.dao.TokenDao;
import valet.intan.com.valetparking.domain.DropPointMaster;
import valet.intan.com.valetparking.domain.EntryCheckoutCont;
import valet.intan.com.valetparking.fragments.CalledCarFragment;
import valet.intan.com.valetparking.fragments.ParkedCarFragment;
import valet.intan.com.valetparking.service.ApiClient;
import valet.intan.com.valetparking.service.ApiEndpoint;
import valet.intan.com.valetparking.service.DownloadCurrentLobbyService;
import valet.intan.com.valetparking.service.FailedTransactionService;
import valet.intan.com.valetparking.service.PostCheckoutService;
import valet.intan.com.valetparking.service.ProcessRequest;
import valet.intan.com.valetparking.util.CheckinCheckoutAlarm;
import valet.intan.com.valetparking.util.CheckoutReadyAlarm;
import valet.intan.com.valetparking.util.DownloadCheckinAlarm;
import valet.intan.com.valetparking.util.PrefManager;
import valet.intan.com.valetparking.util.RefreshTokenAlarm;
import valet.intan.com.valetparking.util.SyncingCheckin;
import valet.intan.com.valetparking.util.SyncingCheckout;
import valet.intan.com.valetparking.util.ValetDbHelper;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ParkedCarFragment.CountParkedCarListener,
        CalledCarFragment.CountCalledCarListener{

    public static final String ACTION_DOWNLOAD_CHECKINS = "com.valet.download.data.checkin";
    public static final String ACTION_REPORT = "com.valet.report";

    private ViewPager viewPager;
    private ParkedCarPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private TextView txtUserName;
    private NavigationView navView;
    private View headerView;
    private Button btnLogout;
    private TextView txtCountParkedCar;
    private TextView txtCountCalledCar;
    private PrefManager prefManager;
    private MaterialDialog materialDialog;
    private DrawerLayout drawer;
    private ProgressBar syncProgress;
    private TextView textProgress;
    private Button btnCancelLogout;

    private CheckinCheckoutAlarm checkinCheckoutAlarm;
    private DownloadCheckinAlarm downloadCheckinAlarm;
    private String idDropPoint;


    private BroadcastReceiver syncReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkinCheckoutAlarm = CheckinCheckoutAlarm.getInstance(this);
        downloadCheckinAlarm = DownloadCheckinAlarm.getInstance(this);

        prefManager = PrefManager.getInstance(this);

        initSyncReceiver();

        // back to login
        if (prefManager.getAuthResponse() == null || prefManager.getIdSite() == 0) {
            prefManager.saveAuthResponse(null);
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        syncProgress = (ProgressBar) findViewById(R.id.myProgress);
        textProgress = (TextView) findViewById(R.id.myTextProgress);
        btnCancelLogout = (Button) findViewById(R.id.cancel_logout);
        btnCancelLogout.setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
        navView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navView.inflateHeaderView(R.layout.nav_header_main2);
        txtUserName = (TextView) headerView.findViewById(R.id.text_user_name);
        setUserName();

        enableProgressBar(false);
        // set up pager parked car and called cars
        setupPagers();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_entry);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, AddCarActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // startCheckoutEntryAlarm();
    }

    private void initSyncReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SyncingCheckin.ACTION);
        filter.addAction(SyncingCheckin.ACTION_ERROR_RESPONSE);
        filter.addAction(SyncingCheckout.ACTION);
        filter.addAction(SyncingCheckout.ACTION_LOGOUT);
        filter.addAction(SyncingCheckout.ACTION_LOGOUT_ERROR_RESPONSE);

        syncReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case SyncingCheckin.ACTION:
                        setProgressMessage(intent.getStringExtra(SyncingCheckin.EXTRA));
                        break;
                    case SyncingCheckout.ACTION:
                        setProgressMessage(intent.getStringExtra(SyncingCheckout.EXTRA));
                        break;
                    case SyncingCheckout.ACTION_LOGOUT:
                        logout();
                        break;
                    case SyncingCheckin.ACTION_ERROR_RESPONSE:
                    case SyncingCheckout.ACTION_LOGOUT_ERROR_RESPONSE:
                        showErrorSync(intent.getStringExtra(SyncingCheckin.EXTRA));
                        break;
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver,filter);
    }

    private void showErrorSync(String stringExtra) {
        startAllServices();
        enableProgressBar(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Sync data error")
                .setMessage(stringExtra)
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void setProgressMessage(String stringExtra) {
        textProgress.setText(stringExtra);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPrinter();
        startAllServices();
        setTitle();
        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (syncReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
        }
    }

    private void setTitle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DropDao dropDao = DropDao.getInstance(new ValetDbHelper(Main2Activity.this));
                idDropPoint = prefManager.getIdDefaultDropPoint();
                if (idDropPoint == null){
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (idDropPoint != null) {
                            DropPointMaster dropPointMaster = dropDao.getDropPointById(Integer.parseInt(idDropPoint));
                            if (dropPointMaster.getAttrib() != null) {
                                String dropName = dropPointMaster.getAttrib().getDropName();
                                String title = dropName + " " + getCurrentDate();
                                getSupportActionBar().setTitle(title);
                            } else {
                                logout();
                            }
                        }
                    }
                });
            }
        }).run();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void setUserName() {
        String userName = PrefManager.getInstance(this).getUserName();
        txtUserName.setText(userName);
    }

    private void setupPagers() {
        pagerAdapter = new ParkedCarPagerAdapter(this,getSupportFragmentManager());
        pagerAdapter.addFragments(new ParkedCarFragment(), "Parked Cars");
        pagerAdapter.addFragments(new CalledCarFragment(), "Called Cars");
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*
                Fragment fragMentParkedCar = pagerAdapter.getItem(0);
                Fragment fragmentCalledCar = pagerAdapter.getItem(1);

                CheckoutDao checkoutDao = CheckoutDao.getInstance(Main2Activity.this, fragmentCalledCar, fragMentParkedCar);
                TokenDao.getToken(checkoutDao, Main2Activity.this);
                */
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        for (int a=0; a< tabLayout.getTabCount(); a++) {
            View v = pagerAdapter.getTabView(a);
            tabLayout.getTabAt(a).setCustomView(v);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("Do you want to quit admin valet?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        searchView.setMaxWidth(Integer.MAX_VALUE);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            onSearchRequested();
            return true;
        }

        // checking ready checkout car
        if (id == R.id.action_refresh) {
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        //TokenDao.refreshToken(this); // Getting new token

        prefManager.setLoggingOut(false);

        int indexLobbyType = prefManager.getLobbyType();
        ParkedCarFragment parkedCarFragment = (ParkedCarFragment) pagerAdapter.getItem(0);

        if(parkedCarFragment != null) {
            parkedCarFragment.downloadCheckinList(indexLobbyType);
        }

        postCheckinData();

    }

    private void postCheckinData(){
        Intent intent = new Intent(this, FailedTransactionService.class);
        startService(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = new Intent(this, ClosingActivity.class); // set default to closing
        switch (id) {
            case R.id.setting:
                intent = new Intent(this, PreferenceActivity.class);
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
            case R.id.report:
                intent.setAction(ACTION_REPORT);
                break;
            case R.id.menu_imei:
                drawer.closeDrawer(GravityCompat.START);
                showImei();
                return true;
            case R.id.menu_sync_data:
                intent = new Intent(this, SyncingActivity.class);
                break;
            default:
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        
        startActivity(intent);
        
        return true;
    }

    private void showImei() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Id Device")
                .setMessage(PrefManager.getInstance(this).getRemoteDeviceId())
                .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });

        builder.show();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_logout:
                drawer.closeDrawer(GravityCompat.START);
                showLogoutDialog();
                break;
            case R.id.cancel_logout:
                cancelLogout();
                break;
        }
    }

    private void cancelLogout() {
        stopService(new Intent(this, SyncingCheckin.class));
        stopService(new Intent(this, SyncingCheckout.class));
        enableProgressBar(false);
        prefManager.setLoggingOut(false);
    }

    private void showLogoutDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Confirmation")
                .setContentText("Do you want to logout?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        drawer.closeDrawer(GravityCompat.START);

                        syncAllUnsyncData();
                        //logout();
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelText("Cancel")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.cancel();
                    }
                })

                .showCancelButton(true)
                .show();
    }

    private void syncAllUnsyncData() {
        if (!ApiClient.isNetworkAvailable(this)) {
            Toast.makeText(this, "Logout failed, please check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        prefManager.setLoggingOut(true); // flag that define failedtransactionservice to run or not

        stopAllService(); // stop alarm
        stopServiceDirecly(); // stop service
        enableProgressBar(true);

        syncCheckin();
    }

    private void logout() {
        //Toast.makeText(this, "Logging out, please wait", Toast.LENGTH_LONG).show();

        //prefManager.resetDefaultDropPoint();
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {
                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, null);
                Call<ResponseBody> call = apiEndpoint.logout(token);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int code = response.code();
                        if (code == AuthResDao.HTTP_STATUS_LOGOUT_SUKSES) {
                            PrefManager prefManager = PrefManager.getInstance(Main2Activity.this);
                            prefManager.logoutUser();
                            prefManager.setLoggingOut(false);
                            prefManager.setPrinterMacAddress(null);
                            stopAllService();
                            goToSplash();

                        } else if (code == AuthResDao.HTTP_STATUS_LOGOUT_INVALID){
                            prefManager.setLoggingOut(false);
                            //Toast.makeText(Main2Activity.this, "Logout failed, invalid token", Toast.LENGTH_SHORT).show();
                        } else {
                            prefManager.setLoggingOut(false);
                            Toast.makeText(Main2Activity.this, "Logout failed, error response occured. Please try again later.", Toast.LENGTH_SHORT).show();
                            cancelLogout();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        prefManager.setLoggingOut(false);
                        Toast.makeText(Main2Activity.this, "Logout failed: " + t.getMessage() + ". Please try again later.", Toast.LENGTH_SHORT).show();
                        cancelLogout();
                    }
                });

            }
        },this);
    }

    private void syncCheckin() {
        Intent intent = new Intent(this, SyncingCheckin.class);
        intent.setAction(SyncingCheckin.ACTION);
        startService(intent);
    }

    private void goToSplash() {
        PrefManager.getInstance(this).saveAuthResponse(null);
        Intent intent = new Intent(this,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void setCountParkedCar(int count) {
        View v = tabLayout.getTabAt(0).getCustomView();
        txtCountParkedCar = (TextView) v.findViewById(R.id.text_count);
        txtCountParkedCar.setText(String.valueOf(count));
    }

    @Override
    public void setCountCalledCar(int count) {
        View v = tabLayout.getTabAt(1).getCustomView();
        txtCountCalledCar = (TextView) v.findViewById(R.id.text_count);
        txtCountCalledCar.setText(String.valueOf(count));
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            String queryId = uri.getLastPathSegment();
            startParkDetailActivity(queryId);
        } else if (ACTION_DOWNLOAD_CHECKINS.equals(intent.getAction())) {
            downloadCheckinList();
        }

    }

    private void startParkDetailActivity(String idResponse) {
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putExtra(EntryCheckoutCont.KEY_ENTRY_CHECKOUT, Integer.valueOf(idResponse));
        startActivity(intent);
    }

    private void checkPrinter() {
        PrefManager prefManager = PrefManager.getInstance(this);
        String printer = prefManager.getPrinterMacAddress();
        if (printer == null) {
            materialDialog = new MaterialDialog.Builder(this)
                    .title("Connect to Printer")
                    .content("You are not connected to printer. Connect now?")
                    .positiveText("Oke")
                    .positiveColor(Color.parseColor("#00695c"))
                    .negativeText("Cancel")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            startActivity(new Intent(Main2Activity.this, PrinterActivity.class));
                        }
                    }).build();
            materialDialog.show();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy");
        return sdf.format(new Date());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (materialDialog != null) {
            if (materialDialog.isShowing()) {
                materialDialog.dismiss();
            }
            materialDialog = null;

        }
    }

    // checkin, checkout, download current lobby data
    private void startAllServices() {
        checkinCheckoutAlarm.startAlarm();
        downloadCheckinAlarm.startAlarm();
        //startServiceDirectly();
        //startCheckoutEntryAlarm();
    }

    private void stopAllService() {
        checkinCheckoutAlarm.cancelAlarm();
        downloadCheckinAlarm.cancelAlarm();

        RefreshTokenAlarm refreshTokenAlarm = RefreshTokenAlarm.getInstance(this);
        refreshTokenAlarm.cancelAlarm();
        //stopServiceDirecly();
    }

    private void startCheckoutEntryAlarm() {
        CheckoutReadyAlarm checkoutReadyAlarm = CheckoutReadyAlarm.getInstance(this);
        checkoutReadyAlarm.startAlarm();
        /*
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //Intent intent = new Intent(context, EntryCheckoutService.class);
        Intent intent = new Intent();
        intent.setClass(this,valet.digikom.com.valetparking.CheckoutReceiver.class);
        intent.setAction("com.valet.dki");

        //this.sendBroadcast(intent);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,0,intent,0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, alarmIntent);
        //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(),8000,alarmIntent);
        */
    }

    private void enableProgressBar(boolean enable) {
        if (enable) {
            syncProgress.setVisibility(View.VISIBLE);
            textProgress.setVisibility(View.VISIBLE);
            btnCancelLogout.setVisibility(View.VISIBLE);
        }else {
            syncProgress.setVisibility(View.GONE);
            textProgress.setVisibility(View.GONE);
            btnCancelLogout.setVisibility(View.GONE);
        }
    }

    private void stopServiceDirecly() {
        stopService(new Intent(this, FailedTransactionService.class));
        stopService(new Intent(this, PostCheckoutService.class));
    }

    private void startServiceDirectly() {
        startService(new Intent(this, FailedTransactionService.class));
    }

    private void downloadCheckinList() {
        Intent intent = new Intent(this, DownloadCurrentLobbyService.class);
        intent.setAction(DownloadCurrentLobbyService.ACTION_DOWNLOAD);
        startService(intent);
    }

}
