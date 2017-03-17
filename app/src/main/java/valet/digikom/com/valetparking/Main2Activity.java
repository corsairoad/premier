package valet.digikom.com.valetparking;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;
import valet.digikom.com.valetparking.adapter.ParkedCarPagerAdapter;
import valet.digikom.com.valetparking.dao.DropDao;
import valet.digikom.com.valetparking.domain.DropPointMaster;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;
import valet.digikom.com.valetparking.fragments.CalledCarFragment;
import valet.digikom.com.valetparking.fragments.ParkedCarFragment;
import valet.digikom.com.valetparking.util.CheckoutReadyAlarm;
import valet.digikom.com.valetparking.util.PrefManager;
import valet.digikom.com.valetparking.util.ValetDbHelper;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ParkedCarFragment.CountParkedCarListener,
        CalledCarFragment.CountCalledCarListener{

    public static final String ACTION_DOWNLOAD_CHECKIN = "com.valet.download.data.checkin";
    public static final String ACTION_REPORT = "com.valet.report";

    ViewPager viewPager;
    ParkedCarPagerAdapter pagerAdapter;
    TabLayout tabLayout;
    TextView txtUserName;
    NavigationView navView;
    View headerView;
    Button btnLogout;
    TextView txtCountParkedCar;
    TextView txtCountCalledCar;
    PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefManager = PrefManager.getInstance(this);
        if(prefManager.getIdSite() == 0 && prefManager.getAuthResponse() != null) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }

        if (prefManager.getAuthResponse() == null) {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }

        setTitle();

        checkPrinter();

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);
        navView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navView.inflateHeaderView(R.layout.nav_header_main2);
        txtUserName = (TextView) headerView.findViewById(R.id.text_user_name);
        setUserName();

        // set up pager parked car and called cars
        setupPagers();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_entry);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, AddCarActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        handleIntent(getIntent());

        startCheckoutEntryAlarm();
    }

    private void setTitle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DropDao dropDao = DropDao.getInstance(new ValetDbHelper(Main2Activity.this));
                final String idDropPoint = prefManager.getIdDefaultDropPoint();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (idDropPoint != null) {
                            DropPointMaster dropPointMaster = dropDao.getDropPointById(Integer.parseInt(idDropPoint));
                            if (dropPointMaster != null) {
                                String dropName = dropPointMaster.getAttrib().getDropName();
                                String title = dropName + " " + getCurrentDate();
                                getSupportActionBar().setTitle(title);
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
            super.onBackPressed();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
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
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        
        startActivity(intent);
        
        return true;
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

    @Override
    public void onClick(View view) {
        if (view == btnLogout) {
            showLogoutDialog();
        }
    }

    private void showLogoutDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Confirmation")
                .setContentText("Do you want to logout?")
                .setConfirmText("Yes")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        PrefManager.getInstance(Main2Activity.this).logoutUser();
                        goToSplash();
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
        }
        /*
        else if (ACTION_DOWNLOAD_CHECKIN.equals(intent.getAction())) {
            ParkedCarFragment parkedCarFragment = (ParkedCarFragment) pagerAdapter.getItem(0);
            parkedCarFragment.downloadCheckinList();
        }
        */
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
            new MaterialDialog.Builder(this)
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
                    }).show();
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yyyy");
        return sdf.format(new Date());
    }
}
