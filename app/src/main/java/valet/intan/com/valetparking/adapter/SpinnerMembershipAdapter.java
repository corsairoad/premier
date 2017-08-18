package valet.intan.com.valetparking.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.domain.MembershipResponse;

/**
 * Created by DIGIKOM-EX4 on 1/31/2017.
 */

public class SpinnerMembershipAdapter extends ArrayAdapter<MembershipResponse.Data> {

    public SpinnerMembershipAdapter(Context context, int resource, int text_membership, List<MembershipResponse.Data> objects) {
        super(context, resource, text_membership,objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.laoyut_spinner_membership, null);
        }

        TextView textMebership = (TextView) convertView.findViewById(R.id.text_membership);
        textMebership.setText(getItem(position).getAttr().getName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.text_item_membership, null);
        }

        TextView textMebership = (TextView) convertView.findViewById(R.id.text_item_membership);
        textMebership.setText(getItem(position).getAttr().getName());

        return convertView;
    }
}
