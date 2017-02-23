package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.ClosingData;

/**
 * Created by DIGIKOM-EX4 on 2/14/2017.
 */

public class ListClosingAdapter extends RecyclerView.Adapter<ListClosingAdapter.MyViewHolder> {

    private List<ClosingData.Data> closingData;
    private Context context;
    private OnClosingItemClickListener onClosingItemClickListener;

    public ListClosingAdapter(List<ClosingData.Data> closingData, Context context) {
        this.closingData = closingData;
        this.context = context;
        onClosingItemClickListener = (OnClosingItemClickListener) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_checkin,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ClosingData.Data.Attr attr = closingData.get(position).getAttributes();
        String platNo = attr.getPlatNo();
        String header = attr.getTransactionId() + " - " + platNo;
        String createdAt = attr.getCreatedAt();
        String id = closingData.get(position).getId();
        String valetTypeName = attr.getValetTypeName();
        String valetTypeKey = attr.getValetTpyeKey().toLowerCase();

        final String vthdId = attr.getValetHeaderId();
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClosingItemClickListener.OnClosingItemClick(Integer.parseInt(vthdId));
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
        holder.textRunnerName.setText(createdAt);
        holder.textIdCheckin.setText(id);
        holder.textValetTypeName.setText(valetTypeName);
    }

    @Override
    public int getItemCount() {
        return closingData.size();
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(closingData.get(position).getId());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutContainer;
        TextView textPlatNo;
        TextView textRunnerName;
        TextView textIdCheckin;
        TextView textValetTypeName;
        CircleImageView imgCar;
        LinearLayout container;

        public MyViewHolder(View view) {
            super(view);
            textPlatNo = (TextView) view.findViewById(R.id.text_plat_no);
            textRunnerName = (TextView) view.findViewById(R.id.text_runner);
            textIdCheckin = (TextView) view.findViewById(R.id.id_checkin);
            layoutContainer = (LinearLayout) view.findViewById(R.id.container_checkin);
            imgCar = (CircleImageView) view.findViewById(R.id.img_car);
            textValetTypeName = (TextView) view.findViewById(R.id.text_ready);
            textValetTypeName.setVisibility(View.VISIBLE);
            container = (LinearLayout) view.findViewById(R.id.container_checkin);
        }
    }
    public interface OnClosingItemClickListener{
        void OnClosingItemClick(int vthdId);
    }
}
