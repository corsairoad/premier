package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import valet.digikom.com.valetparking.R;

/**
 * Created by DIGIKOM-EX4 on 12/23/2016.
 */

public class CarTypeAdapter extends BaseAdapter {

    Context context;
    List<String> carTypeList;

    public CarTypeAdapter(Context context, List<String> carTypeList) {
        this.context = context;
        this.carTypeList = carTypeList;
    }

    @Override
    public int getCount() {
        return carTypeList.size();
    }

    @Override
    public Object getItem(int i) {
        return carTypeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_item_cartype,viewGroup, false);
        }

        TextView textView = (TextView) view.findViewById(R.id.text_cartype);
        textView.setText(carTypeList.get(i));

        return view;
    }
}
