package valet.intan.com.valetparking;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import valet.intan.com.valetparking.adapter.ListSyncCheckinAdapter;
import valet.intan.com.valetparking.adapter.ListSyncCheckoutAdapter;
import valet.intan.com.valetparking.dao.EntryCheckinContainerDao;
import valet.intan.com.valetparking.dao.FinishCheckoutDao;
import valet.intan.com.valetparking.domain.CheckoutData;
import valet.intan.com.valetparking.domain.EntryCheckinContainer;

public class SyncingActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private List<EntryCheckinContainer> checkins = new ArrayList<>();
    private ListSyncCheckinAdapter syncCheckinAdapter;
    private List<CheckoutData> checkouts = new ArrayList<>();
    private ListSyncCheckoutAdapter syncCheckoutAdapter;

    private RecyclerView listSyncData;
    private TextView textTotalSync;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syncing);

        textTotalSync = (TextView) findViewById(R.id.text_total_sync);
        listSyncData = (RecyclerView) findViewById(R.id.recycler_sync_data);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listSyncData.setLayoutManager(layoutManager);

        initSyncCheckin();
    }

    private void initSyncCheckin() {
        EntryCheckinContainerDao checkinDao = EntryCheckinContainerDao.getInstance(this);
        checkins = checkinDao.fetchAll();
        syncCheckinAdapter = new ListSyncCheckinAdapter(checkins, this);
        listSyncData.setAdapter(syncCheckinAdapter);
        syncCheckinAdapter.notifyDataSetChanged();

        textTotalSync.setText("Unsynchronized Checkin Total: " + checkins.size());
    }

    private void initSyncCheckout() {
        checkouts = FinishCheckoutDao.getInstance(this).getCheckoutData();
        syncCheckoutAdapter = new ListSyncCheckoutAdapter(this,checkouts);
        listSyncData.setAdapter(syncCheckoutAdapter);
        syncCheckoutAdapter.notifyDataSetChanged();

        textTotalSync.setText("Unsynchronized Checkout Total: " + checkouts.size());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.btn_sync_checkin:
                initSyncCheckin();
                break;
            case R.id.btn_sync_checkout:
                initSyncCheckout();
                break;
            default:
                initSyncCheckin();
        }
        return true;
    }
}
