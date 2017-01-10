package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.CarMaster;

/**
 * Created by DIGIKOM-EX4 on 12/23/2016.
 */

public class CarTypeAdapter extends BaseAdapter {

    private Context context;
    private List<CarMaster> carMasterList;

    public CarTypeAdapter(Context context, List<CarMaster> carMasterList) {
        this.context = context;
        this.carMasterList = carMasterList;
    }

    @Override
    public int getCount() {
        return carMasterList.size();
    }

    @Override
    public Object getItem(int i) {
        return carMasterList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return carMasterList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_item_cartype,viewGroup, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.text_cartype);
        textView.setText(carMasterList.get(i).getAttrib().getCarName());

        return view;
    }
}
