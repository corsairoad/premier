package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.Checkin;
import valet.digikom.com.valetparking.domain.EntryCheckinResponse;

/**
 * Created by DIGIKOM-EX4 on 1/23/2017.
 */

public class ListCalledCarAdapter extends RecyclerView.Adapter<ListCalledCarAdapter.ViewHolder> {


    Context context;
    List<EntryCheckinResponse> responsesList;
    OnCalledCarClickListener onCalledCarClickListener;

    public ListCalledCarAdapter(List<EntryCheckinResponse> responseList, Context context, OnCalledCarClickListener onCalledCarClickListener) {
        this.context = context;
        this.responsesList = responseList;
        this.onCalledCarClickListener = onCalledCarClickListener;
    }

    @Override
    public ListCalledCarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkin,parent, false);
        return new ListCalledCarAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        EntryCheckinResponse response = responsesList.get(position);

        if (response.isReadyToCheckout()) {
            holder.textReady.setVisibility(View.VISIBLE);
        }else {
            holder.textReady.setVisibility(View.GONE);
        }

        String platNo = response.getData().getAttribute().getIdTransaksi() + " - " + response.getData().getAttribute().getPlatNo();
        String checkinTime = response.getData().getAttribute().getCheckinTime();

        holder.textPlatNo.setText(platNo);
        holder.textRunnerName.setText(checkinTime);

        holder.layoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCalledCarClickListener.onItemClick((int) getItemId(position));
            }
        });

        holder.textPlatNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCalledCarClickListener.onItemClick((int)getItemId(position));
            }
        });

        holder.circleImageView.setImageResource(R.drawable.call_car2);
        holder.layoutContainer.setBackgroundColor(Color.parseColor("#ffebee"));
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
    public int getItemCount() {
        return responsesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutContainer;
        TextView textPlatNo;
        TextView textRunnerName;
        TextView textIdCheckin;
        CircleImageView circleImageView;
        TextView textReady;

        public ViewHolder(View view) {
            super(view);
            textPlatNo = (TextView) view.findViewById(R.id.text_plat_no);
            textRunnerName = (TextView) view.findViewById(R.id.text_runner);
            textIdCheckin = (TextView) view.findViewById(R.id.id_checkin);
            layoutContainer = (LinearLayout) view.findViewById(R.id.container_checkin);
            circleImageView = (CircleImageView) view.findViewById(R.id.img_car);
            textReady = (TextView) view.findViewById(R.id.text_ready);
        }
    }

    public interface OnCalledCarClickListener {
        void onItemClick(int id);
    }

}
