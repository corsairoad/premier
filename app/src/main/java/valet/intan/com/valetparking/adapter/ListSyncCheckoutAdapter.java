package valet.intan.com.valetparking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.dao.EntryDao;
import valet.intan.com.valetparking.domain.CheckoutData;
import valet.intan.com.valetparking.domain.FinishCheckOut;

/**
 * Created by fadlymunandar on 7/31/17.
 */

public class ListSyncCheckoutAdapter extends RecyclerView.Adapter<ListSyncCheckoutAdapter.CheckoutViewHolder> {

    private List<CheckoutData> checkoutDataList;
    private Context context;
    private Gson gson;

    public ListSyncCheckoutAdapter(Context context, List<CheckoutData> checkoutDataList) {
        this.context = context;
        this.checkoutDataList = checkoutDataList;
        this.gson = new Gson();
    }


    @Override
    public CheckoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sync, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CheckoutViewHolder holder, int position) {
        CheckoutData checkoutData = checkoutDataList.get(position);
        FinishCheckOut checkOut = toFinishCheckout(checkoutData.getJsonData());
        String ticketNo = checkoutData.getNoTiket();
        String checkoutTime = "Checkout " + checkOut.getData().getAttribute().getCheckoutTime();
        String platNo = getPlatNo(ticketNo);

        holder.txtSyncTicketNo.setText(ticketNo);
        holder.txtSyncPlateNo.setText(platNo);
        holder.txtSyncCheckoutTime.setText(checkoutTime);
    }

    @Override
    public int getItemCount() {
        if (!checkoutDataList.isEmpty()) {
            return checkoutDataList.size();
        }
        return 0;
    }

    private String getPlatNo(String ticketNo) {
        return EntryDao.getInstance(this.context).getPlatNoByTicketNo(ticketNo);
    }

    private FinishCheckOut toFinishCheckout(String json) {
        return gson.fromJson(json,FinishCheckOut.class);
    }

    public class CheckoutViewHolder extends RecyclerView.ViewHolder {

        TextView txtSyncTicketNo;
        TextView txtSyncPlateNo;
        TextView txtSynCheckinTime;
        TextView txtSyncCheckoutTime;

        public CheckoutViewHolder(View view) {
            super(view);
            txtSyncTicketNo = (TextView) view.findViewById(R.id.text_sync_ticket_no);
            txtSyncPlateNo = (TextView) view.findViewById(R.id.text_sync_plate_no);
            txtSynCheckinTime = (TextView) view.findViewById(R.id.text_sync_checkin_time);
            txtSyncCheckoutTime = (TextView) view.findViewById(R.id.text_sync_checkout_time);
            txtSynCheckinTime.setVisibility(View.GONE);
        }
    }
}
