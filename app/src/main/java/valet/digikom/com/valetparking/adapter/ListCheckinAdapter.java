package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.Checkin;

/**
 * Created by DIGIKOM-EX4 on 1/5/2017.
 */

public class ListCheckinAdapter extends RecyclerView.Adapter<ListCheckinAdapter.ViewHolder>{

    List<Checkin> checkinList;
    Context context;

    public ListCheckinAdapter(List<Checkin> checkinList, Context context) {
        this.checkinList = checkinList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkin,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.textIdCheckin.setText(checkinList.get(position).getId());
        String platNo = checkinList.get(position).getPlatNo() + " " + checkinList.get(position).getJenisMobil();
        holder.textPlatNo.setText(platNo);
        holder.textRunnerName.setText(checkinList.get(position).getDateString());
    }

    @Override
    public int getItemCount() {
        return checkinList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textPlatNo;
        TextView textRunnerName;
        TextView textIdCheckin;

        public ViewHolder(View view) {
            super(view);
            textPlatNo = (TextView) view.findViewById(R.id.text_plat_no);
            textRunnerName = (TextView) view.findViewById(R.id.text_runner);
            textIdCheckin = (TextView) view.findViewById(R.id.id_checkin);
        }
    }
}
