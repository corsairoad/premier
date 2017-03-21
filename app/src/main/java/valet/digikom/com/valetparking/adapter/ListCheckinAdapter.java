package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;

/**
 * Created by DIGIKOM-EX4 on 1/5/2017.
 */

public class ListCheckinAdapter extends RecyclerView.Adapter<ListCheckinAdapter.ViewHolder>{

    List<Checkin> checkinList;
    Context context;
    List<EntryCheckinResponse> responsesList;
    OnItemCheckinListener onItemCheckinListener;

    public ListCheckinAdapter(List<Checkin> checkinList, List<EntryCheckinResponse> responseList, Context context, OnItemCheckinListener onItemCheckinListener) {
        this.checkinList = checkinList;
        this.context = context;
        this.responsesList = responseList;
        this.onItemCheckinListener = onItemCheckinListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkin,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return responsesList.get(position).getData().getAttribute().getId();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        EntryCheckinResponse response = responsesList.get(position);
        String logoMobil = response.getData().getAttribute().getLogoMobil();
        Glide.with(context)
                .load(logoMobil)
                .centerCrop()
                .placeholder(R.drawable.car_icon)
                .crossFade()
                .into(holder.imgCar);

        String platNo = response.getData().getAttribute().getNoTiket() + " - " + response.getData().getAttribute().getPlatNo();
        String checkinTime = response.getData().getAttribute().getCheckinTime();

        holder.textPlatNo.setText(platNo);
        holder.textRunnerName.setText(checkinTime);

        int id = (int) getItemId(position);
        holder.layoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemCheckinListener.onItemCheckinClick((int) getItemId(position));
            }
        });

        holder.textPlatNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemCheckinListener.onItemCheckinClick((int)getItemId(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return responsesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutContainer;
        TextView textPlatNo;
        TextView textRunnerName;
        TextView textIdCheckin;
        CircleImageView imgCar;

        public ViewHolder(View view) {
            super(view);
            textPlatNo = (TextView) view.findViewById(R.id.text_plat_no);
            textRunnerName = (TextView) view.findViewById(R.id.text_runner);
            textIdCheckin = (TextView) view.findViewById(R.id.id_checkin);
            layoutContainer = (LinearLayout) view.findViewById(R.id.container_checkin);
            imgCar = (CircleImageView) view.findViewById(R.id.img_car);
        }
    }

    public interface OnItemCheckinListener {
        void onItemCheckinClick(int id);
    }
}
