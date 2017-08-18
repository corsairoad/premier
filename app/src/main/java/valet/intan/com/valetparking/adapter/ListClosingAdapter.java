package valet.intan.com.valetparking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.domain.ClosingData;

/**
 * Created by DIGIKOM-EX4 on 2/14/2017.
 */

public class ListClosingAdapter extends RecyclerView.Adapter<ListClosingAdapter.MyViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private boolean isLoadingAdded = false;
    private List<ClosingData.Data> closingData;
    private Context context;
    private OnClosingItemClickListener onClosingItemClickListener;

    public ListClosingAdapter(List<ClosingData.Data> closingData, Context context) {
        this.closingData = closingData;
        this.context = context;
        onClosingItemClickListener = (OnClosingItemClickListener) context;
    }

    public ListClosingAdapter(Context context) {
        this.context = context;
        closingData = new ArrayList<>();
        onClosingItemClickListener = (OnClosingItemClickListener) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkin,parent, false);
        View viewLoading = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case ITEM:
                viewHolder = new MyViewHolder(view);
                break;
            case LOADING:
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return (MyViewHolder) viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (getItemViewType(position) == LOADING) {
            return;
        }

        ClosingData.Data.Attr attr = closingData.get(position).getAttributes();
        String platNo = attr.getPlatNo();
        String noTiket = attr.getNoTiket();
        String remoteTiketNo = attr.getTransactionId();

        String header = noTiket + " - " + platNo;
        String createdAt = attr.getCreatedAt();
        String id = closingData.get(position).getId();
        String valetTypeName = attr.getValetTypeName();
        String valetTypeKey = attr.getValetTpyeKey().toLowerCase();

        final String vthdId = attr.getValetHeaderId();
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onClosingItemClickListener.OnClosingItemClick(Integer.parseInt(vthdId));
                onClosingItemClickListener.OnClosingItemClick(position);
            }
        });


        int colorValetTypeName;
        if ("r".equals(valetTypeKey)) {
            colorValetTypeName = Color.parseColor("#d84315");
        }else {
            colorValetTypeName = Color.parseColor("#4caf50");
        }

        holder.textValetTypeName.setTextColor(colorValetTypeName);

        Glide.with(context)
                .load(attr.getLogoMobil())
                .crossFade()
                .placeholder(R.drawable.car_icon)
                .centerCrop()
                .into(holder.imgCar);

        holder.textPlatNo.setText(header);
        holder.textRemoteTicketNo.setText(remoteTiketNo);
        holder.textRunnerName.setText(createdAt);
        holder.textIdCheckin.setText(id);
        holder.textValetTypeName.setText(valetTypeName);
    }

    @Override
    public int getItemCount() {
        return closingData == null? 0: closingData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == closingData.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(closingData.get(position).getId());
    }

    /*
    HELPERS
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutContainer;
        TextView textPlatNo;
        TextView textRunnerName;
        TextView textIdCheckin;
        TextView textValetTypeName;
        TextView textRemoteTicketNo;
        CircleImageView imgCar;
        LinearLayout container;

        public MyViewHolder(View view) {
            super(view);
            textPlatNo = (TextView) view.findViewById(R.id.text_plat_no);
            textRunnerName = (TextView) view.findViewById(R.id.text_runner);
            textIdCheckin = (TextView) view.findViewById(R.id.id_checkin);
            layoutContainer = (LinearLayout) view.findViewById(R.id.container_checkin);
            textRemoteTicketNo = (TextView) view.findViewById(R.id.text_remote_tiket_no);
            imgCar = (CircleImageView) view.findViewById(R.id.img_car);
            textValetTypeName = (TextView) view.findViewById(R.id.text_ready);
            textValetTypeName.setVisibility(View.VISIBLE);
            container = (LinearLayout) view.findViewById(R.id.container_checkin);
        }
    }

    public class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }

    public interface OnClosingItemClickListener{
        void OnClosingItemClick(int vthdId);
    }

    public void addAll( List<ClosingData.Data> closingData) {
        for (ClosingData.Data data : closingData) {
            add(data);
        }
    }

    public void add(ClosingData.Data data) {
        closingData.add(data);
        //notifyItemInserted(closingData.size() - 1);
    }

    public ClosingData.Data get(int index) {
        return closingData.get(index);
    }

    public List<ClosingData.Data> getClosingData() {
        return this.closingData;
    }

    public void clearData() {
        closingData.clear();
        notifyDataSetChanged();
    }
}
