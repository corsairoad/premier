package valet.intan.com.valetparking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.domain.EntryCheckin;
import valet.intan.com.valetparking.domain.EntryCheckinContainer;

/**
 * Created by fadlymunandar on 7/28/17.
 */

public class ListSyncCheckinAdapter extends RecyclerView.Adapter<ListSyncCheckinAdapter.SyncViewHolder> {

    private List<EntryCheckinContainer> checkins;
    private Context context;

    public ListSyncCheckinAdapter(List<EntryCheckinContainer> checkins, Context context) {
        this.checkins = checkins;
        this.context = context;
    }

    @Override
    public SyncViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sync, parent,false);
        return new SyncViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SyncViewHolder holder, int position) {
        EntryCheckin entryCheckin = checkins.get(position).getEntryCheckin();
        if (entryCheckin != null) {
            holder.txtSyncTicketNo.setText(entryCheckin.getAttrib().getTicketNo());
            holder.txtSyncPlateNo.setText(entryCheckin.getAttrib().getPlatNo());
            holder.txtSynCheckinTime.setText("Checkin " + entryCheckin.getAttrib().getCheckinTime());
        }

    }

    @Override
    public int getItemCount() {
        return checkins.size();
    }

    public class  SyncViewHolder extends RecyclerView.ViewHolder{

        TextView txtSyncTicketNo;
        TextView txtSyncPlateNo;
        TextView txtSynCheckinTime;

        public SyncViewHolder(View view) {
            super(view);
            txtSyncTicketNo = (TextView) view.findViewById(R.id.text_sync_ticket_no);
            txtSyncPlateNo = (TextView) view.findViewById(R.id.text_sync_plate_no);
            txtSynCheckinTime = (TextView) view.findViewById(R.id.text_sync_checkin_time);
        }
    }
}
