package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.ValetTypeJson;

/**
 * Created by DIGIKOM-EX4 on 1/27/2017.
 */

public class ListValetTypeAdapter extends BaseAdapter {

    List<ValetTypeJson.Data> dataList;
    private Context context;

    public ListValetTypeAdapter(Context context, List<ValetTypeJson.Data> dataList) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return dataList.get(i).getAttrib().getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.layout_valet_type, null);

        TextView txtValet = (TextView) convertView.findViewById(R.id.text_valet_type_yp);
        txtValet.setText(dataList.get(position).getAttrib().getValetTypeName());

        return convertView;
    }
}
