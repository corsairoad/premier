package valet.digikom.com.valetparking.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import valet.digikom.com.valetparking.CheckoutActivity;
import valet.digikom.com.valetparking.ParkedCarDetailActivity;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.adapter.ListCheckinAdapter;
import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.dao.ReprintDao;
import valet.digikom.com.valetparking.dao.TokenDao;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.domain.CheckinList;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;
import valet.digikom.com.valetparking.service.ApiClient;
import valet.digikom.com.valetparking.service.ApiEndpoint;
import valet.digikom.com.valetparking.service.ProcessRequest;
import valet.digikom.com.valetparking.util.PrefManager;

public class ParkedCarFragment extends Fragment implements ListCheckinAdapter.OnItemCheckinListener,
        CheckoutDao.OnCarReadyListener, AdapterView.OnItemSelectedListener, ListCheckinAdapter.OnOptionReprintListener {

    RecyclerView listCheckin;
    ListCheckinAdapter adapter;
    ArrayList<Checkin> checkins = new ArrayList<>();
    List<EntryCheckinResponse> responseList = new ArrayList<>();
    TextView textEmpty;
    Spinner spLobbyCheckin;

    CountParkedCarListener listener;
    public static ParkedCarFragment parkedCarFragment;
    EntryDao entryDao;

    LocalBroadcastManager bManager;
    public static final String RECEIVE_CURRENT_LOBBY_DATA = "valet.digikom.com.valetparking.current.lobby";

    int countSpinner;
    public ParkedCarFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parkedCarFragment = this;
        entryDao = EntryDao.getInstance(getContext());
        bManager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RECEIVE_CURRENT_LOBBY_DATA);
        bManager.registerReceiver(bReceiver, intentFilter);
        countSpinner = 0;
    }

    public static ParkedCarFragment getInstance() {
        return parkedCarFragment;
    }

    // receiver download current lobby
    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(RECEIVE_CURRENT_LOBBY_DATA)) {
                Log.d("Download", "Broadcast receiver data current lobby called");
                new LoadCheckinTask().execute();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(bManager != null) {
            bManager.unregisterReceiver(bReceiver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_main2, container, false);

        spLobbyCheckin = (Spinner) view.findViewById(R.id.spinner_lobby_checkin);
        listCheckin = (RecyclerView) view.findViewById(R.id.list_checkin);
        textEmpty = (TextView) view.findViewById(R.id.text_empty);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listCheckin.setHasFixedSize(true);
        listCheckin.setLayoutManager(layoutManager);
        adapter = new ListCheckinAdapter(checkins, responseList,getContext(), this, this);
        listCheckin.setAdapter(adapter);

        ArrayAdapter<CharSequence> spLobbyAdapter = ArrayAdapter.createFromResource(getContext(),R.array.array_lobby_checkin,  R.layout.text_item_spinner_report);
        spLobbyAdapter.setDropDownViewResource(R.layout.text_dropdown_item_spinner_report);
        spLobbyCheckin.setAdapter(spLobbyAdapter);
        spLobbyCheckin.setOnItemSelectedListener(this);

         new LoadCheckinTask().execute();

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (CountParkedCarListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemCheckinClick(int id) {
        Intent intent = new Intent(getContext(), CheckoutActivity.class);
        intent.putExtra(EntryCheckoutCont.KEY_ENTRY_CHECKOUT, id);
        startActivity(intent);
    }

    @Override
    public void onCheckoutReady() {
        new LoadCheckinTask().execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //manageSpinnerValetType(i);;
        PrefManager.getInstance(getContext()).saveLobbyType(i);
        if (ApiClient.isNetworkAvailable(getContext())) {
            downloadCheckinList(i);
        }else {
            Toast.makeText(getContext(), "Download failed, please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void manageSpinnerValetType(int i) {
        if (countSpinner >= 1) {
            clearData();
            PrefManager.getInstance(getContext()).saveLobbyType(i);
            if (ApiClient.isNetworkAvailable(getContext())) {
                downloadCheckinList(i);
            }else {
                Toast.makeText(getContext(), "Download failed, please check your internet connection", Toast.LENGTH_SHORT).show();
            }
        }
        countSpinner++;
    }

    private void clearData() {
        responseList.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onOptionReprintClicked(final String noTiket) {
        if (noTiket != null) {
            noTiket.trim();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"Please wait..", Toast.LENGTH_SHORT).show();
                        }
                    });

                    ReprintDao reprintDao = ReprintDao.getInstance(getContext());
                    int statusPrint = reprintDao.rePrint(noTiket);
                    String message = "Reprint ticket " + noTiket;
                    String content = "";

                    switch (statusPrint) {
                        case ReprintDao.STATUS_PRINT_SUCCEED:
                            message = message + " Succeed";
                            content = "Reprint succeed. You can only reprint once a time";
                            reprintDao.removeReprintData(noTiket);
                            break;
                        case ReprintDao.STATUS_PRINT_FAILED:
                            message = message + " Failed";
                            content = "Either you already reprinted or using different device";
                            break;
                        case ReprintDao.STATUS_PRINT_ERROR:
                            message = message + " Error";
                            content = "Please check the printer and try again later.";
                            break;
                    }

                    final String finalMessage = message;
                    final String finalContent = content;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new MaterialDialog.Builder(getContext())
                                    .title(finalMessage)
                                    .content(finalContent)
                                    .positiveText("Oke")
                                    .build()
                                    .show();
                        }
                    });
                }
            }).start();
        }
    }

    private class LoadCheckinTask extends AsyncTask<Void, Void, List<EntryCheckinResponse>> {

        @Override
        protected List<EntryCheckinResponse> doInBackground(Void... voids) {
            if (entryDao == null) {
                entryDao = EntryDao.getInstance(getContext());
            }
            return entryDao.fetchAllCheckinResponse();
        }

        @Override
        protected void onPostExecute(List<EntryCheckinResponse> entryCheckinResponses) {
            super.onPostExecute(entryCheckinResponses);
            clearData();
            if (entryCheckinResponses != null && !entryCheckinResponses.isEmpty()) {
                responseList.addAll(entryCheckinResponses);
                adapter.notifyDataSetChanged();
                textEmpty.setVisibility(View.GONE);

                Log.d("Download", "Checkin List updated");
                //textTotalCheckin.setText(getResources().getString(R.string.total_checkin) + " " + entryCheckinResponses.size());
                listener.setCountParkedCar(entryCheckinResponses.size());
            }else {
                //textTotalCheckin.setVisibility(View.INVISIBLE);
                textEmpty.setVisibility(View.VISIBLE);
                listener.setCountParkedCar(0);
            }
        }
    }

    public interface CountParkedCarListener {
        void setCountParkedCar (int count);
    }

    public  void downloadCheckinList(final int index) {
        TokenDao.getToken(new ProcessRequest() {
            @Override
            public void process(String token) {

                if (getContext() != null) {
                    Toast.makeText(getContext(), "Downloading data..", Toast.LENGTH_SHORT).show();
                }

                ApiEndpoint apiEndpoint = ApiClient.createService(ApiEndpoint.class, token);
                Call<CheckinList> call = apiEndpoint.getCurrentCheckinList(500);

                if (index == 1) {
                    call = apiEndpoint.getCheckinList(999);
                }

                call.enqueue(new Callback<CheckinList>() {

                    @Override
                    public void onResponse(Call<CheckinList> call, Response<CheckinList> response) {
                        if (response != null && response.body() != null) {
                            //clearData();
                            List<EntryCheckinResponse.Data> checkinList = response.body().getCheckinResponseList();
                            if (!checkinList.isEmpty()) {
                                entryDao.insertListCheckin(checkinList);
                            }else {
                                Toast.makeText(getContext(), "Data Empty", Toast.LENGTH_SHORT).show();
                            }
                        }
                        new LoadCheckinTask().execute();
                    }

                    @Override
                    public void onFailure(Call<CheckinList> call, Throwable t) {
                        new LoadCheckinTask().execute();
                    }
                });
            }
        }, getContext());
    }
}
