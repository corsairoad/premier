package valet.digikom.com.valetparking.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import valet.digikom.com.valetparking.ParkedCarDetailActivity;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.adapter.ListCheckinAdapter;
import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.dao.EntryDao;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;

public class ParkedCarFragment extends Fragment implements ListCheckinAdapter.OnItemCheckinListener, CheckoutDao.OnCarReadyListener {

    RecyclerView listCheckin;
    ListCheckinAdapter adapter;
    ArrayList<Checkin> checkins = new ArrayList<>();
    List<EntryCheckinResponse> responseList = new ArrayList<>();
    TextView textEmpty;
    TextView textTotalCheckin;
    CountParkedCarListener listener;
    public static ParkedCarFragment parkedCarFragment;

    public ParkedCarFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parkedCarFragment = this;
    }

    public static ParkedCarFragment getInstance() {
        return parkedCarFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.content_main2, container, false);

        textTotalCheckin = (TextView) view.findViewById(R.id.text_total_checkin);
        listCheckin = (RecyclerView) view.findViewById(R.id.list_checkin);
        textEmpty = (TextView) view.findViewById(R.id.text_empty);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listCheckin.setHasFixedSize(true);
        listCheckin.setLayoutManager(layoutManager);
        adapter = new ListCheckinAdapter(checkins, responseList,getContext(), this);
        listCheckin.setAdapter(adapter);

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
        Intent intent = new Intent(getContext(), ParkedCarDetailActivity.class);
        intent.putExtra(EntryCheckinResponse.ID_ENTRY_CHECKIN, id);
        startActivity(intent);
    }

    @Override
    public void onCheckoutReady() {
        new LoadCheckinTask().execute();
    }

    private class LoadCheckinTask extends AsyncTask<Void, Void, List<EntryCheckinResponse>> {

        @Override
        protected List<EntryCheckinResponse> doInBackground(Void... voids) {
            EntryDao entryDao = EntryDao.getInstance(getContext());
            return entryDao.fetchAllCheckinResponse();
        }

        @Override
        protected void onPostExecute(List<EntryCheckinResponse> entryCheckinResponses) {
            super.onPostExecute(entryCheckinResponses);
            if (entryCheckinResponses != null && !entryCheckinResponses.isEmpty()) {
                responseList.clear();
                responseList.addAll(entryCheckinResponses);
                adapter.notifyDataSetChanged();
                textEmpty.setVisibility(View.GONE);
                textTotalCheckin.setText(getResources().getString(R.string.total_checkin) + " " + entryCheckinResponses.size());
                listener.setCountParkedCar(entryCheckinResponses.size());
            }else {
                textTotalCheckin.setVisibility(View.INVISIBLE);
                textEmpty.setVisibility(View.VISIBLE);
                listener.setCountParkedCar(0);
            }
        }
    }

    public interface CountParkedCarListener {
        void setCountParkedCar (int count);
    }
}
