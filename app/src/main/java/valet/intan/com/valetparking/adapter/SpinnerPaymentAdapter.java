package valet.intan.com.valetparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import valet.intan.com.valetparking.R;
import valet.intan.com.valetparking.domain.PaymentMethod;

/**
 * Created by DIGIKOM-EX4 on 2/21/2017.
 */

public class SpinnerPaymentAdapter extends BaseAdapter {

    private Context context;
    private List<PaymentMethod.Data> listPayment;

    public SpinnerPaymentAdapter(Context context, List<PaymentMethod.Data> listPayment) {
        this.context = context;
        this.listPayment = listPayment;
    }

    @Override
    public int getCount() {
        return listPayment.size();
    }

    @Override
    public Object getItem(int i) {
        return listPayment.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listPayment.get(i).getAttr().getPaymentId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.text_item_membership, null);
        }
        TextView textPayment = (TextView) view.findViewById(R.id.text_item_membership);
        textPayment.setText(listPayment.get(i).getAttr().getPaymentName());
        return view;
    }
}
