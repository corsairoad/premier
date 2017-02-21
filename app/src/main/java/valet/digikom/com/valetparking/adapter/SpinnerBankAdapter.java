package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.Bank;

/**
 * Created by DIGIKOM-EX4 on 2/21/2017.
 */

public class SpinnerBankAdapter extends BaseAdapter {
    private Context context;
    private List<Bank.Data> bankList;

    public SpinnerBankAdapter(Context context, List<Bank.Data> bankList) {
        this.context = context;
        this.bankList = bankList;
    }

    @Override
    public int getCount() {
        return bankList.size();
    }

    @Override
    public Object getItem(int i) {
        return bankList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return bankList.get(i).getAttr().getBankId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.text_item_membership, null);
        }
        TextView textPayment = (TextView) view.findViewById(R.id.text_item_membership);
        textPayment.setText(bankList.get(i).getAttr().getBankName());

        return view;
    }
}
