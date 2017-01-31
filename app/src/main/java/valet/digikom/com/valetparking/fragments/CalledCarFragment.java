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

import valet.digikom.com.valetparking.CheckoutActivity;
import valet.digikom.com.valetparking.ParkedCarDetailActivity;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.adapter.ListCalledCarAdapter;
import valet.digikom.com.valetparking.dao.CallDao;
import valet.digikom.com.valetparking.dao.CheckoutDao;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;
import valet.digikom.com.valetparking.domain.EntryCheckoutCont;

public class CalledCarFragment extends Fragment implements ListCalledCarAdapter.OnCalledCarClickListener, CheckoutDao.OnCarReadyListener {

    private List<EntryCheckinResponse> responseList = new ArrayList<>();
    private ListCalledCarAdapter calledCarAdapter;
    private TextView textEmpty;
    private CountCalledCarListener listener;
    public static CalledCarFragment calledCarFragment;

    public CalledCarFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calledCarFragment = this;
    }

    public static CalledCarFragment getInstance() {
        return calledCarFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_called_car, container, false);
        textEmpty = (TextView) v.findViewById(R.id.text_empty_called);
        RecyclerView listCalled = (RecyclerView) v.findViewById(R.id.list_called);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listCalled.setHasFixedSize(true);
        listCalled.setLayoutManager(layoutManager);
        calledCarAdapter =  new ListCalledCarAdapter(responseList, getContext(), this);
        listCalled.setAdapter(calledCarAdapter);

        new FetchCalledCarTask().execute();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (CountCalledCarListener) context;
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
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(int id) {
        Intent intent = new Intent(getContext(), CheckoutActivity.class);
        intent.putExtra(EntryCheckoutCont.KEY_ENTRY_CHECKOUT, id);
        startActivity(intent);
    }

    @Override
    public void onCheckoutReady() {
        new FetchCalledCarTask().execute();
    }

    private class FetchCalledCarTask extends AsyncTask<Void, Void, List<EntryCheckinResponse>> {

        @Override
        protected List<EntryCheckinResponse> doInBackground(Void... voids) {
            return CallDao.getInstance(getContext()).fetchAllCalledCars();
        }

        @Override
        protected void onPostExecute(List<EntryCheckinResponse> responseListx) {
            initList(responseListx);
        }
    }

    private void initList(List<EntryCheckinResponse> responseListx) {
        if (!responseListx.isEmpty()) {
            responseList.clear();
            responseList.addAll(responseListx);
            calledCarAdapter.notifyDataSetChanged();
            textEmpty.setVisibility(View.GONE);
            listener.setCountCalledCar(responseListx.size());
        }else {
            listener.setCountCalledCar(0);
        }
    }

    public interface CountCalledCarListener {
        void setCountCalledCar (int count);
    }
}
