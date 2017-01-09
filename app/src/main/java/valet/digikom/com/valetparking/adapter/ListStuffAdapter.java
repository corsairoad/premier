package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.AdditionalItems;
import valet.digikom.com.valetparking.fragments.StepThreeFragment;

/**
 * Created by DIGIKOM-EX4 on 12/26/2016.
 */

public class ListStuffAdapter extends ArrayAdapter<AdditionalItems> {

    int post = -1;
    private ArrayList<Integer> positions;
    private StepThreeFragment.OnStuffSelectedListener listener;

    public ListStuffAdapter(Context context, List<AdditionalItems> objects, StepThreeFragment.OnStuffSelectedListener onStuffListener) {
        super(context, 0, objects);
        this.listener = onStuffListener;
        positions = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_defects, parent, false);
        }

        TextView textId = (TextView) convertView.findViewById(R.id.text_id_x);
        TextView textDefects = (TextView) convertView.findViewById(R.id.text_defect);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_defect);

        if (position == post) {
            checkBox.setChecked(true);
        }

        if (!positions.isEmpty()) {
            for (Integer i: positions) {
                if (i == position) {
                    checkBox.setChecked(true);
                }
            }
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    listener.onStuffSelected(getItem(position).getAttributes().getAdditionalItemMaster().getName());
                }else {
                    listener.onStuffUnselected(getItem(position).getAttributes().getAdditionalItemMaster().getName());
                }
            }
        });

        int id = getItem(position).getAttributes().getAdditionalItemMaster().getId();
        String name = getItem(position).getAttributes().getAdditionalItemMaster().getName();
        textId.setText(String.valueOf(id));
        textDefects.setText(name);
        return convertView;
    }

    @Override
    public int getPosition(AdditionalItems item) {
        return super.getPosition(item);
    }

    public void setPost(int post) {
        this.post = post;
    }

}
