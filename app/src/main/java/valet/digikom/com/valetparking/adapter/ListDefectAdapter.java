package valet.digikom.com.valetparking.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import valet.digikom.com.valetparking.R;
import valet.digikom.com.valetparking.domain.DefectMaster;
import valet.digikom.com.valetparking.fragments.StepTwoFragment;

/**
 * Created by DIGIKOM-EX4 on 12/26/2016.
 */

public class ListDefectAdapter extends ArrayAdapter<DefectMaster> {

    ArrayList<Integer> positions = new ArrayList<>();
    StepTwoFragment.OnDefectSelectedListener listener;

    public ListDefectAdapter(Context context, List<DefectMaster> objects, StepTwoFragment.OnDefectSelectedListener defectListener) {
        super(context, 0, objects);
        this.listener = defectListener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_defects, parent, false);
        }

        TextView textId = (TextView) convertView.findViewById(R.id.text_id_x);
        TextView textDefects = (TextView) convertView.findViewById(R.id.text_defect);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_defect);

        textId.setText("" + getItem(position).getAttributes().getId());
        textDefects.setText(getItem(position).getAttributes().getDefectName());

        final DefectMaster dm = getItem(position);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    listener.onDefectSelected(dm.getAttributes().getDefectName(), dm);
                }else {
                    listener.onDefectUnselected(dm.getAttributes().getDefectName(), dm);
                }
            }
        });
        return convertView;
    }

}
