package valet.digikom.com.valetparking.fragments;

import android.content.Context;
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

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.adapter.ListCalledCarAdapter;
import valet.digikom.com.valetparking.dao.CallDao;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;

public class CalledCarFragment extends Fragment implements ListCalledCarAdapter.OnCalledCarClickListener {

    private List<EntryCheckinResponse> responseList = new ArrayList<>();
    private ListCalledCarAdapter calledCarAdapter;
    private TextView textEmpty;

    public CalledCarFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    }

    private class FetchCalledCarTask extends AsyncTask<Void, Void, List<EntryCheckinResponse>> {

        @Override
        protected List<EntryCheckinResponse> doInBackground(Void... voids) {
            return CallDao.getInstance(getContext()).fetchAllCalledCars();
        }

        @Override
        protected void onPostExecute(List<EntryCheckinResponse> responseListx) {
            if (!responseListx.isEmpty()) {
                responseList.clear();
                responseList.addAll(responseListx);
                calledCarAdapter.notifyDataSetChanged();
                textEmpty.setVisibility(View.GONE);
            }
        }
    }
}
